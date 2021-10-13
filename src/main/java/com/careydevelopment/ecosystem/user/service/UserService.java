package com.careydevelopment.ecosystem.user.service;

import java.util.ArrayList;
import java.util.List;

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

import com.careydevelopment.ecosystem.user.model.User;
import com.careydevelopment.ecosystem.user.model.UserSearchCriteria;
import com.careydevelopment.ecosystem.user.repository.UserRepository;
import com.careydevelopment.ecosystem.user.util.SecurityUtil;

import us.careydevelopment.ecosystem.jwt.service.JwtUserDetailsService;

@Service
public class UserService extends JwtUserDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private MongoTemplate mongoTemplate;

    public UserService(@Autowired UserRepository userRepository) {
        this.userDetailsRepository = userRepository;
    }

    public User updateUser(User user) {
        addExcludedFields(user);
        User updatedUser = ((UserRepository) userDetailsRepository).save(user);

        return updatedUser;
    }

    private void addExcludedFields(User user) {
        User currentUser = securityUtil.getCurrentUser();

        user.setPassword(currentUser.getPassword());
        user.setAuthorityNames(currentUser.getAuthorityNames());
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

            ((UserRepository) userDetailsRepository).save(user);
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
            ((UserRepository) userDetailsRepository).save(user);
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
