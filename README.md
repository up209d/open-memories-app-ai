# Sony Camera Android Apps

Custom Android apps for Sony Alpha cameras (A6500 / ILCE-6500) running the internal Android 4.x platform. Built on reverse-engineered Sony `com.sony.scalar.*` native APIs from decompiled 1st-party PlayMemories apps.

## Target Hardware

| Spec | Value |
|------|-------|
| Camera | Sony A6500 (ILCE-6500, CXD90014 chipset, firmware 2.17+) |
| Platform | Android 4.1–4.4 (KitKat) running inside the camera |
| Screen | 640x480 landscape, 3.0 inch |
| Input | Physical buttons only (shutter, dial, d-pad, menu, trash, enter) |

## Project Structure

```
sony-apps/
├── open-memories-app/    # Working app template for new apps
├── reference-apps/       # Decompiled Sony 1st-party apps (read-only)
│   ├── smooth-reflection/    # Multi-frame raw averaging
│   ├── digital-filter/       # Graduated ND, 3 simultaneous DSPs
│   ├── double-exposure/      # 7 blend modes, live view overlay
│   ├── photo-retouch/        # Image filters, JPEG export
│   ├── picture-effect-plus/  # Color select, Miniature
│   ├── portrait-beauty/      # Face detection, portrait lighting
│   └── live-view-grading/    # Gamma tables, RGB matrix, color grading
├── docs/                 # Generated API documentation website
├── memory/               # Project knowledge base
├── Bible.md              # Complete Sony native API reference
└── CLAUDE.md             # AI assistant project instructions
```

## Build Environment

- **Android Studio**: Koala Feature Drop 2024.1.2
- **Gradle JDK**: Eclipse Temurin 1.8 (Java 8, `1.8.0_422`)
- **Gradle Plugin**: `com.android.tools.build:gradle:4.2.2`
- **compileSdkVersion**: 16 (Android 4.1)
- **minSdkVersion**: 10
- **targetSdkVersion**: 10
- **Build Tools**: 30.0.2
- **NDK**: 21.4.7075529
- **CMake**: 3.10.2

### Test Emulator

- Android 7.1.1 x86 API 25
- Landscape 640x480

## Setup

1. Install [Android Studio Koala Feature Drop 2024.1.2](https://developer.android.com/studio/archive)
2. Configure Gradle JDK as Temurin `1.8`
3. Install SDK 16, NDK `21.4.7075529`, CMake `3.10.2`, Build Tools `30.0.2`
4. Set the `JAVA_HOME` environment variable to your Temurin 1.8 install path
5. Create/update `local.properties` in the app project (replace `YOUR_USER` with your Windows username):

```properties
sdk.dir=C\:\\Users\\YOUR_USER\\AppData\\Local\\Android\\Sdk
ndk.dir=C\:\\Users\\YOUR_USER\\AppData\\Local\\Android\\Sdk\\ndk\\21.4.7075529
org.gradle.java.home=C\:\\Users\\YOUR_USER\\.jdks\\temurin-1.8.0_482
```

## Build

```bash
cd open-memories-app
./gradlew build
```

## Architecture: Dual-Device Pattern

Every app must work on both the Sony camera and a normal Android emulator/device. Detection is handled via:

```java
DeviceInfo.getInstance().isCamera()
// Checks: Build.BRAND="sony" && Build.MODEL="ScalarA" && Build.DEVICE="dslr-diadem"
```

A factory pattern returns real `com.sony.scalar.*` implementations on-camera and dummy/stub implementations elsewhere.

### Key Dependencies

```gradle
implementation 'com.github.ma1co.OpenMemories-Framework:framework:-SNAPSHOT'
compileOnly 'com.github.ma1co.OpenMemories-Framework:stubs:-SNAPSHOT'
implementation 'com.nanohttpd:nanohttpd:2.1.1'
```

## Legacy Compatibility Rules

- Java 8 only — no lambdas, streams, or Optional (anonymous inner classes)
- No AndroidX — use `android.support.v4` if needed
- No Kotlin
- ABI filters: `armeabi-v7a` (camera) + `x86` (emulator)
- Legacy Gradle `apply plugin` syntax only

## Sony Native API Packages

| Package | Contents |
|---------|----------|
| `com.sony.scalar.hardware.*` | CameraEx, CameraSequence, DSP, DeviceBuffer |
| `com.sony.scalar.graphics.*` | OptimizedImage, ImageFilters, JpegExporter |
| `com.sony.scalar.media.*` | AudioManager, MediaPlayer, MediaRecorder |
| `com.sony.scalar.meta.*` | FaceInfo, Histogram, TakenPhotoInfo |
| `com.sony.scalar.provider.*` | AvindexStore (replaces MediaStore) |
| `com.sony.scalar.sysutil.*` | ScalarInput (buttons), ScalarProperties, Settings |
| `com.sony.scalar.widget.*` | OptimizedImageView |

See [Bible.md](Bible.md) for the complete API reference, or browse the [docs site](docs/index.html).

## License

MIT License — see [open-memories-app/LICENSE.txt](open-memories-app/LICENSE.txt)

Based on [OpenMemories-Framework](https://github.com/ma1co/OpenMemories-Framework) by ma1co.
