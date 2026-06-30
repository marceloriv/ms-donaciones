-- Schema para tests con H2 (sin CREATE DATABASE, sin CHARACTER SET)
CREATE TABLE IF NOT EXISTS organizacion (
                                            id_organizacion       BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            nombre                VARCHAR(150)  NOT NULL,
    rut                   VARCHAR(12)   UNIQUE,
    direccion             VARCHAR(200),
    telefono              VARCHAR(20),
    email                 VARCHAR(100)  NOT NULL,
    banco                 VARCHAR(50),
    tipo_cuenta           VARCHAR(30),
    numero_cuenta         VARCHAR(30),
    titular_cuenta        VARCHAR(150),
    rut_titular           VARCHAR(12),
    metodo_pago_preferido VARCHAR(20),
    estado                VARCHAR(20)   NOT NULL DEFAULT 'PENDIENTE',
    fecha_registro        TIMESTAMP     NOT NULL,
    documento_convenio    VARCHAR(255)
    );

CREATE TABLE IF NOT EXISTS representante (
                                             id_representante  BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             id_organizacion   BIGINT        NOT NULL,
                                             nombre            VARCHAR(150)  NOT NULL,
    rut               VARCHAR(12)   NOT NULL,
    cargo             VARCHAR(100),
    email             VARCHAR(100)  NOT NULL,
    telefono          VARCHAR(20),
    tipo              VARCHAR(20)   NOT NULL,
    estado            VARCHAR(20)   NOT NULL DEFAULT 'ACTIVO',
    CONSTRAINT fk_rep_org
    FOREIGN KEY (id_organizacion) REFERENCES organizacion(id_organizacion)
    );

CREATE TABLE IF NOT EXISTS causa_social (
                                            id_causa                 BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            id_organizacion          BIGINT,
                                            nombre                   VARCHAR(150)  NOT NULL,
    descripcion              VARCHAR(1000),
    objetivo_monto           DECIMAL(15,2),
    fecha_inicio             DATE          NOT NULL,
    fecha_fin                DATE,
    estado                   VARCHAR(20)   NOT NULL DEFAULT 'PENDIENTE',
    documento_enviado        BOOLEAN       NOT NULL DEFAULT FALSE,
    fecha_documento_enviado  TIMESTAMP,
    CONSTRAINT fk_causa_org
    FOREIGN KEY (id_organizacion) REFERENCES organizacion(id_organizacion)
    );

CREATE TABLE IF NOT EXISTS donacion (
                                        id_donacion       BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        id_compra         BIGINT         NOT NULL,
                                        id_pago           BIGINT         NOT NULL,
                                        id_usuario        BIGINT         NOT NULL,
                                        id_evento         BIGINT         NOT NULL,
                                        id_causa          BIGINT         NOT NULL,
                                        id_organizacion   BIGINT         NOT NULL,
                                        monto             DECIMAL(15,2)  NOT NULL,
    fecha             TIMESTAMP      NOT NULL,
    estado            VARCHAR(20)    NOT NULL DEFAULT 'APROBADA',
    CONSTRAINT fk_don_causa
    FOREIGN KEY (id_causa) REFERENCES causa_social(id_causa),
    CONSTRAINT fk_don_org
    FOREIGN KEY (id_organizacion) REFERENCES organizacion(id_organizacion)
    );