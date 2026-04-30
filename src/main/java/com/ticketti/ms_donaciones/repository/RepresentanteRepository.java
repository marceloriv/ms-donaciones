package com.ticketti.ms_donaciones.repository;

import com.ticketti.ms_donaciones.enums.TipoRepresentante;
import com.ticketti.ms_donaciones.model.RepresentanteModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RepresentanteRepository
        extends JpaRepository<RepresentanteModel, Long> {

    // Todos los representantes de una organización
    List<RepresentanteModel> findByOrganizacion_IdOrganizacion(Long idOrganizacion);

    // Solo por tipo (LEGAL o COORDINADOR)
    List<RepresentanteModel> findByOrganizacion_IdOrganizacionAndTipo(
            Long idOrganizacion, TipoRepresentante tipo);
}
