package com.ticketti.ms_donaciones.controller;

import com.ticketti.ms_donaciones.dto.CausaSocialRequestDTO;
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
    public ResponseEntity<CausaSocialModel> crear(
            @Valid @RequestBody CausaSocialRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(causaSocialService.crear(dto));
    }

    // GET /api/causas/activas
    @GetMapping("/activas")
    @Operation(summary = "Listar causas activas (visible al comprador)")
    public ResponseEntity<List<CausaSocialModel>> listarActivas() {
        return ResponseEntity.ok(causaSocialService.listarActivas());
    }

    // GET /api/causas/organizacion/{idOrganizacion}
    @GetMapping("/organizacion/{idOrganizacion}")
    @Operation(summary = "Listar causas de una organización")
    public ResponseEntity<List<CausaSocialModel>> listarPorOrganizacion(
            @PathVariable Long idOrganizacion) {
        return ResponseEntity.ok(
                causaSocialService.listarPorOrganizacion(idOrganizacion));
    }

    // GET /api/causas/{id}
    @GetMapping("/{id}")
    @Operation(summary = "Buscar causa por ID")
    public ResponseEntity<CausaSocialModel> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(causaSocialService.buscarPorId(id));
    }

    // DELETE /api/causas/{id}
    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar causa social")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        causaSocialService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}