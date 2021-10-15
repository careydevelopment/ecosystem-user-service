package com.careydevelopment.ecosystem.user.exception;

public class UserNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -2234419646899887299L;

    public UserNotFoundException(String s) {
        super(s);
    }
}
