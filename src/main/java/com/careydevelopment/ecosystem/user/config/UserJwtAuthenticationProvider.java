package com.careydevelopment.ecosystem.user.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.careydevelopment.ecosystem.user.service.JwtService;
import com.careydevelopment.ecosystem.user.util.JwtUtil;

import us.careydevelopment.ecosystem.jwt.config.JwtAuthenticationProvider;

@Component
public class UserJwtAuthenticationProvider extends JwtAuthenticationProvider {

    private static final Logger LOG = LoggerFactory.getLogger(UserJwtAuthenticationProvider.class);


    public UserJwtAuthenticationProvider(@Autowired JwtService jwtUserDetailsService, @Autowired JwtUtil jwtUtil) {
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.jwtUtil = jwtUtil;
    }
    
}
