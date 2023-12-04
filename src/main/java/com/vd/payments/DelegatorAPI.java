package com.vd.payments;

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

@SpringBootApplication
@EnableJpaRepositories("com.vd.payments.REPO")
@EnableWebMvc
@RestController
@OpenAPIDefinition(info = @Info(title = "DELEGATOR API", version = "1.0", description = "API for DELEGATOR - APP"))
public class DelegatorAPI
{

    public static Environment environment;
    public static ApplicationContext appContext;
    public static boolean MODO_DEV;


    public static void main(String[] args)
    {
        SpringApplication.run(DelegatorAPI.class, args);

        String puertoServer = environment.getProperty("server.port");
        MODO_DEV = Boolean.parseBoolean(String.valueOf(environment.getProperty("MODO-DEV")));

        System.out.println("PAYMENTS API - CORRIENDO EN http://localhost:" + puertoServer);
        System.out.println("PAYMENTS API - MODO-DEV: " + MODO_DEV );


        boolean SQL_INIT = Boolean.parseBoolean(DelegatorAPI.environment.getProperty("spring.datasource.sqlInit"));
        String DATA_SOURCE = String.valueOf(DelegatorAPI.environment.getProperty("spring.datasource.url"));
        String DATA_SOURCE_USER = String.valueOf(DelegatorAPI.environment.getProperty("spring.datasource.username"));
        String DATA_SOURCE_PASS = String.valueOf(DelegatorAPI.environment.getProperty("spring.datasource.password"));

        System.out.println("SQL INIT:" + SQL_INIT);
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
        DelegatorAPI.environment = environment;
    }
    private static String dameVariableEntorno(String nombreVariable)
    {
        return environment.getProperty(nombreVariable);
    }

}
