package com.ticketti.ms_donaciones.repository;

import com.ticketti.ms_donaciones.model.DonacionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface DonacionRepository
        extends JpaRepository<DonacionModel, Long> {

    // Donaciones por organización
    List<DonacionModel> findByOrganizacion_IdOrganizacion(Long idOrganizacion);

    // Donaciones por causa social
    List<DonacionModel> findByCausaSocial_IdCausa(Long idCausa);

    // Donaciones por evento (id lógico externo)
    List<DonacionModel> findByIdEvento(Long idEvento);

    // Donaciones por usuario (id lógico externo)
    List<DonacionModel> findByIdUsuario(Long idUsuario);

    // Total recaudado por organización (solo APROBADAS)
    @Query("SELECT COALESCE(SUM(d.monto), 0) FROM DonacionModel d " +
            "WHERE d.organizacion.idOrganizacion = :idOrganizacion " +
            "AND d.estado = 'APROBADA'")
    BigDecimal sumMontoByOrganizacion(@Param("idOrganizacion") Long idOrganizacion);

    // Total recaudado por causa social
    @Query("SELECT COALESCE(SUM(d.monto), 0) FROM DonacionModel d " +
            "WHERE d.causaSocial.idCausa = :idCausa " +
            "AND d.estado = 'APROBADA'")
    BigDecimal sumMontoByCausa(@Param("idCausa") Long idCausa);
}
