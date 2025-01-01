

INSERT IGNORE INTO `configuraciones` (`id`, `nombre`, `nombreProyecto`, `protocolo`, `ip`, `puerto`, `carpetaWeb`, `enabled`, `linux`, `nextRefresh`, `subCarpetaImagenes`) VALUES
(1, 'LOCAL', 'payments', 'http://', 'localhost', NULL, NULL, b'1', b'0', 0, NULL),
(2, 'PROD', 'payments', 'http://', 'payments.viewdevs.com.ar', NULL, NULL, b'0', b'0', 0, NULL);

INSERT IGNORE INTO `documentos` (`id`, `urlOriginal`, `urlProv`, `activo`) VALUES
(1, 'default.jpg', 'default.jpg', b'1');

INSERT IGNORE INTO `instalaciones` (`id`, `nombre`, `urlWEB`, `fkLogo`, `cbu`, `activo`) VALUES
(1, 'View Devs', 'payments.viewdevs.com.ar', 25, '0000003100015933443734', b'1'),
(2, 'Biomed', 'pagos.biomedsur.com.ar', 1, '111111111111111', b'1');

INSERT IGNORE INTO `empresas` (`id`, `nombre`, `fkInstalacion`, `activo`, `fkLogo`) VALUES
(1, 'CEM', 1, b'1', 26),
(2, 'MELIPAL', 1, b'1', 1),
(3, 'IMO', 1, b'1', 1),
(4, 'HPR', 2, b'1', 1),
(5, 'SAN CARLOS', 2, b'1', 1),
(6, 'SEAS',1, b'1',1);



INSERT IGNORE INTO `estados_posibles` (`id`, `nombre`, `color`, `nombreCamino`, `activo`, `fkEstadoNegativo`, `fkEstadoProximoPositivo`, `icono`) VALUES
(1, 'CREACION', '#3498db', 'P1', b'1', 5, 2, 'created'),
(2, 'INFORMADO', '#f1c40f', 'P2', b'1', 5, 3, 'envelope-open'),
(3, 'ABONADO', '#2ecc71', 'P3', b'1', 5, 4, 'money-transfer'),
(4, 'PAGO CONFIRMADO', '#16a085', 'P4', b'1', 5, 4, 'double-check'),
(5, 'CANCELADO', '#e74c3c', 'P99', b'1', 5, 5, 'ban');

INSERT IGNORE INTO `operadores` (`id`, `nombre`, `apellido`, `fkInstalacion`, `pass`, `email`, `fkFotoPerfil`,`admin`, `dni`, `fechaCreacion`, `fechaNacimiento`, `sexo`, `activo`)
VALUES
(1, 'Nicolas', 'Grossi', 1, 'descargar', 'nico.grossi4@gmail.com', 1, 1, '37557878', '2023-10-26', '2023-10-26', '1', b'1'),
(2, 'Lupita', 'Grossi', 1, 'lupita', 'lupita@gmail.com', 1,0, '37557878', '2023-10-26', '2023-10-26', '1', b'1');


INSERT IGNORE INTO `productos` (`id`, `nombre`, `fkInstalacion`, `precio`, `fkFoto`, `activo`) VALUES
(1, 'ClinicApp', 1, 25, 309, b'1'),
(2, 'Hostinger', 1, 5000, 310, b'1');
(3, 'emkt', 1, 10, 1, b'1'),
(4, 'Cuaderno', 1, 200, 1, b'1'),
(5, 'TEST1', 1, 0, 1, b'1'),
