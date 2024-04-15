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
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.http.HttpHeaders;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class FacturaWSTest
{


    @Autowired
    private WebTestClient client;
    private static String tokenJWT;

    @BeforeEach
    void setUp() {
        tokenJWT = getJWT();
        System.out.println("MI TOKEN ES: "+ tokenJWT);
    }

    String getJWT()
    {
        String email = "nico.grossi4@gmail.com";
        String pass = "lupita";
        String jwt = LoginWS.logearse(email,pass);
//
//        EntityExchangeResult<byte[]> result = client.get().uri("operador/dameOperadorLogeado")
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
////                .expectStatus().isOk()
////                .expectHeader().contentType(MediaType.APPLICATION_JSON)
//                .expectBody()
//                .returnResult();
//
//        String strResult = result.toString();
//
        System.out.println("RESULT: " + jwt);


        return  jwt;
    }
    @Test
//    @Order(1)
    void createTest()
    {
        FacturaSaveDTO facturaSaveDTO = new FacturaSaveDTO();

        // 1 - POPULATE FACTURA SAVE DTO:
        facturaSaveDTO.mesFactura = 4;
        facturaSaveDTO.yearFactura = 2024;
        facturaSaveDTO.fkProducto = 5;
        facturaSaveDTO.fkEmpresa = 1;
        facturaSaveDTO.cantidad = 2;
        facturaSaveDTO.precio = -1;

        client.post().uri("/factura/")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenJWT)
                .bodyValue(facturaSaveDTO)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody();
    }

}