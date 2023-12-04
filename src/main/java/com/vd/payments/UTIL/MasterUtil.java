package com.vd.payments.UTIL.serializer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MasterUtil
{
    public static List<String> readFile(String ruta, boolean verbose)
    {
        List<String> arrSQLs = new ArrayList<>();
        StringBuffer acumulador = new StringBuffer();

        try
        {
            FileReader fileReader = new FileReader(ruta);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                if (line.equalsIgnoreCase("||||"))
                {
                    arrSQLs.add(acumulador.toString());
                    acumulador = new StringBuffer();
                }
                else
                {
                    acumulador.append(line);
                }



                if(verbose)
                {
                    System.out.println(line);
                }
            }

            bufferedReader.close();
            fileReader.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return arrSQLs;
    }

    public static LocalDateTime parseoFecha(String dateRecibed)
    {
        LocalDateTime ldt = null;

        try
        {
            int posLastSlash = dateRecibed.lastIndexOf("-");

            int year = 0;
            if (posLastSlash > -1)
            {
                String dateStr = dateRecibed.substring(0, posLastSlash);
                String timeStr = dateRecibed.substring(posLastSlash + 1, dateRecibed.length());
                List<String> partesFecha = Arrays.asList(dateStr.split("-"));
                List<String> partesTime = Arrays.asList(timeStr.split("\\."));

                year = Integer.valueOf(partesFecha.get(0));
                int mes = Integer.valueOf(partesFecha.get(1));
                int dia = Integer.valueOf(partesFecha.get(2));
                int hour = Integer.valueOf(partesTime.get(0));
                int min = Integer.valueOf(partesTime.get(1));

//            System.out.println("YEAR:" + year);
//            System.out.println("mes:" + mes);
//            System.out.println("dia:" + dia);
//            System.out.println("hour:" + hour);
//            System.out.println("min:" + min);

                ldt = LocalDateTime.of(year,mes,dia,hour, min);

            }
        }
        catch (Exception e)
        {
            ldt = LocalDateTime.now();
            e.printStackTrace();
        }


        return ldt;
    }
    public static int saberMiTimeZone()
    {
        // Specify the time zone for your location
        ZoneId localZone = ZoneId.of("Europe/Madrid"); // Replace with your local timezone

        // Create a ZonedDateTime for the current time in your local timezone
        ZonedDateTime localTime = ZonedDateTime.now(localZone);

        // Create a ZonedDateTime for the current time in GMT (UTC)
        ZonedDateTime gmtTime = ZonedDateTime.now(ZoneId.of("GMT"));

        // Calculate the time difference in hours
        long hoursDifference = localTime.getOffset().getTotalSeconds() / 3600;

        System.out.println("Your local time zone is " + localZone);
        System.out.println("Current local time is: " + localTime);
        System.out.println("GMT (UTC) time is: " + gmtTime);
        System.out.println("You are " + hoursDifference + " hours from GMT.");

        return Integer.parseInt(String.valueOf(hoursDifference));
    }
}