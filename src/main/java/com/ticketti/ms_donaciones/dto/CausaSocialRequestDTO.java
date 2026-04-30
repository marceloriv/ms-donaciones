package com.ticketti.ms_donaciones.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CausaSocialRequestDTO {

    @NotNull(message = "La organización es obligatoria")
    private Long idOrganizacion;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String descripcion;
    private BigDecimal objetivoMonto; // opcional

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    private LocalDate fechaFin; // null = causa permanente
}
