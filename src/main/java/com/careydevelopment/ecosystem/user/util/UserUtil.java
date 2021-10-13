package com.careydevelopment.ecosystem.user.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.careydevelopment.ecosystem.user.model.Registrant;
import com.careydevelopment.ecosystem.user.model.User;

@Component
public class UserUtil {

    @Autowired
    private PasswordEncoder encoder;

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
}
