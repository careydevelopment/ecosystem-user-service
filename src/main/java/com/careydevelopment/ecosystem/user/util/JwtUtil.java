package com.careydevelopment.ecosystem.user.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import us.careydevelopment.ecosystem.jwt.util.JwtTokenUtil;

@Component
public class JwtUtil extends JwtTokenUtil {
    
    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.jwtSecret = secret;
    }
}
