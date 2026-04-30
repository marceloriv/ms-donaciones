package com.ticketti.ms_donaciones.repository;

import com.ticketti.ms_donaciones.enums.EstadoOrganizacion;
import com.ticketti.ms_donaciones.model.OrganizacionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizacionRepository extends JpaRepository<OrganizacionModel, Long> {

    // Buscar por estado (ACTIVA / INACTIVA)
    List<OrganizacionModel> findByEstado(EstadoOrganizacion estado);

    // Verificar si ya existe un RUT registrado
    boolean existsByRut(String rut);

    // Buscar por RUT
    Optional<OrganizacionModel> findByRut(String rut);
}
