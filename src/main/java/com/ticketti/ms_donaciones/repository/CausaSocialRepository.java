package com.ticketti.ms_donaciones.repository;

import com.ticketti.ms_donaciones.enums.EstadoCausaSocial;
import com.ticketti.ms_donaciones.model.CausaSocialModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CausaSocialRepository
        extends JpaRepository<CausaSocialModel, Long> {

    // Causas de una organización específica
    List<CausaSocialModel> findByOrganizacion_IdOrganizacion(Long idOrganizacion);

    // Causas activas (las que el comprador puede elegir)
    List<CausaSocialModel> findByEstado(EstadoCausaSocial estado);

    // Combinado: causas activas de una organización
    List<CausaSocialModel> findByOrganizacion_IdOrganizacionAndEstado(
            Long idOrganizacion, EstadoCausaSocial estado);
}
