package com.careydevelopment.ecosystem.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.careydevelopment.ecosystem.user.repository.UserRepository;

import us.careydevelopment.ecosystem.jwt.service.JwtUserDetailsService;


@Service
public class JwtService extends JwtUserDetailsService {

    public JwtService(@Autowired UserRepository userRepository) {
        this.userDetailsRepository = userRepository;
    }
    
}