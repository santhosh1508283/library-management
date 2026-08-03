package com.santhosh.library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateBookCopyRequest {

    @NotNull(message = "Book id required")
    private Long bookId;

    @NotBlank(message = "Barcode is required")
    private String barcode;

    @NotBlank(message = "Shelf number is required")
    private String shelfNumber;
}
