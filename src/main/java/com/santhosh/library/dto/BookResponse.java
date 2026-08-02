package com.santhosh.library.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BookResponse {

    private Long id;

    private String title;

    private String author;

    private String edition;

    private String isbn;
}