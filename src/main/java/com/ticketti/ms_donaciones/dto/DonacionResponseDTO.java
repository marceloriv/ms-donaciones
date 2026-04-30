package com.ticketti.ms_donaciones.dto;

import com.ticketti.ms_donaciones.enums.EstadoDonacion;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DonacionResponseDTO {
    private Long idDonacion;
    private Long idCompra;
    private Long idEvento;
    private Long idUsuario;
    private String nombreCausa;
    private String nombreOrganizacion;
    private BigDecimal monto;
    private LocalDateTime fecha;
    private EstadoDonacion estado;
}
