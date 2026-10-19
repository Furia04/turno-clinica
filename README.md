# Turno Clínica - Sistema de Reserva de Turnos Médicos
**Lenguajes de Programación II - Universidad Católica de Córdoba (UCC)**  
**Primer Entregable del Trabajo Final - Octubre 2026**

---

## 👥 Integrantes del Grupo
* **Poblete Luis**
* **Manrique Leandro**
* **Ortiz Ignacio**

---

## 📖 1. Título y Problema que Resuelve

### Título del Proyecto
**Turno Clínica** — Plataforma Backend de Gestión y Reserva de Turnos Médicos.

### Problema
En centros médicos y consultorios ambulatorios, el registro manual de turnos por teléfono o en papel genera superposiciones horarias, pérdidas de turnos, cancelaciones desorganizadas y falta de trazabilidad histórica sobre las atenciones médicas.

**Turno Clínica** provee una API REST centralizada y persistente que garantiza la asignación coordinada de turnos, valida la disponibilidad horaria sin solapamientos, controla la integridad referencial de los pacientes y los profesionales, y ofrece consultas optimizadas por relación.

---

## 🎯 2. Usuarios Destinados
1. **Pacientes:** Consultan disponibilidad, solicitan y reservan turnos con un profesional médico en un horario específico.
2. **Personal de Recepción / Administrativos:** Gestionan la agenda integral de la clínica, registran profesionales y pacientes, reprograman o cancelan turnos.
3. **Profesionales Médicos:** Consultan su agenda diaria de turnos asignados por paciente y horario.

---

## 🏛️ 3. Modelo de Datos y Relaciones

El sistema implementa 3 entidades principales persistidas en MySQL mediante Spring Data JPA:

### Entidades y Atributos

1. **`Paciente` (`pacientes`)**
   * `id`: `Long` (Clave primaria autoincremental, `@GeneratedValue(strategy = GenerationType.IDENTITY)`)
   * `nombre`: `String` (Obligatorio, máx 100 caracteres)
   * `apellido`: `String` (Obligatorio, máx 100 caracteres)
   * `dni`: `String` (Obligatorio, único, numérico de 7 a 10 dígitos)
   * `telefono`: `String` (Formato válido)
   * `email`: `String` (Obligatorio, validado con formato `@Email`)
   * `obraSocial`: `String` (Opcional, nombre de la prepaga / obra social)

2. **`Profesional` (`profesionales`)**
   * `id`: `Long` (Clave primaria autoincremental, `@GeneratedValue(strategy = GenerationType.IDENTITY)`)
   * `nombre`: `String` (Obligatorio, máx 100 caracteres)
   * `apellido`: `String` (Obligatorio, máx 100 caracteres)
   * `matricula`: `String` (Obligatorio, único, matrícula profesional)
   * `especialidad`: `String` (Obligatorio, ej: Cardiología, Traumatología, etc.)

3. **`Turno` (`turnos`)**
   * `id`: `Long` (Clave primaria autoincremental, `@GeneratedValue(strategy = GenerationType.IDENTITY)`)
   * `fechaHora`: `LocalDateTime` (Obligatorio, fecha y hora futura del turno)
   * `motivo`: `String` (Opcional, descripción del motivo de consulta)
   * `estado`: `EstadoTurno` (Enum: `PENDIENTE`, `CONFIRMADO`, `CANCELADO`, `ATENDIDO`)
   * `paciente`: `@ManyToOne(fetch = FetchType.LAZY, optional = false)` con `@JoinColumn(name = "id_paciente")`
   * `profesional`: `@ManyToOne(fetch = FetchType.LAZY, optional = false)` con `@JoinColumn(name = "id_profesional")`

### Esquema y Cardinalidad de Relaciones

```
+---------------+              +---------------+              +-------------------+
|   PACIENTE    | 1          N |     TURNO     | N          1 |    PROFESIONAL    |
|---------------|--------------|---------------|--------------|-------------------|
| id (PK)       |              | id (PK)       |              | id (PK)           |
| dni (UNIQUE)  |              | fecha_hora    |              | matricula (UNIQUE)|
| nombre        |              | motivo        |              | nombre            |
| apellido      |              | estado        |              | apellido          |
| email         |              | id_pacienteFK |              | especialidad      |
| ...           |              | id_profesioFK |              |                   |
+---------------+              +---------------+              +-------------------+
```

* **Paciente (1) a (N) Turnos:** Un paciente puede registrar múltiples turnos. Un turno pertenece exclusivamente a un paciente.
* **Profesional (1) a (N) Turnos:** Un médico atiende múltiples turnos en su agenda. Un turno está asignado a un único profesional.
* **Paciente (N) a (N) Profesional:** Relación muchos a muchos resuelta a través de la entidad intermedia `Turno`.

---

## 🧱 4. Organización del Proyecto y Arquitectura

El proyecto sigue una arquitectura desacoplada por capas:
```
src/main/java/com/ucc/turnoclinica/
├── TurnoClinicaApplication.java   # Clase principal Spring Boot
├── controller/                    # Capa REST (@RestController, endpoints HTTP, ResponseEntity)
│   ├── PacienteController.java
│   ├── ProfesionalController.java
│   └── TurnoController.java
├── service/                       # Lógica de negocio y transacciones (@Service, @Transactional)
│   ├── PacienteService.java
│   ├── ProfesionalService.java
│   └── TurnoService.java
├── repository/                    # Acceso a base de datos (@Repository, Spring Data JPA)
│   ├── PacienteRepository.java
│   ├── ProfesionalRepository.java
│   └── TurnoRepository.java
├── entity/                        # Entidades JPA (@Entity, @Table, @Id, @ManyToOne)
│   ├── Paciente.java
│   ├── Profesional.java
│   ├── Turno.java
│   └── EstadoTurno.java
├── dto/                           # Data Transfer Objects (Java Records para entrada y salida)
│   ├── PacienteRequestDTO.java
│   ├── PacienteResponseDTO.java
│   ├── ProfesionalRequestDTO.java
│   ├── ProfesionalResponseDTO.java
│   ├── TurnoRequestDTO.java
│   ├── TurnoResponseDTO.java
│   └── ErrorDTO.java
├── mapper/                        # Mapeadores de conversión entre Entidad y DTO (@Component)
│   ├── PacienteMapper.java
│   ├── ProfesionalMapper.java
│   └── TurnoMapper.java
└── exception/                     # Excepciones propias y handler global (@RestControllerAdvice)
    ├── PacienteNotFoundException.java
    ├── ProfesionalNotFoundException.java
    ├── TurnoNotFoundException.java
    ├── ReglaDeNegocioException.java
    └── GlobalExceptionHandler.java
```

> **Buenas prácticas aplicadas:**
> * Inyección de dependencias exclusivamente por **constructor**.
> * El controlador **nunca** accede a los repositorios; interactúa únicamente con el servicio.
> * Los clientes envían y reciben únicamente DTOs (records), desacoplando las entidades de base de datos de la interfaz pública.

---

## 🚦 5. Funcionalidades y Endpoints Disponibles

### Pacientes (`/api/pacientes`)
| Método | Ruta | Descripción | Código Éxito | Códigos Error |
|---|---|---|---|---|
| `GET` | `/api/pacientes` | Listar todos los pacientes | `200 OK` | - |
| `GET` | `/api/pacientes/{id}` | Buscar paciente por ID | `200 OK` | `404 Not Found` |
| `GET` | `/api/pacientes/dni/{dni}` | Buscar paciente por DNI | `200 OK` | `404 Not Found` |
| `GET` | `/api/pacientes/buscar?apellido=...` | Filtrar pacientes por apellido | `200 OK` | - |
| `POST` | `/api/pacientes` | Registrar un nuevo paciente | `201 Created` | `400 Bad Request` |
| `PUT` | `/api/pacientes/{id}` | Actualizar datos del paciente | `200 OK` | `400 / 404` |
| `DELETE` | `/api/pacientes/{id}` | Eliminar paciente | `204 No Content`| `400 (con turnos) / 404` |

### Profesionales (`/api/profesionales`)
| Método | Ruta | Descripción | Código Éxito | Códigos Error |
|---|---|---|---|---|
| `GET` | `/api/profesionales` | Listar todos los médicos | `200 OK` | - |
| `GET` | `/api/profesionales/{id}` | Buscar profesional por ID | `200 OK` | `404 Not Found` |
| `GET` | `/api/profesionales/buscar?especialidad=...` | Filtrar por especialidad | `200 OK` | - |
| `POST` | `/api/profesionales` | Registrar profesional | `201 Created` | `400 Bad Request` |
| `PUT` | `/api/profesionales/{id}` | Actualizar profesional | `200 OK` | `400 / 404` |
| `DELETE` | `/api/profesionales/{id}` | Eliminar profesional | `204 No Content`| `400 (con turnos) / 404` |

### Turnos (`/api/turnos`)
| Método | Ruta | Descripción | Código Éxito | Códigos Error |
|---|---|---|---|---|
| `GET` | `/api/turnos` | Listar todos los turnos | `200 OK` | - |
| `GET` | `/api/turnos/{id}` | Buscar turno por ID | `200 OK` | `404 Not Found` |
| `GET` | `/api/turnos/paciente/{idPaciente}` | **Consulta por relación:** Turnos de un paciente | `200 OK` | `404 (paciente no existe)` |
| `GET` | `/api/turnos/profesional/{idProfesional}` | **Consulta por relación:** Turnos de un médico | `200 OK` | `404 (médico no existe)` |
| `POST` | `/api/turnos` | Reservar nuevo turno | `201 Created` | `400 / 404` |
| `PUT` | `/api/turnos/{id}` | Modificar / reprogramar turno | `200 OK` | `400 / 404` |
| `DELETE` | `/api/turnos/{id}` | Cancelar / eliminar turno | `204 No Content`| `404 Not Found` |

---

## 🛡️ 6. Validaciones y Manejo de Errores

### Validaciones de Entrada (Bean Validation con `@Valid`)
* `@NotBlank`, `@Size(min = 2, max = 100)`: Nombres, apellidos y especialidad obligatorios.
* `@Pattern(regexp = "\\d{7,10}")`: El DNI debe ser numérico y con longitud acorde (7 a 10 dígitos).
* `@Email`: Formato estricto y válido de correo electrónico.
* `@NotNull`: Identificadores de relaciones (`idPaciente`, `idProfesional`) y fecha del turno.
* `@Future`: La fecha y hora de la reserva **no pueden ser pasadas**.

### Reglas de Negocio e Integridad Referencial
1. **Verificación de Existencia de Entidades Relacionadas:** Antes de guardar o actualizar un `Turno`, se comprueba explícitamente en base de datos la existencia del `Paciente` y del `Profesional`. Si alguno no existe, se rechaza la petición con código **`404 Not Found`**.
2. **Disponibilidad Horaria (Sin Superposición):**
   * Se valida que el profesional no tenga otro turno en el mismo día y horario (`"Ese horario ya está reservado, elegí otro."` -> **`400 Bad Request`**).
   * Se valida que el paciente no posea otro turno asignado a la misma hora (`"El paciente ya cuenta con un turno registrado en ese mismo horario."` -> **`400 Bad Request`**).
3. **Unicidad de Datos Clave:** No se permite registrar DNI duplicado para pacientes ni matrícula duplicada para profesionales (**`400 Bad Request`**).
4. **Protección de Integridad al Eliminar (Sin borrado en cascada descontrolado):** Si se intenta eliminar un paciente o un profesional que posee turnos asociados, el sistema impide el borrado y devuelve **`400 Bad Request`** con el mensaje: `"No se puede eliminar porque tiene turnos asociados."`

### Formato de Error Consistente (`ErrorDTO`)
Todas las excepciones capturadas por `@RestControllerAdvice` retornan un JSON unificado:
```json
{
  "status": 404,
  "mensaje": "No se encontró el paciente con ID: 99",
  "timestamp": "2026-10-19T14:30:00"
}
```

---

## 💻 7. Requisitos e Instrucciones de Ejecución

### Requisitos Previos
* **Java:** JDK 21 instalado (LTS).
* **Maven:** Se incluye el wrapper oficial (`./mvnw` / `mvnw.cmd`), por lo que no es necesario instalar Maven manualmente.
* **MySQL:** Servidor MySQL 8.0+ en ejecución (por ejemplo mediante XAMPP, MySQL Server o Docker) en el puerto local por defecto `3306`.

### Configuración de la Base de Datos MySQL
1. Iniciar el servicio de MySQL (puerto `3306`).
2. Abrir MySQL Workbench, phpMyAdmin o terminal y ejecutar el script disponible en:
   [`database/schema.sql`](file:///C:/Users/furia/.gemini/antigravity/scratch/turno-clinica/database/schema.sql)
3. (Opcional) Cargar los datos de prueba iniciales ejecutando:
   [`database/data.sql`](file:///C:/Users/furia/.gemini/antigravity/scratch/turno-clinica/database/data.sql)

> **Nota:** La configuración en `src/main/resources/application.properties` se conecta a:  
> `jdbc:mysql://localhost:3306/turnoclinica?createDatabaseIfNotExist=true` con usuario `root` y contraseña vacía. Si su servidor MySQL utiliza otra contraseña, puede ajustarla en dicho archivo.

### Pasos para Iniciar la Aplicación

#### Opción A — Ejecución por consola con Maven Wrapper:
```bash
# En Windows (PowerShell / CMD)
.\mvnw.cmd spring-boot:run

# En Linux / macOS
./mvnw spring-boot:run
```

#### Opción B — Ejecución de Pruebas Automatizadas:
```bash
.\mvnw.cmd test
```
*(Las pruebas se ejecutan contra base H2 en memoria integrada en `application-test.properties`, garantizando ejecución inmediata e independiente).*

La aplicación se levantará en:  
`http://localhost:8080`

---

## 🧪 8. Peticiones Reproducibles y Casos de Prueba (Consigna)

Se incluye la colección lista para importar en Postman:  
📂 **[`TurnoClinica.postman_collection.json`](file:///C:/Users/furia/.gemini/antigravity/scratch/turno-clinica/TurnoClinica.postman_collection.json)**

A continuación se documentan los 6 casos de prueba mínimos exigidos:

---

### Caso 1: Crear un registro válido
* **Método:** `POST`
* **Ruta:** `http://localhost:8080/api/pacientes`
* **Headers:** `Content-Type: application/json`
* **Body:**
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
* **Resultado Esperado:** `201 Created` y DTO con ID generado:
```json
{
  "id": 1,
  "nombre": "Ignacio",
  "apellido": "Ortiz",
  "dni": "41234567",
  "telefono": "+54 351 7778899",
  "email": "ignacio.ortiz@ucc.edu.ar",
  "obraSocial": "Medifé"
}
```

---

### Caso 2: Listar y buscar un registro existente
* **Método:** `GET`
* **Ruta:** `http://localhost:8080/api/pacientes/1`
* **Resultado Esperado:** `200 OK` con los datos persistidos del paciente ID 1.

---

### Caso 3: Modificar y eliminar un registro
* **Modificación (PUT):**
  * **Ruta:** `http://localhost:8080/api/pacientes/1`
  * **Body:**
  ```json
  {
    "nombre": "Ignacio Gabriel",
    "apellido": "Ortiz",
    "dni": "41234567",
    "telefono": "+54 351 7778899",
    "email": "ignacio.gabriel@ucc.edu.ar",
    "obraSocial": "OSDE 310"
  }
  ```
  * **Resultado Esperado:** `200 OK` con los datos actualizados.
* **Eliminación (DELETE):**
  * **Ruta:** `http://localhost:8080/api/turnos/1`
  * **Resultado Esperado:** `204 No Content` (sin cuerpo).

---

### Caso 4: Enviar datos que incumplan una validación
* **Ejemplo 4A (DNI no numérico y email inválido):**
  * **Método:** `POST`
  * **Ruta:** `http://localhost:8080/api/pacientes`
  * **Body:**
  ```json
  {
    "nombre": "",
    "apellido": "",
    "dni": "no-es-numero",
    "email": "invalido-sin-arroba"
  }
  ```
  * **Resultado Esperado:** `400 Bad Request` y ningún registro guardado.
  ```json
  {
    "status": 400,
    "mensaje": "Datos inválidos: nombre: El nombre es obligatorio; dni: El DNI debe ser numérico y contener entre 7 y 10 dígitos; email: El formato de email no es válido",
    "timestamp": "2026-10-19T14:35:10"
  }
  ```

* **Ejemplo 4B (Fecha del turno en el pasado):**
  * **Método:** `POST`
  * **Ruta:** `http://localhost:8080/api/turnos`
  * **Body:**
  ```json
  {
    "fechaHora": "2020-01-01T10:00:00",
    "motivo": "Turno en fecha pasada",
    "idPaciente": 1,
    "idProfesional": 1
  }
  ```
  * **Resultado Esperado:** `400 Bad Request` (`"La fecha y hora del turno no pueden ser pasadas"`).

---

### Caso 5: Buscar un ID inexistente o asociar una entidad inexistente
* **Ejemplo 5A (Buscar paciente inexistente):**
  * **Método:** `GET`
  * **Ruta:** `http://localhost:8080/api/pacientes/99999`
  * **Resultado Esperado:** `404 Not Found`
  ```json
  {
    "status": 404,
    "mensaje": "No se encontró el paciente con ID: 99999",
    "timestamp": "2026-10-19T14:35:12"
  }
  ```

* **Ejemplo 5B (Asociar paciente inexistente a un nuevo turno):**
  * **Método:** `POST`
  * **Ruta:** `http://localhost:8080/api/turnos`
  * **Body:**
  ```json
  {
    "fechaHora": "2026-12-15T10:00:00",
    "motivo": "Consulta cardiológica",
    "idPaciente": 99999,
    "idProfesional": 1
  }
  ```
  * **Resultado Esperado:** `404 Not Found`
  ```json
  {
    "status": 404,
    "mensaje": "No se encontró el paciente con ID: 99999",
    "timestamp": "2026-10-19T14:35:15"
  }
  ```

---

### Caso 6: Consultar registros mediante una relación
* **Método:** `GET`
* **Ruta:** `http://localhost:8080/api/turnos/paciente/1`
* **Resultado Esperado:** `200 OK` con lista de turnos asignados al paciente o lista vacía `[]` si no tiene turnos:
```json
[
  {
    "id": 1,
    "fechaHora": "2026-11-25T14:30:00",
    "motivo": "Consulta de rutina y control general",
    "estado": "PENDIENTE",
    "paciente": {
      "id": 1,
      "nombre": "Ignacio",
      "apellido": "Ortiz",
      "dni": "41234567",
      "telefono": "+54 351 7778899",
      "email": "ignacio.ortiz@ucc.edu.ar",
      "obraSocial": "Medifé"
    },
    "profesional": {
      "id": 1,
      "nombre": "Ana",
      "apellido": "Martinez",
      "matricula": "MP-10452",
      "especialidad": "Cardiología"
    }
  }
]
```

---

### Caso 7 (Adicional): Intento de eliminar registro con turnos asociados
* **Método:** `DELETE`
* **Ruta:** `http://localhost:8080/api/pacientes/1` (teniendo turnos vinculados)
* **Resultado Esperado:** `400 Bad Request`
```json
{
  "status": 400,
  "mensaje": "No se puede eliminar porque tiene turnos asociados.",
  "timestamp": "2026-10-19T14:36:00"
}
```

---

## 🚀 9. Funcionalidades para el Segundo Entregable
Para la segunda etapa del proyecto se planifica incorporar:
1. **Seguridad y Control de Acceso:** Integración de Spring Security y autenticación stateless con tokens JWT.
2. **Roles y Autorización:** Perfiles diferenciados (`PACIENTE`, `PROFESIONAL`, `ADMINISTRATIVO`) para restringir el acceso a los endpoints.
3. **Entidad Sede / Consultorio:** Soporte para clínicas con múltiples consultorios y ubicaciones físicas.
4. **Notificaciones y Recordatorios:** Generación de avisos automáticos ante confirmación o reprogramación de turnos.
