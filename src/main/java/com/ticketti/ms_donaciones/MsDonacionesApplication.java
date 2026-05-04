package com.ticketti.ms_donaciones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Microservicio MS-Donaciones.
 * Registra y consulta las donaciones del 10% por compra
 * destinadas a las organizaciones beneficiarias.
 */
@SpringBootApplication
@EnableDiscoveryClient // se registra en Eureka
public class MsDonacionesApplication {

	public static void main(String[] args) {

		SpringApplication.run(MsDonacionesApplication.class, args);
	}

}
