package com.careydevelopment.ecosystem.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.careydevelopment.ecosystem.user.model.User;
import com.careydevelopment.ecosystem.user.repository.UserRepository;
import com.careydevelopment.ecosystem.user.util.SecurityUtil;

import us.careydevelopment.ecosystem.jwt.service.JwtUserDetailsService;

@Service
public class UserService extends JwtUserDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    
    @Autowired
    private SecurityUtil securityUtil;
    
    
    public UserService(@Autowired UserRepository userRepository) {
        this.userDetailsRepository = userRepository;
    }
    
    
    public User updateUser(User user) {
        addExcludedFields(user);
        User updatedUser = ((UserRepository)userDetailsRepository).save(user);
        
        return updatedUser;
    }
    
    
    private void addExcludedFields(User user) {
        User currentUser = securityUtil.getCurrentUser();
        
        user.setPassword(currentUser.getPassword());
        user.setAuthorityNames(currentUser.getAuthorityNames());
    }
    
    
    public void updateFailedLoginAttempts(String username) {
        try {
            UserDetails userDetails = loadUserByUsername(username);
            User user = (User)userDetails;
            
            Integer failedLoginAttempts = user.getFailedLoginAttempts();
            if (failedLoginAttempts == null) {
                failedLoginAttempts = 1;
            } else {
                failedLoginAttempts++;
            }
            
            user.setFailedLoginAttempts(failedLoginAttempts);
            user.setLastFailedLoginTime(System.currentTimeMillis());
            
            ((UserRepository)userDetailsRepository).save(user);
        } catch (UsernameNotFoundException e) {
            LOG.error("Problem attempting to update failed login attempts!", e);
        }
    }
    
    
    public void successfulLogin(String username) {
        resetFailedLoginAttempts(username);
    }
    
    
    private void resetFailedLoginAttempts(String username) {
        UserDetails userDetails = loadUserByUsername(username);
        User user = (User)userDetails;
        
        Integer failedLoginAttempts = user.getFailedLoginAttempts();
        if (failedLoginAttempts != null) {
            user.setFailedLoginAttempts(null);
            ((UserRepository)userDetailsRepository).save(user);
        }
    }
}
