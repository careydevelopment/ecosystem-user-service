package com.careydevelopment.ecosystem.user.exception;

import org.springframework.security.core.AuthenticationException;

public class UserServiceAuthenticationException extends AuthenticationException {
    
    private static final long serialVersionUID = 6356844005269578058L;

    public UserServiceAuthenticationException(String s) {
        super(s);
    }
}
