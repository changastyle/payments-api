package com.vd.payments.MODELO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vd.payments.UTIL.serializer.MasterUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "cambios_estado")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CambioEstado
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne() @JoinColumn(name = "fkFactura") @JsonIgnore
    private Factura factura;

    private LocalDateTime fecha;

    @OneToOne() @JoinColumn(name = "fkResponsable")
    private Operador responsable;

    @OneToOne() @JoinColumn(name = "fkEstadoAnterior") @JsonIgnore
    private EstadoPosible estadoAnterior;

    @OneToOne() @JoinColumn(name = "fkEstadoActual") @JsonIgnore
    private EstadoPosible estadoActual;


    public CambioEstado(Factura factura, Operador responsable, EstadoPosible estadoAnterior, EstadoPosible estadoActual)
    {
        this.factura = factura;
        this.fecha = fecha;
        this.responsable = responsable;

        this.estadoAnterior = estadoActual;
        this.estadoActual = estadoActual;

        this.fecha = LocalDateTime.now();
    }
    public String getNombreEstadoAnterior()
    {
        String nombreEstadoAnt = "";

        if(estadoAnterior != null)
        {
            nombreEstadoAnt = estadoAnterior.getNombre();
        }

        return nombreEstadoAnt;
    }
    public String getNombreEstadoActual()
    {
        String nombreEstadoAct = "";

        if(estadoActual != null)
        {
            nombreEstadoAct = estadoActual.getNombre();
        }

        return nombreEstadoAct;
    }
    public String getColorEstadoActual()
    {
        String colorEstadoActual = "";

        if(estadoActual != null)
        {
            colorEstadoActual = estadoActual.getColor();
        }

        return colorEstadoActual;
    }
    public String getIconoEstadoActual()
    {
        String iconoEstadoActual = "";

        if(estadoActual != null)
        {
            iconoEstadoActual = estadoActual.getIcono();
        }

        return iconoEstadoActual;
    }

    public String getFullFechaBonita()
    {
        return MasterUtil.formatearFechaBonita(fecha, true , false);
    }
    public String getOnlyFechaBonita()
    {
        return MasterUtil.formatearOnlyFechaOrHoraBonita(fecha, false );
    }
    public String getOnlyHoraBonita()
    {
        return MasterUtil.formatearOnlyFechaOrHoraBonita(fecha, true );
    }

}
