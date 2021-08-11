package com.careydevelopment.ecosystem.user.service;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.careydevelopment.ecosystem.user.model.Registrant;
import com.careydevelopment.ecosystem.user.model.RegistrantAuthentication;
import com.careydevelopment.ecosystem.user.model.User;
import com.careydevelopment.ecosystem.user.model.UserSearchCriteria;
import com.careydevelopment.ecosystem.user.repository.RegistrantAuthenticationRepository;
import com.careydevelopment.ecosystem.user.repository.UserRepository;
import com.careydevelopment.ecosystem.user.util.RecaptchaUtil;
import com.careydevelopment.ecosystem.user.util.TotpUtil;

import us.careydevelopment.util.api.model.ValidationError;
import us.careydevelopment.util.api.model.ValidationErrorResponse;
import us.careydevelopment.util.date.DateConversionUtil;


@Service
public class RegistrantService {

    private static final Logger LOG = LoggerFactory.getLogger(RegistrantService.class);

    private static final float RECAPTCHA_MIN_SCORE = 0.8f;
    private static final int MAX_MINUTES_FOR_CODE = 5;
      
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;
    
    @Autowired
    private RecaptchaUtil recaptchaUtil;
    
    @Autowired
    private TotpUtil totpUtil;
    
    @Autowired
    private RegistrantAuthenticationRepository registrantAuthenticationRepository;
    
    @Autowired
    private EmailService emailService;

    @Autowired
    private SmsService smsService;
    
    
    public void addAuthority(String username, String authority) {
        User user = userRepository.findByUsername(username);
        
        if (user != null) {
            user.getAuthorityNames().add(authority);
            userRepository.save(user);
        }
    }
    
    
    public boolean validateTextCode(String requestId, String code) {
        boolean verified = smsService.checkValidationCode(requestId, code);
        return verified;
    }
  
    
    public boolean validateEmailCode(String username, String code) {
        List<RegistrantAuthentication> list = validateCode(username, code, RegistrantAuthentication.Type.EMAIL);
        
        if (list != null && list.size() > 0) {
            return true;
        } else {
            return false;
        }
    }
    
    
    public List<RegistrantAuthentication> validateCode(String username, String code, RegistrantAuthentication.Type type) {
        long time = System.currentTimeMillis() - (DateConversionUtil.NUMBER_OF_MILLISECONDS_IN_MINUTE * MAX_MINUTES_FOR_CODE);
        
        List<RegistrantAuthentication> auths = registrantAuthenticationRepository.codeCheck(username, time, type.toString(), code);
        
        return auths;
    }
    
    
    public void createTextCode(String username) {
        User user = userRepository.findByUsername(username);
        LOG.debug("Found user is " + user);
        
        if (user != null) {
            String requestId = smsService.sendValidationCode(user.getPhoneNumber());
            
            if (requestId != null) {
                RegistrantAuthentication auth = new RegistrantAuthentication();
                auth.setUsername(username);
                auth.setTime(System.currentTimeMillis());
                auth.setType(RegistrantAuthentication.Type.TEXT);
                auth.setRequestId(requestId);
                
                registrantAuthenticationRepository.save(auth);            
            }    
        }
    }
    
    
    public void createEmailCode(Registrant registrant) {
        String code = createCode(registrant.getUsername(), RegistrantAuthentication.Type.EMAIL);

        String validationBody = "\n\nYour verification code for Carey Development, LLC and the CarEcosystem Network.\n\n"
                + "Use verification code: " + code;
                
        emailService.sendSimpleMessage(registrant.getEmailAddress(), "Your Verification Code", validationBody);
    }
    
    
    public String createCode(String username, RegistrantAuthentication.Type type) {
        RegistrantAuthentication auth = new RegistrantAuthentication();
        auth.setUsername(username);
        auth.setTime(System.currentTimeMillis());
        auth.setType(type);
        
        String code = totpUtil.getTOTPCode();
        auth.setCode(code);
        
        registrantAuthenticationRepository.save(auth);
        
        return code;
    }
    
    
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
        user.setPhoneNumber(registrant.getPhone());
        
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
            float score = recaptchaUtil.createAssessment(registrant.getRecaptchaResponse());
            
            if (score < RECAPTCHA_MIN_SCORE) {
                //user-friendly error message not necessary if a bot is trying to get in
                addError(errorResponse, "Google thinks you're a bot", null, null);
            }
        } catch (IOException e) {
            LOG.error("Problem validating recaptcha!", e);
            throw new ServiceException("Problem validating recaptcha!", HttpStatus.INTERNAL_SERVER_ERROR.value());
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
                addError(errorResponse, "Email address is taken", "emailAddress", "emailTaken");
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
