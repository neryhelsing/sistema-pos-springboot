package com.casaleo.sistema_pos.repositories;

import com.casaleo.sistema_pos.models.Correlativo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

public interface CorrelativoRepository extends JpaRepository<Correlativo, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Correlativo c WHERE c.clave = :clave")
    Correlativo findByClaveForUpdate(@Param("clave") String clave);
}
