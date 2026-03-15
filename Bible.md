# Sony Scalar Native API Bible
## Complete Reference for Sony Camera Android Development (ILCE-6500 / A6500)

> This document catalogs every known `com.sony.scalar.*` native API discovered through reverse-engineering Sony's PlayMemories Camera Apps (7 decompiled reference apps) and the OpenMemories-Framework stubs (323 Jasmin API stub files). These APIs are undocumented and proprietary, running on Sony cameras with custom Android 4.1-4.4.

**Sources**: 7 decompiled Sony apps (digital-filter, double-exposure, live-view-grading, photo-retouch, picture-effect-plus, portrait-beauty, smooth-reflection) + OpenMemories-Framework stubs (~323 .j files)

---

# Table of Contents

1. [Hardware APIs](#1-hardware-apis) - CameraEx, CameraSequence, DSP, DeviceBuffer, DisplayManager, Indicators
2. [Graphics APIs](#2-graphics-apis) - OptimizedImage, ImageFilters, JpegExporter, ImageAnalyzer
3. [Widget APIs](#3-widget-apis) - OptimizedImageView
4. [Media APIs](#4-media-apis) - AudioManager, MediaRecorder, MediaPlayer, AvindexContentInfo
5. [Metadata APIs](#5-metadata-apis) - TakenPhotoInfo, FaceInfo, Histogram
6. [Provider APIs](#6-provider-apis) - AvindexStore
7. [System Utility APIs](#7-system-utility-apis) - ScalarInput, ScalarProperties, TimeUtil
8. [Didep APIs](#8-didep-apis) - Settings, Power, Temperature, GPS, Bluetooth, Caution
9. [Networking APIs](#9-networking-apis) - ScalarWifiInfo, SsdpDevice, WiFi Direct
10. [Android System Extensions](#10-android-system-extensions) - DAConnectionManager
11. [Base App Framework](#11-base-app-framework) - Architecture pattern
12. [Dual-Device Pattern](#12-dual-device-pattern) - Sony vs Emulator stubs
13. [Reference App Details](#13-reference-app-details)

---

# 1. Hardware APIs

## 1.1 CameraEx
**Package**: `com.sony.scalar.hardware.CameraEx` | **Used by**: ALL 7 apps

The primary camera control class that wraps Sony's proprietary camera hardware. Extends the standard `android.hardware.Camera` with Sony-specific features like burst capture, direct shutter mode, gamma table LUTs, color select for partial color effects, and hardware-accelerated focus/aperture/shutter control. Every Sony camera app must open and manage a CameraEx instance.

### Opening the Camera

The camera must be opened with `OpenOptions` specifying preview mode, media target, and recording type. Always open on the main thread and release in `onPause()`.

```java
// Standard camera open pattern (used by ALL 7 reference apps)
CameraEx.OpenOptions opts = new CameraEx.OpenOptions();
opts.setPreview(true);              // Enable live preview on LCD/EVF
opts.setInheritSetting(true);       // Inherit user's current camera settings
opts.setRecordingMode(0);           // 0 = still photo mode, 1 = movie mode
opts.setTargetMedia(AvindexStore.getExternalMediaIds()[0]); // Save to SD card
CameraEx cameraEx = CameraEx.open(0, opts);

// Get the underlying Android Camera for compatibility
Camera normalCamera = cameraEx.getNormalCamera();

// Reopen with different settings (e.g., switch to movie mode)
CameraEx.OpenOptions newOpts = new CameraEx.OpenOptions();
newOpts.setRecordingMode(1);
cameraEx.reopen(newOpts, new CameraEx.OpenCallback() {
    public void onReopened(CameraEx camera) {
        // Camera is now in movie mode
    }
});
```

### Core Methods

| Method | Return | Description |
|--------|--------|-------------|
| `open(int cameraId, OpenOptions opts)` | `CameraEx` | Opens the camera hardware. Camera ID is always 0 on Sony cameras (single camera). Must be called from main thread. |
| `getNormalCamera()` | `Camera` | Returns the underlying `android.hardware.Camera` instance for standard Android camera operations (preview, parameters). |
| `release()` | `void` | Releases all camera resources. **MUST** be called in `onPause()` or when done. Failure to release will lock the camera hardware. |
| `reopen(OpenOptions, OpenCallback)` | `void` | Reopens the camera with new settings without full release/open cycle. Callback fires when ready. |
| `burstableTakePicture()` | `void` | Captures a photo in burst-capable mode. Used by all apps for still capture. Does not block — result delivered via `ShutterListener` and `CameraSequence` callbacks. |
| `cancelTakePicture()` | `void` | Cancels an in-progress capture operation. |
| `startDirectShutter()` | `void` | Starts direct shutter mode for immediate capture without the normal shutter sequence. Used by picture-effect-plus for Toy Camera mode. |
| `stopDirectShutter(DirectShutterStoppedCallback)` | `void` | Stops direct shutter mode. Callback fires when stopped. |
| `startOneShotFocusDrive()` | `void` | Triggers a single autofocus operation. Focus result delivered via `FocusDriveListener`. |
| `startFocusHold()` / `stopFocusHold()` | `void` | Locks/unlocks the current focus position. Useful for recomposing after focus. |
| `incrementShutterSpeed()` / `decrementShutterSpeed()` | `void` | Steps shutter speed up/down by one stop. Changes delivered via `ShutterSpeedChangeListener`. |
| `incrementAperture()` / `decrementAperture()` | `void` | Steps aperture up/down by one stop. Changes delivered via `ApertureChangeListener`. |
| `adjustAperture(int steps)` | `void` | Adjusts lens aperture by a relative number of steps (positive = stop down, negative = open up). Used by smooth-reflection to simulate ND filter darkening. |
| `startSelfTimerShutter()` / `cancelSelfTimerShutter()` | `void` | Starts/cancels self-timer capture sequence. Timer duration comes from camera settings. |
| `setPreviewMagnification(int factor, int x, int y)` / `stopPreviewMagnification()` | `void` | Zooms into the live preview at the specified point for manual focus assistance. |
| `getLensInfo()` | `LensInfo` | Returns current lens information (model name, focal length, lens type). |
| `getFocusAreaInfos()` | `FocusAreaInfos` | Returns the current AF area configuration and focus point positions. |
| `getExternalFlashInfo()` | `ExternalFlashInfo` | Returns external flash status (HSS, red-eye reduction, wireless, AF light). |
| `startMovieRec()` / `stopMovieRec()` | `void` | Starts/stops movie recording. Camera must be opened with `setRecordingMode(1)`. |

### Color Select API (picture-effect-plus)

Used for the "Part Color" effect — extracts specific colors from the scene while desaturating everything else.

```java
// Sample a color from the live preview at screen coordinates
CameraEx.SelectedColor color = cameraEx.getPreviewDisplayColor(320, 240);

// Assign the sampled color to channel 0 (supports 2 independent channels)
cameraEx.setColorSelectToChannel(0, color);

// Enable color extraction mode with channel 0 active (bitmask: 1 = ch0, 2 = ch1, 3 = both)
CameraEx.ParametersModifier modifier = cameraEx.createParametersModifier();
modifier.setColorSelectMode("extract", 1);  // "extract" enables, "off" disables
```

| Method | Return | Description |
|--------|--------|-------------|
| `getPreviewDisplayColor(int x, int y)` | `SelectedColor` | Samples the color at screen coordinates (x, y) from the live preview. Returns a `SelectedColor` with YCbCr components. Used for color picking in Part Color mode. |
| `setColorSelectToChannel(int channel, SelectedColor color)` | `void` | Assigns a sampled color to one of 2 hardware color selection channels (0 or 1). The hardware will isolate this color during capture/preview. |

### Gamma Table API (live-view-grading, smooth-reflection)

Custom gamma tables allow complete control over the tone curve applied to images. Used for color grading presets and ND filter simulation.

```java
// Create and apply a custom gamma table from CSV data
CameraEx.GammaTable table = cameraEx.createGammaTable();
table.setPictureEffectGammaForceOff(true);  // Override camera's built-in effect gamma

// Write 1024 short values (the tone curve) from an InputStream
InputStream csvStream = getAssets().open("std_vivid.csv");
table.write(csvStream);  // Reads 1024 x 16-bit values
csvStream.close();

cameraEx.setExtendedGammaTable(table);  // Apply to live view + capture
table.release();

// Later: clear custom gamma to restore default
cameraEx.setExtendedGammaTable(null);
```

| Method | Return | Description |
|--------|--------|-------------|
| `createGammaTable()` | `GammaTable` | Creates a new empty gamma table object. The table extends `DeviceBuffer` and holds 1024 16-bit values defining the tone curve. |
| `setExtendedGammaTable(GammaTable)` | `void` | Applies a custom gamma table to the entire image pipeline (live view + capture). Pass `null` to restore the default camera gamma. Takes effect immediately on the live view. |

### Inner Data Classes

| Class | Fields | Description |
|-------|--------|-------------|
| `LensInfo` | `currentFocalLength int`, `model String`, `type int` | Current lens identification. Focal length in mm, model string (e.g., "E PZ 16-50mm F3.5-5.6 OSS"). |
| `ZoomInfo` | `currentPosition int`, `maxPosition int` | Zoom lens position (0 to max). Only meaningful for power zoom lenses. |
| `ApertureInfo` | `currentAperture int` | Current aperture value. Encoded as f-number × 100 (e.g., f/2.8 = 280). |
| `ShutterSpeedInfo` | `currentShutterSpeed int`, `currentShutterSpeed_n float`, `currentShutterSpeed_d float` | Current shutter speed. Integer form is encoded value; n/d form is the fraction (e.g., n=1, d=60 means 1/60s). |
| `FocusPosition` | `currentPosition int`, `maxPosition int` | Manual focus position (0 to max). |
| `SelectedColor` | `Cb int`, `Cr int`, `Phase int`, `Range int`, `Saturation int`, `Y int` | Color sampled from preview in YCbCr color space. Used with `setColorSelectToChannel()` for Part Color effects. |
| `GammaTable` | extends `DeviceBuffer` | Custom tone curve (1024 × 16-bit values). Methods: `write(InputStream)`, `isPictureEffectGammaForceOff()`, `setPictureEffectGammaForceOff(boolean)`, `release()`. |
| `ReviewInfo` | `hist Histogram`, `photo TakenPhotoInfo` | Post-capture review data combining histogram and EXIF metadata. |
| `ExternalFlashInfo` | `afLightAutoStatus int`, `hssStatus int`, `redEyeReductionStatus int`, `wirelessStatus int` | External flash feature status flags. |
| `StoreImageInfo` | `capturedDate int[]`, `directoryName String`, `fileName String`, `fileId int`, `mediaId String` | Information about a stored image after capture completes. |
| `FocusAreaRectInfo` | `left int`, `top int`, `right int`, `bottom int`, `state int` | A single focus area rectangle with its current state (focused, searching, etc.). |
| `CustomWhiteBalanceInfo` | `colorCompensation int`, `colorTemperature int`, `lightBalance int` | Custom white balance settings for manual WB mode. |
| `TrackingFocusInfo` | Constants: `DETECTED`, `SCANNING`, `OFF`, `TRACK_ON`, `TRACK_OFF` | Tracking AF status. |
| `MeteringModeInfo` | `spotX int`, `spotY int` | Spot metering position coordinates. |

### Listener Interfaces (~39 total)

All listeners follow the pattern `onChanged(DataType, CameraEx)`. Register via `cameraEx.setXxxListener(listener)`.

```java
// Example: Listen for shutter speed changes
cameraEx.setShutterSpeedChangeListener(new CameraEx.ShutterSpeedChangeListener() {
    public void onChanged(CameraEx.ShutterSpeedInfo info, CameraEx camera) {
        int speed = info.currentShutterSpeed;
        // Update UI with new shutter speed
    }
});

// Example: Listen for capture completion
cameraEx.setOnCaptureStatusListener(new CameraEx.OnCaptureStatusListener() {
    public void onChanged(int status, CameraEx camera) {
        if (status == 0) {      // COMPLETE
            // Capture succeeded
        } else if (status == 1) { // ERROR
            // Capture failed
        } else if (status == 2) { // CANCEL
            // Capture was cancelled
        }
    }
});
```

| Interface | Signature | Description |
|-----------|-----------|-------------|
| `ShutterListener` | `onShutter(int, CameraEx)` | Fires when shutter actuates. |
| `FocusDriveListener` | `onChanged(FocusPosition, CameraEx)` | AF position changed. |
| `OnCaptureStatusListener` | `onChanged(int, CameraEx)` | Capture status: COMPLETE=0, ERROR=1, CANCEL=2. |
| `FocusAreaListener` | `onChanged(FocusAreaInfos, CameraEx)` | Focus area configuration changed. |
| `PreviewAnalizeListener` | `onChanged(AnalizedData, CameraEx)` | Preview analysis data (histogram, etc.). |
| `SettingChangedListener` | `onChanged(CameraEx)` | Any camera setting was modified. |
| `FaceDetectionListener` | `onChanged(FaceInfo[], CameraEx)` | Real-time face detection results during preview. |
| `StoreImageCompleteListener` | `onChanged(StoreImageInfo, CameraEx)` | Image has been stored to SD card. |
| `PictureReviewInfoListener` | `onChanged(ReviewInfo, CameraEx)` | Post-capture review data available. |
| `ApertureChangeListener` | `onChanged(ApertureInfo, CameraEx)` | Aperture value changed. |
| `ShutterSpeedChangeListener` | `onChanged(ShutterSpeedInfo, CameraEx)` | Shutter speed changed. |
| `AutoISOSensitivityListener` | `onChanged(int, CameraEx)` | Auto ISO value changed (in auto ISO mode). |
| `ZoomChangeListener` | `onChanged(ZoomInfo, CameraEx)` | Zoom position changed (power zoom lenses). |
| `TrackingFocusListener` | `onChanged(TrackingFocusInfo, CameraEx)` | Tracking AF status changed. |
| `FlashChargingStateListener` | `onChanged(boolean, CameraEx)` | Flash charging state (true = charging). |
| `JpegListener` | `onJpeg(byte[], CameraEx)` | Raw JPEG data available from capture. |
| ... and ~22 more | | Equipment, Error, Preview, IntervalRec, NDFilter, MotionShot, ProgramLine, FlashEmitting, FocalLength, PowerZoom, etc. |

### Callback Interfaces

| Interface | Signature | Description |
|-----------|-----------|-------------|
| `EquipmentCallback` | `onEquipmentChange(int type, int status, CameraEx)` | Lens/flash attach/detach. type: DEVICE_TYPE_LENS=1, INTERNAL_FLASH=2, EXTERNAL_FLASH=3. |
| `ErrorCallback` | `onError(int errorCode, CameraEx)` | Camera error occurred. |
| `OpenCallback` | `onReopened(CameraEx)` | Camera reopened after `reopen()` call. |
| `DirectShutterStoppedCallback` | `onShutterStopped(CameraEx)` | Direct shutter mode has stopped. |

---

## 1.2 CameraEx.ParametersModifier
**~1727 lines** - Comprehensive getter/setter interface for every camera parameter. Used by ALL 7 apps.

Create via `cameraEx.createParametersModifier()`. After modifying parameters, call `camera.getNormalCamera().setParameters(params)` to apply. Every setter has a corresponding getter, and every parameter has a `getSupported*()` method returning `List<String>` of valid values.

### Constants

```java
// AE Lock
ParametersModifier.AE_LOCK_ON = "locked";
ParametersModifier.AE_LOCK_OFF = "unlocked";

// Focus Modes
ParametersModifier.FOCUS_MODE_AUTO = "auto";
ParametersModifier.FOCUS_MODE_CONTINUOUS = "continuous-picture";
ParametersModifier.FOCUS_MODE_DMF = "dmf";       // Direct Manual Focus
ParametersModifier.FOCUS_MODE_MANUAL = "manual";

// Flash Modes
ParametersModifier.FLASH_MODE_AUTO = "auto";
ParametersModifier.FLASH_MODE_OFF = "off";
ParametersModifier.FLASH_MODE_ON = "on";

// White Balance
ParametersModifier.WHITE_BALANCE_AUTO = "auto";
```

### Standard Camera Parameters

| Get / Set | Type | Description |
|-----------|------|-------------|
| `getISOSensitivity()` / `setISOSensitivity(int)` | `int` | ISO sensitivity value (e.g., 100, 200, 400, 800, 1600, 3200, 6400, 12800, 25600). Higher values increase light sensitivity but add noise. |
| `getShutterSpeed()` / `setShutterSpeed(int)` | `int` | Shutter speed as encoded integer. Use `ShutterSpeedInfo` for human-readable numerator/denominator. |
| `getAperture()` / `setAperture(int)` | `int` | Aperture f-number encoded as value × 100 (e.g., f/2.8 = 280, f/5.6 = 560). |
| `getExposureCompensation()` / `setExposureCompensation(int)` | `int` | Exposure compensation in steps (typically -15 to +15 in 1/3 EV increments). |
| `getSceneMode()` / `setSceneMode(String)` | `String` | Scene mode preset (e.g., "auto", "portrait", "landscape", "night"). |
| `getWhiteBalance()` / `setWhiteBalance(String)` | `String` | White balance mode (e.g., "auto", "daylight", "cloudy", "tungsten", "fluorescent"). |
| `getFlashMode()` / `setFlashMode(String)` | `String` | Flash mode ("auto", "off", "on", "slow-sync", "rear-sync"). |
| `getFocusMode()` / `setFocusMode(String)` | `String` | Autofocus mode ("auto", "continuous-picture", "dmf", "manual"). |
| `getDriveMode()` / `setDriveMode(String)` | `String` | Drive mode (single shot, continuous, bracket, etc.). |
| `getCreativeStyle()` / `setCreativeStyle(String)` | `String` | Creative style preset (Standard, Vivid, Portrait, Landscape, etc.). |
| `getPictureEffect()` / `setPictureEffect(String)` | `String` | Picture effect mode ("off", "toy_camera", "pop_color", "posterization", "retro_photo", "soft_high_key", "part_color", "high_contrast_mono", "miniature"). |
| `getImageQuality()` / `setImageQuality(String)` | `String` | JPEG quality level. |
| `getImageSize()` / `setImageSize(String)` | `String` | Image resolution size. |
| `getAspectRatio()` / `setAspectRatio(String)` | `String` | Image aspect ratio ("3:2", "16:9", "4:3", "1:1"). |
| `isSilentShutterMode()` / `setSilentShutterMode(boolean)` | `boolean` | Electronic shutter mode (no mechanical shutter sound). |
| `getColorTemperature()` / `setColorTemperature(String)` | `String` | Manual color temperature in Kelvin when WB is set to color temp mode. |
| `getMeteringMode()` / `setMeteringMode(String)` | `String` | Metering mode ("multi", "center", "spot"). |
| `getSteadyShot()` / `setSteadyShot(String)` | `String` | In-body image stabilization (IBIS) mode. |
| `getContrastLevel()` / `setContrastLevel(String)` | `String` | Contrast adjustment level within creative style. |
| `getSaturationLevel()` / `setSaturationLevel(String)` | `String` | Saturation adjustment level within creative style. |
| `getSharpnessLevel()` / `setSharpnessLevel(String)` | `String` | Sharpness adjustment level within creative style. |
| `getFaceDetection()` / `setFaceDetection(String)` | `String` | Face detection mode for AF and AE. |
| `getAutoHDR()` / `setAutoHDR(String)` | `String` | Auto HDR mode. |
| `getDROMode()` / `setDROMode(String)` | `String` | Dynamic Range Optimizer mode and level. |

### Advanced Parameters (discovered from reference apps)

These parameters were found in specific reference apps and provide deeper camera control:

| Get / Set | Type | Used By | Description |
|-----------|------|---------|-------------|
| `setRemoteControlMode(boolean)` | `boolean` | smooth-reflection | Enables remote control mode — **suppresses shutter sound and review screen between burst frames**. Essential for seamless multi-frame capture sequences. |
| `setAutoWhiteBalanceLock(boolean)` | `boolean` | smooth-reflection | Locks/unlocks auto white balance. When locked, AWB stays constant across all frames in a sequence for consistent color. |
| `setAntiHandBlurMode(String)` | `String` | smooth-reflection, digital-filter | Controls optical image stabilization (OIS). Set to `"off"` for tripod-mounted multi-frame capture to prevent OIS interference. |
| `setRGBMatrix(int[])` / `isRGBMatrixSupported()` | `int[]` | smooth-reflection, live-view-grading | Applies a 3×3 RGB color transformation matrix. Pass `null` to clear. Used for color grading and ND filter color cast compensation. Matrix is a flat 9-element array in row-major order. |
| `getLongExposureNR()` | `boolean` | smooth-reflection | Returns whether long exposure noise reduction is enabled. Affects raw data memory layout (BFNR offset). |
| `setColorSelectMode(String, int)` | `String, int` | picture-effect-plus | Controls hardware partial color extraction. Mode: `"extract"` to enable, `"off"` to disable. Int is a channel bitmask (1=ch0, 2=ch1, 3=both). |
| `setContrast(int)` / `setSaturation(int)` | `int` | live-view-grading | Direct contrast/saturation control for color grading (separate from creative style levels). |
| `setSharpnessGainMode(boolean)` / `isSharpnessGainModeSupported()` | `boolean` | live-view-grading | Enables fine sharpness gain control mode (more precise than standard sharpness). |
| `setSharpnessGain(int)` | `int` | live-view-grading | Sets sharpness gain level when gain mode is enabled. |
| `setWhiteBalanceShiftMode(boolean)` / `isWhiteBalanceShiftModeSupported()` | `boolean` | live-view-grading | Enables white balance shift for fine color temperature and tint control. |
| `setWhiteBalanceShiftLB(int)` | `int` | live-view-grading | White balance shift on the amber-blue axis (color temperature). Values are inverted from UI display. |
| `setWhiteBalanceShiftCC(int)` | `int` | live-view-grading | White balance shift on the green-magenta axis (color tint). Values are inverted from UI display. |
| `getSupportedColorDepthTypes()` | `List<String>` | live-view-grading | Returns available color depth channels: `"red"`, `"green"`, `"blue"`, `"cyan"`, `"magenta"`, `"yellow"`. |
| `setColorDepth(String, int)` | `String, int` | live-view-grading | Sets per-channel color depth. Six independent channels for fine color control. |
| `isExtendedGammaTableSupported()` | `boolean` | live-view-grading | Checks if the camera supports custom gamma table API. |
| `isPictureControlExposureShiftSupported()` / `setPictureControlExposureShift(int)` | `boolean` / `int` | live-view-grading | Exposure brightness adjustment integrated into the color grading pipeline. |
| `getFocusAreaMode()` | `String` | portrait-beauty | Returns current focus area mode (e.g., "flex", "local", "wide"). |
| `getFocusPointIndex()` | `int` | portrait-beauty | Returns the selected focus point index for local focus area mode. |

### Capability Query Pattern

Every parameter has a `getSupported*()` method:

```java
ParametersModifier modifier = cameraEx.createParametersModifier();

// Check what focus modes are available
List<String> focusModes = modifier.getSupportedFocusModes();
// → ["auto", "continuous-picture", "dmf", "manual"]

// Check if RGB matrix is supported on this camera
boolean hasRGBMatrix = modifier.isRGBMatrixSupported();
if (hasRGBMatrix) {
    int[] matrix = {256, 0, 0, 0, 256, 0, 0, 0, 256}; // Identity matrix
    modifier.setRGBMatrix(matrix);
}

// Check available color depth channels
List<String> channels = modifier.getSupportedColorDepthTypes();
// → ["red", "green", "blue", "cyan", "magenta", "yellow"]
```

---

## 1.3 CameraSequence
**Package**: `com.sony.scalar.hardware.CameraSequence` | **Used by**: ALL 7 apps

The shot pipeline that manages the full capture flow: capture → develop → store. Handles raw data access, image development (raw-to-RGB conversion), DSP preview plugins for real-time effects, and final image storage.

### Core Methods

```java
// Open a camera sequence
CameraSequence.OpenOptions opts = new CameraSequence.OpenOptions();
opts.setOption("MEMORY_MAP_FILE", "/path/to/memmap.so");
opts.setOption("EXPOSURE_COUNT", 1);
opts.setOption("RECORD_COUNT", 1);
CameraSequence sequence = CameraSequence.open(cameraEx, opts);

// Store the final image to SD card
sequence.storeImage(processedImage, true);  // true = release image after store

// Get capture count
int count = sequence.getCaptureCount();

// Release when done
sequence.release();
```

| Method | Return | Description |
|--------|--------|-------------|
| `open(CameraEx, OpenOptions)` | `CameraSequence` | Opens a new capture sequence bound to the camera. |
| `open(int, OpenOptions)` | `CameraSequence` | Opens a sequence by camera ID. |
| `storeImage(OptimizedImage, boolean)` | `void` | Stores a processed image to the SD card. Boolean controls whether the image is released after storing. |
| `getCaptureCount()` | `int` | Returns the number of frames captured in this sequence. |
| `release()` | `void` | Releases all sequence resources. **MUST** be called when done. |

### CameraSequence.Options

Configuration options for capture sequences and preview plugins:

| Option Key | Type | Description | Used By |
|------------|------|-------------|---------|
| `"MEMORY_MAP_FILE"` | `String` | Path to memory map .so file for capture buffer allocation. | picture-effect-plus, live-view-grading |
| `"EXPOSURE_COUNT"` | `int` | Number of exposures per capture (usually 1). | picture-effect-plus |
| `"RECORD_COUNT"` | `int` | Number of images to record per capture (usually 1). | picture-effect-plus |
| `"PREVIEW_FRAME_RATE"` | `int` | Preview frame rate for DSP plugin (e.g., 30000 = 30fps). | digital-filter, double-exposure |
| `"PREVIEW_FRAME_WIDTH"` | `int` | Preview frame width (e.g., 640 or 1024). | digital-filter, double-exposure |
| `"PREVIEW_FRAME_HEIGHT"` | `int` | Preview frame height (0 = auto-calculate). | digital-filter, double-exposure |
| `"PREVIEW_PLUGIN_NOTIFY_MASK"` | `int` | Controls which plugin notifications are enabled. | digital-filter, double-exposure |
| `"PREVIEW_DEBUG_NOTIFY_ENABLED"` | `boolean` | Enables/disables debug notifications. | digital-filter, double-exposure |
| `"PREVIEW_PLUGIN_OUTPUT_ENABLED"` | `boolean` | Enables DSP plugin rendered output to display. | digital-filter, double-exposure |

### Preview Plugin API (digital-filter, double-exposure)

Allows a DSP processor to process live view frames in real-time, enabling effects like graduated ND preview and double-exposure overlay.

```java
// Install a DSP processor as a live preview plugin
CameraSequence sequence = CameraSequence.open(cameraEx);

CameraSequence.Options options = new CameraSequence.Options();
options.setOption("PREVIEW_FRAME_RATE", 30000);
options.setOption("PREVIEW_FRAME_WIDTH", 1024);
options.setOption("PREVIEW_FRAME_HEIGHT", 0);       // Auto height
options.setOption("PREVIEW_PLUGIN_OUTPUT_ENABLED", true);

sequence.setPreviewPlugin(dsp);          // Install DSP as live preview processor
sequence.startPreviewSequence(options);  // Start real-time preview processing
// ... user sees processed live view ...
sequence.stopPreviewSequence();          // Stop processing
sequence.setPreviewPlugin(null);         // Remove plugin before release
sequence.release();
```

### DefaultDevelopFilter

Converts raw sensor data to a processed RGB `OptimizedImage`. The camera's current settings (picture effect, creative style, etc.) are applied during development.

```java
// Raw-to-processed image development
CameraSequence.DefaultDevelopFilter developFilter = new CameraSequence.DefaultDevelopFilter();

if (developFilter.isSupported()) {
    developFilter.setSource(rawData, false);  // false = don't release raw data yet

    if (developFilter.execute()) {
        OptimizedImage developed = developFilter.getOutput();
        // ... use the developed image ...
    }
}
developFilter.release();
```

| Method | Return | Description |
|--------|--------|-------------|
| `isSupported()` | `boolean` | Checks if DefaultDevelopFilter is available on this hardware. |
| `setSource(RawData, boolean)` | `void` | Sets raw sensor data as input. Boolean controls release of raw data. |
| `getNumberOfSources()` | `int` | Returns number of configured source inputs (expect 1). |
| `getNumberOfOutputs()` | `int` | Returns number of available outputs (0 before execute, 1 after). |
| `execute()` | `boolean` | Develops raw data with active camera settings applied. Returns true on success. |
| `getOutput()` | `OptimizedImage` | Gets the developed RGB image after execute(). |
| `release()` | `void` | Releases filter resources. |

### RawData and RawDataInfo

Raw sensor data from the capture pipeline, accessed in `onShutterSequence()` callbacks. Used extensively by smooth-reflection and digital-filter for raw-level DSP processing.

```java
// In onShutterSequence callback:
CameraSequence.RawData rawData = ...;  // From callback

if (rawData.isValid()) {
    CameraSequence.RawDataInfo info = rawData.getRawDataInfo();

    // Sensor dimensions
    int canvasW = info.canvasSizeX;    // Raw sensor canvas width
    int canvasH = info.canvasSizeY;    // Raw sensor canvas height
    int validW = info.validSizeX;      // Valid image data width
    int validH = info.validSizeY;      // Valid image data height

    // White balance coefficients
    int wbR = info.wbR;               // Red WB coefficient
    int wbB = info.wbB;               // Blue WB coefficient

    // Bayer pattern
    int firstColor = info.firstColor;  // First color in Bayer pattern

    rawData.release();  // Release when done
}
```

**RawDataInfo Fields** (~40 fields, used by smooth-reflection):

| Field | Type | Description |
|-------|------|-------------|
| `canvasSizeX/Y` | `int` | Raw sensor canvas dimensions (includes margins). |
| `marginOffsetX/Y` | `int` | Margin offset within the canvas. |
| `marginSizeX/Y` | `int` | Margin area dimensions. |
| `validOffsetX/Y` | `int` | Offset to valid image data within the canvas. |
| `validSizeX/Y` | `int` | Valid image data dimensions. |
| `firstColor` | `int` | First color in the Bayer pattern (RGGB, GRBG, etc.). |
| `clpR/clpGr/clpGb/clpB` | `int` | Clipping levels per color channel. |
| `clpOfst` | `int` | Clipping offset value. |
| `wbR/wbB` | `int` | White balance coefficients for red and blue channels. |
| `expBit` | `int` | Exposure bit depth. |
| `decompMode/compMode` | `int` | Raw decompression/compression modes. |
| `dth0-3/dp0-3` | `int` | Decompression dithering thresholds and parameters. |
| `th0-3/p0-3` | `int` | Compression thresholds and parameters. |
| `rndBit` | `int` | Rounding bit depth. |
| `ddithRstmod/ddithOn` | `int` | Decompression dithering control. |
| `dithRstmod/dithOn` | `int` | Compression dithering control. |

### SplitShutterSequenceCallback States

For multi-frame sequences with interleaved dark frames:

| State | Description |
|-------|-------------|
| `CONTINUE` | Ready for next frame. |
| `DARK` | Dark frame captured (for noise subtraction). |
| `END` | Sequence complete. |
| `ERROR` | Error occurred during sequence. |
| `INTERRUPT` | Sequence interrupted by user. |
| `SCENE` | Scene frame captured. |

---

## 1.4 DSP (Digital Signal Processor)
**Package**: `com.sony.scalar.hardware.DSP` | **Used by**: 5 apps (smooth-reflection, digital-filter, double-exposure, portrait-beauty, photo-retouch)

Hardware-accelerated signal processor for image compositing, blending, color conversion, and effects. The DSP operates on raw sensor data or `OptimizedImage` buffers in the camera's dedicated image processing hardware, achieving much higher performance than software processing.

### Basic Usage Pattern

```java
// Create a DSP processor
DSP dsp = DSP.createProcessor("sony-di-dsp");

// Load a processing program
dsp.setProgram("libsa_smoothphotography.so");

// Allocate parameter buffers
DeviceBuffer bootParams = dsp.createBuffer(60);    // Boot parameters
DeviceBuffer saParams = dsp.createBuffer(256);      // SA (Signal Accelerator) parameters

// Get hardware properties
int programDesc = dsp.getPropertyAsInt("program-descriptor");
int imgAddr = dsp.getPropertyAsInt(optimizedImage, "memory-address");
int imgOffset = dsp.getPropertyAsInt(optimizedImage, "image-data-offset");
int imgCanvasW = dsp.getPropertyAsInt(optimizedImage, "image-canvas-width");

// Configure and execute
dsp.setArg(0, bootParams);   // Argument 0 = boot parameters
dsp.setArg(1, saParams);     // Argument 1 = SA parameters
dsp.setArg(2, workBuffer);   // Argument 2+ = work buffers/images
boolean success = dsp.execute();

// Cleanup
dsp.clearProgram();
dsp.release();
```

### Methods

| Method | Return | Description |
|--------|--------|-------------|
| `DSP.createProcessor(String name)` | `DSP` | Creates a new DSP processor instance. Always use `"sony-di-dsp"`. |
| `setProgram(String path)` | `void` | Loads a DSP program (.so file). See program list below. |
| `clearProgram()` | `void` | Clears the loaded program. Call before release. |
| `createBuffer(int size)` | `DeviceBuffer` | Allocates a hardware buffer for DSP parameter passing. |
| `createImage(int width, int height)` | `OptimizedImage` | Creates a DSP-backed output image buffer. Used by photo-retouch for LCE output. |
| `createImage(int width, int height, int format)` | `OptimizedImage` | Creates an output image with specific format. |
| `getPropertyAsInt(String key)` | `int` | Gets a DSP property. Key: `"program-descriptor"`. |
| `getPropertyAsInt(Object obj, String key)` | `int` | Gets a hardware property of an image/buffer/memory object. Keys: `"memory-address"`, `"image-data-offset"`, `"image-canvas-width"`, `"image-canvas-height"`. |
| `setArg(int index, DeviceBuffer)` | `void` | Sets a DSP execution argument by index. Index 0 is typically boot params. |
| `setArg(int index, DeviceMemory)` | `void` | Sets a DSP argument pointing to device memory. |
| `setArg(int index, OptimizedImage)` | `void` | Sets a DSP argument pointing to an image. |
| `execute()` | `boolean` | Executes the loaded program with configured arguments. Returns true on success. |
| `release()` | `void` | Releases the DSP processor. **MUST** call `clearProgram()` first. |

### Property Keys

| Key | Applies To | Description |
|-----|-----------|-------------|
| `"program-descriptor"` | DSP instance | Returns the program descriptor ID after loading a program. Used in boot parameters. |
| `"memory-address"` | OptimizedImage, DeviceBuffer, DeviceMemory, RawData | Returns the ARM hardware memory address of the object. |
| `"image-data-offset"` | OptimizedImage | Byte offset from memory address to actual pixel data. |
| `"image-canvas-width"` | OptimizedImage | Canvas width (may differ from image width due to stride alignment). |
| `"image-canvas-height"` | OptimizedImage | Canvas height. |

### Known DSP Programs (.so files)

| Program | Used By | Description |
|---------|---------|-------------|
| `libsa_smoothphotography.so` | smooth-reflection | Multi-frame raw averaging (2-256 frames). Commands: 6=initialize accumulator, 4=accumulate frame. |
| `libsa_smoothphotography_avip.so` | smooth-reflection | Newer AVIP hardware variant of the averaging program. |
| `libsa_ndsa.so` | digital-filter | Graduated ND filter compositing — single/dual/multi-frame raw compositing with gamma tables. |
| `libsa_ndsa_mushashi.so` | digital-filter | Musashi hardware variant of ND filter program. |
| `libsa_mle.so` | double-exposure | Multi-layer exposure blending with 7 blend modes (Add, Mean, Lighten, Darken, Screen, Multiply, Weighted Mean). |
| `libsa_mle_mushashi.so` | double-exposure | Musashi hardware variant of blend program. |
| `libsa_pre_mle.so` | double-exposure | Pre-processing transforms (horizontal flip, vertical flip, 180° rotation) before blending. |
| `libsa_lce.so` | photo-retouch | Luminance/Contrast/Saturation Enhancement with tone table mapping. |
| `libsa_lce_musashi.so` | photo-retouch | Musashi variant of LCE program. |
| `libsa_pack_yuv2rgb.so` | photo-retouch | YUV to RGB color space conversion for display. |
| `libsa_yuv2rgb_musashi_03RGBA.so` | photo-retouch | Musashi variant of YUV-to-RGB conversion. |
| `libDiDspImxCompose.so` | (stubs) | Image compositing. |
| `libDiDspImxAlphaBlend.so` | (stubs) | Alpha channel blending. |
| `libDiDspImxConvert.so` | (stubs) | Image format conversion. |
| `libDiDspImxResize.so` | (stubs) | Image resizing. |
| `libDiDspImxRotate.so` | (stubs) | Image rotation. |

### Memory Address Masking (AXI Addresses)

The DSP uses AXI bus addresses. ARM addresses must be masked per hardware generation:

| Hardware Generation | Mask | Conversion |
|-------------------|------|------------|
| AVIP (older) | `addr & 0x7FFFFFFF` | ARM → AXI address |
| Musashi (newer) | `addr & 0x3FFFFFFF` | ARM → AXI address |

Detection: `ScalarProperties.getString("version.platform")` determines hardware generation.

---

## 1.5 DeviceBuffer / DeviceMemory / MemoryMapConfig
**Package**: `com.sony.scalar.hardware`

Low-level hardware memory management for DSP operations. DeviceBuffers are used for DSP parameters, DeviceMemory for large image data, and MemoryMapConfig for accessing named shared memory regions.

```java
// DeviceBuffer: parameter passing to DSP
DeviceBuffer buffer = dsp.createBuffer(256);
ByteBuffer bb = ByteBuffer.allocate(256);
bb.order(ByteOrder.LITTLE_ENDIAN);
bb.putInt(0, command);       // e.g., 6 = initialize, 4 = accumulate
bb.putInt(4, memoryAddress);
// ... fill parameters ...
buffer.write(bb);
// ... use with dsp.setArg() ...
buffer.release();

// DeviceMemory: large memory allocation for image data
DeviceMemory memory = DeviceMemory.allocate(imageSize);
int addr = dsp.getPropertyAsInt(memory, "memory-address");
// ... use address in DSP parameters ...
memory.release();

// MemoryMapConfig: access named shared memory
MemoryMapConfig config = MemoryMapConfig.create("ScalarJpgCache");
int begin = config.getBeginAdress();
int end = config.getEndAdress();
DeviceMemory mapped = DeviceMemory.allocateFromMemoryMapConfig(config);
```

| Class | Methods | Description |
|-------|---------|-------------|
| `DeviceBuffer` | `getSize()`, `write(ByteBuffer)`, `release()` | Small hardware buffer for DSP parameters. Created via `dsp.createBuffer(size)`. |
| `DeviceMemory` | `static allocate(int)`, `static allocateFromMemoryMapConfig(MemoryMapConfig)`, `read()`, `write()`, `release()` | Large hardware memory allocation for image data. |
| `MemoryMapConfig` | `static create(String name)`, `getBeginAdress()`, `getEndAdress()` | Access to named shared memory regions (e.g., `"ScalarJpgCache"`). Note: "Adress" is the actual Sony API spelling. |
| `YUVPlaneExtractor` | `extract(DeviceBuffer, DeviceBuffer, Options)` | Extracts Y/U/V planes from a YUV buffer. Options specify input/output width and height. |

---

## 1.6 DisplayManager (avio)
**Package**: `com.sony.scalar.hardware.avio.DisplayManager` | **Used by**: ALL 7 apps

Manages the camera's display outputs: rear LCD panel, electronic viewfinder (EVF), and HDMI output. The camera switches between displays based on eye sensor and HDMI connection.

```java
// Get current active display
String activeDisplay = DisplayManager.getActiveDevice();
// → "panel" (LCD), "finder" (EVF), or "hdmi"

// Get display resolution info
DisplayManager.DeviceInfo info = DisplayManager.getDeviceInfo("panel");
int width = info.res_w;    // 640
int height = info.res_h;   // 480
int aspect = info.aspect;  // ASPECT_RATIO_3_2 = 0

// Listen for display changes (e.g., eye sensor triggers EVF)
DisplayManager.setDisplayStatusListener(new DisplayManager.DisplayEventListener() {
    public void onDisplayChanged(String deviceId) {
        // deviceId is the new active display
    }
});
```

| Constant | Value | Description |
|----------|-------|-------------|
| `DEVICE_ID_PANEL` | `"panel"` | Rear LCD screen (3.0 inch, 640×480). |
| `DEVICE_ID_FINDER` | `"finder"` | Electronic viewfinder (EVF). |
| `DEVICE_ID_HDMI` | `"hdmi"` | HDMI output. |
| `ASPECT_RATIO_3_2` | `0` | 3:2 aspect ratio. |
| `ASPECT_RATIO_4_3` | `1` | 4:3 aspect ratio. |
| `ASPECT_RATIO_16_9` | `4` | 16:9 aspect ratio. |

| Method | Description |
|--------|-------------|
| `getActiveDevice()` | Returns the ID of the currently active display output. |
| `getDeviceInfo(String deviceId)` | Returns `DeviceInfo` with aspect ratio and resolution (res_h, res_w). |
| `switchDisplayOutputTo(String deviceId)` | Programmatically switches the active display. |
| `setDisplayStatusListener(DisplayEventListener)` | Registers for display change callbacks. |
| `setQfhdOutput(boolean)` | Enables/disables 4K HDMI output. |

---

## 1.7 Indicators (Light, SubLCD, Cec)
**Package**: `com.sony.scalar.hardware.indicator.*`, `com.sony.scalar.hardware.avio.Cec`

### Light (LED Control)

Controls the camera body LEDs (media access, power, WiFi, recording indicators).

```java
Light.initialize();
Light.setState("MEDIA", "ON", true);    // Turn on media access LED
Light.setState("WIFI", "SLOW", true);   // Slow blink WiFi LED
Light.setState("MEDIA", "OFF", true);   // Turn off
Light.release();
```

| LED ID | Description |
|--------|-------------|
| `ANSWER_BACK` | Confirmation/answer back LED. |
| `MEDIA` | SD card access indicator. |
| `POWER` | Power indicator. |
| `RECORD` | Recording indicator. |
| `WIFI` | WiFi activity indicator. |

| Pattern | Description |
|---------|-------------|
| `ON` | Steady on. |
| `OFF` | Off. |
| `FAST` | Fast blink. |
| `SLOW` | Slow blink. |
| `MIDDLE` | Medium blink rate. |

### SubLCD

Controls the top info LCD (if present on camera model):

`initialize()`, `setState(String)`, `setString(String)`, `release()`

### Cec (HDMI CEC)

HDMI Consumer Electronics Control for TV interaction:

`ONETOUCH_PLAY = 1` — triggers TV to switch to camera input.
`issueMessage(int type)` — sends CEC message.

---

# 2. Graphics APIs

## 2.1 OptimizedImage
**Package**: `com.sony.scalar.graphics.OptimizedImage`

Sony's proprietary image container, stored in hardware-accessible memory (YUV422 format, 2 bytes/pixel). Never constructed directly — obtained from factories, filters, camera capture, or DSP operations. **MUST** always call `release()` when done to free hardware memory.

```java
// Check and use an OptimizedImage
if (image != null && image.isValid()) {
    int w = image.getWidth();
    int h = image.getHeight();
    // ... process image ...
    image.release();  // CRITICAL: always release
}
```

> **Important**: Canvas dimensions may differ from image dimensions due to stride padding. The actual pixel data may be offset within the canvas. Use `DSP.getPropertyAsInt(image, "image-data-offset")` to get the offset.

| Method | Return | Description |
|--------|--------|-------------|
| `getWidth()` | `int` | Image width in pixels. |
| `getHeight()` | `int` | Image height in pixels. |
| `isValid()` | `boolean` | Returns true if the image buffer is still valid (not released). Always check before using. |
| `release()` | `void` | Releases the hardware memory. **MUST** be called. Failing to release causes memory leaks that will crash the camera. |

## 2.2 OptimizedImageFactory
**Package**: `com.sony.scalar.graphics.OptimizedImageFactory`

Decodes images from file paths into OptimizedImage format with optional metadata extraction.

```java
// Load a full-size image with EXIF metadata
OptimizedImageFactory.Options opts = new OptimizedImageFactory.Options();
opts.imageType = 3;         // 3 = full-size image
opts.bBasicInfo = true;     // Decode basic EXIF info
opts.bCamInfo = true;       // Decode camera-specific EXIF
opts.bExtCamInfo = true;    // Decode extended camera info (MakerNote)
opts.bGpsInfo = true;       // Decode GPS data

OptimizedImage image = OptimizedImageFactory.decodeImage("/path/to/image.jpg", opts);
AvindexContentInfo metadata = opts.outContentInfo;  // Populated after decode

// Load a thumbnail for gallery display
OptimizedImageFactory.Options thumbOpts = new OptimizedImageFactory.Options();
thumbOpts.imageType = 2;    // 2 = screen-size thumbnail
OptimizedImage thumb = OptimizedImageFactory.decodeImage("/path/to/image.jpg", thumbOpts);
```

| imageType Value | Description |
|----------------|-------------|
| `2` | Screen-size thumbnail (fast loading for gallery). |
| `3` | Full-size image (for editing/processing). |

## 2.3 ImageAnalyzer
**Package**: `com.sony.scalar.graphics.ImageAnalyzer` | **Used by**: portrait-beauty

Hardware-accelerated face detection on OptimizedImage buffers. Detects up to 8 faces with bounding rectangles and facial feature points.

```java
// Detect faces in an image
ImageAnalyzer analyzer = new ImageAnalyzer();
ImageAnalyzer.AnalyzedFace[] faces = new ImageAnalyzer.AnalyzedFace[8];
int faceCount = analyzer.findFaces(image, faces);

for (int i = 0; i < faceCount; i++) {
    Rect faceRect = faces[i].rect;
    // Face coordinates are in -1000 to +1000 normalized space
    // Convert to image pixels:
    int pixelLeft = (faceRect.left + 1000) * image.getWidth() / 2000;
    int pixelTop = (faceRect.top + 1000) * image.getHeight() / 2000;
    // ... use face rectangle for effects targeting ...
}
analyzer.release();
```

| Method | Return | Description |
|--------|--------|-------------|
| `findFaces(OptimizedImage, AnalyzedFace[])` | `int` | Detects faces and fills the array. Returns the number of faces found (0-8). Face rectangles use -1000 to +1000 coordinate space. |
| `release()` | `void` | Releases analyzer resources. |

## 2.4 JpegExporter
**Package**: `com.sony.scalar.graphics.JpegExporter` | **Used by**: photo-retouch

Encodes an OptimizedImage to JPEG and saves it to the camera's media storage. Used for saving edited images as new files.

```java
// Save an edited image to SD card
JpegExporter exporter = new JpegExporter();
JpegExporter.Options opts = new JpegExporter.Options();
opts.quality = 2;  // 1=Standard, 2=Fine, 3=Extra Fine

String mediaId = AvindexStore.getExternalMediaIds()[0];
exporter.encode(image, mediaId, opts);
exporter.release();

// Wait for database to update so new image appears in gallery
AvindexStore.Images.waitAndUpdateDatabase(getContentResolver(), mediaId);
```

| Quality Value | Description |
|--------------|-------------|
| `1` | Standard quality (smaller file). |
| `2` | Fine quality (good balance). |
| `3` | Extra Fine quality (largest file, best quality). |

## 2.5 Image Filters
**Package**: `com.sony.scalar.graphics.imagefilter.*`

All filters share the same pipeline: `new` → `setSource(img, releaseAfter)` → configure → `execute()` → `getOutput()` → `clearSources()` → `release()`.

### ScaleImageFilter

Resizes an image to target dimensions. Alignment requirement: width must be aligned to 4 bytes, height to 2 bytes.

```java
ScaleImageFilter filter = new ScaleImageFilter();
filter.setSource(image, false);  // false = keep original image
int targetW = 1024 & (-4);      // Align to 4-byte boundary
int targetH = 768 & (-2);       // Align to 2-byte boundary
filter.setDestSize(targetW, targetH);
if (filter.execute()) {
    OptimizedImage scaled = filter.getOutput();
    // ... use scaled image ...
}
filter.clearSources();
filter.release();
```

### CropImageFilter

Extracts a rectangular region from an image. Setting the source rect to the full image dimensions performs a copy.

```java
CropImageFilter filter = new CropImageFilter();
filter.setSource(image, false);
filter.setSrcRect(new Rect(100, 50, 900, 650));  // left, top, right, bottom
if (filter.execute()) {
    OptimizedImage cropped = filter.getOutput();
}
filter.clearSources();
filter.release();
```

### RotateImageFilter

Rotates an image by 90° increments or arbitrary angles. Supports trim mode for auto-cropping after rotation.

```java
// 90-degree rotation
RotateImageFilter filter = new RotateImageFilter();
filter.setSource(image, false);
filter.setTrimMode(3);  // 3 = auto-trim to fit
filter.setRotation(RotateImageFilter.DEGREE_90);
if (filter.execute()) {
    OptimizedImage rotated = filter.getOutput();
}
filter.clearSources();
filter.release();

// Arbitrary angle rotation (photo-retouch: horizon straightening)
RotateImageFilter filter2 = new RotateImageFilter();
filter2.setSource(image, false);
filter2.setTrimMode(3);
filter2.setRotation(2.5);  // 2.5 degrees for horizon correction
filter2.execute();
```

### FaceNRImageFilter (Face Noise Reduction)

Applies noise reduction selectively to detected face regions for skin smoothing. Used by photo-retouch and portrait-beauty.

```java
// Smooth skin in detected faces
FaceNRImageFilter filter = new FaceNRImageFilter();
filter.setSource(image, false);

// Build face rectangle list from detected faces
Vector<Rect> faceRects = new Vector<Rect>();
faceRects.add(new Rect(-200, -300, 200, 100));  // -1000..1000 space
filter.setFaceList(faceRects);

filter.setISOValue(800);    // ISO for NR calibration (higher = stronger base NR)
filter.setNRLevel(3);       // 1 = strongest smoothing, 5 = lightest smoothing
if (filter.execute()) {
    OptimizedImage smoothed = filter.getOutput();
}
filter.clearSources();
filter.release();
```

### MiniatureImageFilter (picture-effect-plus)

Applies tilt-shift blur for miniature/diorama effect. One strip of the image stays sharp while the rest blurs.

```java
MiniatureImageFilter filter = new MiniatureImageFilter();
filter.setSource(image, false);
filter.setMiniatureArea(1);  // 1=H-Center, 2=V-Center, 3=Left, 4=Right, 5=Upper, 6=Lower
if (filter.execute()) {
    OptimizedImage miniature = filter.getOutput();
}
filter.release();
```

| Area Code | Description |
|-----------|-------------|
| `1` | Horizontal center strip in focus (classic tilt-shift). |
| `2` | Vertical center strip in focus. |
| `3` | Left side in focus. |
| `4` | Right side in focus. |
| `5` | Upper region in focus. |
| `6` | Lower region in focus. |

### Other Filters

| Filter | Description |
|--------|-------------|
| `ContrastPlusImageFilter` | Automatic contrast enhancement. |
| `RedEyeImageFilter` | Automatic red-eye removal in flash photos. |
| `SoftFocusImageFilter` | Applies a soft focus/glow effect. |
| `SuperResolutionImageFilter` | Upscales images using super-resolution algorithm. |

---

# 3. Widget APIs

## OptimizedImageView
**Package**: `com.sony.scalar.widget.OptimizedImageView`

Hardware-accelerated display widget for OptimizedImage. Supports zoom, pan, and fit modes. Standard display size: 640×480 pixels (camera LCD resolution).

```java
// Display an image
OptimizedImageView imageView = (OptimizedImageView) findViewById(R.id.image_view);
imageView.setOptimizedImage(image);
imageView.setDisplayType(OptimizedImageView.DISPLAY_TYPE_CENTER_INNER);  // Fit to view
imageView.redraw();

// Zoom and pan
imageView.setScale(2.0f, OptimizedImageView.BOUND_TYPE_LONG_EDGE);
imageView.translate(new Point(50, 30), OptimizedImageView.TRANS_TYPE_INNER_CENTER);

// Check pan limits
OptimizedImageView.Translatability trans = imageView.getTranslatability(type);
if (trans.right) { /* can pan right */ }

// Get layout measurements
OptimizedImageView.LayoutInfo layout = imageView.getLayoutInfo();
Rect clip = layout.clipSize;    // Visible area
Rect draw = layout.drawSize;    // Drawing area
Rect img = layout.imageSize;    // Full image dimensions
Rect view = layout.viewSize;    // View dimensions

// Clear and release
imageView.setOptimizedImage(null);
imageView.release();
```

| Method | Description |
|--------|-------------|
| `setOptimizedImage(OptimizedImage)` | Sets the image to display. Pass `null` to clear. |
| `setDisplayType(int type)` | Sets the fit mode. `DISPLAY_TYPE_CENTER_INNER` fits the image within the view. |
| `setScale(float, int boundType)` | Sets zoom level. `BOUND_TYPE_LONG_EDGE` scales relative to the long edge. |
| `translate(Point, int transType)` | Pans the image. `TRANS_TYPE_INNER_CENTER` pans relative to center. |
| `getLayoutInfo()` | Returns `LayoutInfo` with `.clipSize`, `.drawSize`, `.imageSize`, `.viewSize` Rects. |
| `getTranslatability(int type)` | Returns which directions can still be panned (`.top`, `.bottom`, `.left`, `.right` booleans). |
| `redraw()` | Forces a redraw of the current image. |
| `release()` | Releases hardware display resources. |

---

# 4. Media APIs

## AudioManager
**Package**: `com.sony.scalar.media.AudioManager`

Camera audio system control. Manages microphone and speaker settings.

`getParameters()`, `setParameters()`, `setSettingChangedListener()`, `setMicrophoneChangedListener()`

## AudioRecord / AudioTrack
**Package**: `com.sony.scalar.media`

Hardware audio recording and playback with DeviceBuffer support for efficient memory handling.

- **AudioRecord**: `startRecording()`, `stopRecording()`, `read(DeviceBuffer)`
- **AudioTrack**: `play()`, `pause()`, `stop()`, `write(DeviceBuffer)`

## MediaRecorder
**Package**: `com.sony.scalar.media.MediaRecorder`

Records video from the camera hardware. Must set camera reference and output media before recording.

```java
MediaRecorder recorder = new MediaRecorder();
recorder.setCamera(cameraEx);
recorder.setOutputMedia(AvindexStore.getExternalMediaIds()[0]);
recorder.prepare();
recorder.start();
// ... recording ...
recorder.stop();
recorder.release();
```

Listeners: `RecordListener`, `ErrorListener`, `TimeListener`, `RemainListener`, `StreamWriteListener`.

## MediaPlayer
**Package**: `com.sony.scalar.media.MediaPlayer`

Plays back video files with Sony-specific features like step playback and display modes.

```java
MediaPlayer player = new MediaPlayer();
player.setDataSource(filePath);
player.prepare();
player.start();
player.seekTo(timeMs);
player.stepForward();     // Frame-by-frame advance
player.stepRewind();      // Frame-by-frame reverse
player.setPlaybackSpeed(MediaPlayer.FAST_FORWARD_2);  // 2x speed
player.setDisplayMode(MediaPlayer.FULL_SCREEN);  // 0=full, 1=histogram overlay
player.stop();
player.release();
```

## AvindexContentInfo
**Package**: `com.sony.scalar.media.AvindexContentInfo`

Rich metadata container with ~140+ TAG_ constants for accessing image/video metadata. Populated by `OptimizedImageFactory.decodeImage()` or by querying AvindexStore.

```java
AvindexContentInfo info = opts.outContentInfo;  // From OptimizedImageFactory decode
String filename = info.getAttribute(AvindexContentInfo.TAG_DCF_TBL_FILE_NAME);
int width = info.getAttributeInt(AvindexContentInfo.TAG_IMAGE_WIDTH);
double aperture = info.getAttributeDouble(AvindexContentInfo.TAG_APERTURE);
FaceInfo[] faces = info.getFaceInfo();
OptimizedImage thumbnail = info.getThumbnail();
```

Key tags: `TAG_DCF_TBL_FILE_NAME`, `TAG_DATETIME`, `TAG_IMAGE_WIDTH`, `TAG_IMAGE_HEIGHT`, `TAG_APERTURE`, `TAG_EXPOSURE_TIME`, `TAG_FOCAL_LENGTH`, `TAG_ISO`, `TAG_ORIENTATION`, `TAG_FLASH`.

## HighlightMovieMaker
**Package**: `com.sony.scalar.media.HighlightMovieMaker`

Automatically creates highlight video compilations from captured images/videos.

```java
HighlightMovieMaker maker = new HighlightMovieMaker();
maker.setMovieBgm(HighlightMovieMaker.MUSIC3);          // Background music
maker.setMovieLength(HighlightMovieMaker.LENGTH_30SEC);  // Duration
maker.prepare();
maker.execute();
```

BGM options: `MUSIC1` through `MUSIC8`. Length options: `15SEC`, `30SEC`, `1MIN`, `2MIN`.

## MediaInfo
**Package**: `com.sony.scalar.media.MediaInfo`

Media device information.

`MEDIA_TYPE_SD = 1`, `MEDIA_TYPE_MS = 2` (Memory Stick). Methods: `getMediaType()`, `isEyeFi()`.

---

# 5. Metadata APIs

## TakenPhotoInfo (~90 fields)
**Package**: `com.sony.scalar.meta.TakenPhotoInfo`

Complete EXIF and MakerNote metadata from a captured photo. Obtained from `ReviewInfo.photo` or `AvindexContentInfo`.

| Field | Type | Description |
|-------|------|-------------|
| `FNumber` | `short` | Aperture f-number × 100 (e.g., 280 = f/2.8). |
| `ISOSpeedRatings` | `int` | ISO sensitivity (100, 200, 400, ...). |
| `ExposureTime_num` / `ExposureTime_den` | `int` | Shutter speed as fraction (num/den, e.g., 1/60). |
| `Flash` | `short` | Flash status flags (EXIF flash tag). |
| `FocalLength_num` / `FocalLength_den` | `int` | Focal length as fraction in mm. |
| `WhiteBalance` | `short` | White balance mode (EXIF: 0=auto, 1=manual). |
| `ExposureProgram` | `short` | Exposure program (1=Manual, 2=Program, 3=Aperture Priority, 4=Shutter Priority). |
| `MeteringMode` | `short` | Metering mode (1=average, 2=center, 3=spot, 5=multi). |
| `ImageWidth` / `ImageLength` | `int` | Image dimensions in pixels. |
| `Orientation` | `short` | EXIF orientation (1=normal, 3=180°, 6=90°CW, 8=90°CCW). |
| `DateTimeOriginal` | `String` | Capture timestamp string. |
| `ExposureBiasValue` | `float` | Exposure compensation in EV. |
| `MakerNoteCreativeStyle` | `short` | Sony creative style preset used. |
| `MakerNotePictureEffect` | `short` | Sony picture effect applied. |
| `MakerNoteMultiFrameNR` | `short` | Multi-frame noise reduction status. |
| `MakerNoteAutoHDR` | `short` | Auto HDR status. |
| `FocusMode` | `short` | Focus mode used during capture. |
| `faceInfo` | `FaceInfo[]` | Detected faces at capture time. |
| `faceNumber` | `int` | Number of faces detected. |

## FaceInfo
**Package**: `com.sony.scalar.meta.FaceInfo`

Face detection data from captured images, including facial landmarks and expression analysis.

| Field | Type | Description |
|-------|------|-------------|
| `rect` | `Rect` | Face bounding box in image coordinates. |
| `leftEye` / `rightEye` / `mouth` | `Point` | Facial landmark positions. |
| `faceAngle` | `FaceAngle` | Face rotation angle (yaw). |
| `smile` | `SmileStatus` | Contains `boolean isSmiling` and `int score` (confidence). |
| `gender` | `GenderStatus` | Contains `int gender` and `int score` (confidence). |
| `generation` | `GenerationStatus` | Estimated age generation. |
| `eyeBlink` | `EyeBlinkStatus` | Contains `boolean leftEyeBlink`, `boolean rightEyeBlink`. |

## Histogram
**Package**: `com.sony.scalar.meta.Histogram`

Image histogram data for exposure analysis.

| Field | Type | Description |
|-------|------|-------------|
| `Y` | `short[]` | Luminance histogram (brightness distribution). |
| `R` | `short[]` | Red channel histogram. |
| `G` | `short[]` | Green channel histogram. |
| `B` | `short[]` | Blue channel histogram. |

---

# 6. Provider APIs

## AvindexStore
**Package**: `com.sony.scalar.provider.AvindexStore` | Authority: `"com.sony.scalar.providers.avindex"`

Sony's replacement for `android.provider.MediaStore`. Provides access to all images and videos stored on the camera's SD card. Must be used instead of standard MediaStore on Sony cameras.

```java
// Get SD card media ID
String[] mediaIds = AvindexStore.getExternalMediaIds();
String sdCard = mediaIds[0];

// Check available space
long availableBytes = AvindexStore.getAvailableSize(sdCard);

// Query images
Uri imagesUri = AvindexStore.Images.Media.getContentUri(sdCard);
Cursor cursor = getContentResolver().query(imagesUri, null, null, null, null);

// Save edited image and update database (photo-retouch pattern)
// After JpegExporter.encode():
AvindexStore.Images.waitAndUpdateDatabase(getContentResolver(), sdCard);  // Blocking wait

// Rotate image orientation in database
AvindexStore.Images.Media.rotateImage(getContentResolver(), imageUri, imageId, newOrientation);
```

### Constants

| Constant | Value | Description |
|----------|-------|-------------|
| `MEDIA_TYPE_EXTERNAL` | `1` | External media (SD card). |
| `CONTENT_TYPE_LOAD_STILL` | `1` | Still image content type. |
| `CONTENT_TYPE_LOAD_MP4` | `5` | MP4 video content type. |
| `CONTENT_TYPE_LOAD_XAVC` | `7` | XAVC video content type. |

### Static Methods

| Method | Description |
|--------|-------------|
| `getExternalMediaIds()` | Returns array of available external media IDs (typically one SD card). |
| `getAvailableSize(String mediaId)` | Returns available storage space in bytes. |
| `getMediaInfo(String mediaId)` | Returns `MediaInfo` about the media device. |
| `loadMedia()` | Triggers media database reload. |
| `waitLoadMediaComplete()` | Blocks until media load is complete. |

### Sub-classes

| Class | Method | Description |
|-------|--------|-------------|
| `Images.Media` | `getContentUri(String mediaId)` | Content URI for querying images. |
| `Images.Media` | `rotateImage(ContentResolver, Uri, long, int)` | Updates EXIF orientation. |
| `Images` | `waitAndUpdateDatabase(ContentResolver, String)` | Blocking wait for DB update after save. |
| `Video.Media` | `getContentUri(String mediaId)` | Content URI for querying videos. |
| `Files.Media` | `getContentUri(String mediaId)` | Content URI for querying all files. |

### Observers

| Class | Description |
|-------|-------------|
| `AvindexUpdateObserver` | Notified when media database is updated. `COMPLETE_RESULT_OK = 0`. |
| `AvindexDeleteObserver` | Notified when media is deleted. `COMPLETE_RESULT_OK = 0`. |

### Inhibition Factors (photo-retouch)

Check before saving: `INH_FACTOR_CONTENT_FULL_FOR_STILL`, `INH_FACTOR_FOLDER_FULL_FOR_STILL`, `INH_FACTOR_NEED_REPAIR_AVINDEX`.

---

# 7. System Utility APIs

## ScalarInput (~160 ISV_KEY constants)
**Package**: `com.sony.scalar.sysutil.ScalarInput`

Maps all physical camera buttons, dials, and switches to key code constants. Used in `onKeyDown()`/`onKeyUp()` handlers.

```java
// Handle camera buttons in Activity
public boolean onKeyDown(int keyCode, KeyEvent event) {
    switch (keyCode) {
        case ScalarInput.ISV_KEY_S1_1:  // Half-press shutter
            cameraEx.startOneShotFocusDrive();
            return true;
        case ScalarInput.ISV_KEY_S2:    // Full shutter press
            cameraEx.burstableTakePicture();
            return true;
        case ScalarInput.ISV_KEY_ENTER: // OK button
            confirmAction();
            return true;
        case ScalarInput.ISV_KEY_DELETE: // Trash = Back
            onBackPressed();
            return true;
    }
    return super.onKeyDown(keyCode, event);
}

// Read dial position
ScalarInput.KeyStatus status = ScalarInput.getKeyStatus(ScalarInput.ISV_DIAL_1_STATUS);
int dialPosition = status.status / 22;  // Normalize to usable range
```

### Key Constants

| Key | Value | Physical Button | Description |
|-----|-------|----------------|-------------|
| `ISV_KEY_UP` | `103` | D-pad Up | Navigate up. |
| `ISV_KEY_DOWN` | `108` | D-pad Down | Navigate down. |
| `ISV_KEY_LEFT` | `105` | D-pad Left | Navigate left. |
| `ISV_KEY_RIGHT` | `106` | D-pad Right | Navigate right. |
| `ISV_KEY_ENTER` | `232` | Center/OK button | Confirm selection. |
| `ISV_KEY_MENU` / `ISV_KEY_SK1` | `514` | Menu button | Open menu. |
| `ISV_KEY_DELETE` / `ISV_KEY_SK2` | `595` | Trash/Delete button | Acts as Back button in apps. |
| `ISV_KEY_S1_1` | `516` | Half-press shutter | Autofocus trigger. |
| `ISV_KEY_S2` | `518` | Full shutter press | Capture trigger. |
| `ISV_KEY_STASTOP` | `515` | Movie record button | Start/stop movie recording. |
| `ISV_KEY_FN` | `520` | Function button | Quick function access. |
| `ISV_KEY_AEL` | — | AE Lock button | Exposure lock. |
| `ISV_KEY_PLAY` | — | Playback button | Switch to playback mode. |
| `ISV_KEY_CUSTOM1` | `622` | C1 button | Custom button 1. |
| `ISV_KEY_CUSTOM2` | — | C2 button | Custom button 2. |
| `ISV_KEY_CUSTOM3` | `659` | C3 button | Custom button 3. |
| `ISV_DIAL_1_CLOCKWISE` | `525` | Upper control dial CW | Dial rotation clockwise. |
| `ISV_DIAL_1_COUNTERCW` | — | Upper control dial CCW | Dial rotation counter-clockwise. |
| `ISV_DIAL_2_CLOCKWISE` | — | Lower control dial CW | Rear dial clockwise. |
| `ISV_RING_CLOCKWISE` | `648` | Lens ring CW | Focus/zoom ring rotation. |
| `ISV_KEY_MODE_DIAL` | — | Mode dial | Mode dial change (M/A/S/P). |
| `ISV_KEY_LENS_ATTACH` | — | Lens mount | Lens attach/detach events. |

`getKeyStatus(int keyCode)` → `KeyStatus` (`.status`: `STATUS_ON=1`, `STATUS_OFF=0`)

## ScalarProperties (~80 PROP, ~150 INTVAL constants)
**Package**: `com.sony.scalar.sysutil.ScalarProperties`

Reads camera hardware properties and configuration. All properties are read-only.

```java
// Get camera model name
String model = ScalarProperties.getString("model.name");           // → "ILCE-6500"
String serial = ScalarProperties.getString("model.serial.code");   // → serial number
String platform = ScalarProperties.getString("version.platform");  // → "2.17"

// Check hardware capabilities
String wifiSupported = ScalarProperties.getString("device.wifi.supported");
String mechShutter = ScalarProperties.getString("device.mechanical.shutter");

// Get sensor info (portrait-beauty, smooth-reflection)
int megapixels = ScalarProperties.getInt("mem.rawimage.size.in.mega.pixel"); // → 24
String deviceMem = ScalarProperties.getString("device.memory");

// Get supported picture sizes (smooth-reflection: for buffer allocation)
List<ScalarProperties.PictureSize> sizes = ScalarProperties.getSupportedPictureSizes();
for (ScalarProperties.PictureSize size : sizes) {
    int w = size.width;
    int h = size.height;
}
```

### Known Property Keys

| Key | Return | Description |
|-----|--------|-------------|
| `"model.name"` | `String` | Camera model (e.g., "ILCE-6500", "DSC-RX100M3"). |
| `"model.serial.code"` | `String` | Camera serial number. |
| `"version.platform"` | `String` | Firmware/platform version. Used to detect hardware generation (Musashi vs AVIP). |
| `"device.wifi.supported"` | `String` | Whether WiFi is available. |
| `"device.mechanical.shutter"` | `String` | Whether mechanical shutter is present. |
| `"device.memory"` | `String` | Device memory info for sequence configuration. |
| `"mem.rawimage.size.in.mega.pixel"` | `int` | Raw image sensor megapixel count (16, 20, 24, 36). |

### PictureSize

`ScalarProperties.PictureSize`: `width int`, `height int` — represents a supported image resolution.

## TimeUtil
**Package**: `com.sony.scalar.sysutil.TimeUtil`

Camera system time access (independent of Android system time).

```java
TimeUtil.PlainCalendar cal = TimeUtil.getCurrentCalendar();
int year = cal.year;
int month = cal.month;  // 1-12
int day = cal.day;
int hour = cal.hour;
int minute = cal.minute;
int second = cal.second;

TimeUtil.PlainTimeZone tz = TimeUtil.getCurrentTimeZone();
int gmtOffset = tz.gmtDiff;        // Offset from GMT in minutes
int dstOffset = tz.summerTimeDiff;  // DST offset in minutes
```

---

# 8. Didep APIs ("Device-Independent Device Extension Platform")
**Package**: `com.sony.scalar.sysutil.didep.*`

System-level camera control APIs for power management, settings, temperature monitoring, and peripheral hardware.

## Settings
Controls camera configuration settings. Has 15 inner classes for different setting categories.

| Method | Description |
|--------|-------------|
| `getAutoPowerOffTime()` / `setAutoPowerOffTime(int)` | Auto power off timer in seconds. |
| `getAutoRotate()` | Returns auto-rotate display setting. |
| `getDialSetting()` | Returns control dial configuration. |
| `getGridLine()` | Returns grid line display setting. |
| `getTargetMediaId()` | Returns the current target media (SD card) ID. |
| `setKeyLock(boolean)` | Locks/unlocks physical buttons. |

## ScalarSystemManager
System-level power and reset control.

| Method | Description |
|--------|-------------|
| `isSystemReady()` | Returns true when the camera system is fully initialized. |
| `requestPowerOff(int factor, int param1, int param2)` | Initiates camera shutdown. 32 OFF_FACTOR constants define the reason. |
| `resetAll()` | Factory reset all settings. |

## Power
Battery monitoring.

`Power.getInstance()`, `setBatteryRemainTimeListener(BatteryRemainTimeListener)` — listener receives remaining battery time updates.

## Temperature
Camera temperature monitoring. Cameras may throttle or shut down if overheated.

`getStatus()` — returns current temperature status.
`setCountDownInfoListener()` — countdown before thermal shutdown.
`setStatusCallback()` — continuous temperature monitoring.

## Nfc
NFC hardware control.

`isEnabled()` — check if NFC is available.
`start()` / `stop()` — enable/disable NFC radio.
`CONNECT_COMPLETED = 1` — NFC connection established.

## Gps
GPS hardware control.

`getGpsPowerState()` — returns GPS state.
`setGpsInfoListener()` — GPS fix updates.
States: `GPS_STATE_FIX = 4` (position locked), `GPS_STATE_OFF = 1`.

## Bluetooth
`getBluetoothInfo()`, `setBluetoothInfoListener()` — Bluetooth status monitoring.

## Media (Didep)
SD card format and recovery.

`executeFormat()` — formats the SD card.
`execRecovery(int mode)` — recovery modes: `RECOVERY = 0`, `SALVAGE = 1`, `REPAIR = 2`.

## Gpelibrary
Frame buffer pixel format control. Affects rendering quality and performance.

```java
// Switch to high-quality 32-bit rendering (for photo editing UIs)
Gpelibrary.changeFrameBufferPixel(Gpelibrary.ABGR8888);

// Switch back to low-quality 16-bit (default, saves memory)
Gpelibrary.changeFrameBufferPixel(Gpelibrary.RGBA4444);
```

## Caution
Camera warning system with ~12,000 constant fields covering every possible warning state (overheating, memory full, lens error, battery low, etc.).

`EnableCaution()`, `SetFactor()`, `SetTrigger()`.

## Kikilog
`setUserLog(int logType, Options)` — camera-specific logging system.

---

# 9. Networking APIs

**ScalarWifiInfo** (`com.sony.scalar.sysnetutil`): `getProductCode()` — returns camera's WiFi product identifier.

**SsdpDevice** (`com.sony.scalar.lib.ssdpdevice`): SSDP (Simple Service Discovery Protocol) for UPnP device discovery.

```java
SsdpDevice ssdp = SsdpDevice.getInstance();
ssdp.initialize(serverConf, ddStatusListener);
ssdp.enqueueStartServer();  // Start SSDP advertisement
// ...
ssdp.enqueueStopServer();
```

**WebAPI** (`com.sony.scalar.webapi.*`): Camera-hosted HTTP API for smartphone remote control. Used by srctrl and sync-to-smart-phone. Services include camera control (v1.0-v1.4), content sync (v1.0), access control (pairing/auth). Server built on Leza HTTP framework (`com.sony.scalar.lib.leza.*`).

**AuthLibManager** (`com.sony.scalar.webapi.lib.authlib`): Device authentication for WebAPI pairing. Methods: `initialize()`, `enableMethods()`, `getPrivateMethod()`.

**DdController** (`com.sony.scalar.lib.ddserver`): Device Description server for SSDP/UPnP service advertisement.

**WiFi Direct** (`com.sony.wifi.direct.*`, `com.sony.wifi.p2p.*`): WiFi Direct peer-to-peer connectivity.
**WPS** (`com.sony.wifi.wps.WpsError`): WiFi Protected Setup error handling.

---

# 10. Android System Extensions

## DAConnectionManager (`android.app`)

Sony's extension to the Android `Activity` lifecycle for camera app management. The camera OS communicates with apps through broadcast intents.

```java
// In AndroidManifest.xml: Register receivers
// <receiver android:name=".BootCompletedReceiver">
//   <intent-filter>
//     <action android:name="com.android.server.DAConnectionManagerService.BootCompleted"/>
//   </intent-filter>
// </receiver>

// In Activity onResume(): Register with camera OS
Intent intent = new Intent("com.android.server.DAConnectionManagerService.AppInfoReceive");
intent.putExtra("cycleTime", "APO/NO");  // Disable auto power off while app is running
sendBroadcast(intent);
```

| Intent | Purpose |
|--------|---------|
| `...BootCompleted` | Camera finished booting — app should start its main activity. |
| `...ExitCompleted` | App has acknowledged exit — camera can proceed with shutdown. |
| `...AppInfoReceive` | Register app with camera OS. Extra `"cycleTime"="APO/NO"` disables auto power off. |

---

# 11. Base App Framework

All 19 reference apps share a monolithic framework (~80% of each app's code). Key architecture pattern:

1. **BaseApp** (Activity) — Main activity with lifecycle management and state machine. Handles camera open/close, display switching, key event routing.
2. **Factory** — Maps states to layouts and key handlers via a constituent table. Each state has associated screen layout and button behavior.
3. **CameraSetting** — Manages all camera parameters. Provides `registController()` for apps to register custom parameter controllers.
4. **ExecutorCreator** — App-specific capture pipeline. Returns the appropriate executor (NormalExecutor, SpinalExecutor, etc.) based on current mode.
5. **BaseMenuService + MenuTable** — Menu system with hierarchical menu items, value controllers, and navigation.
6. **AVIndexProvider** — Media database queries via AvindexStore.
7. **TemperatureManager** — Temperature monitoring with auto-shutdown.
8. **SubLcdManager** — Top LCD display control.
9. **BluetoothController** / **GpsController** — Peripheral hardware management.

**Shared ~80%**: Framework, camera lifecycle, menus, playback, display management.
**App-specific ~20%**: Capture pipeline, DSP programs, edit UI, effect-specific controllers.

---

# 12. Dual-Device Pattern

Every app MUST work on both Sony camera hardware and standard Android devices/emulators. Use runtime detection to switch between real Sony APIs and dummy implementations.

```java
// Detection pattern (OpenMemories-Framework)
static boolean isSonyCamera;
static {
    try {
        Class.forName("com.sony.scalar.sysutil.ScalarProperties");
        isSonyCamera = true;
    } catch (ClassNotFoundException e) {
        isSonyCamera = false;
    }
}

// Alternative: check build properties
// Build.BRAND = "sony", Build.MODEL = "ScalarA", Build.DEVICE = "dslr-diadem"
```

### API Fallback Table

| Sony API | Emulator Fallback | Notes |
|----------|-------------------|-------|
| `CameraEx` | `android.hardware.Camera` | Use standard Camera API for preview/capture. |
| `OptimizedImage` | `Bitmap` | Use Android Bitmap for image operations. |
| `OptimizedImageView` | `ImageView` | Use standard ImageView for display. |
| `ScalarInput` | `KeyEvent` | Map Android key codes to equivalent actions. |
| `AvindexStore` | `MediaStore` | Use Android MediaStore for media queries. |
| `DisplayManager` | Fixed LCD info | Return hardcoded 640×480 display info. |
| `DSP` | Skip / software fallback | DSP effects won't work; skip or use software processing. |
| `CameraSequence` | Standard capture | Use normal Camera API capture flow. |
| `Light` / `SubLCD` | No-op | LED and SubLCD control has no equivalent. |
| `DAConnectionManager` | No-op | Camera lifecycle intents are ignored on normal Android. |
| `ScalarProperties` | Hardcoded values | Return reasonable defaults for model name, etc. |

---

# 13. Reference App Details

## 13.1 Smooth Reflection
**Package**: `com.sony.imaging.app.smoothreflection`

### Overview
Simulates long-exposure photography by capturing 2 to 256 consecutive raw frames and averaging them at the raw data level using DSP hardware acceleration. Produces smooth water, light trails, and motion blur effects without actual long exposure. Features custom gamma tables, RGB matrix control, aperture adjustment, and white balance locking for consistent multi-frame capture.

### DSP Programs
- `libsa_smoothphotography.so` — Main raw averaging program
- `libsa_smoothphotography_avip.so` — AVIP hardware variant

### Processing Pipeline
1. **Force Settings**: Disable OIS (`setAntiHandBlurMode("off")`), disable DRO
2. **Lock AWB**: `setAutoWhiteBalanceLock(true)` for consistent color
3. **Remote Control Mode**: `setRemoteControlMode(true)` suppresses shutter sound between frames
4. **Optional ND Simulation**: `adjustAperture(steps)` + custom gamma table + RGB matrix for color cast compensation
5. **Per-Frame Capture**: `burstableTakePicture()` → raw data callback
6. **DSP Raw Averaging**: Command 6 (initialize) for first frame, command 4 (accumulate) for subsequent frames. Uses ~40 RawDataInfo fields + model-specific BFNR offsets
7. **Development**: `DefaultDevelopFilter` converts averaged raw to OptimizedImage
8. **Storage**: `sequence.storeImage()`
9. **Cleanup**: Unlock AWB, disable remote control mode, release DSP

### Unique APIs
- `ParametersModifier.setRemoteControlMode(true)` — suppresses shutter sound between burst frames
- `ParametersModifier.setAutoWhiteBalanceLock(true/false)` — locks AWB across all frames
- `CameraEx.adjustAperture(int)` — programmatic aperture adjustment for ND simulation
- `ScalarProperties.getSupportedPictureSizes()` — for maximum buffer allocation
- Full `RawDataInfo` consumption (~40 fields) — only app using complete raw metadata
- Model-specific BFNR raw address offsets per camera model

---

## 13.2 Digital Filter
**Package**: `com.sony.imaging.app.digitalfilter`

### Overview
Simulates a graduated neutral density filter by capturing multiple raw images and compositing them at the raw data level using up to 3 simultaneous DSP processors. Supports different filter strengths and gradient positions with real-time preview.

### DSP Programs
- `libsa_ndsa.so` — ND filter compositing (standard)
- `libsa_ndsa_mushashi.so` — Musashi hardware variant

### Processing Pipeline
1. **Force Settings**: Disable OIS
2. **Preview Plugin**: Install DSP as `CameraSequence.setPreviewPlugin()` for real-time graduated ND preview at 30fps
3. **Multi-Shot Capture**: `burstableTakePicture()` for each frame
4. **Raw Compositing**: Up to 3 DSP processors simultaneously, with gamma table manipulation
5. **JNI Memory Operations**: `MemoryCopyRaw`, `MemoryCopyDiademToApplication`, `MemoryCopyApplicationToDiadem`
6. **Development and Storage**

### Unique APIs
- 3 simultaneous DSP processors (`NDSA2Multi`) — only app managing 3 DSPs at once
- `MemoryUtil` JNI for direct ARM-to-Diadem hardware memory copies
- `CameraSequence.setPreviewPlugin(DSP)` — real-time DSP preview pipeline
- `DSP.getPropertyAsInt("program-descriptor")` — program descriptor for boot params

---

## 13.3 Double Exposure
**Package**: `com.sony.imaging.app.doubleexposure`

### Overview
Captures or selects two images and composites them using DSP hardware acceleration with 7 different blend modes. Features real-time live view overlay showing the blend result before capturing the second shot.

### DSP Programs
- `libsa_mle.so` — Multi-layer exposure blending
- `libsa_mle_mushashi.so` — Musashi variant
- `libsa_pre_mle.so` — Pre-processing transforms (flip/rotate)

### Blend Modes
| Mode | SA Value | Description |
|------|----------|-------------|
| Add | 0 | Additive blending (brighter). |
| Mean | 1 | Simple average of both images. |
| Lighten | 2 | Takes the brighter pixel from each image. |
| Darken | 3 | Takes the darker pixel from each image. |
| Screen | 4 | Screen blend (like projecting both images). |
| Multiply | 5 | Multiply blend (darkens). |
| Weighted Mean | 6 | Adjustable weight (0-255) between images. |

### Processing Pipeline
1. **First Image**: Capture or select from gallery
2. **Memory Transfer**: Copy first image to DSP memory (`memoryCopyApplicationToDiadem()`)
3. **Live View Overlay**: Install DESA as preview plugin (`SFR_MODE_LIVEVIEW=1`) — user sees real-time blend of first image + camera feed
4. **Blend Mode Selection**: Update SA parameters in real-time to preview different modes
5. **Second Image Capture**: Standard capture
6. **Final Compositing**: `DESA.execute()` blends both OptimizedImages via DSP
7. **Cleanup**: Stop preview, release resources

### Unique APIs
- Live view overlay with pre-loaded image via `CameraSequence.setPreviewPlugin(DSP)`
- 7 hardware-accelerated blend modes
- SFR mode switching (`SFR_MODE_LIVEVIEW=1` vs `SFR_MODE_PLAYBACK=0`)
- `DeviceMemory` for storing first image in hardware memory during live view
- AXI address masking per hardware generation

---

## 13.4 Photo Retouch
**Package**: `com.sony.imaging.app.photoretouch`

### Overview
Post-capture image editing with comprehensive tools: crop, rotate (90° and arbitrary angle), resize, face skin smoothing, and brightness/contrast/saturation adjustment via DSP-based LCE (Luminance Contrast Enhancement). Saves edited images as new JPEG files.

### DSP Programs
- `libsa_lce.so` — Luminance/Contrast/Saturation Enhancement
- `libsa_lce_musashi.so` — Musashi variant
- `libsa_pack_yuv2rgb.so` — YUV to RGB color conversion
- `libsa_yuv2rgb_musashi_03RGBA.so` — Musashi variant

### Processing Pipeline
1. **Image Selection**: User selects from gallery
2. **Orientation Handling**: Map EXIF orientation to rotation degree
3. **Editing**: Apply filter chain (any combination):
   - **Crop**: `CropImageFilter.setSrcRect(Rect)`
   - **Rotate 90°**: `RotateImageFilter.setRotation(DEGREE_90/180/270)` with `setTrimMode(3)`
   - **Rotate arbitrary**: `RotateImageFilter.setRotation(2.5)` for horizon correction
   - **Resize**: `ScaleImageFilter.setDestSize(w, h)` with preset M/S sizes per aspect ratio
   - **Skin Smoothing**: `FaceNRImageFilter` with face rectangles + ISO + NR level
   - **BCS**: DSP-based LCE pipeline (JNI tone table → DSP → YUV-to-RGB)
4. **Save**: `JpegExporter.encode()` with quality=2 → `AvindexStore.Images.waitAndUpdateDatabase()`

### Unique APIs
- Only app using all 4 image filters together
- `JpegExporter.encode()` — saving edited images as new JPEG files
- `DSP.createImage(width, height)` — creating DSP-backed output buffers
- `AvindexStore.Images.Media.rotateImage()` — EXIF orientation update
- `AvindexStore.Images.waitAndUpdateDatabase()` — blocking DB refresh
- `RotateImageFilter.setRotation(double)` — arbitrary angle rotation
- LCE pipeline with JNI `MakeLceTable()` native call

---

## 13.5 Picture Effect Plus
**Package**: `com.sony.imaging.app.pictureeffectplus`

### Overview
Extends the camera's built-in picture effects with enhanced modes: Part Color Plus (color select with 2 independent channels), Miniature Plus (tilt-shift with 6 focus areas), and Toy Camera Plus (with color variants). Uses hardware color selection API and MiniatureImageFilter.

### Processing Pipelines

**Part Color Plus**:
1. `getPreviewDisplayColor(x, y)` — sample color from live preview
2. `setColorSelectToChannel(0, color)` — assign to channel 0 or 1
3. `setColorSelectMode("extract", channelBitmask)` — enable hardware color extraction
4. Capture — effect is applied at hardware level

**Miniature Plus**:
1. Set `setPictureEffect("miniature")`
2. Capture raw via `burstableTakePicture()`
3. `DefaultDevelopFilter` → developed image
4. `MiniatureImageFilter.setMiniatureArea(1-6)` → tilt-shift blur
5. `sequence.storeImage()`

**Toy Camera Plus**:
1. Set `setPictureEffect("toy_camera")` with variant
2. `startDirectShutter()` / `stopDirectShutter()` — immediate capture
3. Effect applied at hardware level

### Unique APIs
- `CameraEx.setColorSelectToChannel(int, SelectedColor)` — 2-channel color selection
- `CameraEx.getPreviewDisplayColor(int, int)` — live preview color sampling
- `ParametersModifier.setColorSelectMode(String, int)` — hardware color extraction
- `MiniatureImageFilter` with 6 focus areas
- `CameraEx.startDirectShutter()` / `stopDirectShutter()` — direct shutter mode
- `DefaultDevelopFilter.isSupported()` — hardware capability check
- `CameraSequence.Options` with `EXPOSURE_COUNT` and `RECORD_COUNT`

---

## 13.6 Portrait Beauty
**Package**: `com.sony.imaging.app.portraitbeauty`

### Overview
Detects faces in captured images and applies beautification effects: face brightening via DSP-based portrait lighting, soft focus effect, skin smoothing via face noise reduction, and catchlight overlay editing. Uses `ImageAnalyzer` for hardware-accelerated face detection.

### DSP Programs (via app-specific wrappers)
- `SA_PortraitLighting` — Face-targeted brightness enhancement (level 0-6)
- `SA_SoftFocus` — Face-aware soft focus/blur (size categories: 16MP/20MP/24MP/36MP/XGA)

### Processing Pipeline
1. **Capture**: Standard CameraSequence capture
2. **Raw Development**: DefaultDevelopFilter
3. **Face Detection**: `ImageAnalyzer.findFaces(image, faces[8])` → up to 8 faces with rect + eye + mouth positions
4. **ISO Retrieval**: `ParametersModifier.getISOSensitivity()` for NR calibration
5. **Effect Application**:
   - **Soft Focus**: `SA_SoftFocus.execute(input, output)` — face-aware blur
   - **Portrait Lighting**: `SA_PortraitLighting.execute(output)` — face brightening in-place
   - **Skin Smoothing** (alternative): `FaceNRImageFilter` with face rectangles + ISO + NR level

### Unique APIs
- `ImageAnalyzer.findFaces()` — hardware face detection with AnalyzedFace data
- `SA_PortraitLighting` — face-targeted DSP lighting with 7 intensity levels
- `SA_SoftFocus` — resolution-aware DSP soft focus
- `ScalarProperties.getInt("mem.rawimage.size.in.mega.pixel")` — sensor MP for DSP sizing
- Catchlight overlay editing (eye reflection highlights)

---

## 13.7 Live View Grading
**Package**: `com.sony.imaging.app.liveviewgrading`

### Overview
Applies real-time color grading effects to the live view and captured images using extended gamma tables (loaded from CSV files), RGB color matrices, 6-channel color depth control, and white balance shift. Includes 12 presets across three categories.

### 12 Presets

| Category | Presets | CSV Files |
|----------|---------|-----------|
| Standard | Clear, Vivid, Monochrome, Bold | `std_clear.csv`, `std_vivid.csv`, `std_monochrome.csv`, `std_bold.csv` |
| Cinema | Coast, Silky, Misty, Velvety | `cin_coast_side_light.csv`, `cin_silky.csv`, `cin_misty_blue.csv`, `cin_velvety_dew.csv` |
| Extreme | 180, Surf, Big Air, Snow | `ex_180.csv`, `ex_surf_trip.csv`, `ex_big_air.csv`, `ex_snow_tricks.csv` |

### Processing Pipeline
1. **Preset Selection**: User selects from 12 presets
2. **Parameter Application**: 8 adjustable parameters → Contrast, Saturation, Sharpness (via gain mode), Color Temp, Color Tint (via WB shift), Color Depth R/G/B
3. **Fixed Values**: Per-channel color depth (C/M/Y), exposure shift, shading (toy camera vignette)
4. **RGB Matrix**: `setRGBMatrix(int[9])` — 3×3 color transformation
5. **Gamma Table**: Load CSV → create `GammaTable` → `write(InputStream)` → `setExtendedGammaTable()`
6. **Live Preview**: All settings applied in real-time to camera pipeline
7. **Capture**: `DefaultDevelopFilter` applies grading during raw development
8. **Cleanup**: `setExtendedGammaTable(null)` restores default gamma

### Unique APIs
- `CameraEx.createGammaTable()` + `GammaTable.write(InputStream)` — CSV-based custom tone curves
- `GammaTable.setPictureEffectGammaForceOff(true)` — override built-in gamma
- `ParametersModifier.setRGBMatrix(int[])` — 3×3 color transformation
- 6-channel color depth (`setColorDepth` for R/G/B/C/M/Y)
- `setWhiteBalanceShiftLB()` / `setWhiteBalanceShiftCC()` — fine WB shift
- `setSharpnessGainMode()` / `setSharpnessGain()` — fine sharpness control
- `setPictureControlExposureShift()` — exposure adjustment in color grade pipeline

---

## 13.8 Bracket Pro
**Package**: `com.sony.imaging.app.bracketpro`

### Overview
Advanced multi-exposure bracketing beyond the camera's built-in options. Supports 4 bracket modes: aperture bracket, shutter speed bracket, focus bracket, and flash bracket. Captures 3 sequential shots with parameter variation between each.

### Key APIs
- `CameraEx.burstableTakePicture()` — sequential capture for each bracket step
- `CameraEx.adjustAperture(int)` — aperture bracket: steps aperture between shots
- `CameraEx.adjustShutterSpeed(int)` — shutter bracket: steps shutter speed between shots
- `CameraEx.shiftFocusPosition(int)` — focus bracket: shifts focus distance between shots
- `CameraEx.AutoPictureReviewControl` — auto-review timing between bracket shots
- `ScalarProperties.getString("ro.build.device")` — device-specific timing adjustments

### Processing Pipeline
1. User selects bracket mode and step size
2. First shot captured via `burstableTakePicture()`
3. Parameter adjusted (aperture/shutter/focus/flash) by configured step
4. Second shot captured
5. Parameter adjusted again
6. Third shot captured
7. All three images stored to SD card

---

## 13.9 Graduated Filter
**Package**: `com.sony.imaging.app.graduatedfilter`

### Overview
Simulates a physical graduated ND filter by capturing two raw frames with different exposures and compositing them using DSP hardware. Allows different exposure, white balance, and other settings for upper and lower zones of the frame.

### Key APIs
- `CameraSequence` with `CameraSequence.Options` — multi-frame raw capture with memory maps
- `DSP.createProcessor("sony-di-dsp")` — raw compositing engine
- `DSP.setProgram()` — loads NDSA (ND Simulation Accelerator) program
- `MemoryMapConfig` — memory allocation for raw frame buffers
- `DefaultDevelopFilter` — raw-to-RGB development after compositing
- Async parameter changers: `ChangeAperture`, `ChangeSs`, `ChangeIso`, `ChangeWhiteBalance`

### Processing Pipeline
1. Capture first raw frame with "sky" exposure settings
2. DSP stores first frame in work buffer
3. Change exposure parameters for "ground" zone
4. Capture second raw frame
5. DSP composites upper/lower zones with gradient blend
6. `DefaultDevelopFilter` develops final RGB image
7. Store to SD card

---

## 13.10 Light Graffiti
**Package**: `com.sony.imaging.app.lightgraffiti`

### Overview
Optimizes settings for light painting photography. Provides real-time preview of light trails as they accumulate, using DSP-based live preview compositing with 5 filter modes. Controls LED indicators and supports timed exposures with interval shooting.

### Key APIs
- `CameraSequence.setPreviewPlugin(DSP)` — real-time DSP preview during capture
- `CameraSequence.startPreviewSequence()` — begins live compositing preview
- `LGSAMixFilter` — DSP filter with 5 modes: DIFF, LPF, COMP, DIFF_LPF, COMP_SFR
- `Light.setState(int)` — hardware LED indicator control
- `AvindexStore.getVirtualMediaIds()` — virtual media for intermediate storage
- `ScaleImageFilter` — preview scaling
- `OptimizedImage` / `DeviceBuffer` conversion for DSP pipeline

---

## 13.11 Light Shaft
**Package**: `com.sony.imaging.app.lightshaft`

### Overview
Adds artificial crepuscular rays (god rays/light shafts) to captured photos using DSP post-processing. Detects bright light sources in the image and generates rays emanating from them. Supports 4 effect types with adjustable parameters.

### Key APIs
- `DSP.createProcessor("sony-di-dsp")` — creates DSP for ray generation
- `DSP.setProgram("liblightshafts_top.so")` — loads light shaft effect program
- `DSP.getPropertyAsInt("program-descriptor")` — gets program descriptor for boot params
- In-place DSP modification of `OptimizedImage` via device memory addresses
- `DisplayManager` — coordinate mapping for effect placement
- Dual `sequence.storeImage()` — saves both original and processed versions

### 4 Effect Types
Angel rays, Star burst, Flare, Beam — each with different ray patterns and intensity curves.

---

## 13.12 Manual Lens Compensation
**Package**: `com.sony.imaging.app.manuallenscompensation`

### Overview
Allows manual adjustment of lens optical corrections (peripheral shading/vignetting, chromatic aberration, distortion) for vintage or non-native lenses that lack electronic communication with the camera body. Stores profiles per lens in a local SQLite database.

### Key APIs
- `CameraEx.ParametersModifier.setLensCorrectionLevel(String tag, int level)` — sets correction intensity per type
- `CameraEx.ParametersModifier.getSupportedLensCorrections()` — queries available correction types
- `CameraEx.setExifInfo(HashMap)` — writes custom lens EXIF data (lens name, focal length, f-number)
- `LensParameterProvider` — custom ContentProvider backed by SQLite for lens profile storage
- 6 correction tags: peripheral illumination (R/G/B channels), chromatic aberration, distortion

---

## 13.13 Smooth Reflection 2
**Package**: `com.sony.imaging.app.smoothreflection`

### Overview
Second version of Smooth Reflection with extended model-specific support. Handles different raw memory layouts across camera models (HX400V, HX60V, RX100M3, ILCE-5000, NEX-5R/5T/6). Uses AVIP direct memory access for older models.

### Key APIs
- Same DSP raw averaging pipeline as Smooth Reflection v1
- `DeviceMemory` — AVIP direct memory access for NEX-5R/5T/6 models
- `MemoryMapConfig.setAllocationPolicy()` — PF v2 camera memory management
- Model-specific BFNR offset calculations with different formulas per camera model
- `ScalarProperties.getProperty("model.name")` — model detection for offset selection

### Model-Specific Offsets
| Model | Offset | Condition |
|-------|--------|-----------|
| DSC-RX100M3 | `(canvasSizeX + 64) * 6` | LENR + SS >= 8s |
| ILCE-5000 | `canvasSizeX * 8` | LENR + SS >= 1s |
| DSC-HX400V/HX60V | `canvasSizeX * 6` | LENR + SS >= 8s |

---

## 13.14 Sound Photo
**Package**: `com.sony.imaging.app.soundphoto`

### Overview
Records ambient audio before and after the shutter press, embedding the audio data into the JPEG file. Uses hardware DSP for LPCM audio encoding and zoom noise reduction. Supports multiple microphone types with automatic detection.

### Key APIs
- `AudioRecord` — circular buffer recording with frame-level position control
- `AudioRecord.EncoderParameters` — 7 LPCM encoder configuration keys
- `AudioTrack` — DSP-based audio playback
- `AudioManager` — microphone type detection (6 mic types) with change callbacks
- `DSP` — dual DSP: LPCM encoder + zoom noise reduction (conditional on `dsp.zoomnr.supported`)
- `ComposeAudioImage` — JNI native method for embedding audio into JPEG

---

## 13.15 Smart Remote Control (SRCtrl)
**Package**: `com.sony.imaging.app.srctrl`

### Overview
Runs an HTTP server inside the camera, enabling smartphone apps to remotely control shutter, exposure, live view, and other camera functions via a WebAPI protocol. Supports device discovery (SSDP), authentication (AuthLib), NFC pairing, and live view binary streaming.

### Key APIs
- **Leza HTTP Server** (`com.sony.scalar.lib.leza.*`) — `ServerBuilder`, `HttpProcessBuilder`, servlet routing
- **WebAPI** — 90+ method endpoints across versions v1.0-v1.4 (actTakePicture, setExposureMode, startLiveview, etc.)
- **AuthLibManager** — device authentication and pairing
- **SsdpDevice** — UPnP service advertisement for smartphone discovery
- **Nfc** controller — touch-to-connect NFC pairing
- **ScalarInput** / **ScalarProperties** — button events and model detection
- Live view binary streaming with AF frame overlay data

### Architecture
Unlike other apps, SRCtrl is a **server application** — it doesn't directly control the camera hardware but exposes camera controls via HTTP endpoints that smartphone apps call.

---

## 13.16 Star Trails
**Package**: `com.sony.imaging.app.startrails`

### Overview
Automates long-duration star trail photography by capturing multiple exposures and compositing them in-camera. Can output both still images and AVI video compilations. Uses DSP for frame compositing and MediaRecorder for video encoding.

### Key APIs
- `CameraEx` — interval capture control
- `CameraSequence` with `DefaultDevelopFilter` — raw development per frame
- `DSP` — frame compositing (lighten blend for star trails)
- `MediaRecorder` — AVI video encoding from composite frames
- `AvindexStore` — media database with `AvindexUpdateObserver` for write monitoring
- `PlainCalendar` / `TimeUtil` / `PlainTimeZone` — precise timing for interval shots
- `MediaInfo` — SD card type detection
- `MemoryMapConfig` — memory allocation for multi-frame buffers

---

## 13.17 Sync to Smartphone
**Package**: `com.sony.imaging.app.synctosmartphone`

### Overview
Automatically transfers photos to a registered smartphone when the camera powers off. Runs a WebAPI content sync server, discovers paired devices via SSDP, authenticates via AuthLib, and streams images over Wi-Fi Direct.

### Key APIs
- **WebAPI contentsync v1.0** — `actPairing`, `getInterfaceInformation`, `notifySyncStatus`
- **SsdpDevice** / **DdController** — UPnP device discovery and advertisement
- **AuthLibManager** — smartphone pairing and authentication
- **ScalarWifiInfo** — WiFi product code and connection management
- **AvindexStore** — `loadMedia()`, `waitLoadMediaComplete()`, `getImageInfo()` for media access
- `Settings` / `Status` — system configuration and HDMI settings

---

## 13.18 Time-lapse
**Package**: `com.sony.imaging.app.timelapse`

### Overview
Captures photos at configurable intervals and compiles them into an AVI or MP4 movie file in-camera. Supports AE tracking between frames to handle changing light conditions. Optional Angle Shift add-on provides dynamic camera angle changes between frames.

### Key APIs
- `CameraEx` — interval capture with exposure tracking via `TimelapseExposureController`
- `CameraSequence` with `DefaultDevelopFilter.setRawFileStoreEnabled()` — raw storage control
- `MediaRecorder` — AVI/MP4 video compilation
- `RotateImageFilter` — frame rotation for Angle Shift add-on
- `DisplayManager` — display control during long capture sessions
- `AudioManager` — audio settings for video compilation
- `AvindexStore` — media database for output video
- `Kikilog` — telemetry for capture statistics

---

## 13.19 Touchless Shutter
**Package**: `com.sony.imaging.app.each`

### Overview
Uses the EVF (electronic viewfinder) proximity sensor as a touchless shutter trigger — waving your hand in front of the viewfinder fires the shutter without physical contact, eliminating camera shake. Also supports live bulb mode with real-time exposure preview.

### Key APIs
- `DisplayManager` / `DisplayEventListener` — **event ID 4096** is the EVF proximity sensor trigger (core touchless mechanism)
- `CameraEx.cancelExposure()` — ends bulb exposure
- `CameraEx.startSelfTimerShutter()` — self-timer capture
- `KeyStatus` — hardware key state queries
- `Kikilog` — telemetry with 4 sub-IDs for boot time, capture events, bulb duration
- `MediaRecorder` — movie recording mode
- `AudioManager` — audio control for video

### Capture Modes
Normal capture, composite 3-exposure, effect mode, bulb/live bulb, and movie recording — all triggered by EVF proximity sensor event.

---

## Reference App Comparison Summary

| App | Feature | DSP | Key Filters | Key APIs |
|-----|---------|-----|-------------|----------|
| bracket-pro | Multi-exposure bracketing | No | DefaultDevelop | adjustAperture, adjustShutterSpeed, shiftFocusPosition, AutoPictureReviewControl |
| digital-filter | Graduated ND | Yes (×3) | DSP + MemoryUtil JNI | Preview Plugin, 3 simultaneous DSPs |
| double-exposure | Multi-overlay (7 blend modes) | Yes | DSP blend + transforms | Live view overlay, SFR modes, DeviceMemory |
| graduated-filter | Graduated ND filter | Yes | DSP raw compositing | CameraSequence, MemoryMapConfig, DefaultDevelopFilter, NDSA program |
| light-graffiti | Light painting | Yes | LGSAMixFilter (5 modes) | CameraSequence preview plugin, Light indicator, AvindexStore virtual media |
| light-shaft | Crepuscular rays (god rays) | Yes | DSP in-place effect | liblightshafts_top.so, DSP.getPropertyAsInt, DisplayManager coordinates |
| live-view-grading | Real-time LUT color grading | No | DefaultDevelop | GammaTable, RGBMatrix, ColorDepth, WBShift |
| manual-lens-compensation | Lens correction profiles | No | None | setLensCorrectionLevel, setExifInfo, LensParameterProvider |
| photo-retouch | Edit tools (crop/rotate/BCS) | Yes | Scale, Crop, Rotate, FaceNR | JpegExporter, LCE, DSP.createImage, AvindexStore save |
| picture-effect-plus | Creative fx (Part Color, Miniature) | No | Miniature, DefaultDevelop | ColorSelect, getPreviewDisplayColor, DirectShutter |
| portrait-beauty | Face beautify | Yes | FaceNR + Analyzer | ImageAnalyzer, SA_PortraitLighting, SA_SoftFocus |
| smooth-reflection | ND simulation (2-256 frame avg) | Yes | DSP raw blend | adjustAperture, setRemoteControlMode, GammaTable, RawDataInfo |
| smooth-reflection-2 | ND simulation v2 (model-specific) | Yes | DSP raw blend | DeviceMemory AVIP, model-specific BFNR offsets, MemoryMapConfig |
| sound-photo | Audio + photo capture | Yes | DSP audio encoder | AudioRecord, AudioTrack, AudioManager, ComposeAudioImage JNI |
| srctrl | Smartphone remote control | No | None | WebAPI server (Leza HTTP), SSDP, AuthLib, NFC, live view streaming |
| star-trails | Star trail compositing | Yes | Scale, Crop, Miniature, SoftFocus | MediaRecorder (AVI), AvindexStore, PlainCalendar, DSP compositing |
| sync-to-smart-phone | Auto-sync to smartphone | No | None | WebAPI (contentsync), SSDP/DdController, AuthLib, ScalarWifiInfo |
| time-lapse | Interval capture to video | Yes | RotateImageFilter | MediaRecorder (AVI/MP4), DisplayManager, TimelapseExposureController |
| touchless-shutter | EVF sensor trigger + live bulb | No | None | DisplayManager event ID 4096 (EVF sensor), cancelExposure, KeyStatus |

**Shared ~80%**: Framework, camera lifecycle, menus, playback, display management, button handling.
**App-specific ~20%**: Capture pipeline, DSP programs, edit UI, effect-specific controllers.

---
*Analysis: ~10,000+ Java files (19 Sony apps) + 323 Jasmin stubs (OpenMemories-Framework)*
