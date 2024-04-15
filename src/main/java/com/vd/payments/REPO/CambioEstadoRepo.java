package com.vd.payments.REPO;

import com.vd.payments.MODELO.CambioEstado;
import com.vd.payments.MODELO.Config;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CambioEstadoRepo extends JpaRepository<CambioEstado, Integer>
{
    @Query("SELECT c FROM CambioEstado c")
    public Optional<CambioEstado> findALl();

}