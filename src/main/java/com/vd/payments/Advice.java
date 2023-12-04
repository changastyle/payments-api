package com.vd.payments;

import com.vd.payments.EXCEPTIONS.NotFoundExc;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class Advice
{
    @ExceptionHandler(value = NotFoundExc.class)
    public ResponseEntity<Object> configNotFound(NotFoundExc exception)
    {

        return new ResponseEntity<>(exception.message , HttpStatus.NOT_FOUND);
    }
}
