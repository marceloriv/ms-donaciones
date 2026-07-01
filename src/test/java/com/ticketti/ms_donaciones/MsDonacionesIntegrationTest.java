package com.ticketti.ms_donaciones;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketti.ms_donaciones.client.MensajeriaClient;
import com.ticketti.ms_donaciones.dto.AsociarOrganizacionRequestDTO;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import com.ticketti.ms_donaciones.dto.CausaSocialRequestDTO;
import com.ticketti.ms_donaciones.dto.OrganizacionRequestDTO;
import com.ticketti.ms_donaciones.repository.CausaSocialRepository;
import com.ticketti.ms_donaciones.repository.OrganizacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        // Permite que RabbitTestConfig (interna) sobreescriba rabbitListenerContainerFactory
        // de RabbitMQConfig. Necesario para desactivar el auto-start de listeners
        // sin un broker disponible en el entorno de tests.
        "spring.main.allow-bean-definition-overriding=true"
})
@WebAppConfiguration
@Transactional
class MsDonacionesIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private CausaSocialRepository causaSocialRepository;

    @Autowired
    private OrganizacionRepository organizacionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // Clientes externos mockeados para no depender de otros microservicios
    @MockitoBean
    private MensajeriaClient mensajeriaClient;

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    /**
     * Reemplaza la rabbitListenerContainerFactory de RabbitMQConfig por una
     * que NO hace auto-start: los @RabbitListener quedan registrados pero los
     * contenedores nunca intentan conectarse al broker en tiempo de test.
     */
    @TestConfiguration
    static class RabbitTestConfig {
        @Bean
        @Primary
        SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
                ConnectionFactory connectionFactory,
                Jackson2JsonMessageConverter messageConverter) {
            SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
            factory.setConnectionFactory(connectionFactory);
            factory.setMessageConverter(messageConverter);
            factory.setAutoStartup(false);
            return factory;
        }
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        causaSocialRepository.deleteAll();
        organizacionRepository.deleteAll();
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Long crearOrganizacionYRetornarId() throws Exception {
        OrganizacionRequestDTO dto = new OrganizacionRequestDTO();
        dto.setNombre("Fundación Test");
        dto.setRut("12345678-9");
        dto.setEmail("test@fundacion.cl");
        dto.setTelefono("+56912345678");
        dto.setDireccion("Av. Test 123");

        var result = mockMvc.perform(post("/api/v1/organizaciones")
                        .header("X-Usuario-Id", "1")
                        .header("X-Rol-Usuario-Id", "ADMINPLATAFORMA")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString())
                .path("idOrganizacion").asLong();
    }

    private Long crearCausaSocialYRetornarId() throws Exception {
        CausaSocialRequestDTO dto = new CausaSocialRequestDTO();
        dto.setNombre("Causa Test");
        dto.setDescripcion("Descripción de prueba");
        dto.setObjetivoMonto(new BigDecimal("1000000"));
        dto.setFechaInicio(LocalDate.now());

        var result = mockMvc.perform(post("/api/v1/causas")
                        .header("X-Usuario-Id", "1")
                        .header("X-Rol-Usuario-Id", "ORGANIZADOR")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString())
                .path("idCausa").asLong();
    }

    // ── Tests: Organizaciones ─────────────────────────────────────────────────

    @Test
    void crearOrganizacion_datosValidos_retorna201YPersiste() throws Exception {
        OrganizacionRequestDTO dto = new OrganizacionRequestDTO();
        dto.setNombre("Fundación Test");
        dto.setRut("12345678-9");
        dto.setEmail("test@fundacion.cl");
        dto.setTelefono("+56912345678");
        dto.setDireccion("Av. Test 123");

        mockMvc.perform(post("/api/v1/organizaciones")
                        .header("X-Usuario-Id", "1")
                        .header("X-Rol-Usuario-Id", "ADMINPLATAFORMA")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idOrganizacion").exists())
                .andExpect(jsonPath("$.nombre").value("Fundación Test"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));

        assertThat(organizacionRepository.count()).isEqualTo(1);
    }

    @Test
    void buscarOrganizacion_inexistente_retorna404() throws Exception {
        mockMvc.perform(get("/api/v1/organizaciones/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listarOrganizaciones_sinDatos_retornaListaVacia() throws Exception {
        mockMvc.perform(get("/api/v1/organizaciones"))
                .andExpect(status().isOk());
    }

    // ── Tests: Causas Sociales ────────────────────────────────────────────────

    @Test
    void crearCausaSocial_sinOrganizacion_estadoInicialEsPendiente() throws Exception {
        CausaSocialRequestDTO dto = new CausaSocialRequestDTO();
        dto.setNombre("Causa sin org");
        dto.setFechaInicio(LocalDate.now());

        mockMvc.perform(post("/api/v1/causas")
                        .header("X-Usuario-Id", "2")
                        .header("X-Rol-Usuario-Id", "ORGANIZADOR")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCausa").exists())
                .andExpect(jsonPath("$.nombre").value("Causa sin org"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.documentoEnviado").value(false));

        assertThat(causaSocialRepository.count()).isEqualTo(1);
    }

    @Test
    void crearCausaSocial_conOrganizacion_vinculaCorrectamente() throws Exception {
        Long idOrg = crearOrganizacionYRetornarId();

        CausaSocialRequestDTO dto = new CausaSocialRequestDTO();
        dto.setNombre("Causa con org");
        dto.setIdOrganizacion(idOrg);
        dto.setObjetivoMonto(new BigDecimal("500000"));
        dto.setFechaInicio(LocalDate.now());

        mockMvc.perform(post("/api/v1/causas")
                        .header("X-Usuario-Id", "1")
                        .header("X-Rol-Usuario-Id", "ORGANIZADOR")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idOrganizacion").value(idOrg))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    void activarCausa_desdePendiente_cambiaEstadoAActiva() throws Exception {
        Long id = crearCausaSocialYRetornarId();

        mockMvc.perform(put("/api/v1/causas/{id}/activar", id)
                        .header("X-Usuario-Id", "1")
                        .header("X-Rol-Usuario-Id", "ADMINPLATAFORMA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ACTIVA"));

        var causa = causaSocialRepository.findById(id).orElseThrow();
        assertThat(causa.getEstado().name()).isEqualTo("ACTIVA");
    }

    @Test
    void activarCausa_desdeActiva_retorna400() throws Exception {
        Long id = crearCausaSocialYRetornarId();

        // Primera activación
        mockMvc.perform(put("/api/v1/causas/{id}/activar", id)
                        .header("X-Usuario-Id", "1")
                        .header("X-Rol-Usuario-Id", "ADMINPLATAFORMA"))
                .andExpect(status().isOk());

        // Segunda activación debe fallar
        mockMvc.perform(put("/api/v1/causas/{id}/activar", id)
                        .header("X-Usuario-Id", "1")
                        .header("X-Rol-Usuario-Id", "ADMINPLATAFORMA"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void activarCausa_noExiste_retorna404() throws Exception {
        mockMvc.perform(put("/api/v1/causas/9999/activar")
                        .header("X-Usuario-Id", "1")
                        .header("X-Rol-Usuario-Id", "ADMINPLATAFORMA"))
                .andExpect(status().isNotFound());
    }

    @Test
    void asociarOrganizacion_causaExistente_retorna200YVincula() throws Exception {
        Long idOrg = crearOrganizacionYRetornarId();
        Long idCausa = crearCausaSocialYRetornarId();

        AsociarOrganizacionRequestDTO dto = new AsociarOrganizacionRequestDTO();
        dto.setIdOrganizacion(idOrg);

        mockMvc.perform(put("/api/v1/causas/{id}/organizacion", idCausa)
                        .header("X-Usuario-Id", "1")
                        .header("X-Rol-Usuario-Id", "ADMINPLATAFORMA")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idOrganizacion").value(idOrg))
                .andExpect(jsonPath("$.nombreOrganizacion").exists());

        var causa = causaSocialRepository.findById(idCausa).orElseThrow();
        assertThat(causa.getOrganizacion()).isNotNull();
        assertThat(causa.getOrganizacion().getIdOrganizacion()).isEqualTo(idOrg);
    }

    @Test
    void asociarOrganizacion_orgNoExiste_retorna404() throws Exception {
        Long idCausa = crearCausaSocialYRetornarId();

        AsociarOrganizacionRequestDTO dto = new AsociarOrganizacionRequestDTO();
        dto.setIdOrganizacion(9999L);

        mockMvc.perform(put("/api/v1/causas/{id}/organizacion", idCausa)
                        .header("X-Usuario-Id", "1")
                        .header("X-Rol-Usuario-Id", "ADMINPLATAFORMA")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void listarCausasActivas_soloMuestraActivasConOrganizacion() throws Exception {
        Long idOrg = crearOrganizacionYRetornarId();

        // Causa PENDIENTE sin org — NO debe aparecer en activas
        crearCausaSocialYRetornarId();

        // Causa ACTIVA con org — SÍ debe aparecer en activas
        CausaSocialRequestDTO dto = new CausaSocialRequestDTO();
        dto.setNombre("Causa Activa con Org");
        dto.setIdOrganizacion(idOrg);
        dto.setFechaInicio(LocalDate.now());

        var result = mockMvc.perform(post("/api/v1/causas")
                        .header("X-Usuario-Id", "1")
                        .header("X-Rol-Usuario-Id", "ORGANIZADOR")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn();
        Long idActivable = objectMapper.readTree(result.getResponse().getContentAsString())
                .path("idCausa").asLong();

        mockMvc.perform(put("/api/v1/causas/{id}/activar", idActivable)
                        .header("X-Usuario-Id", "1")
                        .header("X-Rol-Usuario-Id", "ADMINPLATAFORMA"))
                .andExpect(status().isOk());

        // Solo debe aparecer la causa activa con organización
        mockMvc.perform(get("/api/v1/causas/activas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Causa Activa con Org"))
                .andExpect(jsonPath("$[0].estado").value("ACTIVA"));
    }

    @Test
    void buscarCausaPorId_existente_retorna200() throws Exception {
        Long id = crearCausaSocialYRetornarId();

        mockMvc.perform(get("/api/v1/causas/{id}", id)
                        .header("X-Usuario-Id", "1")
                        .header("X-Rol-Usuario-Id", "ORGANIZADOR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCausa").value(id))
                .andExpect(jsonPath("$.nombre").value("Causa Test"));
    }

    @Test
    void listarTodasCausas_incluyePendientesYActivas() throws Exception {
        crearCausaSocialYRetornarId();

        mockMvc.perform(get("/api/v1/causas/todas")
                        .header("X-Usuario-Id", "1")
                        .header("X-Rol-Usuario-Id", "ADMINPLATAFORMA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void desactivarCausa_existente_cambiaEstadoAInactiva() throws Exception {
        Long id = crearCausaSocialYRetornarId();

        mockMvc.perform(delete("/api/v1/causas/{id}", id)
                        .header("X-Usuario-Id", "1")
                        .header("X-Rol-Usuario-Id", "ADMINPLATAFORMA"))
                .andExpect(status().isNoContent());

        var causa = causaSocialRepository.findById(id).orElseThrow();
        assertThat(causa.getEstado().name()).isEqualTo("INACTIVA");
    }

    @Test
    void flujoCompleto_crearActivarAsociarOrg_causaAparaceEnActivas() throws Exception {
        Long idOrg = crearOrganizacionYRetornarId();
        Long idCausa = crearCausaSocialYRetornarId();

        // 1. Asociar organización
        AsociarOrganizacionRequestDTO dtoOrg = new AsociarOrganizacionRequestDTO();
        dtoOrg.setIdOrganizacion(idOrg);
        mockMvc.perform(put("/api/v1/causas/{id}/organizacion", idCausa)
                        .header("X-Usuario-Id", "1")
                        .header("X-Rol-Usuario-Id", "ADMINPLATAFORMA")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoOrg)))
                .andExpect(status().isOk());

        // 2. Activar
        mockMvc.perform(put("/api/v1/causas/{id}/activar", idCausa)
                        .header("X-Usuario-Id", "1")
                        .header("X-Rol-Usuario-Id", "ADMINPLATAFORMA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ACTIVA"));

        // 3. Verificar que aparece en listado público de activas
        mockMvc.perform(get("/api/v1/causas/activas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].idOrganizacion").value(idOrg));
    }
}
