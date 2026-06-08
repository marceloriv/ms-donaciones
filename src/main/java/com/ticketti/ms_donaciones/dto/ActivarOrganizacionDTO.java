package com.ticketti.ms_donaciones.dto;


import com.ticketti.ms_donaciones.enums.MetodoPago;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

// DTO que el admin envía al aprobar una organización pendiente.
// Contiene los datos bancarios que el organizador no puede completar.
@Data
public class ActivarOrganizacionDTO {
    @NotBlank
    private String banco;

    @NotBlank
    private String tipoCuenta;

    @NotBlank
    private String numeroCuenta;

    @NotBlank
    private String titularCuenta;

    @NotBlank
    private String rutTitular;

    @NotNull
    private MetodoPago metodoPagoPreferido;
}
