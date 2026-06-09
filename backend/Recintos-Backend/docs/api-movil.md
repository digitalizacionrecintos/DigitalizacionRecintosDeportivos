# API Recintos Deportivos - Documentación para App Móvil

**Base URL:** `http://<host>:8080/api`

**Autenticación:** Cookies `auth_token` (HttpOnly) + `session_active` seteadas en `POST /api/user/login`.

---

## 1. Autenticación y Usuarios (`/api/user`)

### Registro
```
POST /api/user/register
Body: { "correo": "...", "contrasena": "...", "nombre": "...", "apellido": "...", "telefono": "..." }
→ 201: ResponseUsuarioDTO | 409: "El correo ya está registrado"
```

### Login
```
POST /api/user/login
Body: { "correo": "...", "contrasena": "..." }
→ 200: ResponseUsuarioDTO + cookies (auth_token, session_active) | 401
```

### Logout
```
POST /api/user/logout
→ 200: { "message": "Sesion cerrada exitosamente" }
```

### Perfil
```
POST /api/user/profile
Body (raw int): 1
→ 200: ResponseUsuarioDTO | 404
```

### Actualizar perfil
```
PUT /api/user/{id}
Body: { "nombre": "...", "apellido": "...", "telefono": "...", "correo": "...", "informacion": "..." }
→ 200: ResponseUsuarioDTO | 400
```

### Historial del usuario
```
GET /api/user/{id}/history
→ 200: HistorialUsuarioDTO
```

### Listar encargados disponibles
```
GET /api/user/managers/available
→ 200: [ Usuario, ... ]
```
## 2. Recintos (`/api/recinto`)

### Listar todos
```
GET /api/recinto/all → 200: [ Recinto, ... ]
```

### Listar disponibles
```
GET /api/recinto/available → 200: [ Recinto, ... ]
```

### Obtener por ID
```
GET /api/recinto/{id} → 200: Recinto | 404
```

### Crear
```
POST /api/recinto/create
Body: { "nombre": "...", "ubicacion": "...", "descripcion": "...", "imagenUrl": "...", "capacidad": 100, "coordenadasGPS": "..." }
→ 201: ResponseRecintoDTO
```

### Editar
```
PUT /api/recinto/edit/{id}
Body: { "nombre": "...", "ubicacion": "...", "descripcion": "...", "imagenUrl": "...", "capacidad": 150, "coordenadasGPS": "...", "estado": "ACTIVO" }
→ 200: Recinto | 404 | 400
```

### Eliminar
```
DELETE /api/recinto/{id} → 200: Recinto | 404
```

### Cambiar estado
```
PUT /api/recinto/change-status/{id}
Body (raw string): "ACTIVO" | "INACTIVO" | "MANTENCION"
→ 200: Recinto | 404
```

---

## 3. Eventos (`/api/event`)

### Listar todos
```
GET /api/event/all → 200: [ Evento, ... ]
```

### Obtener por ID
```
GET /api/event/{id} → 200: Evento | 404
```

### Crear
```
POST /api/event/create
Body: {
  "titulo": "...",
  "descripcion": "...",
  "imagenUrl": "...",
  "horaInicio": "2025-01-15T10:00:00",
  "horaFin": "2025-01-15T12:00:00",
  "fechaInicio": "2025-01-15",
  "categoriaId": 1,
  "cupoMaximo": 100,
  "publicoObjetivo": "general",
  "recintoId": 1,
  "encargadoId": 2,
  "maximoPorInscripcion": 5,
  "cursoId": null
}
→ 201: ResponseEventoDTO
```

### Editar
```
PUT /api/event/edit/{id}
Body: { "descripcion": "...", "horaInicio": "...", "horaFin": "...", "fechaInicio": "...", "cupoMaximo": 100, "recintoId": 1, "encargadoId": 2, "categoriaId": 1, "maximoPorInscripcion": 5, "cursoId": null }
→ 200: Evento | 404
```

### Eliminar
```
DELETE /api/event/{id} → 200: Evento | 404
```

### Cambiar estado
```
PUT /api/event/change-status/{id}
Body (raw string): "DISPONIBLE" | "TERMINADO" | "CANCELADO" | "EN_PROGRESO"
→ 200: Evento | 404
```

### Eventos disponibles (sin curso)
```
GET /api/event/avaible → 200: [ Evento, ... ]
```

### Eventos en progreso
```
GET /api/event/in-progress → 200: [ Evento, ... ]
```

### Cupo del evento
```
GET /api/event/{id}/quota → 200: CupoEventoDTO
```

### Contar inscritos
```
GET /api/event/{id}/inscritos/count → 200: Long
```

### Eventos por encargado
```
GET /api/event/manager/{idEncargado} → 200: [ EventoConAsistentesDTO, ... ]
```

### Asignar encargado
```
PUT /api/event/{idEvento}/assign-manager/{idEncargado} → 200: Evento | 400
```

---

## 4. Cursos (`/api/curso`)

### Listar todos
```
GET /api/curso/all → 200: [ ResponseCursoDTO, ... ]
```

### Listar disponibles
```
GET /api/curso/avaible → 200: [ Curso, ... ]
```

### Obtener por ID
```
GET /api/curso/{id} → 200: ResponseCursoDTO | 404
```

### Crear
```
POST /api/curso/create
Body: {
  "nombre": "...",
  "descripcion": "...",
  "fechaInicio": "2025-03-01",
  "fechaFin": "2025-06-30",
  "horaInicio": "09:00:00",
  "horaFin": "10:00:00",
  "dias": "LUNES, MIERCOLES, VIERNES",
  "cupo": 20,
  "maximoPorInscripcion": 1,
  "idRecinto": 1,
  "idEncargado": 2,
  "idCategoria": 3,
  "horarios": [
    { "dia": "MONDAY", "horaInicio": "09:00", "horaFin": "10:00" },
    { "dia": "WEDNESDAY", "horaInicio": "09:00", "horaFin": "10:00" },
    { "dia": "FRIDAY", "horaInicio": "09:00", "horaFin": "10:00" }
  ]
}
→ 201: ResponseCursoDTO
```

### Editar
```
PUT /api/curso/edit/{id}
Body: { ... mismos campos que create ... }
→ 200: ResponseCursoDTO | 404
```

### Eliminar
```
DELETE /api/curso/{id} → 200
```

### Publicar
```
POST /api/curso/{id}/publicar → 200: ResponseCursoDTO | 400
```

### Cancelar
```
POST /api/curso/{id}/cancelar → 200: ResponseCursoDTO | 400
```

---

## 5. Inscripciones (`/api/inscripcion`)

### Inscribir a evento
```
POST /api/inscripcion/register
Body: { "nombre": "...", "apellidos": "...", "edad": 25, "idTutor": 3, "idEvento": 1 }
→ 201: Inscripcion | 400 | 409
```

### Inscripcion masiva a evento
```
POST /api/inscripcion/register-batch
Body: [ { "nombre": "...", "apellidos": "...", "edad": 25, "idTutor": 3, "idEvento": 1 }, ... ]
→ 201: [ InscripcionBatchResponseDTO, ... ] | 400
```

### Inscripcion masiva a curso
```
PUT /api/inscripcion/register-course
Body: { "idUsuario": 3, "idCurso": 1, "listaInscripcion": [ { "nombre": "...", "apellido": "...", "edad": 25 }, ... ] }
→ 200: String mensaje | 400
```

### Verificar estado inscripcion a evento
```
GET /api/inscripcion/check?idEvento=1&idUsuario=3
→ 200: SesionDTO
```

### Verificar estado inscripcion a curso
```
GET /api/inscripcion/check-course?idCurso=1&idUsuario=3
→ 200: InscripcionEstadoCursoResponseDTO
```

### Actualizar asistencia (batch)
```
PUT /api/inscripcion/attendance-batch
Body: { "idEvento": 1, "ids": [1, 2, 3] }  ← ids de inscripcion que ASISTIERON
→ 200: String mensaje | 400
```

---

## 6. Categorias (`/api/categoria`)

### Listar todas
```
GET /api/categoria/all → 200: [ Categoria, ... ]
```

### Obtener por ID
```
GET /api/categoria/{id} → 200: Categoria | 404
```

### Crear
```
POST /api/categoria/create
Body: { "nombre": "...", "descripcion": "..." }
→ 201: Categoria
```

### Editar
```
PUT /api/categoria/edit/{id}
Body: { "nombre": "...", "descripcion": "..." }
→ 200: Categoria | 404
```

### Eliminar
```
DELETE /api/categoria/{id} → 200
```

---

## 7. Encargados (`/api/encargados`)

### Listar todos
```
GET /api/encargados → 200: [ Usuario, ... ]
```

### Obtener por ID
```
GET /api/encargados/{id} → 200: ResponseUsuarioDTO | 404
```

### Registrar encargado
```
POST /api/encargados
Body: { "correo": "...", "contrasena": "...", "nombre": "...", "apellido": "...", "telefono": "..." }
→ 201: ResponseUsuarioDTO | 409
```

### Actualizar
```
PUT /api/encargados/{id}
Body: { "nombre": "...", "apellido": "...", "telefono": "...", "correo": "..." }
→ 200: ResponseUsuarioDTO | 400
```

### Eliminar
```
DELETE /api/encargados/{id} → 204 | 404 | 409
```

---

## 8. Estadisticas (`/api/estadisticas`)

### Estadisticas generales
```
GET /api/estadisticas/general?anio=2024
→ 200: EstadisticasResponseDTO
```

### Estadisticas de cursos
```
GET /api/estadisticas/cursos?anio=2024
→ 200: EstadisticasCursosDTO
```

---

## 9. Notificaciones (`/api/notificaciones`)

### Listar por usuario
```
GET /api/notificaciones/usuario/{idUsuario} → 200: [ Notificacion, ... ]
```

### Marcar como leida
```
PUT /api/notificaciones/{idNotificacion}/leer → 200
```

### Guardar token FCM
```
POST /api/notificaciones/token
Body: { "idUsuario": 3, "token": "fcm-token-..." }
→ 200
```

---

## 10. Storage (`/api/storage`)

### Subir archivo
```
POST /api/storage/upload
Content-Type: multipart/form-data
Body: file=@archivo.jpg
→ 200: "ruta/del/archivo.jpg" (String)
```

---

## 11. CSRF (`/api`)

### Obtener token CSRF
```
GET /api/csrf-token
→ 200: { "token": "uuid..." }
```
*Nota: Solo necesario para peticiones same-origin con metodos POST/PUT/DELETE/PATCH.*

---

## Modelos (DTOs de respuesta)

### ResponseUsuarioDTO
```json
{ "id": 1, "nombre": "Admin", "apellido": "Sistema", "correo": "admin@municipalidad.com", "rol": "ROLE_ADMIN", "telefono": "+56911111111", "informacion": null }
```
Roles: `ROLE_ADMIN` | `ROLE_ENCARGADO` | `ROLE_CLIENTE`

### ResponseRecintoDTO
```json
{ "idRecinto": 1, "nombre": "...", "ubicacion": "...", "descripcion": "...", "capacidad": 500, "coordenadasGPS": "...", "estado": "ACTIVO" }
```
Estados: `ACTIVO` | `INACTIVO` | `MANTENCION`

### ResponseEventoDTO
```json
{
  "idEvento": 1, "estado": "DISPONIBLE", "titulo": "...", "descripcion": "...",
  "horaInicio": "10:00:00", "horaFin": "12:00:00", "fechaInicio": "2025-01-15",
  "categoria": { "id": 1, "nombre": "...", "descripcion": "..." },
  "cupoMaximo": 100, "publicoObjetivo": "...", "cursoId": null, "cursoNombre": null,
  "inscritosActuales": 30,
  "recinto": { "idRecinto": 1, "nombre": "...", "ubicacion": "...", "descripcion": "...", "capacidad": 500, "coordenadasGPS": "...", "estado": "ACTIVO" },
  "encargado": { "id": 2, "nombre": "...", "apellido": "...", "correo": "...", "rol": "ROLE_ENCARGADO", "telefono": "...", "informacion": null },
  "encargadoId": 2, "categoriaId": 1, "maximoPorInscripcion": 5
}
```
Estados: `DISPONIBLE` | `TERMINADO` | `CANCELADO` | `EN_PROGRESO`

### ResponseCursoDTO
```json
{
  "idCurso": 1, "nombre": "...", "descripcion": "...",
  "fechaInicio": "2025-03-01", "fechaFin": "2025-06-30",
  "cupo": 20, "maximoPorInscripcion": 1,
  "recinto": { ... }, "encargado": { ... }, "categoria": { ... },
  "horarios": [ { "dia": "MONDAY", "horaInicio": "09:00", "horaFin": "10:00" }, ... ],
  "sesiones": [ ResponseEventoDTO, ... ],
  "cantidadSesiones": 0, "estado": "PUBLICADO"
}
```
Estados: `BORRADOR` | `PUBLICADO` | `EN_PROGRESO` | `FINALIZADO` | `CANCELADO`

### CupoEventoDTO
```json
{ "idEvento": 1, "cupoMaximo": 100, "inscritos": 30, "disponibles": 70 }
```

### SesionDTO
```json
{ "tituloEvento": "...", "fechaInicio": "2025-01-15", "horaInicio": "2025-01-15T10:00:00", "horaFin": "2025-01-15T12:00:00", "inscripciones": [ Inscripcion, ... ] }
```

### EventoConAsistentesDTO
```json
{ "idEvento": 1, "titulo": "...", "fechaInicio": "...", "horaInicio": "...", "horaFin": "...", "estado": "...", "ubicacionRecinto": "...", "imagenUrl": "...", "asistentes": [ AsistenteDTO, ... ], "categoria": { ... }, "recinto": { "titulo": "...", "imagenUrl": "..." } }
```

### HistorialUsuarioDTO
```json
{
  "cursos": [ { "nombre": "...", "participantes": [ { "idInscripcion": 1, "nombre": "...", "apellido": "...", "estadoAsistencia": "PENDIENTE" }, ... ] } ],
  "eventos": [ HistorialInscripcionDTO, ... ]
}
```

### InscripcionEstadoCursoResponseDTO
```json
{ "inscrito": true, "sesiones": [ SesionDTO, ... ] }
```
