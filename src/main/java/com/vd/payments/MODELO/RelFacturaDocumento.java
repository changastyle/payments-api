package com.vd.payments.MODELO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "rels_factura_documentos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RelFacturaDocumento
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @OneToOne() @JoinColumn(name = "fkFactura") @JsonIgnore
    private Factura factura;
    @OneToOne() @JoinColumn(name = "fkDocumento")
    private Documento documento;
    private LocalDateTime fechaUP;

    public boolean isActivo()
    {
        boolean activo = false;

        if(documento != null)
        {
            activo = documento.isActivo();
        }

        return activo;
    }
}
