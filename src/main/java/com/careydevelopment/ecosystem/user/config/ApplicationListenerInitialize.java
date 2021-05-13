package com.careydevelopment.ecosystem.user.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.careydevelopment.ecosystem.user.repository.UserRepository;
import com.careydevelopment.ecosystem.user.util.JwtTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ApplicationListenerInitialize implements ApplicationListener<ApplicationReadyEvent>  {
	
    @Autowired
    UserRepository userRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Autowired
    PasswordEncoder encoder;
	
    public void onApplicationEvent(ApplicationReadyEvent event) {
        ObjectMapper mapper = new ObjectMapper();
        
        setCachedData();        
    }
    
    
    private void setCachedData() {
        setJwtCachedData();
    }

    
    private void setJwtCachedData() {
        JwtTokenUtil.SECRET = jwtSecret;
    }
}