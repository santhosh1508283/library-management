package com.santhosh.library.service;

import com.santhosh.library.dto.JoinWaitlistRequest;
import com.santhosh.library.dto.WaitlistResponse;

import java.util.List;

public interface WaitlistService {

    void joinWaitlist(JoinWaitlistRequest request);
    List<WaitlistResponse> getWaitlist();
    void cancelWaitlist(Long waitlistId);
}
