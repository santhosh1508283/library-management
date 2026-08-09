package com.santhosh.library.repository;

import com.santhosh.library.entity.Waitlist;
import com.santhosh.library.entity.WaitlistStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WaitlistRepository extends JpaRepository<Waitlist, Long> {

    boolean existsByUserIdAndBookIdAndStatus(
            Long userId,
            Long bookId,
            WaitlistStatus status
    );

    List<Waitlist> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Waitlist> findByIdAndUserIdAndStatus(
            Long waitlistId,
            Long userId,
            WaitlistStatus status
    );

}
