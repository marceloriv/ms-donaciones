-- Datos de prueba para desarrollo local
INSERT IGNORE INTO organizacion
    (nombre, rut, direccion, telefono, email,
     banco, tipo_cuenta, numero_cuenta, titular_cuenta,
     rut_titular, metodo_pago_preferido, estado, fecha_registro)
VALUES
    ('Un Techo Para Chile', '76543210-1',
     'Av. Holanda 1015, Providencia', '+56912345678',
     'contacto@techo.org', 'Banco Estado', 'Corriente',
     '123456789', 'Un Techo Para Chile', '76543210-1',
     'TRANSFERENCIA', 'ACTIVA', NOW()),
    ('Fundación Las Rosas', '65432109-2',
     'Camino El Observatorio 4903, Las Condes', '+56987654321',
     'info@lasrosas.cl', 'Banco de Chile', 'Corriente',
     '987654321', 'Fundación Las Rosas', '65432109-2',
     'TRANSFERENCIA', 'ACTIVA', NOW());

INSERT IGNORE INTO causa_social
    (id_organizacion, nombre, descripcion, objetivo_monto,
     fecha_inicio, estado)
VALUES
    (1, 'Viviendas 2026',
     'Construcción de viviendas en zona rural', 5000000.00,
     '2026-01-01', 'ACTIVA'),
    (2, 'Cuidado adulto mayor',
     'Atención y acompañamiento a adultos mayores', 3000000.00,
     '2026-01-01', 'ACTIVA');