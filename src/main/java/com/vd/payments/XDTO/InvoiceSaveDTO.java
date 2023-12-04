package com.vd.payments.XDTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class InvoiceSaveDTO extends BaseDTO
{
    private int mesPeriodo;
    private int yearPeriodo;
    private int fkProducto;
    private int fkCliente;

}
