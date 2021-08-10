package com.careydevelopment.ecosystem.user.controller;

import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.careydevelopment.ecosystem.user.model.Registrant;
import com.careydevelopment.ecosystem.user.model.RegistrantAuthentication;
import com.careydevelopment.ecosystem.user.model.User;
import com.careydevelopment.ecosystem.user.repository.RegistrantAuthenticationRepository;
import com.careydevelopment.ecosystem.user.service.RegistrantService;
import com.careydevelopment.ecosystem.user.service.ServiceException;

import us.careydevelopment.util.api.model.ValidationErrorResponse;
import us.careydevelopment.util.api.response.ResponseEntityUtil;
import us.careydevelopment.util.api.util.ValidationUtil;

@RestController
public class RegistrationController {

    private static final Logger LOG = LoggerFactory.getLogger(RegistrationController.class);
    
    
    @Autowired
    private RegistrantService registrantService;
    
    @Autowired
    private Validator validator;
    
    @Autowired
    private RegistrantAuthenticationRepository registrantAuthenticationRepository;
    
    
    @GetMapping("/emailVerificationStatus")
    public ResponseEntity<?> getEmailVerificationStatus(@RequestParam String username, @RequestParam String code) {
        LOG.debug("Checking email verification for user " + username + " with code " + code);

        boolean verified = registrantService.validateEmailCode(username, code);
        if (verified) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    
    
    @GetMapping("/smsVerificationStatus")
    public ResponseEntity<?> getSmsVerificationStatus(@RequestParam String username, @RequestParam String code) {
        LOG.debug("Checking SMS verification for user " + username + " with code " + code);

        List<RegistrantAuthentication> auths = registrantAuthenticationRepository.findByUsernameAndTypeOrderByTimeDesc(username, RegistrantAuthentication.Type.TEXT.toString());
        
        if (auths != null && auths.size() > 0) {
            //most recent persisted record will be the latest SMS record
            RegistrantAuthentication auth = auths.get(0);
            
            boolean verified = registrantService.validateTextCode(auth.getRequestId(), code);
            
            if (verified) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
        }
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    
    
    @PostMapping("/registrant")
    public ResponseEntity<?> createRegistrant(@RequestBody Registrant registrant) {
        LOG.debug("Registrant is " + registrant);
        
        Set<ConstraintViolation<Object>> violations = validator.validate(registrant);
        LOG.debug("Violations: " + violations);
        
        ValidationErrorResponse validationErrorResponse = ValidationUtil.convertConstraintViolationsToValidationErroResponse(violations);
        
        //add in any validations not caught by JSR 380
        ValidationErrorResponse response = registrantService.validateRegistrant(registrant, validationErrorResponse);
        LOG.debug("validation is " + response);
        
        if (response.hasErrors()) {
            return ResponseEntityUtil.createResponseEntity(response);
        }
        
        try {
            User savedUser = registrantService.saveUser(registrant);
            
            if (savedUser == null) {
                return ResponseEntityUtil.createResponseEntityWithError("Registrant not saved. Please contact support.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            
            registrantService.createEmailCode(registrant);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (ServiceException se) {
            return ResponseEntityUtil.createResponseEntityWithError(se.getMessage(), se.getStatusCode());
        }
    }
}
