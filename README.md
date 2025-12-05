# AppRoom - Registro de Compras

Aplicación Android para registrar y visualizar compras diarias utilizando **Room Database** y **Jetpack Compose**.

## Descripción

AppRoom es una aplicación simple y funcional que permite a los usuarios:
- Registrar compras con información del comprador, productos y precio
- Visualizar una lista desplegable de todas las compras registradas
- Persistencia de datos local usando Room Database
- Generación automática de fecha y hora (formato 24h)
- Notificaciones periódicas cada 15 minutos con WorkManager

## Características

### Pantalla Principal
- **Lista desplegable** de compras ordenadas por más recientes
- Cada item muestra "Compra #ID" con flecha para expandir/contraer
- Al expandir se muestra:
  - Comprador
  - Productos
  - Precio Total (S/.)
  - Fecha (HH:mm en formato 24 horas)
- **Botón flotante (+)** para agregar nueva compra
- Mensaje cuando no hay compras registradas

### Pantalla de Formulario
- Campo de texto: **Comprador**
- Campo de texto: **Productos**
- Campo numérico: **Precio Total (S/.)**
- Generación automática de fecha al guardar
- Validación de campos requeridos
- Navegación automática a lista después de guardar

### Sistema de Notificaciones ⏰
- **WorkManager** para notificaciones en segundo plano
- Notificaciones periódicas cada **15 minutos** (intervalo mínimo permitido)
- Mensaje: **"Tienes un nuevo pedido"**
- Canal de notificación: **"Notificaciones de Compras"**
- Solicitud automática de permisos en Android 13+ (Tiramisu)
- Persistencia incluso si la app se cierra
- Optimización de batería mediante WorkManager

## Arquitectura

El proyecto sigue las mejores prácticas de Android con:

### Estructura de Carpetas
```
app/src/main/java/com/dbApp/approom/
├── data/
│   ├── Compra.kt              # Entidad Room
│   ├── CompraDao.kt            # Data Access Object
│   ├── CompraDatabase.kt       # Base de datos Room
│   └── CompraRepository.kt     # Repositorio de datos
├── screens/
│   └── FormularioScreen.kt     # Pantalla de formulario
├── ui/theme/
│   ├── Color.kt
│   ├── Theme.kt
│   └── Type.kt
├── MainActivity.kt             # Pantalla principal + navegación
├── CompraViewModel.kt          # ViewModel para gestión de estado
└── NotificacionWorker.kt       # Worker para notificaciones periódicas
```

### Componentes Principales

#### 1. **Entidad Room - Compra**
```kotlin
@Entity(tableName = "compras")
data class Compra(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val comprador: String,
    val productos: String,
    val precioTotal: Double,
    val fecha: String
)
```

#### 2. **DAO - CompraDao**
- `getAllCompras()`: Obtiene todas las compras como Flow
- `insertCompra()`: Inserta nueva compra

#### 3. **ViewModel - CompraViewModel**
- Gestiona el estado de la lista de compras
- Expone `StateFlow<List<Compra>>`
- Maneja operaciones de inserción

#### 4. **Repository - CompraRepository**
- Abstrae el acceso a datos
- Facilita testing y mantenimiento

#### 5. **Worker - NotificacionWorker**
- Extiende de `Worker` de WorkManager
- Ejecuta tareas en segundo plano
- Crea canal de notificación para Android 8+
- Muestra notificaciones con título y texto personalizados
- Configuración:
  ```kotlin
  class NotificacionWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
      override fun doWork(): Result {
          mostrarNotificacion()
          return Result.success()
      }
  }
  ```

#### 6. **MainActivity**
- Gestiona permisos de notificación usando `ActivityResultContracts`
- Configura WorkManager al iniciar la app
- Intervalo de notificaciones: 15 minutos (mínimo permitido)
- Política: `ExistingPeriodicWorkPolicy.KEEP` (evita duplicados)

## 🛠️ Tecnologías y Librerías

| Tecnología | Versión | Uso |
|------------|---------|-----|
| **Kotlin** | 2.0.21 | Lenguaje principal |
| **Jetpack Compose** | 2024.09.00 | UI declarativa |
| **Room Database** | 2.6.1 | Persistencia local |
| **Navigation Compose** | 2.8.0 | Navegación entre pantallas |
| **WorkManager** | 2.9.0 | Notificaciones en segundo plano |
| **Material 3** | Latest | Diseño UI |
| **Coroutines** | Latest | Programación asíncrona |
| **StateFlow** | Latest | Manejo de estado reactivo |
| **KSP** | 2.0.21-1.0.28 | Procesamiento de anotaciones |

## Dependencias

### build.gradle.kts (Project)
```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}
```

### build.gradle.kts (App)
```kotlin
dependencies {
    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    
    // Navigation
    implementation(libs.androidx.navigation.compose)
    
    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    
    // Compose & Material3
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui)
    // ... otras dependencias
}
```

## Instalación y Configuración

### Requisitos Previos
- **Android Studio**: Hedgehog (2023.1.1) o superior
- **JDK**: 11 o superior
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 36

### Permisos Requeridos
La aplicación requiere el siguiente permiso declarado en `AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```
- **POST_NOTIFICATIONS**: Para mostrar notificaciones (Android 13+)
- La app solicita este permiso automáticamente al iniciar

### Pasos de Instalación

1. **Clonar o descargar el proyecto**
   ```bash
   git clone <url-del-repositorio>
   cd AppRoom
   ```

2. **Abrir en Android Studio**
   - File → Open → Seleccionar carpeta AppRoom

3. **Sincronizar Gradle**
   - Esperar a que aparezca la barra amarilla
   - Clic en **"Sync Now"**
   - O: File → Sync Project with Gradle Files

4. **Esperar descarga de dependencias**
   - Primera vez puede tomar 2-5 minutos
   - Verificar en Build Output

5. **Ejecutar la aplicación**
   - Conectar dispositivo o iniciar emulador
   - Run → Run 'app' (Shift + F10)

## Uso de la Aplicación

### Agregar una Compra
1. Presiona el botón flotante **+** en la esquina inferior derecha
2. Completa los campos:
   - **Comprador**: Nombre de quien realizó la compra
   - **Productos**: Lista de productos comprados
   - **Precio Total**: Monto en soles (S/.)
3. Presiona **"Guardar Compra"**
4. Automáticamente regresarás a la lista principal

### Visualizar Compras
1. En la pantalla principal verás todas las compras
2. Cada item muestra "Compra #"
3. Toca cualquier item para expandir/contraer detalles
4. Los datos se muestran con formato:
   - Comprador: [Nombre]
   - Productos: [Lista]
   - Precio Total: $XX.XX
   - Fecha: HH:mm

### Recibir Notificaciones 🔔
1. **Primera vez**: Al abrir la app, acepta el permiso de notificaciones
2. **Notificaciones automáticas**: Cada 15 minutos recibirás una notificación que dice:
   - Título: **"Nueva Compra"**
   - Mensaje: **"Tienes un nuevo pedido"**
3. **Persistencia**: Las notificaciones continúan incluso si cierras la app
4. **Desactivar**: Para detener las notificaciones, desinstala la app o desactiva las notificaciones en configuración del sistema

#### ¿Por qué 15 minutos?
WorkManager, la tecnología usada para notificaciones en segundo plano, tiene un **intervalo mínimo de 15 minutos** para tareas periódicas. Esto es una restricción de Android para optimizar el consumo de batería y rendimiento del dispositivo.

## Base de Datos

### Esquema
- **Nombre**: `compra_database`
- **Versión**: 1
- **Tabla**: `compras`

### Campos de la Tabla
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | INTEGER | Primary Key (autoincremental) |
| comprador | TEXT | Nombre del comprador |
| productos | TEXT | Lista de productos |
| precioTotal | REAL | Precio en formato double |
| fecha | TEXT | Hora en formato HH:mm |

## Diseño UI

- **Material Design 3**: Componentes modernos y accesibles
- **Theme**: Configuración por defecto de Material 3
- **Componentes principales**:
  - `Scaffold`: Estructura de pantalla
  - `TopAppBar`: Barra superior
  - `LazyColumn`: Lista eficiente
  - `Card`: Items de compra
  - `FloatingActionButton`: Botón agregar
  - `OutlinedTextField`: Campos de entrada
  - `AnimatedVisibility`: Animaciones suaves

## Notas Técnicas

### Patrones Utilizados
- **MVVM** (Model-View-ViewModel)
- **Repository Pattern**
- **Singleton Pattern** (Database)
- **Observer Pattern** (StateFlow)

### Programación Reactiva
- `Flow`: Para streams de datos
- `StateFlow`: Para estado observable
- `collectAsState()`: Integración Compose

### Manejo de Concurrencia
- `viewModelScope`: Scope del ViewModel
- `suspend functions`: Operaciones asíncronas
- `launch`: Lanzamiento de corrutinas

### WorkManager y Tareas en Segundo Plano
- **PeriodicWorkRequest**: Tareas periódicas cada 15 minutos
- **Worker**: Clase que ejecuta el trabajo en background
- **WorkManager**: Garantiza ejecución incluso con la app cerrada
- **ExistingPeriodicWorkPolicy.KEEP**: Evita duplicar tareas al reiniciar
- **NotificationChannel**: Canales de notificación para Android 8+
- **ActivityResultContracts**: Sistema moderno de permisos (reemplaza onRequestPermissionsResult deprecated)

### Gestión de Notificaciones
```kotlin
// Configuración en MainActivity
private fun iniciarNotificacionesPeriodicas() {
    val workRequest = PeriodicWorkRequestBuilder<NotificacionWorker>(
        15, TimeUnit.MINUTES
    ).build()
    
    WorkManager.getInstance(this).enqueueUniquePeriodicWork(
        "notificaciones_compras",
        ExistingPeriodicWorkPolicy.KEEP,
        workRequest
    )
}
```

### Solicitud de Permisos (Android 13+)
```kotlin
private val requestPermissionLauncher = registerForActivityResult(
    ActivityResultContracts.RequestPermission()
) { isGranted: Boolean ->
    if (isGranted) {
        iniciarNotificacionesPeriodicas()
    }
}
```

## 📱 Sistema de Notificaciones con WorkManager

### Descripción General
La aplicación implementa un sistema de notificaciones periódicas utilizando **WorkManager**, que es la solución recomendada por Google para tareas en segundo plano garantizadas y diferibles.

### Características del Sistema

#### 1. **Notificaciones Periódicas**
- **Frecuencia**: Cada 15 minutos (intervalo mínimo permitido por Android)
- **Mensaje**: "Tienes un nuevo pedido"
- **Persistencia**: Continúa funcionando incluso si la app se cierra
- **Optimización**: Respeta las políticas de ahorro de batería de Android

#### 2. **Componentes Implementados**

##### NotificacionWorker.kt
```kotlin
class NotificacionWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        mostrarNotificacion()
        return Result.success()
    }
    
    private fun mostrarNotificacion() {
        // Crea canal de notificación
        // Construye y muestra la notificación
    }
}
```

**Funcionalidades**:
- Crea canal de notificación para Android 8.0+ (Oreo)
- Construye notificación con `NotificationCompat.Builder`
- Usa icono del sistema: `ic_dialog_info`
- Configura auto-cancelación al tocar

##### MainActivity.kt - Configuración
```kotlin
// Registro de launcher para permisos
private val requestPermissionLauncher = registerForActivityResult(
    ActivityResultContracts.RequestPermission()
) { isGranted ->
    if (isGranted) iniciarNotificacionesPeriodicas()
}

// Configuración de WorkManager
private fun iniciarNotificacionesPeriodicas() {
    val workRequest = PeriodicWorkRequestBuilder<NotificacionWorker>(
        15, TimeUnit.MINUTES
    ).build()
    
    WorkManager.getInstance(this).enqueueUniquePeriodicWork(
        "notificaciones_compras",
        ExistingPeriodicWorkPolicy.KEEP,
        workRequest
    )
}
```

#### 3. **Flujo de Funcionamiento**

```
[App Inicia]
    ↓
[¿Android 13+?] → NO → [Inicia WorkManager]
    ↓ SÍ                       ↓
[¿Permiso concedido?]         ↓
    ↓ NO → [Solicita permiso] ↓
    ↓ SÍ                       ↓
[Inicia WorkManager] ←────────┘
    ↓
[Crea PeriodicWorkRequest]
    ↓
[Programa cada 15 minutos]
    ↓
[WorkManager ejecuta Worker]
    ↓
[Muestra notificación]
    ↓
[Espera 15 minutos] → [Repite]
```

#### 4. **Ventajas de WorkManager**

| Ventaja | Descripción |
|---------|-------------|
| **Garantizado** | La tarea se ejecutará incluso si la app se cierra o el dispositivo se reinicia |
| **Optimizado** | Respeta Doze Mode y App Standby de Android |
| **Flexible** | Permite configurar restricciones (batería, red, etc.) |
| **Compatible** | Funciona en todas las versiones de Android desde API 14+ |
| **Observable** | Permite monitorear el estado de las tareas |

#### 5. **Limitaciones Conocidas**

⚠️ **Intervalo Mínimo**: WorkManager no permite intervalos menores a 15 minutos para `PeriodicWorkRequest`. Esto es una restricción de Android para:
- Optimizar consumo de batería
- Reducir uso de recursos del sistema
- Cumplir políticas de background execution

**Alternativas para intervalos menores**:
- `Foreground Service` (requiere notificación permanente)
- `AlarmManager` (puede ser interrumpido por el sistema)
- Coroutines en Activity (solo funciona con app abierta)

#### 6. **Configuración de Canal de Notificación**

```kotlin
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    val channel = NotificationChannel(
        "compras_channel",
        "Notificaciones de Compras",
        NotificationManager.IMPORTANCE_DEFAULT
    )
    notificationManager.createNotificationChannel(channel)
}
```

**Propiedades del Canal**:
- **ID**: `compras_channel`
- **Nombre**: "Notificaciones de Compras"
- **Importancia**: DEFAULT (sonido y aparición en barra de estado)

#### 7. **Gestión de Permisos**

En Android 13 (API 33) y superior, se requiere permiso explícito del usuario para mostrar notificaciones.

**AndroidManifest.xml**:
```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

**Solicitud dinámica**: Se solicita automáticamente al iniciar la app usando `ActivityResultContracts.RequestPermission()`.

### Prueba del Sistema

1. **Instalar y abrir la app**
2. **Aceptar permiso de notificaciones** (si es Android 13+)
3. **Esperar 15 minutos** para la primera notificación
4. **Cerrar la app** - las notificaciones continúan
5. **Verificar** - cada 15 minutos debe aparecer la notificación

### Depuración

Para verificar que WorkManager está funcionando:

```kotlin
// En MainActivity, agregar logging
WorkManager.getInstance(this)
    .getWorkInfosForUniqueWorkLiveData("notificaciones_compras")
    .observe(this) { workInfos ->
        workInfos.forEach { workInfo ->
            Log.d("WorkManager", "Estado: ${workInfo.state}")
        }
    }
```

## Autor

Joshep Antony Ccahuana Larota

---

## 📋 Resumen Rápido

### Stack Tecnológico
- ✅ **Kotlin** + Jetpack Compose
- ✅ **Room Database** para persistencia
- ✅ **WorkManager** para notificaciones
- ✅ **Material Design 3**
- ✅ **MVVM Architecture**
- ✅ **Coroutines** + StateFlow

### Funcionalidades Principales
1. 📝 **Registro de compras** con formulario simple
2. 📋 **Lista expandible** de compras guardadas
3. 🔔 **Notificaciones automáticas** cada 15 minutos
4. 💾 **Persistencia local** con Room
5. 🕐 **Fecha/hora automática** en formato 24h

### Datos Técnicos
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 36
- **Intervalo de notificaciones**: 15 minutos
- **Base de datos**: SQLite via Room
- **Navegación**: Navigation Compose

### Comandos Útiles
```bash
# Compilar
./gradlew assembleDebug

# Limpiar build
./gradlew clean

# Ejecutar en dispositivo
./gradlew installDebug
```

---

**Proyecto desarrollado con fines educativos - IDNP 2025**

