package com.vd.payments.CONTROLLERS;

import com.vd.payments.XDTO.InstalacionSaveDTO;
import com.vd.payments.MODELO.Instalacion;
import com.vd.payments.MODELO.Operador;
import com.vd.payments.REPO.InstalacionRepo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;

@RestController
@RequestMapping(value = "instalacion")
@CrossOrigin
@SecurityRequirement(name = "jwt")
public class InstalacionWS
{

    @Autowired
    public InstalacionRepo instalacionRepo;


    @GetMapping("/")
    @Operation(summary = "Devuelve la instalacion del Operador Logeado" , security = @SecurityRequirement(name = "bearerAuth"))
    public Instalacion get(@RequestHeader HttpHeaders headers)
    {

        for(int i = 0 ; i < headers.size() ; i++)
        {
            System.out.println("H" + i + " : " + headers.get(i));
        }
        Instalacion instalacionDB = null;

        Operador operadorLogeado = LoginWS.dameOperadorLogeado(headers);

        if(operadorLogeado != null)
        {
            Instalacion instalacionAsociadaOperador = operadorLogeado.getInstalacion();

            if(instalacionAsociadaOperador != null)
            {
                int fkInstalacionAsociada = instalacionAsociadaOperador.getId();

                instalacionDB = instalacionRepo.getById(fkInstalacionAsociada);
            }
        }

        return instalacionDB;
    }

    @PostMapping("save")
    public Instalacion save(@RequestBody InstalacionSaveDTO instalacionSaveDTO)
    {
        Instalacion instalacionNew = null;

        if(instalacionSaveDTO != null)
        {
            instalacionNew = (Instalacion) instalacionSaveDTO.toEntity(Instalacion.class);

            instalacionNew.setLogo(FotosWS.porDefecto());

            instalacionNew = instalacionRepo.save(instalacionNew);

        }

        return instalacionNew;
    }

}
