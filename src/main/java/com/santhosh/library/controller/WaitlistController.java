package com.santhosh.library.controller;

import com.santhosh.library.dto.JoinWaitlistRequest;
import com.santhosh.library.dto.WaitlistResponse;
import com.santhosh.library.service.WaitlistService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/waitlist")
public class WaitlistController {

    private final WaitlistService waitlistService;

    public WaitlistController(WaitlistService waitlistService){
        this.waitlistService = waitlistService;
    }

    @PostMapping
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<Void> joinWaitlist(@RequestBody @Valid JoinWaitlistRequest request){
        waitlistService.joinWaitlist(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<List<WaitlistResponse>> getWaitlist(){
        return ResponseEntity.ok().body(waitlistService.getWaitlist());
    }

    @DeleteMapping("/{waitlistId}")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<Void> cancelWaitlist(@PathVariable Long waitlistId) {
        waitlistService.cancelWaitlist(waitlistId);
        return ResponseEntity.noContent().build();
    }

}
