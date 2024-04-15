package com.vd.payments.CONTROLLERS;

import com.vd.payments.MODELO.*;
import com.vd.payments.REPO.*;
import com.vd.payments.XCP.NotFoundExc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class EmpresaWSTest
{
    @Autowired
    OperadorREPO operadorREPO;
    @Autowired
    EmpresaRepo empresaREPO;
    @Autowired
    InstalacionRepo instalacionRepo;
//    RelEmpresaEmpleadoREPO relEmpresaEmpleadoREPO;
    @Autowired
    DocREPO docREPO;
    @Autowired
    EmpresaWS empresaWS;
    HttpHeaders headers;

    String emailColaborador = "nico.grossi4@gmail.com";
    String emailColaborador2 = "mrubino@gmail.com";
    String passColaborador = "lupita";

    Operador operador;
    Operador operador2;
    @BeforeEach
    void setUp()
    {
//        Documento documento = new Documento(1, "default.png", "default.png",true);
//        docREPO.save(documento);
//
//        // 1 - INSTALACION 1:
//        Instalacion instalacion = new Instalacion(1 , "VD" , DocWS.porDefecto(),"payments.viewdevs.com.ar",new ArrayList<>(),true);
//        operador = new Operador(1 , passColaborador , "Nicolas" , "Grossi" , emailColaborador , "" , null, LocalDate.now() , LocalDate.now(),"M", DocWS.porDefecto(),false,true);
//        instalacion.addOperador(operador);
//        instalacionRepo.save(instalacion);
//
//        // 2 - INSTALACION 2:
//        Instalacion instalacion2 = new Instalacion(2 , "BC" , DocWS.porDefecto(),"bc.com.ar",new ArrayList<>(),true);
//        operador2 = new Operador(2 , passColaborador , "Martin" , "Rubino" , emailColaborador2 , "" , null, LocalDate.now() , LocalDate.now(),"M", DocWS.porDefecto(),false,true);
//        instalacion2.addOperador(operador2);
//        instalacionRepo.save(instalacion2);
//
//
//        Empresa empresa1 = new Empresa(1 , "CEM" , instalacion, true);
//        Empresa empresa2 = new Empresa(2 , "SEAS" , instalacion , true);
//        empresaREPO.save(empresa1);
//        empresaREPO.save(empresa2);
//
////        RelEmpresaEmpleado rel1 = new RelEmpresaEmpleado(-1 , empresa1 , operador, true);
////        RelEmpresaEmpleado rel2 = new RelEmpresaEmpleado(-1 , empresa2 , operador2, true);
////        relEmpresaEmpleadoREPO.save(rel1);
////        relEmpresaEmpleadoREPO.save(rel2);
//
//        String jwt = LoginWS.logearse(emailColaborador,passColaborador);
//        headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer " + jwt);

    }

    @Test
    public void addColaboradorEmail()
    {
        Empresa empresa = empresaREPO.getByIDN(1);
        Operador colaborador = operadorREPO.getOperadorByEmail(emailColaborador);

        if(empresa != null && colaborador != null)
        {
            int fkEmpresa = empresa.getId();
            int fkColaborador = colaborador.getId();
            System.out.println("COLABORADOR:" + fkColaborador );
            System.out.println("EMPRESA:" +  fkEmpresa);

//            RelEmpresaEmpleado relTest = empresaWS.addColaboradorEmail(fkEmpresa,emailColaborador,headers);
//            System.out.println("ADD - relTest:" +  relTest.getId() + " - ESTADO: "+ relTest.isActivo() );

//            assertTrue(relTest.isActivo());
        }

    }
    @Test
    public void rmColaboradorEmail()
    {
        Empresa empresa = empresaREPO.getByIDN(1);
        Operador colaborador = operadorREPO.getOperadorByEmail(emailColaborador);

        if(empresa != null && colaborador != null)
        {
            int fkEmpresa = empresa.getId();
            int fkColaborador = colaborador.getId();
            System.out.println("COLABORADOR:" + fkColaborador );
            System.out.println("EMPRESA:" +  fkEmpresa);

//            RelEmpresaEmpleado relTest = empresaWS.rmColaboradorEmail(fkEmpresa,emailColaborador,headers);
//            System.out.println("RM - relTest:" +  relTest.getId() + " - ESTADO: "+ relTest.isActivo() );

//            assertTrue(!relTest.isActivo());
        }

    }
    @Test()
    public void checkAddOperadorFromOtherEmpresa()
    {
        String mensajeEsperado = "Empresa no puede ser editada por un operador externo";

        Empresa empresa = empresaREPO.getByIDN(1);

        Operador operadorX = operador2;
//        Operador operadorX = operador;

        if(empresa != null && operadorX != null)
        {
            int fkEmpresa = empresa.getId();
            int fkColaborador = operadorX.getId();
            String emailColaboradorX = operadorX.getEmail();
            System.out.println("COLABORADOR:" + fkColaborador  + " - " + emailColaboradorX);
            System.out.println("EMPRESA:" +  fkEmpresa + " - " + empresa.getNombre());

            Exception exception = assertThrows(NotFoundExc.class, () -> {
                // Llama al método que lanza la excepción
//                RelEmpresaEmpleado relTest = empresaWS.addColaboradorEmail(fkEmpresa,emailColaboradorX,headers);

//                if(relTest != null)
//                {
//                    int fkRel = relTest.getId();
//                    System.out.println("ADD COLABORADOR DE OTRA EMPRESA - relTest:" + fkRel + " - ESTADO: " + relTest.isActivo());
//
//                    if (fkRel != 1)
//                    {
//                        fail("ERROR: REL DEBE VOLVER CON ID 1");
//                    }
//
//                    if(!relTest.isActivo())
//                    {
//                        fail("ERROR: REL DEBE ESTAR ACTIVA");
//                    }
//                }
//                else
//                {
//                    fail("REL TEST == NULL");
//                }
            });

            String mesnajeRecibido = exception.getMessage();
            System.out.println("MENSAJE RECIBIDO:" + mesnajeRecibido);
            assertEquals(mensajeEsperado, mesnajeRecibido);


        }
        else
        {
            if(empresa != null )
            {
                fail("empresa  == NULL");
            }
            if(operador2 != null )
            {
                fail("colaborador2  == NULL");
            }
        }

    }
}