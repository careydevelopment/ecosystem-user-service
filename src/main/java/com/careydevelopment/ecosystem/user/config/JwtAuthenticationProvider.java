package com.careydevelopment.ecosystem.user.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.careydevelopment.ecosystem.user.util.JwtUtil;

import us.careydevelopment.ecosystem.jwt.config.CredentialsAndJwtAuthenticationProvider;
import us.careydevelopment.ecosystem.jwt.service.JwtUserDetailsService;

@Component
public class JwtAuthenticationProvider extends CredentialsAndJwtAuthenticationProvider {

    private static final Logger LOG = LoggerFactory.getLogger(JwtAuthenticationProvider.class);

    public JwtAuthenticationProvider(@Autowired JwtUserDetailsService service, @Autowired JwtUtil jwtUtil) {
        this.jwtUserDetailsService = service;
        this.jwtUtil = jwtUtil;
    }

}
