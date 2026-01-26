package com.casaleo.sistema_pos.repositories;

import com.casaleo.sistema_pos.models.Pago;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PagoRepository extends JpaRepository<Pago, Integer> {

    @Query("""
        SELECT p
        FROM Pago p
        WHERE LOWER(p.nPago) LIKE LOWER(CONCAT('%', :query, '%'))
    """)
    Page<Pago> buscarPorNPago(@Param("query") String query, Pageable pageable);
}
