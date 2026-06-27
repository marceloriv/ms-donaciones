package com.ticketti.ms_donaciones.service;

import com.ticketti.ms_donaciones.dto.CausaSocialRequestDTO;
import com.ticketti.ms_donaciones.enums.EstadoCausaSocial;
import com.ticketti.ms_donaciones.exception.ResourceNotFoundException;
import com.ticketti.ms_donaciones.model.CausaSocialModel;
import com.ticketti.ms_donaciones.model.OrganizacionModel;
import com.ticketti.ms_donaciones.repository.CausaSocialRepository;
import com.ticketti.ms_donaciones.repository.OrganizacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CausaSocialService {

    private final CausaSocialRepository causaSocialRepository;
    private final OrganizacionRepository organizacionRepository;

    /** Crear causa social asociada a una organización */
    public CausaSocialModel crear(CausaSocialRequestDTO dto) {
        OrganizacionModel org = organizacionRepository
                .findById(dto.getIdOrganizacion())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Organización", dto.getIdOrganizacion()));

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


    /** Listar causas activas (el comprador las ve al elegir) */
    public List<CausaSocialModel> listarActivas() {
        return causaSocialRepository.findByEstado(EstadoCausaSocial.ACTIVA);
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
}
