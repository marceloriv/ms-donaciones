package com.ticketti.ms_donaciones.dto;

import com.ticketti.ms_donaciones.enums.EstadoCausaSocial;
import com.ticketti.ms_donaciones.model.CausaSocialModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de respuesta para CausaSocial. Expone solo los datos planos
 * necesarios para el frontend, evitando devolver la entidad JPA
 * directamente (que generaría ciclos de serialización con Organizacion).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CausaSocialResponseDTO {

    private Long idCausa;
    private String nombre;
    private String descripcion;
    private BigDecimal objetivoMonto;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private EstadoCausaSocial estado;

    // Datos planos de la organización, sin anidar el objeto completo
    private Long idOrganizacion;
    private String nombreOrganizacion;

    /**
     * Convierte la entidad CausaSocialModel a su DTO de respuesta.
     *
     * @param causa entidad obtenida desde la base de datos
     * @return DTO listo para serializar como JSON
     */
    public static CausaSocialResponseDTO desdeModelo(CausaSocialModel causa) {
        return CausaSocialResponseDTO.builder()
                .idCausa(causa.getIdCausa())
                .nombre(causa.getNombre())
                .descripcion(causa.getDescripcion())
                .objetivoMonto(causa.getObjetivoMonto())
                .fechaInicio(causa.getFechaInicio())
                .fechaFin(causa.getFechaFin())
                .estado(causa.getEstado())
                .idOrganizacion(
                        causa.getOrganizacion() != null
                                ? causa.getOrganizacion().getIdOrganizacion()
                                : null
                )
                .nombreOrganizacion(
                        causa.getOrganizacion() != null
                                ? causa.getOrganizacion().getNombre()
                                : null
                )
                .build();
    }
}
