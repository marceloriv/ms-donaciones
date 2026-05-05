# MS-Donaciones — Ticketti

Microservicio responsable del registro y consulta de donaciones.
Cada compra destina un 10% fijo del monto total a una organizacion
sin fines de lucro elegida por el comprador.

## Stack
- Java 17 + Spring Boot 3
- MySQL 8.4 (BD `ticketti_donaciones`)
- Eureka Client + Spring Cloud Config
- Maven

## Patrones aplicados
- Repository Pattern (acceso a datos via JpaRepository)

## Como ejecutar
1. Levantar Eureka, Config Server y MySQL
2. Crear la BD `ticketti_donaciones`
3. `mvn spring-boot:run`

## Equipo 7 — DSY1106 — Duoc UC