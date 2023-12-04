package com.vd.payments.REPO;

import com.vd.payments.MODELO.Config;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfigRepo extends JpaRepository<Config, Integer>
{
    @Query("SELECT c FROM Config c")
    public Optional<Config> findALl();

}