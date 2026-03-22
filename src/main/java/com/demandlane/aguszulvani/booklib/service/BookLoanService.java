package com.demandlane.aguszulvani.booklib.service;

import com.demandlane.aguszulvani.booklib.exception.BusinessException;
import com.demandlane.aguszulvani.booklib.model.entity.Book;
import com.demandlane.aguszulvani.booklib.model.entity.Loan;
import com.demandlane.aguszulvani.booklib.model.entity.Member;
import com.demandlane.aguszulvani.booklib.model.request.BookLoanRequest;
import com.demandlane.aguszulvani.booklib.repository.BookRepository;
import com.demandlane.aguszulvani.booklib.repository.LoanRepository;
import com.demandlane.aguszulvani.booklib.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BookLoanService {

    LoanRepository loanRepository;
    BookRepository bookRepository;
    MemberRepository memberRepository;

    @Value( "${global.configuration.max-loan}")
    Long maxActiveLoan;

    @Value( "${global.configuration.allow-member-to-borrow-when-overdue-loan}")
    Boolean allowMemberToBorrowWhenOverdueLoan;

    @Value( "${global.configuration.loan-due-days}")
    Integer loanDueDays;

    public BookLoanService(LoanRepository loanRepository, BookRepository bookRepository, MemberRepository memberRepository) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
    }

    /**
     * TODO: add member specific validation against global configuration
     */
    @Transactional
    public Loan loanBook(BookLoanRequest bookLoanRequest) throws BusinessException {
        Optional<Member> member = memberRepository.findById(bookLoanRequest.getMemberId());
        if (member.isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Member not found");
        }

        Optional<Book> book = bookRepository.findById(bookLoanRequest.getBookId());
        if (book.isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Book not found");
        }

        long countOfActiveLoan = loanRepository.countByMemberIdAndReturnedAtIsNull(bookLoanRequest.getMemberId());
        if (countOfActiveLoan >= maxActiveLoan) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Member has reached the maximum loan limit");
        }

        List<Loan> overdueLoans = loanRepository.findByMemberIdAndReturnedAtIsNullAndDueDateBefore(
                bookLoanRequest.getMemberId(),
                java.time.LocalDateTime.now()
        );

        if (!overdueLoans.isEmpty() && !allowMemberToBorrowWhenOverdueLoan) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Member has overdue loan");
        }

        Loan loan = Loan.builder()
                .member(member.get())
                .book(book.get())
                .borrowedAt(bookLoanRequest.getBorrowDateTime())
                .dueDate(bookLoanRequest.getBorrowDateTime().plusDays(loanDueDays))
                .build();

        Book bookEntity = bookRepository.save(book.get());
        bookEntity.setAvailableCopies(bookEntity.getAvailableCopies() - 1);
        bookRepository.save(bookEntity);

        return loanRepository.save(loan);
    }
}
