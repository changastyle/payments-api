package com.vd.payments.CONTROLLERS;

import com.vd.payments.MODELO.Instalacion;
import com.vd.payments.MODELO.Menu;
import com.vd.payments.MODELO.Operador;
import com.vd.payments.REPO.ConfigRepo;
import com.vd.payments.REPO.MenuREPO;
import com.vd.payments.UTIL.serializer.MasterUtil;
import com.vd.payments.XCP.NotFoundExc;
import com.vd.payments.XDTO.MenuSDTO;
import com.vd.payments.XDTO.TuplaEmpresaRolDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(value = "menu")
@CrossOrigin
public class MenuWS
{
    @Autowired
    public MenuREPO menuREPO;

    @GetMapping(value = "/empty")
    @Operation(summary = "Return un menu empty para facilitar la creacion")
    public MenuSDTO empty()
    {
        MenuSDTO menuSDTO = new MenuSDTO("","","",999,true);
//        MenuSDTO menuSDTO = new MenuSDTO("Test","test.url","",999,true);
        return menuSDTO;
    }
    @GetMapping(value = "/")
    @Operation(summary = "Listar todos los menus activos")
    public List<Menu> findAll(@RequestHeader HttpHeaders headers)
    {
        List<Menu> arrMenus = new ArrayList<>();

        TuplaEmpresaRolDTO tupla = LoginWS.dameTuplaOperadorLogeado(headers);

        if(tupla != null)
        {
            int fkInstalacion = tupla.getFkInstalacion();
            boolean adminLevel = tupla.getAdminOfOperador();

            arrMenus = findAllByInstalacionAndAdminLevel(fkInstalacion,adminLevel);
        }


        return arrMenus;
    }
    @GetMapping(value = "/all")
    @Operation(summary = "Listar todos los menus activos e inactivos")
    public List<Menu> findAllActivosEInactivos(@RequestHeader HttpHeaders headers)
    {
        List<Menu> arrMenus = new ArrayList<>();

        TuplaEmpresaRolDTO tupla = LoginWS.dameTuplaOperadorLogeado(headers);

        if(tupla != null)
        {
            int fkInstalacion = tupla.getFkInstalacion();
            boolean adminLevel = tupla.getAdminOfOperador();

            arrMenus = findAllByInstalacionAndAdminLevel(fkInstalacion,adminLevel);
        }


        return arrMenus;
    }
    public List<Menu> findAllByInstalacionAndAdminLevel(int fkInstalacion , boolean adminLevel)
    {
        List<Menu> arrMenus = new ArrayList<>();

        if(fkInstalacion != -1)
        {
            arrMenus = menuREPO.findAllAndRemoveAdminsMenusIfOperadorNotAdmin(fkInstalacion, adminLevel);
        }
        else
        {
            throw new NotFoundExc("Instalacion not Found : " + fkInstalacion);
        }
        Collections.sort(arrMenus);

        return arrMenus;
    }

    @PostMapping(value = "/")
    @Operation(summary = "Crear un nuevo Menu")
    public Menu create
    (
        @RequestBody() MenuSDTO menuSDTO,
        @RequestHeader HttpHeaders headers
    )
    {
        Menu menuNvo = new Menu();


        Operador operadorLogeado = LoginWS.dameOperadorLogeado(headers);

        if(operadorLogeado != null)
        {
            menuNvo = (Menu) menuSDTO.toEntity(Menu.class);
            if (menuNvo != null)
            {
                Instalacion instalacion = operadorLogeado.getInstalacion();

                if(instalacion != null)
                {
                    menuNvo.setInstalacion(instalacion);
                    menuNvo.setRequiereAdmin(true);
                    menuNvo.setActivo(true);
                    menuNvo = menuREPO.save(menuNvo);
                }
            }
        }

        return menuNvo;
    }
    @PutMapping(value = "/{id}")
    @Operation(summary = "Editar un Menu existente")
    public Menu edit
    (
            @PathVariable(value = "id") int id,
            @RequestBody() MenuSDTO menuSDTO,
            @RequestHeader HttpHeaders headers
    )
    {
        Menu menuDB = null;

        Operador operador = LoginWS.dameOperadorLogeado(headers);

        if (operador != null)
        {
            if(operador.isAdmin() && operador.isActivo())
            {
                if (id != -1)
                {
                     menuDB = menuREPO.getByIDN(id,1);

                    if(menuDB != null)
                    {
                        menuDB.update(menuSDTO.getNombre(),menuSDTO.getUrl() , menuSDTO.getIcono() , menuSDTO.getOrden() , menuSDTO.isRequiereAdmin() , true);
                        menuDB = menuREPO.save(menuDB);
                    }
                }
            }
        }

        return menuDB;
    }
    @GetMapping(value = "/{id}/disable/")
    @Operation(summary = "Desabilitar o Habilitar un menu")
    public Menu disable
    (
            @PathVariable(value = "id") int id,
            @RequestHeader HttpHeaders headers
    )
    {
        Menu menuDB = null;

        Operador operador = LoginWS.dameOperadorLogeado(headers);

        if (operador != null)
        {
            if(operador.isAdmin() && operador.isActivo())
            {
                if (id != -1)
                {
                     menuDB = menuREPO.getByIDN(id,1);

                    if(menuDB != null)
                    {
                        boolean estadoAnterior = menuDB.isActivo();
                        boolean nuevoEstado = !estadoAnterior;
                        menuDB.setActivo(nuevoEstado);
                        menuDB = menuREPO.save(menuDB);
                    }
                }
            }
        }

        return menuDB;
    }
    @PatchMapping(value = "/{idMenu}/{campo}/{valor}")
    @Operation(summary = "Editar un campo en particular del Menu")
    public Menu edit
    (
        @PathVariable() int idMenu,
        @PathVariable() String campo,
        @PathVariable() String valor,
        @RequestHeader HttpHeaders headers
    )
    {
        Menu menuDB = null;

        Operador operadorLogeado = LoginWS.dameOperadorLogeado(headers);

        if(operadorLogeado != null)
        {
            if(operadorLogeado.isAdmin())
            {
                if(idMenu != -1)
                {
                    menuDB = menuREPO.getByIDN(idMenu,1);

                    if(menuDB != null)
                    {
                        if(campo.equalsIgnoreCase("nombre"))
                        {
                            valor = MasterUtil.pMayus(valor);
                            menuDB.setNombre(valor);
                        }
                        if(campo.equalsIgnoreCase("url"))
                        {
                            menuDB.setUrl(valor);
                        }
                        if(campo.equalsIgnoreCase("icono"))
                        {
                            menuDB.setIcono(valor);
                        }
                        if(campo.equalsIgnoreCase("orden"))
                        {
                            if(MasterUtil.isNumeric(valor))
                            {
                                menuDB.setOrden(Integer.parseInt(valor));
                            }
                        }
                        if(campo.equalsIgnoreCase("admin"))
                        {
                            boolean valorAnterior = menuDB.isRequiereAdmin();
                            boolean valorNuevo = ! valorAnterior;
                            menuDB.setRequiereAdmin(valorNuevo);
                        }
                        menuDB = menuREPO.save(menuDB);
                    }
                }
            }
        }



        return menuDB;
    }
    @DeleteMapping(value = "/{idMenu}")
    @Operation(summary = "Eliminacion definitiva de un menu")
    public boolean delete
    (
        @PathVariable() int idMenu,
        @RequestHeader HttpHeaders headers
    )
    {
        boolean rm = false;
        Menu menuDB = null;

        Operador operadorLogeado = LoginWS.dameOperadorLogeado(headers);
        if(operadorLogeado != null)
        {
            if(operadorLogeado.isAdmin())
            {
                if(idMenu != -1)
                {
                    menuDB = menuREPO.getByIDN(idMenu,1);

                    if(menuDB != null)
                    {
                        menuREPO.delete(menuDB);
                        rm = true;
                    }
                }
            }
        }


        return rm;
    }

}
