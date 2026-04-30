package com.ticketti.ms_donaciones.model;

import com.ticketti.ms_donaciones.enums.EstadoDonacion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "donacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonacionModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_donacion")
    private Long idDonacion;

    // --- IDs lógicos (otros microservicios, NO son FK reales) ---
    @Column(name = "id_compra", nullable = false)
    private Long idCompra;

    @Column(name = "id_pago", nullable = false)
    private Long idPago;

    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @Column(name = "id_evento", nullable = false)
    private Long idEvento;

    // --- FK locales (mismo microservicio) ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_causa", nullable = false)
    private CausaSocialModel causaSocial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_organizacion", nullable = false)
    private OrganizacionModel organizacion;

    // --- Datos propios ---
    // Monto calculado en MSCarrito (10% del total de la compra)
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal monto;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoDonacion estado;

    @PrePersist
    protected void onCreate() {
        this.fecha = LocalDateTime.now();
        if (this.estado == null) this.estado = EstadoDonacion.APROBADA;
    }
}
