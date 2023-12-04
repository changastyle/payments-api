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



}
