package com.ticketti.ms_donaciones.client;

import com.ticketti.ms_donaciones.client.dto.EnviarDocumentoCausaRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-mensajeria", url = "${ms-mensajeria.url:http://localhost:8085}")
public interface MensajeriaClient {

    @PostMapping("/api/v1/notificaciones/causa-documento")
    void enviarDocumentoCausa(@RequestBody EnviarDocumentoCausaRequestDTO dto);
}
