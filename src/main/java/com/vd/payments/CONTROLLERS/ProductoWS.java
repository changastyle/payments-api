package com.vd.payments.CONTROLLERS;

import com.vd.payments.MODELO.*;
import com.vd.payments.REPO.DocREPO;
import com.vd.payments.REPO.ProductoREPO;
import com.vd.payments.XCP.NotFoundExc;
import com.vd.payments.XDTO.ProductoSDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "producto")
@CrossOrigin
public class ProductoWS
{
    @Autowired
    private ProductoREPO productoREPO;
    @Autowired
    private DocREPO docREPO;

    @GetMapping(value = "/")
    @Operation(
            summary = "Return all productos from Logged user according to his Instalacion",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public List<Producto> findAll
    (
            @RequestHeader HttpHeaders headers
    )
    {
        List<Producto> arrSuscripciones = new ArrayList<>();

        int fkInstalacion = LoginWS.getFKInstalacionOperadorLogeado(headers);

        if(fkInstalacion != -1)
        {
            arrSuscripciones = productoREPO.findAllByInstalacion(fkInstalacion);
        }
        else
        {
            throw new NotFoundExc("Instalacion NOT FOUND: " + fkInstalacion);
        }

        return  arrSuscripciones;
    }
//    @GetMapping(value = "/{fkempresa}")
//    @Operation(
//            summary = "Retrieve suscripcions FROM fkEmpresa AND loged user",
//            security = @SecurityRequirement(name = "bearerAuth")
//    )
//    public List<Suscripcion> findAllByFKEmpresa
//    (
//        @RequestHeader HttpHeaders headers,
//        @PathVariable() int fkempresa
//    )
//    {
//        List<Suscripcion> arrSuscripcions = null;
//
//        int fkInstalacion = LoginWS.getFKInstalacionOperadorLogeado(headers);
//
//        if(fkInstalacion != -1)
//        {
//            arrSuscripcions = suscripcionREPO.findByFKEmpresa(fkempresa, fkInstalacion);
//        }
//        else
//        {
//            throw new NotFoundExc("Instalacion NOT FOUND: " + fkInstalacion);
//        }
//
//        return arrSuscripcions;
//    }
//    @GetMapping(value = "/id/{id}")
//    @Operation(
//            summary = "Retrieve information of specific suscripcion FROM loged user",
//            security = @SecurityRequirement(name = "bearerAuth")
//    )
//    public Suscripcion getByID
//    (
//        @RequestHeader HttpHeaders headers,
//        @PathVariable() int id
//    )
//    {
//        Suscripcion suscripcionDB = null;
//
//        int fkInstalacionOperadorLogeado = LoginWS.getFKInstalacionOperadorLogeado(headers);
//
//        if(fkInstalacionOperadorLogeado != -1)
//        {
//
//            int fkInstalacion = LoginWS.getFKInstalacionOperadorLogeado(headers);
//
//            if(fkInstalacion != -1)
//            {
//                suscripcionDB = suscripcionREPO.getByIDN(id, fkInstalacion);
//            }
//            else
//            {
//                throw new NotFoundExc("Instalacion NOT FOUND: " + fkInstalacion);
//            }
//
//            return  suscripcionDB;
//        }
//        else
//        {
//            throw  new NoLogeadoExc("USUARIO NO LOGEADO");
//        }
//
//    }
//
//
    @PostMapping(value = "/")
    public Producto createNew
    (
        @RequestHeader HttpHeaders headers,
        @RequestBody ProductoSDTO productoSDTO
    )
    {

        Producto productoNEW = null;

        Instalacion instalacionDB = LoginWS.getInstalacionOperadorLogeado(headers);

        if(instalacionDB != null)
        {
            int fkInstalacion = instalacionDB.getId();

            if (fkInstalacion != -1)
            {
                productoNEW = (Producto) productoSDTO.toEntity(Producto.class);
                productoNEW.setId( -1 );
                productoNEW.setInstalacion(instalacionDB);
                productoNEW.setActivo(true);

                int fkFoto = productoSDTO.fkFotoNotMandatory;

                boolean cargueFoto = false;
                if(fkFoto > 0)
                {
                    Documento fotoDB = docREPO.getByIDN(fkFoto);

                    if(fotoDB != null)
                    {
                        productoNEW.setFoto( fotoDB );
                        cargueFoto = true;
                    }
                }

                if(!cargueFoto)
                {
                    Documento fotoDefault = DocWS.porDefecto(headers);

                    if(fotoDefault != null)
                    {
                        productoNEW.setFoto(fotoDefault);
                    }
                }

                productoNEW = productoREPO.save(productoNEW);
            }
            else
            {
                throw new NotFoundExc("INSTALACION NOT FOUND : " + fkInstalacion);
            }
        }
        else
        {
                throw new NotFoundExc("INSTALACION NOT FOUND");
        }



        return productoNEW;
    }
}
