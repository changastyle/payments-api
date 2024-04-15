package com.vd.payments.MODELO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "det_factura") @Data
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DetFactura
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private double cantidad;
    @OneToOne() @JoinColumn(name = "fkProducto")
    private Producto producto;
    private double importeDetalleFijo;
    @ManyToOne() @JoinColumn(name = "fkFactura") @JsonIgnore
    private Factura facturaPadre;
    private boolean activo;

    public DetFactura(double cantidad, Producto producto, double importeDetalleFijo, Factura facturaPadre, boolean activo)
    {
        this.cantidad = cantidad;
        this.producto = producto;
        this.importeDetalleFijo = importeDetalleFijo;
        this.facturaPadre = facturaPadre;
        this.activo = activo;
    }

    public double getCalcularPrecioDetalle()
    {
        double rta = 0;
        double totalCalculado = 0;

        totalCalculado = cantidad * producto.getPrecio();

        if(importeDetalleFijo > totalCalculado)
        {
            rta = importeDetalleFijo;
        }
        else
        {
            rta = totalCalculado;
        }

        rta = rta * cantidad;

        return rta;
    }
}
