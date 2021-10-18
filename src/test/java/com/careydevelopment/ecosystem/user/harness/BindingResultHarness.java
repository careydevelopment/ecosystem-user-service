package com.careydevelopment.ecosystem.user.harness;

import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

public class BindingResultHarness {

    public static final String ERROR_FIELD = "field";
    public static final String ERROR_MESSAGE = "error message";
    
    public static BindingResult getBindingResultWithErrors(Object target, String objectName) {
        BindingResult bindingResult = new BeanPropertyBindingResult(target, objectName);
        
        ObjectError objectError = new ObjectError(ERROR_FIELD, ERROR_MESSAGE);
        bindingResult.addError(objectError);
        
        return bindingResult;
    }
}
