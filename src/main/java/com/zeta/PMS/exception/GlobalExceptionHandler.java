package com.zeta.PMS.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserCreationException.class) public ResponseEntity<String> handleUserCreation(UserCreationException userCreationException){
        return new ResponseEntity<>(userCreationException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PaymentCreationException.class) public ResponseEntity<String> handlePaymentCreation(PaymentCreationException paymentCreationException){
        return new ResponseEntity<>(paymentCreationException.getMessage(),HttpStatus.BAD_REQUEST);
    }
}


