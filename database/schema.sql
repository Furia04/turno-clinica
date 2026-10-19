-- ==============================================================
-- Script de Creación de Base de Datos y Tablas - Turno Clínica
-- Lenguajes de Programación II - Universidad Católica de Cuyo (UCCuyo)
-- ==============================================================

CREATE DATABASE IF NOT EXISTS turnoclinica
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE turnoclinica;

-- Tabla: pacientes
CREATE TABLE IF NOT EXISTS pacientes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    dni VARCHAR(15) NOT NULL UNIQUE,
    telefono VARCHAR(30),
    email VARCHAR(120) NOT NULL,
    obra_social VARCHAR(100)
) ENGINE=InnoDB;

-- Tabla: profesionales
CREATE TABLE IF NOT EXISTS profesionales (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    matricula VARCHAR(30) NOT NULL UNIQUE,
    especialidad VARCHAR(100) NOT NULL
) ENGINE=InnoDB;

-- Tabla: turnos
CREATE TABLE IF NOT EXISTS turnos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha_hora DATETIME NOT NULL,
    motivo VARCHAR(255),
    estado VARCHAR(20) NOT NULL,
    id_paciente BIGINT NOT NULL,
    id_profesional BIGINT NOT NULL,
    CONSTRAINT fk_turnos_paciente FOREIGN KEY (id_paciente) REFERENCES pacientes(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_turnos_profesional FOREIGN KEY (id_profesional) REFERENCES profesionales(id) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;
