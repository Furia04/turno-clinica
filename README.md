<div align="center">

# 🏥 Turno Clínica - API Backend REST
### *Sistema Integral de Gestión y Reserva de Turnos Médicos*
**Universidad Católica de Cuyo (UCCuyo) — Facultad de Ciencias Económicas y Empresariales / Ingeniería**  
**Cátedra: Lenguajes de Programación II | Primer Entregable del Trabajo Final**

[![Java](https://img.shields.io/badge/Java-21%20LTS-orange.svg?logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-brightgreen.svg?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Data JPA](https://img.shields.io/badge/Spring%20Data-JPA-blue.svg?logo=hibernate&logoColor=white)](https://spring.io/projects/spring-data-jpa)
[![MySQL](https://img.shields.io/badge/Database-MySQL%208.0+-4479A1.svg?logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Build Status](https://img.shields.io/badge/Build-Passing-success.svg?logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![API](https://img.shields.io/badge/API-REST%20CRUD-blueviolet.svg)](#-funcionalidades-y-endpoints-disponibles)
[![Postman](https://img.shields.io/badge/Postman-Collection%20Ready-FF6C37.svg?logo=postman&logoColor=white)](#-colección-de-postman-y-pruebas-reproducibles)

---

### 👨‍💻 Integrantes del Proyecto
| Apellido y Nombre | Rol / Carrera | Institución |
|---|---|---|
| **Ortiz Ignacio** | Estudiante LPII | Universidad Católica de Cuyo |
| **Poblete Luis** | Estudiante LPII | Universidad Católica de Cuyo |
| **Manrique Leandro** | Estudiante LPII | Universidad Católica de Cuyo |

---

</div>

## 📌 Tabla de Contenidos
1. [Descripción y Problema que Resuelve](#-1-descripción-del-proyecto-y-problema)
2. [Perfiles de Usuario](#-2-perfiles-de-usuario)
3. [Modelo de Datos y Relaciones](#-3-modelo-de-datos-y-relaciones)
4. [Arquitectura y Organización](#-4-arquitectura-y-organización-del-código)
5. [Funcionalidades y Endpoints](#-5-funcionalidades-y-endpoints-disponibles)
6. [Validaciones y Manejo de Errores](#-6-validaciones-y-manejo-de-errores)
7. [Instrucciones de Instalación y Ejecución](#-7-instrucciones-de-instalación-y-ejecución)
8. [Colección de Postman y Casos de Prueba](#-8-colección-de-postman-y-pruebas-reproducibles)
9. [Próximos Pasos (Segundo Entregable)](#-9-próximos-pasos-segundo-entregable)

---

## 🎯 1. Descripción del Proyecto y Problema

### El Desafío
En la administración tradicional de centros médicos y policlínicos ambulatorios, el registro manual de turnos por libreta o por vía telefónica ocasiona:
* ⚠️ **Superposición horaria** de pacientes citados con el mismo profesional.
* ⚠️ **Pérdida de turnos** y ausentismo no coordinado.
* ⚠️ **Falta de trazabilidad** respecto a qué paciente fue atendido por qué profesional médico.
* ⚠️ **Inconsistencia referencial** en cancelaciones o modificaciones de datos.

### La Solución: Turno Clínica
**Turno Clínica** proporciona una solución robusta a nivel de backend: una API RESTful desarrollada con **Java 21**, **Spring Boot** y persistencia relacional en **MySQL** mediante **Spring Data JPA**. Garantiza integridad referencial, reglas de validación en tiempo real para disponibilidad horaria, y desacoplamiento limpio mediante DTOs y Mappers.

---

## 👥 2. Perfiles de Usuario

* 🧑‍⚕️ **Profesionales Médicos:** Consultan su agenda diaria de atención y turnos asignados por paciente y horario.
* 🩺 **Pacientes:** Solicitan, consultan, reprograman o cancelan sus reservas de turnos según especialidad y profesional.
* 🖥️ **Personal de Recepción / Gestión:** Administran altas de profesionales y pacientes, supervisan la disponibilidad de la clínica y resuelven turnos en mesa de entrada.

---

## 🏛️ 3. Modelo de Datos y Relaciones

El backend implementa tres entidades principales con claves primarias autoincrementales (`GenerationType.IDENTITY`) persistidas en MySQL:

### Entidades

* **`Paciente` (`pacientes`):** Datos filiatorios y de contacto (`id`, `nombre`, `apellido`, `dni` [UNIQUE], `telefono`, `email`, `obra_social`).
* **`Profesional` (`profesionales`):** Datos del cuerpo médico (`id`, `nombre`, `apellido`, `matricula` [UNIQUE], `especialidad`).
* **`Turno` (`turnos`):** Agenda de atención (`id`, `fecha_hora`, `motivo`, `estado` [Enum: `PENDIENTE`, `CONFIRMADO`, `CANCELADO`, `ATENDIDO`]).

### Diagrama de Relaciones y Cardinalidad

```
  ┌───────────────────────────────────┐               ┌──────────────────────────────────────┐
  │             PACIENTE              │               │             PROFESIONAL              │
  ├───────────────────────────────────┤               ├──────────────────────────────────────┤
  │ PK  id: BIGINT                    │               │ PK  id: BIGINT                       │
  │     nombre: VARCHAR(100)          │               │     nombre: VARCHAR(100)             │
  │     apellido: VARCHAR(100)        │               │     apellido: VARCHAR(100)           │
  │ UQ  dni: VARCHAR(15)              │               │ UQ  matricula: VARCHAR(30)           │
  │     telefono: VARCHAR(30)         │               │     especialidad: VARCHAR(100)       │
  │     email: VARCHAR(120)           │               │                                      │
  │     obra_social: VARCHAR(100)     │               │                                      │
  └─────────────────┬─────────────────┘               └──────────────────┬───────────────────┘
                    │ 1                                                  │ 1
                    │                                                    │
                    │ N                                                  │ N
                  ┌─┴────────────────────────────────────────────────────┴─┐
                  │                         TURNO                          │
                  ├────────────────────────────────────────────────────────┤
                  │ PK  id: BIGINT                                         │
                  │     fecha_hora: DATETIME                               │
                  │     motivo: VARCHAR(255)                               │
                  │     estado: VARCHAR(20) [PENDIENTE, CONFIRMADO...]     │
                  │ FK  id_paciente: BIGINT (NOT NULL)                     │
                  │ FK  id_profesional: BIGINT (NOT NULL)                  │
                  └────────────────────────────────────────────────────────┘
```

* **Relación Paciente 1 a N Turno:** Un paciente puede registrar varios turnos. Un turno pertenece exclusivamente a un paciente. Mapeado con `@ManyToOne(fetch = FetchType.LAZY, optional = false)` y `@JoinColumn(name = "id_paciente")`.
* **Relación Profesional 1 a N Turno:** Un profesional gestiona múltiples turnos. Un turno se atiende con un único profesional. Mapeado con `@ManyToOne(fetch = FetchType.LAZY, optional = false)` y `@JoinColumn(name = "id_profesional")`.
* **Relación Paciente N a N Profesional:** Resuelta a través de la entidad intermedia `Turno`.

---

## 🧱 4. Arquitectura y Organización del Código

El proyecto cumple de forma estricta el estándar de capas recomendado por la cátedra:

```
src/main/java/com/ucc/turnoclinica/
│
├── TurnoClinicaApplication.java        # Punto de entrada (@SpringBootApplication)
│
├── controller/                         # Capa Web / API REST (@RestController)
│   ├── PacienteController.java         # Endpoints CRUD de Pacientes
│   ├── ProfesionalController.java      # Endpoints CRUD de Profesionales
│   └── TurnoController.java            # Endpoints de Turnos y consultas relacionales
│
├── service/                            # Capa de Lógica de Negocio (@Service, @Transactional)
│   ├── PacienteService.java            # Lógica, unicidad de DNI y reglas de borrado
│   ├── ProfesionalService.java         # Lógica, matrícula única y reglas de borrado
│   └── TurnoService.java               # Control de solapamiento, disponibilidad y validación
│
├── repository/                         # Capa de Acceso a Datos (Spring Data JPA)
│   ├── PacienteRepository.java
│   ├── ProfesionalRepository.java
│   └── TurnoRepository.java            # Consultas findByPaciente_Id, findByProfesional_Id
│
├── entity/                             # Capa de Dominio / Entidades JPA
│   ├── Paciente.java
│   ├── Profesional.java
│   ├── Turno.java
│   └── EstadoTurno.java
│
├── dto/                                # Data Transfer Objects (Java 21 Records)
│   ├── PacienteRequestDTO.java         # Validación de entrada (@Valid)
│   ├── PacienteResponseDTO.java        # Retorno con ID generado
│   ├── ProfesionalRequestDTO.java
│   ├── ProfesionalResponseDTO.java
│   ├── TurnoRequestDTO.java
│   ├── TurnoResponseDTO.java
│   └── ErrorDTO.java                   # Formato consistente de respuestas de error
│
├── mapper/                             # Conversión Entidad <-> DTO (@Component)
│   ├── PacienteMapper.java
│   ├── ProfesionalMapper.java
│   └── TurnoMapper.java
│
└── exception/                          # Manejo Centralizado de Excepciones
    ├── PacienteNotFoundException.java  # Retorna 404
    ├── ProfesionalNotFoundException.java # Retorna 404
    ├── TurnoNotFoundException.java     # Retorna 404
    ├── ReglaDeNegocioException.java    # Retorna 400
    └── GlobalExceptionHandler.java     # @RestControllerAdvice
```

> **Pilares de Buenas Prácticas:**
> 1. **Inyección de dependencias por constructor:** Facilita la inmutabilidad y la testabilidad.
> 2. **Desacoplamiento total Controller-Repository:** El controlador nunca llama directamente al repositorio.
> 3. **Uso de Java Records para DTOs:** Inmutabilidad garantizada, código conciso y seguro.

---

## 🚦 5. Funcionalidades y Endpoints Disponibles

### 👤 Pacientes (`/api/pacientes`)
| Método | Endpoint | Descripción | Código Éxito | Códigos Error |
|---|---|---|:---:|:---:|
| `GET` | `/api/pacientes` | Listar todos los pacientes | `200 OK` | - |
| `GET` | `/api/pacientes/{id}` | Buscar paciente por ID | `200 OK` | `404 Not Found` |
| `GET` | `/api/pacientes/dni/{dni}` | Buscar paciente por DNI exacto | `200 OK` | `404 Not Found` |
| `GET` | `/api/pacientes/buscar?apellido=...` | Buscar por coincidencia de apellido | `200 OK` | - |
| `POST` | `/api/pacientes` | Registrar un nuevo paciente | `201 Created` | `400 Bad Request` |
| `PUT` | `/api/pacientes/{id}` | Modificar datos de un paciente | `200 OK` | `400 / 404` |
| `DELETE` | `/api/pacientes/{id}` | Eliminar paciente | `204 No Content` | `400 (turnos vinculados) / 404` |

### 👨‍⚕️ Profesionales (`/api/profesionales`)
| Método | Endpoint | Descripción | Código Éxito | Códigos Error |
|---|---|---|:---:|:---:|
| `GET` | `/api/profesionales` | Listar todos los médicos | `200 OK` | - |
| `GET` | `/api/profesionales/{id}` | Buscar profesional por ID | `200 OK` | `404 Not Found` |
| `GET` | `/api/profesionales/buscar?especialidad=...` | Filtrar profesionales por especialidad | `200 OK` | - |
| `POST` | `/api/profesionales` | Registrar profesional | `201 Created` | `400 Bad Request` |
| `PUT` | `/api/profesionales/{id}` | Modificar datos de un profesional | `200 OK` | `400 / 404` |
| `DELETE` | `/api/profesionales/{id}` | Eliminar profesional | `204 No Content` | `400 (turnos vinculados) / 404` |

### 📅 Turnos (`/api/turnos`)
| Método | Endpoint | Descripción | Código Éxito | Códigos Error |
|---|---|---|:---:|:---:|
| `GET` | `/api/turnos` | Listar todos los turnos | `200 OK` | - |
| `GET` | `/api/turnos/{id}` | Buscar turno por ID | `200 OK` | `404 Not Found` |
| `GET` | `/api/turnos/paciente/{idPaciente}` | **Consulta por relación:** Turnos de un paciente | `200 OK` | `404 (paciente no existe)` |
| `GET` | `/api/turnos/profesional/{idProfesional}` | **Consulta por relación:** Turnos de un médico | `200 OK` | `404 (médico no existe)` |
| `POST` | `/api/turnos` | Reservar nuevo turno médico | `201 Created` | `400 / 404` |
| `PUT` | `/api/turnos/{id}` | Reprogramar / modificar turno | `200 OK` | `400 / 404` |
| `DELETE` | `/api/turnos/{id}` | Cancelar / eliminar turno | `204 No Content` | `404 Not Found` |

---

## 🛡️ 6. Validaciones y Manejo de Errores

### Control de Datos de Entrada (Bean Validation)
* **`@NotBlank` y `@Size`:** Exigencia de nombres, apellidos y matrícula válidos.
* **`@Pattern`:** Formato numérico estricto para DNI (entre 7 y 10 cifras).
* **`@Email`:** Control de sintaxis de correo electrónico.
* **`@Future`:** La fecha de reserva de turno **no puede estar en el pasado**.

### Reglas de Negocio Protegidas
1. **Comprobación Previa de Existencia:** No se permite crear ni asociar turnos con un ID de paciente o profesional inexistente en la base de datos (responde **`404 Not Found`**).
2. **Disponibilidad Horaria sin Solapamiento:**
   * Si el médico ya tiene un turno reservado en ese mismo día y horario: responde **`400 Bad Request`** con `"Ese horario ya está reservado, elegí otro."`
   * Si el paciente ya cuenta con otro turno en ese horario: responde **`400 Bad Request`** con `"El paciente ya cuenta con un turno registrado en ese mismo horario."`
3. **No Duplicidad:** Control de DNI duplicado para pacientes y matrícula duplicada para profesionales (**`400 Bad Request`**).
4. **Conservación de la Integridad Referencial:** No se aplican cascadas que borren turnos silenciosamente. Al intentar eliminar un paciente o un profesional con turnos registrados, la API lo rechaza con **`400 Bad Request`** informando: `"No se puede eliminar porque tiene turnos asociados."`

### Estructura Uniforme de Error (`ErrorDTO`)
```json
{
  "status": 404,
  "mensaje": "No se encontró el paciente con ID: 99",
  "timestamp": "2026-10-19T14:30:00"
}
```

---

## 💻 7. Instrucciones de Instalación y Ejecución

### Requisitos
* **Java 21 JDK** o superior instalado.
* **MySQL 8.0+** corriendo en el puerto `3306` (XAMPP, MySQL Server o Docker).

### 1. Preparar la Base de Datos MySQL
En tu cliente MySQL (Workbench, phpMyAdmin o terminal), ejecuta el script de creación:
```sql
-- Ejecutar el contenido del archivo database/schema.sql
CREATE DATABASE IF NOT EXISTS turnoclinica
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
```
*(Opcional: puedes ejecutar también `database/data.sql` para tener registros precargados de prueba).*

### 2. Configurar Credenciales (si fuera necesario)
El archivo [`src/main/resources/application.properties`](file:///C:/Users/furia/.gemini/antigravity/scratch/turno-clinica/src/main/resources/application.properties) contiene los valores estándar locales:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/turnoclinica?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=America/Argentina/Cordoba
spring.datasource.username=root
spring.datasource.password=
```

### 3. Ejecutar la Aplicación
El proyecto incluye el Maven Wrapper oficial, por lo que **no necesitas tener Maven instalado**:

```powershell
# En Windows (PowerShell / CMD)
.\mvnw.cmd spring-boot:run

# En Linux / macOS
./mvnw spring-boot:run
```

La aplicación arrancará y estará disponible en:  
`http://localhost:8080`

### 4. Ejecutar la Suite de Pruebas Automatizadas
Para correr las pruebas unitarias y de integración que verifican los casos de la rúbrica:
```powershell
.\mvnw.cmd test
```
*(Los tests utilizan una base H2 en memoria aislada, garantizando resultado exitoso inmediato sin alterar tu base MySQL).*

---

## 🧪 8. Colección de Postman y Pruebas Reproducibles

Se incluye la colección completa en el repositorio:  
📂 **[`TurnoClinica.postman_collection.json`](./TurnoClinica.postman_collection.json)** (puedes importarla directamente en Postman).

A continuación se resumen los **6 casos de prueba mínimos** requeridos por la cátedra:

### 1️⃣ Caso 1: Crear un registro válido
* **POST** `http://localhost:8080/api/pacientes`
* **JSON:**
```json
{
  "nombre": "Ignacio",
  "apellido": "Ortiz",
  "dni": "41234567",
  "telefono": "+54 351 7778899",
  "email": "ignacio.ortiz@ucc.edu.ar",
  "obraSocial": "Medifé"
}
```
* **Resultado:** `201 Created` con DTO que incluye el `id` autogenerado.

### 2️⃣ Caso 2: Listar y buscar un registro existente
* **GET** `http://localhost:8080/api/pacientes/1`
* **Resultado:** `200 OK` con los datos persistidos del paciente.

### 3️⃣ Caso 3: Modificar y eliminar un registro
* **Modificar:** **PUT** `http://localhost:8080/api/pacientes/1` con nuevos datos -> **`200 OK`**.
* **Eliminar:** **DELETE** `http://localhost:8080/api/turnos/1` -> **`204 No Content`** (sin cuerpo).

### 4️⃣ Caso 4: Enviar datos que incumplan una validación
* **POST** `http://localhost:8080/api/pacientes` con campos vacíos o email inválido.
* **Resultado:** `400 Bad Request` y ningún registro inválido guardado.
* **POST** `http://localhost:8080/api/turnos` con fecha pasada (`"fechaHora": "2020-01-01T10:00:00"`).
* **Resultado:** `400 Bad Request` (`"La fecha y hora del turno no pueden ser pasadas"`).

### 5️⃣ Caso 5: Buscar un ID inexistente o asociar una entidad inexistente
* **GET** `http://localhost:8080/api/pacientes/99999` -> **`404 Not Found`**.
* **POST** `http://localhost:8080/api/turnos` asociando `idPaciente: 99999` -> **`404 Not Found`** (`"No se encontró el paciente con ID: 99999"`).

### 6️⃣ Caso 6: Consultar registros mediante una relación
* **GET** `http://localhost:8080/api/turnos/paciente/1`
* **Resultado:** `200 OK` con la lista de turnos asociados al paciente o lista vacía `[]` si no posee turnos aún.

### 7️⃣ Caso Adicional: Impedir eliminación de entidad con turnos vinculados
* **DELETE** `http://localhost:8080/api/pacientes/1` (con turnos activos).
* **Resultado:** `400 Bad Request` (`"No se puede eliminar porque tiene turnos asociados."`).

---

## 🚀 9. Próximos Pasos (Segundo Entregable)
En la próxima entrega se integrará:
1. **Seguridad con Spring Security:** Autenticación basada en JSON Web Tokens (JWT).
2. **Autorización y Roles:** Perfiles diferenciados (`ROLE_PACIENTE`, `ROLE_PROFESIONAL`, `ROLE_ADMIN`).
3. **Módulo de Sedes / Consultorios:** Gestión de consultorios físicos y asignación de salas.
4. **Notificaciones:** Integración de alertas automáticas para recordatorio de turnos.

---

<div align="center">
Desarrollado para la cátedra <b>Lenguajes de Programación II</b> — Universidad Católica de Cuyo (UCCuyo) 2026.
</div>
