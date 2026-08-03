package com.santhosh.library.service;

import com.santhosh.library.dto.ReturnBookRequest;
import com.santhosh.library.entity.BookCopy;
import com.santhosh.library.entity.BookCopyStatus;
import com.santhosh.library.entity.Loan;
import com.santhosh.library.entity.LoanStatus;
import com.santhosh.library.exception.BookCopyNotFoundException;
import com.santhosh.library.exception.LoanNotFoundException;
import com.santhosh.library.repository.BookCopyRepository;
import com.santhosh.library.repository.LoanRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReturnServiceImp implements ReturnService {

    private final BookCopyRepository bookCopyRepository;
    private final LoanRepository loanRepository;

    public ReturnServiceImp(BookCopyRepository bookCopyRepository, LoanRepository loanRepository){
        this.bookCopyRepository = bookCopyRepository;
        this.loanRepository = loanRepository;
    }

    @Override
    @Transactional
    public void returnBook(ReturnBookRequest request){
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
