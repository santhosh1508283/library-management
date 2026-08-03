package com.santhosh.library.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateBookCopyShelfRequest {
    @NotBlank(message = "Shelf number required")
    private String shelfNumber;
}
