package com.vd.payments.REPO;

import com.vd.payments.MODELO.Instalacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstalacionRepo extends JpaRepository<Instalacion, Integer>
{
//    @Query("SELECT i FROM Instalacion i WHERE i. ?1 AND o.pass = ?2" )
//    public List<Instalacion> fingByOperadorID(int id);
//    @Query("SELECT o FROM Operador o WHERE o.email = ?1 AND o.pass = ?2" )
//    public List<Operador> dameUsuarioPorEmailYPass(String email, String pass);


//    @Query("SELECT o FROM Operador o WHERE o.instalacion.id = ?1" )
//    public List<Operador> findOperadorPorInstalacion(int fkInstalacion);


}
