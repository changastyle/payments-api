package com.vd.payments.REPO;

import com.vd.payments.MODELO.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoREPO extends JpaRepository<Producto, Integer>
{
    @Query("SELECT p FROM Producto p WHERE p.instalacion.id = :fkInstalacion AND p.activo = true")
    public List<Producto> findAllByInstalacion(int fkInstalacion);
    @Query("SELECT p FROM Producto p WHERE  p.id = :id AND p.instalacion.id = :fkInstalacion AND p.activo = true")
    public List<Producto> findByIDN(int id , int fkInstalacion);

    default Producto getByIDN(int id , int fkInstalacion)
    {
        Producto rta = null;

        List<Producto> arr = findByIDN(id , fkInstalacion);

        if(arr != null)
        {
            rta = arr.stream().findFirst().orElse(null);
        }

        return rta;
    }
    default Producto getByInstalacionID(int id , int fkInstalacion)
    {
        Producto rta = null;

        List<Producto> arr = findByIDN(id , fkInstalacion);

        if(arr != null)
        {
            rta = arr.stream().findFirst().orElse(null);
        }

        return rta;
    }
}
