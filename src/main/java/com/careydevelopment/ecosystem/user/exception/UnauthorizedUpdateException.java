package com.careydevelopment.ecosystem.user.exception;

public class UnauthorizedUpdateException extends RuntimeException {

    private static final long serialVersionUID = 2766148850385132820L;

    public UnauthorizedUpdateException(String s) {
        super(s);
    }
}
