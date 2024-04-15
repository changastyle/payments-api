package com.vd.payments.XDTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vd.payments.MODELO.Documento;
import com.vd.payments.MODELO.Empresa;
import com.vd.payments.MODELO.Instalacion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class InstalacionConEmpresas extends BaseDTO
{
    private Instalacion instalacion;
    private List<Empresa> arrEmpresas;

    public InstalacionConEmpresas(Instalacion instalacion)
    {
        if(instalacion != null)
        {
            this.instalacion = instalacion;
            this.arrEmpresas = instalacion.getArrEmpresas();
        }
    }
}
