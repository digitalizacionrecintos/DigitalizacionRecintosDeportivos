# Documentación del Proyecto: Recintos-Backend

## 1. Introducción
**Recintos-Backend** es una aplicación robusta desarrollada con **Spring Boot** diseñada para la gestión de recintos municipales, coordinación de eventos deportivos/culturales, manejo de inscripciones y envío de notificaciones push. El sistema permite administrar de manera eficiente la infraestructura pública y la participación ciudadana.

---

## 2. Stack Tecnológico
*   **Lenguaje:** Java 17
*   **Framework:** Spring Boot 3.5.7
*   **Base de Datos:** PostgreSQL 15
*   **Seguridad:** Spring Security + BCrypt
*   **Gestión de Dependencias:** Gradle
*   **Contenedores:** Docker & Docker Compose
*   **Mapeo de Datos:** MapStruct
*   **Simplificación de Código:** Lombok
*   **Notificaciones:** Firebase Admin SDK

---

## 3. Requisitos Previos
*   **JDK 17** o superior.
*   **Docker** y **Docker Compose** (recomendado para base de datos y despliegue).
*   **Gradle** (opcional, se incluye `gradlew`).
*   Archivo `firebase-service-account.json` en `src/main/resources/` (para notificaciones push).

---

## 4. Instalación y Configuración

### 4.1. Configuración de Base de Datos
El proyecto utiliza PostgreSQL. La forma más rápida de iniciar es mediante Docker:

```bash
docker-compose up -d db
```

Esto iniciará una instancia de PostgreSQL con las siguientes credenciales (definidas en `docker-compose.yml`):
*   **User:** rambi
*   **Password:** 1234
*   **Database:** recintos_db
*   **Port:** 5432

### 4.2. Ejecución Local (Modo Desarrollo)
Si tienes el JDK instalado, puedes ejecutar la aplicación directamente:

```bash
./gradlew bootRun
```

La aplicación estará disponible en `http://localhost:8080`.

### 4.3. Despliegue con Docker
Para desplegar todo el stack (App + DB):

```bash
docker-compose up --build
```

---

## 5. Estructura de la Aplicación (Arquitectura)
El proyecto sigue una arquitectura por capas estándar de Spring Boot:

1.  **`controller`**: Define los endpoints REST. Manejan la entrada/salida mediante DTOs.
2.  **`service`**: Contiene la lógica de negocio. Se divide en interfaces y clases `impl`.
3.  **`repository`**: Interfaces que extienden de `JpaRepository` para el acceso a datos.
4.  **`model`**: Entidades JPA que representan las tablas en la base de datos.
5.  **`dto`**: Objetos de transferencia de datos para desacoplar la API del modelo interno.
6.  **`mappers`**: Utiliza MapStruct para convertir entre Entidades y DTOs eficientemente.
7.  **`config`**: Configuraciones de Seguridad, Firebase, Web y WebSocket.

---

## 6. Módulos Principales

### 6.1. Gestión de Eventos (`EventoController`)
*   Permite crear, editar, listar y eliminar eventos.
*   Asignación de categorías y recintos.
*   Control de estados: `DISPONIBLE`, `TERMINADO`, `TRANSCURRIENDO`.

### 6.2. Recintos (`RecintoController`)
*   Administración de gimnasios, piscinas, estadios, etc.
*   Almacenamiento de ubicación, capacidad y coordenadas GPS.

### 6.3. Usuarios y Roles (`UsuarioController`)
*   **Roles:** `ADMIN`, `ENCARGADO`, `CLIENTE`.
*   Registro y login de usuarios con contraseñas encriptadas.

### 6.4. Inscripciones y Asistencia (`InscripcionController`)
*   Manejo de cupos para eventos.
*   Registro de asistencia (Presente/Ausente).
*   Prevención de duplicados mediante excepciones personalizadas.

### 6.5. Notificaciones (`NotificacionController`)
*   Envío de notificaciones personalizadas mediante Firebase.
*   Historial de notificaciones por usuario.

---

## 7. Seguridad y Acceso
La seguridad está configurada en `SecurityConfig.java`. Actualmente, el sistema permite acceso PermitAll a la mayoría de los endpoints de la API para facilitar la integración con el frontend, pero incluye la base para implementar seguridad por roles.

*   **CORS:** Configurado para permitir peticiones desde cualquier origen (ajustar en producción).
*   **Passwords:** Encriptación mediante `BCryptPasswordEncoder`.

---

## 8. Almacenamiento de Archivos (`StorageService`)
El sistema gestiona la subida de imágenes para eventos y recintos.
*   Los archivos se guardan en la carpeta `/uploads` en la raíz del proyecto.
*   En Docker, esta carpeta está mapeada como un volumen persistente.

---

## 9. Extensibilidad y Modificación

### ¿Cómo agregar un nuevo módulo?
1.  **Crear la Entidad:** En `com.recintos.municipalidad.model`.
2.  **Crear el Repositorio:** En `com.recintos.municipalidad.repository`.
3.  **Crear DTOs y Mapper:** Si es necesario, añadir en `dto` y `mappers`.
4.  **Implementar el Servicio:** Definir la interfaz en `service` y su implementación en `impl`.
5.  **Crear el Controller:** Exponer los endpoints en `controller`.
6.  **Configurar Seguridad:** Si el nuevo endpoint requiere restricciones, añadir en `SecurityConfig`.

### Modificación de Tareas Programadas
El proyecto usa `@EnableScheduling`. Puedes añadir métodos con `@Scheduled` en cualquier componente de servicio para tareas de limpieza o actualizaciones automáticas de estado de eventos.

---

## 10. Datos de Prueba (Seeding)
La clase `DataSeeder.java` se encarga de poblar la base de datos automáticamente al inicio si está vacía. Crea:
*   Usuarios de prueba (Admin, Encargado, Cliente).
*   8 recintos principales en Arica.
*   Categorías deportivas.
*   100 eventos de prueba distribuidos entre 2021 y 2024.

---

## 11. Contacto y Mantenimiento
Para modificaciones extensas, asegurarse de mantener la integridad de los mappers y la coherencia de los DTOs para no romper la compatibilidad con el frontend.
