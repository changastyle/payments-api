package com.vd.payments.MODELO;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.*;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Entity
@Table(name = "instalaciones")
@Data
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Instalacion implements Comparable<Instalacion>
{
    //ATRIBUTOS:
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nombre;
    private String seqFact;

    @OneToOne()
    @JoinColumn(name = "fkLogo")
    private Documento logo;
    private String urlWEB;
    private String cbu;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "instalacion")
    @JsonIgnore
    private List<Empresa> arrEmpresas;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "instalacion")
    @JsonIgnore
    private List<Producto> arrProductos;

//    @OneToMany(cascade = CascadeType.ALL,mappedBy = "instalacion") @JsonIgnore
//    private List<Operador> arrOperadores;


//    @OneToMany(cascade = CascadeType.PERSIST,mappedBy = "instalacion") @JsonIgnore
//    private List<Cliente> arrClientes;

    boolean activo;

    public Instalacion()
    {
        logo = new Documento("default.jpg", this);
//        arrOperadores = new ArrayList<>();
//        arrProductos = new ArrayList<>();
    }

    public int compareTo(Instalacion otro)
    {
        return 1;
    }

    //    public void addOperador(Operador operador)
//    {
//        if(arrOperadores != null)
//        {
//            arrOperadores = new ArrayList<>();
//        }
//
//        if(operador != null)
//        {
////            operador.setInstalacion(this);
//            arrOperadores.add(operador);
//        }
//    }
    public int getSeqFactInt()
    {
        int rta = -1;

        if (seqFact != null)
        {
            rta = Integer.parseInt(seqFact);
        }
        return rta;
    }

    @Override
    public int hashCode()
    {
//        return Objects.hash(thi, instalacion.getNombre());
        return Objects.hash(this.getNombre());
    }

}
