package com.vd.payments.REPO;

import com.vd.payments.MODELO.Operador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OperadorREPO extends JpaRepository<Operador, Integer>
{
    @Query("SELECT o FROM Operador o WHERE o.email = ?1 AND o.pass = ?2" )
    public List<Operador> dameUsuarioPorEmailYPass(String email, String pass);


    @Query("SELECT o FROM Operador o WHERE o.instalacion.id = ?1" )
    public List<Operador> findOperadorPorInstalacion(int fkInstalacion);


}
