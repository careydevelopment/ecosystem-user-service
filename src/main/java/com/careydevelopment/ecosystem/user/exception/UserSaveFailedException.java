package com.careydevelopment.ecosystem.user.exception;

public class UserSaveFailedException extends RuntimeException {

    private static final long serialVersionUID = 132624850105946905L;

    public UserSaveFailedException(String s) {
        super(s);
    }
}
