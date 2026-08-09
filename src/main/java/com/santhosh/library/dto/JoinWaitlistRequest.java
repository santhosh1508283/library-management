package com.santhosh.library.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JoinWaitlistRequest {
    @NotNull(message = "Book id is required")
    private Long bookId;
}
