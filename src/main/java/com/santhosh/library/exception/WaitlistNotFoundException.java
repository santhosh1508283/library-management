package com.santhosh.library.exception;

public class WaitlistNotFoundException extends RuntimeException{

    public WaitlistNotFoundException(String message){
        super(message);
    }
}
