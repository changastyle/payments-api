package com.vd.payments.XDTO;

import com.vd.payments.MODELO.Instalacion;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class MenuSDTO extends BaseDTO
{
    public String nombre;
    public String url;
    public String icono;
    public int orden = 999;
    public boolean requiereAdmin;
}
