package com.careydevelopment.ecosystem.user.controller;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.careydevelopment.ecosystem.user.exception.InvalidRegistrantRequestException;
import com.careydevelopment.ecosystem.user.model.Registrant;
import com.careydevelopment.ecosystem.user.model.RegistrantAuthentication;
import com.careydevelopment.ecosystem.user.model.User;
import com.careydevelopment.ecosystem.user.repository.RegistrantAuthenticationRepository;
import com.careydevelopment.ecosystem.user.service.RegistrantService;

import us.careydevelopment.ecosystem.jwt.constants.Authority;
import us.careydevelopment.util.api.model.IRestResponse;
import us.careydevelopment.util.api.model.ValidationError;
import us.careydevelopment.util.api.response.ResponseEntityUtil;
import us.careydevelopment.util.api.validation.ValidationUtil;

@RestController
public class RegistrationController {

    private static final Logger LOG = LoggerFactory.getLogger(RegistrationController.class);
    
    
    @Autowired
    private RegistrantService registrantService;
    
    @Autowired
    private RegistrantAuthenticationRepository registrantAuthenticationRepository;
    
    
    @GetMapping("/emailVerificationStatus")
    public ResponseEntity<?> getEmailVerificationStatus(@RequestParam String username, @RequestParam String code) {
        LOG.debug("Checking email verification for user " + username + " with code " + code);

        boolean verified = registrantService.validateEmailCode(username, code);
        
        if (verified) {
            registrantService.createTextCode(username);
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
            
            boolean verified = registrantService.validateTextCode(auth, code);
            
            if (verified) {
                registrantService.addAuthority(username, Authority.BASIC_ECOSYSTEM_USER);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
        }
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    
    @ExceptionHandler(InvalidRegistrantRequestException.class)
    public ResponseEntity<IRestResponse<List<ValidationError>>> invalidRegistrant(InvalidRegistrantRequestException ex) {
        List<ValidationError> errors = ex.getErrors();
        return ResponseEntityUtil.createResponseEntityWithValidationErrors(errors);
    }
    
    @PostMapping("/")
    public ResponseEntity<IRestResponse<User>> createUser(@Valid @RequestBody Registrant registrant, BindingResult bindingResult) {
        LOG.debug("Registrant is " + registrant);
        
        List<ValidationError> validationErrors = ValidationUtil.convertBindingResultToValidationErrors(bindingResult);
        
        //look for any validations not caught by JSR 380
        registrantService.validateRegistrant(registrant, validationErrors);
        
        User savedUser = registrantService.saveUser(registrant);
            
            //registrantService.createEmailCode(registrant);
            
        return ResponseEntityUtil.createSuccessfulResponseEntity("Successfully registered!", HttpStatus.CREATED.value(), savedUser);
    }
}
