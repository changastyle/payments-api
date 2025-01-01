package com.vd.payments.XDTO;

import com.vd.payments.MODELO.Empresa;
import com.vd.payments.MODELO.Instalacion;
import com.vd.payments.MODELO.Menu;
import com.vd.payments.MODELO.Operador;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TuplaEmpresaRolDTO
{
    public Operador operador;
    public Instalacion instalacion;
    public List<Empresa> arrEmpresas;
    public List<Menu> arrMenus;

    public void addEmpresa(Empresa empresa)
    {
        if (arrEmpresas == null)
        {
            arrEmpresas = new ArrayList<>();
        }

        arrEmpresas.add(empresa);
    }

    public int getFkInstalacion()
    {
        int fkInstalacion = -1;

        if (instalacion != null)
        {
            fkInstalacion = instalacion.getId();
        }

        return fkInstalacion;
    }

    public boolean getAdminOfOperador()
    {
        boolean adminLevel = false;

        if (operador != null)
        {
            adminLevel = operador.isAdmin();
        }

        return adminLevel;
    }

    public Empresa getEmpresaPrincipal()
    {
        Empresa empresaPrincipal = null;

        if (arrEmpresas != null)
        {
            if (arrEmpresas.size() > 0)
            {
                empresaPrincipal = arrEmpresas.get(0);
            }
        }


        return empresaPrincipal;
    }

    public int getFKEmpresaPrincipal()
    {
        int fkEmpresaPrincipal = -1;
        Empresa empresaPrincipal = getEmpresaPrincipal();

        if (empresaPrincipal != null)
        {
            fkEmpresaPrincipal = empresaPrincipal.getId();
        }


        return fkEmpresaPrincipal;
    }

    public String getEmailOperador()
    {
        String emailOperador = "";
        if (operador != null)
        {
            emailOperador = operador.getEmail();
        }
        return emailOperador;
    }

}
