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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de DonacionService")
class DonacionServiceTest {

    @Mock
    private DonacionRepository donacionRepository;

    @InjectMocks
    private DonacionService donacionService;

    private DonacionModel donacion;
    private OrganizacionModel org;
    private CausaSocialModel causa;

    @BeforeEach
    void setUp() {
        org = OrganizacionModel.builder()
                .idOrganizacion(1L)
                .nombre("Fundación Las Rosas")
                .build();

        causa = CausaSocialModel.builder()
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
    @DisplayName("Listar donaciones por organización retorna lista correcta")
    void listarPorOrganizacion_retornaLista() {
        when(donacionRepository.findByOrganizacion_IdOrganizacion(1L))
                .thenReturn(List.of(donacion));

        List<DonacionResponseDTO> resultado =
                donacionService.listarPorOrganizacion(1L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getMonto())
                .isEqualByComparingTo(new BigDecimal("5000"));
        assertThat(resultado.get(0).getNombreOrganizacion())
                .isEqualTo("Fundación Las Rosas");
    }

    @Test
    @DisplayName("Total por organización retorna suma correcta")
    void totalPorOrganizacion_retornaSuma() {
        when(donacionRepository.sumMontoByOrganizacion(1L))
                .thenReturn(new BigDecimal("15000"));

        BigDecimal total = donacionService.totalPorOrganizacion(1L);

        assertThat(total).isEqualByComparingTo(new BigDecimal("15000"));
    }

    @Test
    @DisplayName("Registrar donación llama al repositorio una vez")
    void registrar_donacion_guardaCorrectamente() {
        when(donacionRepository.save(any(DonacionModel.class)))
                .thenReturn(donacion);

        DonacionModel resultado = donacionService.registrar(donacion);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getMonto())
                .isEqualByComparingTo(new BigDecimal("5000"));
        verify(donacionRepository, times(1)).save(donacion);
    }

    @Test
    @DisplayName("Listar por causa retorna donaciones filtradas")
    void listarPorCausa_retornaFiltrado() {
        when(donacionRepository.findByCausaSocial_IdCausa(1L))
                .thenReturn(List.of(donacion));

        List<DonacionResponseDTO> resultado =
                donacionService.listarPorCausa(1L);

        assertThat(resultado).hasSize(1);










        
        assertThat(resultado.get(0).getNombreCausa())
                .isEqualTo("Cuidado adulto mayor");
    }
}
