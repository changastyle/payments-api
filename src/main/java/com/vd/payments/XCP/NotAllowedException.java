package com.vd.payments.XCP;

public class NotAllowedException extends RuntimeException
{
    public String message;

    public NotAllowedException(String message)
    {
        super(message);
        this.message = message;
    }
}
