package com.vd.payments.UTIL;

import com.vd.payments.PaymentsAPI;
import com.vd.payments.UTIL.serializer.MasterUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.sql.PreparedStatement;

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

            int lineasAfectadas = executeSQL(lineaLoop);
            System.out.println("LINEAS AFECTADAS: " + lineasAfectadas);

            contador++;
        }
    }
    public static int executeSQL(String sql)
    {
        int filasAfectadas = 0;
        Connection connection = null;
        try
        {
            // Establece la conexión a la base de datos H2 (cambia la URL, usuario y contraseña según tus configuraciones)
            String USER = PaymentsAPI.dameVariableEntorno("MYSQL_USER");
            String PW = PaymentsAPI.dameVariableEntorno("MYSQL_PW");
            System.out.println("USER : " + USER);
            System.out.println("PW : " + PW);
            connection = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "password");
            // Establece la conexión a la base de datos MySQL (cambia la URL, usuario y contraseña según tus configuraciones)
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/tu_basedatos", "tu_usuario", "tu_contraseña");


            // Define la consulta SQL que deseas ejecutar
//            String sqlQuery = "INSERT INTO tu_tabla (columna1, columna2) VALUES (?, ?)";

            // Prepara la declaración SQL con parámetros
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql))
            {
                // Establece valores para los parámetros (si es necesario)
//                preparedStatement.setString(1, "valor_para_columna1");
//                preparedStatement.setString(2, "valor_para_columna2");

                // Ejecuta la consulta
                filasAfectadas = preparedStatement.executeUpdate();

                // Verifica el número de filas afectadas (opcional)
                System.out.println("Filas afectadas: " + filasAfectadas);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            // Cierra la conexión al finalizar
            try
            {
                if (connection != null)
                {
                    connection.close();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        return filasAfectadas;
    }
}
