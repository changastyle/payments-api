package com.vd.payments.REPO;

import com.vd.payments.MODELO.DeletionDoc;
import com.vd.payments.MODELO.Suscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeletionDocREPO extends JpaRepository<DeletionDoc, Integer>
{
//    @Query("SELECT s FROM Suscripcion s WHERE s.empresa.instalacion.id = :fkInstalacion AND s.activo = true")
//    public List<Suscripcion> findByInstalacion(int fkInstalacion);
//    @Query("SELECT s FROM Suscripcion s WHERE s.empresa.id = :fkEmpresa AND s.activo = true")
//    public List<Suscripcion> findByEmpresa(int fkEmpresa);

//    default Suscripcion getByEmpresa(int fkEmpresa)
//    {
//        Suscripcion rta = null;
//
//        List<Suscripcion> arr = findByEmpresa(fkEmpresa);
//
//        if(arr != null)
//        {
//            rta = arr.stream().findFirst().orElse(null);
//        }
//
//        return rta;
//    }
//    default Suscripcion getByInstalacion(int fkInstalacion)
//    {
//        Suscripcion rta = null;
//
//        List<Suscripcion> arr = findByInstalacion(fkInstalacion);
//
//        if(arr != null)
//        {
//            rta = arr.stream().findFirst().orElse(null);
//        }
//
//        return rta;
//    }
}
