package com.vd.payments.MODELO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "empresas" )
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Empresa
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nombre;

    @ManyToOne() @JoinColumn(name = "fkInstalacion") @JsonIgnore
    private Instalacion instalacion;

    @OneToOne() @JoinColumn(name = "fkLogo")
    private Documento logo;

//    @OneToMany(mappedBy = "empresa")
//    private List<Operador> arrEmpleados;
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

    public boolean operadorPertenece(Operador operador)
    {
        boolean pertenece = false;

        return pertenece;
    }
}
