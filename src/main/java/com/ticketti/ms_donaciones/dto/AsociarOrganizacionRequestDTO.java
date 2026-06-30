package com.ticketti.ms_donaciones.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AsociarOrganizacionRequestDTO {

    @NotNull(message = "El id de la organización es obligatorio")
    private Long idOrganizacion;
}
