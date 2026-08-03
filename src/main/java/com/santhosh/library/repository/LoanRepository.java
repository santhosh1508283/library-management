package com.santhosh.library.repository;

import com.santhosh.library.entity.Loan;
import com.santhosh.library.entity.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    Optional<Loan> findByBookCopyIdAndStatus(Long bookCopyId, LoanStatus status);

    List<Loan> findAllByUserIdAndStatus(Long userId, LoanStatus status);

}
