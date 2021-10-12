package com.careydevelopment.ecosystem.user.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.careydevelopment.ecosystem.user.harness.UserHarness;
import com.careydevelopment.ecosystem.user.model.Registrant;
import com.careydevelopment.ecosystem.user.model.User;

@ExtendWith(MockitoExtension.class)
public class UserUtilTest {

    @Mock
    private PasswordEncoder encoder;
    
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
}
