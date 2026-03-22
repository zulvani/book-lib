package com.demandlane.aguszulvani.booklib.service;

import com.demandlane.aguszulvani.booklib.exception.BusinessException;
import com.demandlane.aguszulvani.booklib.model.entity.Book;
import com.demandlane.aguszulvani.booklib.model.entity.Loan;
import com.demandlane.aguszulvani.booklib.model.entity.Member;
import com.demandlane.aguszulvani.booklib.model.request.BookLoanRequest;
import com.demandlane.aguszulvani.booklib.repository.BookRepository;
import com.demandlane.aguszulvani.booklib.repository.LoanRepository;
import com.demandlane.aguszulvani.booklib.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
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
        log.info("Loaning book: {}", bookLoanRequest);

        Optional<Member> member = memberRepository.findById(bookLoanRequest.getMemberId());
        if (member.isEmpty()) {
            log.error("Loaning book: Member not found: {}", bookLoanRequest.getMemberId());
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Member not found");
        }

        Optional<Book> book = bookRepository.findById(bookLoanRequest.getBookId());
        if (book.isEmpty()) {
            log.error("Loaning book: Book not found: {}", bookLoanRequest.getBookId());
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Book not found");
        }

        long countOfActiveLoan = loanRepository.countByMemberIdAndReturnedAtIsNull(bookLoanRequest.getMemberId());
        if (countOfActiveLoan >= maxActiveLoan) {
            log.error("Loaning book: Member has reached the maximum loan limit: {}", bookLoanRequest.getMemberId());
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Member has reached the maximum loan limit");
        }

        List<Loan> overdueLoans = loanRepository.findByMemberIdAndReturnedAtIsNullAndDueDateBefore(
                bookLoanRequest.getMemberId(),
                java.time.LocalDateTime.now()
        );

        if (!overdueLoans.isEmpty() && !allowMemberToBorrowWhenOverdueLoan) {
            log.error("Loaning book: Member has overdue loan: {}", bookLoanRequest.getMemberId());
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Member has overdue loan");
        }

        if (book.get().getAvailableCopies() <= 0) {
            log.error("Loaning book: Book is not available: {}", bookLoanRequest.getBookId());
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Book is not available");
        }

        log.info("Loaning book: Book available copies: {}", book.get().getAvailableCopies());
        Book bookEntity = bookRepository.save(book.get());
        bookEntity.setAvailableCopies(bookEntity.getAvailableCopies() - 1);
        bookRepository.save(bookEntity);

        log.info("Loaning book: Book loaned");
        Loan loan = Loan.builder()
                .member(member.get())
                .book(book.get())
                .borrowedAt(bookLoanRequest.getBorrowDateTime())
                .dueDate(bookLoanRequest.getBorrowDateTime().plusDays(loanDueDays))
                .build();

        return loanRepository.save(loan);
    }

    @Transactional
    public Loan returnBook(UUID loanId) throws BusinessException{
        log.info("Returning book: {}", loanId);
        Optional<Loan> loan = loanRepository.findById(loanId);

        if (loan.isEmpty()) {
            log.error("Returning book: Load not found: {}", loanId);
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Load not found");
        }

        log.info("Returning book: Book returned: {}", loanId);
        Book book = loan.get().getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        log.info("Returning book: Book loaned: {}", loanId);
        Loan loanEntity = loan.get();
        loanEntity.setReturnedAt(java.time.LocalDateTime.now());
        return loanRepository.save(loanEntity);
    }
}
