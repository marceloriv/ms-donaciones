package com.ticketti.ms_donaciones.model;

import com.ticketti.ms_donaciones.enums.EstadoCausaSocial;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "causa_social")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CausaSocialModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_causa")
    private Long idCausa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_organizacion", nullable = false)
    private OrganizacionModel organizacion;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 1000)
    private String descripcion;

    // Meta de recaudación (opcional)
    @Column(name = "objetivo_monto", precision = 15, scale = 2)
    private BigDecimal objetivoMonto;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    // null = causa permanente
    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoCausaSocial estado;

    @OneToMany(mappedBy = "causaSocial", cascade = CascadeType.ALL)
    private List<DonacionModel> donaciones;

    @PrePersist
    protected void onCreate() {
        if (this.estado == null) this.estado = EstadoCausaSocial.ACTIVA;
    }
}
