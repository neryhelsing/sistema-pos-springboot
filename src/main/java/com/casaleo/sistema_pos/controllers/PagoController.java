package com.casaleo.sistema_pos.controllers;

import com.casaleo.sistema_pos.dto.PagoCreateDTO;
import com.casaleo.sistema_pos.dto.PagoResponseDTO;
import com.casaleo.sistema_pos.services.PagoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pagos")
@CrossOrigin(origins = "*")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @GetMapping
    public Page<PagoResponseDTO> listarPagos(
            @RequestParam(defaultValue = "") String query,
            Pageable pageable
    ) {
        return pagoService.listarPagos(query, pageable);
    }

    @PostMapping
    public PagoResponseDTO crearPago(@RequestBody PagoCreateDTO dto) {
        return pagoService.crearPago(dto);
    }
}
