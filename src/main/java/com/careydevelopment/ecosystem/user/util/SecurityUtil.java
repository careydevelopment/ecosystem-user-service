package com.careydevelopment.ecosystem.user.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.careydevelopment.ecosystem.user.model.User;

import us.careydevelopment.ecosystem.jwt.constants.Authority;

@Component
public class SecurityUtil {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityUtil.class);

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        return user;
    }

    public boolean isAuthorizedByUserId(String userId) {
        boolean authorized = false;

        User user = getCurrentUser();

        if (user != null && userId != null) {
            if (user.getId() != null) {
                if (user.getId().equals(userId)) {
                    authorized = true;
                } else {
                    //if the user is an admin, can do anything
                    if (user.getAuthorityNames() != null && user.getAuthorityNames().contains(Authority.ADMIN_ECOSYSTEM_USER)) {
                        authorized = true;
                    }
                }
            }
        }

        return authorized;
    }
}
