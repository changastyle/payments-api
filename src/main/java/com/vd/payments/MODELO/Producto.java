package com.vd.payments.MODELO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Producto
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nombre;
    private double precioSugerido;

    @OneToOne() @JoinColumn(name = "fkFoto")
    private Documento foto;

    @ManyToOne() @JoinColumn(name = "fkInstalacion") @JsonIgnore
    private Instalacion instalacion;
    private boolean activo;


    public int getFkInstalacion()
    {
        int fkInstalacion = -1;

        if(instalacion != null)
        {
            fkInstalacion = instalacion.getId();
        }

        return fkInstalacion;
    }
}
