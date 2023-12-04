package com.vd.payments.REPO;

import com.vd.payments.MODELO.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrupoREPO extends JpaRepository<Grupo, Integer>
{
    @Query("SELECT g FROM Grupo g WHERE g.instalacion.id = :fkInstalacion")
    public List<Grupo> findGrupoByFKInstalacion(@Param("fkInstalacion") int fkInstalacion);
}