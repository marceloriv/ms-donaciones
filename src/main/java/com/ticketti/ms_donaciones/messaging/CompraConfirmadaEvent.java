package com.ticketti.ms_donaciones.messaging;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.math.BigDecimal;

/**
 * Representa el payload del evento que publica MSCarrito
 * cuando un pago es aprobado. Los campos coinciden con
 * el CarritoDeCompras serializado por MSCarrito.
 *
 * @JsonIgnoreProperties(ignoreUnknown = true) permite que
 * lleguen campos extra sin romper la deserialización.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompraConfirmadaEvent {

    // ID del carrito (= idCompra en nuestra BD)
    private Long idCarrito;

    // ID del pago registrado en MSCarrito
    private Long pagoId;

    // ID del usuario que compró
    private Long usuarioId;

    // ID de la causa social elegida por el comprador
    private Long causaSocialId;

    // Monto total de la compra
    private BigDecimal total;

    // 10% ya calculado por MSCarrito
    private BigDecimal montoDonacion;

    // El evento al que compró entradas (viene del detalle)
    private Long eventoId;
}
