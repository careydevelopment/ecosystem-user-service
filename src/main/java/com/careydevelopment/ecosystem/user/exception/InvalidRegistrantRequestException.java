package com.careydevelopment.ecosystem.user.exception;

import java.util.List;

import us.careydevelopment.util.api.model.ValidationError;

public class InvalidRegistrantRequestException extends ServiceException {

    private static final long serialVersionUID = 2395556208074390293L;

    private List<ValidationError> errors;
    
    public InvalidRegistrantRequestException(List<ValidationError> errors) {
        super("Registrant validation failed!");
        this.errors = errors;
    }
    
    public List<ValidationError> getErrors() {
        return errors;
    }
}
