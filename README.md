# ContactsApp - Aplicación de Contactos Moderna

<img src="https://github.com/jcuevashub/ClaroDO/raw/main/Screenshot_20250924_145741.png" 
     alt="Captura" 
     width="400"/>

Una aplicación Android nativa desarrollada en Kotlin con Jetpack Compose que permite gestionar contactos con una interfaz moderna y funcionalidades avanzadas.

## 🚀 Características Principales

### ✨ Gestión de Contactos
- **Crear contactos** con nombre, apellido, teléfono e imagen
- **Listar contactos** con diseño moderno y fluido
- **Buscar contactos** con filtrado dinámico en tiempo real
- **Eliminar contactos** con deslizamiento o selección múltiple
- **Sincronización** con servidor remoto
- **Navegación alfabética** con índice lateral interactivo

### 🎨 Diseño y UX/UI Moderno
- **Material Design 3** con theming dinámico
- **Animaciones fluidas** y transiciones suaves
- **Skeleton loading** para mejor experiencia de usuario
- **Pull-to-refresh** para actualización manual
- **Haptic feedback** para retroalimentación táctil
- **Modo selección múltiple** con animaciones

### 🌐 Internacionalización
- **Soporte multiidioma** (Español/Inglés)
- **Detección automática** del idioma del sistema
- **Cambio dinámico** de idioma sin reiniciar la app

### 📱 Características Técnicas
- **Arquitectura MVVM** con Clean Architecture
- **Jetpack Compose** para UI declarativa
- **Corrutinas de Kotlin** para programación asíncrona
- **Dependency Injection** con Dagger Hilt
- **Base de datos local** con Room
- **API REST** con Retrofit y manejo de errores
- **Navegación** con Navigation Compose

## 🛠 Tecnologías y Librerías

### Frontend
- **Kotlin** - Lenguaje de programación principal
- **Jetpack Compose** - Framework de UI moderno
- **Material Design 3** - Sistema de diseño de Google
- **Compose Animation** - Animaciones fluidas
- **Coil** - Carga de imágenes asíncrona

### Arquitectura
- **MVVM** - Patrón de arquitectura
- **Clean Architecture** - Separación de responsabilidades
- **Use Cases** - Lógica de negocio encapsulada
- **Repository Pattern** - Abstracción de fuentes de datos

### Inyección de Dependencias
- **Dagger Hilt** - Framework de DI para Android

### Base de Datos y Persistencia
- **Room** - Base de datos SQLite local
- **DataStore** - Almacenamiento de preferencias
- **Kotlin Serialization** - Serialización de datos

### Networking
- **Retrofit** - Cliente HTTP type-safe
- **OkHttp** - Cliente HTTP con interceptors
- **Gson Converter** - Conversión JSON

### Testing
- **JUnit** - Unit testing framework
- **Mockito** - Mocking framework
- **Coroutines Test** - Testing para corrutinas
- **Espresso** - UI testing
- **Compose Test** - Testing para Compose UI

## 📁 Estructura del Proyecto

```
app/src/main/java/com/example/contactsapp/
├── common/                     # Utilidades y constantes
│   ├── LanguageManager.kt     # Gestión de idiomas
│   ├── StringConstants.kt     # Constantes de strings
│   └── StringResources.kt     # Recursos de strings
├── data/                      # Capa de datos
│   ├── local/                 # Base de datos local
│   │   ├── ContactDao.kt      # Acceso a datos Room
│   │   ├── ContactDatabase.kt # Configuración de BD
│   │   ├── ContactEntity.kt   # Entidad de BD
│   │   └── ContactMapper.kt   # Mapeo de entidades
│   ├── remote/                # API remota
│   │   ├── ContactApiService.kt    # Definición de API
│   │   ├── ContactRemoteDto.kt     # DTOs de red
│   │   ├── ContactRemoteMapper.kt  # Mapeo de DTOs
│   │   ├── ContactRemoteDataSource.kt # Fuente de datos remota
│   │   └── NetworkResult.kt        # Wrapper de resultados
│   └── repository/            # Implementación de repositorios
│       ├── ContactRepositoryImpl.kt     # Repositorio principal
│       └── ContactRepositoryEnhanced.kt # Versión mejorada
├── domain/                    # Capa de dominio
│   ├── model/                # Modelos de dominio
│   │   └── Contact.kt        # Modelo principal
│   ├── repository/           # Interfaces de repositorio
│   │   └── ContactRepository.kt
│   └── usecase/              # Casos de uso
│       ├── CreateContactUseCase.kt    # Crear contacto
│       ├── GetContactsUseCase.kt      # Obtener contactos
│       ├── SearchContactsUseCase.kt   # Buscar contactos
│       ├── DeleteContactsUseCase.kt   # Eliminar contactos
│       ├── UpdateContactUseCase.kt    # Actualizar contacto
│       └── SyncContactsUseCase.kt     # Sincronizar contactos
├── presentation/              # Capa de presentación
│   ├── components/           # Componentes reutilizables
│   │   ├── ContactList.kt           # Lista de contactos
│   │   ├── DynamicAvatar.kt         # Avatar dinámico
│   │   ├── SkeletonLoader.kt        # Cargador skeleton
│   │   ├── AlphabetIndex.kt         # Índice alfabético
│   │   ├── ContactQuickActions.kt   # Acciones rápidas
│   │   └── SimpleSearchField.kt     # Campo de búsqueda
│   ├── contactlist/          # Pantalla de lista
│   │   ├── ContactListScreen.kt     # UI de lista
│   │   ├── ContactListViewModel.kt  # ViewModel
│   │   └── ContactListUiState.kt    # Estado de UI
│   ├── createcontact/        # Pantalla de creación
│   │   ├── CreateContactScreen.kt     # UI de creación
│   │   ├── CreateContactViewModel.kt  # ViewModel
│   │   ├── CreateContactUiState.kt    # Estado de UI
│   │   ├── PhoneVisualTransformation.kt # Formato telefónico
│   │   └── ValidationConstants.kt      # Constantes de validación
│   ├── settings/             # Pantalla de configuración
│   │   ├── SettingsScreen.kt        # UI de configuración
│   │   └── SettingsViewModel.kt     # ViewModel
│   └── navigation/           # Navegación
│       ├── NavGraph.kt              # Grafo de navegación
│       └── Routes.kt                # Definición de rutas
├── di/                       # Inyección de dependencias
│   ├── AppModule.kt          # Módulo principal
│   └── NetworkModule.kt      # Módulo de red
├── ui/theme/                 # Theming
│   ├── Theme.kt              # Configuración de tema
│   └── Type.kt               # Tipografía
├── ContactsApplication.kt    # Clase Application
└── MainActivity.kt           # Actividad principal
```

## 🔧 Configuración y Desarrollo

### Prerrequisitos
- **Android Studio** Giraffe | 2022.3.1 o superior
- **JDK** 8 o superior
- **Android SDK** nivel 24 (Android 7.0) o superior
- **Kotlin** 1.9.0 o superior

### Instalación

1. **Clonar el repositorio**
```bash
git clone <repository-url>
cd ClaroDO
```

2. **Abrir en Android Studio**
   - Abre Android Studio
   - Selecciona "Open an existing project"
   - Navega hasta el directorio del proyecto

3. **Sincronizar dependencias**
   - Android Studio sincronizará automáticamente las dependencias
   - Si no, ve a File → Sync Project with Gradle Files

4. **Ejecutar la aplicación**
   - Conecta un dispositivo Android o inicia un emulador
   - Haz click en el botón Run (▶️) o usa `Ctrl+R`

### Configuración de Desarrollo

#### Build Variants
```kotlin
buildTypes {
    debug {
        isDebuggable = true
        applicationIdSuffix = ".debug"
    }
    release {
        isMinifyEnabled = true
        proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
    }
}
```

#### Configuración de la API
La aplicación usa JSONPlaceholder como API de prueba. Para usar tu propia API:

1. Modifica la base URL en `NetworkModule.kt`:
```kotlin
@Provides
@Singleton
fun provideRetrofit(): Retrofit {
    return Retrofit.Builder()
        .baseUrl("https://tu-api.com/") // Cambiar aquí
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}
```

## 📱 Funcionalidades Detalladas

### Lista de Contactos
- **Vista moderna** con cards y avatars dinámicos
- **Búsqueda en tiempo real** con filtrado instantáneo
- **Selección múltiple** con animaciones suaves
- **Deslizar para eliminar** con confirmación
- **Pull-to-refresh** para sincronización manual
- **Índice alfabético** lateral para navegación rápida
- **Estados vacíos** informativos y atractivos

### Creación de Contactos
- **Formulario intuitivo** con validación en tiempo real
- **Generación automática** de avatars coloridos
- **Formato automático** de números telefónicos
- **Animaciones de feedback** para mejor UX
- **Validación de campos** con mensajes claros
- **Guardado automático** al completar formulario

### Configuración
- **Cambio de idioma** dinámico (Español/Inglés)
- **Sincronización manual** con indicadores de progreso
- **Persistencia de preferencias** con DataStore

### Networking y Sincronización
- **Manejo robusto de errores** de red
- **Retry automático** para operaciones fallidas
- **Cache local** con Room para uso offline
- **Sincronización bidireccional** con servidor

## 🧪 Testing

### Ejecutar Tests
```bash
# Unit Tests
./gradlew test

# Instrumented Tests
./gradlew connectedAndroidTest

# Test Coverage
./gradlew jacocoTestReport
```

### Cobertura de Testing
- **Unit Tests** para ViewModels y Use Cases
- **Integration Tests** para Repository y Database
- **UI Tests** para pantallas principales
- **Mocking** con Mockito para dependencias

## 🔒 Consideraciones de Seguridad

- **Validación de entrada** en todos los campos
- **Sanitización de datos** antes del almacenamiento
- **Comunicación segura** con HTTPS
- **Manejo seguro de errores** sin exposición de datos sensibles
- **Permisos mínimos** requeridos (solo INTERNET)

## 🚀 Características Avanzadas

### Performance
- **Lazy loading** en listas grandes
- **Image caching** con Coil
- **Memory management** optimizado
- **Smooth animations** a 60fps
- **Efficient database queries** con Room

### Accesibilidad
- **Content descriptions** para screen readers
- **Contrast ratios** apropiados
- **Touch targets** de tamaño adecuado
- **Keyboard navigation** support

## 🛣 Roadmap Futuro

### Versión 2.0
- [ ] **Grupos de contactos** y categorización
- [ ] **Backup y restore** en la nube
- [ ] **Temas personalizados** y modo oscuro
- [ ] **Widgets** para pantalla de inicio
- [ ] **Exportación/Importación** CSV/vCard

### Versión 2.1
- [ ] **Llamadas y SMS** directos desde la app
- [ ] **Historial de interacciones**
- [ ] **Notas y recordatorios** por contacto
- [ ] **Integración con redes sociales**

## 📄 Licencia

Este proyecto está desarrollado como parte de un portafolio de desarrollo Android y está disponible bajo la licencia MIT.

## 👨‍💻 Autor

Desarrollado con ❤️ usando las mejores prácticas de desarrollo Android moderno.

---

**¿Encontraste un bug o tienes una sugerencia?**
Abre un issue en el repositorio o contribuye con un pull request.
