package com.vd.payments.EXCEPTIONS;

// Define una excepción personalizada para indicar la falta de configuraciones
//@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundExc extends RuntimeException
{
    public String message;

    public NotFoundExc(String message)
    {
        super(message);
        this.message = message;
    }
}