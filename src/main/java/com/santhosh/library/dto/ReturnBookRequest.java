package com.santhosh.library.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReturnBookRequest {

    @NotBlank(message = "Barcode is required")
    private String barcode;

}
