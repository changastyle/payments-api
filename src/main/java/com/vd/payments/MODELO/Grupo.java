package com.vd.payments.MODELO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import jakarta.persistence.*;

@Entity @Table(name = "grupos") @Data @AllArgsConstructor  @Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Grupo implements Comparable<Grupo>
{
    //ATRIBUTOS:
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nombre;

    @ManyToOne() @JoinColumn(name = "fkInstalacion")
    private Instalacion instalacion;

    @OneToOne(cascade = CascadeType.PERSIST) @JoinColumn(name = "fkLogo")
    private Foto logo;
    boolean activo;
    public Grupo()
    {
        logo = new Foto("default.jpg");
    }

    public int compareTo(Grupo otro)
    {
        return 1;
    }

}
