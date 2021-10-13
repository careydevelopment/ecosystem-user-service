package com.careydevelopment.ecosystem.user.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.careydevelopment.ecosystem.user.exception.EmailCodeCreateFailedException;
import com.careydevelopment.ecosystem.user.exception.InvalidRegistrantRequestException;
import com.careydevelopment.ecosystem.user.exception.ServiceException;
import com.careydevelopment.ecosystem.user.exception.UserSaveFailedException;
import com.careydevelopment.ecosystem.user.harness.UserHarness;
import com.careydevelopment.ecosystem.user.model.Registrant;
import com.careydevelopment.ecosystem.user.model.RegistrantAuthentication;
import com.careydevelopment.ecosystem.user.model.User;
import com.careydevelopment.ecosystem.user.model.UserSearchCriteria;
import com.careydevelopment.ecosystem.user.repository.RegistrantAuthenticationRepository;
import com.careydevelopment.ecosystem.user.repository.UserRepository;
import com.careydevelopment.ecosystem.user.util.TotpUtil;
import com.careydevelopment.ecosystem.user.util.UserUtil;

import us.careydevelopment.ecosystem.jwt.util.RecaptchaUtil;
import us.careydevelopment.util.api.model.ValidationError;

@ExtendWith(MockitoExtension.class)
public class RegistrantServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserUtil userUtil;

    @Mock
    private RecaptchaUtil recaptchaUtil;

    @Mock
    private TotpUtil totpUtil;

    @Mock
    private RegistrantAuthenticationRepository registrantAuthenticationRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private SmsService smsService;

    @InjectMocks
    private RegistrantService registrantService;

    @Test
    public void testSaveUserWithSuccess() {
        Registrant registrant = UserHarness.getValidRegistrant();
        User user = UserHarness.getValidUser();

        Mockito.when(userUtil.convertRegistrantToUser(registrant)).thenReturn(user);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        User savedUser = registrantService.saveUser(registrant);

        Assertions.assertNotNull(savedUser);
        Assertions.assertEquals(UserHarness.VALID_EMAIL_ADDRESS, savedUser.getEmail());
        Assertions.assertEquals(UserHarness.VALID_FIRST_NAME, savedUser.getFirstName());
        Assertions.assertEquals(UserHarness.VALID_LAST_NAME, savedUser.getLastName());
    }

    @Test
    public void testSaveUserWithException() {
        Registrant registrant = UserHarness.getValidRegistrant();

        Mockito.when(userUtil.convertRegistrantToUser(registrant))
                .thenThrow(new RuntimeException("Problem converting!"));

        Assertions.assertThrows(UserSaveFailedException.class, () -> registrantService.saveUser(registrant));
    }

    @Test
    public void testCreateEmailCodeWithSuccess() {
        Registrant registrant = UserHarness.getValidRegistrant();
        final String code = "111222";

        Mockito.when(totpUtil.getTOTPCode()).thenReturn(code);
        Mockito.when(registrantAuthenticationRepository.save(Mockito.any(RegistrantAuthentication.class)))
                .thenReturn(null);

        try {
            registrantService.createEmailCode(registrant);
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void testAddAuthorityWithException() {
        Mockito.when(userRepository.findByUsername(Mockito.anyString()))
                .thenThrow(new RuntimeException("Problem finding user!"));

        Assertions.assertThrows(ServiceException.class, () -> registrantService.addAuthority("username", "authority"));
    }

    @Test
    public void testCreateEmailCodeWithException() {
        Registrant registrant = UserHarness.getValidRegistrant();

        Mockito.when(totpUtil.getTOTPCode()).thenThrow(new RuntimeException("Problem getting code!"));

        Assertions.assertThrows(EmailCodeCreateFailedException.class,
                () -> registrantService.createEmailCode(registrant));
    }

    @Test
    public void testValidateEmailCodeWithException() {
        Mockito.when(registrantAuthenticationRepository.findByUsernameAndTypeOrderByTimeDesc(Mockito.anyString(),
                Mockito.anyString())).thenThrow(new RuntimeException("Problem finding username!"));

        Assertions.assertThrows(ServiceException.class, () -> registrantService.validateEmailCode("username", "code"));
    }

    @Test
    public void testValidateRegistrantWithException() {
        Registrant registrant = UserHarness.getValidRegistrant();

        Mockito.when(userService.search(Mockito.any(UserSearchCriteria.class)))
                .thenThrow(new RuntimeException("Problem finding user!"));

        Assertions.assertThrows(ServiceException.class,
                () -> registrantService.validateRegistrant(registrant, new ArrayList<ValidationError>()));
    }
    
    @Test
    public void testValidateRegistrantWithValidationErrors() {
        Registrant registrant = UserHarness.getValidRegistrant();
        User user = UserHarness.getValidUser();
        registrantService.recaptchaActive = "false";
        
        Mockito.when(userService.search(Mockito.any(UserSearchCriteria.class)))
                .thenReturn(List.of(user));

        Assertions.assertThrows(InvalidRegistrantRequestException.class,
                () -> registrantService.validateRegistrant(registrant, new ArrayList<ValidationError>()));
    }
}
