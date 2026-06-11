package com.ticketti.ms_donaciones.service;

import com.ticketti.ms_donaciones.dto.CausaSocialRequestDTO;
import com.ticketti.ms_donaciones.enums.EstadoCausaSocial;
import com.ticketti.ms_donaciones.exception.ResourceNotFoundException;
import com.ticketti.ms_donaciones.model.CausaSocialModel;
import com.ticketti.ms_donaciones.model.OrganizacionModel;
import com.ticketti.ms_donaciones.repository.CausaSocialRepository;
import com.ticketti.ms_donaciones.repository.OrganizacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de CausaSocialService")
class CausaSocialServiceTest {

    @Mock private CausaSocialRepository causaSocialRepository;
    @Mock private OrganizacionRepository organizacionRepository;

    @InjectMocks
    private CausaSocialService causaSocialService;

    private OrganizacionModel org;
    private CausaSocialModel causa;
    private CausaSocialRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        org = OrganizacionModel.builder()
                .idOrganizacion(1L)
                .nombre("Un Techo Para Chile")
                .build();

        causa = CausaSocialModel.builder()
                .idCausa(1L)
                .nombre("Viviendas 2026")
                .organizacion(org)
                .estado(EstadoCausaSocial.ACTIVA)
                .fechaInicio(LocalDate.now())
                .build();

        requestDTO = new CausaSocialRequestDTO();
        requestDTO.setIdOrganizacion(1L);
        requestDTO.setNombre("Viviendas 2026");
        requestDTO.setDescripcion("Construcción de viviendas en zona rural");
        requestDTO.setObjetivoMonto(new BigDecimal("5000000"));
        requestDTO.setFechaInicio(LocalDate.now());
    }

    @Test
    @DisplayName("Crear causa social exitosamente")
    void crear_causaSocial_exitosa() {
        when(organizacionRepository.findById(1L))
                .thenReturn(Optional.of(org));
        when(causaSocialRepository.save(any(CausaSocialModel.class)))
                .thenReturn(causa);

        CausaSocialModel resultado = causaSocialService.crear(requestDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Viviendas 2026");
        verify(causaSocialRepository, times(1)).save(any(CausaSocialModel.class));
    }

    @Test
    @DisplayName("Crear causa con organización inexistente lanza excepción")
    void crear_organizacionNoExiste_lanzaExcepcion() {
        when(organizacionRepository.findById(99L))
                .thenReturn(Optional.empty());

        requestDTO.setIdOrganizacion(99L);

        assertThatThrownBy(() -> causaSocialService.crear(requestDTO))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(causaSocialRepository, never()).save(any());
    }

    @Test
    @DisplayName("Listar causas activas retorna lista")
    void listarActivas_retornaLista() {
        when(causaSocialRepository.findByEstado(EstadoCausaSocial.ACTIVA))
                .thenReturn(List.of(causa));

        List<CausaSocialModel> resultado = causaSocialService.listarActivas();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEstado()).isEqualTo(EstadoCausaSocial.ACTIVA);
    }

    @Test
    @DisplayName("Listar causas por organización retorna lista filtrada")
    void listarPorOrganizacion_retornaFiltrado() {
        when(causaSocialRepository.findByOrganizacion_IdOrganizacion(1L))
                .thenReturn(List.of(causa));

        List<CausaSocialModel> resultado =
                causaSocialService.listarPorOrganizacion(1L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getOrganizacion().getIdOrganizacion())
                .isEqualTo(1L);
    }

    @Test
    @DisplayName("Buscar causa por ID retorna causa correcta")
    void buscarPorId_retornaCausa() {
        when(causaSocialRepository.findById(1L))
                .thenReturn(Optional.of(causa));

        CausaSocialModel resultado = causaSocialService.buscarPorId(1L);

        assertThat(resultado.getIdCausa()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Buscar causa por ID inexistente lanza excepción")
    void buscarPorId_noExiste_lanzaExcepcion() {
        when(causaSocialRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> causaSocialService.buscarPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Desactivar causa cambia estado a INACTIVA")
    void desactivar_causaSocial_exitoso() {
        when(causaSocialRepository.findById(1L))
                .thenReturn(Optional.of(causa));
        when(causaSocialRepository.save(any(CausaSocialModel.class)))
                .thenReturn(causa);

        causaSocialService.desactivar(1L);

        assertThat(causa.getEstado()).isEqualTo(EstadoCausaSocial.INACTIVA);
        verify(causaSocialRepository, times(1)).save(causa);
    }
}