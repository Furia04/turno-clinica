-- ==============================================================
-- Datos de prueba para el sistema Turno Clínica
-- Lenguajes de Programación II - UCC
-- ==============================================================

USE turnoclinica;

-- Limpieza previa (opcional si se reinicia la prueba)
DELETE FROM turnos;
DELETE FROM profesionales;
DELETE FROM pacientes;

-- Inserción de Pacientes iniciales
INSERT INTO pacientes (id, nombre, apellido, dni, telefono, email, obra_social) VALUES
(1, 'Juan', 'Perez', '35123456', '+54 351 4567890', 'juan.perez@example.com', 'OSDE'),
(2, 'Maria', 'Gomez', '28987654', '+54 351 9876543', 'maria.gomez@example.com', 'Swiss Medical'),
(3, 'Carlos', 'Lopez', '40555666', '+54 351 5556667', 'carlos.lopez@example.com', 'Apross');

-- Inserción de Profesionales iniciales
INSERT INTO profesionales (id, nombre, apellido, matricula, especialidad) VALUES
(1, 'Ana', 'Martinez', 'MP-10452', 'Cardiología'),
(2, 'Lucas', 'Rossi', 'MP-11890', 'Traumatología'),
(3, 'Sofia', 'Fernandez', 'MP-09743', 'Pediatría');

-- Inserción de Turnos iniciales (fechas a futuro)
INSERT INTO turnos (id, fecha_hora, motivo, estado, id_paciente, id_profesional) VALUES
(1, '2026-11-15 09:00:00', 'Control cardiológico anual', 'CONFIRMADO', 1, 1),
(2, '2026-11-15 10:00:00', 'Dolor en la rodilla izquierda tras actividad física', 'PENDIENTE', 2, 2),
(3, '2026-11-20 11:30:00', 'Chequeo pediátrico escolar', 'PENDIENTE', 3, 3),
(4, '2026-12-01 16:00:00', 'Seguimiento de presión arterial', 'CONFIRMADO', 1, 1);
