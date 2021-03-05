package com.careydevelopment.ecosystem.user.harness;

import com.careydevelopment.ecosystem.user.model.User;

public class UserHarness {

    private static final String VALID_FIRST_NAME = "Manny";
    private static final String VALID_LAST_NAME = "Granados";
    private static final String VALID_USERNAME = "mgranados";
    private static final String VALID_ID = "444";
    
    
    public static final User getValidUser() {
        User user = new User();
        
        user.setFirstName(VALID_FIRST_NAME);
        user.setLastName(VALID_LAST_NAME);
        user.setUsername(VALID_USERNAME);
        user.setId(VALID_ID);
        
        return user;
    }
}
