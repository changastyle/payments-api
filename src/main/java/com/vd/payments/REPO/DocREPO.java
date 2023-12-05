package com.vd.payments.REPO;

import com.vd.payments.MODELO.Documento;
import com.vd.payments.MODELO.Operador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocREPO extends JpaRepository<Documento, Integer>
{
    @Query("SELECT d FROM Documento d WHERE d.id = :idDoc AND d.activo = true")
    public List<Documento> findByIDN(int idDoc);

    default Documento getByIDN(int id)
    {
        Documento rta = null;

        List<Documento> arr = findByIDN(id);

        if(arr != null)
        {
            rta = arr.stream().findFirst().orElse(null);
        }

        return rta;
    }
}
