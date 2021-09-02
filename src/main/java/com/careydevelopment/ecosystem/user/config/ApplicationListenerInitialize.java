package com.careydevelopment.ecosystem.user.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.careydevelopment.ecosystem.user.model.Registrant;
import com.careydevelopment.ecosystem.user.model.RegistrantAuthentication;
import com.careydevelopment.ecosystem.user.model.User;
import com.careydevelopment.ecosystem.user.model.UserSearchCriteria;
import com.careydevelopment.ecosystem.user.repository.IpLogRepository;
import com.careydevelopment.ecosystem.user.repository.RegistrantAuthenticationRepository;
import com.careydevelopment.ecosystem.user.repository.UserRepository;
import com.careydevelopment.ecosystem.user.service.RegistrantService;
import com.careydevelopment.ecosystem.user.service.SmsService;
import com.careydevelopment.ecosystem.user.service.UserService;
import com.careydevelopment.ecosystem.user.util.TotpUtil;

import us.careydevelopment.ecosystem.jwt.constants.Authority;

@Component
public class ApplicationListenerInitialize implements ApplicationListener<ApplicationReadyEvent>  {
	
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationListenerInitialize.class); 

    
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;
	
    @Autowired
    UserService userService;
    
    @Autowired
    TotpUtil totpUtil;
    
    @Autowired
    IpLogRepository ipLogRepository;
    
    @Autowired
    RegistrantService registrantService;
    
    @Autowired
    RegistrantAuthenticationRepository registrantAuthenticationRepository;
    
    @Autowired
    SmsService smsService;
    
    
    public void onApplicationEvent(ApplicationReadyEvent event) {

    }
}