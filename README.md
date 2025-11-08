# AppRoom - Registro de Compras

Aplicación Android para registrar y visualizar compras diarias utilizando **Room Database** y **Jetpack Compose**.

## Descripción

AppRoom es una aplicación simple y funcional que permite a los usuarios:
- Registrar compras con información del comprador, productos y precio
- Visualizar una lista desplegable de todas las compras registradas
- Persistencia de datos local usando Room Database
- Generación automática de fecha y hora (formato 24h)

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
└── CompraViewModel.kt          # ViewModel para gestión de estado
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

## 🛠️ Tecnologías y Librerías

| Tecnología | Versión | Uso |
|------------|---------|-----|
| **Kotlin** | 2.0.21 | Lenguaje principal |
| **Jetpack Compose** | 2024.09.00 | UI declarativa |
| **Room Database** | 2.6.1 | Persistencia local |
| **Navigation Compose** | 2.8.0 | Navegación entre pantallas |
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

## Autor

Joshep Antony Ccahuana Larota
