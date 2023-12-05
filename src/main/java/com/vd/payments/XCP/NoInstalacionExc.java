package com.vd.payments.XCP;

public class NoInstalacionExc extends RuntimeException
{
    public String message;

    public NoInstalacionExc(String message)
    {
        super(message);
        this.message = message;
    }
}
