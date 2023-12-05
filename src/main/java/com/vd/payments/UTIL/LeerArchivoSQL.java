package com.vd.payments.UTIL;

import com.vd.payments.UTIL.serializer.MasterUtil;

import java.util.List;

public class LeerArchivoSQL
{
    public static void main(String args[])
    {
        String rutaSQLInit = "src/main/resources/INIT.SQL";
        System.out.println("LEYENDO ARCHIVO:");

        List<String> arrLineasSQL = MasterUtil.readLinesOfSQLFile(rutaSQLInit,true);
        System.out.println("ARR LINEAS: " + arrLineasSQL.size());

        int contador = 0;
        for(String lineaLoop : arrLineasSQL)
        {
            System.out.println(contador + ")" + lineaLoop );
            contador++;
        }
    }
}
