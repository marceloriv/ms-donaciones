package com.ticketti.ms_donaciones.service;

import com.ticketti.ms_donaciones.client.MensajeriaClient;
import com.ticketti.ms_donaciones.client.dto.EnviarDocumentoCausaRequestDTO;
import com.ticketti.ms_donaciones.dto.CausaSocialRequestDTO;
import com.ticketti.ms_donaciones.dto.CausaSocialResponseDTO;
import com.ticketti.ms_donaciones.enums.EstadoCausaSocial;
import com.ticketti.ms_donaciones.exception.BusinessException;
import com.ticketti.ms_donaciones.exception.ResourceNotFoundException;
import com.ticketti.ms_donaciones.model.CausaSocialModel;
import com.ticketti.ms_donaciones.model.OrganizacionModel;
import com.ticketti.ms_donaciones.repository.CausaSocialRepository;
import com.ticketti.ms_donaciones.repository.OrganizacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CausaSocialService {

    private final CausaSocialRepository causaSocialRepository;
    private final OrganizacionRepository organizacionRepository;
    private final MensajeriaClient mensajeriaClient;

    /** Crear causa social. La organización es opcional: puede asociarse después. */
    public CausaSocialModel crear(CausaSocialRequestDTO dto) {
        OrganizacionModel org = null;
        if (dto.getIdOrganizacion() != null) {
            org = organizacionRepository
                    .findById(dto.getIdOrganizacion())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Organización", dto.getIdOrganizacion()));
        }

        CausaSocialModel causa = CausaSocialModel.builder()
                .organizacion(org)
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .objetivoMonto(dto.getObjetivoMonto())
                .fechaInicio(dto.getFechaInicio())
                .fechaFin(dto.getFechaFin())
                .build();

        return causaSocialRepository.save(causa);
    }

    /** Listar todas las causas sociales */
    public List<CausaSocialModel> listarTodas() {
        return causaSocialRepository.findAll();
    }


    /**
     * Listar causas activas (el comprador/organizador las ve al elegir).
     * Solo se muestran las que ya tienen organización asociada, porque sin
     * ella no hay cuenta bancaria a donde depositar las donaciones.
     */
    public List<CausaSocialModel> listarActivas() {
        return causaSocialRepository
                .findByEstadoAndOrganizacionIsNotNull(EstadoCausaSocial.ACTIVA);
    }

    /** Listar causas de una organización específica */
    public List<CausaSocialModel> listarPorOrganizacion(Long idOrganizacion) {
        return causaSocialRepository
                .findByOrganizacion_IdOrganizacion(idOrganizacion);
    }

    /** Buscar por ID */
    public CausaSocialModel buscarPorId(Long id) {
        return causaSocialRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("CausaSocial", id));
    }

    /** Desactivar causa (borrado lógico) */
    public void desactivar(Long id) {
        CausaSocialModel causa = buscarPorId(id);
        causa.setEstado(EstadoCausaSocial.INACTIVA);
        causaSocialRepository.save(causa);
    }

    /**
     * El organizador sube el PDF de respaldo de la causa. No se persiste en
     * disco (no sobrevive en ECS/Fargate): se reenvía por correo al equipo
     * Ticketti, que lo revisa y luego activa la causa con {@link #activar}.
     */
    public CausaSocialResponseDTO enviarDocumento(Long id, MultipartFile archivo, String nombreOrganizador) {
        CausaSocialModel causa = buscarPorId(id);

        String archivoBase64;
        try {
            archivoBase64 = Base64.getEncoder().encodeToString(archivo.getBytes());
        } catch (IOException e) {
            throw new BusinessException("No se pudo leer el archivo: " + e.getMessage());
        }

        mensajeriaClient.enviarDocumentoCausa(EnviarDocumentoCausaRequestDTO.builder()
                .idCausa(causa.getIdCausa())
                .nombreCausa(causa.getNombre())
                .nombreOrganizador(nombreOrganizador)
                .archivoBase64(archivoBase64)
                .nombreArchivo(archivo.getOriginalFilename())
                .build());

        causa.setDocumentoEnviado(true);
        causa.setFechaDocumentoEnviado(LocalDateTime.now());
        return CausaSocialResponseDTO.desdeModelo(causaSocialRepository.save(causa));
    }

    /**
     * El admin activa la causa. La validación del documento de respaldo es
     * un proceso humano (el admin revisa el correo recibido y luego llama
     * este endpoint) — no se bloquea en código por documentoEnviado, porque
     * esto también lo usa el admin para activar causas que él mismo creó
     * directamente (sin documento, sin organizador de por medio).
     */
    public CausaSocialResponseDTO activar(Long id) {
        CausaSocialModel causa = buscarPorId(id);

        if (causa.getEstado() != EstadoCausaSocial.PENDIENTE) {
            throw new BusinessException(
                    "La causa no está en estado PENDIENTE. Estado actual: " + causa.getEstado());
        }

        causa.setEstado(EstadoCausaSocial.ACTIVA);
        return CausaSocialResponseDTO.desdeModelo(causaSocialRepository.save(causa));
    }
}
