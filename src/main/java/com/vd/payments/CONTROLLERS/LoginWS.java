package com.vd.payments.CONTROLLERS;

import com.google.gson.Gson;
import com.vd.payments.DelegatorAPI;
import com.vd.payments.XDTO.OperadorSaveDTO;
import com.vd.payments.MODELO.Foto;
import com.vd.payments.MODELO.Instalacion;
import com.vd.payments.MODELO.Operador;
import com.vd.payments.MODELO.enums.Sexo;
import com.vd.payments.XDTO.LoginParser;
import com.vd.payments.REPO.InstalacionRepo;
import com.vd.payments.REPO.OperadorREPO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

//import static com.google.common.hash.Hashing.hmacSha256;
//import static com.google.common.io.BaseEncoding.base64;

@RestController
@RequestMapping(value = "operador")
public class LoginWS
{
    private static OperadorREPO operadorREPO;
    private static InstalacionRepo instalacionRepo;
    private static final String secretkey = "qwerty";

    // AUTOWIRED A OPERADOR REPO PARA PODER USARLO EN METHODO ESTATICO:
    @Autowired
    public LoginWS(OperadorREPO operadorREPO, InstalacionRepo instalacionRepo)
    {
        LoginWS.operadorREPO = operadorREPO;
        LoginWS.instalacionRepo = instalacionRepo;
    }


    // WS LOGIN !!!!!!!!!!!!
    @PostMapping(value = "logearse")
    @CrossOrigin(origins= {"*"}, maxAge = 4800, allowCredentials = "false" )
    public String logearse
    (
            @RequestParam(value = "email" , required = true) String email,
            @RequestParam(value = "pass" , required = true) String pass
    )
    {
        String token = "";

        Operador operadorDB = null;

        System.out.println("|--------------------------------------------------------------------------|");
        System.out.println("    LOGEARSE:" + email + " - " + pass);
        List<Operador> arrOperadoresDB = operadorREPO.dameUsuarioPorEmailYPass(email, pass);

        for(Operador operadorLoop : arrOperadoresDB)
        {
            if(operadorLoop.isActivo())
            {
                Map<String , Object> payload = new HashMap<>();
                payload.put("email", String.valueOf(operadorLoop.getEmail()));
                payload.put("id", String.valueOf(operadorLoop.getId()));


                Instant issuedAt = Instant.now().truncatedTo(ChronoUnit.SECONDS);
                Instant expiration = issuedAt.plus(30, ChronoUnit.MINUTES);


                System.out.println("    EXPIRA EN: "+  Date.from(expiration)+ " - " + Date.from(expiration).getTime());

                try
                {
                    //The JWT signature algorithm we will be using to sign the token
                    token = Jwts.builder()
                            .setIssuedAt(Date.from(issuedAt))
                            .setExpiration(Date.from(expiration))
//                            .setSubject("admin")
//                            .setAudience("Solr")
                            .addClaims(payload)
                            .signWith(SignatureAlgorithm.HS256,secretkey.getBytes()).compact();

                    System.out.println("    jwtToken=" + token);
                }
                catch (Exception e)
                {
                    System.out.println(e.getMessage());
                }
            }
        }

        //  MAPPING TO DTO:
//        OperadorLogeadoDTO operadorLogeadoDTO = new OperadorLogeadoDTO(operadorDB);
        System.out.println("|--------------------------------------------------------------------------|");

        return token;
    }

    @PostMapping(value = "dameOperadorLogeado")
    @Operation(summary = "Devuelve el operador logeado" , security = @SecurityRequirement(name = "bearerAuth"))
    @CrossOrigin
    private static ResponseEntity<Operador> dameOperadorLogeadoWS(@RequestHeader HttpHeaders headers)
    {
        ResponseEntity responseEntity = new ResponseEntity(HttpStatus.NOT_FOUND);

        Operador operadorLogedoDB = dameOperadorLogeado(headers);

        if(operadorLogedoDB  != null)
        {
            responseEntity = new ResponseEntity(operadorLogedoDB , HttpStatus.OK);
        }

        return responseEntity;
    }
//    public static Operador dameOperadorLogeado(@RequestHeader HttpHeaders headers)
//    public static Operador dameOperadorLogeado(String token)
//    {
//        System.out.println("|----------------------------- COMPROBANDO OPERADOR LOGEADO TOKEN ----------------------|");
//
//        Operador operadorLogedoDB = null;
//
//        if(DelegatorAPI.MODO_DEV)
//        {
//            operadorLogedoDB = getOperadorByID(1);
//        }
//        else
//        {
//            operadorLogedoDB = getOperadorFromTokenReal(token);
//        }
//        return operadorLogedoDB;
//    }
    public static Operador dameOperadorLogeado(HttpHeaders headers)
    {
        System.out.println("|----------------------------- COMPROBANDO OPERADOR LOGEADO ----------------------|");

        Operador operadorLogedoDB = null;

        if(DelegatorAPI.MODO_DEV)
        {
            operadorLogedoDB = getOperadorByID(1);
        }
        else
        {
            operadorLogedoDB = getOperadorFromToken(headers);
        }

        return operadorLogedoDB;
    }

    public static Operador getOperadorFromTokenReal(String token)
    {
        Operador operadorDB = null;

        System.out.println("    TOKEEEEN:" + token);

        LoginParser operadorParser = jwtDecode(token);

        if (operadorParser != null)
        {
            String idStr = operadorParser.getId();
            String emailToken = operadorParser.getEmail();
            Date fechaExpiracion = operadorParser.getFechaExp();

            System.out.println("|---------------------------------|");
            System.out.println("        FECHA EXPIRECION: " + fechaExpiracion);
            System.out.println("|---------------------------------|");

            if (idStr != null)
            {
                int id = Integer.parseInt(idStr);

                if (id != -1 && id != 0)
                {
                    // 1 - COMPRUEBO QUE EL ID Y EL EMAIL DEL USUARIO SEAN EL MISMO:
                    if (emailToken != null && id != -1)
                    {
                        Operador operadorAux = getOperadorByID(id);

                        if (operadorAux != null)
                        {
                            // 2 - TENGO EL USUARIO POR ID Y VERIFICO QUE EL EMAIL SEA EL MISMO:
                            if (operadorAux.getEmail().equalsIgnoreCase(emailToken))
                            {
                                operadorDB = operadorAux;
                            }
                        }
                    }
                }
            }
        }


        return operadorDB;
    }
    public static Operador getOperadorFromToken(HttpHeaders headers)
    {
        Operador operadorDB = null;


        if(headers != null)
        {
            if(headers.size() > 0)
            {
                List<String> arrStrTokens = headers.get("authorization");
                System.out.println("HEADERS:" + headers.toString());



                if (arrStrTokens != null)
                {
                    if (arrStrTokens.size() > 0)
                    {
                        String token = arrStrTokens.get(0);

                        String bearer = "Bearer ";
                        if(token.startsWith(bearer))
                        {
                            token = token.substring(bearer.length() , token.length());
                        }

                        System.out.println("    TOKEEEEN:" + token);


                        LoginParser operadorParser = jwtDecode(token);

                        if (operadorParser != null)
                        {
                            String idStr = operadorParser.getId();
                            String emailToken = operadorParser.getEmail();
                            Date fechaExpiracion = operadorParser.getFechaExp();

                            System.out.println("|---------------------------------|");
                            System.out.println("        FECHA EXPIRECION: " + fechaExpiracion);
                            System.out.println("|---------------------------------|");

                            if (idStr != null)
                            {
                                int id = Integer.parseInt(idStr);

                                if (id != -1 && id != 0)
                                {
                                    // 1 - COMPRUEBO QUE EL ID Y EL EMAIL DEL USUARIO SEAN EL MISMO:
                                    if (emailToken != null && id != -1)
                                    {
                                        Operador operadorAux = getOperadorByID(id);

                                        if (operadorAux != null)
                                        {
                                            // 2 - TENGO EL USUARIO POR ID Y VERIFICO QUE EL EMAIL SEA EL MISMO:
                                            if (operadorAux.getEmail().equalsIgnoreCase(emailToken))
                                            {
                                                operadorDB = operadorAux;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        return operadorDB;
    }

    private static LoginParser jwtDecode
            (
                    @RequestParam(value = "token" , required = true) String token
            )
    {
        String payload ="";
        Operador operadorDB = null;

        if(token.contains("Authorization:") || token.contains("Bearer"))
        {
            List<String> arrTokenParts = Arrays.asList(token.split(" "));
            for(String strTokenLoop: arrTokenParts)
            {
                System.out.println("TOKEN PARTS:" + strTokenLoop);
            }

            if(arrTokenParts.size() > 0)
            {
                token = arrTokenParts.get(arrTokenParts.size()-1);
            }
        }
        System.out.println("TOKEN: "+ token);
        Base64.Decoder decoder = Base64.getUrlDecoder();

        String[] chunks = token.split("\\.");
        List<String> arrChunks = Arrays.asList(chunks);
        for(String chunkLoop : arrChunks)
        {
            System.out.println("CHUNK: " + chunkLoop);
        }
        System.out.println("    DECODE - chunks: " +  chunks.length);

        if(chunks.length > 1)
        {
            String header = new String(decoder.decode(chunks[0]));
            payload = new String(decoder.decode(chunks[1]));

            System.out.println("    DECODE: " +  payload);
        }

        LoginParser loginParser = new Gson().fromJson(payload, LoginParser.class);
        System.out.println("    EXP:" + loginParser.getFechaExp() + " - " + loginParser.getExp());

        System.out.println("|--------------------------------------------------------------------------|");


        return loginParser;
    }

















    @GetMapping(value = "/")
    @CrossOrigin
    public List<Operador> findOperadors
    (
            @RequestHeader HttpHeaders headers
    )
    {
        List<Operador> arrOperadores = new ArrayList<Operador>();

        Operador operadorLogeado = null ;
//        Operador operadorLogeado = dameOperadorLogeado(headers) ;

        if(operadorLogeado != null)
        {
            int fkInstalacion = operadorLogeado.getFKConsultorioAsociado();

            if(fkInstalacion != -1)
            {
                operadorREPO.findOperadorPorInstalacion(1);

                arrOperadores = operadorREPO.findAll();
                Collections.sort(arrOperadores);
            }
        }



        return arrOperadores;
    }
    @GetMapping(value = "/{id}")
    @CrossOrigin
    public static Operador getOperadorByID(@PathVariable("id") int id)
    {
        return operadorREPO.getById(id);
    }


    @PostMapping("save")
    public Operador save(@RequestBody OperadorSaveDTO operadorSaveDTO)
    {
        Operador operadorNew = null;

        if(operadorSaveDTO != null)
        {
            operadorNew = (Operador) operadorSaveDTO.toEntity(Operador.class);

            int fkInstalacion = operadorSaveDTO.getFkInstalacion();
            if(fkInstalacion != -1)
            {
                Instalacion instalacionDB = instalacionRepo.getById(fkInstalacion);

                if(instalacionDB != null)
                {
                    operadorNew.setInstalacion(instalacionDB);

                    Foto fotoDefault = FotosWS.porDefecto();
                    operadorNew.setFotoPerfil(fotoDefault);

                    String strSexo = "Femenino";
                    if(operadorSaveDTO.getSexo() == Sexo.Masculino)
                    {
                        strSexo = "Masculino";
                    }

                    operadorNew.setSexo(strSexo);
                    operadorNew.setFechaCreacion(LocalDate.now());
                    operadorNew.setActivo(true);
                    operadorNew = operadorREPO.save(operadorNew);
                }
            }
        }

        return operadorNew;
    }



}
