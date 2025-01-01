package com.vd.payments.CONTROLLERS;

import com.google.gson.Gson;
import com.vd.payments.MODELO.*;
import com.vd.payments.PaymentsAPI;
import com.vd.payments.REPO.EmpresaRepo;
import com.vd.payments.XCP.NoInstalacionExc;
import com.vd.payments.XCP.NotFoundExc;
import com.vd.payments.XCP.NotHeaderExc;
import com.vd.payments.XCP.NoLogeadoExc;
import com.vd.payments.XDTO.OperadorSaveDTO;
import com.vd.payments.MODELO.enums.Sexo;
import com.vd.payments.XDTO.LoginParser;
import com.vd.payments.REPO.InstalacionRepo;
import com.vd.payments.REPO.OperadorREPO;
import com.vd.payments.XDTO.TuplaEmpresaRolDTO;
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
import java.time.LocalDateTime;
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
    private static EmpresaWS empresaWS;
    private static MenuWS menuWS;
    private static final String secretkey = "qwerty";

    // AUTOWIRED A OPERADOR REPO PARA PODER USARLO EN METHODO ESTATICO:
    @Autowired
    public LoginWS(OperadorREPO operadorREPO, InstalacionRepo instalacionRepo, EmpresaWS empresaWS, MenuWS menuWS)
    {
        LoginWS.operadorREPO = operadorREPO;
        LoginWS.instalacionRepo = instalacionRepo;
        LoginWS.empresaWS = empresaWS;
        LoginWS.menuWS = menuWS;
    }


    // WS LOGIN !!!!!!!!!!!!
    @PostMapping(value = "logearse")
    @Operation(summary = "Retrieve a JWT from an email and password of an active User")
    @CrossOrigin(origins = {"*"}, maxAge = 4800, allowCredentials = "false")
    public static String logearse
    (
            @RequestParam(value = "email", required = true) String email,
            @RequestParam(value = "pass", required = true) String pass
    )
    {
        String token = "";

        Operador operadorDB = null;

        System.out.println("|--------------------------------------------------------------------------|");
        System.out.println("    LOGEARSE:" + email + " - " + pass);
        operadorDB = operadorREPO.getUsuarioPorEmailYPass(email, pass);

        if (operadorDB != null)
        {
            if (operadorDB.isActivo())
            {
                Map<String, Object> payload = new HashMap<>();
                payload.put("email", String.valueOf(operadorDB.getEmail()));
                payload.put("id", String.valueOf(operadorDB.getId()));


                Instant issuedAt = Instant.now().truncatedTo(ChronoUnit.SECONDS);
                Instant expiration = issuedAt.plus(30, ChronoUnit.MINUTES);


                System.out.println("    EXPIRA EN: " + Date.from(expiration) + " - " + Date.from(expiration).getTime());

                try
                {
                    //The JWT signature algorithm we will be using to sign the token
                    token = Jwts.builder()
                            .setIssuedAt(Date.from(issuedAt))
                            .setExpiration(Date.from(expiration))
                            //                            .setSubject("admin")
                            //                            .setAudience("Solr")
                            .addClaims(payload)
                            .signWith(SignatureAlgorithm.HS256, secretkey.getBytes()).compact();

                    System.out.println("    jwtToken=" + token);
                } catch (Exception e)
                {
                    System.out.println(e.getMessage());
                }
            } else
            {
                throw new NotFoundExc("EL USUARIO NO SE ENCUENTRA ACTIVO");
            }
        } else
        {
            throw new NotFoundExc("NO EXISTE UN OPERADOR PARA TAL USUARIO Y PASS");
        }

        //  MAPPING TO DTO:
//        OperadorLogeadoDTO operadorLogeadoDTO = new OperadorLogeadoDTO(operadorDB);
        System.out.println("|--------------------------------------------------------------------------|");

        return token;
    }

    @PostMapping(value = "dameOperadorLogeado")
    @Operation(summary = "Devuelve el operador logeado", security = @SecurityRequirement(name = "bearerAuth"))
    @CrossOrigin
    public static Operador dameOperadorLogeado(@RequestHeader HttpHeaders headers) throws NotHeaderExc
    {
        if (PaymentsAPI.DEBUG)
        {
            System.out.println("|----------------------------- COMPROBANDO OPERADOR LOGEADO ----------------------|");
        }

        Operador operadorLogedoDB = null;

        operadorLogedoDB = getOperadorFromToken(headers);

        if (operadorLogedoDB == null)
        {
            if (PaymentsAPI.MODO_DEV)
            {
                operadorLogedoDB = getOperadorByID(headers, 8);
//                operadorLogedoDB = getOperadorByID(headers, 1);
            }
        }

        return operadorLogedoDB;
    }

    @PostMapping(value = "dameTuplaOperadorLogeado")
    @Operation(summary = "Devuelve la empresa a la que pertenece el operador logeado", security = @SecurityRequirement(name = "bearerAuth"))
    @CrossOrigin
    public static TuplaEmpresaRolDTO dameTuplaOperadorLogeado(@RequestHeader HttpHeaders headers) throws NotHeaderExc
    {
        TuplaEmpresaRolDTO tupla = null;
        Operador operadorLogedo = dameOperadorLogeado(headers);
        List<Empresa> arrEmpresas = new ArrayList<>();
        List<Menu> arrMenus = new ArrayList<>();

        if (operadorLogedo != null)
        {
            if (operadorLogedo.isAdmin())
            {
                // SI EL OPERADOR ES ADMIN:
                Instalacion instalacion = operadorLogedo.getInstalacion();
                if (instalacion != null)
                {
                    int fkInstalacionAdmin = instalacion.getId();

                    if (fkInstalacionAdmin != -1)
                    {
                        arrEmpresas = empresaWS.findAllEmpresasFromInstalacion(fkInstalacionAdmin);
                    }

                    arrMenus = menuWS.findAllByInstalacionAndAdminLevel(fkInstalacionAdmin, operadorLogedo.isAdmin());
                }
            } else
            {
                // OPERADOR NO ES ADMIN, ES COMUN:
                Empresa empresa = operadorLogedo.getEmpresa();
                if (empresa != null)
                {
                    arrEmpresas.add(empresa);
                }
            }

            Instalacion instalacion = operadorLogedo.getInstalacion();

            if (instalacion != null)
            {
                tupla = new TuplaEmpresaRolDTO(operadorLogedo, instalacion, arrEmpresas, arrMenus);
                int fkInstalacion = instalacion.getId();
                arrMenus = menuWS.findAllByInstalacionAndAdminLevel(fkInstalacion, operadorLogedo.isAdmin());
            }

        }


        return tupla;
    }

    public static Operador getOperadorFromToken(HttpHeaders headers) throws NotHeaderExc
    {
        Operador operadorDB = null;
        LoginParser operadorParser = null;
        boolean tengoOperadorEnHeaders = false;


        // 1 - VERIFICO QUE VENGAN LOS HEADERS CON EL AUTHORIZATION Y EL BEARER:
        if (headers != null)
        {
            if (headers.size() > 0)
            {
                List<String> arrStrTokens = headers.get("authorization");

                if (PaymentsAPI.DEBUG)
                {
                    System.out.println("HEADERS:" + headers);
                }

                if (arrStrTokens != null)
                {
                    if (arrStrTokens.size() > 0)
                    {
                        String token = arrStrTokens.get(0);

                        String bearer = "Bearer ";
                        if (token.startsWith(bearer))
                        {
                            token = token.substring(bearer.length());

                        }

                        tengoOperadorEnHeaders = true;
                        if (PaymentsAPI.DEBUG)
                        {
                            System.out.println("    TOKEEEEN:" + token);
                        }

                        operadorParser = jwtDecode(token);
                    } else
                    {
                        throw new NotHeaderExc("HEADER AUTORIZATION NULL");
                    }
                } else
                {
                    if (!PaymentsAPI.MODO_DEV)
                    {
                        throw new NotHeaderExc("HEADER AUTORIZATION NULL");
                    }
                }
            } else
            {
                if (PaymentsAPI.MODO_DEV)
                {
                    tengoOperadorEnHeaders = false;
                } else
                {
                    throw new NotHeaderExc("HEADERS SIZE < 0");
                }
            }
        }


        // 2 - SI NO TENGO EL OPEADOR VERIFICO SI LA API ESTA EN MODO DEV PARA MOCKEAR UN USUARIO POR DEFECTO:
        if (!tengoOperadorEnHeaders)
        {
            if (PaymentsAPI.MODO_DEV)
            {
                operadorParser = new LoginParser("1", "nico.grossi4@gmail.com", LocalDateTime.now().getSecond());
//                operadorDB = getOperadorByID(headers,1);

            }
        }


        // 3 - VERIFICO QUE EL PARSER REALMENTE EXISTA EN DB POR SU EMAIL Y PASS Y LO DEVUELVO COMO EL OPERADOR LOGEADO:
        if (operadorParser != null)
        {
            String idStr = operadorParser.getId();
            String emailToken = operadorParser.getEmail();
            Date fechaExpiracion = operadorParser.getFechaExp();

            if (PaymentsAPI.DEBUG)
            {
                System.out.println("|---------------------------------|");
                System.out.println("        FECHA EXPIRECION: " + fechaExpiracion);
                System.out.println("|---------------------------------|");
            }

            if (idStr != null)
            {
                int id = Integer.parseInt(idStr);

                if (id != -1 && id != 0)
                {
                    // 1 - COMPRUEBO QUE EL ID Y EL EMAIL DEL USUARIO SEAN EL MISMO:
                    if (emailToken != null && id != -1)
                    {
                        Operador operadorAux = getOperadorByID(headers, id);

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

    private static LoginParser jwtDecode
            (
                    @RequestParam(value = "token", required = true) String token
            )
    {
        String payload = "";
        Operador operadorDB = null;

        if (token.contains("Authorization:") || token.contains("Bearer"))
        {
            List<String> arrTokenParts = Arrays.asList(token.split(" "));
            for (String strTokenLoop : arrTokenParts)
            {
                System.out.println("TOKEN PARTS:" + strTokenLoop);
            }

            if (arrTokenParts.size() > 0)
            {
                token = arrTokenParts.get(arrTokenParts.size() - 1);
            }
        }
        if (PaymentsAPI.DEBUG)
        {
            System.out.println("TOKEN: " + token);
        }
        Base64.Decoder decoder = Base64.getUrlDecoder();

        String[] chunks = token.split("\\.");
        String[] arrChunks = chunks;
        for (String chunkLoop : arrChunks)
        {
            if (PaymentsAPI.DEBUG)
            {
                System.out.println("CHUNK: " + chunkLoop);
            }
        }
        if (PaymentsAPI.DEBUG)
        {
            System.out.println("    DECODE - chunks: " + chunks.length);
        }

        if (chunks.length > 1)
        {
            String header = new String(decoder.decode(chunks[0]));
            payload = new String(decoder.decode(chunks[1]));

            if (PaymentsAPI.DEBUG)
            {
                System.out.println("    DECODE: " + payload);
            }
        }

        LoginParser loginParser = new Gson().fromJson(payload, LoginParser.class);
        if (PaymentsAPI.DEBUG)
        {
            System.out.println("    EXP:" + loginParser.getFechaExp() + " - " + loginParser.getExp());

            System.out.println("|--------------------------------------------------------------------------|");
        }


        return loginParser;
    }

    @PostMapping(value = "getFKInstalacionFromOperadorLogeado")
    @Operation(summary = "Devuelve el FK-INSTALACION del operador logeado", security = @SecurityRequirement(name = "bearerAuth"))
    @CrossOrigin
    public static int getFKInstalacionOperadorLogeado(@RequestHeader HttpHeaders headers) throws NotHeaderExc
    {
        int fkInstalacion = -1;

        System.out.println("|----------------------------- GETTING FK INSTALACION FROM OPERADOR LOGEADO ----------------------|");

        Operador operadorLogedoDB = dameOperadorLogeado(headers);

        if (operadorLogedoDB != null)
        {
            Instalacion instalacionOPeradorLogeado = operadorLogedoDB.getInstalacion();

            if (instalacionOPeradorLogeado != null)
            {
                fkInstalacion = instalacionOPeradorLogeado.getId();
            }

        }

        return fkInstalacion;
    }

    public static Instalacion getInstalacionOperadorLogeado(@RequestHeader HttpHeaders headers) throws NotHeaderExc
    {
        Instalacion instalacionRTA = null;

        System.out.println("|----------------------------- GETTING FK INSTALACION FROM OPERADOR LOGEADO ----------------------|");

        Operador operadorLogedoDB = dameOperadorLogeado(headers);

        if (operadorLogedoDB != null)
        {
            Instalacion instalacionOPeradorLogeado = operadorLogedoDB.getInstalacion();

            if (instalacionOPeradorLogeado != null)
            {
                instalacionRTA = instalacionOPeradorLogeado;
            }

        }

        return instalacionRTA;
    }


    public static boolean colaboradorPerteneceAEmpresa(Operador colaborador, Empresa empresa)
    {
        boolean pertenece = false;

        if (colaborador != null)
        {
            if (empresa != null)
            {
                Instalacion instalacionColaborador = colaborador.getInstalacion();
                Instalacion instalacionEmpresa = empresa.getInstalacion();

                if (instalacionColaborador != null && instalacionEmpresa != null)
                {
                    int fkInstalacionColaborador = instalacionColaborador.getId();
                    int fkInstalacionEmpresa = instalacionEmpresa.getId();

                    if (fkInstalacionColaborador == fkInstalacionEmpresa)
                    {
                        pertenece = true;
                    }
                }
            }
        }

        return pertenece;
    }


    @GetMapping(value = "/")
    @CrossOrigin
    @Operation(
            summary = "Listar todos los operadores de una Instalacion ",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public List<Operador> findOperadors
            (
                    @RequestHeader HttpHeaders headers
            )
    {
        List<Operador> arrOperadores = new ArrayList<Operador>();

        int fkInstalacion = getFKInstalacionOperadorLogeado(headers);

        if (fkInstalacion != -1)
        {
            arrOperadores = operadorREPO.findOperadorPorInstalacion(fkInstalacion);
            Collections.sort(arrOperadores);
        } else
        {
            throw new NoInstalacionExc("Instalacion no valida : " + fkInstalacion);
        }

        return arrOperadores;
    }

    @GetMapping(value = "/{id}")
    @CrossOrigin
    @Operation(
            summary = "Return user by ID",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public static Operador getOperadorByIDRest
            (
                    @RequestHeader HttpHeaders headers,
                    @PathVariable("id") int id
            )
    {
        Operador operadorRTA = null;

        Operador operadorLogeado = LoginWS.dameOperadorLogeado(headers);

        if (operadorLogeado != null)
        {
            Operador operadorAux = getOperadorByID(headers, id);

            if (operadorLogeado.getFKEmpresa() == operadorAux.getFKEmpresa())
            {
                operadorRTA = operadorAux;
            } else
            {
                throw new NotFoundExc("Operador with ID (" + id + ") NO ES DE TU EMPRESA");
            }
        }


        return operadorRTA;
    }

    private static Operador getOperadorByID(HttpHeaders headers, int id)
    {
        Operador operadorRTA = operadorREPO.getByIDN(id);

        if (operadorRTA == null)
        {
            throw new NotFoundExc("Operador with ID (" + id + ") NOT FOUND");
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

        if (operadorLogeado != null)
        {
            if (operadorSaveDTO != null)
            {
                operadorNew = (Operador) operadorSaveDTO.toEntity(Operador.class);

                Instalacion instalacion = operadorLogeado.getInstalacion();
                if (instalacion != null)
                {
                    int fkInstalacion = instalacion.getId();
                    if (fkInstalacion != -1)
                    {
                        Instalacion instalacionDB = instalacionRepo.getById(fkInstalacion);

                        if (instalacionDB != null)
                        {
//                            operadorNew.setInstalacion(instalacionDB);

                            Documento fotoDefault = DocWS.porDefecto(headers);
                            operadorNew.setFotoPerfil(fotoDefault);

                            String strSexo = "Femenino";
                            if (operadorSaveDTO.getSexo() == Sexo.Masculino)
                            {
                                strSexo = "Masculino";
                            }

                            operadorNew.setSexo(strSexo);
                            operadorNew.setFechaCreacion(LocalDate.now());
                            operadorNew.setActivo(true);
                            operadorNew = operadorREPO.save(operadorNew);
                        } else
                        {
                            throw new NoInstalacionExc("Instalacion DB = NULL");
                        }
                    }
                } else
                {
                    throw new NoInstalacionExc("Operador Logeado.Instalacion = NULL");
                }
            }
        } else
        {
            throw new NoLogeadoExc("NO hay operador logeado..");
        }

        return operadorNew;
    }


}
