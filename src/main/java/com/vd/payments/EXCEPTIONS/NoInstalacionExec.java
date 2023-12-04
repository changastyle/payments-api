package com.vd.payments.EXCEPTIONS;

public class NoInstalacionExec extends RuntimeException
{
    public String message;

    public NoInstalacionExec(String message)
    {
        super(message);
        this.message = message;
    }
}
