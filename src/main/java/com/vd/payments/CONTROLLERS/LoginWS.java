package com.vd.payments.CONTROLLERS;

import com.google.gson.Gson;
import com.vd.payments.DelegatorAPI;
import com.vd.payments.EXCEPTIONS.NoInstalacionExec;
import com.vd.payments.EXCEPTIONS.NotFoundExc;
import com.vd.payments.EXCEPTIONS.NotHeaderExc;
import com.vd.payments.EXCEPTIONS.NoLogeadoExec;
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
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
    @Operation(summary = "Get JWT from email and password of active User")
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
    public static Operador dameOperadorLogeado(@RequestHeader HttpHeaders headers) throws NotHeaderExc
    {
        System.out.println("|----------------------------- COMPROBANDO OPERADOR LOGEADO ----------------------|");

        Operador operadorLogedoDB = null;

        operadorLogedoDB = getOperadorFromToken(headers);

        if(operadorLogedoDB == null)
        {
            if(DelegatorAPI.MODO_DEV)
            {
                operadorLogedoDB = getOperadorByID(headers,1);
            }
        }

        return operadorLogedoDB;
    }

    public static Operador getOperadorFromToken(HttpHeaders headers) throws NotHeaderExc
    {
        Operador operadorDB = null;

        if(!DelegatorAPI.MODO_DEV)
        {
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
                                            Operador operadorAux = getOperadorByID(headers , id);

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
                        else
                        {
                            throw new NotHeaderExc("HEADER AUTORIZATION NULL");
                        }
                    }
                    else
                    {
                        throw new NotHeaderExc("HEADER AUTORIZATION NULL");
                    }
                }
                else
                {
                    throw new NotHeaderExc("HEADERS SIZE < 0");
                }
            }
            else
            {
                throw new NotHeaderExc("ARR HEADERS == NULL");
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
    @Operation(
            summary = "List all the users of my company ",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public List<Operador> findOperadors
    (
            @RequestHeader HttpHeaders headers
    )
    {
        List<Operador> arrOperadores = new ArrayList<Operador>();

        Operador operadorLogeado = dameOperadorLogeado(headers) ;

        if(operadorLogeado != null)
        {
            int fkInstalacion = operadorLogeado.getFKConsultorioAsociado();

            if(fkInstalacion != -1)
            {
                arrOperadores = operadorREPO.findOperadorPorInstalacion(1);
                Collections.sort(arrOperadores);
            }
            else
            {
                throw new NoInstalacionExec("Instalacion no valida : " + fkInstalacion );
            }
        }
        else
        {
            throw new NoLogeadoExec("Operador no logeado");
        }

        return arrOperadores;
    }
    @GetMapping(value = "/{id}")
    @CrossOrigin
    @Operation(
            summary = "Return user by ID",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public static Operador getOperadorByID(
            @RequestHeader HttpHeaders headers,
            @PathVariable("id") int id)
    {
        Operador operadorRTA = null;
        Operador operadorLogeado = dameOperadorLogeado(headers) ;

        if(operadorLogeado != null)
        {
            int fkInstalacion = operadorLogeado.getFKConsultorioAsociado();

            if(fkInstalacion != -1)
            {
                operadorRTA = operadorREPO.getById(id);
            }
            else
            {
                throw new NoInstalacionExec("Instalacion no valida : " + fkInstalacion );
            }
        }
        else
        {
            throw new NoLogeadoExec("Operador no logeado");
        }

        if(operadorRTA == null)
        {
            throw new NotFoundExc("Operador with ID (" +  id + ") NOT FOUND");
        }

        return operadorRTA;
    }


    @PostMapping("create")
    @Operation(
            summary = "Create new Usuario",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public Operador create
    (
        @RequestBody @Valid OperadorSaveDTO operadorSaveDTO,
        @RequestHeader HttpHeaders headers
    )
    {
        Operador operadorNew = null;
        Operador operadorLogeado = dameOperadorLogeado(headers);

        if(operadorLogeado != null)
        {
            if(operadorSaveDTO != null)
            {
                operadorNew = (Operador) operadorSaveDTO.toEntity(Operador.class);

                Instalacion instalacion = operadorLogeado.getInstalacion();
                if(instalacion != null)
                {
                    int fkInstalacion = instalacion.getId();
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
                        else
                        {
                            throw new NoInstalacionExec("Instalacion DB = NULL");
                        }
                    }
                }
                else
                {
                    throw new NoInstalacionExec("Operador Logeado.Instalacion = NULL");
                }
            }
        }
        else
        {
            throw new NoLogeadoExec("NO hay operador logeado..");
        }

        return operadorNew;
    }



}
