package com.demandlane.aguszulvani.booklib.service;

import com.demandlane.aguszulvani.booklib.exception.BusinessException;
import com.demandlane.aguszulvani.booklib.model.entity.Book;
import com.demandlane.aguszulvani.booklib.model.entity.Loan;
import com.demandlane.aguszulvani.booklib.model.entity.Member;
import com.demandlane.aguszulvani.booklib.model.request.BookLoanRequest;
import com.demandlane.aguszulvani.booklib.repository.BookRepository;
import com.demandlane.aguszulvani.booklib.repository.LoanRepository;
import com.demandlane.aguszulvani.booklib.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookLoanServiceTest {

    @Mock
    LoanRepository loanRepository;

    @Mock
    BookRepository bookRepository;

    @Mock
    MemberRepository memberRepository;

    @InjectMocks
    BookLoanService bookLoanService;

    private UUID memberId;
    private UUID bookId;
    private UUID loanId;
    private Member member;
    private Book book;
    private Loan loan;
    private BookLoanRequest request;

    @BeforeEach
    void setUp() {
        memberId = UUID.randomUUID();
        bookId = UUID.randomUUID();
        loanId = UUID.randomUUID();

        // Inject @Value fields
        ReflectionTestUtils.setField(bookLoanService, "maxActiveLoan", 3L);
        ReflectionTestUtils.setField(bookLoanService, "allowMemberToBorrowWhenOverdueLoan", false);
        ReflectionTestUtils.setField(bookLoanService, "loanDueDays", 14);

        member = Member.builder()
                .id(memberId)
                .name("Agus Zulvani")
                .build();

        book = Book.builder()
                .id(bookId)
                .title("Clean Code")
                .availableCopies(3)
                .build();

        loan = Loan.builder()
                .id(loanId)
                .member(member)
                .book(book)
                .borrowedAt(LocalDateTime.now())
                .dueDate(LocalDateTime.now().plusDays(14))
                .build();

        request = new BookLoanRequest();
        request.setMemberId(memberId);
        request.setBookId(bookId);
        request.setBorrowDateTime(LocalDateTime.now());
    }

    // ==================== loanBook ====================

    @Test
    void loanBook_success() throws BusinessException {
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(loanRepository.countByMemberIdAndReturnedAtIsNull(memberId)).thenReturn(0L);
        when(loanRepository.findByMemberIdAndReturnedAtIsNullAndDueDateBefore(eq(memberId), any())).thenReturn(Collections.emptyList());
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        Loan result = bookLoanService.loanBook(request);

        assertNotNull(result);
        verify(loanRepository).save(any(Loan.class));
        verify(bookRepository, times(2)).save(any(Book.class));
    }

    @Test
    void loanBook_memberNotFound_throwsException() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () -> bookLoanService.loanBook(request));
        assertEquals("Member not found", ex.getMessage());
    }

    @Test
    void loanBook_bookNotFound_throwsException() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () -> bookLoanService.loanBook(request));
        assertEquals("Book not found", ex.getMessage());
    }

    @Test
    void loanBook_maxLoanReached_throwsException() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(loanRepository.countByMemberIdAndReturnedAtIsNull(memberId)).thenReturn(3L);

        BusinessException ex = assertThrows(BusinessException.class, () -> bookLoanService.loanBook(request));
        assertEquals("Member has reached the maximum loan limit", ex.getMessage());
    }

    @Test
    void loanBook_hasOverdueLoan_throwsException() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(loanRepository.countByMemberIdAndReturnedAtIsNull(memberId)).thenReturn(1L);
        when(loanRepository.findByMemberIdAndReturnedAtIsNullAndDueDateBefore(eq(memberId), any()))
                .thenReturn(List.of(loan));

        BusinessException ex = assertThrows(BusinessException.class, () -> bookLoanService.loanBook(request));
        assertEquals("Member has overdue loan", ex.getMessage());
    }

    @Test
    void loanBook_bookNotAvailable_throwsException() {
        book.setAvailableCopies(0);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(loanRepository.countByMemberIdAndReturnedAtIsNull(memberId)).thenReturn(0L);
        when(loanRepository.findByMemberIdAndReturnedAtIsNullAndDueDateBefore(eq(memberId), any()))
                .thenReturn(Collections.emptyList());

        BusinessException ex = assertThrows(BusinessException.class, () -> bookLoanService.loanBook(request));
        assertEquals("Book is not available", ex.getMessage());
    }

    // ==================== returnBook ====================

    @Test
    void returnBook_success() throws BusinessException {
        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        Loan result = bookLoanService.returnBook(loanId);

        assertNotNull(result);
        assertNotNull(loan.getReturnedAt());
        verify(bookRepository).save(any(Book.class));
        verify(loanRepository).save(any(Loan.class));
    }

    @Test
    void returnBook_loanNotFound_throwsException() {
        when(loanRepository.findById(loanId)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () -> bookLoanService.returnBook(loanId));
        assertEquals("Load not found", ex.getMessage());
    }

    @Test
    void returnBook_increasesAvailableCopies() throws BusinessException {
        int initialCopies = book.getAvailableCopies();
        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        bookLoanService.returnBook(loanId);

        assertEquals(initialCopies + 1, book.getAvailableCopies());
    }
}
