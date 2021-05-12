package com.careydevelopment.ecosystem.user.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.careydevelopment.ecosystem.user.repository.UserRepository;
import com.careydevelopment.ecosystem.user.util.JwtTokenUtil;
import com.careydevelopment.ecosystem.user.util.PropertiesUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ApplicationListenerInitialize implements ApplicationListener<ApplicationReadyEvent>  {
	
    @Autowired
    UserRepository userRepository;

    
    @Value("${ecosystem.properties.file.location}")
    private String ecosystemPropertiesFile;

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
        PropertiesUtil propertiesUtil = new PropertiesUtil(ecosystemPropertiesFile);
        String jwtSecret = propertiesUtil.getProperty("jwt.secret");
        JwtTokenUtil.SECRET = jwtSecret;
    }
}