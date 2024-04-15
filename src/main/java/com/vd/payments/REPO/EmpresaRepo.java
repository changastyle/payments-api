package com.vd.payments.REPO;

import com.vd.payments.MODELO.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpresaRepo extends JpaRepository<Empresa, Integer>
{
    @Query("SELECT c FROM Empresa c WHERE  c.id = :fkEmpresa AND c.activo = true")
    public List<Empresa> findByIDN(int fkEmpresa);
    @Query("SELECT c FROM Empresa c WHERE  c.instalacion.id = :fkInstalacion AND c.activo = true")
    public List<Empresa> findAllByFKInstalacion(int fkInstalacion);
    @Query("SELECT c FROM Empresa c WHERE c.instalacion.id = :fkInstalacion  AND c.id = :fkEmpresa  AND c.activo = true")
    public List<Empresa> findByInstalacionAndEmpresaID(int fkEmpresa , int fkInstalacion);

//    @Query("SELECT c FROM Empresa c WHERE c.instalacion.id = :fkInstalacion c.activo = true" )
//    public List<Empresa> findByInstalacionID(int fkInstalacion);

    default Empresa getByIDN(int fkEmpresa)
    {
        Empresa rta = null;

        List<Empresa> arr = findByIDN(fkEmpresa);

        if(arr != null)
        {
            rta = arr.stream().findFirst().orElse(null);
        }

        return rta;
    }
    default Empresa getByInstalacionAndEmpresaID(int fkEmpresa , int fkInstalacion)
    {
        Empresa rta = null;

        List<Empresa> arr = findByInstalacionAndEmpresaID(fkEmpresa,fkInstalacion);

        if(arr != null)
        {
            rta = arr.stream().findFirst().orElse(null);
        }

        return rta;
    }
    default Empresa getByInstalacionID(int id , int fkInstalacion)
    {
        Empresa rta = null;

        List<Empresa> arr = findByInstalacionAndEmpresaID(id , fkInstalacion);

        if(arr != null)
        {
            rta = arr.stream().findFirst().orElse(null);
        }

        return rta;
    }
}
