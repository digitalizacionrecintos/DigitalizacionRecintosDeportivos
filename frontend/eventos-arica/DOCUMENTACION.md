# Documentación del Proyecto: Eventos Arica

## 1. Introducción
El proyecto **Eventos Arica** es una aplicación web desarrollada para la **Municipalidad de Arica**. Su objetivo principal es la gestión integral de eventos locales, permitiendo a los administradores y encargados coordinar espacios (recintos), categorías de eventos, personal a cargo y visualizar estadísticas en tiempo real sobre la participación y asistencia.

## 2. Arquitectura y Tecnologías
La aplicación está construida siguiendo las mejores prácticas modernas de desarrollo frontend:

*   **Framework**: [Angular](https://angular.io/) (Versión 21^).
*   **Estilos**: [Tailwind CSS](https://tailwindcss.com/) para un diseño responsivo, moderno y altamente personalizable.
*   **Gestión de Estado y RxJS**: Uso extensivo de Observables para operaciones asíncronas.
*   **Gráficos**: [NGX-Charts](https://swimlane.github.io/ngx-charts/) para la visualización de datos estadísticos.
*   **Comunicación en Tiempo Real**: WebSockets mediante `STOMP` y `SockJS` para actualizaciones instantáneas.
*   **Backend**: API REST consumida desde `http://proyectos4-daniel.ddns.net:8080/api`.

---

## 3. Características Principales
*   **Autenticación Segura**: Sistema de login con protección de rutas mediante `Guards`.
*   **Panel de Eventos**: Visualización, filtrado y búsqueda de eventos.
*   **Gestión CRUD**:
    *   **Eventos**: Creación, edición y eliminación de eventos masivos.
    *   **Recintos**: Administración de lugares físicos (estadios, parques, salones).
    *   **Categorías**: Clasificación de eventos (Cultutra, Deportes, etc).
    *   **Encargados**: Registro y asignación de personal responsable.
*   **Estadísticas Avanzadas**: Gráficos interactivos de participación por evento y categoría.
*   **Soporte Multimedia**: Manejo de imágenes y banners para cada evento.

---

## 4. Guía de Instalación desde Cero

### Requisitos Previos
Antes de comenzar, asegúrate de tener instalado:
1.  **Node.js**: Versión 18 o superior (Se recomienda la LTS más reciente).
2.  **NPM**: Gestor de paquetes (incluido con Node.js).
3.  **Angular CLI**: Opcional, pero recomendado (`npm install -g @angular/cli`).

### Pasos de Instalación

1.  **Clonar el repositorio**:
    ```bash
    git clone https://github.com/DanielA091/Proyectos4-Frontend.git
    cd eventos-arica
    ```

2.  **Instalar dependencias**:
    Utiliza npm para descargar todas las librerías necesarias:
    ```bash
    npm install
    ```

3.  **Configuración del Entorno**:
    Verifica los archivos de entorno en `src/environments/`.
    *   `environment.ts` (Desarrollo)
    *   `environment.prod.ts` (Producción)
    
    Asegúrate de que la `apiUrl` apunte al servidor correcto.

4.  **Configurar Tailwind CSS** (Ya incluido en el proyecto):
    Si necesitas regenerar los estilos o realizar cambios en la configuración:
    *   El archivo de configuración principal es `tailwind.config.js`.
    *   Los estilos base se encuentran en `src/styles.css`.

---

## 5. Ejecución del Proyecto

### Desarrollo
Para iniciar un servidor de desarrollo local:
```bash
npm start
# O bien
ng serve
```
La aplicación estará disponible en `http://localhost:4200/`. El servidor se recargará automáticamente si cambias algún archivo.

### Producción (Build)
Para compilar la aplicación para despliegue:
```bash
npm run build
```
Los archivos compilados se generarán en la carpeta `dist/eventos-arica/`.

---

## 6. Estructura de Carpetas
```text
src/
├── app/
│   ├── components/     # Componentes visuales (Login, Eventos, Sidebar, etc)
│   ├── services/       # Lógica de comunicación con la API y WebSockets
│   ├── models/         # Interfaces y modelos de datos (DTOs)
│   ├── guards/         # Protección de rutas (Auth Guard)
│   ├── app.routes.ts   # Configuración de navegación
│   └── app.config.ts   # Configuración global de Angular
├── assets/             # Imágenes, iconos y fuentes estáticas
├── environments/       # Archivos de configuración de API y modo producción
└── styles.css          # Estilos globales y directivas de Tailwind
```

---

## 7. Mantenimiento y Limpieza
Este proyecto sigue una política de código limpio:
*   **Sin Comentarios**: Se ha eliminado el ruido visual de comentarios innecesarios para favorecer el "Self-Documenting Code".
*   **Sin Emojis**: Se han removido emojis de la lógica y consola para mantener un estándar profesional.
*   **Componentes Standalone**: Utiliza la arquitectura moderna de Angular sin la necesidad de `AppModule`.

---

### Contacto y Soporte
Desarrollado para la Municipalidad de Arica. Para dudas técnicas, contactar con el equipo de desarrollo de Proyectos 4.
