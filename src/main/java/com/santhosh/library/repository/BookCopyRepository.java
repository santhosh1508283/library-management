package com.santhosh.library.repository;

import com.santhosh.library.entity.BookCopy;
import com.santhosh.library.entity.BookCopyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {

    boolean existsByBarcode(String barcode);
    List<BookCopy> findByBookId(Long bookId);
    Optional<BookCopy> findFirstByBookIdAndStatus(Long bookId, BookCopyStatus status);
    Optional<BookCopy> findByBarcode(String barcode);

}
