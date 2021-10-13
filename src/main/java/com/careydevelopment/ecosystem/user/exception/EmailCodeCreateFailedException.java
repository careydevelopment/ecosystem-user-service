package com.careydevelopment.ecosystem.user.exception;

public class EmailCodeCreateFailedException extends RuntimeException {

    private static final long serialVersionUID = -460034748688687252L;

    public EmailCodeCreateFailedException(String s) {
        super(s);
    }
}
