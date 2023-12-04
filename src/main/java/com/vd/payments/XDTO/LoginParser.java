package com.vd.payments.XDTO;


import lombok.Data;

import java.util.Date;

@Data
public class LoginParser
{
    private String id;
    private String email;
    private long exp;

    public Date getFechaExp()
    {
        System.out.println("|---------------------------|");
        System.out.println("EXP RECIBIDA: "+ exp);

        long millis = exp * 1000;

        Date dateOfExpiration = new Date(millis);
        System.out.println("MILLIS: "+ millis);
        System.out.println("DATE RECIBIDA: "+ dateOfExpiration);
        System.out.println("|---------------------------|");

        return dateOfExpiration;
    }
}
