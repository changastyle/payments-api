package com.vd.payments.MODELO;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity @Table(name = "suscripciones")
@Data @NoArgsConstructor @AllArgsConstructor
public class Suscripcion
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @OneToOne() @JoinColumn(name = "fkEmpresa")
    private Empresa empresa;
    @OneToOne() @JoinColumn(name = "fkProducto")
    private Producto producto;
    private boolean activo;
}
