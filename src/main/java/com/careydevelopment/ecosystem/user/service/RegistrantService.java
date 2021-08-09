package com.careydevelopment.ecosystem.user.service;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.careydevelopment.ecosystem.user.model.Registrant;
import com.careydevelopment.ecosystem.user.model.User;
import com.careydevelopment.ecosystem.user.model.UserSearchCriteria;
import com.careydevelopment.ecosystem.user.repository.UserRepository;
import com.careydevelopment.ecosystem.user.util.RecaptchaUtil;

import us.careydevelopment.util.api.model.ValidationError;
import us.careydevelopment.util.api.model.ValidationErrorResponse;


@Service
public class RegistrantService {

    private static final Logger LOG = LoggerFactory.getLogger(RegistrantService.class);

    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;
    
    
    @Autowired
    private RecaptchaUtil recaptchaUtil;
    
    
    public User saveUser(Registrant registrant) {
        User user = convertRegistrantToUser(registrant);
        
        User savedUser = userRepository.save(user);
        return savedUser;
    }
    
    
    private User convertRegistrantToUser(Registrant registrant) {
        User user = new User();
        
        user.setEmail(registrant.getEmailAddress());
        user.setFirstName(registrant.getFirstName());
        user.setLastName(registrant.getLastName());
        user.setPassword(encoder.encode(registrant.getPassword()));
        user.setUsername(registrant.getUsername());
        
        return user;
    }
    
    
    public ValidationErrorResponse validateRegistrant(Registrant registrant, ValidationErrorResponse errorResponse) {
        validateUniqueName(errorResponse, registrant);
        validateUniqueEmail(errorResponse, registrant);
        validateRecaptcha(errorResponse, registrant);
        
        return errorResponse;
    }    
    
    
    private void validateRecaptcha(ValidationErrorResponse errorResponse, Registrant registrant) {
        try {
            recaptchaUtil.createAssessment(registrant.getRecaptchaResponse());
        } catch (IOException e) {
            LOG.error("Problem validating recaptcha!", e);
        }
    }
    
    
    private void validateUniqueName(ValidationErrorResponse errorResponse, Registrant registrant) {
        String username = registrant.getUsername();
        
        if (!StringUtils.isBlank(username)) {
            UserSearchCriteria searchCriteria = new UserSearchCriteria();
            searchCriteria.setUsername(username.trim());
            
            List<User> users = userService.search(searchCriteria);
            if (users.size() > 0) {
                addError(errorResponse, "Username is taken", "username", "usernameTaken");
            }
        }
    }
    
    
    private void validateUniqueEmail(ValidationErrorResponse errorResponse, Registrant registrant) {
        String email = registrant.getEmailAddress();
        
        if (!StringUtils.isBlank(email)) {
            UserSearchCriteria searchCriteria = new UserSearchCriteria();
            searchCriteria.setEmailAddress(email.trim());
            
            List<User> users = userService.search(searchCriteria);
            if (users.size() > 0) {
                addError(errorResponse, "Email address is taken", "email", "emailTaken");
            }
        }
    }
    
    
    private void addError(ValidationErrorResponse errorResponse, String errorMessage, String field, String code) {        
        ValidationError validationError = new ValidationError();
        validationError.setCode(code);
        validationError.setDefaultMessage(errorMessage);
        validationError.setField(field);
        
        errorResponse.getErrors().add(validationError);
    }
}
