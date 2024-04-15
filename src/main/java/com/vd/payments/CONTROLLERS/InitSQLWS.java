package com.vd.payments.CONTROLLERS;

import com.vd.payments.PaymentsAPI;
import com.vd.payments.UTIL.serializer.MasterUtil;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping(value = "sql")
@CrossOrigin
public class InitSQLWS
{

    @GetMapping(value = "/init/{pass}")
    @Operation(summary = "Inicializa cosas comunes en SQL")
    public int init
    (
            @PathVariable() String pass
    )
    {
        int lineasAfectadasTotales = 0;
        if(pass.equalsIgnoreCase("lupita"))
        {
                String rutaSQLInit = "src/main/resources/INIT.SQL";
                System.out.println("LEYENDO ARCHIVO:");

                List<String> arrLineasSQL = MasterUtil.readLinesOfSQLFile(rutaSQLInit,true);
                System.out.println("ARR LINEAS: " + arrLineasSQL.size());

                int contador = 0;
                for(String lineaLoop : arrLineasSQL)
                {
                    System.out.println(contador + ")" + lineaLoop );

                    int lineasAfectadas = executeSQL(lineaLoop , true);
                    lineasAfectadasTotales += lineasAfectadas;
                    System.out.println("LINEAS AFECTADAS: " + lineasAfectadas);

                    contador++;
                }
        }

        return lineasAfectadasTotales;
    }
    private int executeSQL(String sql, boolean enH2)
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

            if(enH2)
            {
                connection = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "password");
            }
            else
            {
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/payments", USER, PW);

            }
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
