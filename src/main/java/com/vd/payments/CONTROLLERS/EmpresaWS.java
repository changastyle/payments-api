package com.vd.payments.CONTROLLERS;

import com.vd.payments.MODELO.Documento;
import com.vd.payments.MODELO.Empresa;
import com.vd.payments.MODELO.Operador;
import com.vd.payments.MODELO.Suscripcion;
import com.vd.payments.REPO.DocREPO;
import com.vd.payments.REPO.EmpresaRepo;
import com.vd.payments.REPO.OperadorREPO;
import com.vd.payments.REPO.SuscripcionREPO;
import com.vd.payments.XCP.CustomException;
import com.vd.payments.XCP.NoLogeadoExc;
import com.vd.payments.XCP.NotAllowedException;
import com.vd.payments.XCP.NotFoundExc;
import com.vd.payments.XDTO.TuplaEmpresaRolDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "empresa")
@CrossOrigin
public class EmpresaWS
{
    @Autowired
    private EmpresaRepo empresaREPO;
    @Autowired
    private OperadorREPO operadorREPO;
    @Autowired
    private SuscripcionREPO relProductoEmpresaREPO;
    @Autowired
    private DocREPO docREPO;
//    @Autowired
//    private RelEmpresaEmpleadoREPO relEmpresaEmpleadoREPO;

    @GetMapping(value = "/")
    @Operation(
            summary = "Devuelve una Lista de Empresas segun el Operador Logeado",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public List<Empresa> findEmpresasByLoggedUser(@RequestHeader HttpHeaders headers)
    {
        List<Empresa> arrEmpresas = new ArrayList<>();

        int fkInstalacion = LoginWS.getFKInstalacionOperadorLogeado(headers);

        if (fkInstalacion != -1)
        {
            arrEmpresas = empresaREPO.findAllByFKInstalacion(fkInstalacion);
        }
        else
        {
            throw new NotFoundExc("No hay empresa asociada el operador logeado");
        }

        return arrEmpresas;
    }

    @GetMapping(value = "/{id}")
    @Operation(
            summary = "Return empresa by id FROM loged user",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public Empresa getEmpresa
            (
                    @RequestHeader HttpHeaders headers,
                    @PathVariable() int id
            )
    {
        Empresa clienteDB = null;

        int fkInstalacionOperadorLogeado = LoginWS.getFKInstalacionOperadorLogeado(headers);

        if (fkInstalacionOperadorLogeado != -1)
        {
            clienteDB = empresaREPO.getByInstalacionID(id, fkInstalacionOperadorLogeado);

            if (clienteDB == null)
            {
                throw new NotFoundExc("NO EXISTE EMPRESA CON ID: " + id);
            }
        }
        else
        {
            throw new NoLogeadoExc("USUARIO NO LOGEADO");
        }

        return clienteDB;
    }

    @PatchMapping(value = "/logo/attach/{fkEmpresa}/{fkDocumento}")
    @Operation(
            summary = "Cambia el logo de una empresa, solo el (ADMIN) puede hacer esto",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public Empresa changeLogoEmpresa
            (
                    @PathVariable(value = "fkEmpresa") int fkEmpresa,
                    @PathVariable(value = "fkDocumento") int fkDocumento,
                    @RequestHeader HttpHeaders headers
            )
    {
        Operador operadorLogeado = LoginWS.dameOperadorLogeado(headers);
        Empresa empresaDB = null;

        if (operadorLogeado != null)
        {
            if (operadorLogeado.isAdmin())
            {
                if (fkDocumento != -1 && fkEmpresa != -1)
                {
                    empresaDB = empresaREPO.getByIDN(fkEmpresa);
                    if (empresaDB != null)
                    {
                        Documento documentoDB = docREPO.getByIDN(fkDocumento);
                        if (documentoDB != null)
                        {
                            empresaDB.setLogo(documentoDB);
                            empresaREPO.save(empresaDB);
                        }
                        else
                        {
                            throw new NotFoundExc("NO SE HA ENCONTRADO DOCUMENTO CON ID: " + fkDocumento);
                        }
                    }
                    else
                    {
                        throw new NotFoundExc("NO SE HA ENCONTRADO EMPRESA CON ID: " + fkEmpresa);
                    }
                }
                else
                {
                    throw new CustomException("FK EMPRESA == " + fkEmpresa + " | FK DOCUMENTO: " + fkDocumento);
                }
            }
            else
            {
                throw new NotAllowedException("NO ERES ADMIN, NO PUEDES REALIZAR ESTA OPERACION");
            }
        }
        else
        {
            throw new NoLogeadoExc("NO HAY OPERADOR LOGEADO");
        }

        return empresaDB;
    }

    //    @PostMapping(value = "relOperador/search/")
//    @CrossOrigin
//    @Operation(
//            summary = "Search operadores de una Instalacion from nombre, apellido or email ",
//            security = @SecurityRequirement(name = "bearerAuth")
//    )
//    public List<RelEmpresaEmpleado>  searchByNombreApellidoOrEmail
//            (
//                    @RequestParam() String busqueda,
//                    @RequestHeader HttpHeaders headers
//            )
//    {
//        List<RelEmpresaEmpleado> arrRelsOperadores = new ArrayList<RelEmpresaEmpleado>();
//
//        int fkInstalacion = LoginWS.getFKInstalacionOperadorLogeado(headers) ;
//
//        if(fkInstalacion != -1)
//        {
//            arrRelsOperadores = relEmpresaEmpleadoREPO.findOperadorPorInstalacionSearchByNameOrApellidoOrEmail(busqueda, fkInstalacion);
//            Collections.sort(arrRelsOperadores);
//        }
//        else
//        {
//            throw new NoLogeadoExc("Operador no logeado");
//        }
//
//        return arrRelsOperadores;
//    }
//
//    //<editor-fold desc="COLABORADORES:">
    @GetMapping(value = "/colaboradores/{fkEmpresa}")
    @Operation(
            summary = "Devuelve una Listado de colaboradores de la empresa del operador logeado",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public List<Operador> findColaboradoresFromEmpresa
    (
            @RequestHeader HttpHeaders headers,
            @PathVariable(value = "fkEmpresa") int fkEmpresa

    )
    {
        List<Operador> arrOperadores = new ArrayList<>();

        Operador operadorLogeado = LoginWS.dameOperadorLogeado(headers);

        if (operadorLogeado != null)
        {
//            int fkEmpresa = operadorLogeado.getFKEmpresa();
            int fkInstalacion = operadorLogeado.getFKInstalacion();

            if (fkEmpresa != -1 && fkInstalacion != -1)
            {
                arrOperadores = operadorREPO.findOperadorPorEmpresa(fkEmpresa);
            }
        }
        else
        {
            throw new NoLogeadoExc("NO HAY OPERADOR LOGEADO");
        }

        return arrOperadores;
    }

    @DeleteMapping(value = "/{fkEmpresa}/rmColaborador/")
    @Operation(
            summary = "Remueve un colaborador de una Empresa",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public Operador rmColaboradorEmail
            (
                    @PathVariable() int fkEmpresa,
                    @RequestParam(name = "emailColaborador") String emailColaborador,
                    @RequestHeader HttpHeaders headers
            )
    {
        Operador operadorDEL = null;
        if (emailColaborador != null && emailColaborador.length() > 0)
        {
            operadorDEL = operadorREPO.getOperadorByEmail(emailColaborador);

            if (operadorDEL != null)
            {
                operadorDEL.setEmpresa(null);
                operadorDEL = operadorREPO.save(operadorDEL);
            }
        }
        return operadorDEL;
    }

    //    {
//        RelEmpresaEmpleado relNew = null;
//        Cliente empresaDB = empresaREPO.getByIDN(fkEmpresa);
//        Operador colaboradorDB = operadorREPO.getOperadorByEmail(emailColaborador);
//        Operador operadorLogeado = LoginWS.dameOperadorLogeado(headers);
//
//        if(operadorLogeado != null)
//        {
//            if(LoginWS.colaboradorPerteneceAEmpresa(colaboradorDB,empresaDB))
//            {
//                if(empresaDB != null)
//                {
//                    if(colaboradorDB != null)
//                    {
//                        // 1 -  COMPRUEBO QUE NO EXISTA PREVIAMENTE UNA RELACION ASI (EMPLEADO , EMPRESA):
//                        int fkEmpleado = colaboradorDB.getId();
//                        RelEmpresaEmpleado relExistente = relEmpresaEmpleadoREPO.existeRel(fkEmpresa, fkEmpleado);
//
//                        if(relExistente != null)
//                        {
//                            // 3 -  SI YA EXISTIA LA RELACION SOLO LA ACTIVO:
//                            relExistente.setActivo(false);
//
//                            // 4 - GUARDO:
//                            if(relExistente != null)
//                            {
//                                relNew = relEmpresaEmpleadoREPO.save(relExistente);
//                            }
//                        }
//                    }
//                    else
//                    {
//                        throw new NotFoundExc("Colaborador not found: " + emailColaborador);
//                    }
//                }
//                else
//                {
//                    throw new NotFoundExc("Empresa not found: " + fkEmpresa);
//                }
//            }
//            else
//            {
//                throw new NotFoundExc("Empresa no puede ser editada por un operador externo");
//            }
//        }
//        else
//        {
//            throw new NoLogeadoExc("Operador No Logeado");
//        }
//
//
//
//        return relNew;
//    }
    public List<Empresa> findAllEmpresasFromInstalacion(int fkInstalacion)
    {
        List<Empresa> arrAllEmpresasInstalacion = new ArrayList<>();
        if (fkInstalacion != -1)
        {
            arrAllEmpresasInstalacion = empresaREPO.findAllByFKInstalacion(fkInstalacion);
        }

        return arrAllEmpresasInstalacion;
    }

    @GetMapping(value = "/people/all")
    @Operation(
            summary = "Lista todos las personas de una instalacion",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public List<Operador> findAll(@RequestHeader HttpHeaders headers)
    {
        int fkInstalacion = LoginWS.getFKInstalacionOperadorLogeado(headers);

        List<Operador> arrAllOperadoresInstalacion = operadorREPO.findOperadorPorInstalacion(fkInstalacion);

        return arrAllOperadoresInstalacion;
    }

    @GetMapping(value = "/people/left/{fkEmpresa}")
    @Operation(
            summary = "Lista todos las personas faltantes de una empresa pero que estan en la instalacion",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public List<Operador> findAllMenosYaCargadosEnEmpresa
            (
                    @PathVariable() int fkEmpresa,
                    @RequestHeader HttpHeaders headers
            )
    {
        int fkInstalacion = LoginWS.getFKInstalacionOperadorLogeado(headers);

        List<Operador> arrAllOperadoresInstalacion = operadorREPO.findOperadorPorInstalacion(fkInstalacion);
        List<Operador> arrAllOperadoresEmpresa = operadorREPO.findOperadorPorEmpresa(fkEmpresa);

        System.out.println("Operadores Instalacion(" + arrAllOperadoresInstalacion.size() + ")");
        int i = 0;
        for (Operador operadorInstalacion : arrAllOperadoresInstalacion)
        {
            System.out.println(i + ")" + operadorInstalacion.getApellido() + "," + operadorInstalacion.getNombre());
            i++;
        }
        System.out.println("----------");
        System.out.println("Operadores Empresa(" + arrAllOperadoresEmpresa.size() + ")");
        int f = 0;
        for (Operador operadorEmpresa : arrAllOperadoresEmpresa)
        {
            System.out.println(f + ")" + operadorEmpresa.getApellido() + "," + operadorEmpresa.getNombre());
            f++;
        }

        List<Operador> operadoresNoEnEmpresa = new ArrayList<>(arrAllOperadoresInstalacion);

        // Eliminamos de la nueva lista los operadores que están en la empresa
        operadoresNoEnEmpresa.removeAll(arrAllOperadoresEmpresa);
//        List<Operador> arrRes =   arrAllOperadoresInstalacion.removeAll(arrAllOperadoresEmpresa);
//        List<Operador> arrRes = arrAllOperadoresInstalacion.stream()
//                .filter(instalacionOperador -> !arrAllOperadoresEmpresa.contains(instalacionOperador))
//                .collect(Collectors.toList());

        if (operadoresNoEnEmpresa == null)
        {
            operadoresNoEnEmpresa = new ArrayList<>();
        }

        return operadoresNoEnEmpresa;
    }

    @PostMapping(value = "/{fkEmpresa}/addColaborador/{emailOperador}")
    @Operation(
            summary = "Agregar un colaborador a una Empresa",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public Operador addColaboradorEmail
            (
                    @PathVariable() int fkEmpresa,
                    @PathVariable() String emailOperador,
                    @RequestHeader HttpHeaders headers
            )
    {
        Empresa empresaDB = empresaREPO.getByIDN(fkEmpresa);
        Operador operadorDB = operadorREPO.getOperadorByEmail(emailOperador);
        Operador operadorLogeado = LoginWS.dameOperadorLogeado(headers);

        if (operadorLogeado != null)
        {
            if (LoginWS.colaboradorPerteneceAEmpresa(operadorDB, empresaDB))
            {
                if (empresaDB != null)
                {
                    if (operadorDB != null)
                    {
                        operadorDB.setEmpresa(empresaDB);

                        operadorDB = operadorREPO.save(operadorDB);
                    }
                    else
                    {
                        throw new NotFoundExc("Colaborador not found: " + emailOperador);
                    }
                }
                else
                {
                    throw new NotFoundExc("Empresa not found: " + fkEmpresa);
                }
            }
            else
            {
                throw new NotFoundExc("Empresa no puede ser editada por un operador externo");
            }
        }
        else
        {
            throw new NoLogeadoExc("Operador No Logeado");
        }


        return operadorDB;
    }
    //</editor-fold>


}
