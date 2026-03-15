# Graduated Filter - API Reference

## 1. App Overview

| Field | Value |
|-------|-------|
| **Package** | `com.sony.imaging.app.graduatedfilter` |
| **Purpose** | Graduated ND filter simulation using DSP-accelerated multi-frame raw compositing |
| **Description** | Simulates a physical graduated neutral density filter by capturing two exposures with different camera settings (shutter speed, aperture, ISO, white balance, exposure compensation) and blending them using DSP hardware acceleration. The first exposure captures the bright area (e.g., sky), the second captures the dark area (e.g., foreground), and they are composited at the raw data level via a custom DSP program (NDSA). Supports filter-base or standard-base shooting order, RAW and RAW+JPEG output, image adjustment post-processing, and self-timer. |

## 2. Architecture

### Main Activity
- `GraduatedFilterApp` extends `BaseApp`, registered as `singleInstance` launcher activity
- Version: 1.1105
- Application class: `com.sony.imaging.app.graduatedfilter.common.AppContext`
- Registers 8 custom camera controllers overriding default behavior (AEL, ExposureMode, WhiteBalance, CreativeStyle, ExposureCompensation, SilentShutter, FocusMode, Metering)

### Key Classes

| Class | Path | Role |
|-------|------|------|
| `GraduatedFilterApp` | `GraduatedFilterApp.java` | Main activity - registers controllers, initializes effect parameters |
| `GFCompositProcess` | `shooting/GFCompositProcess.java` | Core composite engine - manages 2-shot capture, DSP blending, raw handling |
| `GFExecutorCreator` | `shooting/GFExecutorCreator.java` | Creates special executor, sets default camera settings (single drive, face detection off) |
| `GFAdapterImpl` | `shooting/GFAdapterImpl.java` | Custom imaging adapter for CameraSequence integration |
| `GFEffectParameters` | `shooting/GFEffectParameters.java` | Manages graduated filter parameters (SS, ISO, aperture, WB per exposure) |
| `NDSA` | `sa/NDSA.java` | DSP signal-analysis wrapper for raw ND compositing |
| `ChangeAperture` | `shooting/ChangeAperture.java` | Asynchronous aperture change with callback |
| `ChangeSs` | `shooting/ChangeSs.java` | Asynchronous shutter speed change with callback |
| `ChangeIso` | `shooting/ChangeIso.java` | Asynchronous ISO change with callback |
| `ChangeWhiteBalance` | `shooting/ChangeWhiteBalance.java` | Asynchronous white balance change with callback |
| `GFImageAdjustmentUtil` | `common/GFImageAdjustmentUtil.java` | Post-capture image adjustment processing |
| `GFRawAPIHandling` | `common/GFRawAPIHandling.java` | Raw data extraction and physical address management |
| `SaUtil` | `common/SaUtil.java` | DSP utility - AVIP detection, address conversion |

## 3. Sony Scalar API Usage

### com.sony.scalar.hardware.CameraEx

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `cameraEx.burstableTakePicture()` | None | `void` | Takes picture in burstable mode for 2-shot composite sequence | `shooting/GFCompositProcess.java` |
| `cameraEx.cancelTakePicture()` | None | `void` | Cancels current take between 1st and 2nd exposures | `shooting/GFCompositProcess.java` |
| `cameraEx.startSelfTimerShutter()` | None | `void` | Starts self-timer shutter | `shooting/GFCompositProcess.java` |
| `cameraEx.getNormalCamera()` | None | `Camera` | Gets underlying Camera for direct parameter access | `shooting/GFCompositProcess.java` |
| `cameraEx.setRecordingMedia(int[], RecordingMediaChangeCallback)` | Media IDs, callback | `void` | Sets recording media with change notification | `shooting/GFCompositProcess.java` |

### CameraEx.ParametersModifier

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `modifier.isAutoWhiteBalanceLockSupported()` | None | `boolean` | Checks AWB lock support | `shooting/GFCompositProcess.java` |
| `modifier.setAutoWhiteBalanceLock(boolean)` | `true`/`false` | `void` | Locks AWB between composite shots when both use auto WB | `shooting/GFCompositProcess.java` |
| `modifier.getRemoteControlMode()` | None | `boolean` | Gets remote control mode state | `shooting/GFCompositProcess.java` |

### CameraEx.AutoPictureReviewControl

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `new CameraEx.AutoPictureReviewControl()` | None | `AutoPictureReviewControl` | Creates review control | `shooting/GFCompositProcess.java` |
| `control.getPictureReviewTime()` | None | `int` | Gets current auto review time | `shooting/GFCompositProcess.java` |
| `control.setPictureReviewTime(int)` | Time in ms (0=off) | `void` | Disables review between composite shots, restores after | `shooting/GFCompositProcess.java` |

### CameraEx.RecordingMediaChangeCallback

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `onRecordingMediaChange(CameraEx)` | CameraEx | `void` | Callback when recording media change completes | `shooting/GFCompositProcess.java` |

### com.sony.scalar.hardware.CameraSequence

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `sequence.storeImage(OptimizedImage, boolean)` | Image, release flag | `void` | Stores composite image to media (called for both individual and final images) | `shooting/GFCompositProcess.java` |

### CameraSequence.Options

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `opt.setOption("MEMORY_MAP_FILE", String)` | File path | `void` | Sets memory map file for special sequence (device-specific) | `shooting/GFCompositProcess.java` |
| `opt.setOption("EXPOSURE_COUNT", int)` | Count (1) | `void` | Sets number of exposures per sequence step | `shooting/GFCompositProcess.java` |
| `opt.setOption("RECORD_COUNT", int)` | Count (1 or 2) | `void` | Sets number of images to record (2 for saving both originals) | `shooting/GFCompositProcess.java` |
| `opt.setOption("INTERIM_PRE_REVIEW_ENABLED", boolean)` | `false` | `void` | Disables interim preview between shots | `shooting/GFCompositProcess.java` |
| `opt.setOption("AUTO_RELEASE_LOCK_ENABLED", boolean)` | `false` | `void` | Disables automatic lock release between shots | `shooting/GFCompositProcess.java` |
| `opt.setOption("DETECTION_OFF", boolean)` | `false` | `void` | Controls detection feature during sequence | `shooting/GFCompositProcess.java` |

### CameraSequence.RawData

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `raw.release()` | None | `void` | Releases raw data buffer | `shooting/GFCompositProcess.java` |

### CameraSequence.DefaultDevelopFilter

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `new CameraSequence.DefaultDevelopFilter()` | None | `DefaultDevelopFilter` | Creates default RAW-to-YCbCr development filter | `shooting/GFCompositProcess.java` |
| `filter.setSource(RawData, boolean)` | Raw data, flag | `void` | Sets raw data source for development | `shooting/GFCompositProcess.java` |
| `filter.execute()` | None | `void` | Executes RAW development to produce OptimizedImage | `shooting/GFCompositProcess.java` |
| `filter.getOutput()` | None | `OptimizedImage` | Gets developed YCbCr image | `shooting/GFCompositProcess.java` |
| `filter.clearSources()` | None | `void` | Clears source references | `shooting/GFCompositProcess.java` |
| `filter.release()` | None | `void` | Releases filter resources | `shooting/GFCompositProcess.java` |

### CameraSequence.YcDataInfo

| Field | Type | Description | Source File |
|-------|------|-------------|-------------|
| `ycInfo.wbB` | `int` | White balance blue coefficient | `shooting/GFCompositProcess.java` |
| `ycInfo.wbR` | `int` | White balance red coefficient | `shooting/GFCompositProcess.java` |

### com.sony.scalar.hardware.DSP

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `DSP.createProcessor("sony-di-dsp")` | Processor name | `DSP` | Creates DSP processor for ND raw compositing | `shooting/GFCompositProcess.java` |
| `dsp.createBuffer(int)` | Size in bytes | `DeviceBuffer` | Allocates device buffer for raw data (sized by canvas dimensions or 16032768 for AVIP) | `shooting/GFCompositProcess.java` |
| `dsp.release()` | None | `void` | Releases DSP processor | `shooting/GFCompositProcess.java` |

### com.sony.scalar.hardware.DeviceBuffer

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `buffer.release()` | None | `void` | Releases device buffer memory | `shooting/GFCompositProcess.java` |

### com.sony.scalar.hardware.MemoryMapConfig

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `MemoryMapConfig.setAllocationPolicy(int)` | Policy (1) | `void` | Sets memory allocation policy for special sequence | `shooting/GFCompositProcess.java` |

### com.sony.scalar.graphics.OptimizedImage

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `img.release()` | None | `void` | Releases optimized image memory | `shooting/GFCompositProcess.java` |

### com.sony.scalar.sysutil.ScalarInput

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `ScalarInput.getKeyStatus(int)` | Key code | `KeyStatus` | Gets key status for underwater housing detection | `GraduatedFilterApp.java` |

### com.sony.scalar.sysutil.ScalarProperties

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `ScalarProperties.getString("mem.rawimage.size.in.mega.pixel")` | Key | `String` | Gets raw image megapixel count for memory map file selection | `shooting/GFCompositProcess.java` |
| `ScalarProperties.getString("device.memory")` | Key | `String` | Gets device memory identifier for memory map file selection | `shooting/GFCompositProcess.java` |

### AELController (indirect CameraEx)

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `AELController.getInstance().holdAELock(boolean)` | `true`/`false` | `void` | Locks AE during composite sequence (if not already locked by user) | `shooting/GFCompositProcess.java` |
| `AELController.getInstance().getAELockButtonState()` | None | `boolean` | Checks if user manually engaged AE lock | `shooting/GFCompositProcess.java` |

## 4. Image Processing Pipeline

1. **prepare()**: Initializes camera, checks RAW/RAW+JPEG quality, detects remote control mode, saves auto review time, checks self-timer, sets physical addresses for AVIP
2. **preTakePicture()**: Determines which parameters need changing (SS, aperture per exposure mode), sets memory map configuration, creates `CameraSequence.Options` with `MEMORY_MAP_FILE`, `EXPOSURE_COUNT=1`, `RECORD_COUNT=1`, creates DSP via `DSP.createProcessor("sony-di-dsp")`, disables auto review
3. **takePicture() - 1st exposure**: Sets camera to 1st exposure settings (WB, aperture, SS, ISO, EV comp via async callbacks), waits for all settings to complete, calls `burstableTakePicture()` with AE lock
4. **onShutterSequence() - 1st raw**: Stores 1st raw image, copies raw data to DSP buffer via `NDSA.copy()`, cancels take picture, sets 2nd exposure options (`RECORD_COUNT=2`), applies 2nd camera settings
5. **takePicture() - 2nd exposure**: Automatic continuation - calls `burstableTakePicture()` with new settings
6. **onShutterSequence() - 2nd raw**: Gets 2nd raw data info, creates DSP buffer, opens NDSA compositor with both raws, executes DSP blend via `NDSA.execute()`, stores composite image
7. **Post-processing**: If image adjustment enabled, shows adjustment UI; otherwise stores final composite and restores auto review time

## 5. Button Handling

| Key Constant | Action |
|-------------|--------|
| `ISV_KEY_S1_1` | Half-press shutter - enters EE state with filter preview |
| `ISV_KEY_S2` | Full shutter press - starts 2-shot composite sequence |
| `ISV_KEY_MENU` / `ISV_KEY_SK1` | Opens graduated filter settings menu |
| `ISV_KEY_DELETE` / `ISV_KEY_SK2` | Cancel/back |
| `ISV_KEY_FN` | Function button for quick filter parameter access |
| `ISV_DIAL_1` / `ISV_DIAL_2` | Adjust filter parameters (effect strength, position) |
| `ISV_KEY_PLAY` | Switch to playback |

## 6. Unique APIs

| API | Description |
|-----|-------------|
| `NDSA.open(DSP, DeviceBuffer, RawDataInfo, DeviceBuffer, RawDataInfo, RawData)` | Opens the graduated ND DSP compositor with both raw buffers and their metadata |
| `NDSA.execute()` | Executes the DSP raw-level ND blend between two exposures |
| `NDSA.copy(DSP, DeviceBuffer, RawDataInfo, RawData)` | Copies raw data to a DSP device buffer for later compositing |
| `CameraSequence.DefaultDevelopFilter` | Develops raw data to YCbCr OptimizedImage - used for WB coefficient extraction |
| `CameraSequence.YcDataInfo` | YCbCr metadata with WB coefficients (wbB, wbR) used to match WB between exposures |
| `MemoryMapConfig.setAllocationPolicy(1)` | Sets memory allocation policy for the special capture sequence |
| `GFRawAPIHandling.setPhicalAddress()` | Sets physical memory addresses for AVIP-class hardware DSP access |
| `GFRawAPIHandling.getGFRawDataInfo(RawData)` | Extracts raw data info into app-specific structure for DSP processing |
| `ChangeAperture/ChangeSs/ChangeIso/ChangeWhiteBalance` | Async camera parameter change classes with callbacks - used for synchronized 2-shot parameter switching |
| `SaUtil.isAVIP()` | Detects AVIP-class hardware for selecting correct DSP buffer sizes and addressing |
