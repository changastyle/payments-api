package com.vd.payments.CONTROLLERS;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vd.payments.XDTO.FacturaSaveDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
//import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.http.HttpHeaders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ActiveProfiles("test")
class FacturaWSTest
{


    //    @Autowired
//    private WebTestClient client;
    private static String tokenJWT;

    @BeforeEach
    void setUp()
    {
        tokenJWT = getJWT();
        System.out.println("MI TOKEN ES: " + tokenJWT);
    }

    String getJWT()
    {
        String email = "lupita@gmail.com";
        String pass = "lupita";
        String jwt = LoginWS.logearse(email, pass);
        System.out.println("RESULT: " + jwt);


        return jwt;
    }

    // Crear los headers para la autenticación con el JWT, recibiendo el JWT como parámetro
    private HttpHeaders createHeaders(String tokenJWT)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + tokenJWT);
        return headers;
    }

    @Test
//    @Order(1)
    void createTest()
    {
        FacturaSaveDTO facturaSaveDTO = new FacturaSaveDTO();

        // 1 - POPULATE FACTURA SAVE DTO:
        facturaSaveDTO.mesFactura = 4;
        facturaSaveDTO.yearFactura = 2024;
        facturaSaveDTO.fkEmpresa = 1;

        // Crear los headers con el token JWT, pasándolo como parámetro
        HttpHeaders headers = createHeaders(tokenJWT);

        FacturaWS facturaWS = new FacturaWS();
        facturaWS.create(facturaSaveDTO, headers);
//        client.post().uri("/factura/")
//                .contentType(MediaType.APPLICATION_JSON)
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenJWT)
//                .bodyValue(facturaSaveDTO)
//                .exchange()
//                .expectStatus().isOk()
//                .expectHeader().contentType(MediaType.APPLICATION_JSON)
//                .expectBody();
    }

}