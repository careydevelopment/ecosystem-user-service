package com.careydevelopment.ecosystem.user.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.careydevelopment.ecosystem.user.model.User;
import com.careydevelopment.ecosystem.user.repository.IpLogRepository;
import com.careydevelopment.ecosystem.user.repository.UserRepository;
import com.careydevelopment.ecosystem.user.service.IpLogService;

@Component
public class ApplicationListenerInitialize implements ApplicationListener<ApplicationReadyEvent>  {
	
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationListenerInitialize.class);

    
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;
	
    @Autowired
    IpLogRepository ipLogRepo;
    
    @Autowired
    IpLogService ipLogService;
    
    public void onApplicationEvent(ApplicationReadyEvent event) {
        //User user = userRepository.findByUsername("milton");
        //user.setFailedLoginAttempts(null);
        
        //userRepository.save(user);
    }
}