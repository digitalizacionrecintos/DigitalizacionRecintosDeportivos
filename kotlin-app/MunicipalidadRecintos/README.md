# Municipalidad Recintos - App

Este proyecto es una aplicación multiplataforma para la gestión de recintos y eventos.

## 📖 Documentación Completa
Para una guía detallada sobre la arquitectura, instalación, cómo añadir nuevas funcionalidades y cambios recientes, consulta la **[Documentación del Proyecto](./DOCUMENTATION.md)**.

---

### Descripción General
Esta es una aplicación desarrollada con **Kotlin Multiplatform (KMP)** que apunta a Android e iOS shareando la lógica de negocio y la interfaz de usuario mediante **Compose Multiplatform**.

* [/composeApp](./composeApp/src) contiene el código compartido.
  - [commonMain](./composeApp/src/commonMain/kotlin) código común para todas las plataformas.
  - [androidMain](./composeApp/src/androidMain/kotlin) y [iosMain](./composeApp/src/iosMain/kotlin) para implementaciones específicas.

* [/iosApp](./iosApp) contiene el proyecto nativo de iOS que actúa como entry point.

### Ejecución
- **Android**: `./gradlew.bat :composeApp:assembleDebug` (Windows) o `./gradlew :composeApp:assembleDebug` (Unix).
- **iOS**: Abrir `/iosApp` en Xcode.
