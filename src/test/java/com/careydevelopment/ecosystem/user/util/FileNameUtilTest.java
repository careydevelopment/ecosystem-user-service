package com.careydevelopment.ecosystem.user.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.careydevelopment.ecosystem.user.harness.UserHarness;
import com.careydevelopment.ecosystem.user.model.User;

public class FileNameUtilTest {

    @Test
    public void testCreateFileNameWithValidUser() {
        User user = UserHarness.getValidUser();
        String fileName = FileNameUtil.createFileName(user);
        Assertions.assertTrue(fileName.startsWith(user.getId()));
    }
    
    
    @Test
    public void testCreateFileNameWithNullUser() {
        try {
            String fileName = FileNameUtil.createFileName(null);
            Assertions.fail();
        } catch (IllegalArgumentException ie) {
            //we should get here
        }
    }
    
    
    @Test
    public void testCreateFileNameWithNullUserId() {
        try {
            User user = UserHarness.getValidUser();
            user.setId(null);
            
            String fileName = FileNameUtil.createFileName(user);
            Assertions.fail();
        } catch (IllegalArgumentException ie) {
            //we should get here
        }
    }
}
