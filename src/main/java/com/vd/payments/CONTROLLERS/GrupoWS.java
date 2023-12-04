package com.vd.payments.CONTROLLERS;

import com.vd.payments.XDTO.GrupoSDTO;
import com.vd.payments.REPO.FotoRepo;
import com.vd.payments.REPO.GrupoREPO;
import com.vd.payments.MODELO.Foto;
import com.vd.payments.MODELO.Grupo;
import com.vd.payments.MODELO.Instalacion;
import com.vd.payments.MODELO.Operador;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "grupo")
public class GrupoWS
{
    private static GrupoREPO grupoREPO;
    private static FotoRepo fotoRepo;

    @Autowired
    public GrupoWS(GrupoREPO grupoREPO, FotoRepo fotoRepo)
    {
        GrupoWS.grupoREPO = grupoREPO;
        GrupoWS.fotoRepo = fotoRepo;
    }

    public static Optional<Instalacion> dameInstalacion(HttpHeaders headers)
    {
        Optional<Instalacion> rta = null;
        Operador operadorLogeado = LoginWS.dameOperadorLogeado(headers);

        if (operadorLogeado != null) {
            Instalacion instalacion = operadorLogeado.getInstalacion();

            if (instalacion != null) {
                rta = Optional.of(instalacion);
            }
        }
        return  rta;
    }
    @GetMapping(value = "/find")
    @Operation(
            summary = "Listar los grupos de un Operador Logeado by Instalacion",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public static List<Grupo> findGrupos(@RequestHeader HttpHeaders headers)
    {
        List<Grupo> arrGrupos = new ArrayList<>();

        Optional<Instalacion> ins = dameInstalacion(headers);
        if (ins.isPresent())
        {
            int fkInstalacion = ins.get().getId();
            arrGrupos = grupoREPO.findGrupoByFKInstalacion(fkInstalacion);
            //        List<Grupo> arrGrupos = grupoREPO.findAll();
        }

        return arrGrupos;
    }
    @PostMapping(value = "/")
    @Operation(
            summary = "Para crear un nuevo grupo en la Instalacion y foto",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public static Grupo create(@RequestHeader HttpHeaders headers, @RequestBody()GrupoSDTO grupoSDTO)
    {
        Grupo grupoNew = (Grupo) grupoSDTO.toEntity(Grupo.class);

        Operador operadorLogeado = LoginWS.dameOperadorLogeado(headers);

        if (operadorLogeado != null)
        {
            Instalacion instalacion = operadorLogeado.getInstalacion();

            if(instalacion != null)
            {
                if(grupoNew != null)
                {
                    Foto logo = fotoRepo.getById(grupoSDTO.getFkLogo());

                    if(logo != null)
                    {
                        grupoNew.setLogo(logo);
                    }

                    grupoNew.setInstalacion(instalacion);
                    grupoNew.setActivo(true);

                    grupoNew = grupoREPO.save(grupoNew);
                }
            }
        }

        return grupoNew;
    }
}
