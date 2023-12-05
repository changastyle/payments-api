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

    @ManyToOne() @JoinColumn(name = "fkInstalacion") @JsonIgnore
    private Instalacion instalacion;

    private LocalDate fechaNacimiento;
    private LocalDate fechaCreacion;

    private String sexo;

    @OneToOne() @JoinColumn(name = "fkFotoPerfil")
    private Documento fotoPerfil;

    private boolean activo;

    public int getFKConsultorioAsociado()
    {
        int fkConsultorioASociado = -1;

        if(instalacion != null)
        {
            fkConsultorioASociado = instalacion.getId();
        }

        return fkConsultorioASociado;
    }
    public int compareTo(Operador otro)
    {
        return 1;
    }

}
