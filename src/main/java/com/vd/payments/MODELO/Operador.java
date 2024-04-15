package com.vd.payments.MODELO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity @Table(name = "operadores")
@Data  @NoArgsConstructor @AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Operador  implements Comparable<Operador>
{
    //ATRIBUTOS:
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JsonIgnore
    private String pass;
    private String nombre;
    private String apellido;
    private String email;
    private String dni;

//    @ManyToOne() @JoinColumn(name = "fkInstalacion") @JsonIgnore
//    private Instalacion instalacion;
    @ManyToOne() @JoinColumn(name = "fkEmpresa") @JsonIgnore
    private Empresa empresa;
    @ManyToOne() @JoinColumn(name = "fkInstalacion") @JsonIgnore
    private Instalacion instalacion;

    private LocalDate fechaNacimiento;
    private LocalDate fechaCreacion;

    private String sexo;

    @OneToOne() @JoinColumn(name = "fkFotoPerfil")
    private Documento fotoPerfil;

    private boolean admin;
    private boolean activo;
    @Transient
    private boolean selected;

    public int getFKInstalacion()
    {
        int fkInstalacion = -1;

        Instalacion instalacionEmpresa = getInstalacion();

        if(instalacionEmpresa != null)
        {
            fkInstalacion = instalacionEmpresa.getId();
        }

        return fkInstalacion;
    }
//    @JsonIgnore
//    public Instalacion getInstalacion()
//    {
//        Instalacion instalacion = null;
//
//        if(empresa != null)
//        {
//            instalacion = empresa.getInstalacion();
//        }
//
//        return instalacion;
//    }
    public int getFKEmpresa()
    {
        int fkEmpresa = -1;

        if(empresa != null)
        {
            fkEmpresa = empresa.getId();
        }

        return fkEmpresa;
    }
    public int compareTo(Operador otro)
    {
        return 1;
    }

}
