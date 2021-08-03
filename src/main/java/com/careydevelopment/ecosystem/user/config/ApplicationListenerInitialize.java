package com.careydevelopment.ecosystem.user.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.careydevelopment.ecosystem.user.model.User;
import com.careydevelopment.ecosystem.user.model.UserSearchCriteria;
import com.careydevelopment.ecosystem.user.repository.UserRepository;
import com.careydevelopment.ecosystem.user.service.UserService;

@Component
public class ApplicationListenerInitialize implements ApplicationListener<ApplicationReadyEvent>  {
	
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationListenerInitialize.class);

    
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;
	
    @Autowired
    UserService userService;
    
    
    public void onApplicationEvent(ApplicationReadyEvent event) {

//        UserSearchCriteria criteria = new UserSearchCriteria();
//        criteria.setUsername("milton");
//        List<User> users = userService.search(criteria);
//        System.err.println(users);
    }
}