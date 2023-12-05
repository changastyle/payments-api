package com.vd.payments.XCP;

public class NotHeaderExc extends RuntimeException
{
    public String message;

    public NotHeaderExc(String message)
    {
        super(message);
        this.message = message;
    }
}
