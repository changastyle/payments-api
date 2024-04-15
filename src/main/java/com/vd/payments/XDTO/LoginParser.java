package com.vd.payments.XDTO;


import com.vd.payments.PaymentsAPI;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginParser
{
    private String id;
    private String email;
    private long exp;

    public Date getFechaExp()
    {
        if (PaymentsAPI.DEBUG)
        {
            System.out.println("|---------------------------|");
            System.out.println("EXP RECIBIDA: " + exp);
        }

        long millis = exp * 1000;

        Date dateOfExpiration = new Date(millis);
        if (PaymentsAPI.DEBUG)
        {
            System.out.println("MILLIS: " + millis);
            System.out.println("DATE RECIBIDA: " + dateOfExpiration);
            System.out.println("|---------------------------|");
        }
        return dateOfExpiration;
    }
}
