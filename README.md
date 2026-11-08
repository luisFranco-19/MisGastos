# 💳 Mis Gastos — Android App

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android" />
  <img src="https://img.shields.io/badge/Kotlin-2.0-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin" />
  <img src="https://img.shields.io/badge/Jetpack%20Compose-UI-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Jetpack Compose" />
  <img src="https://img.shields.io/badge/Material%20Design-3-795548?style=for-the-badge" alt="Material 3" />
  <img src="https://img.shields.io/badge/Architecture-Clean%20%2B%20MVVM-009688?style=for-the-badge" alt="Clean Architecture" />
  <img src="https://img.shields.io/badge/Database-Room%20(Offline--first)-FF6F00?style=for-the-badge&logo=sqlite&logoColor=white" alt="Room" />
</p>

Aplicación nativa para Android diseñada para el registro, control y análisis de gastos personales. Desarrollada con un enfoque **Offline-First**, sincronización en segundo plano con **Google Sheets API**, interfaz moderna en **Jetpack Compose + Material 3** y un **Widget** para la pantalla de inicio con **Jetpack Glance**.

---

## 🌟 Características Principales

- 🏠 **Panel Principal (Dashboard)**: Resumen en tiempo real del gasto mensual, presupuesto disponible, balance y desglose de las últimas transacciones.
- ➕ **Registro Rápido y Flexible**: Captura de gastos en segundos con selección de categoría, método de pago, fecha personalizable y notas.
- 📊 **Estadísticas y Métricas Visuales**: Gráficos interactivos de pastel (desglose por categoría) y barras (evolución mensual y comparativas).
- 🔄 **Sincronización con Google Sheets**: Respaldo automático en la nube a través de la API oficial de Google Sheets sin depender de servidores de terceros.
- 📶 **Enfoque Offline-First**: Registra y consulta gastos sin conexión; los cambios se sincronizan en cuanto el dispositivo recupere conexión gracias a **WorkManager**.
- 📱 **Widget en Pantalla de Inicio**: Acceso rápido al resumen de gastos del mes y botón para registrar gastos al instante usando **Jetpack Glance**.
- 🎯 **Control de Presupuesto**: Establece límites mensuales y recibe alertas visuales progresivas al alcanzar el 80%, 90% y 100%.
- 🔔 **Recordatorios Diarios**: Notificaciones locales programadas para mantener tus registros al día.
- 🎨 **Diseño Moderno & Dinámico**: Construido 100% en Jetpack Compose, con soporte fluido para temas **Claro**, **Oscuro** y paleta **Material You**.

---

## 🛠️ Tecnologías y Librerías

| Componente | Tecnología | Propósito |
|------------|------------|-----------|
| **Lenguaje** | [Kotlin 2.0](https://kotlinlang.org/) | Lenguaje base con Corrutinas y Flow |
| **UI Toolkit** | [Jetpack Compose](https://developer.android.com/jetpack/compose) | Interfaz declarativa moderna |
| **Diseño** | [Material 3](https://m3.material.io/) | Componentes y estilos de última generación |
| **Persistencia Local** | [Room Database](https://developer.android.com/training/data-storage/room) | Base de datos SQLite local tipada y reactiva |
| **Preferencias** | [DataStore Preferences](https://developer.android.com/topic/libraries/architecture/datastore) | Almacenamiento clave-valor reactivo |
| **Segundo Plano** | [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager) | Sincronización diferida y tolerante a fallos |
| **Widget** | [Jetpack Glance](https://developer.android.com/jetpack/compose/glance) | Widgets de pantalla de inicio con Compose |
| **Nube / API** | [Google Sheets API v4](https://developers.google.com/sheets/api) & [OAuth 2.0](https://developers.google.com/identity) | Respaldo y sincronización en hojas de cálculo |
| **Redes** | [Retrofit](https://square.github.io/retrofit/) & [OkHttp](https://square.github.io/okhttp/) | Cliente HTTP para servicios externos |
| **Navegación** | [Navigation Compose](https://developer.android.com/jetpack/compose/navigation) | Flujo y rutas seguras entre pantallas |

---

## 🏛️ Arquitectura del Proyecto

El proyecto sigue los principios de **Clean Architecture** junto con el patrón de presentación **MVVM**:

```
app/src/main/java/com/misgastos/app/
├── data/                    # Capa de Datos
│   ├── local/               # Base de datos Room (Entities, DAOs, Migraciones) y DataStore
│   ├── remote/              # Servicios Google Auth y Sheets API v4
│   └── repository/          # Implementación de los repositorios
├── domain/                  # Capa de Dominio (Lógica de Negocio Pura)
│   ├── model/               # Modelos de negocio (Expense, Category, Budget, etc.)
│   ├── repository/          # Contratos e interfaces de repositorios
│   └── usecase/             # Casos de uso (RegisterExpense, GetStatistics, etc.)
├── presentation/            # Capa de Presentación
│   ├── components/          # Componentes reusables y gráficas personalizadas
│   ├── navigation/          # Rutas y barra de navegación inferior
│   ├── screens/             # Pantallas (Home, Register, Expenses, Statistics, Settings)
│   └── theme/               # Paleta de colores, tipografía y temas
├── di/                      # Contenedor de Inyección de Dependencias (AppContainer)
├── notification/            # Alarmas y receptores para recordatorios diarios
├── widget/                  # Implementación del Widget con Jetpack Glance
└── worker/                  # Workers para sincronización en segundo plano con WorkManager
```

---

## 🚀 Inicio Rápido y Compilación

### Requisitos Previos

- **Android Studio** Hedgehog (2023.1.1) o superior.
- **JDK 17**.
- **Android SDK** API 34 (mínimo soportado: API 26 / Android 8.0).

### Compilar desde la terminal

1. **Clonar el repositorio**:
   ```bash
   git clone https://github.com/luisFranco-19/MisGastos.git
   cd MisGastos
   ```

2. **Compilar el APK de depuración (Debug)**:
   ```bash
   ./gradlew assembleDebug
   ```
   *El archivo APK generado estará en:* `app/build/outputs/apk/debug/app-debug.apk`

3. **Ejecutar pruebas unitarias**:
   ```bash
   ./gradlew test
   ```

---

## 📑 Configuración de Google Sheets (Opcional)

Para habilitar la sincronización remota automática con Google Sheets:

1. Crea un proyecto en [Google Cloud Console](https://console.cloud.google.com/).
2. Habilita la **Google Sheets API**.
3. Configura la pantalla de consentimiento OAuth y agrega tu Client ID de Android con el SHA-1 de tu clave de firma.
4. Consulta la guía detallada paso a paso en [GOOGLE_SHEETS_SETUP.md](GOOGLE_SHEETS_SETUP.md).

---

## 📄 Licencia

Este proyecto es privado y de uso personal.
