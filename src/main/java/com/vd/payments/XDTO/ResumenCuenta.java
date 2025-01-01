package com.vd.payments.XDTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vd.payments.MODELO.CambioEstado;
import com.vd.payments.MODELO.Factura;
import com.vd.payments.MODELO.Instalacion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @NoArgsConstructor
public class ResumenCuenta
{
    @JsonIgnore
    private Instalacion instalacion;
    private List<Factura> arrFacturas;

    public double getCalcularDeuda()
    {
        double montoAdeudado = 0;
        if(arrFacturas != null)
        {
            for(Factura facturaLoop : arrFacturas)
            {
                CambioEstado ultCambioEstado = facturaLoop.getUltimoCambioEstadoFactura();

//                if(ultCambioEstado.getId()== 1)
//                {
                    montoAdeudado += facturaLoop.getCalcularImporteFinal();
//                }
            }
        }

        return montoAdeudado;
    }
    public String getCBUInstalacion()
    {
        String cbuInstalacion = "";
        if(instalacion != null)
        {
            cbuInstalacion = instalacion.getCbu();
        }

        return cbuInstalacion;
    }
}
