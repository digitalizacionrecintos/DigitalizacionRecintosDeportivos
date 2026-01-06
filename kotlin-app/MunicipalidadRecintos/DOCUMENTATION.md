# Documentación del Proyecto: Municipalidad Recintos

Esta aplicación es una plataforma multiplataforma (Android e iOS) desarrollada con **Kotlin Multiplatform (KMP)** y **Compose Multiplatform**, diseñada para la gestión de recintos y eventos municipales. Permite a los usuarios ver y registrarse en eventos, y a los administradores (Managers) gestionar la asistencia y el estado de los mismos.

---

## 1. Arquitectura del Proyecto

El proyecto sigue los principios de **Clean Architecture**, dividiendo la lógica en tres capas principales dentro de `composeApp/src/commonMain/kotlin/org/example/project`:

### Estructura Detallada de Directorios

```text
composeApp/src/commonMain/kotlin/org/example/project/
├── data/
│   ├── remote/          # Cliente Ktor y definición de ApiService.
│   │   └── dto/         # Objetos de datos para comunicación JSON.
│   └── repository/      # (Opcional) Implementaciones de repositorios.
├── domain/
│   ├── manager/         # Gestores de estado global (Session, Config, Theme).
│   ├── model/           # Modelos de negocio (Evento, Recinto, User).
│   ├── usecase/         # Lógica de negocio reutilizable.
│   └── util/            # Helpers de fechas y tipos.
└── presentation/
    ├── components/      # Bloques de construcción visual (Botones, Inputs).
    ├── features/        # Pantallas organizadas por funcionalidad.
    │   ├── auth/        # Login y Registro.
    │   ├── events/      # Listado y detalle de eventos para clientes.
    │   ├── manager/     # Gestión de eventos y asistencia para encargados.
    │   └── profile/     # Perfil de usuario y configuración de servidor.
    ├── tabs/            # Definición de las pestañas de navegación inferior.
    ├── theme/           # Definición del sistema de diseño (Colores, Tipos).
    └── MainScreen.kt    # Punto de entrada de la UI con navegación por roles.
```

### Capa de Presentación (`presentation`)
Contiene la interfaz de usuario y la lógica de vista.
- **Features**: Organizado por módulos funcionales (Auth, Events, Manager, Profile). Cada feature suele tener su `Screen` (Voyager), `ViewModel` y a veces un `Contract` para definir el estado y los eventos.
- **Components**: Componentes de UI reutilizables (Botones personalizados, Campos de texto, Shimmer, Modales).
- **Tabs**: Define la navegación principal de la app mediante una barra inferior.
- **Theme**: Configuración de colores, tipografía y tema global.

### Capa de Dominio (`domain`)
Contiene las reglas de negocio y modelos de datos puros.
- **Model**: Definiciones de datos como `Recinto`, `Evento`, `UserRole`.
- **Manager**: Singletons que gestionan el estado global (Sesión de usuario, Configuración del servidor, Temas, Preferencias locales).
- **Util**: Funciones de ayuda (Manejo de fechas, validaciones).

### Capa de Datos (`data`)
Se encarga de la persistencia y la comunicación externa.
- **Remote**: Implementación de `ApiService` utilizando **Ktor**. Define los endpoints y la configuración del cliente HTTP.
- **DTO**: Objetos de Transferencia de Datos para la serialización JSON.

---

## 2. Tecnologías Utilizadas

- **Lenguaje**: Kotlin 2.1.0
- **UI Framework**: Compose Multiplatform
- **Navegación**: [Voyager](https://voyager.adriel.cafe/)
- **Networking**: Ktor Client (con ContentNegotiation y JSON)
- **Serialización**: Kotlinx Serialization
- **Persistencia Local**: Multiplatform Settings
- **Inyección de Dependencias**: Gestión manual mediante Singletons (Managers) y ViewModels por pantalla.

---

## 3. Instalación y Configuración

### Requisitos Previos
- **JDK 17** o superior.
- **Android Studio** (Koala o más reciente) o **IntelliJ IDEA**.
- **Xcode** (solo para compilar iOS en macOS).
- **Gradle**: El proyecto utiliza el Gradle Wrapper incluido.

### Pasos para Ejecutar
1. **Clonar el repositorio**.
2. **Abrir el proyecto** en Android Studio/IntelliJ.
3. **Configurar el Backend**:
   - Al iniciar la app por primera vez, verás la pantalla de **Configuración del Servidor**.
   - Ingresa la dirección IP y el puerto donde se encuentra corriendo el backend.
   - Estos datos se guardan localmente para futuras sesiones.
4. **Android**: Selecciona la configuración `composeApp` y presiona *Run*.
5. **iOS**: Abre el archivo `iosApp/iosApp.xcworkspace` en Xcode o usa la configuración de ejecución en Android Studio (si tienes el plugin KMP instalado).

---

## 4. Guía de Modificaciones y Extensibilidad

### Cómo añadir una nueva Pantalla
1. Crea una clase que herede de `Screen` (de Voyager) en el paquete `presentation`.
2. Implementa la función `@Composable override fun Content()`.
3. Si necesitas lógica compleja, crea un `ViewModel` que herede de `androidx.lifecycle.ViewModel`.
4. Para navegar a ella: `navigator.push(NuevaPantalla())`.

### Cómo añadir un nuevo Endpoint API
1. Define el nuevo método en `ApiService.kt`.
2. Si el endpoint requiere datos, crea un `DTO` en `data/remote/dto`.
3. Implementa la llamada utilizando `client.get`, `client.post`, etc.
   ```kotlin
   suspend fun getDatosNuevos(): List<NuevoDTO> = client.get("api/endpoint").body()
   ```

### Cómo modificar el Estilo Global
1. Los colores principales se encuentran en `presentation/theme/Theme.kt`.
2. La tipografía se define en `presentation/theme/Type.kt`.
3. Muchos componentes personalizados usan parámetros para `Color` o `TextStyle`, permitiendo ajustes rápidos sin cambiar la implementación interna.

---

## 5. Gestión de Sesión y Roles

La aplicación utiliza `SessionManager` para controlar el estado del usuario.
- **Roles**: Existen dos roles principales (`CLIENT` y `MANAGER`).
- **Navegación Dinámica**: En `MainScreen.kt`, la barra de navegación inferior cambia según el rol del usuario conectado, mostrando herramientas de gestión a los administradores y la lista de eventos a los clientes.

---

## 6. Mantenimiento del Código

Recientemente se ha realizado una limpieza profunda para eliminar:
- Comentarios redundantes.
- Emojis en el código fuente.
- Líneas vacías sobrantes.

Esto asegura que el código sea profesional, esté listo para producción y cumpla con estándares de limpieza técnica.
