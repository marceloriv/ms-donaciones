package com.ticketti.ms_donaciones.messaging;

import com.ticketti.ms_donaciones.config.RabbitMQConfig;
import com.ticketti.ms_donaciones.model.CausaSocialModel;
import com.ticketti.ms_donaciones.model.DonacionModel;
import com.ticketti.ms_donaciones.model.OrganizacionModel;
import com.ticketti.ms_donaciones.repository.CausaSocialRepository;
import com.ticketti.ms_donaciones.repository.DonacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Escucha el evento de pago aprobado publicado por MSCarrito.
 * Registra la donación correspondiente en la BD de MS-Donaciones.
 *
 * Flujo:
 * MSCarrito publica → RabbitMQ queue "pago.aprobado"
 * → DonacionConsumer recibe → registra Donacion en BD
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DonacionConsumer {

    private final DonacionRepository donacionRepository;
    private final CausaSocialRepository causaSocialRepository;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_PAGO)
    @Transactional
    public void procesarPagoAprobado(CompraConfirmadaEvent evento) {
        log.info("Evento recibido: pago.aprobado para carrito {}",
                evento.getIdCarrito());

        try {
            // 1. Buscar la causa social elegida por el comprador
            CausaSocialModel causa = causaSocialRepository
                    .findById(evento.getCausaSocialId())
                    .orElse(null);

            if (causa == null) {
                log.warn("CausaSocial {} no encontrada, saltando donacion para carrito {}",
                        evento.getCausaSocialId(), evento.getIdCarrito());
                return;
            }

            // 2. Obtener la organización de esa causa
            OrganizacionModel org = causa.getOrganizacion();

            // 3. Construir y guardar la donación
            DonacionModel donacion = DonacionModel.builder()
                    // IDs lógicos de otros microservicios (sin FK)
                    .idCompra(evento.getIdCarrito())
                    .idPago(evento.getPagoId())
                    .idUsuario(evento.getUsuarioId())
                    .idEvento(evento.getEventoId())
                    // FK locales (misma BD)
                    .causaSocial(causa)
                    .organizacion(org)
                    // El monto ya viene calculado desde MSCarrito
                    .monto(evento.getMontoDonacion())
                    .build();

            donacionRepository.save(donacion);

            log.info("Donacion registrada: {} CLP para causa '{}' org '{}'",
                    evento.getMontoDonacion(),
                    causa.getNombre(),
                    org.getNombre());

        } catch (Exception e) {
            // Si falla, RabbitMQ reintentará según su configuración
            log.error("Error procesando evento pago.aprobado carrito {}: {}",
                    evento.getIdCarrito(), e.getMessage());
            throw e; // re-lanzar para que RabbitMQ reintente
        }
    }
}