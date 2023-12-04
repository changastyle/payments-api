package com.vd.payments.XDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class ComponenteSaveDTO extends BaseDTO
{
    private String nombre;
    private int fkSitio;

}
