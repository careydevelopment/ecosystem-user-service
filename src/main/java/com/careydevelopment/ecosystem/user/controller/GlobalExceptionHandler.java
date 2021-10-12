package com.careydevelopment.ecosystem.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.careydevelopment.ecosystem.user.exception.ServiceException;

import us.careydevelopment.util.api.model.IRestResponse;
import us.careydevelopment.util.api.response.ResponseEntityUtil;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<IRestResponse<Void>> serviceException(ServiceException se) {
        return ResponseEntityUtil.createResponseEntityWithError(se.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
