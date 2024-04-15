package com.vd.payments.CONTROLLERS;

import com.vd.payments.MODELO.*;
import com.vd.payments.REPO.*;
import com.vd.payments.XCP.CustomException;
import com.vd.payments.XCP.NoInstalacionExc;
import com.vd.payments.XCP.NoLogeadoExc;
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
import java.util.List;

@RestController
@RequestMapping(value = "estados")
@CrossOrigin
public class CambioEstadoWS
{
    @Autowired
    private CambioEstadoRepo cambioEstadoRepo;
    @Autowired
    private EstadoPosibleRepo estadoPosibleRepo;
    @Autowired
    private FacturaREPO facturaREPO;

    @GetMapping(value = "/disponibles")
    @Operation(
            summary = "Devuelve una Lista de Facturas segun el FK_EMPRESA del operador",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public List<EstadoPosible> findEstadoDisponibles(@RequestHeader HttpHeaders headers)
    {
        List<EstadoPosible> arrEstadosPosibles = new ArrayList<>();

        arrEstadosPosibles = estadoPosibleRepo.findEstadosPosiblesActivos();

        return arrEstadosPosibles;
    }

    @GetMapping(value = "/{id}")
    @Operation(
            summary = "Devuelve un Estado posible por ID",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public EstadoPosible getEstadoPosibleByID
            (
                    @PathVariable() int id,
                    @RequestHeader HttpHeaders headers
            )
    {
        EstadoPosible estadoPosible = null;

        estadoPosible = estadoPosibleRepo.getByIDN(id);

        return estadoPosible;
    }

    @GetMapping(value = "/default")
    @Operation(
            summary = "Default Estado Posible",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public EstadoPosible getEstadoDefault
            (
                    @RequestHeader HttpHeaders headers
            )
    {
        EstadoPosible estadoDefault = null;

        estadoDefault = estadoPosibleRepo.getByIDN(1);

        return estadoDefault;
    }

    @GetMapping(value = "/avanzar/factura/{idFactura}")
    @Operation(
            summary = "Avanzar Estado de una Factura por ID",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public Factura avanzarEstadoDeUnaFactura
            (
                    @PathVariable() int idFactura,
                    @RequestHeader HttpHeaders headers
            )
    {
        Factura facturaDB = null;

        // 1 - DAME EL OP LOGAEADO:
        Operador operadorLogeado = LoginWS.dameOperadorLogeado(headers);

        if (operadorLogeado != null)
        {
            if (idFactura != -1)
            {
                // 2 - BUSCO LA FACTURA:
                facturaDB = facturaREPO.getByIDN(idFactura);

                if (facturaDB != null)
                {
                    // 3 - COMPRUEBO QUE LA FACTURA Y EL OPERADOR SEAN DE LA MISMA EMPRESA:
                    Empresa empresaFactura = facturaDB.getEmpresa();
                    Empresa empresaOpLogeado = operadorLogeado.getEmpresa();

                    int fkEmpresaFactura = empresaFactura.getId();
                    int fkEmpresaOperadorLogeado = empresaOpLogeado.getId();

                    if (empresaFactura != null && empresaOpLogeado != null)
                    {
                        if (fkEmpresaFactura == fkEmpresaOperadorLogeado)
                        {
                            // 4 - BUSCO EL ULTIMO CAMBIO DE ESTADO Y CALCULO EL PROXIMO ESTADO POSIBLE:
                            CambioEstado ultCambio = facturaDB.getUltimoCambioEstadoFactura();

                            if (ultCambio != null)
                            {
                                EstadoPosible estadoActual = ultCambio.getEstadoActual();

                                if (estadoActual != null)
                                {
                                    EstadoPosible estadoProximo = estadoActual.getEstadoProximoPositivo();

                                    if (estadoProximo != null)
                                    {
                                        // 5 -  VERIFICO NO HABER LLEGADO AL FINAL: QUE REALMENTE HAYA UN CAMBIO DE ESTADO:
                                        if(estadoProximo.getId() != estadoActual.getId())
                                        {
                                            // 6 - CREO UN NUEVO CAMBIO DE ESTADO Y LO AGREGO AL LISTADO DE CAMBIOS DE ESTADO DE LA FACTURA:
                                            CambioEstado nuevoCambioEstado = new CambioEstado(facturaDB, operadorLogeado, estadoActual, estadoProximo);

                                            facturaDB.addCambioEstado(nuevoCambioEstado);

                                            // 7 - GUARDO LA FACTURA EN DB:
                                            facturaDB = facturaREPO.save(facturaDB);
                                        }
                                        else {
                                            throw new CustomException("NO HAY MAS CAMBIOS, ESTADO ACTUAL: " + estadoActual.getId() + " - " + estadoActual.getNombre() + " - ESTADO PROXIMO: " +  estadoProximo.getId() + " - " + estadoProximo.getNombre());
                                        }
                                    }
                                    else
                                    {
                                        throw new CustomException("ESTADO PROXIMO == NULL");
                                    }
                                }
                                else
                                {
                                    throw new CustomException("ESTADO ACTUAL == NULL");
                                }
                            }
                            else
                            {
                                throw new CustomException("ULT ESTADO == NULL ");
                            }
                        }
                        else
                        {
                            throw new CustomException("EL FK EMPRESA DEL OPERADOR LOGEADO ES : " + fkEmpresaOperadorLogeado + " Y EL FK EMPRESA DE LA FACTURA ES: " +  fkEmpresaFactura );
                        }
                    }
                    else
                    {
                        if (empresaFactura == null)
                        {
                            throw new CustomException("EMPRESA FACTURA == NULL ");
                        }
                        else
                        {
                            throw new CustomException("EMPRESA OPERADOR LOGEADO == NULL ");
                        }
                    }
                }
            }
            else
            {
                throw new CustomException("ID FACTURA == -1");
            }
        }
        else
        {
            throw new NoLogeadoExc("Operador no logeado");
        }


        return facturaDB;
    }
    @GetMapping(value = "/avanzar/negativo/factura/{idFactura}")
    @Operation(
            summary = "Avanzar a un Estado Negativo al actual de una Factura por ID",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public Factura avanzarNegativoEstadoDeUnaFactura
            (
                    @PathVariable() int idFactura,
                    @RequestHeader HttpHeaders headers
            )
    {
        Factura facturaDB = null;

        // 1 - DAME EL OP LOGAEADO:
        Operador operadorLogeado = LoginWS.dameOperadorLogeado(headers);

        if (operadorLogeado != null)
        {
            if (idFactura != -1)
            {
                // 2 - BUSCO LA FACTURA:
                facturaDB = facturaREPO.getByIDN(idFactura);

                if (facturaDB != null)
                {
                    // 3 - COMPRUEBO QUE LA FACTURA Y EL OPERADOR SEAN DE LA MISMA EMPRESA:
                    Empresa empresaFactura = facturaDB.getEmpresa();
                    Empresa empresaOpLogeado = operadorLogeado.getEmpresa();

                    int fkEmpresaFactura = empresaFactura.getId();
                    int fkOperadorFactura = empresaOpLogeado.getId();

                    if (empresaFactura != null && empresaOpLogeado != null)
                    {
                        if (fkEmpresaFactura == fkOperadorFactura)
                        {
                            // 4 - BUSCO EL ULTIMO CAMBIO DE ESTADO Y CALCULO EL PROXIMO ESTADO POSIBLE:
                            CambioEstado ultCambio = facturaDB.getUltimoCambioEstadoFactura();

                            if (ultCambio != null)
                            {
                                EstadoPosible estadoActual = ultCambio.getEstadoActual();

                                if (estadoActual != null)
                                {
                                    EstadoPosible estadoProximo = estadoActual.getEstadoNegativo();

                                    if (estadoProximo != null)
                                    {
                                        // 5 -  VERIFICO NO HABER LLEGADO AL FINAL: QUE REALMENTE HAYA UN CAMBIO DE ESTADO:
                                        if(estadoProximo.getId() != estadoActual.getId())
                                        {
                                            CambioEstado nuevoCambioEstado = new CambioEstado(facturaDB, operadorLogeado, estadoActual, estadoProximo);

                                            facturaDB.addCambioEstado(nuevoCambioEstado);

                                            // 6 - GUARDO LA FACTURA EN DB:
                                            facturaDB = facturaREPO.save(facturaDB);
                                        }
                                        else {
                                            throw new CustomException("NO HAY MAS CAMBIOS, ESTADO ACTUAL: " + estadoActual.getId() + " - " + estadoActual.getNombre() + " - ESTADO PROXIMO: " +  estadoProximo.getId() + " - " + estadoProximo.getNombre());
                                        }
                                    }
                                    else
                                    {
                                        throw new CustomException("ESTADO PROXIMO == NULL");
                                    }
                                }
                                else
                                {
                                    throw new CustomException("ESTADO ACTUAL == NULL");
                                }
                            }
                            else
                            {
                                throw new CustomException("ULT ESTADO == NULL ");
                            }
                        }
                        else
                        {
                            if (empresaFactura == null)
                            {
                                throw new CustomException("EMPRESA FACTURA == NULL ");
                            }
                            else
                            {
                                throw new CustomException("EMPRESA OPERADOR LOGEADO == NULL ");
                            }
                        }
                    }
                    else
                    {
                        throw new CustomException("ID FACTURA == -1");
                    }
                }
            }
            else
            {
                throw new CustomException("ID FACTURA == -1");
            }
        }
        else
        {
            throw new NoLogeadoExc("Operador no logeado");
        }


        return facturaDB;
    }
}