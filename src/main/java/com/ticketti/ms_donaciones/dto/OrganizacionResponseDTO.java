package com.ticketti.ms_donaciones.dto;

import com.ticketti.ms_donaciones.enums.EstadoOrganizacion;
import com.ticketti.ms_donaciones.enums.MetodoPago;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrganizacionResponseDTO {
    private Long idOrganizacion;
    private String nombre;
    private String rut;
    private String direccion;
    private String telefono;
    private String email;
    private MetodoPago metodoPagoPreferido;
    private EstadoOrganizacion estado;
    private LocalDateTime fechaRegistro;
    // Datos bancarios no se exponen en el GET público
}
