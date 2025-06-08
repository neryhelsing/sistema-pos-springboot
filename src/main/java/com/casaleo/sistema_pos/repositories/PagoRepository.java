package com.casaleo.sistema_pos.repositories;

import com.casaleo.sistema_pos.models.Pago;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagoRepository extends JpaRepository<Pago, Integer> {
    Page<Pago> findBynPagoContainingIgnoreCase(String query, Pageable pageable);
}
