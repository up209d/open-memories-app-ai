# Portrait Beauty - API Reference

## 1. App Overview

| Field | Value |
|-------|-------|
| **Package** | `com.sony.imaging.app.portraitbeauty` |
| **Purpose** | Face beautification with portrait lighting and catchlight editing |
| **Description** | Detects faces in captured images and applies beautification effects: face brightening via DSP-based portrait lighting, soft focus effect, skin smoothing via face noise reduction, and catchlight overlay editing. Uses ImageAnalyzer for face detection and two custom DSP processors (SA_PortraitLighting, SA_SoftFocus) for effects. |

## 2. Architecture

### Main Activity
- Entry point managed via `BaseApp` framework
- State machine driven by `com.sony.imaging.app.fw.State` subclasses

### Key Classes

| Class | Path | Role |
|-------|------|------|
| `PortraitBeautySAWrapper` | `common/PortraitBeautySAWrapper.java` | Central coordinator - manages face detection, DSP SA processors, and effect application |
| `SA_PortraitLighting` | `pbsa/SA_PortraitLighting.java` | DSP processor for face-targeted brightness/lighting effects |
| `SA_SoftFocus` | `pbsa/SA_SoftFocus.java` | DSP processor for face-targeted soft focus/blur effects |
| `PortraitBeautyBackUpKey` | `common/PortraitBeautyBackUpKey.java` | Backup key definitions for persisting user preferences |
| `AppContext` | `common/AppContext.java` | Application context accessor |

## 3. Sony Scalar API Usage

### com.sony.scalar.graphics.ImageAnalyzer - Face Detection

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `new ImageAnalyzer()` | None | `ImageAnalyzer` | Creates a face detection analyzer | `common/PortraitBeautySAWrapper.java` |
| `analyzer.findFaces(OptimizedImage, AnalyzedFace[])` | Image, output array (max 8) | `int` | Detects faces in the image, populates AnalyzedFace array, returns count | `common/PortraitBeautySAWrapper.java` |
| `analyzer.release()` | None | `void` | Releases the analyzer resources | `common/PortraitBeautySAWrapper.java` |

### ImageAnalyzer.AnalyzedFace

| Field | Type | Description | Source File |
|-------|------|-------------|-------------|
| `face.rect` | `Rect` | Bounding rectangle of the detected face | `common/PortraitBeautySAWrapper.java` |
| `face` (object) | `Face` | Contains rect, eye positions, mouth position for each detected face | `common/PortraitBeautySAWrapper.java` |

### com.sony.scalar.graphics.imagefilter.FaceNRImageFilter - Skin Smoothing

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `new FaceNRImageFilter()` | None | `FaceNRImageFilter` | Creates face-targeted noise reduction filter for skin smoothing | `common/PortraitBeautySAWrapper.java` |
| `filter.setSource(OptimizedImage, boolean)` | Image, release original flag | `void` | Sets input image | `common/PortraitBeautySAWrapper.java` |
| `filter.setFaceList(Vector<Rect>)` | Face rectangle list | `void` | Sets detected face regions to apply smoothing to | `common/PortraitBeautySAWrapper.java` |
| `filter.setISOValue(int)` | ISO sensitivity | `void` | Sets ISO for NR calibration (higher ISO = stronger NR base) | `common/PortraitBeautySAWrapper.java` |
| `filter.setNRLevel(int)` | Level 1-5 | `void` | Sets noise reduction strength | `common/PortraitBeautySAWrapper.java` |
| `filter.execute()` | None | `boolean` | Applies face-targeted noise reduction | `common/PortraitBeautySAWrapper.java` |
| `filter.getOutput()` | None | `OptimizedImage` | Gets the smoothed result | `common/PortraitBeautySAWrapper.java` |
| `filter.clearSources()` | None | `void` | Clears input references | `common/PortraitBeautySAWrapper.java` |
| `filter.release()` | None | `void` | Releases filter resources | `common/PortraitBeautySAWrapper.java` |

### SA_PortraitLighting (DSP-based, `com.sony.imaging.app.pbsa`)

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `new SA_PortraitLighting(String)` | Package name | `SA_PortraitLighting` | Creates portrait lighting DSP processor with package-specific DSP binary path | `common/PortraitBeautySAWrapper.java` |
| `sa.open()` | None | `void` | Opens/initializes the DSP processor | `common/PortraitBeautySAWrapper.java` |
| `sa.initialize()` | None | `void` | Prepares DSP for execution (allocates buffers) | `common/PortraitBeautySAWrapper.java` |
| `sa.setFace(int, AnalyzedFace[])` | Face count, face array | `void` | Sets detected face data for lighting target | `common/PortraitBeautySAWrapper.java` |
| `sa.setIsoValue(int)` | ISO sensitivity | `void` | Sets ISO for effect calibration | `common/PortraitBeautySAWrapper.java` |
| `sa.setLevel(int)` | Level 0-6 | `void` | Sets face brightening intensity (0=minimum, 6=maximum) | `common/PortraitBeautySAWrapper.java` |
| `sa.execute(OptimizedImage)` | Image (in-place) | `boolean` | Applies portrait lighting effect to the image in-place | `common/PortraitBeautySAWrapper.java` |
| `sa.close()` | None | `void` | Releases DSP resources | `common/PortraitBeautySAWrapper.java` |

### SA_SoftFocus (DSP-based, `com.sony.imaging.app.pbsa`)

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `new SA_SoftFocus(String)` | Package name | `SA_SoftFocus` | Creates soft focus DSP processor | `common/PortraitBeautySAWrapper.java` |
| `sa.open()` | None | `void` | Opens/initializes the DSP processor | `common/PortraitBeautySAWrapper.java` |
| `sa.initialize()` | None | `void` | Prepares DSP for execution | `common/PortraitBeautySAWrapper.java` |
| `sa.setSAWorkSize(int, int)` | Width, height | `void` | Sets the working image dimensions for DSP processing | `common/PortraitBeautySAWrapper.java` |
| `sa.setSize(int)` | Size enum (0-4) | `void` | Sets image size category: 0=16MP, 1=20MP, 2=24MP, 3=36MP, 4=XGA | `common/PortraitBeautySAWrapper.java` |
| `sa.setLevel(int)` | Level 0-6 | `void` | Sets soft focus intensity | `common/PortraitBeautySAWrapper.java` |
| `sa.execute(OptimizedImage, OptimizedImage)` | Input, output | `boolean` | Applies soft focus effect | `common/PortraitBeautySAWrapper.java` |
| `sa.close()` | None | `void` | Releases DSP resources | `common/PortraitBeautySAWrapper.java` |

### com.sony.scalar.hardware.CameraEx.ParametersModifier

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `modifier.getISOSensitivity()` | None | `int` | Gets current ISO sensitivity value for NR calibration | `common/PortraitBeautySAWrapper.java` |
| `modifier.getFocusAreaMode()` | None | `String` | Gets current focus area mode (e.g., "flex", "local") | `common/PortraitBeautySAWrapper.java` |
| `modifier.getFocusPointIndex()` | None | `int` | Gets focus point index for local focus area mode | `common/PortraitBeautySAWrapper.java` |

### com.sony.scalar.sysutil.ScalarProperties

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `ScalarProperties.getInt("mem.rawimage.size.in.mega.pixel")` | Key | `int` | Gets raw image megapixel count to select DSP buffer size (16/20/24/36 MP) | `common/PortraitBeautySAWrapper.java` |

## 4. Image Processing Pipeline

### Capture Flow
1. **Standard Capture** - Uses CameraSequence-based capture pipeline
2. **Raw Development** - DefaultDevelopFilter processes raw to OptimizedImage
3. **Face Detection** - `ImageAnalyzer.findFaces()` detects up to 8 faces, returning `AnalyzedFace[]` with face rectangles and feature points (eyes, mouth)
4. **ISO Retrieval** - Gets current ISO from `ParametersModifier.getISOSensitivity()` (falls back to auto ISO)

### Effect Application Pipeline
5. **Soft Focus** - `SA_SoftFocus.execute(input, output)` applies face-aware soft focus blur
6. **Portrait Lighting** - `SA_PortraitLighting.execute(output)` applies face brightening in-place
7. **Both effects share the same level** (0-6), controlled by "white skin" preference value

### Alternative: Skin Smoothing Pipeline
- **FaceNRImageFilter** provides a simpler alternative:
  1. Set face rectangles from AnalyzedFace data
  2. Set ISO value and NR level (1-5, where 1=strongest)
  3. Execute - applies noise reduction targeted at face skin regions

### Image Size Selection for DSP
- DSP buffer sizes vary by sensor resolution:
  - 16MP: IMAGERSIZE_16M (0)
  - 20MP: IMAGERSIZE_20M (1)
  - 24MP: IMAGERSIZE_24M (2)
  - 36MP: IMAGERSIZE_36M (3)
  - XGA (preview): IMAGESIZE_XGA (4)

## 5. Button Handling

Button handling is managed by the shared `BaseApp` framework via `ScalarInput` key events:

| Key Constant | Action |
|-------------|--------|
| `ISV_KEY_S1_1` | Half-press shutter - autofocus (face priority) |
| `ISV_KEY_S2` | Full shutter press - capture portrait |
| `ISV_KEY_UP/DOWN` | Adjust beautification level (white skin 1-7) |
| `ISV_KEY_LEFT/RIGHT` | Navigate between editing options (lighting, skin, catchlight) |
| `ISV_KEY_ENTER` | Confirm / apply effect |
| `ISV_KEY_DELETE` | Cancel / back |
| `ISV_KEY_MENU` | Open settings menu |
| `ISV_DIAL_1` | Fine-adjust effect level |

## 6. Unique APIs

### APIs unique to portrait-beauty (not commonly used by other reference apps):

| API | Description |
|-----|-------------|
| **`ImageAnalyzer.findFaces(OptimizedImage, AnalyzedFace[])`** | Hardware-accelerated face detection returning up to 8 faces with rect, eye, and mouth positions |
| **`ImageAnalyzer.AnalyzedFace`** | Rich face data structure with bounding rect and facial feature positions |
| **`SA_PortraitLighting`** | Custom DSP processor for face-targeted brightness enhancement with 7 intensity levels |
| **`SA_PortraitLighting.setFace(int, AnalyzedFace[])`** | Passes face detection results directly to DSP for targeted processing |
| **`SA_SoftFocus`** | Custom DSP processor for face-aware soft focus/blur with configurable size category |
| **`SA_SoftFocus.setSAWorkSize(int, int)`** | Sets DSP working dimensions for optimal soft focus kernel size |
| **`SA_SoftFocus.setSize(int)`** | Resolution-aware DSP configuration (16MP/20MP/24MP/36MP/XGA) |
| **`ScalarProperties.getInt("mem.rawimage.size.in.mega.pixel")`** | Gets sensor megapixel count for DSP buffer sizing |
| **`FocusAreaController.getFocusPoint()`** | Gets focus point coordinates for face prioritization |
| **Catchlight editing** | Overlay editing of eye catchlight reflections (unique UI feature) |
