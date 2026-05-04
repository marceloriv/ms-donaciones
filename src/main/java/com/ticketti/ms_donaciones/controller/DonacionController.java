package com.ticketti.ms_donaciones.controller;

import com.ticketti.ms_donaciones.dto.DonacionResponseDTO;
import com.ticketti.ms_donaciones.service.DonacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/donaciones")
@RequiredArgsConstructor
@Tag(name = "Donaciones", description = "Consulta de donaciones y montos recaudados")
public class DonacionController {

    private final DonacionService donacionService;

    // GET /api/donaciones/organizacion/{id}
    @GetMapping("/organizacion/{id}")
    @Operation(summary = "Donaciones por organización")
    public ResponseEntity<List<DonacionResponseDTO>> porOrganizacion(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                donacionService.listarPorOrganizacion(id));
    }

    // GET /api/donaciones/causa/{id}
    @GetMapping("/causa/{id}")
    @Operation(summary = "Donaciones por causa social")
    public ResponseEntity<List<DonacionResponseDTO>> porCausa(
            @PathVariable Long id) {
        return ResponseEntity.ok(donacionService.listarPorCausa(id));
    }

    // GET /api/donaciones/evento/{id}
    @GetMapping("/evento/{id}")
    @Operation(summary = "Donaciones por evento")
    public ResponseEntity<List<DonacionResponseDTO>> porEvento(
            @PathVariable Long id) {
        return ResponseEntity.ok(donacionService.listarPorEvento(id));
    }

    // GET /api/donaciones/usuario/{id}
    @GetMapping("/usuario/{id}")
    @Operation(summary = "Donaciones de un usuario")
    public ResponseEntity<List<DonacionResponseDTO>> porUsuario(
            @PathVariable Long id) {
        return ResponseEntity.ok(donacionService.listarPorUsuario(id));
    }

    // GET /api/donaciones/total/organizacion/{id}
    @GetMapping("/total/organizacion/{id}")
    @Operation(summary = "Total recaudado por organización")
    public ResponseEntity<BigDecimal> totalOrganizacion(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                donacionService.totalPorOrganizacion(id));
    }

    // GET /api/donaciones/total/causa/{id}
    @GetMapping("/total/causa/{id}")
    @Operation(summary = "Total recaudado por causa social")
    public ResponseEntity<BigDecimal> totalCausa(@PathVariable Long id) {
        return ResponseEntity.ok(donacionService.totalPorCausa(id));
    }
}
