package com.careydevelopment.ecosystem.user.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import com.careydevelopment.ecosystem.user.service.JwtService;
import com.careydevelopment.ecosystem.user.util.JwtUtil;
import us.careydevelopment.ecosystem.jwt.config.WebSecurityConfig;


@Configuration
@EnableWebSecurity
public class UserWebSecurityConfig extends WebSecurityConfig {

    public UserWebSecurityConfig(@Autowired JwtService jwtUserDetailsService, 
            @Autowired UserJwtAuthenticationProvider jwtAuthenticationProvider, @Autowired JwtUtil jwtUtil) {
        
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.jwtUtil = jwtUtil;
    }
}
