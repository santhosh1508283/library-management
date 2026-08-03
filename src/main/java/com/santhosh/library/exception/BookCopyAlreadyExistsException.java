package com.santhosh.library.exception;

public class BookCopyAlreadyExistsException extends RuntimeException{
    public BookCopyAlreadyExistsException(String message){
        super(message);
    }
}
