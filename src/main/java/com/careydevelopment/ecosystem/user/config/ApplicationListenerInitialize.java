package com.careydevelopment.ecosystem.user.config;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.careydevelopment.ecosystem.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ApplicationListenerInitialize implements ApplicationListener<ApplicationReadyEvent>  {
	
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationListenerInitialize.class);

    
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;
	
    public void onApplicationEvent(ApplicationReadyEvent event) {
    }
}