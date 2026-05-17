# Arquitectura 4+1 - Recintos Deportivos Backend

El modelo **4+1 Vistas** describe la arquitectura del sistema desde cinco perspectivas complementarias. A continuación se presenta cada vista aplicada a tu proyecto Spring Boot.

---

## 1. Vista Lógica (Logical View)

Describe los elementos principales del diseño orientado a objetos: clases, interfaces y sus relaciones.

### Paquete: `model`

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              model / enums                                   │
├─────────────────────────────────────────────────────────────────────────────┤
│  «enumeration»                                                               │
│  EstadoCurso                                                                 │
├─────────────────────────────────────────────────────────────────────────────┤
│  + BORRADOR                                                                  │
│  + ACTIVO                                                                    │
│  + FINALIZADO                                                                │
│  + CANCELADO                                                                 │
└─────────────────────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────���────────────────────────────┐
│                              model                                           │
├─────────────────────────────────────────────────────────────────────────────┤
│  «entity»                         «entity»                «entity»          │
│  Usuario                           Categoria              Recinto            │
├─────────────────────────────────────────────────────────────────────────────┤
│  - idUsuario: Long (PK)           - id: Long (PK)        - idRecinto: Long   │
│  - correo: String                  - nombre: String       - nombre: String   │
│  - password: String                - descripcion: String - ubicacion: String│
│  - nombre: String                                         - descripcion: Str │
│  - apellido: String                                       - imagenUrl: String│
│  - telefono: String                                       - capacidad: Integer│
│  - rol: String                                            - coordenadasGPS:  │
│  - fcmToken: String                                       - estado: String   │
│  - informacion: String                                     │                  │
│  ──────────────────────────────────                     ─────────────────── │
│  + eventosGestionados: List<Evento> 1         *───────────► eventos         │
│  + inscripciones: List<Inscripcion> 1        *───────────►                  │
│  + inscripcionesConfirmadas: List<Inscripcion>            │                  │
│  + notificaciones: List<Notificacion> 1     *────────────┘                  │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│  «entity»                         «entity»                                  │
│  Evento                           Curso                                      │
├─────────────────────────────────────────────────────────────────────────────┤
│  - idEvento: Long                  - idCurso: Long                           │
│  - titulo: String                 - nombre: String                          │
│  - descripcion: String            - descripcion: String                     │
│  - imagenUrl: String              - fechaInicio: LocalDate                  │
│  - cupoMaximo: Integer            - fechaFin: LocalDate                     │
│  - horaInicio: LocalDateTime       - horaInicio: LocalTime                  │
│  - horaFin: LocalDateTime          - horaFin: LocalTime                      │
│  - fechaInicio: LocalDate          - dias: String                           │
│  - estado: String                 - cupo: Integer                           │
│  - maximoPorInscripcion: Integer  - maximoPorInscripcion: Integer          │
│  - inscritos: Long (transient)    - estado: EstadoCurso                     │
│  ─────────────────────────────────  ───────────────────────────────────────  │
│  + encargado: Usuario ◄─────────── + encargado: Usuario                     │
│  + recinto: Recinto ◄──────────── + recinto: Recinto                       │
│  + categoria: Categoria ◄───────── + categoria: Categoria                 │
│  + curso: Curso (nullable) ◄─────── + sesiones: List<Evento> 1      *     │
│  + inscripciones: List<Inscripcion>  + horarios: List<CursoHorario> 1 *   │
└────────────────────────────���────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│  «entity»                         «entity»              «entity»             │
│  Inscripcion                       Notificacion         CursoHorario         │
├─────────────────────────────────────────────────────────────────────────────┤
│  - idInscripcion: Long             - idNotificacion:    - id: Long          │
│  - nombre: String                 - mensaje: String    - dia: DayOfWeek    │
│  - apellido: String               - fecha: LocalDateTime- horaInicio: Time │
│  - edad: Integer                  - leido: boolean     - horaFin: Time      │
│  - fechaHoraRegistro: DateTime    - idEvento: Long     ─────────────────── │
│  - estadoAsistencia: String        ─────────────────────────────────────────│
│  ─────────────────────────────────  + usuario: Usuario ◄───────────────────┘
│  + evento: Evento ◄──────────────────────────────────────────────────────┘   │
│  + tutor: Usuario (opcional)                                                │
│  + encargadoConfirmacion: Usuario (opcional)                               │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

### Paquete: `service`

```
┌──────────────────────────────────────────────────��──────────────────────────┐
│                              service (interfaces)                           │
├─────────────────────────────────────────────────────────────────────────────┤
│  «interface»                                                                 │
│  ServicioUsuario                                                            │
├─────────────────────────────────────────────────────────────────────────────┤
│  + registrarUsuario(RegistroUsuarioDTO): ResponseUsuarioDTO                 │
│  + login(LoginUsuarioDTO): ResponseUsuarioDTO                              │
│  + obtenerPerfil(int): ResponseUsuarioDTO                                   │
│  + listarEncargados(): List<Usuario>                                        │
│  + obtenerHistorialInscripciones(Long): HistorialUsuarioDTO                 │
│  + guardarTokenFCM(Long, String): void                                     │
│  + registrarEncargado(RegistroUsuarioDTO): ResponseUsuarioDTO               │
│  + obtenerEncargado(Long): ResponseUsuarioDTO                              │
│  + actualizarEncargado(Long, UpdateEncargadoDTO): ResponseUsuarioDTO       │
│  + eliminarEncargado(Long): boolean                                         │
│  + actualizarPerfil(Long, UpdateUsuarioDTO): ResponseUsuarioDTO            │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│  «interface»                «interface»           «interface»               │
│  ServicioEvento              ServicioInscripcion  ServicioEstadistica     │
├──────────────────────────────┬─────────────────────┬──────────────────────┤
│  + listarEventos(): List    │ + inscribirUsuario() │ + obtenerEstadisticas()│
│  + buscarEvento(Long): Opt  │ + inscribirUsuarios  │ + obtenerEstadisticas │
│  + guardarEvento(DTO): Resp │   Masivo(List): List │   Cursos(Integer): DTO│
│  + editarEvento(Long, DTO)  │ + verificarEstadoIns │                       │
│  + eliminarEvento(int): Ev  │ + actualizarAsistencia│                       │
│  + cambiarEstado(Long,Str)  │ + inscribirUsuarios   │                       │
│  + listarEventosEnTranscur  │   ACurso(DTO)         │                       │
│  + obtenerCupo(Long): DTO   │ + verificarEstadoIns  │                       │
│  + listarEventosPorEncarg   │   ACurso(Long,Long):  │                       │
│  + asignarEncargado(Long,L) │                       │                       │
│  + listarEventosDisponibles │                       │                       │
│  + listarEventosSinCurso()  │                       │                       │
└──────────────────────────────┴─────────────────────┴──────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│  «interface»           «interface»            «interface»                    │
│  ServicioRecinto       ServicioCategoria      ServicioNotificacion          │
├───────────────────────┬──────────────────────┬───────────────────────────────┤
│  + listarRecintos()   │  + crearCategoria()  │  + enviarNotificacion()       │
│  + buscarRecinto(Long)│  + editarCategoria() │  + obtenerNotificaciones()     │
│  + guardarRecinto(DTO)│  + eliminarCategoria│  + marcarComoLeida(Long)       │
��  + editarRecinto()    │  + listarCategorias()│                                │
│  + eliminarRecinto()  │  + buscarCategoria()│                                │
│  + listarDisponibles()│                      │                                │
│  + cambiarEstado()    │                      │                                │
└───────────────────────┴──────────────────────┴───────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│  «interface»           «interface»            «interface»                    │
│  ServicioCurso         ServicioFirebase      StorageService                 │
├───────────────────────┬──────────────────────┬──────────────────────────────┤
│  + listarCursos()     │  + enviarNotificacion │  + guardarArchivo()           │
│  + listarDisponibles()│    push(Token, Msg)  │  + obtenerUrl()               │
│  + buscarCurso(Long)  │                      │  + eliminarArchivo()          │
│  + guardarCurso(Curso)│                      │                              │
│  + editarCurso(Long)  │                      │                              │
│  + eliminarCurso(Long)│                      │                              │
└───────────────────────┴──────────────────────┴──────────────────────────────┘
```

---

### Paquete: `controller`

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              controller                                      │
├─────��───────────────────────────────────────────────────────────────────────┤
│  «rest»                                 «rest»                               │
│  UsuarioController                      EventoController                     │
├─────────────────────────────────────────┬───────────────────────────────────┤
│  POST   /api/user/register              │  POST   /api/event/create          │
│  POST   /api/user/login                 │  GET    /api/event/avaible         │
│  POST   /api/user/logout                │  PUT    /api/event/edit/{id}       │
│  POST   /api/user/profile               │  DELETE /api/event/{id}             │
│  PUT    /api/user/{id}                  │  GET    /api/event/all             │
│  GET    /api/user/managers/available     │  GET    /api/event/{id}            │
│  GET    /api/user/{id}/history          │  PUT    /api/event/change-status/..│
│                                         │  GET    /api/event/in-progress    │
│                                         │  GET    /api/event/{id}/quota      │
│                                         │  GET    /api/event/{id}/inscritos  │
│                                         │  GET    /api/event/manager/{id}    │
│                                         │  PUT    /api/event/{id}/assign... │
└─────────────────────────────────────────┴───────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│  «rest»                             «rest»                                  │
│  CursoController                    InscripcionController                   │
├─────────────────────────────���───────┬────────────────────────────────────────┤
│  GET    /api/curso/all              │  POST   /api/inscripcion/register      │
│  POST   /api/curso/create           │  POST   /api/inscripcion/register-batch│
│  GET    /api/curso/{id}             │  GET    /api/inscripcion/check          │
│  PUT    /api/curso/edit/{id}        │  GET    /api/inscripcion/check-course   │
│  DELETE /api/curso/{id}            │  PUT    /api/inscripcion/attendance..  │
│  POST   /api/curso/{id}/publicar   │  PUT    /api/inscripcion/register-course│
│  POST   /api/curso/{id}/cancelar   │                                       │
│  GET    /api/curso/avaible         │                                       │
└─────────────────────────────────────┴────────────────────────────────────────┘

┌───────────────────────────────────────���──────────────────────────────────────┐
│  «rest»                    «rest»                   «rest»                     │
│  RecintoController        CategoriaController    NotificacionController       │
├──────────────────────────┼───────────────────────┼───────────────────────────┤
│  POST   /api/recinto/..   │  POST   /api/categoria│  GET    /api/notificaciones│
│  GET    /api/recinto/all  │  PUT    /api/categoria│  PUT    /api/notificaciones│
│  GET    /api/recinto/{id} │  DELETE /api/categoria │  POST   /api/notificaciones│
│  PUT    /api/recinto/..   │  GET    /api/categoria │                            │
│  DELETE /api/recinto/{id} │  GET    /api/categoria │                            │
│  GET    /api/recinto/ava  │      /all                                           │
│  PUT    /api/recinto/..   │  GET    /api/categoria │                            │
└───────��──────────────────┴───────────────────────┴───────────────────────────┘
```

---

## 2. Vista de Proceso (Process View)

Describe los procesos并发 y las interacciones entre componentes (hilos, tareas, mensajes).

### Diagrama de Comunicación entre Componentes

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              CLIENT (Mobile/Web)                            │
└─────────────────────────────────┬───────────────────────────────────────────┘
                                  │ HTTP/REST
                                  │ Cookies (JWT)
                                  ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                         EventoController                                    │
│  ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐              │
│  │ crearEvento()   │ │ editarEvento()   │ │ eliminarEvento()│              │
│  └────────┬────────┘ └────────┬────────┘ └────────┬────────┘              │
└──────────┼────────────────────┼────────────────────┼──────────────────────┘
           │                    │                    │
           ▼                    ▼                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                          ServicioEvento                                     │
│  ┌─────────────────────────────────────────────────────────────────┐        │
│  │  + guardarEvento()  + editarEvento()  + eliminarEvento()       │        │
│  └────────────────────────────────┬────────────────────────────────┘        │
└──────────────────────────────────┼──────────────────────────────────────────┘
                                   │
           ┌───────────────────────┼───────────────────────┐
           ▼                       ▼                       ▼
┌──────────────────┐    ┌──────────────────┐    ┌──────────────────┐
│ RepositorioEvento│    │  ServicioFirebase│    │ ServicioNotific.  │
│                  │    │                  │    │                  │
│ JpaRepository    │    │ Push Notifications│    │ FCM Integration  │
│                  │    │                  │    │                  │
└────────┬─────────┘    └──────────────────┘    └──────────────────┘
         │
         │ JPA
         ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                           PostgreSQL Database                               │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐      │
│  │ usuarios │  │ recinto  │  │ evento   │  │inscripcion│ │ categoria│      │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘  └──────────┘      │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Flujo de Inscripción

```
┌──────────────┐     ┌───────────────────┐     ┌──────────────────────┐
│  Mobile App │────►│InscripcionController│────►│ ServicioInscripcion  │
└──────────────┘     └───────────────────┘     └──────────┬───────────┘
                                                           │
           ┌───────────────────────────────────────────────┼───────────────┐
           │                                               │               │
           ▼                                               ▼               ▼
┌──────────────────┐    ┌──────────────────┐    ┌──────────────────┐
│ RepositorioEvento│    │ RepositorioIns.  │    │ ServicioFirebase │
│                  │    │                  │    │                  │
│ validateCupo()   │    │ save(Inscripcion)│    │ Push Notification│
└────────┬─────────┘    └────────┬─────────┘    └────────┬─────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌──────────────────┐    ┌───��──────────────┐    ┌──────────────────┐
│   PostgreSQL     │    │   PostgreSQL     │    │   FCM Server      │
│   (cupo -1)      │    │   (inscripcion)   │    │                   │
└──────────────────┘    └──────────────────┘    └──────────────────┘
```

---

## 3. Vista de Desarrollo (Development View)

Describe la organización del código fuente y los módulos del proyecto.

### Estructura de Paquetes

```
com.recintos.municipalidad/
│
├── RecintosBackendApplication.java          (Main Application)
│
├── config/
│   ├── SecurityConfig.java                  (Spring Security config)
│   ├── WebConfig.java                       (CORS, WebSocket config)
│   ├── FirebaseConfig.java                  (Firebase initialization)
│   ├── WebSocketConfig.java                 (WebSocket endpoints)
│   └── DataSeeder.java                      (Initial data population)
│
├── controller/
│   ├── dto/                                (Data Transfer Objects)
│   │   ├── RegistroUsuarioDTO.java
│   │   ├── LoginUsuarioDTO.java
│   │   ├── ResponseUsuarioDTO.java
│   │   ├── UpdateUsuarioDTO.java
│   │   ├── UpdateEncargadoDTO.java
│   │   ├── CrearEventoDTO.java
│   │   ├── EditarEventoDTO.java
│   │   ├── ResponseEventoDTO.java
│   │   ├── EventoConAsistentesDTO.java
│   │   ├── CupoEventoDTO.java
│   │   ├── CreateCursoDTO.java
│   │   ├── UpdateCursoDTO.java
│   │   ├── ResponseCursoDTO.java
│   │   ├── CursoHorarioDTO.java
│   │   ├── InscripcionDTO.java
│   │   ├── InscripcionBatchResponseDTO.java
│   │   ├── InscripcionCursoDTO.java
│   │   ├── InscripcionCursoMasivaDTO.java
│   │   ├── InscripcionEstadoCursoResponseDTO.java
│   │   ├── SesionDTO.java
│   │   ├── UpdateAsistenciaDTO.java
│   │   ├── CrearRecintoDTO.java
│   │   ├── EditarRecintoDTO.java
│   │   ├── ResponseRecintoDTO.java
│   │   ├��─ CategoriaDTO.java
│   │   ├── EstadisticasResponseDTO.java
│   │   ├── EstadisticasCursosDTO.java
│   │   ├── EstadisticaDTO.java
│   │   ├── HistorialUsuarioDTO.java
│   │   ├── HistorialInscripcionDTO.java
│   │   ├── AsistenteDTO.java
│   │   ├── EstadoInscripcionDTO.java
│   │   └── UsuarioDTO.java
│   │
│   ├── UsuarioController.java
│   ├── EventoController.java
│   ├── CursoController.java
│   ├── InscripcionController.java
│   ├── RecintoController.java
│   ├── CategoriaController.java
│   ├── NotificacionController.java
│   ├── EstadisticaController.java
│   ├── EncargadoController.java
│   ├── StorageController.java
│   └── CsrfController.java
│
├── service/
│   ├── ServicioUsuario.java
│   ├── ServicioEvento.java
│   ├── ServicioCurso.java
│   ├── ServicioInscripcion.java
│   ├── ServicioRecinto.java
│   ├── ServicioCategoria.java
│   ├── ServicioNotificacion.java
│   ├── ServicioEstadistica.java
│   ├── ServicioFirebase.java
│   ├── StorageService.java
│   ├── CursoStatusService.java
│   └── impl/
│       ├── ServicioUsuarioImpl.java
│       ├── ServicioEventoImpl.java
│       ├── ServicioCursoImpl.java
│       ├── ServicioInscripcionImpl.java
│       ├── ServicioRecintoImpl.java
│       ├── ServicioCategoriaImpl.java
│       ├── ServicioNotificacionImpl.java
│       ├── ServicioEstadisticaImpl.java
│       ├── ServicioFirebaseImpl.java
│       └── FileSystemStorageService.java
│
├── repository/
│   ├── RepositorioUsuario.java
│   ├── RepositorioEvento.java
│   ├── RepositorioCurso.java
│   ├── RepositorioInscripcion.java
│   ├── RepositorioRecinto.java
│   ├── RepositorioCategoria.java
│   └── RepositorioNotificacion.java
│
├── model/
│   ├── enums/
│   │   └── EstadoCurso.java
│   ├── Usuario.java
│   ├── Evento.java
│   ├── Curso.java
│   ├── CursoHorario.java
│   ├── Inscripcion.java
│   ��── Recinto.java
│   ├── Categoria.java
│   ├── Notificacion.java
│   └── SesionId.java
│
├── mappers/
│   ├── IGenericMapper.java
│   ├── UsuarioMapper.java
│   ├── EventoMapper.java
│   ├── CursoMapper.java
│   ├── RecintoMapper.java
│   └── CategoriaMapper.java
│
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── ErrorResponse.java
│   ├── UsuarioYaInscritoException.java
│   └── SinCupoException.java
│
└── filter/
    ├── CsrfValidationFilter.java
    └── SecurityHeadersFilter.java
```

### Dependencias entre Paquetes

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        dependency_graph                                      │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   controller ────────────────► service                                       │
│       │                              │                                       │
│       │                              │                                       │
│       ▼                              ▼                                       │
│   controller.dto ◄─────────────── service                                   │
│       │                              │                                       │
│       │                              ▼                                       │
│       └─────────────────────────── model                                    │
│       │                              │                                       │
│       │                              ▼                                       │
│       │                        repository                                   │
│       │                              │                                       │
│       ▼                              ▼                                       │
│   mappers ◄──────────────────── repository                                   │
│       │                              │                                       │
│       │                              ▼                                       │
│       └─────────────────────────── model                                     │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 4. Vista Física (Physical View)

Describe la infraestructura de deployment: servidores, bases de datos, y redes.

### Arquitectura de Deployment

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              PRODUCTION ENVIRONMENT                         │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │                         Docker Compose Stack                          │   │
│  │                                                                       │   │
│  │  ┌─────────────────┐              ┌─────────────────┐               │   │
│  │  │   Spring Boot   │              │   PostgreSQL    │               │   │
│  │  │   Application   │              │   Database      │               │   │
│  │  │                 │              │                 │               │   │
│  │  │  Port: 8080     │◄────────────►│  Port: 5432     │               │   │
│  │  │                 │   JDBC       │                 │               │   │
│  │  └────────┬────────┘              └─────────────────┘               │   │
│  │           │                                                       │   │
│  │           │ HTTP                                                  │   │
│  │           ▼                                                       │   │
│  │  ┌─────────────────────────────────────────────────────────────┐   │   │
│  │  │                         INTERNET                             │   │   │
│  │  └─────────────────────────────────────────────────────────────┘   │   │
│  │           │                                                       │   │
│  │           ▼                                                       │   │
│  │  ┌─────────────────┐              ┌─────────────────┐               │   │
│  │  │   Mobile App    │              │   Web Client    │               │   │
│  │  │   (Android/iOS) │              │   (React/Vue)  │               │   │
│  │  └─────────────────┘              └─────────────────┘               │   │
│  │                                                                       │   │
│  │  ┌─────────────────┐              ┌─────────────────┐               │   │
│  │  │   Firebase      │              │   File System   │               │   │
│  │  │   FCM Server    │              │   Storage       │               │   │
│  │  │                 │              │   (uploads/)    │               │   │
│  │  └─────────────────┘              └─────────────────┘               │   │
│  │                                                                       │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Configuración de Servidores

| Componente | Tecnología | Puerto | Descripción |
|------------|-------------|--------|-------------|
| Backend API | Spring Boot 3.5 | 8080 | REST API principal |
| Database | PostgreSQL | 5432 | Almacenamiento de datos |
| File Storage | Local FS | - | Almacenamiento de imágenes |
| FCM | Firebase Cloud Messaging | - | Push notifications |

### application.properties / application.yml

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:postgresql://db:5432/recintos_deportivos
    username: postgres
    password: ${DB_PASSWORD}
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false

app:
  jwt:
    secret: ${JWT_SECRET}
```

---

## 5. Vista de Escenarios (Scenario View)

Describe casos de uso críticos del sistema.

### Escenario 1: Registro e Inscripción a Evento

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│  Participante│     │  Controller  │     │   Service    │     │ Repository  │
└──────┬───────┘     └──────┬───────┘     └──────┬───────┘     └─────��┬───────┘
       │                    │                    │                    │
       │ 1. POST /register  │                    │                    │
       │───────────────────►│                    │                    │
       │                    │ 2. registrarUsuario│                    │
       │                    │───────────────────►│                    │
       │                    │                    │ 3. findByCorreo()   │
       │                    │                    │───────────────────►│
       │                    │                    │◄───────────────────│
       │                    │                    │ 4. save(usuario)    │
       │                    │                    │───────────────────►│
       │                    │                    │◄───────────────────│
       │                    │◄───────────────────│                    │
       │◄───────────────────│                    │                    │
       │ 201 Created        │                    │                    │
       │                    │                    │                    │
       │ ──────────────────────────────────────────────────────────────│
       │                    │                    │                    │
       │ 5. POST /inscripcion/register            │                    │
       │───────────────────►│                    │                    │
       │                    │ 6. inscribirUsuario│                    │
       │                    │───────────────────►│                    │
       │                    │                    │ 7. findById(evento)│
       │                    │                    │──────────────���────►│
       │                    │                    │◄───────────────────│
       │                    │                    │ 8. countByEvento() │
       │                    │                    │───────────────────►│
       │                    │                    │◄───────────────────│
       │                    │                    │ 9. save(inscripcion)│
       │                    │                    │───────────────────►│
       │                    │                    │◄───────────────────│
       │                    │                    │10.enviarNotificacion│
       │                    │                    │───────────────────►│
       │                    │◄───────────────────│                    │
       │◄───────────────────│                    │                    │
       │ 201 Created        │                    │                    │
```

### Escenario 2: Gestión de Cursos

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│  Encargado   │     │  Controller  │     │   Service    │     │   Service    │
└──────┬───────┘     └──────┬───────┘     └──────┬───────┘     └──────┬───────┘
       │                    │                    │                    │
       │ 1. POST /create    │                    │                    │
       │───────────────────►│                    │                    │
       │                    │ 2. crearCurso()    │                    │
       │                    │───────────────────►│                    │
       │                    │                    │ 3. buscarRecinto() │
       │                    │                    │───────────────────►│
       │                    │                    │◄───────────────────│
       │                    │                    │ 4. buscarCategoria │
       │                    │                    │───────────────────►│
       │                    │                    │◄───────────────────│
       │                    │                    │ 5. save(curso)     │
       │                    │                    │───────────────────►│
       │                    │                    │ 6. generarSesiones │
       │                    │                    │───────────────────►│
       │                    │                    │◄───────────────────│
       │                    │◄───────────────────│                    │
       │◄───────────────────│                    │                    │
       │ 201 Created       │                    │                    │
       │                    │                    │                    │
       │ 7. POST /publicar │                    │                    │
       │───────────────────►│                    │                    │
       │                    │ 8. publicarCurso() │                    │
       │                    │───────────────────►│                    │
       │                    │                    │ 9. actualizarEstado│
       │                    │                    │───────────────────►│
       │                    │◄───────────────────│                    │
       │◄───────────────────│                    │                    │
       │ 200 OK            │                    │                    │
```

---

## Resumen de Vistas

| Vista | Descripción | Artefactos Principales |
|-------|-------------|----------------------|
| **Lógica** | Estructura de clases y objetos | Modelos, Services, Controllers, DTOs |
| **Proceso** | Hilos, concurrencia, procesos | Flujos de请求, interacciones entre componentes |
| **Desarrollo** | Organización del código fuente | Estructura de paquetes, módulos |
| **Física** | Deployment y arquitectura de infrastructure | Docker, PostgreSQL, Spring Boot |
| **Escenarios** | Casos de uso críticos | Diagramas de secuencia para flujos principales |
