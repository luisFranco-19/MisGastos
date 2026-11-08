# Configuración de Google Sheets para Mis Gastos

Esta guía explica paso a paso cómo configurar Google Sheets API para que la aplicación pueda sincronizar los gastos automáticamente.

## 1. Crear proyecto en Google Cloud

1. Ir a [Google Cloud Console](https://console.cloud.google.com/)
2. Hacer clic en **Crear proyecto**
3. Nombre del proyecto: `Mis Gastos`
4. Hacer clic en **Crear**

## 2. Habilitar Google Sheets API

1. En el menú lateral: **APIs y servicios** → **Biblioteca**
2. Buscar `Google Sheets API`
3. Hacer clic en **Google Sheets API**
4. Hacer clic en **Habilitar**

## 3. Configurar pantalla de consentimiento OAuth

1. En el menú lateral: **APIs y servicios** → **Pantalla de consentimiento OAuth**
2. Seleccionar **Externo** (o **Interno** si usas Google Workspace)
3. Hacer clic en **Crear**
4. Completar los datos:
   - Nombre de la app: `Mis Gastos`
   - Correo electrónico de soporte: tu correo
   - Logotipo (opcional)
5. En **Alcances (Scopes)**:
   - Agregar: `https://www.googleapis.com/auth/spreadsheets`
6. En **Usuarios de prueba** (para apps en modo testing):
   - Agregar tu correo electrónico de Gmail
7. Guardar

## 4. Crear credenciales Android (Client ID)

1. En el menú lateral: **APIs y servicios** → **Credenciales**
2. Hacer clic en **Crear credenciales** → **ID de cliente de OAuth**
3. Tipo de aplicación: **Android**
4. Nombre: `Mis Gastos Android`
5. **Nombre del paquete**: `com.misgastos.app`
6. **Huella digital del certificado SHA-1**: ver sección siguiente
7. Hacer clic en **Crear**

## 5. Obtener SHA-1 del certificado

### Debug (para desarrollo)

```bash
# Windows
keytool -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android

# macOS/Linux
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

### Desde Android Studio

1. Abrir Android Studio
2. En el panel lateral derecho: **Gradle** → **app** → **Tasks** → **android** → **signingReport**
3. Copiar el valor **SHA1**

### Release (para producción)

```bash
keytool -list -v -keystore tu-keystore.jks -alias tu-alias
```

## 6. Configurar SHA-1 en Google Cloud

1. Copiar la huella SHA-1 obtenida
2. Pegarla en el campo **Huella digital del certificado SHA-1** al crear la credencial
3. Guardar

> **IMPORTANTE**: Necesitas agregar TANTO el SHA-1 de debug COMO el de release si vas a usar ambos.

## 7. Verificar configuración

En la página de credenciales deberías ver:

| Campo | Valor |
|-------|-------|
| Tipo | Android |
| Paquete | com.misgastos.app |
| SHA-1 | Tu huella digital |

## 8. Usar la app

1. Abrir **Mis Gastos**
2. Ir a **Ajustes** → **Cuenta Google** → **Conectar con Google**
3. Seleccionar tu cuenta de Google
4. Conceder permisos para Google Sheets
5. La app creará automáticamente un Spreadsheet llamado "Mis Gastos"
6. Los gastos se sincronizarán automáticamente

## 9. Estructura del Spreadsheet

La aplicación crea una hoja con las siguientes columnas:

| Columna | Descripción |
|---------|-------------|
| A | ID del gasto |
| B | Fecha (dd/MM/yyyy) |
| C | Monto |
| D | Método de pago |
| E | Categoría |
| F | Descripción |
| G | Fecha de creación (dd/MM/yyyy HH:mm) |

## 10. Notas importantes

- **NO** colocar el Client ID en el código fuente
- **NO** compartir el archivo `debug.keystore`
- Las credenciales de OAuth están vinculadas al SHA-1 y al package name
- Si cambias el package name o el keystore, debes actualizar las credenciales
- Para producción, necesitas crear un keystore de release y agregar su SHA-1

## Solución de problemas

### Error 403: access_denied
- Verificar que tu correo está en la lista de **Usuarios de prueba** en la pantalla de consentimiento
- Esperar unos minutos después de configurar

### Error: DEVELOPER_ERROR
- El SHA-1 configurado en Google Cloud no coincide con el certificado de la app
- Verificar que el nombre del paquete es exactamente `com.misgastos.app`

### Error: Sign-in failed
- Asegurarse de que Google Sheets API está habilitada
- Verificar que las credenciales son de tipo **Android**
- Comprobar que el SHA-1 es correcto

### No se crea el Spreadsheet
- Verificar conexión a Internet
- Revisar que los permisos de Google Sheets fueron concedidos
- Revisar logs en Logcat (filtrar por `GoogleSheetsService`)
