package com.vd.payments.CONTROLLERS;

import com.vd.payments.MODELO.Foto;
import com.vd.payments.REPO.FotoRepo;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "fotos")
@SecurityRequirement(name = "bearerAuth")
public class FotosWS
{

    @Autowired
    private static FotoRepo fotoRepo;
    public FotosWS(FotoRepo fotoRepo)
    {
        this.fotoRepo = fotoRepo;
    }

    @GetMapping("/")
    public List<Foto> findFotos()
    {
        return fotoRepo.findAll();
    }
    @GetMapping("/{id}")
    public Foto get(@PathVariable int id)
    {
        Foto fotoRta = new Foto();
        Foto fotoDB = fotoRepo.getById(id);
        if(fotoDB != null)
        {
            fotoRta = fotoDB;
        }
        return fotoRta;
    }
    @GetMapping("/default")
    public static  Foto porDefecto()
    {
        Foto fotoRta = null;
        Foto fotoDB = fotoRepo.getById(1);

        if(fotoDB == null)
        {
            fotoDB = new Foto("default.jpg");
            fotoDB = fotoRepo.save(fotoDB);
        }

        fotoRta = fotoDB;

        return fotoRta;
    }

//      EL SAVE SE HACE POR UPLOAD ONLY:
//    @PostMapping(name = "/")
//    public Foto save(Foto foto)
//    {
//        return fotoRepo.save(foto);
//    }
}
