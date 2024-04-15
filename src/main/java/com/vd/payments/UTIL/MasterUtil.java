package com.vd.payments.UTIL.serializer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MasterUtil
{
    public static List<String> readLinesOfSQLFile(String ruta, boolean verbose)
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
                if (line.contains(";"))
                {
                    acumulador.append(line);
                    arrSQLs.add(acumulador.toString());
                    acumulador = new StringBuffer();
                }
                else
                {
                    acumulador.append(line);
                }


                if (verbose)
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
                String timeStr = dateRecibed.substring(posLastSlash + 1);
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

                ldt = LocalDateTime.of(year, mes, dia, hour, min);

            }
        }
        catch (Exception e)
        {
            ldt = LocalDateTime.now();
            e.printStackTrace();
        }


        return ldt;
    }

    public static String formatearLDTForFiles(LocalDateTime ldt)
    {
        String rta = "";
        if (ldt != null)
        {
            int year = ldt.getYear();
            int mes = ldt.getMonthValue();
            int dia = ldt.getDayOfMonth();
            int hours = ldt.getHour();
            int minutes = ldt.getMinute();
            int seconds = ldt.getSecond();

            String strYear = MasterUtil.checkZeros(year);
            String strMes = MasterUtil.checkZeros(mes);
            String strDias = MasterUtil.checkZeros(dia);
            String strHora = MasterUtil.checkZeros(hours);
            String strMin = MasterUtil.checkZeros(minutes);
            String strSec = MasterUtil.checkZeros(seconds);

            rta = strYear + "_" + strMes + "_" + strDias + "_" + strHora + "_" + strMin + "_" + strSec;
        }

        return rta;
    }

    public static String formatearFechaBonita(LocalDateTime ldt, boolean incluirHora, boolean incluirSegs)
    {
        String rta = "";
        if (ldt != null)
        {
            int year = ldt.getYear();
            int mes = ldt.getMonthValue();
            int dia = ldt.getDayOfMonth();
            int hours = ldt.getHour();
            int minutes = ldt.getMinute();
            int seconds = ldt.getSecond();

            String strYear = MasterUtil.checkZeros(year);
            String strMes = MasterUtil.checkZeros(mes);
            String strDias = MasterUtil.checkZeros(dia);
            String strHora = MasterUtil.checkZeros(hours);
            String strMin = MasterUtil.checkZeros(minutes);
            String strSec = MasterUtil.checkZeros(seconds);

            rta = strDias + "/" + strMes + "/" + strYear;
            if (incluirHora)
            {
                rta += " (" + strHora + ":" + strMin + ")";
            }
            if (incluirSegs)
            {
                rta += strSec;
            }
//            rta =  strYear +"_" + strMes + "_" + strDias + "_" + strHora + "_" + strMin + "_" + strSec;
        }

        return rta;
    }

    public static String formatearOnlyFechaOrHoraBonita(LocalDateTime ldt, boolean soloHora)
    {
        String rta = "";
        if (ldt != null)
        {
            int year = ldt.getYear();
            int mes = ldt.getMonthValue();
            int dia = ldt.getDayOfMonth();
            int hours = ldt.getHour();
            int minutes = ldt.getMinute();
            int seconds = ldt.getSecond();

            String strYear = MasterUtil.checkZeros(year);
            String strMes = MasterUtil.checkZeros(mes);
            String strDias = MasterUtil.checkZeros(dia);
            String strHora = MasterUtil.checkZeros(hours);
            String strMin = MasterUtil.checkZeros(minutes);
            String strSec = MasterUtil.checkZeros(seconds);

            if (soloHora)
            {
                rta = "(" + strHora + ":" + strMin + ")";
            }
            else
            {
                rta = strDias + "/" + strMes + "/" + strYear;
            }
        }

        return rta;
    }

    public static String generarCodigoAleatorio(int cantDigitos)
    {
        String rta = "";

        for (int i = 0; i < cantDigitos; i++)
        {
            int random = (int) (Math.random() * 10);

            rta += String.valueOf(random);
        }

        return rta;
    }

    public static boolean verifyFolderExist(String rutaCarpetaEnDisco)
    {
        boolean exist = false;

        File carpetaDondeLoGuardo = new File(rutaCarpetaEnDisco);

        if (!carpetaDondeLoGuardo.exists())
        {
            carpetaDondeLoGuardo.mkdir();
        }
        return exist;
    }

    public static String checkZeros(int numero)
    {
        String rta = String.valueOf(numero);

        if (numero < 10)
        {
            rta = "0" + numero;
        }

        return rta;
    }

    public static boolean isNumeric(String str)
    {
        try
        {
            Double.parseDouble(str);
            return true;
        }
        catch (NumberFormatException e)
        {
            return false;
        }
    }

    public static String separatePipeByPos(String strRaw, int posDeseada)
    {
        String rta = String.valueOf(strRaw);

        List<String> arrSplited = Arrays.asList(strRaw.split("\\|"));

        if (arrSplited != null)
        {
            if (arrSplited.size() >= posDeseada)
            {
                rta = arrSplited.get(posDeseada);
            }
        }

        return rta;
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

    public static String leerArgumentosPrograma(String[] args)
    {
        String rta = "";
        if (args.length > 0)
        {
            String sqlInit = args[0];
            if (sqlInit != null)
            {
                if (sqlInit.length() > 0)
                {
                    rta = sqlInit;
                }
            }
        }
        else
        {
            rta = "";
        }

        return rta;
    }

    public static boolean leerArgumentosProgramaAsBoolean(String[] args)
    {
        boolean rta = Boolean.parseBoolean(leerArgumentosPrograma(args));

        return rta;
    }

    public static boolean calcularSiArchivoEsFoto(String nuevoNombreFullFS)
    {
        boolean rta = nuevoNombreFullFS.endsWith(".jpg") || nuevoNombreFullFS.endsWith(".jpeg") || nuevoNombreFullFS.endsWith(".png") || nuevoNombreFullFS.endsWith(".gif");

        return rta;
    }

    public static String pMayus(String str)
    {
        String rta = str;
        if (str != null)
        {
            if (str.length() > 0)
            {
                String primera = String.valueOf(str.charAt(0));
                String resto = str.substring(1);

                rta = primera.toUpperCase() + resto;
            }
        }
        return rta;
    }
}