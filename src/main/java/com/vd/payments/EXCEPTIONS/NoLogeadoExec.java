package com.vd.payments.EXCEPTIONS;

public class NoLogeadoExec extends RuntimeException
{

    public String message;

    public NoLogeadoExec(String message)
    {
        super(message);
        this.message = message;
    }
}
