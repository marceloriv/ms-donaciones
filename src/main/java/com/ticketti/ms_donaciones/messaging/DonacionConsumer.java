package com.ticketti.ms_donaciones.messaging;

import com.ticketti.ms_donaciones.config.RabbitMQConfig;
import com.ticketti.ms_donaciones.exception.ResourceNotFoundException;
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

        // Validar campos requeridos — mensaje malformado se descarta sin reintentar
        if (evento.getPagoId() == null || evento.getUsuarioId() == null
                || evento.getMontoDonacion() == null) {
            log.warn("Evento pago.aprobado carrito {} descartado: campos nulos " +
                    "(pagoId={}, usuarioId={}, montoDonacion={})",
                    evento.getIdCarrito(), evento.getPagoId(),
                    evento.getUsuarioId(), evento.getMontoDonacion());
            return;
        }

        if (evento.getCausaSocialId() == null) {
            log.warn("Evento pago.aprobado carrito {} descartado: sin causa social.",
                    evento.getIdCarrito());
            return;
        }

        try {
            // 1. Buscar la causa social elegida por el comprador
            CausaSocialModel causa = causaSocialRepository
                    .findById(evento.getCausaSocialId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "CausaSocial", evento.getCausaSocialId()));

            // 2. Obtener la organización de esa causa
            OrganizacionModel org = causa.getOrganizacion();

            // 3. Construir y guardar la donación
            DonacionModel donacion = DonacionModel.builder()
                    .idCompra(evento.getIdCarrito())
                    .idPago(evento.getPagoId())
                    .idUsuario(evento.getUsuarioId())
                    .idEvento(evento.getEventoId())
                    .causaSocial(causa)
                    .organizacion(org)
                    .monto(evento.getMontoDonacion())
                    .build();

            donacionRepository.save(donacion);

            log.info("Donacion registrada: {} CLP para causa '{}' org '{}'",
                    evento.getMontoDonacion(), causa.getNombre(), org.getNombre());

        } catch (ResourceNotFoundException e) {
            // Causa no existe — dato referencial inválido, descartar sin reintentar
            log.warn("Evento pago.aprobado carrito {} descartado: {}",
                    evento.getIdCarrito(), e.getMessage());
        } catch (Exception e) {
            // Error transiente (BD, red) — relanzar para que RabbitMQ reintente
            log.error("Error procesando evento pago.aprobado carrito {}: {}",
                    evento.getIdCarrito(), e.getMessage());
            throw e;
        }
    }
}