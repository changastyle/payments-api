package com.vd.payments.REPO;

import com.vd.payments.MODELO.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public interface MenuREPO extends JpaRepository<Menu, Integer>
{
//    @Query("SELECT m FROM Menu m WHERE m.instalacion.id = :fkInstalacion AND m.requiereAdmin = :requiereAdmin AND m.activo = true")
//    public List<Menu> findAllInstalacionAndCheckAdminRequired(int fkInstalacion, boolean requiereAdmin);
    @Query("SELECT m FROM Menu m WHERE m.instalacion.id = :fkInstalacion AND m.activo = true")
    public List<Menu> findAllInstalacion(int fkInstalacion);
    @Query("SELECT m FROM Menu m WHERE  m.id = :id AND m.instalacion.id = :fkInstalacion AND m.activo = true")
    public List<Menu> findByIDN(int id , int fkInstalacion);

    default List<Menu> findAllAndRemoveAdminsMenusIfOperadorNotAdmin(int fkInstalacion , boolean operadorEsAdmin)
    {
        List<Menu> arrQuitadoMenusParaAdmins = new ArrayList<>();
        List<Menu> arrDB = findAllInstalacion(fkInstalacion);

        if(arrDB != null)
        {
            if(!operadorEsAdmin)
            {
                for(Menu menuLoop : arrDB)
                {
                    if(!menuLoop.isRequiereAdmin())
                    {
                        arrQuitadoMenusParaAdmins.add(menuLoop);
                    }
                }
//                arrQuitadoMenusParaAdmins = arrDB.stream().filter(menu -> !menu.isRequiereAdmin()).collect(Collectors.toList());
            }
            else
            {
                arrQuitadoMenusParaAdmins = arrDB;
            }
        }

        return arrQuitadoMenusParaAdmins;
    }
    default Menu getByIDN(int id , int fkInstalacion)
    {
        Menu rta = null;

        List<Menu> arr = findByIDN(id , fkInstalacion);

        if(arr != null)
        {
            rta = arr.stream().findFirst().orElse(null);
        }

        return rta;
    }
}
