package com.santhosh.library.service;

import com.santhosh.library.dto.JoinWaitlistRequest;
import com.santhosh.library.dto.WaitlistResponse;
import com.santhosh.library.entity.Book;
import com.santhosh.library.entity.User;
import com.santhosh.library.entity.Waitlist;
import com.santhosh.library.entity.WaitlistStatus;
import com.santhosh.library.exception.BookNotFoundException;
import com.santhosh.library.exception.WaitlistAlreadyExistsException;
import com.santhosh.library.repository.BookRepository;
import com.santhosh.library.repository.WaitlistRepository;
import com.santhosh.library.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WaitlistServiceImp implements WaitlistService {

    private final WaitlistRepository waitlistRepository;
    private final BookRepository bookRepository;

    public WaitlistServiceImp(WaitlistRepository waitlistRepository, BookRepository bookRepository){
        this.waitlistRepository = waitlistRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    @Transactional
    public void joinWaitlist(JoinWaitlistRequest request) {

        User user = SecurityUtils.getCurrentUser();

        Book book = bookRepository.findByIdAndDeletedFalse(request.getBookId())
                .orElseThrow(() -> new BookNotFoundException("Book not found"));

        if (waitlistRepository.existsByUserIdAndBookIdAndStatus(
                user.getId(),
                book.getId(),
                WaitlistStatus.WAITING)) {
            throw new WaitlistAlreadyExistsException("User already requested");
        }

        Waitlist waitlist = new Waitlist();
        waitlist.setBook(book);
        waitlist.setUser(user);

        waitlistRepository.save(waitlist);
    }

    @Override
    public List<WaitlistResponse> getWaitlist() {
        User user = SecurityUtils.getCurrentUser();
        List<Waitlist> waitlists = waitlistRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId());
        List<WaitlistResponse> responses = new ArrayList<>();
        for(Waitlist waitlist : waitlists){
            WaitlistResponse waitlistResponse = new WaitlistResponse();

            waitlistResponse.setWaitlistId(waitlist.getId());
            waitlistResponse.setJoinedAt(waitlist.getCreatedAt());
            waitlistResponse.setStatus(waitlist.getStatus());

            waitlistResponse.setBookId(waitlist.getBook().getId());
            waitlistResponse.setTitle(waitlist.getBook().getTitle());

            responses.add(waitlistResponse);
        }
        return responses;
    }
}
