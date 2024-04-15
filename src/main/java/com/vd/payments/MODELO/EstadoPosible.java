package com.vd.payments.MODELO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "estados_posibles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class EstadoPosible
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nombre;
    private String nombreCamino;
    private String color;
    private String icono;

    @OneToOne() @JoinColumn(name = "fkEstadoNegativo") @JsonIgnore
    private EstadoPosible estadoNegativo;

    @OneToOne() @JoinColumn(name = "fkEstadoProximoPositivo") @JsonIgnore
    private EstadoPosible estadoProximoPositivo;

    private boolean activo;

    public String getNombreEstadoNegativo()
    {
        String nombreEstadoAnt = "";
        if(estadoNegativo != null)
        {
            nombreEstadoAnt = estadoNegativo.getNombre();
        }
        return  nombreEstadoAnt;
    }
    public String getNombreEstadoProx()
    {
        String nombreEstadoAnt = "";
        if(estadoProximoPositivo != null)
        {
            nombreEstadoAnt = estadoProximoPositivo.getNombre();
        }
        return  nombreEstadoAnt;
    }

}
