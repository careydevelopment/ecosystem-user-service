package com.careydevelopment.ecosystem.user.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.careydevelopment.ecosystem.user.controller.UserController;
import com.careydevelopment.ecosystem.user.repository.UserRepository;
import com.careydevelopment.ecosystem.user.util.JwtTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ApplicationListenerInitialize implements ApplicationListener<ApplicationReadyEvent>  {
	
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationListenerInitialize.class);

    
    @Autowired
    UserRepository userRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Autowired
    PasswordEncoder encoder;
	
    public void onApplicationEvent(ApplicationReadyEvent event) {
        ObjectMapper mapper = new ObjectMapper();
        
        setCachedData();        
        
        
        Path path = Paths.get("/home/briancarey37060/careydevelopment");

        if (Files.exists(path)) {
            LOG.info("It exists");
        } else {
            LOG.info("It doesn't exist");
        }
    }
    
    
    private void setCachedData() {
        setJwtCachedData();
    }

    
    private void setJwtCachedData() {
        JwtTokenUtil.SECRET = jwtSecret;
    }
}