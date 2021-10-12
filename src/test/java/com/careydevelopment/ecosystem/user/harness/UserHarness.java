package com.careydevelopment.ecosystem.user.harness;

import com.careydevelopment.ecosystem.user.model.Registrant;
import com.careydevelopment.ecosystem.user.model.User;

public class UserHarness {

    public static final String VALID_FIRST_NAME = "Manny";
    public static final String VALID_LAST_NAME = "Granados";
    public static final String VALID_USERNAME = "mgranados";
    public static final String VALID_ID = "444";
    public static final String VALID_EMAIL_ADDRESS = "you@toohottohandle.com";
    public static final String VALID_PASSWORD = "password";
    public static final String VALID_PHONE = "919-555-1212";
    
    public static final User getValidUser() {
        User user = new User();
        
        user.setFirstName(VALID_FIRST_NAME);
        user.setLastName(VALID_LAST_NAME);
        user.setUsername(VALID_USERNAME);
        user.setId(VALID_ID);
        user.setEmail(VALID_EMAIL_ADDRESS);
        user.setPassword(VALID_PASSWORD);
        user.setPhoneNumber(VALID_PHONE);
        
        return user;
    }
    
    public static final Registrant getValidRegistrant() {
        Registrant registrant = new Registrant();
        
        registrant.setEmailAddress(VALID_EMAIL_ADDRESS);
        registrant.setFirstName(VALID_FIRST_NAME);
        registrant.setLastName(VALID_LAST_NAME);
        registrant.setPassword(VALID_PASSWORD);
        registrant.setPhone(VALID_PHONE);
        registrant.setUsername(VALID_USERNAME);
        
        return registrant;
    }
}
