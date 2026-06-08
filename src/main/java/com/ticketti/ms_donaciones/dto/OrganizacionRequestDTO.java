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

    private String banco;
    private String tipoCuenta;
    private String numeroCuenta;
    private String titularCuenta;
    private String rutTitular;
    private MetodoPago metodoPagoPreferido;
}