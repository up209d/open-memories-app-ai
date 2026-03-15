# Light Shaft - API Reference

## 1. App Overview

| Field | Value |
|-------|-------|
| **Package** | `com.sony.imaging.app.lightshaft` |
| **Purpose** | Adds artificial crepuscular rays (god rays/light shafts) to photos using DSP processing |
| **Description** | Captures a single photo and applies a DSP-accelerated light shaft effect to simulate crepuscular rays (god rays) emanating from a user-specified light source point. Supports 4 effect types: Angel (soft glow rays), Star (star-burst pattern), Flare (lens flare simulation), and Beam (focused beam rays). Each effect has configurable parameters including light source position, strength, length, range (angular spread), direction, and number of shafts. The DSP program (`liblightshafts_top.so`) processes the full-resolution YCbCr image in-place after development from raw. Saves both original and processed images. Features real-time effect preview on the OSD via coordinate mapping. |

## 2. Architecture

### Main Activity
- `LightShaft` extends `BaseApp`, registered as `singleInstance` launcher activity
- Version: 1.20
- Application class: `com.sony.imaging.app.lightshaft.AppContext`
- Uses Kikilog for telemetry logging

### Key Classes

| Class | Path | Role |
|-------|------|------|
| `LightShaft` | `LightShaft.java` | Main activity - initializes effect parameters, manages boot state, logs Kikilog |
| `ShaftsEffect` | `shooting/ShaftsEffect.java` | Core DSP effect engine - manages 4 effect types, DSP execution, coordinate mapping |
| `LightShaftEffectProcess` | `shooting/LightShaftEffectProcess.java` | Capture pipeline - single shot, develop, apply effect, save original + processed |
| `LightShaftExecutorCreator` | `shooting/camera/LightShaftExecutorCreator.java` | Creates special+spinal executor, forces single drive mode and DRO off |
| `ShaftsEffect.Parameters` | `shooting/ShaftsEffect.java` (inner) | Effect parameter container with 4 sub-types (Angel, Star, Flare, Beam) |
| `ShaftsEffect.BaseParameters` | `shooting/ShaftsEffect.java` (inner) | Base parameter class - point, strength, length, range, direction, numberOfShafts |
| `ShaftView` | `shooting/widget/ShaftView.java` | OSD overlay view showing light source position and ray preview |
| `OverHeadShaftView` | `shooting/widget/OverHeadShaftView.java` | Overhead shaft position view |
| `EffectView` | `shooting/widget/EffectView.java` | Effect selection and parameter adjustment view |
| `LightSourceSettingLayout` | `menu/layout/LightSourceSettingLayout.java` | Layout for light source position setting |
| `LightShaftBackUpKey` | `LightShaftBackUpKey.java` | Persists last selected effect and option settings |
| `LightShaftConstants` | `LightShaftConstants.java` | App-wide constants including EV dial rotation state |
| `LSNormalFunctionTable` | `shooting/LSNormalFunctionTable.java` | Function table for normal shooting mode |

## 3. Sony Scalar API Usage

### com.sony.scalar.hardware.CameraEx

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `cameraEx.burstableTakePicture()` | None | `void` | Takes single picture in burstable mode | `shooting/LightShaftEffectProcess.java` |
| `cameraEx.cancelTakePicture()` | None | `void` | Cancels take before triggering capture | `shooting/LightShaftEffectProcess.java` |
| `cameraEx.startSelfTimerShutter()` | None | `void` | Starts self-timer shutter | `shooting/LightShaftEffectProcess.java` |

### com.sony.scalar.hardware.CameraSequence

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `sequence.storeImage(OptimizedImage, boolean)` | Image, release flag | `void` | Stores image - called twice: once for original (release=false), once for processed (release=true) | `shooting/LightShaftEffectProcess.java` |

### CameraSequence.Options

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `opt.setOption("MEMORY_MAP_FILE", String)` | File path | `void` | Sets memory map file for special sequence | `shooting/LightShaftEffectProcess.java` |
| `opt.setOption("EXPOSURE_COUNT", int)` | 1 | `void` | Single exposure | `shooting/LightShaftEffectProcess.java` |
| `opt.setOption("RECORD_COUNT", int)` | 1 or 2 | `void` | 2 for saving both original + processed (1 if no card) | `shooting/LightShaftEffectProcess.java` |

### CameraSequence.DefaultDevelopFilter

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `new CameraSequence.DefaultDevelopFilter()` | None | `DefaultDevelopFilter` | Creates RAW-to-YCbCr development filter | `shooting/LightShaftEffectProcess.java` |
| `filter.setSource(RawData, boolean)` | Raw data, `true` | `void` | Sets raw source with release flag | `shooting/LightShaftEffectProcess.java` |
| `filter.execute()` | None | `void` | Develops raw to OptimizedImage | `shooting/LightShaftEffectProcess.java` |
| `filter.getOutput()` | None | `OptimizedImage` | Gets developed full-resolution image | `shooting/LightShaftEffectProcess.java` |
| `filter.release()` | None | `void` | Releases filter resources | `shooting/LightShaftEffectProcess.java` |

### com.sony.scalar.hardware.DSP

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `DSP.createProcessor("sony-di-dsp")` | Processor name | `DSP` | Creates DSP for light shaft effect processing | `shooting/ShaftsEffect.java` |
| `dsp.setProgram(String)` | `liblightshafts_top.so` path | `void` | Loads the light shaft SA program | `shooting/ShaftsEffect.java` |
| `dsp.createBuffer(int)` | Size in bytes | `DeviceBuffer` | Creates work buffers (104 for params, 128 for ray table, dynamic for SA work) | `shooting/ShaftsEffect.java` |
| `dsp.getPropertyAsInt(Object, String)` | Image/buffer, property | `int` | Gets properties: `"memory-address"`, `"image-canvas-width"`, `"image-data-offset"` | `shooting/ShaftsEffect.java` |
| `dsp.setArg(int, Object)` | Index (0-5), buffer/image | `void` | Sets DSP args: 0=boot_param, 1=params, 2=input, 3=output, 4=sa_work, 5=ray_table | `shooting/ShaftsEffect.java` |
| `dsp.execute()` | None | `boolean` | Executes light shaft DSP program | `shooting/ShaftsEffect.java` |
| `dsp.clearProgram()` | None | `void` | Clears loaded DSP program | `shooting/ShaftsEffect.java` |
| `dsp.release()` | None | `void` | Releases DSP processor | `shooting/ShaftsEffect.java` |

### com.sony.scalar.hardware.DeviceBuffer

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `buffer.write(ByteBuffer)` | Parameter ByteBuffer | `void` | Writes effect parameters and ray table to device buffers | `shooting/ShaftsEffect.java` |
| `buffer.release()` | None | `void` | Releases device buffer | `shooting/ShaftsEffect.java` |

### com.sony.scalar.graphics.OptimizedImage

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `img.getWidth()` | None | `int` | Gets image width for aspect ratio calculation | `shooting/ShaftsEffect.java` |
| `img.getHeight()` | None | `int` | Gets image height for aspect ratio calculation | `shooting/ShaftsEffect.java` |

### com.sony.scalar.hardware.avio.DisplayManager

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `DisplayManager.VideoRect` (fields) | `pxLeft`, `pxRight`, `pxTop`, `pxBottom` | `int` | EE display rectangle for OSD coordinate mapping | `shooting/ShaftsEffect.java` |
| `DisplayManager.DeviceStatus` (fields) | `viewPattern` | `int` | Display rotation/orientation for coordinate transforms | `shooting/ShaftsEffect.java` |

### com.sony.scalar.sysutil.ScalarProperties

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `ScalarProperties.getString("version.platform")` | Key | `String` | Gets platform version for PF version checks | `shooting/ShaftsEffect.java` |

### com.sony.scalar.sysutil.ScalarInput

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `ScalarInput.getKeyStatus(int)` | Key code | `KeyStatus` | Gets key status for housing detection | `LightShaft.java` |

### com.sony.scalar.sysutil.didep.Kikilog

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `new Kikilog.Options()` | None | `Kikilog.Options` | Creates telemetry options | `LightShaft.java`, `shooting/LightShaftEffectProcess.java` |
| `Kikilog.setUserLog(int, Options)` | Log ID, options | `void` | Logs telemetry - app start (4157), effect usage (4158-4161 per effect type) | `LightShaft.java`, `shooting/LightShaftEffectProcess.java` |

### DisplayModeObserver (wraps DisplayManager)

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `getInstance().getActiveDeviceStatus()` | None | `DeviceStatus` | Gets current display device status (viewPattern for rotation) | `shooting/ShaftsEffect.java` |
| `getInstance().getActiveDevice()` | None | `int` | Gets active display device ID (0=LCD, etc.) | `shooting/ShaftsEffect.java` |
| `getInstance().setNotificationListener(NotificationListener)` | Listener | `void` | Registers for display change notifications | `shooting/ShaftsEffect.java` |
| `getInstance().controlGraphicsOutputAll(boolean)` | Enable | `void` | Controls graphics output on all displays | Used during state transitions |

## 4. Image Processing Pipeline

1. **prepare()**: Creates `CameraSequence.Options` with `MEMORY_MAP_FILE`, `EXPOSURE_COUNT=1`, `RECORD_COUNT=2` (or 1 if no card), sets adapter options
2. **takePicture()**: Calls `cancelTakePicture()` then `burstableTakePicture()` for single capture
3. **onShutterSequence()**: Progress callback (0/5), develops raw via `DefaultDevelopFilter` to full-resolution OptimizedImage (progress 1/5 to 2/5)
4. **Save original**: `sequence.storeImage(original, false)` saves unprocessed image (progress 3/5)
5. **Apply effect**: `ShaftsEffect.getInstance().run(original, sequence)`:
   a. Creates `DSP.createProcessor("sony-di-dsp")`
   b. Loads `liblightshafts_top.so` DSP program
   c. Creates boot parameter buffer (via `prepare_sa_bootparam`)
   d. Gets image memory address, canvas width, data offset from DSP properties
   e. Creates SA work buffer (size based on image dimensions)
   f. Creates 128-byte ray table buffer
   g. Gets picture size and calculates light source point in image coordinates (handles aspect ratio differences)
   h. Writes effect parameters to ByteBuffer: input/output addresses, dimensions, canvas width, light source point, effect-specific params
   i. Sets 6 DSP arguments: boot_param, params, input image, output image (same buffer - in-place), SA work, ray table
   j. Executes DSP program
   k. Releases work buffers and DSP
6. **Save processed**: `sequence.storeImage(copy, true)` saves processed image with release (progress 4/5)
7. **Complete**: `adapter.enableNextCapture(0)` signals completion

## 5. Button Handling

| Key Constant | Action |
|-------------|--------|
| `ISV_KEY_S1_1` | Half-press shutter - enters EE state |
| `ISV_KEY_S2` | Full shutter press - captures photo and applies light shaft effect |
| `ISV_KEY_MENU` / `ISV_KEY_SK1` | Opens effect selection/settings menu |
| `ISV_KEY_DELETE` / `ISV_KEY_SK2` | Cancel/back |
| `ISV_KEY_FN` | Function button |
| `ISV_KEY_ENTER` | Confirm light source position |
| `ISV_KEY_UP/DOWN/LEFT/RIGHT` | Move light source position on OSD |
| `ISV_DIAL_1` | Adjust effect parameter (strength/length/range depending on context) |
| `ISV_DIAL_2` | Adjust effect parameter (direction/numberOfShafts) |
| `ISV_KEY_PLAY` | Switch to playback |

## 6. Unique APIs

| API | Description |
|-----|-------------|
| `DSP.setProgram("liblightshafts_top.so")` | Custom DSP program for crepuscular ray generation |
| `DSP.getPropertyAsInt(image, "memory-address")` | Gets physical memory address of OptimizedImage for DSP in-place processing |
| `DSP.getPropertyAsInt(image, "image-canvas-width")` | Gets image canvas width (may differ from image width due to alignment) |
| `DSP.getPropertyAsInt(image, "image-data-offset")` | Gets byte offset to actual image data within memory buffer |
| 4 effect types with unique DSP parameters | Angel (1), Star (2), Flare (3), Beam (4) - each with different ray table configurations |
| In-place DSP processing | Input and output are the same OptimizedImage - DSP modifies pixels in-place |
| `prepare_sa_bootparam()` | Creates boot parameter buffer with program descriptor for DSP initialization |
| OSD coordinate mapping system | Maps between 0-1000 abstract coordinates and actual display pixels, accounting for rotation and active device |
| Aspect ratio handling | Detects image aspect (3:2, 16:9, 4:3, 1:1) and adjusts light source coordinates for imager vs picture size differences |
| `Kikilog.setUserLog()` | Telemetry logging for app start (ID 4157) and per-effect usage (Angel=4158, Star=4159, Flare=4160, Beam=4161) |
| `sequence.storeImage()` called twice | Saves both original (unprocessed) and effect-processed versions of the same capture |
| `DisplayModeObserver` notifications | Listens for display device/rotation/YUV layout changes to update effect preview coordinates in real-time |
