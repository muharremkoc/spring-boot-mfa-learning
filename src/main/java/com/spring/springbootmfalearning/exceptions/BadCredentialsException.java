package com.spring.springbootmfalearning.exceptions;

public class BadCredentialsException extends Exception {
    public BadCredentialsException(String message) {
        super(message);
    }
}