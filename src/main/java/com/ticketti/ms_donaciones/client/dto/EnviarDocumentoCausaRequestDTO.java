package com.ticketti.ms_donaciones.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnviarDocumentoCausaRequestDTO {
    private Long idCausa;
    private String nombreCausa;
    private String nombreOrganizador;
    private String archivoBase64;
    private String nombreArchivo;
}
