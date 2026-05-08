package com.ticketti.ms_donaciones.service;

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
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de OrganizacionService")
class OrganizacionServiceTest {

    @Mock
    private OrganizacionRepository organizacionRepository;

    @InjectMocks
    private OrganizacionService organizacionService;

    private OrganizacionRequestDTO requestDTO;
    private OrganizacionModel organizacion;

    @BeforeEach
    void setUp() {
        requestDTO = new OrganizacionRequestDTO();
        requestDTO.setNombre("Un Techo Para Chile");
        requestDTO.setRut("76543210-1");
        requestDTO.setDireccion("Av. Holanda 1015");
        requestDTO.setTelefono("+56912345678");
        requestDTO.setEmail("contacto@techo.org");
        requestDTO.setBanco("Banco Estado");
        requestDTO.setTipoCuenta("Corriente");
        requestDTO.setNumeroCuenta("123456789");
        requestDTO.setTitularCuenta("Un Techo Para Chile");
        requestDTO.setRutTitular("76543210-1");
        requestDTO.setMetodoPagoPreferido(MetodoPago.TRANSFERENCIA);

        organizacion = OrganizacionModel.builder()
                .idOrganizacion(1L)
                .nombre("Un Techo Para Chile")
                .rut("76543210-1")
                .estado(EstadoOrganizacion.ACTIVA)
                .build();
    }

    @Test
    @DisplayName("Crear organización exitosamente")
    void crear_organizacion_exitosa() {
        when(organizacionRepository.existsByRut(anyString()))
                .thenReturn(false);
        when(organizacionRepository.save(any(OrganizacionModel.class)))
                .thenReturn(organizacion);

        OrganizacionResponseDTO resultado =
                organizacionService.crear(requestDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre())
                .isEqualTo("Un Techo Para Chile");
        verify(organizacionRepository, times(1))
                .save(any(OrganizacionModel.class));
    }

    @Test
    @DisplayName("Crear organización con RUT duplicado lanza excepción")
    void crear_organizacion_rut_duplicado() {
        when(organizacionRepository.existsByRut("76543210-1"))
                .thenReturn(true);

        assertThatThrownBy(() ->
                organizacionService.crear(requestDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("76543210-1");

        verify(organizacionRepository, never())
                .save(any(OrganizacionModel.class));
    }

    @Test
    @DisplayName("Buscar por ID inexistente lanza ResourceNotFoundException")
    void buscarPorId_noExiste_lanzaExcepcion() {
        when(organizacionRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                organizacionService.buscarPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Desactivar organización cambia estado a INACTIVA")
    void desactivar_organizacion_exitoso() {
        when(organizacionRepository.findById(1L))
                .thenReturn(Optional.of(organizacion));
        when(organizacionRepository.save(any(OrganizacionModel.class)))
                .thenReturn(organizacion);

        organizacionService.desactivar(1L);

        assertThat(organizacion.getEstado())
                .isEqualTo(EstadoOrganizacion.INACTIVA);
        verify(organizacionRepository, times(1))
                .save(organizacion);
    }
}