package com.ticketti.ms_donaciones.service;

import com.ticketti.ms_donaciones.dto.DonacionResponseDTO;
import com.ticketti.ms_donaciones.enums.EstadoDonacion;
import com.ticketti.ms_donaciones.model.CausaSocialModel;
import com.ticketti.ms_donaciones.model.DonacionModel;
import com.ticketti.ms_donaciones.model.OrganizacionModel;
import com.ticketti.ms_donaciones.repository.DonacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests extendidos de DonacionService")
class DonacionServiceExtendedTest {

    @Mock private DonacionRepository donacionRepository;

    @InjectMocks
    private DonacionService donacionService;

    private DonacionModel donacion;

    @BeforeEach
    void setUp() {
        OrganizacionModel org = OrganizacionModel.builder()
                .idOrganizacion(1L)
                .nombre("Fundación Las Rosas")
                .build();

        CausaSocialModel causa = CausaSocialModel.builder()
                .idCausa(1L)
                .nombre("Cuidado adulto mayor")
                .organizacion(org)
                .build();

        donacion = DonacionModel.builder()
                .idDonacion(1L)
                .idCompra(100L)
                .idPago(200L)
                .idUsuario(5L)
                .idEvento(10L)
                .causaSocial(causa)
                .organizacion(org)
                .monto(new BigDecimal("5000"))
                .fecha(LocalDateTime.now())
                .estado(EstadoDonacion.APROBADA)
                .build();
    }

    @Test
    @DisplayName("Listar por evento retorna donaciones filtradas")
    void listarPorEvento_retornaFiltrado() {
        when(donacionRepository.findByIdEvento(10L))
                .thenReturn(List.of(donacion));

        List<DonacionResponseDTO> resultado =
                donacionService.listarPorEvento(10L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getIdEvento()).isEqualTo(10L);
    }

    @Test
    @DisplayName("Listar por usuario retorna donaciones del usuario")
    void listarPorUsuario_retornaFiltrado() {
        when(donacionRepository.findByIdUsuario(5L))
                .thenReturn(List.of(donacion));

        List<DonacionResponseDTO> resultado =
                donacionService.listarPorUsuario(5L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getIdUsuario()).isEqualTo(5L);
    }

    @Test
    @DisplayName("Total por causa retorna suma correcta")
    void totalPorCausa_retornaSuma() {
        when(donacionRepository.sumMontoByCausa(1L))
                .thenReturn(new BigDecimal("20000"));

        BigDecimal total = donacionService.totalPorCausa(1L);

        assertThat(total).isEqualByComparingTo(new BigDecimal("20000"));
    }

    @Test
    @DisplayName("Listar por organización retorna lista vacía cuando no hay donaciones")
    void listarPorOrganizacion_sinDonaciones_retornaVacio() {
        when(donacionRepository.findByOrganizacion_IdOrganizacion(99L))
                .thenReturn(List.of());

        List<DonacionResponseDTO> resultado =
                donacionService.listarPorOrganizacion(99L);

        assertThat(resultado).isEmpty();
    }
}
