-- Datos de prueba para desarrollo local

INSERT IGNORE INTO organizacion
    (nombre, rut, direccion, telefono, email,
     banco, tipo_cuenta, numero_cuenta, titular_cuenta,
     rut_titular, metodo_pago_preferido, estado, fecha_registro)
VALUES
    ('Fundación Manos Unidas', '11111111-1',
     'Av. Principal 123', '+56911111111',
     'contacto@manosunidas.cl', NULL, NULL,
     NULL, NULL, NULL,
     NULL, 'ACTIVA', NOW()),

    ('Comedor Solidario Norte', '22222222-2',
     'Calle Esperanza 456', '+56922222222',
     'hola@comedorsolidario.cl', NULL, NULL,
     NULL, NULL, NULL,
     NULL, 'PENDIENTE', NOW()),

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
    (id_organizacion, nombre, descripcion, objetivo_monto, fecha_inicio, estado)
VALUES
    (1, 'Becas escolares',
     'Apoyo escolar para niños y niñas en situación vulnerable.',
     1000000.00, CURDATE(), 'ACTIVA'),

    (1, 'Ayuda alimentaria',
     'Entrega de canastas de alimentos a familias vulnerables.',
     750000.00, CURDATE(), 'ACTIVA'),

    (3, 'Viviendas 2026',
     'Construcción de viviendas en zona rural.',
     5000000.00, '2026-01-01', 'ACTIVA'),

    (4, 'Cuidado adulto mayor',
     'Atención y acompañamiento a adultos mayores.',
     3000000.00, '2026-01-01', 'ACTIVA');