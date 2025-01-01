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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
@Table(name = "facturas")
@Data
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Factura implements Comparable<Factura>
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int seqFact;
    @OneToOne()
    @JoinColumn(name = "fkEmpresa")
    private Empresa empresa;

    @OneToMany(mappedBy = "facturaPadre", cascade = CascadeType.ALL)
    private List<DetFactura> arrDetsFactura;

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<RelFacturaDocumento> arrRelsDocs;

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL)
    private List<CambioEstado> arrCambiosEstado;

    @ManyToOne()
    @JoinColumn(name = "fk_instalacion", nullable = false)
    @JsonIgnore
    private Instalacion instalacion;

    private LocalDateTime fechaEmision;
    private String mes;
    private String year;
    private boolean activo;

    public Factura()
    {
        arrDetsFactura = new ArrayList<>();
        arrRelsDocs = new ArrayList<>();
        arrCambiosEstado = new ArrayList<>();
    }

    public String getNombreCalculado()
    {
        String nombreCalculado = "";
        if (empresa != null)
        {
            nombreCalculado += empresa.getNombre() + " - " + getNombreProductoPrincipal() + " (" + mes + "/" + year + ")";
        }

        return nombreCalculado;
    }

    public void addDetFactura(DetFactura detFactura)
    {
        if (arrDetsFactura == null)
        {
            arrDetsFactura = new ArrayList<>();
        }

        arrDetsFactura.add(detFactura);
        detFactura.setFacturaPadre(this);
    }

    public void addCambioEstado(CambioEstado cambioEstado)
    {
        if (arrCambiosEstado == null)
        {
            arrCambiosEstado = new ArrayList<>();
        }

        arrCambiosEstado.add(cambioEstado);
        cambioEstado.setFactura(this);
    }

    public void addDocumento(Documento documento)
    {
        if (arrRelsDocs == null)
        {
            arrRelsDocs = new ArrayList<>();
        }

        LocalDateTime ya = LocalDateTime.now();
//        documento.setActivo(true);
        arrRelsDocs.add(new RelFacturaDocumento(-1, this, documento, ya));

    }

    public String getMes()
    {
        mes = MasterUtil.checkZeros(Integer.parseInt(mes));
        return mes;
    }

    public String getNombreProductoPrincipal()
    {
        String nombreProductoPrincipal = "";

        if (arrDetsFactura != null)
        {
            if (arrDetsFactura.size() > 0)
            {
                DetFactura detFacturaPrincipal = arrDetsFactura.get(0);

                if (detFacturaPrincipal != null)
                {
                    Producto productoPrincipal = detFacturaPrincipal.getProducto();
                    if (productoPrincipal != null)
                    {
                        nombreProductoPrincipal = productoPrincipal.getNombre();
                    }
                }

            }
        }

        return nombreProductoPrincipal;
    }

    public double getCalcularImporteFinal()
    {
        double importeFinal = 0;

        if (arrDetsFactura != null)
        {
            for (DetFactura detFacturaLoop : arrDetsFactura)
            {
                importeFinal += detFacturaLoop.getCalcularPrecioDetalle();
            }
        }

        return importeFinal;
    }

    public List<RelFacturaDocumento> getArrRelsDocs()
    {
        List<RelFacturaDocumento> arrAux = new ArrayList<>();

        arrAux = arrRelsDocs.stream().filter(relFacturaDocumento -> relFacturaDocumento.isActivo() && relFacturaDocumento.getDocumento() != null && relFacturaDocumento.getDocumento().isActivo()).collect(Collectors.toList());

        return arrAux;
    }

    public String getFechaEmisionBonita()
    {
        return MasterUtil.formatearFechaBonita(fechaEmision, true, false);
    }

    public String getOnlyFechaEmisionBonita()
    {
        return MasterUtil.formatearFechaBonita(fechaEmision, false, false);
    }

    public String getOnlyHoraEmisionBonita()
    {
        return MasterUtil.formatearFechaBonita(fechaEmision, false, false);
    }


    public CambioEstado getUltimoCambioEstadoFactura()
    {
        CambioEstado ultCambioEstado = null;

        if (arrCambiosEstado != null)
        {
            int ultIndex = arrCambiosEstado.size() - 1;

            ultCambioEstado = arrCambiosEstado.get(ultIndex);
        }
        return ultCambioEstado;
    }

    public String getCBUInstalacion()
    {
        String cbu_instalacion = "";

        if (instalacion != null)
        {
            cbu_instalacion = instalacion.getCbu();
        }

        return cbu_instalacion;
    }

    public List<Documento> getArrDocsCalc()
    {
        List<Documento> arrDocs = new ArrayList<>();

//        arrDocs = this.arrRelsDocs.stream().map(relFacturaDocumento -> {return relFacturaDocumento.getDocumento();}).collect(Collectors.toList());
        arrDocs = this.arrRelsDocs.stream()
                .map(RelFacturaDocumento::getDocumento)
                .filter(Objects::nonNull)
                .filter(documento -> documento.isActivo())// Filtra documentos nulos
                .collect(Collectors.toList());

        return arrDocs;
    }

    public int getCalcularCantCambiosEstado()
    {
        return arrCambiosEstado.size();
    }

    @Override
    public int compareTo(Factura otra)
    {
        int rta = this.fechaEmision.compareTo(otra.getFechaEmision());
        return rta;
    }

    public String getStrCalcularImporteFinal()
    {
        String rta = "$" + getCalcularImporteFinal();


        return rta;
    }

    public String getPeriodoFactura()
    {
        return MasterUtil.obtenerMes(Integer.parseInt(getMes())) + " " + getYear();
    }
}
