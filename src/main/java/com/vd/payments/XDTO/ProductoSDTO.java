package com.vd.payments.XDTO;

import com.vd.payments.MODELO.Documento;
import com.vd.payments.MODELO.Instalacion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class ProductoSDTO extends BaseDTO
{
    public String nombre;
    public double precio;
    public int fkFotoNotMandatory = -1;
}
