package com.ticketti.ms_donaciones.config;

import com.ticketti.ms_donaciones.enums.EstadoCausaSocial;
import com.ticketti.ms_donaciones.enums.EstadoOrganizacion;
import com.ticketti.ms_donaciones.model.CausaSocialModel;
import com.ticketti.ms_donaciones.model.OrganizacionModel;
import com.ticketti.ms_donaciones.repository.CausaSocialRepository;
import com.ticketti.ms_donaciones.repository.OrganizacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private final OrganizacionRepository organizacionRepository;
    private final CausaSocialRepository causaSocialRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedOrganizaciones();
        seedCausasSociales();
    }

    private void seedOrganizaciones() {
        if (organizacionRepository.count() > 0) return;

        organizacionRepository.save(OrganizacionModel.builder()
                .nombre("Fundación Manos Unidas")
                .rut("11111111-1")
                .direccion("Av. Principal 123")
                .telefono("+56911111111")
                .email("contacto@manosunidas.cl")
                .estado(EstadoOrganizacion.ACTIVA)
                .build());

        organizacionRepository.save(OrganizacionModel.builder()
                .nombre("Comedor Solidario Norte")
                .rut("22222222-2")
                .direccion("Calle Esperanza 456")
                .telefono("+56922222222")
                .email("hola@comedorsolidario.cl")
                .estado(EstadoOrganizacion.PENDIENTE)
                .build());

        organizacionRepository.save(OrganizacionModel.builder()
                .nombre("Un Techo Para Chile")
                .rut("76543210-1")
                .direccion("Av. Holanda 1015, Providencia")
                .telefono("+56912345678")
                .email("contacto@techo.org")
                .banco("Banco Estado")
                .tipoCuenta("Corriente")
                .numeroCuenta("123456789")
                .titularCuenta("Un Techo Para Chile")
                .rutTitular("76543210-1")
                .estado(EstadoOrganizacion.ACTIVA)
                .build());

        organizacionRepository.save(OrganizacionModel.builder()
                .nombre("Fundación Las Rosas")
                .rut("65432109-2")
                .direccion("Camino El Observatorio 4903, Las Condes")
                .telefono("+56987654321")
                .email("info@lasrosas.cl")
                .banco("Banco de Chile")
                .tipoCuenta("Corriente")
                .numeroCuenta("987654321")
                .titularCuenta("Fundación Las Rosas")
                .rutTitular("65432109-2")
                .estado(EstadoOrganizacion.ACTIVA)
                .build());
    }

    private void seedCausasSociales() {
        if (causaSocialRepository.count() > 0) return;

        OrganizacionModel orgPrincipal = organizacionRepository
                .findByRut("11111111-1").orElseThrow();
        OrganizacionModel orgTecho = organizacionRepository
                .findByRut("76543210-1").orElse(orgPrincipal);
        OrganizacionModel orgLasRosas = organizacionRepository
                .findByRut("65432109-2").orElse(orgPrincipal);

        causaSocialRepository.save(CausaSocialModel.builder()
                .organizacion(orgPrincipal)
                .nombre("Becas escolares")
                .descripcion("Apoyo escolar para niños y niñas en situación vulnerable.")
                .objetivoMonto(new BigDecimal("1000000.00"))
                .fechaInicio(LocalDate.now())
                .estado(EstadoCausaSocial.ACTIVA)
                .build());

        causaSocialRepository.save(CausaSocialModel.builder()
                .organizacion(orgPrincipal)
                .nombre("Ayuda alimentaria")
                .descripcion("Entrega de canastas de alimentos a familias vulnerables.")
                .objetivoMonto(new BigDecimal("750000.00"))
                .fechaInicio(LocalDate.now())
                .estado(EstadoCausaSocial.ACTIVA)
                .build());

        causaSocialRepository.save(CausaSocialModel.builder()
                .organizacion(orgTecho)
                .nombre("Viviendas 2026")
                .descripcion("Construcción de viviendas en zona rural.")
                .objetivoMonto(new BigDecimal("5000000.00"))
                .fechaInicio(LocalDate.of(2026, 1, 1))
                .estado(EstadoCausaSocial.ACTIVA)
                .build());

        causaSocialRepository.save(CausaSocialModel.builder()
                .organizacion(orgLasRosas)
                .nombre("Cuidado adulto mayor")
                .descripcion("Atención y acompañamiento a adultos mayores.")
                .objetivoMonto(new BigDecimal("3000000.00"))
                .fechaInicio(LocalDate.of(2026, 1, 1))
                .estado(EstadoCausaSocial.ACTIVA)
                .build());
    }
}