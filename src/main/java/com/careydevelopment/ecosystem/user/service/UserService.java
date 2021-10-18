package com.careydevelopment.ecosystem.user.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.careydevelopment.ecosystem.user.exception.ServiceException;
import com.careydevelopment.ecosystem.user.exception.UserNotFoundException;
import com.careydevelopment.ecosystem.user.model.User;
import com.careydevelopment.ecosystem.user.model.UserSearchCriteria;
import com.careydevelopment.ecosystem.user.repository.UserRepository;

import us.careydevelopment.ecosystem.jwt.service.JwtUserDetailsService;

@Service
public class UserService extends JwtUserDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private MongoTemplate mongoTemplate;
    
    public UserService(@Autowired UserRepository userRepository) {
        this.userDetailsRepository = userRepository;
    }

    private UserRepository getUserRepository() {
        return (UserRepository)userDetailsRepository;
    }
    
    public User updateUser(User user) {
        updateFields(user);
        
        try {
            User updatedUser = getUserRepository().save(user);
            return updatedUser;
        } catch (Exception e) {
            LOG.error("Problem updating user!", e);
            throw new ServiceException("Problem updating user!");
        }
    }

    private void updateFields(User user) {
        Optional<User> currentUserOpt = getUserRepository().findById(user.getId());

        if (currentUserOpt.isPresent()) {
            User currentUser = currentUserOpt.get();
            
            //don't let user change username or email address
            //they need be unique across the board
            user.setUsername(currentUser.getUsername());
            user.setEmail(currentUser.getEmail());
            
            user.setPassword(currentUser.getPassword());
            user.setAuthorityNames(currentUser.getAuthorityNames());    
        } else {
            throw new UserNotFoundException("No user with ID: " + user.getId());
        }
    }

    public void updateFailedLoginAttempts(String username) {
        try {
            UserDetails userDetails = loadUserByUsername(username);
            User user = (User) userDetails;

            Integer failedLoginAttempts = user.getFailedLoginAttempts();
            if (failedLoginAttempts == null) {
                failedLoginAttempts = 1;
            } else {
                failedLoginAttempts++;
            }

            user.setFailedLoginAttempts(failedLoginAttempts);
            user.setLastFailedLoginTime(System.currentTimeMillis());

            getUserRepository().save(user);
        } catch (UsernameNotFoundException e) {
            LOG.error("Problem attempting to update failed login attempts!", e);
        }
    }

    public void successfulLogin(String username) {
        resetFailedLoginAttempts(username);
    }

    private void resetFailedLoginAttempts(String username) {
        UserDetails userDetails = loadUserByUsername(username);
        User user = (User) userDetails;

        Integer failedLoginAttempts = user.getFailedLoginAttempts();
        if (failedLoginAttempts != null) {
            user.setFailedLoginAttempts(null);
            getUserRepository().save(user);
        }
    }

    public List<User> search(UserSearchCriteria searchCriteria) {
        List<AggregationOperation> ops = new ArrayList<>();

        if (StringUtils.isBlank(searchCriteria.getEmailAddress())
                && StringUtils.isBlank(searchCriteria.getUsername())) {
            return new ArrayList<>();
        }

        if (!StringUtils.isBlank(searchCriteria.getEmailAddress())) {
            AggregationOperation emailMatch = Aggregation
                    .match(Criteria.where("email").is(searchCriteria.getEmailAddress()));
            ops.add(emailMatch);
        }

        if (!StringUtils.isBlank(searchCriteria.getUsername())) {
            AggregationOperation usernameMatch = Aggregation
                    .match(Criteria.where("username").is(searchCriteria.getUsername()));
            ops.add(usernameMatch);
        }

        Aggregation aggregation = Aggregation.newAggregation(ops);
        List<User> users = mongoTemplate.aggregate(aggregation, mongoTemplate.getCollectionName(User.class), User.class)
                .getMappedResults();

        return users;
    }
} 
