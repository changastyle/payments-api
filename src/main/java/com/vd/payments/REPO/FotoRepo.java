package com.vd.payments.REPO;

import com.vd.payments.MODELO.Foto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FotoRepo extends JpaRepository<Foto, Integer>
{
}
