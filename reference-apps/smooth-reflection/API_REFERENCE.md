# Smooth Reflection - API Reference

## 1. App Overview

| Field | Value |
|-------|-------|
| **Package** | `com.sony.imaging.app.smoothreflection` |
| **Purpose** | Long-exposure simulation via multi-frame averaging (2-256 shots) |
| **Description** | Simulates long-exposure photography by capturing 2 to 256 consecutive raw frames and averaging them at the raw data level using DSP hardware acceleration. Produces smooth water, light trails, and motion blur effects without actual long exposure. Features custom gamma tables, RGB matrix control, aperture adjustment, and white balance locking for consistent multi-frame capture. |

## 2. Architecture

### Main Activity
- Entry point managed via `BaseApp` framework
- State machine driven by `com.sony.imaging.app.fw.State` subclasses

### Key Classes

| Class | Path | Role |
|-------|------|------|
| `SaRawComp` | `shooting/SaRawComp.java` | DSP raw compositing engine - accumulates and averages raw frames |
| `SmoothReflectionCompositProcess` | `shooting/SmoothReflectionCompositProcess.java` | Multi-frame capture pipeline - handles burst capture, AWB lock, remote control mode |
| `CompositProcess` | `shooting/CompositProcess.java` | Simplified composite capture process |
| `SingleProcess` | `shooting/SingleProcess.java` | Single-shot capture process using burstableTakePicture |
| `SmoothReflectionForceSettingState` | `shooting/SmoothReflectionForceSettingState.java` | Forces camera settings: disables OIS, DRO, sets self-timer |
| `SmoothReflectionUtil` | `common/SmoothReflectionUtil.java` | Utility for gamma tables, RGB matrix, aperture adjustment, ND filter simulation |
| `SaUtil` | `common/SaUtil.java` | DSP helper utilities: memory address conversion, SA parameter building, execution |

## 3. Sony Scalar API Usage

### com.sony.scalar.hardware.DSP - Raw Averaging

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `DSP.createProcessor("sony-di-dsp")` | Processor name | `DSP` | Creates DSP hardware processor for raw frame averaging | `shooting/SaRawComp.java` |
| `dsp.setProgram(String)` | `libsa_smoothphotography.so` or `libsa_smoothphotography_avip.so` | `void` | Loads the multi-frame averaging SA program (AVIP variant for newer HW) | `shooting/SaRawComp.java` |
| `dsp.createBuffer(int)` | Size (based on max picture size) | `DeviceBuffer` | Allocates work buffers for raw frame accumulation | `shooting/SaRawComp.java` |
| `dsp.clearProgram()` | None | `void` | Clears loaded DSP program | `shooting/SaRawComp.java` |
| `dsp.release()` | None | `void` | Releases DSP processor | `shooting/SaRawComp.java` |

### com.sony.scalar.hardware.CameraSequence

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `sequence.storeImage(OptimizedImage, boolean)` | Image, release flag | `void` | Stores the final averaged image to media | `shooting/SmoothReflectionCompositProcess.java` |

### CameraSequence.RawData / RawDataInfo

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `raw.getRawDataInfo()` | None | `RawDataInfo` | Gets comprehensive raw sensor metadata for DSP processing | `shooting/SaRawComp.java` |

#### RawDataInfo Fields Used

| Field | Type | Description | Source File |
|-------|------|-------------|-------------|
| `info.canvasSizeX` | `int` | Raw sensor canvas width | `shooting/SaRawComp.java` |
| `info.canvasSizeY` | `int` | Raw sensor canvas height | `shooting/SaRawComp.java` |
| `info.marginOffsetX/Y` | `int` | Margin offset within canvas | `shooting/SaRawComp.java` |
| `info.marginSizeX/Y` | `int` | Margin dimensions | `shooting/SaRawComp.java` |
| `info.validOffsetX/Y` | `int` | Valid image data offset | `shooting/SaRawComp.java` |
| `info.validSizeX/Y` | `int` | Valid image dimensions | `shooting/SaRawComp.java` |
| `info.firstColor` | `int` | Bayer pattern first color | `shooting/SaRawComp.java` |
| `info.clpR/clpGr/clpGb/clpB` | `int` | Clipping levels per color channel | `shooting/SaRawComp.java` |
| `info.clpOfst` | `int` | Clipping offset | `shooting/SaRawComp.java` |
| `info.wbR/wbB` | `int` | White balance R/B coefficients | `shooting/SaRawComp.java` |
| `info.ddithRstmod/ddithOn` | `int` | Decompression dithering parameters | `shooting/SaRawComp.java` |
| `info.expBit` | `int` | Exposure bit depth | `shooting/SaRawComp.java` |
| `info.decompMode` | `int` | Raw decompression mode | `shooting/SaRawComp.java` |
| `info.dth0-3/dp0-3` | `int` | Dithering thresholds and parameters | `shooting/SaRawComp.java` |
| `info.dithRstmod/dithOn` | `int` | Compression dithering parameters | `shooting/SaRawComp.java` |
| `info.rndBit` | `int` | Rounding bit depth | `shooting/SaRawComp.java` |
| `info.compMode` | `int` | Raw compression mode | `shooting/SaRawComp.java` |
| `info.th0-3/p0-3` | `int` | Compression thresholds and parameters | `shooting/SaRawComp.java` |

### com.sony.scalar.hardware.CameraEx

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `camera.burstableTakePicture()` | None | `void` | Takes picture in burst mode for multi-frame sequence | `shooting/SmoothReflectionCompositProcess.java`, `shooting/CompositProcess.java`, `shooting/SingleProcess.java` |
| `camera.startSelfTimerShutter()` | None | `void` | Starts self-timer capture | `shooting/SingleProcess.java` |
| `camera.adjustAperture(int)` | Steps (positive/negative) | `void` | Adjusts lens aperture by relative steps for ND filter simulation | `common/SmoothReflectionUtil.java` |

### CameraEx.ParametersModifier - Capture Control

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `modifier.setRemoteControlMode(boolean)` | `true`/`false` | `void` | Enables remote control mode - suppresses shutter sound and review between frames | `shooting/SmoothReflectionCompositProcess.java` |
| `modifier.setAutoWhiteBalanceLock(boolean)` | `true`/`false` | `void` | Locks auto white balance across all frames for consistent color | `shooting/SmoothReflectionCompositProcess.java` |
| `modifier.setAntiHandBlurMode(String)` | `"off"` | `void` | Disables OIS for tripod-mounted multi-frame capture | `shooting/SmoothReflectionForceSettingState.java` |
| `modifier.setRGBMatrix(int[])` | 3x3 matrix or `null` | `void` | Applies/clears RGB color transformation matrix for ND filter color cast compensation | `common/SmoothReflectionUtil.java` |
| `modifier.getLongExposureNR()` | None | `boolean` | Checks if long exposure noise reduction is enabled (affects raw address calculation) | `shooting/SaRawComp.java` |

### CameraEx.ShutterSpeedInfo

| Field | Type | Description | Source File |
|-------|------|-------------|-------------|
| `info.currentShutterSpeed_n` | `float` | Shutter speed numerator | `shooting/SaRawComp.java` |
| `info.currentShutterSpeed_d` | `float` | Shutter speed denominator | `shooting/SaRawComp.java` |

### CameraEx - Extended Gamma Table

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `camera.createGammaTable()` | None | `CameraEx.GammaTable` | Creates custom gamma table for ND filter tone curve | `common/SmoothReflectionUtil.java` |
| `camera.setExtendedGammaTable(GammaTable)` | Table or null | `void` | Applies custom gamma to simulate ND filter darkness | `common/SmoothReflectionUtil.java` |

### com.sony.scalar.sysutil.ScalarProperties

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `ScalarProperties.getString("model.name")` | Key | `String` | Gets camera model name for model-specific raw offset calculations | `shooting/SaRawComp.java` |
| `ScalarProperties.getString("version.platform")` | Key | `String` | Gets platform version for AXI address masking | `common/SaUtil.java` |
| `ScalarProperties.getSupportedPictureSizes()` | None | `List<PictureSize>` | Gets all supported picture sizes to determine max buffer allocation | `shooting/SaRawComp.java` |

### ScalarProperties.PictureSize

| Field | Type | Description | Source File |
|-------|------|-------------|-------------|
| `size.width` | `int` | Picture width in pixels | `shooting/SaRawComp.java` |
| `size.height` | `int` | Picture height in pixels | `shooting/SaRawComp.java` |

### com.sony.scalar.hardware.DeviceBuffer / DeviceMemory

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `buffer.write(ByteBuffer)` | Parameter data | `void` | Writes SA parameters to hardware buffer | `common/SaUtil.java` |
| `buffer.release()` | None | `void` | Releases hardware buffer | `shooting/SaRawComp.java` |

## 4. Image Processing Pipeline

### Multi-Frame Capture Sequence
1. **Force Settings** - `SmoothReflectionForceSettingState`:
   - Disables anti-hand-blur (OIS) via `setAntiHandBlurMode("off")`
   - Disables DRO (Dynamic Range Optimizer) via `DROAutoHDRController.setValue("off")`
   - Restores self-timer setting from preferences
2. **AWB Lock** - `setAutoWhiteBalanceLock(true)` locks white balance for consistent color across all frames
3. **Remote Control Mode** - `setRemoteControlMode(true)` suppresses inter-frame shutter sounds and review screens
4. **Optional: Aperture Adjustment** - `adjustAperture(steps)` simulates ND filter by stopping down the lens
5. **Optional: Gamma Table** - Custom gamma table applied via `setExtendedGammaTable()` to darken exposure for ND simulation
6. **Optional: RGB Matrix** - `setRGBMatrix(int[])` compensates for color casts introduced by ND simulation

### Per-Frame DSP Processing
7. **Burst Capture** - `burstableTakePicture()` captures each raw frame
8. **Raw Data Callback** - `onShutterSequence()` receives `CameraSequence.RawData` for each frame
9. **DSP Raw Averaging** - `SaRawComp.execute()`:
   - Gets `RawDataInfo` with full sensor metadata (canvas size, margins, valid area, Bayer pattern, WB, compression)
   - Builds 256-byte SA parameter buffer with:
     - Command: 6 (first frame = initialize) or 4 (subsequent = accumulate)
     - Raw memory address (with model-specific BFNR offset correction)
     - Work buffer address
     - Frame count
     - Full RawDataInfo fields
   - Executes DSP program which accumulates raw frames in work buffers
10. **Iteration** - Steps 7-9 repeat for 2-256 frames as configured

### Final Processing
11. **Development** - `DefaultDevelopFilter` converts averaged raw data to OptimizedImage
12. **Storage** - `sequence.storeImage()` saves the final image
13. **Cleanup** - Unlock AWB, disable remote control mode, release DSP resources

### Model-Specific Raw Address Offsets
The app handles BFNR (Before Frame Noise Reduction) raw address offsets differently per camera model:

| Model | Offset Calculation | Condition |
|-------|--------------------|-----------|
| DSC-RX100M3 | `(canvasSizeX + 64) * 6` | Long exposure NR + SS >= 8s |
| ILCE-5000 | `canvasSizeX * 8` | Long exposure NR + SS >= 1s |
| DSC-HX400V | `canvasSizeX * 6` | Long exposure NR + SS >= 8s |
| DSC-HX60V | `canvasSizeX * 6` | Long exposure NR + SS >= 8s |

## 5. Button Handling

Button handling is managed by the shared `BaseApp` framework via `ScalarInput` key events:

| Key Constant | Action |
|-------------|--------|
| `ISV_KEY_S1_1` | Half-press shutter - autofocus |
| `ISV_KEY_S2` | Full shutter press - start multi-frame capture sequence |
| `ISV_KEY_UP/DOWN` | Adjust number of frames (2/4/8/16/32/64/128/256) |
| `ISV_KEY_LEFT/RIGHT` | Navigate settings |
| `ISV_KEY_ENTER` | Confirm frame count / start capture |
| `ISV_KEY_DELETE` | Cancel / abort capture sequence |
| `ISV_KEY_MENU` | Open menu |
| `ISV_DIAL_1` | Adjust frame count |
| `ISV_DIAL_2` | Adjust capture interval |

## 6. Unique APIs

### APIs unique to smooth-reflection (not commonly used by other reference apps):

| API | Description |
|-----|-------------|
| **`CameraEx.ParametersModifier.setRemoteControlMode(true)`** | Suppresses shutter sound and review screen between burst frames for seamless multi-frame capture |
| **`CameraEx.ParametersModifier.setAutoWhiteBalanceLock(true/false)`** | Locks/unlocks AWB across the entire capture sequence for color consistency |
| **`CameraEx.adjustAperture(int)`** | Programmatic aperture adjustment by relative steps - simulates ND filter darkening |
| **`ScalarProperties.getSupportedPictureSizes()`** | Queries all supported picture sizes to allocate maximum-size DSP work buffers |
| **`ScalarProperties.getString("model.name")`** | Camera model identification for model-specific raw address offset calculations |
| **`CameraEx.ShutterSpeedInfo`** | Shutter speed data structure (numerator/denominator) used for BFNR offset decisions |
| **`ParametersModifier.getLongExposureNR()`** | Queries long exposure noise reduction state for raw data layout adjustment |
| **DSP raw frame averaging** (`libsa_smoothphotography.so`) | Unique SA program that accumulates 2-256 raw frames with weighted averaging |
| **AVIP variant** (`libsa_smoothphotography_avip.so`) | Newer hardware variant of the averaging program with different parameter layout |
| **DSP command protocol** | Frame 0 uses command=6 (initialize accumulator), subsequent frames use command=4 (accumulate) |
| **Full RawDataInfo consumption** | Only app that uses the complete set of ~40 RawDataInfo fields including compression/decompression parameters |
| **Model-specific BFNR raw offsets** | Camera-model-aware raw memory address adjustments for noise reduction frame offsets |
