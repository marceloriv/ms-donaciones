package com.ticketti.ms_donaciones.config;

import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configura el comportamiento de reintentos de los clientes Feign de
 * ms-donaciones. Sin esta configuración Feign falla de inmediato ante el
 * primer error transiente; con el Retryer intenta hasta 2 veces adicionales
 * con backoff exponencial antes de propagar la excepción.
 */
@Configuration
public class FeignRetryConfig {

    @Bean
    public Retryer feignRetryer() {
        // period=1000ms, maxPeriod=2000ms, maxAttempts=3 (1 original + 2 reintentos)
        return new Retryer.Default(1000, 2000, 3);
    }
}
