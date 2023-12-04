package com.vd.payments.XDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor @AllArgsConstructor @Builder
public class ProductoSaveDTO extends BaseDTO
{
    private String nombre;
    private int abono;
    private boolean euro;
    private boolean usd;
    private boolean ars;
    private int fkInstalacion;
}
