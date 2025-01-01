package com.vd.payments.CONTROLLERS;

import com.vd.payments.MODELO.Documento;
import com.vd.payments.MODELO.Operador;
import com.vd.payments.REPO.DocREPO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import javax.print.Doc;
import java.util.List;

@RestController
@RequestMapping(value = "documento")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin
public class DocWS
{

        @Autowired
        private static DocREPO docREPO;
        public DocWS(DocREPO docREPO)
        {
            DocWS.docREPO = docREPO;
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
        public static  Documento porDefecto(@RequestHeader HttpHeaders headers)
        {
            Documento docRTA = null;
            Documento docDB = docREPO.getById(1);

            Operador operadorLogeado = LoginWS.dameOperadorLogeado(headers);
            if(operadorLogeado != null)
            {
                if(docDB == null)
                {
                    docDB = new Documento("default.jpg" , operadorLogeado.getInstalacion());
                    docDB = docREPO.save(docDB);
                }

                docRTA = docDB;
            }


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

        @DeleteMapping(value = "/{idDocumento}")
        @Operation(
                summary = "Delete un documento por ID",
                security = @SecurityRequirement(name = "bearerAuth")
        )
        public static Documento deleteDocumento(@PathVariable() int idDocumento , @RequestHeader HttpHeaders headers)
        {
            Documento documentoDB = null;

            if (idDocumento != -1)
            {
                documentoDB = docREPO.getByIDN(idDocumento);

                if( documentoDB !=  null)
                {
                    documentoDB.setActivo(false);
                    documentoDB = docREPO.save(documentoDB);
                }
            }

            return documentoDB;
        }
//      EL SAVE SE HACE POR UPLOAD ONLY:
//    @PostMapping(name = "/")
//    public Foto save(Foto foto)
//    {
//        return fotoRepo.save(foto);
//    }
}
