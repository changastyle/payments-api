package com.vd.payments.MODELO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "menus") @Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Menu implements Comparable<Menu>
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nombre;
    private String url;
    private String icono;
    @OneToOne() @JoinColumn(name = "fkInstalacion")
    private Instalacion instalacion;
    @Column(columnDefinition = "INT DEFAULT 999")
    private int orden = 999;
    @Column(columnDefinition = "Boolean DEFAULT false")
    private boolean requiereAdmin = false;
    private boolean activo;
    @Transient
    private boolean editandoNombreFast ;
    @Transient
    private boolean editandoUrlFast ;
    @Transient
    private boolean editandoIconoFast ;
    @Transient
    private boolean editandoOrdenFast ;

    public void update(String nombre , String url , String icono , int orden , boolean requiereAdmin, boolean activo)
    {
        this.nombre = nombre;
        this.url = url;
        this.icono = icono;
        this.orden = orden;
        this.requiereAdmin = requiereAdmin;
        this.activo = activo;
    }

    @Override
    public int compareTo(Menu o)
    {
        if(this.orden > o.orden)
        {
            return 1;
        }
        else
        {
            return -1;
        }
    }
}
