# MS-Donaciones — Ticketti

Microservicio responsable del registro y consulta de donaciones, causas sociales y organizaciones.
Cada compra destina un porcentaje del monto total a una organización sin fines de lucro elegida por el comprador.

## Stack Tecnológico

- **Lenguaje:** Java 17 + Spring Boot 3.4.5
- **Base de Datos:** MySQL 8.4 (`donaciones_db`)
- **Mensajería:** RabbitMQ (consumo de eventos de pago)
- **Service Discovery:** Eureka Client
- **Config Remoto:** Spring Cloud Config Server
- **Documentación:** SpringDoc OpenAPI (Swagger UI)
- **Containerización:** Docker & Docker Compose

## Patrones Aplicados

- **Repository Pattern** — acceso a datos vía `JpaRepository`
- **Event-Driven Architecture** — consumo de eventos `pago.aprobado` vía RabbitMQ
- **DTO Pattern** — separación entre modelo JPA y contratos de API

---

## Endpoints REST

### Donaciones

| Método | Ruta | Acceso | Descripción |
|--------|------|--------|-------------|
| `GET` | `/api/v1/donaciones/me` | Autenticado | Donaciones del usuario autenticado (userId extraído del JWT) |
| `GET` | `/api/v1/donaciones/{id}` | ADMINPLATAFORMA / ORGANIZADOR | Detalle de una donación |
| `GET` | `/api/v1/donaciones/**` | ADMINPLATAFORMA / ORGANIZADOR | Historial general / reportes |

### Causas Sociales

| Método | Ruta | Acceso | Descripción |
|--------|------|--------|-------------|
| `GET` | `/api/v1/causas/activas` | Público | Causas con estado ACTIVA |
| `GET` | `/api/v1/causas/organizacion/{id}` | ADMINPLATAFORMA / ORGANIZADOR | Causas de una organización |
| `GET` | `/api/v1/causas/{id}` | ADMINPLATAFORMA / ORGANIZADOR | Detalle de una causa |
| `POST` | `/api/v1/causas` | ADMINPLATAFORMA / ORGANIZADOR | Crear causa social |
| `DELETE` | `/api/v1/causas/{id}` | ADMINPLATAFORMA | Desactivar causa |

### Organizaciones

| Método | Ruta | Acceso | Descripción |
|--------|------|--------|-------------|
| `GET` | `/api/v1/organizaciones` | Público | Listar organizaciones |
| `GET` | `/api/v1/organizaciones/{id}` | ADMINPLATAFORMA / ORGANIZADOR | Detalle |
| `POST` | `/api/v1/organizaciones` | ADMINPLATAFORMA / ORGANIZADOR | Crear organización |
| `POST` | `/api/v1/organizaciones/{id}/documento` | ADMINPLATAFORMA / ORGANIZADOR | Subir convenio |
| `PUT` | `/api/v1/organizaciones/{id}/activar` | ADMINPLATAFORMA | Activar organización pendiente |
| `PUT` | `/api/v1/organizaciones/{id}` | ADMINPLATAFORMA | Editar organización |
| `DELETE` | `/api/v1/organizaciones/{id}` | ADMINPLATAFORMA | Desactivar organización |

---

## Seguridad

El microservicio valida JWT propio. Las reglas en `SecurityConfig`:

- `/v3/api-docs/**`, `/api-docs/**`, `/swagger-ui/**` → público (Swagger)
- `GET /api/v1/donaciones/**` → público (el BFF aplica restricciones por rol)
- `GET /api/v1/causas/**` → público
- `GET /api/v1/organizaciones/**` → público
- `POST`, `PUT`, `DELETE` → requieren JWT

### Extracción de userId desde JWT

El endpoint `GET /donaciones/me` extrae el `usuarioId` directamente del token JWT sin recibirlo como parámetro. `JwtService.extractUserId(token)` lee el claim `usuarioId` incluido en el token generado por ms-usuarios.

---

## Integración con RabbitMQ

**Consumidor:** escucha eventos de pago aprobado desde ms-carrito.

```yaml
Exchange: ticketti.exchange (Topic)
Queue:    pago.aprobado
Routing Key: pago.aprobado
```

**Estructura del evento (`CompraConfirmadaEvent`):**
```json
{
  "idCarrito": 100,
  "pagoId": 200,
  "usuarioId": 5,
  "causaSocialId": 1,
  "total": 50000.00,
  "montoDonacion": 5000.00,
  "eventoId": 10
}
```

**Flujo:**
1. ms-carrito publica evento en `pago.aprobado`
2. `DonacionConsumer` recibe el evento
3. Valida que existe la causa social
4. Registra la donación en BD con estado `APROBADA`

---

## Ejecución

### Docker Compose (Recomendado)

```bash
# Levantar todos los servicios (requiere .env en la raíz del microservicio)
docker compose up -d

# Ver logs de la app
docker compose logs -f app

# Detener
docker compose down
```

**Servicios que se levantan:**
- MySQL 8.4 → `localhost:3308` (puerto externo, interno 3306)
- MS-Donaciones → `localhost:8084`

### Local

```bash
mvn spring-boot:run
```

Requiere MySQL corriendo localmente y las variables de entorno definidas.

---

## Configuración

El proyecto obtiene su configuración desde el Config Server (`config-server/src/main/resources/config/ms-donaciones.yml`). El `application.yml` local solo contiene el import del config server.

### Variables de Entorno (`.env`)

| Variable | Descripción |
|----------|-------------|
| `MYSQL_ROOT_PASSWORD` | Password root de MySQL |
| `MYSQL_DATABASE` | Nombre de la BD (`donaciones_db`) |
| `SPRING_DATASOURCE_USERNAME` | Usuario de la BD |
| `SPRING_DATASOURCE_PASSWORD` | Password de la BD |
| `RABBITMQ_USER` | Usuario de RabbitMQ |
| `RABBITMQ_PASS` | Password de RabbitMQ |
| `JWT_SECRET` | Secret compartido para validar JWT |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | URL de Eureka |

---

## Estructura del Proyecto

```
ms-donaciones/
├── src/main/java/com/ticketti/ms_donaciones/
│   ├── config/
│   │   ├── DataSeeder.java        # Datos iniciales
│   │   ├── OpenApiConfig.java     # Configuración Swagger
│   │   └── RabbitMQConfig.java    # Configuración colas
│   ├── controller/
│   │   ├── CausaSocialController.java
│   │   ├── DonacionController.java     # incluye GET /donaciones/me
│   │   └── OrganizacionController.java
│   ├── dto/                       # Request/Response DTOs
│   ├── enums/                     # EstadoDonacion, EstadoCausaSocial, etc.
│   ├── exception/                 # GlobalExceptionHandler
│   ├── messaging/
│   │   └── DonacionConsumer.java  # Listener pago.aprobado
│   ├── model/                     # Entidades JPA
│   ├── repository/                # JpaRepository interfaces
│   ├── security/
│   │   ├── JwtAuthenticationFilter.java
│   │   ├── JwtService.java        # incluye extractUserId()
│   │   └── SecurityConfig.java
│   └── service/
│       ├── CausaSocialService.java
│       ├── DonacionService.java   # incluye listarPorUsuario()
│       └── OrganizacionService.java
├── src/main/resources/
│   ├── application.yml            # Solo import del config server
│   ├── schema.sql
│   └── data.sql
├── docker-compose.yml
├── Dockerfile
└── pom.xml
```

---

## Verificación

```bash
# Health check
curl http://localhost:8084/actuator/health

# Swagger UI
http://localhost:8084/swagger-ui.html
# (también accesible en /api-docs para la spec JSON)

# MySQL directo
docker exec -it ms-donaciones-mysql mysql -u root -p donaciones_db
```

---

## Comandos Útiles

```bash
mvn clean install          # Compilar y empaquetar
mvn test                   # Ejecutar tests
docker compose up -d --build app   # Rebuild y levantar solo la app
docker compose logs -f app         # Logs en tiempo real
docker compose down -v             # Detener y eliminar volúmenes
```

---

## Equipo

Equipo 7 — DSY1106 — Duoc UC

**Última actualización:** 27 de junio de 2026
