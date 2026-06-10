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
        if (organizacionRepository.count() > 0) {
            return;
        }

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
    }

    private void seedCausasSociales() {
        if (causaSocialRepository.count() > 0) {
            return;
        }

        OrganizacionModel organizacion = organizacionRepository.findByRut("11111111-1")
                .orElseGet(() -> organizacionRepository.findAll().stream()
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("No hay organizaciones para asociar causas")));

        causaSocialRepository.save(CausaSocialModel.builder()
                .organizacion(organizacion)
                .nombre("Becas escolares")
                .descripcion("Apoyo escolar para niños y niñas en situación vulnerable.")
                .objetivoMonto(new BigDecimal("1000000.00"))
                .fechaInicio(LocalDate.now())
                .estado(EstadoCausaSocial.ACTIVA)
                .build());

        causaSocialRepository.save(CausaSocialModel.builder()
                .organizacion(organizacion)
                .nombre("Ayuda alimentaria")
                .descripcion("Entrega de canastas de alimentos a familias vulnerables.")
                .objetivoMonto(new BigDecimal("750000.00"))
                .fechaInicio(LocalDate.now())
                .estado(EstadoCausaSocial.ACTIVA)
                .build());
    }
}
