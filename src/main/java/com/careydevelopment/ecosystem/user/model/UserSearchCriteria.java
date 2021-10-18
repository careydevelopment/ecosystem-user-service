package com.careydevelopment.ecosystem.user.model;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class UserSearchCriteria {

    private String emailAddress;
    private String username;

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

}
