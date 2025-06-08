package com.casaleo.sistema_pos.services;

import com.casaleo.sistema_pos.dto.PagoResponseDTO;
import com.casaleo.sistema_pos.models.Pago;
import com.casaleo.sistema_pos.repositories.PagoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PagoService {

    @Autowired
    private PagoRepository pagoRepository;

    public Page<PagoResponseDTO> listarPagos(String query, Pageable pageable) {
        Page<Pago> pagos = pagoRepository.findBynPagoContainingIgnoreCase(query, pageable);

        return pagos.map(p -> new PagoResponseDTO(
                p.getId(),
                p.getNPago(),
                p.getTotalPagado(),
                p.getCreadoEn() // ← Este es LocalDateTime y se formatea en el constructor del DTO
        ));
    }
}