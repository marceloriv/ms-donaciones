package com.ticketti.ms_donaciones.model;

import com.ticketti.ms_donaciones.enums.TipoRepresentante;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "representante")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepresentanteModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_representante")
    private Long idRepresentante;

    // FK a Organizacion (misma BD, sí usamos @ManyToOne)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_organizacion", nullable = false)
    private OrganizacionModel organizacion;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false, length = 12)
    private String rut;

    @Column(length = 100)
    private String cargo;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(length = 20)
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoRepresentante tipo;

    @Column(nullable = false, length = 20)
    private String estado;

    @PrePersist
    protected void onCreate() {
        if (this.estado == null) this.estado = "ACTIVO";
    }
}
