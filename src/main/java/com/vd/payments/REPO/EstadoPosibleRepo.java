package com.vd.payments.REPO;

import com.vd.payments.MODELO.CambioEstado;
import com.vd.payments.MODELO.EstadoPosible;
import com.vd.payments.MODELO.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstadoPosibleRepo extends JpaRepository<EstadoPosible, Integer>
{
    @Query("SELECT e FROM EstadoPosible e WHERE e.activo = true")
    public List<EstadoPosible> findEstadosPosiblesActivos();

    default EstadoPosible getByIDN(int idRecibido)
    {
        EstadoPosible rta = null;

        List<EstadoPosible> arr = findEstadosPosiblesActivos();

        if (arr != null)
        {
            rta = arr.stream()
                    .filter(estadoPosible -> estadoPosible.getId() == idRecibido)
                    .findFirst()
                    .orElse(null);
        }

        return rta;
    }
}