package com.careydevelopment.ecosystem.user.service;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.careydevelopment.ecosystem.user.exception.ServiceException;
import com.careydevelopment.ecosystem.user.exception.UserNotFoundException;
import com.careydevelopment.ecosystem.user.harness.UserHarness;
import com.careydevelopment.ecosystem.user.model.User;
import com.careydevelopment.ecosystem.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void testUpdateUserSuccessful() {
        final String newStreet = "111 new street";
        
        User user = UserHarness.getValidUser();
        User updatedUser = UserHarness.getValidUser();
        
        Optional<User> userOpt = Optional.of(user);
        
        updatedUser.setStreet1(newStreet);
        
        Mockito.when(userRepository.findById(Mockito.anyString())).thenReturn(userOpt);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(updatedUser);
        
        User returnedUser = userService.updateUser(user);
        Assertions.assertEquals(newStreet, returnedUser.getStreet1());
    }

    @Test
    public void testUpdateUserUnexpectedException() {
        User user = UserHarness.getValidUser();
        Optional<User> userOpt = Optional.of(user);
        
        Mockito.when(userRepository.findById(Mockito.anyString())).thenReturn(userOpt);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenThrow(new RuntimeException("Something went wrong!"));
        
        Assertions.assertThrows(ServiceException.class, () -> userService.updateUser(user));
    }
    
    @Test
    public void testUpdateUserNotFound() {
        User user = UserHarness.getValidUser();
        
        Optional<User> userOpt = Optional.empty();
        
        Mockito.when(userRepository.findById(Mockito.anyString())).thenReturn(userOpt);
        
        Assertions.assertThrows(UserNotFoundException.class, () -> userService.updateUser(user));
    }
    
    @Test
    public void testUpdateFailedLoginAttemptsAddOne() {
        final long currentTime = System.currentTimeMillis();
        
        //make sure we get a good time differential 
        try {
            Thread.sleep(500);
        } catch (Exception e) {
            Assertions.fail();
        }
        
        User user = UserHarness.getValidUser();
        user.setFailedLoginAttempts(1);
        user.setLastFailedLoginTime(currentTime);
        
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(user);
        
        userService.updateFailedLoginAttempts(user.getUsername());
        
        Assertions.assertEquals(2, user.getFailedLoginAttempts());
        Assertions.assertNotEquals(currentTime, user.getLastFailedLoginTime());
    }
    
    @Test
    public void testResetFailedLoginAttemptsAddOne() {
        User user = UserHarness.getValidUser();
        user.setFailedLoginAttempts(1);
        
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(user);
        
        userService.successfulLogin(user.getUsername());
        
        Assertions.assertNull(user.getFailedLoginAttempts());
    }    
}
