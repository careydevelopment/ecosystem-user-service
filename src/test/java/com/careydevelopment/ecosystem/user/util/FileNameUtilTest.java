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
    
    
    @Test
    public void testCreateFileName() {
        User user = UserHarness.getValidUser();
        String originalFileName = "image.jpg";
        
        String newFileName = FileNameUtil.createFileName(user, originalFileName);
        
        Assertions.assertTrue(newFileName.startsWith(user.getId()));
        Assertions.assertTrue(newFileName.endsWith(".jpg"));
    }
    
    
    @Test
    public void testAppendExtensionFromOriginalFileNameWithNullFileName() {
        String fileName = null;
        String originalFileName = "image.jpg";
        
        Assertions.assertThrows(IllegalArgumentException.class, 
                () -> FileNameUtil.appendExtensionFromOriginalFileName(fileName, originalFileName));
    }
    
    
    @Test
    public void testAppendExtensionFromOriginalFileNameWithEmptyFileName() {
        String fileName = "";
        String originalFileName = "image.jpg";
        
        Assertions.assertThrows(IllegalArgumentException.class, 
                () -> FileNameUtil.appendExtensionFromOriginalFileName(fileName, originalFileName));
    }
    
    
    @Test
    public void testAppendExtensionFromOriginalFileNameWithEmptyOriginalFileName() {
        String fileName = "444-4545454";
        String originalFileName = "";
        
        Assertions.assertThrows(IllegalArgumentException.class, 
                () -> FileNameUtil.appendExtensionFromOriginalFileName(fileName, originalFileName));
    }
    
    
    @Test
    public void testAppendExtensionFromOriginalFileNameWithNullOriginalFileName() {
        String fileName = "444-4545454";
        String originalFileName = null;
        
        Assertions.assertThrows(IllegalArgumentException.class, 
                () -> FileNameUtil.appendExtensionFromOriginalFileName(fileName, originalFileName));
    }
    
    
    @Test
    public void testAppendExtensionFromOriginalFileNameWithValidArgs() {
        String fileName = "444-4545454";
        String originalFileName = "image.jpg";
        
        String newFileName = FileNameUtil.appendExtensionFromOriginalFileName(fileName, originalFileName);
        
        Assertions.assertEquals("444-4545454.jpg", newFileName);
    }
}
