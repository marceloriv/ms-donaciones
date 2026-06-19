package com.ticketti.ms_donaciones.controller;

import com.ticketti.ms_donaciones.dto.CausaSocialRequestDTO;
import com.ticketti.ms_donaciones.dto.CausaSocialResponseDTO;
import com.ticketti.ms_donaciones.model.CausaSocialModel;
import com.ticketti.ms_donaciones.service.CausaSocialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/causas")
@RequiredArgsConstructor
@Tag(name = "Causas Sociales", description = "Gestión de causas sociales por organización")
public class CausaSocialController {

    private final CausaSocialService causaSocialService;

    // POST /api/causas
    @PostMapping
    @Operation(summary = "Crear causa social")
    public ResponseEntity<CausaSocialResponseDTO> crear(
            @Valid @RequestBody CausaSocialRequestDTO dto) {
        CausaSocialModel creada = causaSocialService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CausaSocialResponseDTO.desdeModelo(creada));
    }

    // GET /api/causas/activas
    @GetMapping("/activas")
    @Operation(summary = "Listar causas activas (visible al comprador)")
    public ResponseEntity<List<CausaSocialResponseDTO>> listarActivas() {
        List<CausaSocialResponseDTO> resultado = causaSocialService.listarActivas()
        .stream()
        .map(CausaSocialResponseDTO::desdeModelo)
        .toList();
        return ResponseEntity.ok(resultado);
    }

    // GET /api/causas/organizacion/{idOrganizacion}
    @GetMapping("/organizacion/{idOrganizacion}")
    @Operation(summary = "Listar causas de una organización")
    public ResponseEntity<List<CausaSocialResponseDTO>> listarPorOrganizacion(
            @PathVariable Long idOrganizacion) {
        List<CausaSocialResponseDTO> resultado = causaSocialService
                .listarPorOrganizacion(idOrganizacion)
                .stream()
                .map(CausaSocialResponseDTO::desdeModelo)
                .toList();
        return ResponseEntity.ok(resultado);
    }

    // GET /api/causas/{id}
    @GetMapping("/{id}")
    @Operation(summary = "Buscar causa por ID")
    public ResponseEntity<CausaSocialResponseDTO> buscarPorId(@PathVariable Long id) {
        CausaSocialModel causa = causaSocialService.buscarPorId(id);
        return ResponseEntity.ok(CausaSocialResponseDTO.desdeModelo(causa));
    }

    // DELETE /api/causas/{id}
    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar causa social")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        causaSocialService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
