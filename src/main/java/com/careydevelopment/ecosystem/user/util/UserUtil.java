package com.careydevelopment.ecosystem.user.util;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import com.careydevelopment.ecosystem.user.exception.InvalidRequestException;
import com.careydevelopment.ecosystem.user.exception.UnauthorizedUpdateException;
import com.careydevelopment.ecosystem.user.exception.UserNotFoundException;
import com.careydevelopment.ecosystem.user.model.Registrant;
import com.careydevelopment.ecosystem.user.model.User;
import com.careydevelopment.ecosystem.user.repository.UserRepository;

import us.careydevelopment.util.api.model.ValidationError;
import us.careydevelopment.util.api.validation.ValidationUtil;

@Component
public class UserUtil {
    
    private static final Logger LOG = LoggerFactory.getLogger(UserUtil.class);

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private SecurityUtil securityUtil;
    
    @Autowired
    private UserRepository userRepository;
    
    public User convertRegistrantToUser(Registrant registrant) {
        User user = new User();

        user.setEmail(registrant.getEmailAddress());
        user.setFirstName(registrant.getFirstName());
        user.setLastName(registrant.getLastName());
        user.setPassword(encoder.encode(registrant.getPassword()));
        user.setUsername(registrant.getUsername());
        user.setPhoneNumber(registrant.getPhone());

        return user;
    }  
    
    
    public void validateUserUpdate(User user, BindingResult bindingResult) {
        boolean allowed = securityUtil.isAuthorizedByUserId(user.getId());

        if (allowed) {
            if (bindingResult.hasErrors()) {
                LOG.error("Binding result: " + bindingResult);
                
                List<ValidationError> errors = ValidationUtil.convertBindingResultToValidationErrors(bindingResult);
                throw new InvalidRequestException("Invalid user request", errors);
            }
        } else {
            LOG.error("Not allowed to update user ID " + user.getId());
            throw new UnauthorizedUpdateException("Not allowed to update user ID " + user.getId());
        }
    }
    
    public User validateUserDelete(String userId) {
        boolean allowed = securityUtil.isAuthorizedByUserId(userId);

        if (allowed) {
            Optional<User> userOpt = userRepository.findById(userId);
            
            if (userOpt.isEmpty()) {
                throw new UserNotFoundException("User " + userId + " doesn't exist");
            } else {
                return userOpt.get();
            }
        } else {
            LOG.debug("Not allowed to delete user ID " + userId);
            throw new UnauthorizedUpdateException("Not allowed to delete another user");
        }
    }
}
