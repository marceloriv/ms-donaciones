package com.ticketti.ms_donaciones.service;

import com.ticketti.ms_donaciones.dto.ActivarOrganizacionDTO;
import com.ticketti.ms_donaciones.dto.OrganizacionRequestDTO;
import com.ticketti.ms_donaciones.dto.OrganizacionResponseDTO;
import com.ticketti.ms_donaciones.enums.EstadoOrganizacion;
import com.ticketti.ms_donaciones.enums.MetodoPago;
import com.ticketti.ms_donaciones.exception.BusinessException;
import com.ticketti.ms_donaciones.exception.ResourceNotFoundException;
import com.ticketti.ms_donaciones.model.OrganizacionModel;
import com.ticketti.ms_donaciones.repository.OrganizacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests extendidos de OrganizacionService")
class OrganizacionServiceExtendedTest {

    @Mock private OrganizacionRepository organizacionRepository;

    @InjectMocks
    private OrganizacionService organizacionService;

    private OrganizacionModel orgActiva;
    private OrganizacionModel orgPendiente;
    private OrganizacionRequestDTO requestDTO;
    private ActivarOrganizacionDTO activarDTO;

    @BeforeEach
    void setUp() {
        orgActiva = OrganizacionModel.builder()
                .idOrganizacion(1L)
                .nombre("Fundación Manos Unidas")
                .rut("11111111-1")
                .estado(EstadoOrganizacion.ACTIVA)
                .build();

        orgPendiente = OrganizacionModel.builder()
                .idOrganizacion(2L)
                .nombre("Nueva Organización")
                .rut("22222222-2")
                .estado(EstadoOrganizacion.PENDIENTE)
                .build();

        requestDTO = new OrganizacionRequestDTO();
        requestDTO.setNombre("Fundación Manos Unidas");
        requestDTO.setRut("11111111-1");
        requestDTO.setEmail("contacto@manos.cl");

        activarDTO = new ActivarOrganizacionDTO();
        activarDTO.setBanco("Banco Estado");
        activarDTO.setTipoCuenta("Corriente");
        activarDTO.setNumeroCuenta("123456789");
        activarDTO.setTitularCuenta("Nueva Organización");
        activarDTO.setRutTitular("22222222-2");
        activarDTO.setMetodoPagoPreferido(MetodoPago.TRANSFERENCIA);
    }

    @Test
    @DisplayName("Listar activas retorna solo organizaciones activas")
    void listarActivas_retornaSoloActivas() {
        when(organizacionRepository.findByEstado(EstadoOrganizacion.ACTIVA))
                .thenReturn(List.of(orgActiva));

        List<OrganizacionResponseDTO> resultado =
                organizacionService.listarActivas();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEstado())
                .isEqualTo(EstadoOrganizacion.ACTIVA);
    }

    @Test
    @DisplayName("Actualizar organización con RUT duplicado lanza excepción")
    void actualizar_rutDuplicado_lanzaExcepcion() {
        orgActiva.setRut("11111111-1");
        requestDTO.setRut("33333333-3");

        when(organizacionRepository.findById(1L))
                .thenReturn(Optional.of(orgActiva));
        when(organizacionRepository.existsByRut("33333333-3"))
                .thenReturn(true);

        assertThatThrownBy(() ->
                organizacionService.actualizar(1L, requestDTO))
                .isInstanceOf(BusinessException.class);

        verify(organizacionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Actualizar organización exitosamente")
    void actualizar_organizacion_exitosa() {
        when(organizacionRepository.findById(1L))
                .thenReturn(Optional.of(orgActiva));
        when(organizacionRepository.save(any(OrganizacionModel.class)))
                .thenReturn(orgActiva);

        OrganizacionResponseDTO resultado =
                organizacionService.actualizar(1L, requestDTO);

        assertThat(resultado).isNotNull();
        verify(organizacionRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Guardar documento convenio exitosamente")
    void guardarDocumento_exitoso() {
        when(organizacionRepository.findById(2L))
                .thenReturn(Optional.of(orgPendiente));
        when(organizacionRepository.save(any(OrganizacionModel.class)))
                .thenReturn(orgPendiente);

        OrganizacionResponseDTO resultado =
                organizacionService.guardarDocumento(2L, "convenio.pdf");

        assertThat(orgPendiente.getDocumentoConvenio())
                .isEqualTo("convenio.pdf");
        verify(organizacionRepository, times(1)).save(orgPendiente);
    }

    @Test
    @DisplayName("Activar organización PENDIENTE exitosamente")
    void activar_organizacionPendiente_exitoso() {
        when(organizacionRepository.findById(2L))
                .thenReturn(Optional.of(orgPendiente));
        when(organizacionRepository.save(any(OrganizacionModel.class)))
                .thenReturn(orgPendiente);

        OrganizacionResponseDTO resultado =
                organizacionService.activar(2L, activarDTO);

        assertThat(orgPendiente.getEstado())
                .isEqualTo(EstadoOrganizacion.ACTIVA);
        assertThat(orgPendiente.getBanco()).isEqualTo("Banco Estado");
        verify(organizacionRepository, times(1)).save(orgPendiente);
    }

    @Test
    @DisplayName("Activar organización que no está PENDIENTE lanza excepción")
    void activar_organizacionNoEsPendiente_lanzaExcepcion() {
        when(organizacionRepository.findById(1L))
                .thenReturn(Optional.of(orgActiva));

        assertThatThrownBy(() ->
                organizacionService.activar(1L, activarDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("PENDIENTE");

        verify(organizacionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Guardar documento en organización inexistente lanza excepción")
    void guardarDocumento_organizacionNoExiste_lanzaExcepcion() {
        when(organizacionRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                organizacionService.guardarDocumento(99L, "convenio.pdf"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
