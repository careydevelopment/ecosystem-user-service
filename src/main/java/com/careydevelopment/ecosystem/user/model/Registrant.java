package com.careydevelopment.ecosystem.user.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Registrant {

    @NotNull
    @Size(min=1, max=32, message="First name must be between 1 and 32 characters")
    private String firstName;
    
    @NotNull
    @Size(min=1, max=32, message="Last name must be between 1 and 32 characters")
    private String lastName;
    
    @NotNull
    @Size(min=5, max=12, message="Username must be between 5 and 12 characters")
    private String username;
    
    @NotNull
    @Size(min=8, max=20, message="Password must be between 8 and 20 characters")
    private String password;
    
    @NotNull
    @Pattern(regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", message="Email address is invalid")
    private String emailAddress;

    @NotNull
    private String phone;

    @JsonProperty("g-recaptcha-response")
    private String recaptchaResponse;
    
    
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getRecaptchaResponse() {
        return recaptchaResponse;
    }

    public void setRecaptchaResponse(String recaptchaResponse) {
        this.recaptchaResponse = recaptchaResponse;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
