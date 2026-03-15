# Sony Camera Android Apps - Project Guidelines

## Required Reading (load at conversation start)
When starting any task that involves writing code, understanding APIs, or building apps, read these files FIRST:

1. **`./Bible.md`** - Complete Sony native API reference (~1,760 lines). Contains every known `com.sony.scalar.*` class, method, type with human-readable descriptions, Java 8 code examples, and usage patterns. **This is the primary API reference.**
2. **`./memory/MEMORY.md`** - Project knowledge base index. Points to detailed memory files:
   - `./memory/sony-scalar-hardware.md` - CameraEx, CameraSequence, DSP, DisplayManager
   - `./memory/sony-scalar-graphics.md` - OptimizedImage, ImageFilters, JpegExporter
   - `./memory/sony-scalar-system.md` - ScalarInput, ScalarProperties, AvindexStore, didep
   - `./memory/app-architecture.md` - Reference app architecture, dual-device pattern
3. **`./reference-apps/*/API_REFERENCE.md`** - Per-app API usage details (19 files, one per decompiled Sony app). Read when you need deep details about a specific app's implementation:
   - `./reference-apps/bracket-pro/API_REFERENCE.md` - Multi-exposure bracketing (aperture, shutter, focus, flash)
   - `./reference-apps/digital-filter/API_REFERENCE.md` - Graduated ND, 3 simultaneous DSPs
   - `./reference-apps/double-exposure/API_REFERENCE.md` - 7 blend modes, live view overlay
   - `./reference-apps/graduated-filter/API_REFERENCE.md` - Graduated ND filter, DSP raw compositing
   - `./reference-apps/light-graffiti/API_REFERENCE.md` - Light painting, long exposure compositing, LED control
   - `./reference-apps/light-shaft/API_REFERENCE.md` - Crepuscular rays (god rays) via DSP effects
   - `./reference-apps/live-view-grading/API_REFERENCE.md` - Gamma tables, RGB matrix, color grading
   - `./reference-apps/manual-lens-compensation/API_REFERENCE.md` - Lens vignetting/distortion correction, EXIF
   - `./reference-apps/photo-retouch/API_REFERENCE.md` - Image filters, JpegExporter, LCE
   - `./reference-apps/picture-effect-plus/API_REFERENCE.md` - Color select, Miniature, DirectShutter
   - `./reference-apps/portrait-beauty/API_REFERENCE.md` - Face detection, portrait lighting
   - `./reference-apps/smooth-reflection/API_REFERENCE.md` - Multi-frame raw averaging, DSP raw pipeline
   - `./reference-apps/smooth-reflection-2/API_REFERENCE.md` - Multi-frame raw averaging v2, model-specific offsets
   - `./reference-apps/sound-photo/API_REFERENCE.md` - Audio recording + photo, DSP audio encoding
   - `./reference-apps/srctrl/API_REFERENCE.md` - WebAPI remote control server, SSDP, live view streaming
   - `./reference-apps/star-trails/API_REFERENCE.md` - Star trail compositing, AVI video encoding
   - `./reference-apps/sync-to-smart-phone/API_REFERENCE.md` - Auto-sync to smartphone via Wi-Fi/WebAPI
   - `./reference-apps/time-lapse/API_REFERENCE.md` - Interval capture to video, angle shift add-on
   - `./reference-apps/touchless-shutter/API_REFERENCE.md` - EVF eye sensor trigger, live bulb mode
4. **`./docs/index.html`** - Browsable HTML documentation website (same content as Bible.md in web format)

**Cost-saving strategy**: For simple tasks, this CLAUDE.md has enough context. For API-heavy tasks (building apps, using Sony APIs), read Bible.md first. Only read individual API_REFERENCE.md files when you need specific app implementation details.

## Project Overview
This workspace contains legacy Android apps targeting Sony Alpha cameras (specifically Sony A6500 / ILCE-6500) that run a custom Android 4.x (KitKat) platform internally. Sony's native APIs are undocumented; all knowledge is reverse-engineered from decompiled 1st-party apps.

## Target Hardware
- **Camera**: Sony A6500 (ILCE-6500, CXD90014 chipset, firmware 2.17+)
- **Platform**: Android 4.1-4.4 (KitKat) running inside the camera
- **Screen**: 640x480 landscape, 3.0 inch
- **Input**: Physical buttons only (no touchscreen) - shutter, dial, d-pad, menu, trash/delete, enter
- **Detection**: `Build.BRAND="sony"`, `Build.MODEL="ScalarA"`, `Build.DEVICE="dslr-diadem"`

## Build Environment (MUST follow exactly)
- **Android Studio**: Koala Feature Drop 2024.1.2
- **Gradle JDK**: Eclipse Temurin 1.8 (Java 8, 1.8.0_422)
- **Gradle Plugin**: `com.android.tools.build:gradle:4.2.2`
- **compileSdkVersion**: 16 (Android 4.1)
- **minSdkVersion**: 10
- **targetSdkVersion**: 10
- **Build Tools**: 30.0.2
- **NDK**: 21.4.7075529
- **CMake**: 3.10.2
- **Java Compatibility**: VERSION_1_8

## Test Device (Emulator)
- Android 7.1.1 x86 API 25
- Landscape 640x480

## Directory Structure
- `./open-memories-app/` - Working app (skeleton/template for new apps)
- `./reference-apps/` - Decompiled Sony 1st-party apps (19 apps, read-only reference)
- `./docs/` - Generated API documentation website (index.html)
- `./memory/` - Project knowledge base for Claude (API details, architecture, patterns)
  - `MEMORY.md` - Main index: project context, directory map, API summary, critical patterns
  - `sony-scalar-hardware.md` - CameraEx, CameraSequence, DSP, DisplayManager, Indicators
  - `sony-scalar-graphics.md` - OptimizedImage, ImageFilters, JpegExporter, OptimizedImageView
  - `sony-scalar-system.md` - ScalarInput, ScalarProperties, AvindexStore, didep, Networking
  - `app-architecture.md` - Reference app architecture, dual-device pattern, build config
- `./tmp/` - Cloned repos (OpenMemories-Framework with API_REFERENCE.md)
- `./Bible.md` - Comprehensive Sony native API reference

## Critical Rules

### Legacy Compatibility
- ALL code must compile with compileSdkVersion 16 and Java 8
- Use ONLY libraries compatible with Android 4.1+ (API 16)
- No Java 8 lambdas, streams, or Optional - use anonymous inner classes
- No AndroidX - use android.support.v4 if needed
- No Kotlin - Java only
- No modern Gradle DSL - use legacy `apply plugin` syntax
- ABI filters: `armeabi-v7a` (camera) + `x86` (emulator)

### Dual-Device Pattern (MANDATORY)
Every app MUST work on both:
1. **Sony A6500 camera** - using real `com.sony.scalar.*` native APIs
2. **Normal Android device/emulator** - using dummy/stub implementations

Use OpenMemories-Framework pattern:
```java
// Detection: DeviceInfo.getInstance().isCamera()
// Internally checks: Build.BRAND="sony" && Build.MODEL="ScalarA" && Build.DEVICE="dslr-diadem"
// Factory returns CameraXxx (real) or Xxx (dummy) implementation
```

### Sony Native API Packages
- `com.sony.scalar.hardware.*` - CameraEx, CameraSequence, DSP, DeviceBuffer, indicators
- `com.sony.scalar.graphics.*` - OptimizedImage, ImageFilters, JpegExporter
- `com.sony.scalar.media.*` - AudioManager, MediaPlayer, MediaRecorder
- `com.sony.scalar.meta.*` - FaceInfo, Histogram, TakenPhotoInfo
- `com.sony.scalar.provider.*` - AvindexStore (replaces MediaStore)
- `com.sony.scalar.sysutil.*` - ScalarInput (buttons), ScalarProperties, Settings
- `com.sony.scalar.widget.*` - OptimizedImageView
- See `Bible.md` for complete API reference with methods and usage patterns

### Key Dependencies (for new projects)
```gradle
implementation 'com.github.ma1co.OpenMemories-Framework:framework:-SNAPSHOT'
compileOnly 'com.github.ma1co.OpenMemories-Framework:stubs:-SNAPSHOT'
implementation 'com.nanohttpd:nanohttpd:2.1.1'
```
Repositories: maven.google.com, repo1.maven.org/maven2, jitpack.io

### Camera Button Mapping (ScalarInput)
- ISV_KEY_UP/DOWN/LEFT/RIGHT - D-pad
- ISV_KEY_ENTER - OK button
- ISV_KEY_MENU/SK1 - Menu
- ISV_KEY_DELETE/SK2 - Trash (acts as Back)
- ISV_KEY_S1_1 - Half-press shutter (focus)
- ISV_KEY_S2 - Full shutter press
- ISV_KEY_FN - Function button
- ISV_KEY_AEL - AE Lock
- ISV_KEY_PLAY - Playback
- ISV_KEY_STASTOP - Movie record
- ISV_KEY_CUSTOM1 - C1 button
- ISV_DIAL_1/2 - Upper/Lower dials
- ISV_KEY_MODE_DIAL - Mode dial
- ISV_KEY_LENS_ATTACH - Lens events

### Important Broadcast Intents
- `com.android.server.DAConnectionManagerService.BootCompleted` - Camera boots app
- `com.android.server.DAConnectionManagerService.ExitCompleted` - App exit done
- `com.android.server.DAConnectionManagerService.AppInfoReceive` - Register with camera OS
- `com.android.server.DAConnectionManagerService.apo` - Auto power off ("APO/NO" to disable)

### New App Creation Checklist
1. Copy `open-memories-app` project structure
2. Update applicationId, app name
3. Extend BaseActivity for all activities (handles key events, display, lifecycle)
4. Register BootCompletedReceiver and ExitCompletedReceiver
5. Call notifyAppInfo() and setAutoPowerOffMode(false) on resume
6. Implement dummy fallbacks for all Sony APIs
7. Test on emulator first, then camera
