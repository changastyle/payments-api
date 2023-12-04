package com.vd.payments;

import com.vd.payments.EXCEPTIONS.NoInstalacionExec;
import com.vd.payments.EXCEPTIONS.NotFoundExc;
import com.vd.payments.EXCEPTIONS.NotHeaderExc;
import com.vd.payments.EXCEPTIONS.NoLogeadoExec;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class Advice extends ResponseEntityExceptionHandler
{
    @ExceptionHandler(value = NotFoundExc.class)
    public ResponseEntity<Object> notFound(NotFoundExc exception)
    {
        return new ResponseEntity<>(exception.message , HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(value = NoLogeadoExec.class)
    public ResponseEntity<Object> notLogged(NoLogeadoExec exception)
    {
        return new ResponseEntity<>(exception.message , HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(value = NotHeaderExc.class)
    public ResponseEntity<Object> noHeaders(NotHeaderExc exception)
    {
        return new ResponseEntity<>(exception.message , HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(value = NoInstalacionExec.class)
    public ResponseEntity<Object> noInstalacion(NoInstalacionExec exception)
    {
        return new ResponseEntity<>(exception.message , HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request)
    {
        Map<String,String> arrErrors = new HashMap<>();

        if(ex != null)
        {
            if(ex.getBindingResult() != null)
            {
                ex.getBindingResult().getFieldErrors().forEach(error ->
                {
                    arrErrors.put(error.getField() , error.getDefaultMessage());
                });
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(arrErrors);
    }
}
