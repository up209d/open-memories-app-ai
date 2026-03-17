# Cube LUT App for Sony A6500

Apply .cube 3D LUT color grading to Sony A6500 camera output.

## Features
- Load .cube 3D LUT files from SD card `/luts/` folder
- LUT selection menu with preview thumbnails (128x128 .jpg)
- Real-time ISP preview approximation (1D GammaTable + 3x3 RGBMatrix)
- Accurate capture via JNI trilinear 33³ interpolation
- Keep RAW toggle (saves both RAW + LUT-applied JPEG)
- Fallback to bundled LUTs when SD card folder unavailable
- Background LUT processing with capture queue (max 5)
- EXIF metadata embedding (LUT info in UserComment tag)
- SD card logging for camera debugging

## LUT Setup
1. Create `/luts/` folder on SD card root
2. Place `.cube` files (must be `LUT_3D_SIZE 33`)
3. Optional: add 128x128 `.jpg` preview with same filename

## Controls
| Action | Button | Touch |
|--------|--------|-------|
| Open LUT menu | MENU | Tap LUT name |
| Capture | Shutter | - |
| Toggle RAW | FN | Tap RAW |
| Toggle Preview | AEL | Tap PRV |
| Cycle LUTs | Dial | Tap < > arrows |
| Help | C1 | Tap ? |
| Debug overlay | Long-press C1 | - |
| Back/Exit | DELETE | - |

## Building
Requires Android Studio Koala Feature Drop 2024.1.2 with:
- JDK: Eclipse Temurin 1.8
- NDK: 21.4.7075529
- Build Tools: 30.0.2

```bash
# Build
./gradlew assembleDebug

# Push test LUTs to emulator
adb push ./luts/ /sdcard/luts/

# Install
adb install app/build/outputs/apk/debug/UPCubeLutApp-debug-1.0.apk
```

## Architecture
| File | Purpose |
|------|---------|
| `CubeLutApp.java` | Main activity: camera preview, capture, LUT management |
| `LutMenuActivity.java` | LUT browser with thumbnails |
| `LutListAdapter.java` | Custom list adapter for LUT entries |
| `CubeParser.java` | Parse .cube files (metadata-only or full data) |
| `LutFileManager.java` | Scan SD card/assets for LUT files |
| `LutDecomposer.java` | Decompose 3D LUT → 1D curve + 3x3 matrix for ISP |
| `IspController.java` | Apply/clear GammaTable + RGBMatrix on camera ISP |
| `LutProcessor.java` | JNI bridge to native trilinear interpolation |
| `lut-processor.cpp` | Native C++ trilinear 33³ LUT processing |
| `FileLogger.java` | SD card file logging for camera debugging |
| `HelpActivity.java` | Help screen showing all controls |

## How It Works
**Preview**: 3D LUT decomposed into 1D gamma curve + 3x3 RGB matrix, applied to camera ISP hardware for real-time approximate color grading.

**Capture**: Full 3D LUT applied via JNI trilinear interpolation on every pixel (~300-500ms per image). Background worker thread processes captures without blocking the shutter.

## Limitations
- ISP preview is approximate (equivalent to ~2x2x2 to 3x3x3 LUT)
- Capture adds ~300-500ms per image for LUT processing
- Only 33×33×33 .cube files supported
- Preview thumbnails must be exactly 128×128 pixels
