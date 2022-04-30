package com.partyluck.party_luck.exception;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice
@RestController
public class MyExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse processValidationError(MethodArgumentNotValidException exception) {
        BindingResult bindingResult=exception.getBindingResult();
        String[] errors=new String[bindingResult.getFieldErrors().size()];
        int i=0;
        for(FieldError fieldError:bindingResult.getFieldErrors()) {
            errors[i] = fieldError.getDefaultMessage();
            i++;
        }
        ErrorResponse errorResponse=new ErrorResponse();
        errorResponse.setHttp(200);
        errorResponse.setStatus(false);
        errorResponse.setMsg(errors);
        return errorResponse;
    }
}
