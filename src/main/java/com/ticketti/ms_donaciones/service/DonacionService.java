package com.ticketti.ms_donaciones.service;

import com.ticketti.ms_donaciones.dto.DonacionResponseDTO;
import com.ticketti.ms_donaciones.exception.ResourceNotFoundException;
import com.ticketti.ms_donaciones.model.DonacionModel;
import com.ticketti.ms_donaciones.repository.DonacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DonacionService {

    private final DonacionRepository donacionRepository;

    /** Registrar donación (llamado desde el consumer RabbitMQ en rama 4) */
    public DonacionModel registrar(DonacionModel donacion) {
        return donacionRepository.save(donacion);
    }

    /** Donaciones por organización */
    public List<DonacionResponseDTO> listarPorOrganizacion(Long idOrganizacion) {
        return donacionRepository
                .findByOrganizacion_IdOrganizacion(idOrganizacion)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /** Donaciones por causa social */
    public List<DonacionResponseDTO> listarPorCausa(Long idCausa) {
        return donacionRepository
                .findByCausaSocial_IdCausa(idCausa)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /** Donaciones por evento */
    public List<DonacionResponseDTO> listarPorEvento(Long idEvento) {
        return donacionRepository.findByIdEvento(idEvento)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /** Donaciones por usuario */
    public List<DonacionResponseDTO> listarPorUsuario(Long idUsuario) {
        return donacionRepository.findByIdUsuario(idUsuario)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /** Total recaudado por organización */
    public BigDecimal totalPorOrganizacion(Long idOrganizacion) {
        return donacionRepository.sumMontoByOrganizacion(idOrganizacion);
    }

    /** Total recaudado por causa */
    public BigDecimal totalPorCausa(Long idCausa) {
        return donacionRepository.sumMontoByCausa(idCausa);
    }

    // --- Mapper privado ---
    private DonacionResponseDTO mapToResponse(DonacionModel d) {
        DonacionResponseDTO dto = new DonacionResponseDTO();
        dto.setIdDonacion(d.getIdDonacion());
        dto.setIdCompra(d.getIdCompra());
        dto.setIdEvento(d.getIdEvento());
        dto.setIdUsuario(d.getIdUsuario());
        dto.setNombreCausa(d.getCausaSocial().getNombre());
        dto.setNombreOrganizacion(d.getOrganizacion().getNombre());
        dto.setMonto(d.getMonto());
        dto.setFecha(d.getFecha());
        dto.setEstado(d.getEstado());
        return dto;
    }
}