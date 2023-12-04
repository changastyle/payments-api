

INSERT INTO `configuraciones` (`id`, `nombre`, `nombreProyecto`, `protocolo`, `ip`, `puerto`, `carpetaWeb`, `enabled`, `linux`, `nextRefresh`, `subCarpetaImagenes`) VALUES
(1, 'LOCAL', 'delegator', 'http://', 'localhost', NULL, NULL, b'1', b'0', 0, NULL),
(2, 'PROD', 'delegator', 'http://', 'delegator.viewdevs.com.ar', NULL, NULL, b'0', b'0', 0, NULL);


INSERT INTO `instalaciones` (`id`, `nombre`, `fkLogo`, `urlWEB`, `activo`) VALUES
(1, 'View Devs', 1, 'delegator.viewdevs.com.ar', b'1');


INSERT INTO `operadores` (`id`, `nombre`, `apellido`, `fkInstalacion`, `pass`, `email`, `fkFotoPerfil`, `dni`, `fechaCreacion`, `fechaNacimiento`, `sexo`, `activo`) VALUES
(1, 'Nicolas', 'Grossi', 1, 'descargar', 'nico.grossi4@gmail.com', 1, '37557878', '2023-10-26', '2023-10-26', '1', b'1'),
(2, 'Tomas', 'Grossi', 1, 'lupita', 'tomigrossi1998@gmail.com', 1, '37557878', '2023-10-26', '2023-10-26', '1', b'1');
