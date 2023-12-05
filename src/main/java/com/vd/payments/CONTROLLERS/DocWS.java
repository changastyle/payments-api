package com.vd.payments.CONTROLLERS;

import com.vd.payments.MODELO.Documento;
import com.vd.payments.REPO.DocREPO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.print.Doc;
import java.util.List;

@RestController
@RequestMapping(value = "documento")
@SecurityRequirement(name = "bearerAuth")
public class DocWS
{

        @Autowired
        private static DocREPO docREPO;
        public DocWS(DocREPO docREPO)
        {
            this.docREPO = docREPO;
        }

        @GetMapping("/")
        public List<Documento> findFotos()
        {
            return docREPO.findAll();
        }

        @GetMapping("/{id}")
        public Documento get(@PathVariable int id)
        {
            Documento docRTA = new Documento();
            Documento docDB = docREPO.getByIDN(id);

            if(docDB != null)
            {
                docRTA = docDB;
            }

            return docRTA;
        }

        @GetMapping("/default")
        public static  Documento porDefecto()
        {
            Documento docRTA = null;
            Documento docDB = docREPO.getById(1);

            if(docDB == null)
            {
                docDB = new Documento("default.jpg");
                docDB = docREPO.save(docDB);
            }

            docRTA = docDB;

            return docRTA;
        }

    @GetMapping(value = "/attach/{idInvoice}/{idFoto}")
    @CrossOrigin
    public static Documento attach
            (
                    @PathVariable() int idInvoice,
                    @PathVariable() int idDoc
            )
    {
        Documento docRTA = null;

        docRTA = docREPO.getByIDN(idDoc);

        return docRTA;
    }

//      EL SAVE SE HACE POR UPLOAD ONLY:
//    @PostMapping(name = "/")
//    public Foto save(Foto foto)
//    {
//        return fotoRepo.save(foto);
//    }
}
