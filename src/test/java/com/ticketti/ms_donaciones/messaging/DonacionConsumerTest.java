package com.ticketti.ms_donaciones.messaging;

import com.ticketti.ms_donaciones.model.CausaSocialModel;
import com.ticketti.ms_donaciones.model.DonacionModel;
import com.ticketti.ms_donaciones.model.OrganizacionModel;
import com.ticketti.ms_donaciones.repository.CausaSocialRepository;
import com.ticketti.ms_donaciones.repository.DonacionRepository;
import com.ticketti.ms_donaciones.repository.OrganizacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de DonacionConsumer (RabbitMQ)")
class DonacionConsumerTest {

    @Mock private DonacionRepository donacionRepository;
    @Mock private CausaSocialRepository causaSocialRepository;
    @Mock private OrganizacionRepository organizacionRepository;

    @InjectMocks
    private DonacionConsumer donacionConsumer;

    private CompraConfirmadaEvent evento;
    private CausaSocialModel causa;

    @BeforeEach
    void setUp() {
        OrganizacionModel org = OrganizacionModel.builder()
                .idOrganizacion(1L)
                .nombre("Un Techo Para Chile")
                .build();

        causa = CausaSocialModel.builder()
                .idCausa(1L)
                .nombre("Viviendas 2026")
                .organizacion(org)
                .build();

        evento = new CompraConfirmadaEvent();
        evento.setIdCarrito(100L);
        evento.setPagoId(200L);
        evento.setUsuarioId(5L);
        evento.setCausaSocialId(1L);
        evento.setEventoId(10L);
        evento.setMontoDonacion(new BigDecimal("5000"));
        evento.setTotal(new BigDecimal("50000"));
    }

    @Test
    @DisplayName("Procesar evento válido registra donación")
    void procesarEvento_valido_registraDonacion() {
        when(causaSocialRepository.findById(1L))
                .thenReturn(Optional.of(causa));
        when(donacionRepository.save(any(DonacionModel.class)))
                .thenAnswer(i -> i.getArgument(0));

        donacionConsumer.procesarPagoAprobado(evento);

        verify(donacionRepository, times(1))
                .save(any(DonacionModel.class));
    }

    @Test
    @DisplayName("Evento con causa inexistente omite la donación sin lanzar excepción")
    void procesarEvento_causaNoExiste_omiteDonacion() {
        when(causaSocialRepository.findById(99L))
                .thenReturn(Optional.empty());

        evento.setCausaSocialId(99L);

        // La implementación hace orElse(null) + log.warn + return: no lanza excepción
        donacionConsumer.procesarPagoAprobado(evento);

        verify(donacionRepository, never())
                .save(any(DonacionModel.class));
    }
}
