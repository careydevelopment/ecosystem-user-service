package com.careydevelopment.ecosystem.user.util;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;

import com.careydevelopment.ecosystem.user.exception.InvalidRequestException;
import com.careydevelopment.ecosystem.user.exception.UnauthorizedUpdateException;
import com.careydevelopment.ecosystem.user.exception.UserNotFoundException;
import com.careydevelopment.ecosystem.user.harness.BindingResultHarness;
import com.careydevelopment.ecosystem.user.harness.UserHarness;
import com.careydevelopment.ecosystem.user.model.Registrant;
import com.careydevelopment.ecosystem.user.model.User;
import com.careydevelopment.ecosystem.user.repository.UserRepository;


@ExtendWith(MockitoExtension.class)
public class UserUtilTest {

    @Mock
    private PasswordEncoder encoder;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private SecurityUtil securityUtil;
    
    @InjectMocks
    private UserUtil userUtil;
    
    @Test
    public void testConvertRegistrantToUserWithSuccess() {
        Registrant registrant = UserHarness.getValidRegistrant();
        
        Mockito.when(encoder.encode(Mockito.anyString())).thenReturn(UserHarness.VALID_PASSWORD);
        
        User user = userUtil.convertRegistrantToUser(registrant);
        
        Assertions.assertEquals(UserHarness.VALID_FIRST_NAME, user.getFirstName());
        Assertions.assertEquals(UserHarness.VALID_LAST_NAME, user.getLastName());
        Assertions.assertEquals(UserHarness.VALID_EMAIL_ADDRESS, user.getEmail());
        Assertions.assertEquals(UserHarness.VALID_PASSWORD, user.getPassword());
        Assertions.assertEquals(UserHarness.VALID_PHONE, user.getPhoneNumber());
        Assertions.assertEquals(UserHarness.VALID_USERNAME, user.getUsername());
    }
    
    @Test
    public void testValidateUserUpdateWithBindingErrors() {
        User user = UserHarness.getValidUser();
        
        BindingResult bindingResult = BindingResultHarness.getBindingResultWithErrors(new Registrant(), "registrant");
        
        Mockito.when(securityUtil.isAuthorizedByUserId(Mockito.anyString())).thenReturn(true);
        
        Assertions.assertThrows(InvalidRequestException.class, () -> userUtil.validateUserUpdate(user, bindingResult));
    }
    
    @Test
    public void testValidateUserUpdateUnauthorized() {
        User user = UserHarness.getValidUser();
        
        BindingResult bindingResult = BindingResultHarness.getBindingResultWithErrors(new Registrant(), "registrant");
        
        Mockito.when(securityUtil.isAuthorizedByUserId(Mockito.anyString())).thenReturn(false);
        
        Assertions.assertThrows(UnauthorizedUpdateException.class, () -> userUtil.validateUserUpdate(user, bindingResult));
    }
    
    @Test
    public void testValidateUserDeleteAuthorized() {
        User user = UserHarness.getValidUser();
        Optional<User> userOpt = Optional.of(user);
        
        Mockito.when(securityUtil.isAuthorizedByUserId(Mockito.anyString())).thenReturn(true);
        Mockito.when(userRepository.findById(Mockito.anyString())).thenReturn(userOpt);
        
        User returnedUser = userUtil.validateUserDelete(user.getId());
        
        Assertions.assertEquals(returnedUser.getId(), userOpt.get().getId());
    }
    
    @Test
    public void testValidateUserDeleteUnauthorized() {
        User user = UserHarness.getValidUser();
        
        Mockito.when(securityUtil.isAuthorizedByUserId(Mockito.anyString())).thenReturn(false);

        Assertions.assertThrows(UnauthorizedUpdateException.class, () -> userUtil.validateUserDelete(user.getId()));
    }
    
    @Test
    public void testValidateUserDeleteUserNotFound() {
        User user = UserHarness.getValidUser();
        Optional<User> userOpt = Optional.empty();
        
        Mockito.when(securityUtil.isAuthorizedByUserId(Mockito.anyString())).thenReturn(true);
        Mockito.when(userRepository.findById(Mockito.anyString())).thenReturn(userOpt);

        Assertions.assertThrows(UserNotFoundException.class, () -> userUtil.validateUserDelete(user.getId()));
    }
}
