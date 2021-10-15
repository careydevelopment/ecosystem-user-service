package com.careydevelopment.ecosystem.user.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.careydevelopment.ecosystem.user.exception.InvalidRequestException;
import com.careydevelopment.ecosystem.user.exception.ServiceException;
import com.careydevelopment.ecosystem.user.exception.UnauthorizedUpdateException;

import us.careydevelopment.util.api.model.IRestResponse;
import us.careydevelopment.util.api.model.ValidationError;
import us.careydevelopment.util.api.response.ResponseEntityUtil;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<IRestResponse<Void>> serviceException(ServiceException se) {
        return ResponseEntityUtil.createResponseEntityWithError(se.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
    
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<IRestResponse<List<ValidationError>>> invalidRegistrant(
            InvalidRequestException ex) {
        List<ValidationError> errors = ex.getErrors();
        return ResponseEntityUtil.createResponseEntityWithValidationErrors(errors);
    }
    
    @ExceptionHandler(UnauthorizedUpdateException.class)
    public ResponseEntity<IRestResponse<Void>> unauthorizedUpdate(
            UnauthorizedUpdateException ex) {
        return ResponseEntityUtil.createResponseEntityWithUnauthorized(ex.getMessage());
    }
}
