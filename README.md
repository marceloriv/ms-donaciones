# MS-Donaciones — Ticketti

Microservicio responsable del registro y consulta de donaciones.
Cada compra destina un 10% fijo del monto total a una organización
sin fines de lucro elegida por el comprador.

## 🏗️ Stack Tecnológico
- **Lenguaje:** Java 17 + Spring Boot 3.4.5
- **Base de Datos:** MySQL 8.4 (BD: `donaciones_db`)
- **Mensajería:** RabbitMQ 3 (consumo de eventos)
- **Service Discovery:** Eureka Client
- **Config Remoto:** Spring Cloud Config Server
- **Gestión:** Maven
- **Containerización:** Docker & Docker Compose

## 🎯 Patrones Aplicados
- **Repository Pattern** - Acceso a datos vía JpaRepository
- **Event-Driven Architecture** - Consumo de eventos vía RabbitMQ
- **Microservices** - Arquitectura orientada a servicios

---

## 🚀 Instalación y Ejecución

### Opción A: Docker Compose (Recomendado)

```bash
# Levantar todos los servicios
docker-compose up -d

# Ver logs
docker-compose logs -f app

# Detener servicios
docker-compose down
```

**Servicios que se levantan:**
- MySQL 8.4 → `localhost:3306` (usuario: `root`, password: `root`)
- RabbitMQ 3 → `localhost:5672` (usuario: `ticketti`, password: `ticketti123`)
- MS-Donaciones → `localhost:8084`
- RabbitMQ Management UI → `http://localhost:15672`

### Opción B: Ejecución Local

```bash
# Requisitos: MySQL y RabbitMQ corriendo en localhost

mvn spring-boot:run
```

---

## 📋 Configuración

### Variables de Entorno

El proyecto utiliza **Spring Cloud Config Server** como fuente central de configuración. 
Las variables soportadas son:

| Variable | Descripción | Default Local |
|---|---|---|
| `SPRING_DATASOURCE_URL` | URL de conexión MySQL | `jdbc:mysql://localhost:3306/donaciones_db?...` |
| `SPRING_DATASOURCE_USERNAME` | Usuario MySQL | `root` |
| `SPRING_DATASOURCE_PASSWORD` | Password MySQL | (vacío) |
| `RABBITMQ_HOST` | Host de RabbitMQ | `localhost` |
| `RABBITMQ_PORT` | Puerto de RabbitMQ | `5672` |
| `RABBITMQ_USER` | Usuario de RabbitMQ | `ticketti` |
| `RABBITMQ_PASS` | Password de RabbitMQ | `ticketti123` |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | URL de Eureka | `http://localhost:8761/eureka/` |

### Archivos de Configuración

- `src/main/resources/application.properties` - Configuración por defecto
- `.env` - Variables para Docker Compose
- `docker-compose.yml` - Orquestación de contenedores

---

## 🔌 Integración con RabbitMQ

### Contrato de Eventos

**Consumidor:** Escucha eventos de pago aprobado desde MSCarrito

```yaml
Exchange: ticketti.exchange (Topic)
Queue: pago.aprobado
Routing Key: pago.aprobado
```

**Estructura del evento (CompraConfirmadaEvent):**
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

### Flujo de Procesamiento

1. MSCarrito publica evento en `pago.aprobado`
2. MS-Donaciones consume el evento
3. Valida que existe la causa social
4. Registra la donación en BD
5. Asocia la donación a organización

---

## ✔️ Verificación

### Health Check
```bash
curl http://localhost:8084/actuator/health
```

### Acceso a Swagger UI
```
http://localhost:8084/swagger-ui.html
```

### Verificar RabbitMQ
```bash
# Management UI
http://localhost:15672
# Usuario: ticketti | Password: ticketti123
```

### Verificar Base de Datos
```bash
docker exec -it ms-donaciones-mysql mysql -u root -proot donaciones_db
USE donaciones_db;
SHOW TABLES;
```

---

## 📁 Estructura del Proyecto

```
ms-donaciones/
├── src/main/java/com/ticketti/ms_donaciones/
│   ├── config/              # Configuración (RabbitMQ, etc)
│   ├── controller/          # Endpoints REST
│   ├── dto/                 # Objetos de transferencia
│   ├── enums/               # Enumeraciones
│   ├── exception/           # Manejo de excepciones
│   ├── messaging/           # Consumidor de eventos
│   ├── model/               # Entidades JPA
│   ├── repository/          # Acceso a datos
│   └── service/             # Lógica de negocio
├── src/main/resources/
│   ├── application.properties
│   ├── schema.sql           # Definición de BD
│   └── data.sql             # Datos iniciales
├── docker-compose.yml       # Orquestación de contenedores
├── Dockerfile               # Imagen Docker
└── pom.xml                  # Dependencias Maven
```

---

## 🔧 Comandos Útiles

```bash
# Construir proyecto
mvn clean install

# Ejecutar tests
mvn test

# Construir imagen Docker
docker build -t ms-donaciones:latest .

# Ver logs en vivo
docker-compose logs -f app

# Acceder a MySQL
docker-compose exec mysql mysql -u root -proot donaciones_db

# Resetear BD (elimina volúmenes)
docker-compose down -v
```

---

## 📞 Endpoints Principales

- `GET /api/organizaciones` - Listar organizaciones
- `GET /api/organizaciones/{id}` - Obtener organización
- `GET /api/causas-sociales` - Listar causas sociales
- `GET /api/donaciones` - Listar donaciones
- `GET /api/donaciones/{id}` - Obtener donación

Más detalles en Swagger: `http://localhost:8084/swagger-ui.html`

---

## 🐛 Solución de Problemas

### Conexión rechazada a MySQL
```bash
# Verificar que el contenedor está corriendo
docker-compose ps

# Ver logs de MySQL
docker-compose logs mysql
```

### RabbitMQ no conecta
```bash
# Verificar healthcheck
docker-compose logs rabbitmq

# Verificar credenciales
docker exec ms-donaciones-rabbitmq rabbitmqctl list_users
```

### Errores de lazy loading
- La app usa `@Transactional` en el consumidor para evitar `LazyInitializationException`
- Las relaciones ManyToOne tienen `fetch = LAZY`

---

## 📝 Notas Importantes

- **BD:** El nombre cambió de `ticketti_donaciones` a `donaciones_db` (alineado con Config Server)
- **RabbitMQ:** Usa credenciales `ticketti:ticketti123` (no guest:guest)
- **Config Server:** Cuando esté activo, sobrescribe los valores de `application.properties`
- **Eureka:** Configuración lista pero opcional en desarrollo local

---

## 👥 Equipo
Equipo 7 — DSY1106 — Duoc UC

**Última actualización:** 9 de mayo de 2026