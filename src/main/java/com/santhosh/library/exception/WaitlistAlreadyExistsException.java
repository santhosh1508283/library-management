package com.santhosh.library.exception;

public class WaitlistAlreadyExistsException extends RuntimeException{
    public WaitlistAlreadyExistsException(String message){
        super(message);
    }
}
