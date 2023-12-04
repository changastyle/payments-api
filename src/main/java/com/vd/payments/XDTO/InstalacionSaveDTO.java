package com.vd.payments.XDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class InstalacionSaveDTO extends BaseDTO
{
    private String nombre;
    private String urlWEB;
}
