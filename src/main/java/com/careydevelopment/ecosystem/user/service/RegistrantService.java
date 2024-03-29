package com.careydevelopment.ecosystem.user.service;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.careydevelopment.ecosystem.user.exception.EmailCodeCreateFailedException;
import com.careydevelopment.ecosystem.user.exception.InvalidRequestException;
import com.careydevelopment.ecosystem.user.exception.ServiceException;
import com.careydevelopment.ecosystem.user.exception.TextCodeCreateFailedException;
import com.careydevelopment.ecosystem.user.exception.UserSaveFailedException;
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
import us.careydevelopment.util.api.validation.ValidationUtil;
import us.careydevelopment.util.date.DateConversionUtil;

@Service
public class RegistrantService {

    private static final Logger LOG = LoggerFactory.getLogger(RegistrantService.class);

    private static final int MAX_MINUTES_FOR_CODE = 5;
    private static final int MAX_FAILED_ATTEMPTS = 5;

    @Value("${recaptcha.active}")
    String recaptchaActive;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecaptchaUtil recaptchaUtil;

    @Autowired
    private TotpUtil totpUtil;

    @Autowired
    private RegistrantAuthenticationRepository registrantAuthenticationRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private UserUtil userUtil;

    public void addAuthority(String username, String authority) {
        try {
            User user = userRepository.findByUsername(username);

            if (user != null) {
                user.getAuthorityNames().add(authority);
                userRepository.save(user);
            }
        } catch (Exception e) {
            LOG.error("Problem adding authority!", e);
            throw new ServiceException("Problem adding authority!");
        }
    }

    public boolean validateTextCode(RegistrantAuthentication auth, String code) {
        boolean verified = false;
        LOG.debug("Failed attempts for " + auth.getUsername() + " is " + auth.getFailedAttempts());

        try {
            if (auth.getFailedAttempts() < MAX_FAILED_ATTEMPTS) {
                String requestId = auth.getRequestId();
                verified = smsService.checkValidationCode(requestId, code);

                if (!verified) {
                    auth.setFailedAttempts(auth.getFailedAttempts() + 1);
                    registrantAuthenticationRepository.save(auth);
                }
            }
        } catch (Exception e) {
            LOG.error("Problem validating text code!", e);
            throw new ServiceException("Problem validating text code!");
        }

        return verified;
    }

    public boolean validateEmailCode(String username, String code) {
        try {
            int previousAttempts = getPreviousAttempts(username, RegistrantAuthentication.Type.EMAIL);
    
            if (previousAttempts < MAX_FAILED_ATTEMPTS) {
                List<RegistrantAuthentication> list = validateCode(username, code, RegistrantAuthentication.Type.EMAIL);
    
                if (list != null && list.size() > 0) {
                    return true;
                } else {
                    incrementFailedAttempts(username, RegistrantAuthentication.Type.EMAIL);
                    return false;
                }
            } else {
                LOG.debug("User " + username + " exceeded max attempts to validate the email code");
                return false;
            }
        } catch (Exception e) {
            LOG.error("Problem validating email code!", e);
            throw new ServiceException("Problem validating email code!");
        }
    }

    private int getPreviousAttempts(String username, RegistrantAuthentication.Type type) {
        int previousAttempts = 0;
        LOG.debug("Checking previous attempts for " + username);

        List<RegistrantAuthentication> auths = registrantAuthenticationRepository
                .findByUsernameAndTypeOrderByTimeDesc(username, type.toString());

        if (auths != null && auths.size() > 0) {
            RegistrantAuthentication auth = auths.get(0);
            previousAttempts = auth.getFailedAttempts();
        }

        LOG.debug("Failed attempts is " + previousAttempts);
        return previousAttempts;
    }

    private void incrementFailedAttempts(String username, RegistrantAuthentication.Type type) {
        List<RegistrantAuthentication> auths = registrantAuthenticationRepository
                .findByUsernameAndTypeOrderByTimeDesc(username, type.toString());

        if (auths != null && auths.size() > 0) {
            RegistrantAuthentication auth = auths.get(0);
            auth.setFailedAttempts(auth.getFailedAttempts() + 1);

            registrantAuthenticationRepository.save(auth);
        }
    }

    private List<RegistrantAuthentication> validateCode(String username, String code,
            RegistrantAuthentication.Type type) {

        long time = System.currentTimeMillis()
                - (DateConversionUtil.NUMBER_OF_MILLISECONDS_IN_MINUTE * MAX_MINUTES_FOR_CODE);

        List<RegistrantAuthentication> auths = registrantAuthenticationRepository.codeCheck(username, time,
                type.toString(), code);

        return auths;
    }

    public void createTextCode(String username) {
        User user = userRepository.findByUsername(username);
        LOG.debug("Found user is " + user);

        if (user != null) {
            try {
                String requestId = smsService.sendValidationCode(user.getPhoneNumber());

                if (requestId != null) {
                    RegistrantAuthentication auth = new RegistrantAuthentication();
                    auth.setUsername(username);
                    auth.setTime(System.currentTimeMillis());
                    auth.setType(RegistrantAuthentication.Type.TEXT);
                    auth.setRequestId(requestId);

                    registrantAuthenticationRepository.save(auth);
                } else {
                    LOG.error("Unable to create text code as user " + username + " doesn't exist");
                    throw new TextCodeCreateFailedException("User " + username + " doesn't exist");
                }
            } catch (Exception e) {
                LOG.error("Problem creating text code!", e);
                throw new TextCodeCreateFailedException(e.getMessage());
            }
        }
    }

    public void createEmailCode(Registrant registrant) {
        try {
            String code = createCode(registrant.getUsername(), RegistrantAuthentication.Type.EMAIL);

            String validationBody = "\n\nYour verification code for Carey Development, LLC and the CarEcosystem Network.\n\n"
                    + "Use verification code: " + code;

            emailService.sendSimpleMessage(registrant.getEmailAddress(), "Your Verification Code", validationBody);
        } catch (Exception e) {
            LOG.error("Problem creating email code!", e);
            throw new EmailCodeCreateFailedException(e.getMessage());
        }
    }

    private String createCode(String username, RegistrantAuthentication.Type type) {
        RegistrantAuthentication auth = new RegistrantAuthentication();
        auth.setUsername(username);
        auth.setTime(System.currentTimeMillis());
        auth.setType(type);

        String code = totpUtil.getTOTPCode();
        auth.setCode(code);

        registrantAuthenticationRepository.save(auth);

        return code;
    }

    public User saveUser(Registrant registrant) {
        User savedUser = null;

        try {
            User user = userUtil.convertRegistrantToUser(registrant);
            savedUser = userRepository.save(user);
        } catch (Exception e) {
            LOG.error("Problem saving user!", e);
            throw new UserSaveFailedException(e.getMessage());
        }

        return savedUser;
    }

    public void validateRegistrant(Registrant registrant, List<ValidationError> errors) {
        handleRemainingValidations(registrant, errors);
        LOG.debug("validation is " + errors);

        if (errors.size() > 0) {
            throw new InvalidRequestException("Invalid registrant", errors);
        }
    }

    private void handleRemainingValidations(Registrant registrant, List<ValidationError> errors) {
        try {
            validateUniqueName(errors, registrant);
            validateUniqueEmail(errors, registrant);
            validateRecaptcha(errors, registrant);
        } catch (Exception e) {
            LOG.error("Problem validating registrant!", e);
            throw new ServiceException("Problem validating registrant!");
        }
    }
    
    private void validateRecaptcha(List<ValidationError> errors, Registrant registrant) throws IOException {
        if (!StringUtils.isBlank(recaptchaActive) && !recaptchaActive.equalsIgnoreCase("false")) {
            float score = recaptchaUtil.createAssessment(registrant.getRecaptchaResponse());

            if (score < RecaptchaUtil.RECAPTCHA_MIN_SCORE) {
                // user-friendly error message not necessary if a bot is trying to get in
                ValidationUtil.addError(errors, "Google thinks you're a bot", null, null);
            }
        }
    }

    private void validateUniqueName(List<ValidationError> errors, Registrant registrant) {
        String username = registrant.getUsername();

        if (!StringUtils.isBlank(username)) {
            UserSearchCriteria searchCriteria = new UserSearchCriteria();
            searchCriteria.setUsername(username.trim());

            List<User> users = userService.search(searchCriteria);
            if (users.size() > 0) {
                ValidationUtil.addError(errors, "Username is taken", "username", "usernameTaken");
            }
        }
    }

    private void validateUniqueEmail(List<ValidationError> errors, Registrant registrant) {
        String email = registrant.getEmailAddress();

        if (!StringUtils.isBlank(email)) {
            UserSearchCriteria searchCriteria = new UserSearchCriteria();
            searchCriteria.setEmailAddress(email.trim());

            List<User> users = userService.search(searchCriteria);
            if (users.size() > 0) {
                ValidationUtil.addError(errors, "Email address is taken", "emailAddress", "emailTaken");
            }
        }
    }
}
