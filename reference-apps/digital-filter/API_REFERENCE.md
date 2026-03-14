# Digital Filter - API Reference

## 1. App Overview

| Field | Value |
|-------|-------|
| **Package** | `com.sony.imaging.app.digitalfilter` |
| **Purpose** | Graduated ND filter simulation via multi-shot raw compositing |
| **Description** | Simulates a graduated neutral density filter by capturing multiple raw images and compositing them at the raw data level using DSP hardware acceleration. Supports different filter strengths and positions. |

## 2. Architecture

### Main Activity
- Entry point managed via `BaseApp` framework (shared base)
- State machine driven by `com.sony.imaging.app.fw.State` subclasses

### Key Classes

| Class | Path | Role |
|-------|------|------|
| `GFCompositProcess` | `shooting/GFCompositProcess.java` | Main compositing capture pipeline - handles multi-shot raw capture and DSP compositing |
| `GFForceSettingState` | `shooting/GFForceSettingState.java` | Forces camera settings (disables anti-hand-blur) before shooting |
| `NDSA` | `sa/NDSA.java` | ND filter SA (Signal Accelerator) processor - single frame |
| `NDSA2` | `sa/NDSA2.java` | ND filter SA processor v2 - dual frame compositing |
| `NDSA2Multi` | `sa/NDSA2Multi.java` | ND filter SA processor v2 multi-frame compositing with 3 simultaneous DSP processors |
| `PRESA` | `sa/PRESA.java` | Preview SA processor - provides live preview of the ND effect |
| `MemoryUtil` | `common/MemoryUtil.java` | JNI wrapper for direct memory access between app and Diadem hardware |
| `GFRawDataInfo` | `common/GFRawDataInfo.java` | Encapsulates raw sensor data metadata |
| `GFShootingOrderController` | `shooting/camera/GFShootingOrderController.java` | Controls multi-shot capture sequence |
| `GFClippingCorrectionController` | `shooting/camera/GFClippingCorrectionController.java` | Handles clipping correction for composited images |

## 3. Sony Scalar API Usage

### com.sony.scalar.hardware.DSP

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `DSP.createProcessor(String)` | `"sony-di-dsp"` | `DSP` | Creates a DSP hardware processor instance for signal acceleration | `sa/NDSA2Multi.java`, `sa/PRESA.java` |
| `dsp.setProgram(String)` | Path to `.so` library | `void` | Loads a DSP program (e.g., `libsa_ndsa.so`, `libsa_ndsa_mushashi.so`) | `sa/NDSA2Multi.java` |
| `dsp.createBuffer(int)` | Size in bytes | `DeviceBuffer` | Allocates a hardware device buffer for DSP parameter passing | `sa/NDSA2Multi.java` |
| `dsp.getPropertyAsInt(String)` | `"program-descriptor"` | `int` | Gets the DSP program descriptor for boot parameters | `sa/NDSA2Multi.java` |
| `dsp.getPropertyAsInt(Object, String)` | `(rawData, "memory-address")` | `int` | Gets hardware memory address of a raw data buffer | `sa/NDSA2Multi.java` |
| `dsp.setArg(int, DeviceBuffer)` | Index, buffer | `void` | Sets a DSP execution argument (boot params, SA params, work buffers) | `sa/NDSA2Multi.java` |
| `dsp.setArg(int, DeviceMemory)` | Index, memory | `void` | Sets a DSP execution argument pointing to device memory | `sa/NDSA2Multi.java` |
| `dsp.execute()` | None | `boolean` | Executes the loaded DSP program with configured arguments | `sa/NDSA2Multi.java` |
| `dsp.clearProgram()` | None | `void` | Clears the loaded DSP program before release | `sa/NDSA2Multi.java` |
| `dsp.release()` | None | `void` | Releases the DSP processor and associated resources | `sa/NDSA2Multi.java` |

### com.sony.scalar.hardware.CameraSequence

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `CameraSequence.open(CameraEx)` | Camera instance | `CameraSequence` | Opens a camera sequence for multi-shot raw capture | `sa/PRESA.java` |
| `sequence.setPreviewPlugin(DSP)` | DSP processor | `void` | Installs a DSP processor as a live preview plugin for real-time effect preview | `sa/PRESA.java` |
| `sequence.startPreviewSequence(Options)` | Sequence options | `void` | Starts the preview sequence with the installed plugin | `sa/PRESA.java` |
| `sequence.stopPreviewSequence()` | None | `void` | Stops the running preview sequence | `sa/PRESA.java` |
| `sequence.release()` | None | `void` | Releases the camera sequence | `sa/PRESA.java` |
| `sequence.storeImage(OptimizedImage, boolean)` | Image, release flag | `void` | Stores processed image to media | `shooting/GFCompositProcess.java` |

### CameraSequence.Options

| Method | Parameters | Description | Source File |
|--------|-----------|-------------|-------------|
| `setOption("PREVIEW_FRAME_RATE", int)` | Frame rate (e.g., `30000`) | Sets preview frame rate for DSP plugin | `sa/PRESA.java` |
| `setOption("PREVIEW_FRAME_WIDTH", int)` | Width (e.g., `1024`) | Sets preview frame width | `sa/PRESA.java` |
| `setOption("PREVIEW_FRAME_HEIGHT", int)` | Height | Sets preview frame height | `sa/PRESA.java` |
| `setOption("PREVIEW_PLUGIN_NOTIFY_MASK", int)` | Mask value | Controls which plugin notifications are enabled | `sa/PRESA.java` |
| `setOption("PREVIEW_DEBUG_NOTIFY_ENABLED", boolean)` | `false` | Enables/disables debug notifications | `sa/PRESA.java` |
| `setOption("PREVIEW_PLUGIN_OUTPUT_ENABLED", boolean)` | `true` | Enables DSP plugin output to display | `sa/PRESA.java` |

### CameraSequence.RawData / RawDataInfo

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `raw.getRawDataInfo()` | None | `RawDataInfo` | Gets metadata about the raw sensor data (canvas size, margins, white balance, etc.) | `shooting/GFCompositProcess.java` |
| `raw.isValid()` | None | `boolean` | Checks if the raw data buffer is valid | `shooting/GFCompositProcess.java` |
| `raw.release()` | None | `void` | Releases the raw data buffer | `shooting/GFCompositProcess.java` |

### com.sony.scalar.hardware.DeviceBuffer / DeviceMemory

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `buffer.write(ByteBuffer)` | Data to write | `void` | Writes parameter data to hardware device buffer | `sa/NDSA2Multi.java` |
| `buffer.release()` | None | `void` | Releases the device buffer | `sa/NDSA2Multi.java` |
| `memory.release()` | None | `void` | Releases device memory allocation | `sa/NDSA2Multi.java` |

### com.sony.scalar.hardware.CameraEx

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `cameraEx.burstableTakePicture()` | None | `void` | Takes a picture in burst-capable mode | `shooting/GFCompositProcess.java` |
| `cameraEx.startSelfTimerShutter()` | None | `void` | Starts self-timer shutter sequence | `shooting/GFCompositProcess.java` |

### CameraEx.ParametersModifier

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `modifier.setAntiHandBlurMode(String)` | `"off"` | `void` | Disables optical image stabilization for tripod shooting | `shooting/GFForceSettingState.java` |

### com.sony.scalar.sysutil.ScalarProperties

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `ScalarProperties.getString(String)` | `"version.platform"` | `String` | Gets platform version to determine hardware generation | `sa/NDSA2Multi.java` |

### com.sony.scalar.provider.AvindexStore

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `AvindexStore.getExternalMediaIds()` | None | `String[]` | Gets available external media (SD card) identifiers | `util/DatabaseUtil.java` |

## 4. Image Processing Pipeline

1. **Force Settings** - `GFForceSettingState` disables anti-hand-blur (OIS) for consistent multi-frame capture
2. **Preview Plugin** - `PRESA` creates a DSP processor, installs it as a CameraSequence preview plugin for real-time graduated ND preview
3. **Multi-Shot Capture** - `GFCompositProcess` triggers `burstableTakePicture()` for each frame in the sequence
4. **Raw Data Retrieval** - Each `onShutterSequence()` callback provides `CameraSequence.RawData` with sensor-level data
5. **DSP Raw Compositing** - `NDSA2Multi` creates up to 3 simultaneous DSP processors, loads `libsa_ndsa.so` programs
6. **Parameter Configuration** - Boot parameters (program descriptor, mode) and SA parameters (memory addresses, canvas sizes, composite count, gamma tables) are written to DeviceBuffers
7. **Memory Operations** - `MemoryUtil` JNI performs direct memory copies between application memory and Diadem hardware memory (`MemoryCopyRaw`, `MemoryCopyDiademToApplication`, `MemoryCopyApplicationToDiadem`)
8. **Composite Execution** - DSP `execute()` performs raw-level pixel compositing with configurable ND gradient
9. **Development** - `CameraSequence.DefaultDevelopFilter` converts composited raw data to RGB OptimizedImage
10. **Storage** - `sequence.storeImage()` saves the final image to media

## 5. Button Handling

Button handling is managed by the shared `BaseApp` framework via `ScalarInput` key events:

| Key Constant | Action |
|-------------|--------|
| `ISV_KEY_S1_1` | Half-press shutter - autofocus |
| `ISV_KEY_S2` | Full shutter press - triggers multi-shot capture sequence |
| `ISV_KEY_UP/DOWN` | Adjust ND filter position/strength |
| `ISV_KEY_LEFT/RIGHT` | Navigate effect parameters |
| `ISV_KEY_ENTER` | Confirm settings |
| `ISV_KEY_DELETE` | Back/cancel |
| `ISV_KEY_MENU` | Open menu |
| `ISV_DIAL_1` | Upper dial - adjust filter gradient position |
| `ISV_DIAL_2` | Lower dial - adjust filter intensity |

## 6. Unique APIs

### APIs unique to digital-filter (not commonly used by other reference apps):

| API | Description |
|-----|-------------|
| **3 simultaneous DSP processors** (`NDSA2Multi`) | Only app that creates and manages 3 DSP processors simultaneously for parallel raw compositing |
| **MemoryUtil JNI** (`MemoryCopyRaw`, `MemoryCopyDiademToApplication`, `MemoryCopyApplicationToDiadem`) | Direct hardware memory access via native JNI - copies raw sensor data between ARM application memory and Diadem hardware memory |
| **NDSA/NDSA2/NDSA2Multi raw compositing** | Three distinct SA (Signal Accelerator) programs for different compositing modes: single-frame ND, dual-frame ND, and multi-frame ND with gamma correction |
| **Gamma table manipulation in raw pipeline** | `GAMMA_8_16_TABLE_SIZE = 2050`, `ADRC_ON/OFF` flags for tone curve modification during raw compositing |
| **Preview plugin with DSP** | Real-time graduated ND preview via `CameraSequence.setPreviewPlugin(DSP)` |
