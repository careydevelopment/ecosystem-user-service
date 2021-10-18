package com.careydevelopment.ecosystem.user.util;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.careydevelopment.ecosystem.user.harness.UserHarness;
import com.careydevelopment.ecosystem.user.model.User;

import us.careydevelopment.ecosystem.jwt.constants.Authority;

@ExtendWith(MockitoExtension.class)
public class SecurityUtilTest {

    @Mock
    private SessionUtil sessionUtil;
    
    @InjectMocks
    private SecurityUtil securityUtil;
    
    @Test
    public void testValidateUserAllowed() {
        User user = UserHarness.getValidUser();
        
        Mockito.when(sessionUtil.getCurrentUser()).thenReturn(user);
        
        boolean allowed = securityUtil.isAuthorizedByUserId(user.getId());
        Assertions.assertTrue(allowed);
    }
    
    @Test
    public void testValidateUserNotAllowed() {
        User user = UserHarness.getValidUser();
        
        User returnedUser = UserHarness.getValidUser();
        returnedUser.setId("22");
        
        Mockito.when(sessionUtil.getCurrentUser()).thenReturn(returnedUser);
        
        boolean allowed = securityUtil.isAuthorizedByUserId(user.getId());
        Assertions.assertFalse(allowed);
    }
    
    @Test
    public void testValidateUserAdmin() {
        User user = UserHarness.getValidUser();
        
        User returnedUser = UserHarness.getValidUser();
        returnedUser.setId("22");
        returnedUser.setAuthorityNames(List.of(Authority.ADMIN_ECOSYSTEM_USER));
        
        Mockito.when(sessionUtil.getCurrentUser()).thenReturn(returnedUser);
        
        boolean allowed = securityUtil.isAuthorizedByUserId(user.getId());
        Assertions.assertTrue(allowed);
    }
    
    @Test
    public void testValidateUserNullId() {
        User user = UserHarness.getValidUser();
        user.setId(null);
        
        User returnedUser = UserHarness.getValidUser();
        returnedUser.setId("22");
        returnedUser.setAuthorityNames(List.of(Authority.ADMIN_ECOSYSTEM_USER));
        
        Mockito.when(sessionUtil.getCurrentUser()).thenReturn(returnedUser);
        
        boolean allowed = securityUtil.isAuthorizedByUserId(user.getId());
        Assertions.assertFalse(allowed);
    }
}
