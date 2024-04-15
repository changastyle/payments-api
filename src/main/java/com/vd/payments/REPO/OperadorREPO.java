package com.vd.payments.REPO;

import com.vd.payments.MODELO.Operador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OperadorREPO extends JpaRepository<Operador, Integer>
{
    @Query("SELECT o FROM Operador o WHERE o.email = ?1 AND o.pass = ?2" )
    public List<Operador> dameUsuarioPorEmailYPass(String email, String pass);

    @Query("SELECT o FROM Operador o WHERE o.email = :email " )
    public List<Operador> findOperadorByEmail(String email);


    @Query("SELECT o FROM Operador o WHERE o.instalacion.id = :fkInstalacion" )
    public List<Operador> findOperadorPorInstalacion(int fkInstalacion);
    @Query("SELECT o FROM Operador o WHERE o.empresa.id = :fkEmpresa AND o.activo = true" )
    public List<Operador> findOperadorPorEmpresa(int fkEmpresa);
    @Query("SELECT o FROM Operador o WHERE o.empresa.instalacion.id = :fkInstalacion AND o.activo = true" )
    public List<Operador> findOperadorPorInstalacionOnlyActivos(int fkInstalacion);

    @Query("SELECT o FROM Operador o WHERE o.id = :id AND o.activo = true" )
    public List<Operador> findByID(int id);


    default Operador getOperadorByEmail(String email)
    {
        Operador rta = null;

        List<Operador> arr = findOperadorByEmail(email);

        if(arr != null)
        {
            rta = arr.stream().findFirst().orElse(null);
        }

        return rta;
    }
    default Operador getUsuarioPorEmailYPass(String email, String pass)
    {
        Operador rta = null;

        List<Operador> arr = dameUsuarioPorEmailYPass(email,pass);

        if(arr != null)
        {
            rta = arr.stream().findFirst().orElse(null);
        }

        return rta;
    }
    default Operador getByIDN(int id)
    {
        Operador rta = null;

        List<Operador> arr = findByID(id);

        if(arr != null)
        {
            rta = arr.stream().findFirst().orElse(null);
        }

        return rta;
    }
}
