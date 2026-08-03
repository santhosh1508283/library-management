package com.santhosh.library.dto;

import com.santhosh.library.entity.BookCopyStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateBookCopyStatusRequest {

    @NotNull(message = "Status is required")
    private BookCopyStatus status;

}
