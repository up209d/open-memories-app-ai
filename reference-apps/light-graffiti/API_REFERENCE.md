# Light Graffiti - API Reference

## 1. App Overview

| Field | Value |
|-------|-------|
| **Package** | `com.sony.imaging.app.lightgraffiti` |
| **Purpose** | Light painting via multi-exposure compositing with real-time DSP preview |
| **Description** | Captures light painting photography by taking up to 3 sequential exposures (underexposed background, light painting exposure, optional fill exposure) and compositing them using DSP hardware acceleration. Features a real-time preview plugin that shows the composited result on the live view during the light painting phase. Uses a multi-stage shooting workflow (1st/2nd/3rd exposures) with differential image processing (DIFF, LPF, COMP filters) via custom SA programs running on the DSP. Supports self-timer (2s/10s), remote control, virtual media for temporary storage, and exposure compensation manipulation for underexposure. |

## 2. Architecture

### Main Activity
- `LightGraffitiApp` extends `BaseApp`, registered as `singleInstance` launcher activity
- Version: 0.0030
- Application class: `com.sony.imaging.app.lightgraffiti.AppContext`
- Registers 12 custom camera controllers

### Key Classes

| Class | Path | Role |
|-------|------|------|
| `LightGraffitiApp` | `LightGraffitiApp.java` | Main activity - registers controllers, manages boot state |
| `CompositProcess` | `shooting/CompositProcess.java` | Core multi-exposure engine - 3-stage capture with DSP compositing |
| `LGExecutorCreator` | `shooting/LGExecutorCreator.java` | Creates custom executor and LGImagingAdapterImpl |
| `LGNormalExecutor` | `shooting/LGNormalExecutor.java` | Custom executor for light graffiti capture mode |
| `LGStableExecutor` | `shooting/LGStableExecutor.java` | Stable state executor |
| `LGImagingAdapterImpl` | `shooting/LGImagingAdapterImpl.java` | Custom imaging adapter with CameraSequence access |
| `LGStateHolder` | `shooting/LGStateHolder.java` | Multi-stage state machine (SHOOTING_1ST through PROCESSING) |
| `LGSAMixFilter` | `util/LGSAMixFilter.java` | DSP SA program wrapper - DIFF, LPF, COMP filter modes |
| `LGPreviewEffect` | `util/LGPreviewEffect.java` | Real-time DSP preview plugin for live compositing view |
| `LGImageUtil` | `util/LGImageUtil.java` | Image utility - SFR crop, OptimizedImage/DeviceBuffer conversion |
| `LGSAReviewFilter` | `util/LGSAReviewFilter.java` | SA filter for review image generation |
| `LGSelfTimerThread` | `shooting/timer/LGSelfTimerThread.java` | Self-timer control (2s/10s) |
| `LGTimerThread` | `shooting/timer/LGTimerThread.java` | Exposure duration timer |
| `LGAppTopController` | `shooting/controller/LGAppTopController.java` | Top-level app controller for duration and mode settings |

## 3. Sony Scalar API Usage

### com.sony.scalar.hardware.CameraEx

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `cameraEx.burstableTakePicture()` | None | `void` | Takes picture in burstable mode for each exposure stage | `shooting/CompositProcess.java` |
| `cameraEx.cancelTakePicture()` | None | `void` | Cancels take between exposure stages | `shooting/CompositProcess.java` |
| `cameraEx.startSelfTimerShutter()` | None | `void` | Starts self-timer shutter | `shooting/CompositProcess.java` |
| `cameraEx.setRecordingMedia(int[], RecordingMediaChangeCallback)` | Virtual media IDs, callback | `void` | Switches to virtual media for 1st exposure (temporary storage) | `shooting/CompositProcess.java` |
| `cameraEx.setAutoPictureReviewControl(AutoPictureReviewControl)` | Control | `void` | Sets auto picture review controller | `shooting/CompositProcess.java` |

### CameraEx.AutoPictureReviewControl

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `new CameraEx.AutoPictureReviewControl()` | None | `AutoPictureReviewControl` | Creates review control | `shooting/CompositProcess.java` |
| `control.getPictureReviewTime()` | None | `int` | Gets current review time (workaround: min 2 if 0) | `shooting/CompositProcess.java` |
| `control.setPictureReviewTime(int)` | Time (0=off) | `void` | Disables review during multi-shot sequence | `shooting/CompositProcess.java` |

### CameraEx.RecordingMediaChangeCallback

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `onRecordingMediaChange(CameraEx)` | CameraEx | `void` | Callback when virtual media switch completes, triggers burstableTakePicture | `shooting/CompositProcess.java` |

### com.sony.scalar.hardware.CameraSequence

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `sequence.storeImage(OptimizedImage, boolean)` | Image, release flag | `void` | Stores developed image to media (called in each process stage) | `shooting/CompositProcess.java` |
| `sequence.setPreviewPlugin(DSP)` | DSP instance or null | `void` | Sets DSP as real-time preview plugin for live compositing view | `util/LGPreviewEffect.java` |
| `sequence.startPreviewSequence(Options)` | Preview options | `void` | Starts real-time preview with DSP plugin | `util/LGPreviewEffect.java` |
| `sequence.stopPreviewSequence()` | None | `void` | Stops real-time preview | `util/LGPreviewEffect.java` |

### CameraSequence.Options

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `opt.setOption("MEMORY_MAP_FILE", String)` | File path | `void` | Sets memory map file for special sequence | `shooting/CompositProcess.java` |
| `opt.setOption("EXPOSURE_COUNT", int)` | Count (1) | `void` | Sets exposures per step (only for PF < 2.0) | `shooting/CompositProcess.java` |
| `opt.setOption("RECORD_COUNT", int)` | Count (1) | `void` | Sets record count per step | `shooting/CompositProcess.java` |
| `opt.setOption("INTERIM_PRE_REVIEW_ENABLED", boolean)` | `true` | `void` | Enables interim preview between stages | `shooting/CompositProcess.java` |
| `opt.setOption("DETECTION_OFF", boolean)` | `false` | `void` | Keeps detection enabled | `shooting/CompositProcess.java` |
| `opt.setOption("AUTO_RELEASE_LOCK_ENABLED", boolean)` | `false` | `void` | Disables auto lock release | `shooting/CompositProcess.java` |
| `opt.setOption("PREVIEW_FRAME_RATE", int)` | 30000 | `void` | Sets preview frame rate (30fps) | `util/LGPreviewEffect.java` |
| `opt.setOption("PREVIEW_FRAME_WIDTH", int)` | Scaled width | `void` | Sets preview frame width | `util/LGPreviewEffect.java` |
| `opt.setOption("PREVIEW_FRAME_HEIGHT", int)` | 0 (auto) | `void` | Sets preview frame height (0=auto) | `util/LGPreviewEffect.java` |
| `opt.setOption("PREVIEW_PLUGIN_NOTIFY_MASK", int)` | 0 | `void` | Sets preview plugin notification mask | `util/LGPreviewEffect.java` |
| `opt.setOption("PREVIEW_DEBUG_NOTIFY_ENABLED", boolean)` | `false` | `void` | Disables preview debug notifications | `util/LGPreviewEffect.java` |
| `opt.setOption("PREVIEW_PLUGIN_OUTPUT_ENABLED", boolean)` | `true` | `void` | Enables preview plugin output to display | `util/LGPreviewEffect.java` |

### CameraSequence.DefaultDevelopFilter

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `new CameraSequence.DefaultDevelopFilter()` | None | `DefaultDevelopFilter` | Creates RAW-to-YCbCr development filter | `shooting/CompositProcess.java` |
| `filter.setSource(RawData, boolean)` | Raw data, flag | `void` | Sets raw source for development | `shooting/CompositProcess.java` |
| `filter.execute()` | None | `void` | Executes development | `shooting/CompositProcess.java` |
| `filter.getOutput()` | None | `OptimizedImage` | Gets developed YCbCr image | `shooting/CompositProcess.java` |
| `filter.release()` | None | `void` | Releases filter | `shooting/CompositProcess.java` |

### CameraSequence.RawData

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `raw.release()` | None | `void` | Releases raw data | `shooting/CompositProcess.java` |
| `raw.isValid()` | None | `boolean` | Checks if raw data is still valid after release | `shooting/CompositProcess.java` |

### com.sony.scalar.hardware.DSP

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `DSP.createProcessor("sony-di-dsp")` | Processor name | `DSP` | Creates DSP processor for SA mix filter | `util/LGSAMixFilter.java` |
| `dsp.setProgram(String)` | `libsa_lightgraffiti_avip.so` or `libsa_lightgraffiti_musashi.so` | `void` | Loads light graffiti SA program (AVIP or Musashi variant) | `util/LGSAMixFilter.java` |
| `dsp.createBuffer(int)` | Size (60 for boot param, 172 for SA param) | `DeviceBuffer` | Allocates DSP work buffers | `util/LGSAMixFilter.java` |
| `dsp.getPropertyAsInt(Object, String)` | Image/buffer, property name | `int` | Gets memory address, canvas width from image/buffer | `util/LGSAMixFilter.java` |
| `dsp.getPropertyAsInt(String)` | `"program-descriptor"` | `int` | Gets program descriptor for boot parameter | `util/LGSAMixFilter.java` |
| `dsp.setArg(int, Object)` | Arg index (0-4), DeviceBuffer/OptimizedImage | `void` | Sets DSP execution arguments (boot param, SA param, images) | `util/LGSAMixFilter.java` |
| `dsp.execute()` | None | `boolean` | Executes DSP program, returns success | `util/LGSAMixFilter.java` |
| `dsp.release()` | None | `void` | Releases DSP processor | `util/LGSAMixFilter.java` |

### com.sony.scalar.hardware.DeviceBuffer

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `buffer.write(ByteBuffer, int, int)` | ByteBuffer, size, offset | `void` | Writes SA parameter data to device buffer | `util/LGSAMixFilter.java` |
| `buffer.write(ByteBuffer)` | ByteBuffer | `void` | Writes boot parameter to device buffer | `util/LGSAMixFilter.java` |
| `buffer.read(byte[])` | Output byte array | `void` | Reads DSP execution results | `util/LGSAMixFilter.java` |
| `buffer.release()` | None | `void` | Releases device buffer | `util/LGSAMixFilter.java`, `shooting/CompositProcess.java` |

### com.sony.scalar.hardware.MemoryMapConfig

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `MemoryMapConfig.setAllocationPolicy(int)` | Policy (1) | `void` | Sets memory allocation policy for special sequence | `shooting/CompositProcess.java` |

### com.sony.scalar.graphics.OptimizedImage

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `img.getWidth()` | None | `int` | Gets image width | `shooting/CompositProcess.java` |
| `img.getHeight()` | None | `int` | Gets image height | `shooting/CompositProcess.java` |
| `img.release()` | None | `void` | Releases image memory | `shooting/CompositProcess.java` |

### com.sony.scalar.graphics.imagefilter.ScaleImageFilter

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `new ScaleImageFilter()` | None | `ScaleImageFilter` | Creates scale/copy filter | `shooting/CompositProcess.java` |
| `filter.setSource(OptimizedImage, boolean)` | Source image, flag | `void` | Sets source image to scale/copy | `shooting/CompositProcess.java` |
| `filter.setDestSize(int, int)` | Width, height | `void` | Sets destination size (same size = copy) | `shooting/CompositProcess.java` |
| `filter.execute()` | None | `boolean` | Executes scale/copy operation | `shooting/CompositProcess.java` |
| `filter.getOutput()` | None | `OptimizedImage` | Gets scaled/copied output | `shooting/CompositProcess.java` |
| `filter.release()` | None | `void` | Releases filter | `shooting/CompositProcess.java` |

### com.sony.scalar.provider.AvindexStore

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `AvindexStore.getVirtualMediaIds()` | None | `int[]` | Gets virtual media IDs for temporary 1st exposure storage | `shooting/CompositProcess.java` |

### com.sony.scalar.hardware.indicator.Light

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `Light.setState("LID_SELF_TIMER", boolean)` | LED name, on/off | `void` | Controls self-timer LED indicator (flash for remote focus, steady for counting) | `shooting/CompositProcess.java` |

### com.sony.scalar.sysutil.ScalarProperties

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `ScalarProperties.getString("version.platform")` | Key | `String` | Gets platform version for PF < 2.0 compatibility checks | `shooting/CompositProcess.java` |
| `ScalarProperties.getString("mem.rawimage.size.in.mega.pixel")` | Key | `String` | Gets raw image megapixel for memory map selection | `shooting/CompositProcess.java` |
| `ScalarProperties.getString("device.memory")` | Key | `String` | Gets device memory identifier for memory map selection | `shooting/CompositProcess.java` |

### com.sony.scalar.sysutil.ScalarInput

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `ScalarInput.getKeyStatus(int)` | Key code | `KeyStatus` | Gets key status for housing detection | `LightGraffitiApp.java` |

### ExposureCompensationController (indirect CameraEx)

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `getInstance().getExposureCompensationStep()` | None | `float` | Gets EV compensation step size | `shooting/CompositProcess.java` |
| `getInstance().getExposureCompensationIndex()` | None | `int` | Gets current EV comp index (saved/restored) | `shooting/CompositProcess.java` |

### AELController (indirect CameraEx)

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `AELController.getInstance().cancelAELock()` | None | `void` | Cancels AE lock at start of each prepare phase | `shooting/CompositProcess.java` |

## 4. Image Processing Pipeline

1. **prepare()**: Initializes state holder, sets callback, enables next capture
2. **onPrepare() - 1st stage**: Gets PF version, sets `MemoryMapConfig.setAllocationPolicy(1)`, creates `CameraSequence.Options` with memory map file, saves auto review time, saves and zeroes EV compensation
3. **takePicture() - 1st exposure**: Sets EV comp to -3.0 (underexpose background), locks auto focus, switches to virtual media via `setRecordingMedia(AvindexStore.getVirtualMediaIds())`, calls `burstableTakePicture()` with 440ms delay
4. **process_1st()**: Develops raw via `DefaultDevelopFilter`, stores Yc_1 via `sequence.storeImage()`, resets EV comp to 0, cancels take picture
5. **takePicture() - 2nd exposure**: Normal exposure for light painting, calls `burstableTakePicture()` with delay
6. **process_2nd()**: Develops raw to Yc_3, stores image, copies Yc_3 to Yc_2 via `ScaleImageFilter`, runs SA DIFF filter (Yc_3 vs Yc_1 -> diff), runs SA LPF filter (smooth), generates miniature preview in DeviceBuffer
7. **Real-time preview**: `LGPreviewEffect.startPreviewEffect()` sets DSP as `sequence.setPreviewPlugin()`, starts preview sequence at 30fps showing composite of previous shots with live view
8. **takePicture() - 3rd exposure**: Optional fill shot, calls `burstableTakePicture()` directly
9. **process_3rd()**: Develops raw, runs SA COMP filter (composite DeviceBuffer + new image), stores final image, generates review thumbnails, calls `enableNextCapture(0)`

## 5. Button Handling

| Key Constant | Action |
|-------------|--------|
| `ISV_KEY_S1_1` | Half-press - enters EE state, starts autofocus |
| `ISV_KEY_S2` | Full press - starts exposure (pushed during capture = cancel or start 3rd shot) |
| `ISV_KEY_MENU` / `ISV_KEY_SK1` | Opens light graffiti settings (duration, mode) |
| `ISV_KEY_DELETE` / `ISV_KEY_SK2` | Cancel current operation / back |
| `ISV_KEY_FN` | Function button |
| `ISV_DIAL_1` / `ISV_DIAL_2` | Parameter adjustment |
| `ISV_KEY_PLAY` | Switch to playback |

## 6. Unique APIs

| API | Description |
|-----|-------------|
| `CameraSequence.setPreviewPlugin(DSP)` | Sets DSP as real-time preview processor - enables live composite view during light painting |
| `CameraSequence.startPreviewSequence(Options)` | Starts real-time preview with DSP plugin at specified frame rate (30fps) |
| `CameraSequence.stopPreviewSequence()` | Stops the real-time preview |
| `AvindexStore.getVirtualMediaIds()` | Gets virtual media IDs for temporary storage of 1st exposure |
| `Light.setState("LID_SELF_TIMER", boolean)` | Controls the self-timer LED indicator for visual feedback |
| `LGSAMixFilter` (5 filter modes) | DSP SA program with DIFF (0), LPF (1), COMP (2), DIFF_LPF (3), COMP_SFR (4) modes for multi-stage compositing |
| `ScaleImageFilter` | Image scale/copy filter used to duplicate YCbCr images between processing stages |
| `LGImageUtil.copyOptimizedImageToDeviceBuffer()` | Converts OptimizedImage to DeviceBuffer for DSP preview plugin input |
| `LGImageUtil.copyDeviceBufferToOptimizedImage()` | Converts DeviceBuffer back to OptimizedImage |
| `LGImageUtil.getSFRImage()` | Crops and scales image for preview/review display |
| Multi-stage shooting state machine | `LGStateHolder` with states: SHOOTING_1ST, EXPOSING_1ST, PROCESSING, SHOOTING_2ND, EXPOSING_2ND, SHOOTING_3RD_BEFORE_SHOOT, EXPOSING_3RD |
