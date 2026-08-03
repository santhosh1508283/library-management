package com.santhosh.library.service;

import com.santhosh.library.dto.CreateLoanRequest;
import com.santhosh.library.dto.LoanResponse;
import com.santhosh.library.dto.ReturnLoanRequest;
import com.santhosh.library.entity.*;
import com.santhosh.library.exception.BookCopyNotFoundException;
import com.santhosh.library.exception.BookNotFoundException;
import com.santhosh.library.exception.LoanNotFoundException;
import com.santhosh.library.repository.BookCopyRepository;
import com.santhosh.library.repository.BookRepository;
import com.santhosh.library.repository.LoanRepository;
import com.santhosh.library.repository.UserRepository;
import com.santhosh.library.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LoanServiceImp implements LoanService{

    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;
    private final LoanRepository loanRepository;

    public LoanServiceImp(BookRepository bookRepository, BookCopyRepository bookCopyRepository, LoanRepository loanRepository){
        this.bookRepository = bookRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.loanRepository = loanRepository;
    }

    @Override
    @Transactional
    //Todo add row level lock
    public LoanResponse createLoan(CreateLoanRequest request){

        Book book = bookRepository.findByIdAndDeletedFalse(request.getBookId()).orElseThrow(()-> new BookNotFoundException("Book not found"));

        //Todo need to add to waitlist after implementing that service instead of throwing error
        BookCopy bookCopy = bookCopyRepository.findFirstByBookIdAndStatus(book.getId(), BookCopyStatus.AVAILABLE).orElseThrow(()-> new BookCopyNotFoundException("Book copies are not available"));

        //Todo Check whether the user already has an active loan for the same book title and add check for max borrowing 5 books
        User user = SecurityUtils.getCurrentUser();
        LocalDateTime now = LocalDateTime.now();

        Loan loan = new Loan();

        loan.setBookCopy(bookCopy);
        loan.setUser(user);
        loan.setBorrowedAt(now);
        loan.setDueDate(now.plusDays(15));

        bookCopy.setStatus(BookCopyStatus.BORROWED);

        loan = loanRepository.save(loan);

        LoanResponse response = new LoanResponse();

        response.setBorrowedAt(loan.getBorrowedAt());
        response.setLoanId(loan.getId());
        response.setDueDate(loan.getDueDate());
        response.setStatus(loan.getStatus());

        response.setBookId(book.getId());
        response.setBookTitle(book.getTitle());

        response.setBarcode(bookCopy.getBarcode());
        response.setCopyId(bookCopy.getId());

        return response;
    }

    @Override
    @Transactional
    public void returnLoan(ReturnLoanRequest request){
        BookCopy bookCopy = bookCopyRepository.findByBarcode(request.getBarcode()).orElseThrow(() -> new BookCopyNotFoundException("Invalid barcode"));
        Loan loan = loanRepository.findByBookCopyIdAndStatus(bookCopy.getId(), LoanStatus.BORROWED).orElseThrow(() -> new LoanNotFoundException("No active loan found for this copy"));
        // TODO:
        // 1. Calculate overdue fine
        // 2. Process payment
        // 3. Notify first waitlisted user
        // 4. Reserve copy for waitlisted user
        bookCopy.setStatus(BookCopyStatus.AVAILABLE);
        loan.setReturnedAt(LocalDateTime.now());
        loan.setStatus(LoanStatus.RETURNED);
    }

}
