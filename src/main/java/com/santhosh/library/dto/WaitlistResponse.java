package com.santhosh.library.dto;

import com.santhosh.library.entity.WaitlistStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class WaitlistResponse {
    private Long waitlistId;
    private Long bookId;
    private String title;
    private WaitlistStatus status;
    private LocalDateTime joinedAt;
}
