# Mis Gastos — Aplicación Android de Control de Gastos

Aplicación Android nativa para registro y control de gastos personales con sincronización automática a Google Sheets, widget de pantalla de inicio, y funcionamiento offline-first.

## Capturas de pantalla

La aplicación incluye:
- 🏠 Pantalla principal con resumen mensual
- ➕ Registro rápido de gastos
- 📊 Estadísticas con gráficas
- 📋 Lista de gastos con búsqueda y filtros
- ⚙️ Ajustes completos
- 📱 Widget de pantalla de inicio

## Tecnologías

| Tecnología | Uso |
|-----------|-----|
| Kotlin 2.0 | Lenguaje principal |
| Jetpack Compose | UI declarativa |
| Material 3 | Diseño visual |
| Jetpack Navigation | Navegación entre pantallas |
| Room Database | Almacenamiento local |
| Kotlin Coroutines + Flow | Programación asíncrona |
| WorkManager | Sincronización en segundo plano |
| Jetpack Glance | Widget de Android |
| Google Sheets API v4 | Sincronización remota |
| Google OAuth 2.0 | Autenticación |
| Retrofit + OkHttp | Comunicación HTTP |
| Gson | Serialización JSON |
| DataStore | Preferencias de usuario |
| Gradle Kotlin DSL | Sistema de compilación |

## Arquitectura

**Clean Architecture + MVVM**

```
app/
├── data/
│   ├── local/           # Room DB, DAOs, Entities, DataStore
│   ├── remote/google/   # Google Auth, Sheets API
│   └── repository/      # Implementaciones de repositorios
├── di/                  # AppContainer (DI manual)
├── domain/
│   ├── model/           # Modelos de dominio
│   ├── repository/      # Interfaces de repositorios
│   └── usecase/         # Casos de uso
├── presentation/
│   ├── components/      # Componentes compartidos + gráficas
│   ├── navigation/      # NavGraph, Screen, BottomNavBar
│   ├── screens/         # home, register, expenses, statistics, settings
│   └── theme/           # Color, Type, Theme
├── widget/              # Jetpack Glance widget
├── worker/              # ExpenseSyncWorker, SyncManager
├── MainActivity.kt
└── MisGastosApplication.kt
```

## Flujo principal

```
Widget/App → RegisterExpenseScreen → ViewModel → UseCase → Repository → Room
                                                                         ↓
                                                                    UI actualizada
                                                                         ↓
                                                              WorkManager → Google Sheets
```

## Instalación

### Requisitos

- Android Studio Hedgehog (2023.1.1) o superior
- JDK 17
- Android SDK 34
- Dispositivo/emulador Android API 26+

### Pasos

1. Clonar o descargar el proyecto
2. Abrir en Android Studio
3. Esperar a que Gradle sincronice las dependencias
4. Ejecutar la aplicación en un dispositivo o emulador

### Generar Gradle Wrapper (si es necesario)

Si el proyecto no incluye el archivo `gradle-wrapper.jar`, Android Studio lo generará automáticamente al abrir el proyecto. Alternativamente:

```bash
gradle wrapper --gradle-version 8.7
```

## Configuración de Google Sheets

Consultar [GOOGLE_SHEETS_SETUP.md](GOOGLE_SHEETS_SETUP.md) para la guía completa.

### Resumen rápido

1. Crear proyecto en Google Cloud Console
2. Habilitar Google Sheets API
3. Configurar pantalla de consentimiento OAuth
4. Crear credenciales Android (Client ID)
5. Configurar SHA-1 del certificado de firma
6. Iniciar sesión en la app con la cuenta de Google

## Widget

El widget de pantalla de inicio permite:
- Ver el total gastado del mes
- Registrar gastos con un toque

Para agregar: mantener presionado el escritorio → Widgets → Mis Gastos

## Sincronización

- **Offline-first**: Los gastos se guardan inmediatamente en Room
- **Automática**: WorkManager sincroniza cuando hay conexión
- **Backoff exponencial**: Reintentos automáticos en caso de error
- **Estados**: PENDING → SYNCED o ERROR

## Presupuesto

Configurar en Ajustes → Presupuesto → Presupuesto mensual

Alertas visuales al alcanzar 80%, 90% y 100% del presupuesto.

## Compilar APK Debug

```bash
./gradlew assembleDebug
```

El APK se genera en: `app/build/outputs/apk/debug/app-debug.apk`

## Compilar APK Release

```bash
./gradlew assembleRelease
```

Requiere configurar signing en `app/build.gradle.kts`.

## Ejecutar Tests

```bash
./gradlew test                     # Unit tests
./gradlew connectedAndroidTest     # Instrumented tests
```

## Solución de problemas

### Error de Gradle Wrapper
Si aparece error sobre `gradle-wrapper.jar`, abrir en Android Studio que lo generará automáticamente.

### Error de Google Sign-In
Verificar que el SHA-1 del certificado está configurado en Google Cloud Console.

### Error de sincronización
Verificar conexión a Internet y que la cuenta Google tiene acceso al Spreadsheet.

### Base de datos corrupta
Eliminar datos de la app en Ajustes Android → Apps → Mis Gastos → Almacenamiento → Borrar datos.

## Licencia

Este proyecto es privado y de uso personal.
