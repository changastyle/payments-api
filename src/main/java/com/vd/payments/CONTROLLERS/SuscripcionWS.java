package com.vd.payments.CONTROLLERS;

import com.vd.payments.MODELO.*;
import com.vd.payments.REPO.EmpresaRepo;
import com.vd.payments.REPO.ProductoREPO;
import com.vd.payments.REPO.SuscripcionREPO;
import com.vd.payments.XCP.NoLogeadoExc;
import com.vd.payments.XCP.NotFoundExc;
import com.vd.payments.XDTO.SuscriptionSaveDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "suscripcion")
@CrossOrigin
public class SuscripcionWS
{
    @Autowired
    private SuscripcionREPO suscripcionREPO;
    @Autowired
    private EmpresaRepo empresaRepo;
    @Autowired
    private ProductoREPO productoREPO;

    @GetMapping(value = "/")
    @Operation(
            summary = "Return all Suscripciones from Logged user",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public List<Suscripcion> findAll
    (
            @RequestHeader HttpHeaders headers
    )
    {
        List<Suscripcion> arrSuscripciones = new ArrayList<>();

        int fkInstalacion = LoginWS.getFKInstalacionOperadorLogeado(headers);

        if(fkInstalacion != -1)
        {
            arrSuscripciones = suscripcionREPO.findByInstalacion(fkInstalacion);
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
//            arrSuscripcions = suscripcionREPO.findByFKEmpresa(fkempresa);
//        }
//        else
//        {
//            throw new NotFoundExc("Instalacion NOT FOUND: " + fkInstalacion);
//        }
//
//        return arrSuscripcions;
//    }
    @GetMapping(value = "/id/{id}")
    @Operation(
            summary = "Retrieve information of specific suscripcion FROM loged user",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public Suscripcion getByID
    (
        @RequestHeader HttpHeaders headers,
        @PathVariable() int id
    )
    {
        Suscripcion suscripcionDB = null;

        int fkInstalacionOperadorLogeado = LoginWS.getFKInstalacionOperadorLogeado(headers);

        if(fkInstalacionOperadorLogeado != -1)
        {

            int fkInstalacion = LoginWS.getFKInstalacionOperadorLogeado(headers);

            if(fkInstalacion != -1)
            {
                suscripcionDB = suscripcionREPO.getByInstalacion(fkInstalacion);
            }
            else
            {
                throw new NotFoundExc("Instalacion NOT FOUND: " + fkInstalacion);
            }

            return  suscripcionDB;
        }
        else
        {
            throw  new NoLogeadoExc("USUARIO NO LOGEADO");
        }

    }


    @PostMapping(value = "/")
    public Suscripcion createNew
    (
        @RequestHeader HttpHeaders headers,
        @RequestBody SuscriptionSaveDTO suscriptionDTO
    )
    {

        Suscripcion suscripcion = null;

        Instalacion instalacionDB = LoginWS.getInstalacionOperadorLogeado(headers);
        if(instalacionDB != null)
        {
            int fkInstalacion = instalacionDB.getId();

            if (fkInstalacion != -1)
            {
                suscripcion = (Suscripcion) suscriptionDTO.toEntity(Suscripcion.class);
                suscripcion.setId( -1 );
//                suscripcion.setArrPagos(new ArrayList<>());
//                suscripcion.setFechaInicio(LocalDateTime.now());
                suscripcion.setActivo(true);

                int fkProducto = suscriptionDTO.getFkProducto();;
//                int fkEmpresa = suscriptionDTO.getFkEmpresa();

                if(fkProducto != -1)
                {
                    Producto productoDB = productoREPO.getByIDN(fkProducto , fkInstalacion);

                    if(productoDB != null)
                    {
//                        suscripcion.setAbono(productoDB.getPrecio());
                        suscripcion.setProducto( productoDB );
//                        suscripcion.setInstalacion(instalacionDB);
                    }
                    else
                    {
                        throw new NotFoundExc("PRODUCTO NOT FOUND : " + productoDB);
                    }
                }
                else
                {
                    throw new NotFoundExc("FK EMPRESA == -1");
                }

                suscripcion = suscripcionREPO.save(suscripcion);
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



        return suscripcion;
    }



    @GetMapping(value = "/{fkEmpresa}")
    @Operation(
            summary = "Listar suscripciones asociados a una Empresa",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public List<Suscripcion> findSuscripcionesXX
    (
            @PathVariable() int fkEmpresa
    )
    {
        List<Suscripcion> arrRelProductoEmpresa = new ArrayList<>();

        Empresa empresaDB = empresaRepo.getByIDN(fkEmpresa);

//        Operador operadorLogeado = LoginWS.dameOperadorLogeado(headers);

        boolean operadorPerteneceEmpresa = false;
        if(empresaDB != null)
        {
//            if(empresaDB.getId() == operadorLogeado.getFKEmpresa())
//            {
                operadorPerteneceEmpresa = true;
                arrRelProductoEmpresa = suscripcionREPO.findByEmpresa(fkEmpresa);
//            }
        }

        return arrRelProductoEmpresa;
    }
}
