package com.ticketti.ms_donaciones.model;

import com.ticketti.ms_donaciones.enums.EstadoOrganizacion;
import com.ticketti.ms_donaciones.enums.MetodoPago;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "organizacion")
@Data                  // getter, setter, equals, hashCode, toString
@NoArgsConstructor     // constructor vacío (JPA lo requiere)
@AllArgsConstructor    // constructor con todos los campos
@Builder               // permite Organizacion.builder().nombre("X").build()
public class OrganizacionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_organizacion")
    private Long idOrganizacion;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false, length = 12, unique = true)
    private String rut;

    @Column(nullable = false, length = 200)
    private String direccion;

    @Column(nullable = false, length = 20)
    private String telefono;

    @Column(nullable = false, length = 100)
    private String email;

    // --- Datos bancarios ---
    @Column(nullable = false, length = 50)
    private String banco;

    @Column(name = "tipo_cuenta", nullable = false, length = 30)
    private String tipoCuenta;

    @Column(name = "numero_cuenta", nullable = false, length = 30)
    private String numeroCuenta;

    @Column(name = "titular_cuenta", nullable = false, length = 150)
    private String titularCuenta;

    @Column(name = "rut_titular", nullable = false, length = 12)
    private String rutTitular;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago_preferido", nullable = false, length = 20)
    private MetodoPago metodoPagoPreferido;

    // --- Estado y auditoría ---
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoOrganizacion estado;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    // Relaciones internas de este microservicio
    @OneToMany(mappedBy = "organizacion", cascade = CascadeType.ALL)
    private List<RepresentanteModel> representantes;

    @OneToMany(mappedBy = "organizacion", cascade = CascadeType.ALL)
    private List<CausaSocialModel> causasSociales;

    @PrePersist
    protected void onCreate() {
        this.fechaRegistro = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = EstadoOrganizacion.ACTIVA;
        }
    }
}