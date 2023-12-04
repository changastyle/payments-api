package com.vd.payments.EXCEPTIONS;

public class NotHeaderExc extends RuntimeException
{
    public String message;

    public NotHeaderExc(String message)
    {
        super(message);
        this.message = message;
    }
}
