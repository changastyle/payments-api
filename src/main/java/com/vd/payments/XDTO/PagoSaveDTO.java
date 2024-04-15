package com.vd.payments.XDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class PagoSaveDTO extends BaseDTO
{
    public int fkSuscripcion;
    public int fkEmpresa;
}
