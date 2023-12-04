package com.vd.payments.UTIL;

import com.vd.payments.UTIL.serializer.MasterUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

public class SQLInit
{
    public static Environment environment;
    public static ApplicationContext appContext;
    public static boolean MODO_DEV;
    public static String TIMEZONE;
    public static Long JWT_WINDOW;
    public static String SECRET_KEY_JWT;
    public static String DATA_SOURCE;
    public static String DATA_SOURCE_USER;
    public static String DATA_SOURCE_PASS;

    public static int runSQLFile(String dataSource, String user , String pass)
    {


        if(DATA_SOURCE == null)
        {
            DATA_SOURCE="jdbc:h2:mem:challenge;DB_CLOSE_ON_EXIT=FALSE";
            DATA_SOURCE_USER="sa";
            DATA_SOURCE_PASS="password";
        }

        String sqlFilePath = "src/main/resources/init.sql";

        System.out.println("IMPORTING SQL FILE: [" + sqlFilePath +"]");

        int rowsAffected = 0;
        try
        {
            List<String> arrSQL = MasterUtil.readFile(sqlFilePath, false);
            Connection connection = DriverManager.getConnection(DATA_SOURCE, DATA_SOURCE_USER, DATA_SOURCE_PASS);
            Statement statement = connection.createStatement();

            for(String sqlLoop : arrSQL)
            {
                rowsAffected += statement.executeUpdate(sqlLoop);
                System.out.println("EXECUTING SQL(" + rowsAffected +"): " + sqlLoop);
            }

            statement.close();
            connection.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return rowsAffected;
    }

}
