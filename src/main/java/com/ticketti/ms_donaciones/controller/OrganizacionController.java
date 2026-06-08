package com.ticketti.ms_donaciones.controller;

import com.ticketti.ms_donaciones.dto.ActivarOrganizacionDTO;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

    /**
     * POST /api/v1/organizaciones/{id}/documento
     * El organizador sube el PDF del convenio. Se guarda el nombre del archivo
     * en BD; el archivo físico queda en el servidor bajo /uploads/convenios/.
     */
    @PostMapping("/{id}/documento")
    @Operation(summary = "Subir documento de convenio")
    public ResponseEntity<OrganizacionResponseDTO> subirDocumento(
            @PathVariable Long id,
            @RequestParam("archivo") MultipartFile archivo) throws IOException {

        // Construimos una ruta segura: evita que el nombre del archivo
        // contenga ../ u otras rutas maliciosas
        String nombreArchivo = id + "_" + archivo.getOriginalFilename()
                .replaceAll("[^a-zA-Z0-9._-]", "_");

        // Guardamos el archivo en disco (carpeta uploads/convenios/)
        Path destino = Paths.get("uploads/convenios/" + nombreArchivo);
        Files.createDirectories(destino.getParent());
        Files.copy(archivo.getInputStream(), destino,
                StandardCopyOption.REPLACE_EXISTING);

        // Solo guardamos el nombre en BD, no la ruta completa
        return ResponseEntity.ok(
                organizacionService.guardarDocumento(id, nombreArchivo));
    }

    /**
     * PUT /api/v1/organizaciones/{id}/activar
     * Solo ADMINPLATAFORMA. Completa datos bancarios y cambia estado a ACTIVA.
     */
    @PutMapping("/{id}/activar")
    @Operation(summary = "Aprobar y activar organización pendiente")
    public ResponseEntity<OrganizacionResponseDTO> activar(
            @PathVariable Long id,
            @Valid @RequestBody ActivarOrganizacionDTO dto) {
        return ResponseEntity.ok(organizacionService.activar(id, dto));
    }


}