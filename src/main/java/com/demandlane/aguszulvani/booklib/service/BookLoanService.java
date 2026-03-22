package com.demandlane.aguszulvani.booklib.service;

import com.demandlane.aguszulvani.booklib.exception.BusinessException;
import com.demandlane.aguszulvani.booklib.model.request.BookLoanRequest;
import com.demandlane.aguszulvani.booklib.repository.BookRepository;
import com.demandlane.aguszulvani.booklib.repository.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BookLoanService {

    LoanRepository loanRepository;
    BookRepository bookRepository;

    public BookLoanService(LoanRepository loanRepository, BookRepository bookRepository) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional
    public void loanBook(BookLoanRequest bookLoanRequest) throws BusinessException {

    }

}
