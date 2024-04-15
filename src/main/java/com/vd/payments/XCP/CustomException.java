package com.vd.payments.XCP;

public class CustomException extends RuntimeException
{
    public String message;

    public CustomException(String message)
    {
        super(message);
        this.message = message;
    }
}
