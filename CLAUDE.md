# CLAUDE.md

## Project Overview

TriChromaranCompose is an Android photography app that captures three separate color channel photographs (Red, Green, Blue) and combines them into a single composite image. Built with Kotlin, Jetpack Compose, and CameraX.

- **Package**: `dev.mathewsmobile.trichromarancompose`
- **Min SDK**: 30 | **Target SDK**: 35
- **Language**: Kotlin 2.1.10
- **Build System**: Gradle 8.8.0 with Kotlin DSL and version catalog

## Build & Test Commands

```bash
# Build the project
./gradlew build

# Run unit tests
./gradlew test

# Run instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest

# Clean build
./gradlew clean build

# Install debug APK on connected device
./gradlew installDebug
```

## Project Structure

```
app/src/main/java/dev/mathewsmobile/trichromarancompose/
├── data/                    # Data layer
│   ├── ImageRepository.kt   # Repository for image data access
│   ├── db/
│   │   ├── ImageDatabase.kt # Room database setup
│   │   └── dao/ImageDao.kt  # Data Access Object
│   ├── model/Image.kt       # Room entity
│   └── usecase/             # Business logic
│       ├── AddImageUseCase.kt
│       ├── GetAllImagesUseCase.kt
│       └── GetLastImageUseCase.kt
├── di/                      # Hilt dependency injection modules
│   └── DatabaseModule.kt
├── ext/                     # Extension functions
│   ├── ImageExtensions.kt   # Image processing (YUV/JPEG conversion, channel extraction, RGB combining)
│   └── TimestampExtensions.kt
├── ui/
│   ├── component/           # Reusable Compose components
│   │   ├── ButtonRow.kt
│   │   ├── CameraPreview.kt
│   │   ├── Done.kt
│   │   ├── Error.kt
│   │   ├── Gallery.kt
│   │   ├── Loading.kt
│   │   ├── Overlay.kt
│   │   └── RoundedBorderBox.kt
│   ├── screen/              # Full screen composables
│   │   ├── CameraScreen.kt
│   │   ├── GalleryScreen.kt
│   │   └── ViewImageScreen.kt
│   └── theme/               # Material 3 theming
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
├── viewmodel/               # ViewModels
│   ├── CameraViewModel.kt
│   └── GalleryViewModel.kt
├── MainActivity.kt          # Entry point with Jetpack Navigation
└── TriChromaranApplication.kt  # Hilt application class
```

## Architecture

**MVVM with Clean Architecture layers:**

- **UI Layer** (`ui/`): Jetpack Compose screens and reusable components
- **ViewModel Layer** (`viewmodel/`): State management with `StateFlow`, coroutines via `viewModelScope`
- **Domain Layer** (`data/usecase/`): Use case classes encapsulating business logic
- **Data Layer** (`data/`): Room database, DAOs, repository pattern

**Dependency Injection**: Hilt with `@HiltViewModel`, `@Inject` constructor injection, and `@Module`/`@Provides` for Room singletons.

## Key Frameworks & Dependencies

| Library | Purpose |
|---------|---------|
| Jetpack Compose (BOM 2025.02.00) | Declarative UI |
| CameraX 1.3.0 | Camera capture |
| Room 2.6.1 | Local database |
| Hilt 2.51.1 | Dependency injection |
| Navigation Compose 2.8.9 | Type-safe navigation |
| Coil 2.7.0 | Image loading |
| kotlinx-serialization 1.8.0 | JSON serialization |

## Code Conventions

### Naming

- **Composables**: PascalCase (`CameraScreen`, `Gallery`, `Loading`)
- **Functions**: camelCase (`takePicturesAndCombine`, `emitFinishedThenReady`)
- **ViewModels**: `*ViewModel` suffix
- **Use Cases**: `*UseCase` suffix
- **UI State**: `*UiState` suffix, sealed interface or data class

### UI State Pattern

Sealed interfaces for complex state machines:
```kotlin
sealed interface CameraUiState {
    data object Ready : CameraUiState
    data object Capturing : CameraUiState
    data object Done : CameraUiState
    data class Error(val message: String) : CameraUiState
}
```

Data classes for simple state:
```kotlin
data class GalleryUiState(val images: List<Image> = emptyList())
```

### Navigation

Type-safe routes using `@Serializable` objects and data classes:
```kotlin
@Serializable object CameraScreenRoute
@Serializable object GalleryScreenRoute
@Serializable data class ViewImageRoute(val imagePath: String, val timestamp: Long)
```

### Async Patterns

- `viewModelScope` for ViewModel coroutines
- `withContext(Dispatchers.IO)` for IO-bound work
- `StateFlow` / `Flow` for reactive data
- `SharingStarted.Lazily` for lazy flow initialization
- `suspendCancellableCoroutine` for wrapping camera callbacks

### Error Handling

- Try-catch in image processing with `ImageProxy.close()` in finally blocks
- `Log.e()` for error logging, `Log.d()` for debug
- UI state machines expose error states to the UI layer

### Image Processing Pipeline

1. `ImageProxy` captured from CameraX
2. YUV 4:2:0 or JPEG converted to `Bitmap`
3. `ColorMatrix` applied for channel extraction (R/G/B)
4. RGB channels combined pixel-by-pixel
5. Saved to `MediaStore.Images.Media`
6. Metadata persisted to Room database

## Configuration Notes

- **Java compatibility**: Java 11 (source and target)
- **Kotlin code style**: Official (set in `gradle.properties`)
- **Non-transitive R classes** enabled
- **ProGuard**: Rules file exists but minification is disabled
- **Edge-to-edge rendering** with safe area inset handling
- **Dynamic Material You colors** on Android 12+
- **Permissions**: `CAMERA` (required), `WRITE_EXTERNAL_STORAGE` (up to SDK 28)

## Dependency Management

All dependency versions are centralized in `gradle/libs.versions.toml` using Gradle version catalog. When adding or updating dependencies, modify the version catalog rather than hardcoding versions in `build.gradle.kts`.
