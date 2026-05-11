package com.ticketti.ms_donaciones.controller;

import com.ticketti.ms_donaciones.dto.OrganizacionRequestDTO;
import com.ticketti.ms_donaciones.dto.OrganizacionResponseDTO;
import com.ticketti.ms_donaciones.service.OrganizacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/organizaciones")
@RequiredArgsConstructor
@Tag(name = "Organizaciones", description = "CRUD de organizaciones beneficiarias")
public class OrganizacionController {

    private final OrganizacionService organizacionService;

    // POST /api/organizaciones
    @PostMapping
    @Operation(summary = "Crear nueva organización (admin)")
    public ResponseEntity<OrganizacionResponseDTO> crear(
            @Valid @RequestBody OrganizacionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(organizacionService.crear(dto));
    }

    // GET /api/organizaciones
    @GetMapping
    @Operation(summary = "Listar organizaciones activas")
    public ResponseEntity<List<OrganizacionResponseDTO>> listar() {
        return ResponseEntity.ok(organizacionService.listarActivas());
    }

    // GET /api/organizaciones/{id}
    @GetMapping("/{id}")
    @Operation(summary = "Buscar organización por ID")
    public ResponseEntity<OrganizacionResponseDTO> buscarPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(organizacionService.buscarPorId(id));
    }

    // PUT /api/organizaciones/{id}
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar organización")
    public ResponseEntity<OrganizacionResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody OrganizacionRequestDTO dto) {
        return ResponseEntity.ok(organizacionService.actualizar(id, dto));
    }

    // DELETE /api/organizaciones/{id} (borrado lógico)
    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar organización")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        organizacionService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}