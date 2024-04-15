package com.vd.payments.REPO;

import com.vd.payments.MODELO.Empresa;
import com.vd.payments.MODELO.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public interface FacturaREPO extends JpaRepository<Factura, Integer>
{
    @Query("SELECT f FROM Factura f WHERE  f.empresa.instalacion.id = :fkInstalacion AND f.activo = true")
    public List<Factura> findAllByInstalacionForAdminPurposes(int fkInstalacion);
    @Query("SELECT f FROM Factura f WHERE  f.empresa.id = :fkEmpresa AND f.activo = true")
    public List<Factura> findAllByFKEmpresa(int fkEmpresa);
    @Query("SELECT f FROM Factura f WHERE  f.id = :fkFactura AND f.activo = true")
    public List<Factura> findByIDN(int fkFactura);
//    @Query("SELECT c FROM Empresa c WHERE  c.instalacion.id = :fkInstalacion AND c.activo = true")
//    public List<Empresa> findAllByFKInstalacion(int fkInstalacion);
//    @Query("SELECT c FROM Empresa c WHERE c.instalacion.id = :fkInstalacion  AND c.id = :fkEmpresa  AND c.activo = true")
//    public List<Empresa> findByInstalacionAndEmpresaID(int fkEmpresa , int fkInstalacion);

//    @Query("SELECT c FROM Empresa c WHERE c.instalacion.id = :fkInstalacion c.activo = true" )
//    public List<Empresa> findByInstalacionID(int fkInstalacion);

    default Factura getByIDN(int fkFactura)
    {
        Factura rta = null;

        List<Factura> arr = findByIDN(fkFactura);

        if(arr != null)
        {
            rta = arr.stream().findFirst().orElse(null);
        }

        return rta;
    }
    default Factura getByInstalacionAndID(int idFactura , int fkInstalacion)
    {
        Factura rta = null;

        List<Factura> arr = findAllByInstalacionForAdminPurposes(fkInstalacion);

        if(arr != null)
        {
            rta = arr.stream().filter(factura -> factura.getId() == idFactura).findFirst().orElse(null);
        }

        return rta;
    }
//    default Empresa getByInstalacionAndEmpresaID(int fkEmpresa , int fkInstalacion)
//    {
//        Empresa rta = null;
//
//        List<Empresa> arr = findByInstalacionAndEmpresaID(fkEmpresa,fkInstalacion);
//
//        if(arr != null)
//        {
//            rta = arr.stream().findFirst().orElse(null);
//        }
//
//        return rta;
//    }
//    default Empresa getByInstalacionID(int id , int fkInstalacion)
//    {
//        Empresa rta = null;
//
//        List<Empresa> arr = findByInstalacionAndEmpresaID(id , fkInstalacion);
//
//        if(arr != null)
//        {
//            rta = arr.stream().findFirst().orElse(null);
//        }
//
//        return rta;
//    }
}
