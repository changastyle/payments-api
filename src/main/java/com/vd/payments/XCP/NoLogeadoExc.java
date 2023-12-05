package com.vd.payments.XCP;

public class NoLogeadoExc extends RuntimeException
{

    public String message;

    public NoLogeadoExc(String message)
    {
        super(message);
        this.message = message;
    }
}
