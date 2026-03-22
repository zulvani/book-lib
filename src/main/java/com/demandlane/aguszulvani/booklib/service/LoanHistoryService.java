package com.demandlane.aguszulvani.booklib.service;

import com.demandlane.aguszulvani.booklib.exception.BusinessException;
import com.demandlane.aguszulvani.booklib.model.entity.Loan;
import com.demandlane.aguszulvani.booklib.repository.BookRepository;
import com.demandlane.aguszulvani.booklib.repository.LoanRepository;
import com.demandlane.aguszulvani.booklib.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
public class LoanHistoryService {

    private final LoanRepository loanRepository;
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;

    public LoanHistoryService(LoanRepository loanRepository, MemberRepository memberRepository, BookRepository bookRepository) {
        this.loanRepository = loanRepository;
        this.memberRepository = memberRepository;
        this.bookRepository = bookRepository;
    }

    // ==================== Member Loan History ====================

    public List<Loan> getMemberLoanHistory(UUID memberId) throws BusinessException {
        log.info("Fetching loan history for member: {}", memberId);

        if (!memberRepository.existsById(memberId)) {
            log.error("Member not found: {}", memberId);
            throw new BusinessException(HttpStatus.NOT_FOUND, "Member not found");
        }

        return loanRepository.findByMemberId(memberId);
    }

    public List<Loan> getMemberActiveLoanHistory(UUID memberId) throws BusinessException {
        log.info("Fetching active loans for member: {}", memberId);

        if (!memberRepository.existsById(memberId)) {
            log.error("Member not found: {}", memberId);
            throw new BusinessException(HttpStatus.NOT_FOUND, "Member not found");
        }

        return loanRepository.findByMemberIdAndReturnedAtIsNull(memberId);
    }

    public List<Loan> getMemberReturnedLoanHistory(UUID memberId) throws BusinessException {
        log.info("Fetching returned loans for member: {}", memberId);

        if (!memberRepository.existsById(memberId)) {
            log.error("Member not found: {}", memberId);
            throw new BusinessException(HttpStatus.NOT_FOUND, "Member not found");
        }

        return loanRepository.findByMemberIdAndReturnedAtIsNotNull(memberId);
    }

    // ==================== Book Loan History ====================

    public List<Loan> getBookLoanHistory(UUID bookId) throws BusinessException {
        log.info("Fetching loan history for book: {}", bookId);

        if (!bookRepository.existsById(bookId)) {
            log.error("Book not found: {}", bookId);
            throw new BusinessException(HttpStatus.NOT_FOUND, "Book not found");
        }

        return loanRepository.findByBookId(bookId);
    }

    public List<Loan> getBookActiveLoanHistory(UUID bookId) throws BusinessException {
        log.info("Fetching active loans for book: {}", bookId);

        if (!bookRepository.existsById(bookId)) {
            log.error("Book not found: {}", bookId);
            throw new BusinessException(HttpStatus.NOT_FOUND, "Book not found");
        }

        return loanRepository.findByBookIdAndReturnedAtIsNull(bookId);
    }

    public List<Loan> getBookReturnedLoanHistory(UUID bookId) throws BusinessException {
        log.info("Fetching returned loans for book: {}", bookId);

        if (!bookRepository.existsById(bookId)) {
            log.error("Book not found: {}", bookId);
            throw new BusinessException(HttpStatus.NOT_FOUND, "Book not found");
        }

        return loanRepository.findByBookIdAndReturnedAtIsNotNull(bookId);
    }
}
