# Modelo Entidad-Relación - Recintos Deportivos

## Diagrama Conceptual

```
┌─────────────────┐       ┌─────────────────┐
│    Usuario      │       │   Categoria     │
├─────────────────┤       ├─────────────────┤
│ PK idUsuario    │       │ PK id           │
│    correo       │       │    nombre       │
│    password     │       │    descripcion  │
│    nombre       │       └─────────────────┘
│    apellido     │                │
│    telefono     │                │
│    rol          │         ┌──────┴──────┐
│    fcmToken     │         │             │
│    informacion  │         ▼             ▼
└────────┬────────┘  ┌─────────────┐ ┌─────────────┐
         │           │   Evento    │ │    Curso    │
         │           ├─────────────┤ ├─────────────┤
         │           │ PK idEvento │ │ PK idCurso  │
         │           │    titulo   │ │    nombre   │
         │           │    descrip. │ │    descrip. │
         │           │    imagenUrl│ │    fechaIni │
         │           │    cupoMax  │ │    fechaFin │
         │           │    horaIni  │ │    horaIni  │
         │           │    horaFin  │ │    horaFin  │
         │           │    fechaIni │ │    dias     │
         │           │    estado    │ │    cupo     │
         │           │    maxXIns   │ │    maxXIns  │
         │           │ FK idEncarg.◄┼┘ FK idEncarg│
         │           │ FK idRecinto │ │ FK idRecinto│
         │           │ FK idCategor.│ │ FK idCateg. │
         └─────┐     │ FK idCurso   │ │    estado   │
               │     └──────┬──────┘ └──────┬──────┘
               │            │               │
               │            │               │
         ┌─────┴────────────┴───────┐  ┌─────┴────────┐
         │         Inscripcion      │  │ CursoHorario │
         ├─────────────────────────┤  ├──────────────┤
         │ PK idInscripcion         │  │ PK id        │
         │    nombre                │  │    dia       │
         │    apellido              │  │    horaInicio│
         │    edad                  │  │    horaFin   │
         │    fechaHoraRegistro     │  │ FK idCurso   │
         │    estadoAsistencia      │  └──────────────┘
         │ FK idEvento              │
         │ FK idTutor        ◄──────┘
         │ FK idEncargadoConfirm.
         └──────────��──────────────┘

┌─────────────────┐
│   Recinto       │
├─────────────────┤
│ PK idRecinto    │
│    nombre       │
│    ubicacion    │
│    descripcion  │
│    imagenUrl    │
│    capacidad    │
│    coordenadasGPS│
│    estado       │
└─────────────────┘

┌─────────────────┐
│  Notificacion   │
├─────────────────┤
│ PK idNotificacion│
│    mensaje      │
│    fecha        │
│    leido        │
│    idEvento     │
│ FK idUsuario    │
└─────────────────┘
```

---

## Detalle de Entidades

### Usuario
| Campo | Tipo | Constraints |
|-------|------|-------------|
| idUsuario | BIGSERIAL | PK, NOT NULL |
| correo | VARCHAR | UNIQUE, NOT NULL |
| password | VARCHAR | NOT NULL |
| nombre | VARCHAR | - |
| apellido | VARCHAR | - |
| telefono | VARCHAR | - |
| rol | VARCHAR | NOT NULL |
| fcmToken | VARCHAR | - |
| informacion | TEXT | - |

### Recinto
| Campo | Tipo | Constraints |
|-------|------|-------------|
| idRecinto | BIGSERIAL | PK, NOT NULL |
| nombre | VARCHAR | - |
| ubicacion | VARCHAR | - |
| descripcion | VARCHAR | - |
| imagenUrl | VARCHAR | - |
| capacidad | INTEGER | - |
| coordenadasGPS | VARCHAR | - |
| estado | VARCHAR | - |

### Categoria
| Campo | Tipo | Constraints |
|-------|------|-------------|
| id | BIGSERIAL | PK, NOT NULL |
| nombre | VARCHAR | - |
| descripcion | VARCHAR | - |

### Evento
| Campo | Tipo | Constraints |
|-------|------|-------------|
| idEvento | BIGSERIAL | PK, NOT NULL |
| titulo | VARCHAR | - |
| descripcion | VARCHAR | - |
| imagenUrl | VARCHAR | - |
| cupoMaximo | INTEGER | - |
| horaInicio | TIMESTAMP | - |
| horaFin | TIMESTAMP | - |
| fechaInicio | DATE | - |
| estado | VARCHAR | - |
| maximoPorInscripcion | INTEGER | - |
| idEncargado | BIGINT | FK → Usuario |
| idRecinto | BIGINT | FK → Recinto |
| idCategoria | BIGINT | FK → Categoria |
| idCurso | BIGINT | FK → Curso (nullable) |

### Curso
| Campo | Tipo | Constraints |
|-------|------|-------------|
| idCurso | BIGSERIAL | PK, NOT NULL |
| nombre | VARCHAR | - |
| descripcion | VARCHAR | - |
| fechaInicio | DATE | - |
| fechaFin | DATE | - |
| horaInicio | TIME | - |
| horaFin | TIME | - |
| dias | VARCHAR | - |
| cupo | INTEGER | - |
| maximoPorInscripcion | INTEGER | - |
| idRecinto | BIGINT | FK → Recinto |
| idEncargado | BIGINT | FK → Usuario |
| idCategoria | BIGINT | FK → Categoria |
| estado | VARCHAR | - |

### Inscripcion
| Campo | Tipo | Constraints |
|-------|------|-------------|
| idInscripcion | BIGSERIAL | PK, NOT NULL |
| nombre | VARCHAR | - |
| apellido | VARCHAR | - |
| edad | INTEGER | - |
| fechaHoraRegistro | TIMESTAMP | - |
| estadoAsistencia | VARCHAR | - |
| idEvento | BIGINT | FK → Evento, NOT NULL |
| idTutor | BIGINT | FK → Usuario |
| idEncargado | BIGINT | FK → Usuario |

### Notificacion
| Campo | Tipo | Constraints |
|-------|------|-------------|
| idNotificacion | BIGSERIAL | PK, NOT NULL |
| mensaje | VARCHAR | - |
| fecha | TIMESTAMP | - |
| leido | BOOLEAN | - |
| idEvento | BIGINT | - |
| idUsuario | BIGINT | FK → Usuario, NOT NULL |

### CursoHorario
| Campo | Tipo | Constraints |
|-------|------|-------------|
| id | BIGSERIAL | PK, NOT NULL |
| dia | VARCHAR | - |
| horaInicio | TIME | - |
| horaFin | TIME | - |
| idCurso | BIGINT | FK → Curso |

---

## Relaciones Detalladas

| Relación | Tipo | Descripción |
|----------|------|-------------|
| Usuario → Evento | 1:N | Un usuario puede gestionar múltiples eventos (como encargado) |
| Usuario → Inscripcion | 1:N | Un usuario (tutor) puede inscribir múltiples personas |
| Usuario → Inscripcion | 1:N | Un usuario puede confirmar múltiples inscripciones |
| Usuario → Notificacion | 1:N | Un usuario recibe múltiples notificaciones |
| Recinto → Evento | 1:N | Un recinto puede tener múltiples eventos |
| Recinto → Curso | 1:N | Un recinto puede tener múltiples cursos |
| Categoria → Evento | 1:N | Una categoría puede clasificar múltiples eventos |
| Categoria → Curso | 1:N | Una categoría puede clasificar múltiples cursos |
| Evento → Inscripcion | 1:N | Un evento puede tener múltiples inscripciones |
| Evento → Notificacion | 1:N | Un evento puede generar múltiples notificaciones |
| Curso → Evento | 1:N | Un curso genera múltiples sesiones (eventos) |
| Curso → CursoHorario | 1:N | Un curso tiene múltiples horarios |
| Inscripcion → Evento | N:1 | Cada inscripción pertenece a un evento |

---

## Cardinalidades

- **Usuario**: Entidad independiente sin dependencias principales
- **Recinto**: Entidad independiente, referenciada por Evento y Curso
- **Categoria**: Entidad independiente, referenciada por Evento y Curso
- **Evento**: Depende de Recinto, Categoria, Usuario (encargado) y opcionalmente Curso
- **Curso**: Depende de Recinto, Categoria, Usuario (encargado)
- **Inscripcion**: Depende de Evento y Usuario (tutor y encargado de confirmación)
- **Notificacion**: Depende de Usuario
- **CursoHorario**: Depende de Curso