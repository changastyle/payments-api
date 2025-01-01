package com.vd.payments;

import com.vd.payments.UTIL.serializer.MasterUtil;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.RedirectView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

@SpringBootApplication
@EnableJpaRepositories("com.vd.payments.REPO")
@EnableWebMvc
@RestController
@OpenAPIDefinition(info = @Info(title = "PAYMENTS API", version = "1.0", description = "API FOR PAYMENTS"))
public class PaymentsAPI
{

    public static Environment environment;
    public static ApplicationContext appContext;
    public static boolean MODO_DEV;
    public static boolean DEBUG;


    public static void main(String[] args)
    {
        SpringApplication.run(PaymentsAPI.class, args);

        String puertoServer = environment.getProperty("server.port");
        MODO_DEV = Boolean.parseBoolean(String.valueOf(environment.getProperty("MODO_DEV")));
        DEBUG = Boolean.parseBoolean(String.valueOf(environment.getProperty("DEBUG")));
        String DATA_SOURCE = String.valueOf(PaymentsAPI.environment.getProperty("spring.datasource.url"));

        System.out.println("PAYMENTS API - CORRIENDO EN http://localhost:" + puertoServer);
        System.out.println("PAYMENTS APP - CORRIENDO EN http://localhost/payments/out");
        System.out.println("PAYMENTS API - MODO-DEV: " + MODO_DEV);
        System.out.println("PAYMENTS API - DEBUG: " + DEBUG);
        System.out.println("PAYMENTS API - DATASOURCE : " + DATA_SOURCE);


//        boolean SQL_INIT = MasterUtil.leerArgumentosProgramaAsBoolean(args);
        boolean SQL_INIT = false;
        if (SQL_INIT)
        {

            String DATA_SOURCE_USER = String.valueOf(PaymentsAPI.environment.getProperty("spring.datasource.username"));
            String DATA_SOURCE_PASS = String.valueOf(PaymentsAPI.environment.getProperty("spring.datasource.password"));

            if (SQL_INIT)
            {
                System.out.println("SQL INIT:" + SQL_INIT);
                runInitSQLFile(DATA_SOURCE, DATA_SOURCE_USER, DATA_SOURCE_PASS);
            }
        }
    }


    @GetMapping(value = "/")
    public static RedirectView swagger()
    {
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("swagger-ui.html");

        return redirectView;
    }

    @Autowired
    public void setearEnvironment(Environment environment)
    {
        PaymentsAPI.environment = environment;
    }

    public static String dameVariableEntorno(String nombreVariable)
    {
        return environment.getProperty(nombreVariable);
    }


    public static int runInitSQLFile(String dataSource, String dataSourceUser, String dataSourcePass)
    {


        if (dataSource == null)
        {
            dataSource = "jdbc:h2:mem:challenge;DB_CLOSE_ON_EXIT=FALSE";
            dataSourceUser = "sa";
            dataSourcePass = "password";
        }

        String sqlFilePath = "src/main/resources/INIT.SQL";

        System.out.println("IMPORTING SQL FILE: [" + sqlFilePath + "]");

        int rowsAffected = 0;
        try
        {
            List<String> arrSQL = MasterUtil.readLinesOfSQLFile(sqlFilePath, false);
            Connection connection = DriverManager.getConnection(dataSource, dataSourceUser, dataSourcePass);
            Statement statement = connection.createStatement();

            for (String sqlLoop : arrSQL)
            {
                rowsAffected += statement.executeUpdate(sqlLoop);
                System.out.println("EXECUTING SQL(" + rowsAffected + "): " + sqlLoop);
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
