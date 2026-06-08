package com.ticketti.ms_donaciones.service;

import com.ticketti.ms_donaciones.dto.ActivarOrganizacionDTO;
import com.ticketti.ms_donaciones.dto.OrganizacionRequestDTO;
import com.ticketti.ms_donaciones.dto.OrganizacionResponseDTO;
import com.ticketti.ms_donaciones.enums.EstadoOrganizacion;
import com.ticketti.ms_donaciones.exception.BusinessException;
import com.ticketti.ms_donaciones.exception.ResourceNotFoundException;
import com.ticketti.ms_donaciones.model.OrganizacionModel;
import com.ticketti.ms_donaciones.repository.OrganizacionRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor  // inyección por constructor (Lombok)
public class OrganizacionService {

    private final OrganizacionRepository organizacionRepository;

    /** Crear nueva organización (solo admin) */
    public OrganizacionResponseDTO crear(OrganizacionRequestDTO dto) {
        // Regla de negocio: no permitir RUT duplicado
        if (organizacionRepository.existsByRut(dto.getRut())) {
            throw new BusinessException(
                    "Ya existe una organización con RUT " + dto.getRut());
        }
        OrganizacionModel org = mapToEntity(dto);
        OrganizacionModel guardada = organizacionRepository.save(org);
        return mapToResponse(guardada);
    }

    /** Listar todas las organizaciones activas */
    public List<OrganizacionResponseDTO> listarActivas() {
        return organizacionRepository
                .findByEstado(EstadoOrganizacion.ACTIVA)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /** Buscar por ID */
    public OrganizacionResponseDTO buscarPorId(Long id) {
        OrganizacionModel org = organizacionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Organización", id));
        return mapToResponse(org);
    }

    /** Actualizar datos de la organización */
    public OrganizacionResponseDTO actualizar(Long id,
                                              OrganizacionRequestDTO dto) {
        OrganizacionModel org = organizacionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Organización", id));

        // Si cambia el RUT, verificar que no esté en uso por otra org
        if (!org.getRut().equals(dto.getRut()) &&
                organizacionRepository.existsByRut(dto.getRut())) {
            throw new BusinessException(
                    "Ya existe otra organización con RUT " + dto.getRut());
        }

        org.setNombre(dto.getNombre());
        org.setRut(dto.getRut());
        org.setDireccion(dto.getDireccion());
        org.setTelefono(dto.getTelefono());
        org.setEmail(dto.getEmail());
        org.setBanco(dto.getBanco());
        org.setTipoCuenta(dto.getTipoCuenta());
        org.setNumeroCuenta(dto.getNumeroCuenta());
        org.setTitularCuenta(dto.getTitularCuenta());
        org.setRutTitular(dto.getRutTitular());
        org.setMetodoPagoPreferido(dto.getMetodoPagoPreferido());

        return mapToResponse(organizacionRepository.save(org));
    }

    /** Desactivar organización (borrado lógico) */
    public void desactivar(Long id) {
        OrganizacionModel org = organizacionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Organización", id));
        org.setEstado(EstadoOrganizacion.INACTIVA);
        organizacionRepository.save(org);
    }

    /**
     * Guarda el nombre del archivo de convenio subido por el organizador.
     * El archivo físico es manejado por el controller con MultipartFile.
     */
    public OrganizacionResponseDTO guardarDocumento(Long id, String nombreArchivo) {
        OrganizacionModel org = organizacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organización", id));

        org.setDocumentoConvenio(nombreArchivo);
        return mapToResponse(organizacionRepository.save(org));
    }

    /**
     * El admin aprueba la organización: completa datos bancarios y cambia
     * el estado de PENDIENTE a ACTIVA.
     */
    public OrganizacionResponseDTO activar(Long id, @Valid ActivarOrganizacionDTO dto) {
        OrganizacionModel org = organizacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organización", id));

        // Solo se puede activar una organización que esté PENDIENTE
        if (org.getEstado() != EstadoOrganizacion.PENDIENTE) {
            throw new BusinessException(
                    "La organización no está en estado PENDIENTE. Estado actual: "
                            + org.getEstado());
        }

        // Completar datos bancarios que el organizador no pudo ingresar
        org.setBanco(dto.getBanco());
        org.setTipoCuenta(dto.getTipoCuenta());
        org.setNumeroCuenta(dto.getNumeroCuenta());
        org.setTitularCuenta(dto.getTitularCuenta());
        org.setRutTitular(dto.getRutTitular());
        org.setMetodoPagoPreferido(dto.getMetodoPagoPreferido());
        org.setEstado(EstadoOrganizacion.ACTIVA);

        return mapToResponse(organizacionRepository.save(org));
    }

    // --- Mappers privados ---
    private OrganizacionModel mapToEntity(OrganizacionRequestDTO dto) {
        return OrganizacionModel.builder()
                .nombre(dto.getNombre())
                .rut(dto.getRut())
                .direccion(dto.getDireccion())
                .telefono(dto.getTelefono())
                .email(dto.getEmail())
                .banco(dto.getBanco())
                .tipoCuenta(dto.getTipoCuenta())
                .numeroCuenta(dto.getNumeroCuenta())
                .titularCuenta(dto.getTitularCuenta())
                .rutTitular(dto.getRutTitular())
                .metodoPagoPreferido(dto.getMetodoPagoPreferido())
                .build();
    }

    private OrganizacionResponseDTO mapToResponse(OrganizacionModel org) {
        OrganizacionResponseDTO dto = new OrganizacionResponseDTO();
        dto.setIdOrganizacion(org.getIdOrganizacion());
        dto.setNombre(org.getNombre());
        dto.setRut(org.getRut());
        dto.setDireccion(org.getDireccion());
        dto.setTelefono(org.getTelefono());
        dto.setEmail(org.getEmail());
        dto.setMetodoPagoPreferido(org.getMetodoPagoPreferido());
        dto.setEstado(org.getEstado());
        dto.setFechaRegistro(org.getFechaRegistro());
        dto.setDocumentoConvenio(org.getDocumentoConvenio());
        return dto;
    }
}
