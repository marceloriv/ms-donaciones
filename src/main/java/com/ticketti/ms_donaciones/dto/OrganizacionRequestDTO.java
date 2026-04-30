package com.ticketti.ms_donaciones.dto;

import com.ticketti.ms_donaciones.enums.MetodoPago;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrganizacionRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El RUT es obligatorio")
    private String rut;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;

    @Email(message = "El email no es válido")
    @NotBlank(message = "El email es obligatorio")
    private String email;

    @NotBlank private String banco;
    @NotBlank private String tipoCuenta;
    @NotBlank private String numeroCuenta;
    @NotBlank private String titularCuenta;
    @NotBlank private String rutTitular;

    @NotNull(message = "El método de pago es obligatorio")
    private MetodoPago metodoPagoPreferido;
}