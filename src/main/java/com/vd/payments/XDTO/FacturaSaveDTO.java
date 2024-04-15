package com.vd.payments.XDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class FacturaSaveDTO extends BaseDTO
{
    public int mesFactura;
    public int yearFactura;
    public int fkEmpresa;
    public int fkProducto;
    public int cantidad;
    public int precio;
}
