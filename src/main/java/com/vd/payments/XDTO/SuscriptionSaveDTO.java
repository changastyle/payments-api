package com.vd.payments.XDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class SuscriptionSaveDTO extends BaseDTO
{
    public int fkProducto;
//    public double abono;
    public int periodicidadDias;
}
