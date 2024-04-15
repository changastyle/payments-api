package com.vd.payments.CONTROLLERS;

import com.vd.payments.MODELO.*;
import com.vd.payments.REPO.*;
import com.vd.payments.XCP.NoLogeadoExc;
import com.vd.payments.XCP.NotAllowedException;
import com.vd.payments.XCP.NotFoundExc;
import com.vd.payments.XDTO.FacturaSaveDTO;
import com.vd.payments.XDTO.TuplaEmpresaRolDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(value = "factura")
@CrossOrigin
public class FacturaWS
{
    @Autowired
    private FacturaREPO facturaREPO;
    @Autowired
    private ProductoREPO productoREPO;
    @Autowired
    private EmpresaRepo empresaRepo;
    @Autowired
    private CambioEstadoWS cambioEstadoWS;


    @PostMapping(value = "/")
    @Operation(
            summary = "Crear una factura",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public Factura create(@RequestBody() FacturaSaveDTO facturaSaveDTO, @RequestHeader HttpHeaders headers)
    {
        Factura facturaNva = null;

        TuplaEmpresaRolDTO tupla = LoginWS.dameTuplaOperadorLogeado(headers);
        if(tupla != null)
        {
            int fkEmpresa = 0;
            Operador operadorLogeado = null;
            if (tupla.getAdminOfOperador())
            {
                fkEmpresa = facturaSaveDTO.fkEmpresa;
                Empresa empresaDB = empresaRepo.getByIDN(fkEmpresa);
                operadorLogeado = tupla.getOperador();

                if (empresaDB != null)
                {
                    if (operadorLogeado != null)
                    {
                        facturaNva = new Factura();

                        LocalDateTime ahora = LocalDateTime.now();
                        int mes = facturaSaveDTO.mesFactura;
                        int year = facturaSaveDTO.yearFactura;
                        int precio = facturaSaveDTO.precio;

                        facturaNva.setEmpresa(empresaDB);
                        facturaNva.setFechaEmision(ahora);
                        facturaNva.setMes(String.valueOf(mes));
                        facturaNva.setYear(String.valueOf(year));
                        facturaNva.setActivo(true);

                        Producto productoDB = productoREPO.getByIDN(facturaSaveDTO.fkProducto, tupla.getFkInstalacion());

                        if (productoDB != null)
                        {
                            DetFactura detFactura = new DetFactura(facturaSaveDTO.cantidad, productoDB, precio, facturaNva, true);

                            // 1 - ADD DETALLE FACTURA:
                            facturaNva.addDetFactura(detFactura);

                            // 2 - ADD CAMBIO ESTADO:
                            EstadoPosible estadoDefault = cambioEstadoWS.getEstadoDefault(headers);
                            CambioEstado cambioEstadoInicial = new CambioEstado(facturaNva, operadorLogeado, estadoDefault, estadoDefault);
                            facturaNva.addCambioEstado(cambioEstadoInicial);

                            // 3 - SAVE FACTURA:
                            facturaNva = facturaREPO.save(facturaNva);
                        }
                        else
                        {
                            throw new NotFoundExc("PRODUCTO NOT FOUND WITH ID [" + facturaSaveDTO.fkProducto + "] FOR EMPRESA : " + fkEmpresa);

                        }
                    }
                    else
                    {
                        throw new NotFoundExc("OPERADOR LOGEADO NOT FOUND : " + operadorLogeado.getEmail());
                    }
                }
                else
                {
                    throw new NotFoundExc("EMPRESA NOT FOUND WITH ID : " + fkEmpresa);
                }
            }
            else
            {
                throw new NotAllowedException("OPERADDR IS NOT ADMIN : " + operadorLogeado.getEmail() + " - " + tupla.getAdminOfOperador());
            }
        }
        else
        {
            throw new NoLogeadoExc("TUPLA OPERADOR EMPRESA ROL == NULL");
        }
        return facturaNva;
    }
    @GetMapping(value = "/")
    @Operation(
            summary = "Devuelve una Lista de Facturas segun el FK_EMPRESA del operador",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public List<Factura> findFacturas(@RequestHeader HttpHeaders headers)
    {
        List<Factura> arrFacturas = new ArrayList<>();

        TuplaEmpresaRolDTO tupla = LoginWS.dameTuplaOperadorLogeado(headers);
        if(tupla != null)
        {
            if(tupla.getAdminOfOperador())
            {
                int fkInstalacion = tupla.getFkInstalacion();
                arrFacturas  = facturaREPO.findAllByInstalacionForAdminPurposes(fkInstalacion);
            }
            else
            {
                int fkEmpresa = tupla.getFKEmpresaPrincipal();
                if (fkEmpresa != -1)
                {
                    arrFacturas = facturaREPO.findAllByFKEmpresa(fkEmpresa);
                }
                else
                {
                    throw new NotFoundExc("No hay empresa asociada el operador logeado");
                }
            }
        }

        // 1 - ORDENO A LA MAS VIEJA PRIMERO ORDEN NATURAL:
        Collections.sort(arrFacturas);

        // 2 - HAGO LA REVERSA PARA TENER LA MAS NUEVA ARRIBA:
        Collections.reverse(arrFacturas);

        return arrFacturas;
    }
    @GetMapping(value = "/{id}")
    @Operation(
            summary = "Devuelve Facturas por ID segun el FK_EMPRESA del operador",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public Factura get
    (
        @PathVariable() int id,
        @RequestHeader HttpHeaders headers)
    {
        Factura facturaDB = null;

        TuplaEmpresaRolDTO tupla = LoginWS.dameTuplaOperadorLogeado(headers);
        if(tupla != null)
        {
            if(tupla.getAdminOfOperador())
            {
                int fkInstalacion = tupla.getFkInstalacion();
                facturaDB  = facturaREPO.getByInstalacionAndID(id, fkInstalacion);
            }
            else
            {
            }
        }



        return facturaDB;
    }
}
//    @GetMapping(value = "/{id}")
//
//    @Operation(
//            summary = "Return empresa by id FROM loged user",
//            security = @SecurityRequirement(name = "bearerAuth")
//    )
//    public Empresa getEmpresa
//            (
//                    @RequestHeader HttpHeaders headers,
//                    @PathVariable() int id
//            )
//    {
//        Empresa clienteDB = null;
//
//        int fkInstalacionOperadorLogeado = LoginWS.getFKInstalacionOperadorLogeado(headers);
//
//        if (fkInstalacionOperadorLogeado != -1)
//        {
//            clienteDB = empresaREPO.getByInstalacionID(id, fkInstalacionOperadorLogeado);
//
//            if (clienteDB == null)
//            {
//                throw new NotFoundExc("NO EXISTE EMPRESA CON ID: " + id);
//            }
//        } else
//        {
//            throw new NoLogeadoExc("USUARIO NO LOGEADO");
//        }
//
//        return clienteDB;
//    }
//
//
//    //    @PostMapping(value = "relOperador/search/")
////    @CrossOrigin
////    @Operation(
////            summary = "Search operadores de una Instalacion from nombre, apellido or email ",
////            security = @SecurityRequirement(name = "bearerAuth")
////    )
////    public List<RelEmpresaEmpleado>  searchByNombreApellidoOrEmail
////            (
////                    @RequestParam() String busqueda,
////                    @RequestHeader HttpHeaders headers
////            )
////    {
////        List<RelEmpresaEmpleado> arrRelsOperadores = new ArrayList<RelEmpresaEmpleado>();
////
////        int fkInstalacion = LoginWS.getFKInstalacionOperadorLogeado(headers) ;
////
////        if(fkInstalacion != -1)
////        {
////            arrRelsOperadores = relEmpresaEmpleadoREPO.findOperadorPorInstalacionSearchByNameOrApellidoOrEmail(busqueda, fkInstalacion);
////            Collections.sort(arrRelsOperadores);
////        }
////        else
////        {
////            throw new NoLogeadoExc("Operador no logeado");
////        }
////
////        return arrRelsOperadores;
////    }
////
////    //<editor-fold desc="COLABORADORES:">
//    @GetMapping(value = "/colaboradores/{fkEmpresa}")
//    @Operation(
//            summary = "Devuelve una Listado de colaboradores de la empresa del operador logeado",
//            security = @SecurityRequirement(name = "bearerAuth")
//    )
//    public List<Operador> findColaboradoresFromEmpresa
//    (
//            @RequestHeader HttpHeaders headers,
//            @PathVariable(value = "fkEmpresa") int fkEmpresa
//
//    )
//    {
//        List<Operador> arrOperadores = new ArrayList<>();
//
//        Operador operadorLogeado = LoginWS.dameOperadorLogeado(headers);
//
//        if (operadorLogeado != null)
//        {
////            int fkEmpresa = operadorLogeado.getFKEmpresa();
//            int fkInstalacion = operadorLogeado.getFKInstalacion();
//
//            if (fkEmpresa != -1 && fkInstalacion != -1)
//            {
//                arrOperadores = operadorREPO.findOperadorPorEmpresa(fkEmpresa);
//            }
//        } else
//        {
//            throw new NoLogeadoExc("NO HAY OPERADOR LOGEADO");
//        }
//
//        return arrOperadores;
//    }
//
//    @DeleteMapping(value = "/{fkEmpresa}/rmColaborador/")
//    @Operation(
//            summary = "Remueve un colaborador de una Empresa",
//            security = @SecurityRequirement(name = "bearerAuth")
//    )
//    public Operador rmColaboradorEmail
//    (
//            @PathVariable() int fkEmpresa,
//            @RequestParam(name = "emailColaborador") String emailColaborador,
//            @RequestHeader HttpHeaders headers
//    )
//    {
//        Operador operadorDEL = null;
//        if (emailColaborador != null && emailColaborador.length() > 0)
//        {
//            operadorDEL = operadorREPO.getOperadorByEmail(emailColaborador);
//
//            if (operadorDEL != null)
//            {
//                operadorDEL.setEmpresa(null);
//                operadorDEL = operadorREPO.save(operadorDEL);
//            }
//        }
//        return operadorDEL;
//    }
//    public List<Empresa> findAllEmpresasFromInstalacion(int fkInstalacion)
//    {
//        List<Empresa> arrAllEmpresasInstalacion = new ArrayList<>();
//        if (fkInstalacion != -1)
//        {
//            arrAllEmpresasInstalacion = empresaREPO.findAllByFKInstalacion(fkInstalacion);
//        }
//
//        return arrAllEmpresasInstalacion;
//    }
//
//    @GetMapping(value = "/people/all")
//    @Operation(
//            summary = "Lista todos las personas de una instalacion",
//            security = @SecurityRequirement(name = "bearerAuth")
//    )
//    public List<Operador> findAll(@RequestHeader HttpHeaders headers)
//    {
//        int fkInstalacion = LoginWS.getFKInstalacionOperadorLogeado(headers);
//
//        List<Operador> arrAllOperadoresInstalacion = operadorREPO.findOperadorPorInstalacion(fkInstalacion);
//
//        return arrAllOperadoresInstalacion;
//    }
//
//    @GetMapping(value = "/people/left/{fkEmpresa}")
//    @Operation(
//            summary = "Lista todos las personas faltantes de una empresa pero que estan en la instalacion",
//            security = @SecurityRequirement(name = "bearerAuth")
//    )
//    public List<Operador> findAllMenosYaCargadosEnEmpresa
//    (
//            @PathVariable() int fkEmpresa,
//            @RequestHeader HttpHeaders headers
//    )
//    {
//        int fkInstalacion = LoginWS.getFKInstalacionOperadorLogeado(headers);
//
//        List<Operador> arrAllOperadoresInstalacion = operadorREPO.findOperadorPorInstalacion(fkInstalacion);
//        List<Operador> arrAllOperadoresEmpresa = operadorREPO.findOperadorPorEmpresa(fkEmpresa);
//
//        System.out.println("Operadores Instalacion(" + arrAllOperadoresInstalacion.size() + ")");
//        int i = 0;
//        for(Operador operadorInstalacion : arrAllOperadoresInstalacion)
//        {
//            System.out.println( i + ")" + operadorInstalacion.getApellido() + "," + operadorInstalacion.getNombre());
//            i++;
//        }
//        System.out.println("----------");
//        System.out.println("Operadores Empresa(" + arrAllOperadoresEmpresa.size() + ")");
//        int f = 0;
//        for(Operador operadorEmpresa : arrAllOperadoresEmpresa)
//        {
//            System.out.println( f + ")" + operadorEmpresa.getApellido() + "," + operadorEmpresa.getNombre());
//            f++;
//        }
//
//        List<Operador> operadoresNoEnEmpresa = new ArrayList<>(arrAllOperadoresInstalacion);
//
//        // Eliminamos de la nueva lista los operadores que están en la empresa
//        operadoresNoEnEmpresa.removeAll(arrAllOperadoresEmpresa);
//
//        if(operadoresNoEnEmpresa == null)
//        {
//            operadoresNoEnEmpresa = new ArrayList<>();
//        }
//
//        return operadoresNoEnEmpresa;
//    }
//
//    @PostMapping(value = "/{fkEmpresa}/addColaborador/{emailOperador}")
//    @Operation(
//            summary = "Agregar un colaborador a una Empresa",
//            security = @SecurityRequirement(name = "bearerAuth")
//    )
//    public Operador addColaboradorEmail
//            (
//                    @PathVariable() int fkEmpresa,
//                    @PathVariable() String emailOperador,
//                    @RequestHeader HttpHeaders headers
//            )
//    {
//        Empresa empresaDB = empresaREPO.getByIDN(fkEmpresa);
//        Operador operadorDB = operadorREPO.getOperadorByEmail(emailOperador);
//        Operador operadorLogeado = LoginWS.dameOperadorLogeado(headers);
//
//        if (operadorLogeado != null)
//        {
//            if (LoginWS.colaboradorPerteneceAEmpresa(operadorDB, empresaDB))
//            {
//                if (empresaDB != null)
//                {
//                    if (operadorDB != null)
//                    {
//                        operadorDB.setEmpresa(empresaDB);
//
//                        operadorDB = operadorREPO.save(operadorDB);
//                    } else
//                    {
//                        throw new NotFoundExc("Colaborador not found: " + emailOperador);
//                    }
//                } else
//                {
//                    throw new NotFoundExc("Empresa not found: " + fkEmpresa);
//                }
//            } else
//            {
//                throw new NotFoundExc("Empresa no puede ser editada por un operador externo");
//            }
//        } else
//        {
//            throw new NoLogeadoExc("Operador No Logeado");
//        }
//
//
//        return operadorDB;
//    }
//
//
//
//
//
//}
