package com.ticketti.ms_donaciones.controller;

import com.ticketti.ms_donaciones.dto.AsociarOrganizacionRequestDTO;
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
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/v1/causas")
@RequiredArgsConstructor
@Tag(name = "Causas Sociales", description = "Gestión de causas sociales por organización")
public class CausaSocialController {

    private final CausaSocialService causaSocialService;

    // POST /api/v1/causas
    @PostMapping
    @Operation(summary = "Crear causa social")
    public ResponseEntity<CausaSocialResponseDTO> crear(
            @Valid @RequestBody CausaSocialRequestDTO dto) {
        CausaSocialModel creada = causaSocialService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CausaSocialResponseDTO.desdeModelo(creada));
    }

    //GET /api/v1/causas/todas
    @GetMapping("/todas")
    @Operation(summary = "Listar todas las causas sociales")
    public ResponseEntity<List<CausaSocialResponseDTO>> listarTodas() {
        List<CausaSocialResponseDTO> resultado = causaSocialService.listarTodas()
                .stream()
                .map(CausaSocialResponseDTO::desdeModelo)
                .toList();
        return ResponseEntity.ok(resultado);
    }

    // GET /api/v1/causas/activas
    @GetMapping("/activas")
    @Operation(summary = "Listar causas activas (visible al comprador)")
    public ResponseEntity<List<CausaSocialResponseDTO>> listarActivas() {
        List<CausaSocialResponseDTO> resultado = causaSocialService.listarActivas()
        .stream()
        .map(CausaSocialResponseDTO::desdeModelo)
        .toList();
        return ResponseEntity.ok(resultado);
    }

    // GET /api/v1/causas/organizacion/{idOrganizacion}
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

    // GET /api/v1/causas/{id}
    @GetMapping("/{id}")
    @Operation(summary = "Buscar causa por ID")
    public ResponseEntity<CausaSocialResponseDTO> buscarPorId(@PathVariable Long id) {
        CausaSocialModel causa = causaSocialService.buscarPorId(id);
        return ResponseEntity.ok(CausaSocialResponseDTO.desdeModelo(causa));
    }

    // DELETE /api/v1/causas/{id}
    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar causa social")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        causaSocialService.desactivar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/v1/causas/{id}/documento
     * El organizador sube el PDF de respaldo. No se guarda en disco: se
     * reenvía por correo al equipo Ticketti para validación manual.
     */
    @PostMapping("/{id}/documento")
    @Operation(summary = "Enviar documento de respaldo de la causa para validación")
    public ResponseEntity<CausaSocialResponseDTO> enviarDocumento(
            @PathVariable Long id,
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("nombreOrganizador") String nombreOrganizador) {
        return ResponseEntity.ok(
                causaSocialService.enviarDocumento(id, archivo, nombreOrganizador));
    }

    /**
     * PUT /api/v1/causas/{id}/activar
     * Solo admin. Cambia la causa de PENDIENTE a ACTIVA tras validar el
     * documento de respaldo recibido por correo.
     */
    @PutMapping("/{id}/activar")
    @Operation(summary = "Aprobar y activar causa social pendiente")
    public ResponseEntity<CausaSocialResponseDTO> activar(@PathVariable Long id) {
        return ResponseEntity.ok(causaSocialService.activar(id));
    }

    /**
     * PUT /api/v1/causas/{id}/organizacion
     * Solo admin. Asocia (o reasigna) la organización de una causa social
     * que fue creada por el organizador sin una (ver CausaSocialRequestDTO).
     */
    @PutMapping("/{id}/organizacion")
    @Operation(summary = "Asociar una organización existente a la causa social")
    public ResponseEntity<CausaSocialResponseDTO> asociarOrganizacion(
            @PathVariable Long id,
            @Valid @RequestBody AsociarOrganizacionRequestDTO dto) {
        return ResponseEntity.ok(causaSocialService.asociarOrganizacion(id, dto));
    }
}
