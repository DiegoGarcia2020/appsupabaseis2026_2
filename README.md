# Práctica 3 - Lista de Alumnos con Supabase

Aplicación Android desarrollada en **Kotlin** que se conecta a una base de datos en **Supabase** y muestra la **lista de alumnos** registrados, ordenados alfabéticamente. La pantalla conserva la identidad de la **UTEQ** y está diseñada con `ConstraintLayout` y componentes de **Material Design**.
<div align="center">
  <img src="Screenshot_20261120_212824.png" alt="Pantalla de bienvenida" width="300"/>
</div>
## Pantallas

### 1. Lista de alumnos (`MainActivity`)

Archivo de diseño: `activity_main.xml`

| Elemento | Componente | Descripción |
|---|---|---|
| Logo de la universidad | `ImageView` | Imagen `logouteq` en la parte superior |
| Título | `TextView` | "Lista de alumnos" en verde (`#4CAF50`), negrita y 24sp |
| Lista de alumnos | `EditText` | Campo multilínea que muestra nombre, paralelo, correo y teléfono de cada alumno |
| Mensaje de error | `MaterialAlertDialog` | Diálogo "Error de conexión" con botón **Aceptar** cuando falla la consulta |

## Estructura del código

| Archivo | Descripción |
|---|---|
| `MainActivity.kt` | Consulta la tabla `alumnos` con una corrutina (`lifecycleScope`) y muestra el resultado |
| `data/SupabaseManager.kt` | Objeto único que crea el cliente de Supabase con el módulo `Postgrest` |
| `models/Alumno.kt` | `data class` marcada con `@Serializable` que representa una fila de la tabla |
| `utils/SupabaseErrorHandler.kt` | Muestra los errores de conexión en un diálogo de Material Design |

## Base de datos

Tabla `alumnos` en Supabase:

| Columna | Tipo | Descripción |
|---|---|---|
| `id` | `bigint` | Clave primaria |
| `nombres` | `text` | Nombre completo del alumno |
| `paralelo` | `text` | Paralelo al que pertenece |
| `correo` | `text` | Correo electrónico (opcional) |
| `telefono` | `text` | Número de teléfono (opcional) |
| `foto` | `text` | URL de la foto (opcional) |
| `created_at` | `timestamptz` | Fecha de registro |

## Detalles técnicos

- **Lenguaje:** Kotlin.
- **Layout principal:** `ConstraintLayout`, que posiciona cada elemento en relación con los demás y con los bordes de la pantalla.
- **Conexión a Supabase:** librería `supabase-kt` (BOM `3.6.0`) con el módulo `postgrest-kt` y el cliente `ktor-client-android`.
- **Consulta:** `from("alumnos").select { order("nombres", Order.ASCENDING) }.decodeList<Alumno>()`.
- **Serialización:** plugin `org.jetbrains.kotlin.plugin.serialization` aplicado en el módulo `app` para convertir el JSON en objetos `Alumno`.
- **Credenciales:** `SUPABASE_URL` y `SUPABASE_KEY` se leen desde `local.properties` y se exponen con `buildConfigField` en `BuildConfig`, así no quedan en el código ni en el repositorio.
- **Permisos:** `android.permission.INTERNET` declarado en el `AndroidManifest.xml`.
- **Pantalla de inicio:** se define en el `AndroidManifest.xml` mediante el `intent-filter` con `MAIN` y `LAUNCHER`.
- **Versiones:** AGP `9.4.0`, Gradle `9.6.0`, SDK mínimo y objetivo 37.
