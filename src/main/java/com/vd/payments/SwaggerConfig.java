package com.vd.payments;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@OpenAPIDefinition
(
        info=@Info(title="Payments API")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class SwaggerConfig implements WebMvcConfigurer
{

    @Bean
    public Docket api()
    {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(getApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.vd.payments.CONTROLLERS"))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(List.of(apiKey()));
//                .host("http://localhost:4000/");
//                .tags( new Tag(LOGIN_TAG,"SET DE ENDPOINTS PARA LOGEARSE"));

    }

    @Bean
    public GroupedOpenApi defaul() {
        return GroupedOpenApi.builder()
                .group("DEFAULT")
                .displayName("DEFAULT")
                .pathsToMatch("/**")
                .build();
    }
    @Bean
    public GroupedOpenApi config() {
        return GroupedOpenApi.builder()
                .group("X-CONFIG")
                .displayName("X-CONFIG")
                .pathsToMatch("/config/**")
                .build();
    }
    @Bean
    public GroupedOpenApi upload() {
        return GroupedOpenApi.builder()
                .group("UPLOAD")
                .displayName("UPLOAD")
                .pathsToMatch("/upload/**")
                .build();
    }
    @Bean
    public GroupedOpenApi empresa() {
        return GroupedOpenApi.builder()
                .group("EMPRESA")
                .displayName("EMPRESA")
                .pathsToMatch("/empresa/**" , "/suscripcion/**" , "/pago/**" , "/producto/**")
                .build();
    }
    @Bean
    public GroupedOpenApi facturas() {
        return GroupedOpenApi.builder()
                .group("FACTURAS")
                .displayName("FACTURAS")
                .pathsToMatch("/factura/**","/estados/**")
                .build();
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("LOGIN")
                .displayName("LOGIN")
                .pathsToMatch("/rol/**","/operador/**")
                .build();
    }
//    @Bean
//    public GroupedOpenApi invoices() {
//        return GroupedOpenApi.builder()
//                .group("INVOICES")
//                .displayName("INVOICES")
//                .pathsToMatch("/factura/**","/invoice/**","/suscripcion/**")
//                .build();
//    }

    private ApiKey apiKey()
    {
        return new ApiKey("JWT", "Authorization", "header");
    }
    private SecurityContext securityContext()
    {
        return SecurityContext.builder().securityReferences(defaultAuth()).build();
    }

    private List<SecurityReference> defaultAuth()
    {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return List.of(new SecurityReference("JWT", authorizationScopes));
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {

        registry
                .addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry
                .addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }


    private ApiInfo getApiInfo()
    {
        return new ApiInfo(
                "PAYMENTS API",
                "PAYMENTS API",
                "1.0",
                "Terms of service",
                new Contact("Nicolas Grossi", "www.viewdevs.com.ar", "viewdevscompany@gmail.com"),
                "License of API",
                "API license URL",
                Collections.emptyList());
    }
}