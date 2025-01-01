package com.vd.payments.MODELO;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data @NoArgsConstructor
@Table(name = "deletions_documents")
public class DeletionDoc
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @OneToOne() @JoinColumn(name = "fkDoc")
    private Documento documento;
    @OneToOne() @JoinColumn(name = "fkResponsable")
    private Operador responsable;
    private LocalDateTime fecha;

    public DeletionDoc(Documento documento, Operador responsable)
    {
        this.documento = documento;
        this.responsable = responsable;
        this.fecha = LocalDateTime.now();
    }
}
