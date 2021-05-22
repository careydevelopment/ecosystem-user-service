package com.careydevelopment.ecosystem.user.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import com.careydevelopment.ecosystem.user.service.JwtService;
import com.careydevelopment.ecosystem.user.util.JwtUtil;

import us.careydevelopment.ecosystem.jwt.config.CredentialsAndJwtSecurityConfig;



@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends CredentialsAndJwtSecurityConfig {

    public WebSecurityConfig(@Autowired JwtService jwtUserDetailsService, 
            @Autowired JwtAuthenticationProvider jwtAuthenticationProvider, @Autowired JwtUtil jwtUtil) {
        
        this.authenticationProvider = jwtAuthenticationProvider;
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.jwtUtil = jwtUtil;
    }        
        
}