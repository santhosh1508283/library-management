package com.santhosh.library.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "book_copies")
public class BookCopy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false, unique = true, length = 50)
    private String barcode;

    @Column(nullable = false, length = 10)
    private String shelfNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookCopyStatus status = BookCopyStatus.AVAILABLE;

//    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

//    @UpdateTimestamp
    @Column(nullable = false)
    private  LocalDateTime updatedAt;

    @PrePersist
    private void prePersist(){
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    private void preUpdate(){
        updatedAt = LocalDateTime.now();
    }
}

