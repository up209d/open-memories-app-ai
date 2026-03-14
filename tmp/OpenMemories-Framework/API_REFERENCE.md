# OpenMemories-Framework: Complete Sony API Stub Reference

## Overview

The OpenMemories-Framework project (by ma1co) is a reverse-engineering effort to document and provide stub implementations of Sony's proprietary `com.sony.scalar.*` Android-based camera firmware APIs. The stubs are written in **Jasmin JVM assembly** (`.j` files) and define the complete API surface without implementation, enabling compilation against Sony's proprietary classes. Java **wrapper classes** provide a cleaner API on top of the native Sony classes.

**Total files documented:** ~323 Jasmin stub files + 6 Java framework files

---

## Table of Contents

1. [Java Framework Wrappers](#1-java-framework-wrappers)
2. [android.app Package](#2-androidapp-package)
3. [com.sony.jp Package](#3-comsonymjp-package)
4. [com.sony.scalar.graphics Package](#4-comsонyscalargraphics-package)
5. [com.sony.scalar.graphics.imagefilter Package](#5-comsонyscalargraphicsimagefilter-package)
6. [com.sony.scalar.hardware Package (Core Camera)](#6-comsонyscalarhardware-package-core-camera)
7. [com.sony.scalar.hardware.avio Package](#7-comsонyscalarhardwareavio-package)
8. [com.sony.scalar.hardware.indicator Package](#8-comsонyscalarhardwareindicator-package)
9. [com.sony.scalar.media Package](#9-comsонyscalarmedia-package)
10. [com.sony.scalar.meta Package](#10-comsонyscalarmeta-package)
11. [com.sony.scalar.provider Package](#11-comsонyscalarprovider-package)
12. [com.sony.scalar.sysutil Package](#12-comsонyscalarsysutil-package)
13. [com.sony.scalar.sysutil.didep Package](#13-comsонyscalarsysutildidep-package)
14. [com.sony.scalar.widget Package](#14-comsонyscalarwidget-package)
15. [com.sony.wifi Package](#15-comsонywifi-package)
16. [com.sony.scalar.sysnetutil Package](#16-comsонyscalarsysnetutil-package)
17. [com.sony.scalar.lib.ssdpdevice.v1_1 Package](#17-comsонyscalarlibssdpdevicev1_1-package)

---

## 1. Java Framework Wrappers

These Java files provide a cleaner API over Sony's native classes, with fallback implementations for non-Sony devices.

### DateTime.java
**File:** `framework/src/main/java/com/github/ma1co/openmemories/framework/DateTime.java`
**Purpose:** Query current date and time. Uses Sony's `TimeUtil`/`PlainCalendar` on cameras; standard Java Calendar on other devices.

**Inner Classes:**
- `CameraDateTime extends DateTime` - Sony camera implementation using `TimeUtil.getCurrentCalendar()` and `TimeUtil.getCurrentTimeZone()`

**Methods:**
| Return Type | Method | Description |
|---|---|---|
| `DateTime` | `static getInstance()` | Returns appropriate DateTime instance |
| `Calendar` | `getCurrentTime()` | Returns current time and timezone |

---

### DeviceInfo.java
**File:** `framework/src/main/java/com/github/ma1co/openmemories/framework/DeviceInfo.java`
**Purpose:** Access non-changing device properties (model name, serial number, firmware version). Wraps `ScalarProperties` API on cameras.

**Inner Classes:**
- `CameraDeviceInfo extends DeviceInfo` - Sony camera implementation using `ScalarProperties`
- `enum Category { A_MOUNT_CAMERA, E_MOUNT_CAMERA, STILL_CAMERA, ACTION_CAMERA, OTHER }`

**Methods:**
| Return Type | Method | Description |
|---|---|---|
| `DeviceInfo` | `static getInstance()` | Returns appropriate DeviceInfo instance |
| `boolean` | `isCamera()` | True if device is a Sony camera |
| `String` | `getBrand()` | Device brand (usually "sony") |
| `String` | `getModel()` | Model name (e.g. "ILCE-7RM2") |
| `Category` | `getCategory()` | Device category enum |
| `String` | `getProductCode()` | Non-unique product code |
| `String` | `getSerialNumber()` | Unique serial number |
| `String` | `getFirmwareVersion()` | Firmware version (e.g. "1.00") |
| `int` | `getHardwareVersion()` | Hardware revision (1 or 2) |
| `int` | `getApiVersion()` | Proprietary Sony API revision |
| `String` | `getAndroidVersion()` | Android version string |
| `int` | `getAndroidSdkVersion()` | Android SDK int |
| `String` | `getAndroidIncrementalVersion()` | Android build number |

---

### DisplayManager.java
**File:** `framework/src/main/java/com/github/ma1co/openmemories/framework/DisplayManager.java`
**Purpose:** Access display information (screen, finder, HDMI). Wraps `com.sony.scalar.hardware.avio.DisplayManager` on cameras.

**Inner Classes:**
- `CameraDisplayManager extends DisplayManager` - Sony implementation with HDMI/finder switching
- `enum ColorDepth { HIGH, LOW }` - ARGB_8888 / ARGB_4444
- `enum Display { SCREEN, FINDER, HDMI, NONE }`
- `class DisplayInfo` - Fields: `width`, `height`, `aspectRatio`
- `interface Listener` - Method: `displayChanged(Display)`

**Methods:**
| Return Type | Method | Description |
|---|---|---|
| `DisplayManager` | `static create(Context)` | Factory method |
| `void` | `addListener(Listener)` | Register event listener |
| `void` | `removeListener(Listener)` | Unregister event listener |
| `void` | `setColorDepth(ColorDepth)` | Set frame buffer color depth |
| `DisplayInfo` | `getDisplayInfo(Display)` | Get display dimensions |
| `Display` | `getActiveDisplay()` | Get active display type |
| `void` | `setActiveDisplay(Display)` | Switch to display |
| `DisplayInfo` | `getActiveDisplayInfo()` | Get active display dimensions |
| `DisplayInfo` | `getFrameBufferInfo()` | Get frame buffer dimensions |
| `void` | `release()` | Release resources |

---

### ImageInfo.java
**File:** `framework/src/main/java/com/github/ma1co/openmemories/framework/ImageInfo.java`
**Purpose:** Hold information about an image. Wraps `AvindexContentInfo` on cameras.

**Inner Classes:**
- `CameraImageInfo extends ImageInfo` - Uses `AvindexStore.Images.Media.getImageInfo()`
- `AndroidImageInfo extends ImageInfo` - Uses Android's `ExifInterface`

**Methods:**
| Return Type | Method | Description |
|---|---|---|
| `ImageInfo` | `static create(Context, Uri, long)` | Factory method |
| `long` | `getImageId()` | Unique image ID |
| `String` | `getFilename()` | e.g. "DSC00001.JPG" |
| `String` | `getFolder()` | e.g. "100MSDCF" |
| `Date` | `getDate()` | Date picture was taken |
| `int` | `getWidth()` | Image width |
| `int` | `getHeight()` | Image height |
| `double` | `getAperture()` | e.g. 8.0 |
| `double` | `getExposureTime()` | In seconds, e.g. 0.008 |
| `double` | `getFocalLength()` | In mm, e.g. 50 |
| `int` | `getIso()` | e.g. 100 |
| `int` | `getOrientation()` | EXIF orientation |
| `InputStream` | `getThumbnail()` | ~160x120px |
| `InputStream` | `getPreviewImage()` | ~1616x1080px |
| `InputStream` | `getFullImage()` | Full resolution |

---

### MediaManager.java
**File:** `framework/src/main/java/com/github/ma1co/openmemories/framework/MediaManager.java`
**Purpose:** Query images on the memory card. Wraps `AvindexStore` on cameras.

**Inner Classes:**
- `CameraMediaManager extends MediaManager` - Uses `AvindexStore.Images.Media`
- `AndroidMediaManager extends MediaManager` - Uses `MediaStore.Images.Media`

**Methods:**
| Return Type | Method | Description |
|---|---|---|
| `MediaManager` | `static create(Context)` | Factory method |
| `Uri` | `getImageContentUri()` | Content provider URI |
| `Cursor` | `queryImages()` | All images, default order |
| `Cursor` | `queryNewestImages()` | Newest first |
| `Cursor` | `queryOldestImages()` | Oldest first |
| `long` | `getImageId(Cursor)` | Get ID from cursor |
| `ImageInfo` | `getImageInfo(long)` | Get info by ID |
| `ImageInfo` | `getImageInfo(Cursor)` | Get info from cursor |

---

### BiMap.java
**File:** `framework/src/main/java/com/github/ma1co/openmemories/util/BiMap.java`
**Purpose:** Bidirectional map utility used by wrapper classes.

---

## 2. android.app Package

### DAConnectionManager
**File:** `stubs/src/main/jasmin/android/app/DAConnectionManager.j`
**Stubs:** `android.app.DAConnectionManager`
**Purpose:** Device Accessory (DA) connection management

**Methods:**
| Return Type | Method |
|---|---|
| `DAConnectionManager` | `static getInstance(Context)` |
| `void` | `registerDAConnectionListener(int, String, DAConnectionListener)` |
| `void` | `unregisterDAConnectionListener(DAConnectionListener)` |
| `void` | `sendData(int, String, byte[])` |

---

### SyncAndroidManager
**File:** `stubs/src/main/jasmin/android/app/SyncAndroidManager.j`
**Stubs:** `android.app.SyncAndroidManager`
**Purpose:** Android sync management

---

### WifiSettingManager
**File:** `stubs/src/main/jasmin/android/app/WifiSettingManager.j`
**Stubs:** `android.app.WifiSettingManager`
**Purpose:** WiFi settings management

---

## 3. com.sony.jp Package

### AndroidSystemStateService
**File:** `stubs/src/main/jasmin/com/sony/jp/AndroidSystemStateService.j`
**Purpose:** System state service

### SnscBootTime
**File:** `stubs/src/main/jasmin/com/sony/jp/SnscBootTime.j`
**Purpose:** Boot time tracking

---

## 4. com.sony.scalar.graphics Package

### AvindexGraphics
**File:** `stubs/src/main/jasmin/com/sony/scalar/graphics/AvindexGraphics.j`
**Purpose:** AV index graphics operations for image data retrieval

**Methods:**
| Return Type | Method |
|---|---|
| `byte[]` | `static getScreenNail(String)` |

---

### ImageAnalyzer
**File:** `stubs/src/main/jasmin/com/sony/scalar/graphics/ImageAnalyzer.j`
**Purpose:** Image analysis with face detection

**Inner Classes:**
- `AnalyzedFace` - Fields: `faceAngle FaceAngle`, `faceInfo FaceInfo`

**Methods:**
| Return Type | Method |
|---|---|
| `void` | `<init>()` |
| `AnalyzedFace[]` | `getAnalyzedFaces()` |

---

### JpegExporter
**File:** `stubs/src/main/jasmin/com/sony/scalar/graphics/JpegExporter.j`
**Purpose:** JPEG export with error codes and callbacks

**Inner Classes:**
- `ErrCd` - Error code enum (OK=0, OVER_FILE_SIZE_LIMIT=1, ERR_INVALID_PARAM=2, ..., ERR_OTHER=7)
- `Options` - Fields: `height int`, `jpegQuality int`, `width int`; Methods: `reset()`, `setExif(byte[])`
- `onExportEventListener` (interface) - Method: `onExportEvent(ErrCd, JpegExporter)`

**Methods:**
| Return Type | Method |
|---|---|
| `void` | `<init>()` |
| `void` | `decode(OptimizedImage)` |
| `void` | `encode(DeviceBuffer, Options)` |
| `void` | `release()` |
| `void` | `reset()` |
| `void` | `setOnExportEventListener(onExportEventListener)` |

---

### OptimizedImage
**File:** `stubs/src/main/jasmin/com/sony/scalar/graphics/OptimizedImage.j`
**Purpose:** Hardware-optimized image class (extends `DeviceBuffer`)

**Methods:**
| Return Type | Method |
|---|---|
| `int` | `getColorFormat()` |
| `int` | `getHeight()` |
| `int` | `getOriginalHeight()` |
| `int` | `getOriginalWidth()` |
| `int` | `getWidth()` |

---

### OptimizedImageFactory
**File:** `stubs/src/main/jasmin/com/sony/scalar/graphics/OptimizedImageFactory.j`
**Purpose:** Factory for creating `OptimizedImage` instances

**Inner Classes:**
- `Options` - Fields: `height int`, `width int`

**Methods:**
| Return Type | Method |
|---|---|
| `OptimizedImage` | `static createOptimizedImage(String, Options)` |

---

## 5. com.sony.scalar.graphics.imagefilter Package

### ImageFilter (base class)
**File:** `stubs/src/main/jasmin/com/sony/scalar/graphics/imagefilter/ImageFilter.j`

**Methods:**
| Return Type | Method |
|---|---|
| `void` | `apply(OptimizedImage, OptimizedImage)` |
| `void` | `release()` |

---

### ContrastPlusImageFilter
Extends `ImageFilter`. Constructor: `<init>()`

### CropImageFilter
Extends `ImageFilter`. Constructor: `<init>(Rect)`

### FaceNRImageFilter
Extends `ImageFilter`. Constructor: `<init>(AnalyzedFace[])`

### MiniatureImageFilter
Extends `ImageFilter`. Constructor: `<init>(int, int, int, int, int)`

### RedEyeImageFilter
Extends `ImageFilter`. Constructor: `<init>(AnalyzedFace[])`

### RotateImageFilter
Extends `ImageFilter`. Constructor: `<init>(ROTATION_DEGREE)`

**Inner Class:** `ROTATION_DEGREE` (enum) - Values: `DEGREE_0`, `DEGREE_180`, `DEGREE_270`, `DEGREE_90`

### ScaleImageFilter
Extends `ImageFilter`. Constructor: `<init>(int, int)`

### SoftFocusImageFilter
Extends `ImageFilter`. Constructor: `<init>(int)`

### SuperResolutionImageFilter
Extends `ImageFilter`. Constructor: `<init>(int, int)`

---

## 6. com.sony.scalar.hardware Package (Core Camera)

### CameraEx
**File:** `stubs/src/main/jasmin/com/sony/scalar/hardware/CameraEx.j`
**Purpose:** Central camera control class -- the main entry point for all camera operations

**Methods:**
| Return Type | Method |
|---|---|
| `CameraEx` | `static open(int, OpenCallback)` |
| `void` | `release()` |
| `ParametersModifier` | `createParametersModifier(Camera.Parameters)` |
| `void` | `cancelTakePicture()` |
| `void` | `startDirectShutter(int, DirectShutterStoppedCallback)` |
| `void` | `stopDirectShutter()` |
| `void` | `burstableTakePicture()` |
| `void` | `normalTakePicture()` |
| `void` | `getNormalDeviceBuffer(...)` |
| `void` | `setAutoPictureReviewControl(AutoPictureReviewControl)` |
| `ExifInfo` | `getExifInfo()` |
| `LensInfo` | `getLensInfo()` |
| `ExposureInfo` | `getExposureInfo()` |
| `ApertureInfo` | `getApertureInfo()` |
| `ShutterSpeedInfo` | `getShutterSpeedInfo()` |
| `ZoomInfo` | `getZoomInfo()` |
| `SupportFocus` | `getSupportFocus()` |
| `SupportZoom` | `getSupportZoom()` |
| `void` | `setAutoISOSensitivityListener(AutoISOSensitivityListener)` |
| `void` | `setShutterListener(ShutterListener)` |
| `void` | `setJpegListener(JpegListener)` |
| `void` | `setFaceDetectionListener(FaceDetectionListener)` |
| `void` | `setPreviewAnalizeListener(PreviewAnalizeListener)` |
| `void` | `setPreviewMagnificationListener(PreviewMagnificationListener)` |
| `void` | `setFocusDriveListener(FocusDriveListener)` |
| `void` | `setFocusAreaListener(FocusAreaListener)` |
| `void` | `setTrackingFocusListener(TrackingFocusListener)` |
| `void` | `setSettingChangedListener(SettingChangedListener)` |
| `void` | `setApertureChangeListener(ApertureChangeListener)` |
| `void` | `setShutterSpeedChangeListener(ShutterSpeedChangeListener)` |
| `void` | `setZoomChangeListener(ZoomChangeListener)` |
| `void` | `setFlashChargingStateListener(FlashChargingStateListener)` |
| `void` | `setMeteringModeInfoListener(MeteringModeInfoListener)` |
| `void` | `setPictureReviewInfoListener(PictureReviewInfoListener)` |
| `void` | `setStoreImageCompleteListener(StoreImageCompleteListener)` |
| `void` | `setErrorCallback(ErrorCallback)` |
| `void` | `setEquipmentCallback(EquipmentCallback)` |
| ...and many more listener setters |

---

### CameraEx$ParametersModifier (MASSIVE -- ~1727 lines)
**File:** `stubs/src/main/jasmin/com/sony/scalar/hardware/CameraEx$ParametersModifier.j`
**Purpose:** Hundreds of getter/setter pairs for every camera parameter

**Key Constants (selected):**
| Constant | Value |
|---|---|
| `AE_LOCK_OFF` | `"unlocked"` |
| `AE_LOCK_ON` | `"locked"` |
| `SCENE_MODE_AUTO` | `"auto"` |
| `SCENE_MODE_PORTRAIT` | `"portrait"` |
| `SCENE_MODE_LANDSCAPE` | `"landscape"` |
| `SCENE_MODE_NIGHT` | `"night"` |
| `SCENE_MODE_SPORTS` | `"sports"` |
| `CREATIVE_STYLE_STANDARD` | `"Standard"` |
| `FOCUS_MODE_AUTO` | `"auto"` |
| `FOCUS_MODE_CONTINUOUS` | `"continuous-picture"` |
| `FOCUS_MODE_DMF` | `"dmf"` |
| `FOCUS_MODE_MANUAL` | `"manual"` |
| `WHITE_BALANCE_AUTO` | `"auto"` |
| `FLASH_MODE_AUTO` | `"auto"` |
| `FLASH_MODE_OFF` | `"off"` |
| `FLASH_MODE_ON` | `"on"` |

**Key Methods (selected from hundreds):**
| Return Type | Method |
|---|---|
| `String` | `getAELock()` / `setAELock(String)` |
| `int` | `getISOSensitivity()` / `setISOSensitivity(int)` |
| `String` | `getSceneMode()` / `setSceneMode(String)` |
| `String` | `getWhiteBalance()` / `setWhiteBalance(String)` |
| `String` | `getFlashMode()` / `setFlashMode(String)` |
| `String` | `getFocusMode()` / `setFocusMode(String)` |
| `String` | `getDriveMode()` / `setDriveMode(String)` |
| `int` | `getExposureCompensation()` / `setExposureCompensation(int)` |
| `String` | `getCreativeStyle()` / `setCreativeStyle(String)` |
| `String` | `getPictureEffect()` / `setPictureEffect(String)` |
| `boolean` | `isSilentShutterMode()` / `setSilentShutterMode(boolean)` |
| `String` | `getRawRecMode()` / `setRawRecMode(String)` |
| `String` | `getImageQuality()` / `setImageQuality(String)` |
| `String` | `getImageSize()` / `setImageSize(String)` |
| `String` | `getAspectRatio()` / `setAspectRatio(String)` |
| `int` | `getShutterSpeed()` / `setShutterSpeed(int)` |
| `int` | `getAperture()` / `setAperture(int)` |
| `int` | `getSelfTimer()` / `setSelfTimer(int)` |
| `String` | `getBracketMode()` / `setBracketMode(String)` |
| `int` | `getBracketOrder()` / `setBracketOrder(int)` |
| `int` | `getBracketStep()` / `setBracketStep(int)` |
| `String` | `getColorTemperature()` / `setColorTemperature(String)` |
| `String` | `getNoiseReduction()` / `setNoiseReduction(String)` |
| `String` | `getAutoHDR()` / `setAutoHDR(String)` |
| `String` | `getDROMode()` / `setDROMode(String)` |
| `String` | `getMeteringMode()` / `setMeteringMode(String)` |
| `String` | `getSteadyShot()` / `setSteadyShot(String)` |
| `String` | `getZebraLevel()` / `setZebraLevel(String)` |
| `String` | `getSmileDetection()` / `setSmileDetection(String)` |
| `String` | `getFaceDetection()` / `setFaceDetection(String)` |
| `String` | `getGridLine()` |
| `String` | `getContrastLevel()` / `setContrastLevel(String)` |
| `String` | `getSaturationLevel()` / `setSaturationLevel(String)` |
| `String` | `getSharpnessLevel()` / `setSharpnessLevel(String)` |
| `List<String>` | `getSupportedSceneModes()` |
| `List<String>` | `getSupportedWhiteBalances()` |
| `List<String>` | `getSupportedFlashModes()` |
| `List<String>` | `getSupportedFocusModes()` |
| `List<String>` | `getSupportedDriveModes()` |
| `List<String>` | `getSupportedCreativeStyles()` |
| `List<String>` | `getSupportedPictureEffects()` |
| `List<String>` | `getSupportedImageQualities()` |
| `List<String>` | `getSupportedImageSizes()` |
| `List<String>` | `getSupportedAspectRatios()` |
| ...and many more getSupported*() methods |

---

### CameraEx Inner Data Classes

#### CameraEx$OpenOptions
Fields: `forceOpen boolean`

#### CameraEx$OpenCallback (interface)
Method: `onReopened(CameraEx)`

#### CameraEx$ExifInfo
Fields: `imageData byte[]`

#### CameraEx$LensInfo
Fields: `currentFocalLength int`, `model String`, `type int`

#### CameraEx$ZoomInfo
Fields: `currentPosition int`, `maxPosition int`

#### CameraEx$ExposureInfo
Fields: `currentEV int`

#### CameraEx$ApertureInfo
Fields: `currentAperture int`

#### CameraEx$ShutterSpeedInfo
Fields: `currentShutterSpeed int`

#### CameraEx$FocusPosition
Fields: `currentPosition int`, `maxPosition int`

#### CameraEx$WideTele
Fields: `wide int`, `tele int`

#### CameraEx$SelectedColor
Fields: `Cb int`, `Cr int`, `Phase int`, `Range int`, `Saturation int`, `Y int`

#### CameraEx$GammaTable
Extends `DeviceBuffer`. Methods: `isPictureEffectGammaForceOff() boolean`, `setPictureEffectGammaForceOff(boolean)`

#### CameraEx$ReviewInfo
Fields: `hist Histogram`, `photo TakenPhotoInfo`

#### CameraEx$ExternalFlashInfo
Fields: `afLightAutoStatus int`, `hssStatus int`, `redEyeReductionStatus int`, `wirelessStatus int`

#### CameraEx$AutoPictureReviewControl
Methods: `getPictureReviewTime()`, `setPictureReviewTime(int)`, `getMaxPictureReviewTime()`, `setPictureReviewInfoCb(PictureReviewInfoListener)`

#### CameraEx$StoreImageInfo
Fields: `capturedDate int[]`, `directoryName String`, `fileName String`, `fileId int`, `mediaId String`

#### CameraEx$FocusAreaInfos
Methods: `getFocusAreaRectInfos() FocusAreaRectInfo[]`

#### CameraEx$FocusAreaRectInfo
Fields: `bottom int`, `left int`, `right int`, `top int`, `state int`

#### CameraEx$CustomWhiteBalanceInfo
Fields: `colorCompensation int`, `colorTemperature int`, `lightBalance int`

#### CameraEx$AnalizedData
Contains histogram data fields.

#### CameraEx$PrvwMagInfo
Preview magnification info.

#### CameraEx$PrvwMagChange
Fields: `isSmooth boolean`, `position FocusPosition`

#### CameraEx$TrackingFocusInfo
Constants: `DETECTED`, `SCANNING`, `OFF`, `TRACK_ON`, `TRACK_OFF`

#### CameraEx$MeteringModeInfo
Fields: `spotX int`, `spotY int`

#### CameraEx$SupportFocus
Fields: `autoFocus boolean`, `continuousAF boolean`

#### CameraEx$SupportZoom
Fields: `DMF boolean`, `zoom boolean`

---

### CameraEx Listener Interfaces

| Interface | Method Signature |
|---|---|
| `FocusDriveListener` | `onChanged(FocusPosition, CameraEx)` |
| `OnCaptureStatusListener` | `onChanged(int, CameraEx)` -- Constants: `REASON_CANCEL=2`, `REASON_COMPLETE=0`, `REASON_ERROR=1` |
| `FocusAreaListener` | `onChanged(FocusAreaInfos, CameraEx)` |
| `FocusLightStateListener` | `onChanged(int, CameraEx)` -- Constants: `AF_LAMP_OFF=0`, `AF_LAMP_ON=1` |
| `PreviewMagnificationListener` | `onChanged(PrvwMagInfo, CameraEx)` |
| `PreviewStartListener` | `onChanged(boolean, CameraEx)` |
| `ApertureChangeListener` | `onChanged(ApertureInfo, CameraEx)` |
| `ApertureStfFNumberListener` | `onChanged(int, CameraEx)` |
| `AutoApscModeListener` | `onChanged(int, CameraEx)` |
| `AutoCameraGainListener` | `onChanged(int, CameraEx)` |
| `AutoFocusDoneListener` | `onChanged(int, CameraEx)` |
| `AutoFocusStartListener` | `onChanged(CameraEx)` |
| `AutoISOSensitivityListener` | `onChanged(int, CameraEx)` |
| `AutoSceneModeListener` | `onChanged(String, CameraEx)` |
| `ExposureCompleteListener` | `onChanged(CameraEx)` |
| `FaceDetectionListener` | `onChanged(FaceInfo[], CameraEx)` |
| `FlashChargingStateListener` | `onChanged(boolean, CameraEx)` |
| `FlashEmittingListener` | `onChanged(boolean, CameraEx)` |
| `FocalLengthChangeListener` | `onChanged(int, CameraEx)` |
| `IntervalRecListener` | `onChanged(int, int, CameraEx)` |
| `JpegListener` | `onJpeg(byte[], CameraEx)` |
| `MeteringModeInfoListener` | `onChanged(MeteringModeInfo, CameraEx)` |
| `MotionRecognitionListener` | `onChanged(int, CameraEx)` |
| `MotionShotResultListener` | `onChanged(int, CameraEx)` |
| `NDFilterStatusListener` | `onChanged(int, CameraEx)` |
| `PictureReviewInfoListener` | `onChanged(ReviewInfo, CameraEx)` |
| `PictureReviewStartListener` | `onChanged(CameraEx)` |
| `PowerZoomListener` | `onChanged(int, CameraEx)` |
| `PreviewAnalizeListener` | `onChanged(AnalizedData, CameraEx)` |
| `ProgramLineListener` | `onChanged(int, int, CameraEx)` |
| `ProgramLineRangeOverListener` | `onChanged(int, CameraEx)` |
| `SceneRecognitionJudgeListener` | `onChanged(int, CameraEx)` |
| `SettingChangedListener` | `onChanged(CameraEx)` |
| `ShutterListener` | `onShutter(int, CameraEx)` |
| `ShutterSpeedChangeListener` | `onChanged(ShutterSpeedInfo, CameraEx)` |
| `SteadyRecognitionListener` | `onChanged(int, CameraEx)` |
| `StoreImageCompleteListener` | `onChanged(StoreImageInfo, CameraEx)` |
| `TrackingFocusListener` | `onChanged(TrackingFocusInfo, CameraEx)` |
| `ZoomChangeListener` | `onChanged(ZoomInfo, CameraEx)` |

### CameraEx Callback Interfaces

| Interface | Method Signature |
|---|---|
| `AdditionalDataSizeSetCallback` | `onSetComplete(CameraEx)` |
| `CustomWhiteBalanceCallback` | `onCapture(CustomWhiteBalanceInfo, CameraEx)` |
| `DirectShutterStoppedCallback` | `onShutterStopped(CameraEx)` |
| `EquipmentCallback` | `onEquipmentChange(int, int, CameraEx)` -- Constants: `DEVICE_TYPE_LENS=1`, `DEVICE_TYPE_INTERNAL_FLASH=2`, `DEVICE_TYPE_EXTERNAL_FLASH=3`, `DEVICE_AVAILABLE=1`, `DEVICE_STATUS_CHANGED=2` |
| `ErrorCallback` | `onError(int, CameraEx)` |
| `OpenCallback` | `onReopened(CameraEx)` |
| `ProperExposureLevelCallback` | `onControlLevelNotify(int, CameraEx)` |
| `RecordingMediaChangeCallback` | `onRecordingMediaChange(CameraEx)` |

---

### CameraSequence
**File:** `stubs/src/main/jasmin/com/sony/scalar/hardware/CameraSequence.j`
**Purpose:** Camera sequence operations for split exposures and multi-shot

**Inner Classes/Interfaces:**
- `ShutterSequenceCallback` - Method: `onShutterSequence(RawData, CameraSequence)`
- `SplitShutterSequenceCallback` - Method: `onSplitShutterSequence(RawData, SplitExposureProgressInfo, CameraSequence)` -- Constants: `CONTINUE`, `DARK`, `END`, `ERROR`, `INTERRUPT`, `SCENE`
- `DebugCallback` - Method: `onDebugError(int, int, CameraSequence)` -- Constants: `DEBUG_ERROR_PREVIEW_FATAL=1`, `DEBUG_ERROR_UNKNOWN=0`
- `PreviewPluginCallback` - Method: `onPreviewPluginNotify(PreviewPluginResult, CameraSequence)`

---

### DeviceBuffer
**File:** `stubs/src/main/jasmin/com/sony/scalar/hardware/DeviceBuffer.j`
**Purpose:** Device memory buffer management

**Methods:**
| Return Type | Method |
|---|---|
| `int` | `getSize()` |
| `void` | `release()` |

---

### DeviceMemory
**File:** `stubs/src/main/jasmin/com/sony/scalar/hardware/DeviceMemory.j`
**Purpose:** Device memory allocation

**Methods:**
| Return Type | Method |
|---|---|
| `DeviceBuffer` | `static allocate(int)` |
| `DeviceBuffer` | `static allocateFromMemoryMapConfig(MemoryMapConfig)` |

---

### DSP
**File:** `stubs/src/main/jasmin/com/sony/scalar/hardware/DSP.j`
**Purpose:** Digital Signal Processor control

**Inner Classes:**
- `DeviceType` (enum) - Values: `AUDIO`, `NOISE_REDUCTION`, `ZOOM`
- `PropertyKey` - Constants: `NOISE_REDUCTION_MODE String`, `ZOOM_NR_MODE String`

**Methods:**
| Return Type | Method |
|---|---|
| `void` | `<init>(DeviceType)` |
| `void` | `release()` |
| `void` | `setProperty(String, String)` |

---

### MemoryMapConfig
**File:** `stubs/src/main/jasmin/com/sony/scalar/hardware/MemoryMapConfig.j`
**Purpose:** Memory mapping configuration

**Methods:**
| Return Type | Method |
|---|---|
| `MemoryMapConfig` | `static create(String)` |

---

### YUVPlaneExtractor
**File:** `stubs/src/main/jasmin/com/sony/scalar/hardware/YUVPlaneExtractor.j`
**Purpose:** YUV plane extraction for image processing

**Inner Classes:**
- `Options` - Fields: `inputHeight int`, `inputWidth int`, `outputHeight int`, `outputWidth int`

**Methods:**
| Return Type | Method |
|---|---|
| `void` | `<init>()` |
| `void` | `extract(DeviceBuffer, DeviceBuffer, Options)` |

---

## 7. com.sony.scalar.hardware.avio Package

### DisplayManager (avio)
**File:** `stubs/src/main/jasmin/com/sony/scalar/hardware/avio/DisplayManager.j`
**Purpose:** Low-level display management with HDMI, QFHD, timecode display support

**Inner Classes:**
- `DeviceInfo` - Fields: `aspect int`, `res_h int`, `res_w int`
- `DeviceStatus` - Fields: `active boolean`, `format int`, `id String`, `ntpal int`, `qfhdFormatSupported boolean`, `viewPattern int`
- `VideoRect` - Fields: `pxBottom int`, `pxLeft int`, `pxRight int`, `pxTop int`
- `DisplayEventListener` (interface) - Method: `onDeviceStatusChanged(int)`

**Key Constants (selected):**
| Constant | Type | Value |
|---|---|---|
| `DEVICE_ID_FINDER` | `String` | `"finder"` |
| `DEVICE_ID_HDMI` | `String` | `"hdmi"` |
| `DEVICE_ID_NONE` | `String` | `"none"` |
| `DEVICE_ID_PANEL` | `String` | `"panel"` |
| `ASPECT_RATIO_3_2` | `int` | `0` |
| `ASPECT_RATIO_4_3` | `int` | `1` |
| `ASPECT_RATIO_16_9` | `int` | `4` |
| `EVENT_SWITCH_DEVICE` | `int` | `0` |

**Methods:**
| Return Type | Method |
|---|---|
| `void` | `<init>()` |
| `void` | `finish()` |
| `String` | `getActiveDevice()` |
| `DeviceInfo` | `getDeviceInfo(String)` |
| `DeviceStatus` | `getDeviceStatus(String)` |
| `void` | `releaseDisplayStatusListener()` |
| `void` | `setDisplayStatusListener(DisplayEventListener)` |
| `void` | `switchDisplayOutputTo(String)` |
| `void` | `switchViewPattern(String, int)` |
| `void` | `setQfhdOutput(boolean)` |
| `void` | `setTcDisplay(boolean)` |
| `void` | `setUbDisplay(boolean)` |

---

### Cec
**File:** `stubs/src/main/jasmin/com/sony/scalar/hardware/avio/Cec.j`
**Purpose:** HDMI CEC (Consumer Electronics Control)

**Constants:** `ONETOUCH_PLAY int = 1`

**Methods:**
| Return Type | Method |
|---|---|
| `void` | `issueMessage(int)` |

---

## 8. com.sony.scalar.hardware.indicator Package

### Light
**File:** `stubs/src/main/jasmin/com/sony/scalar/hardware/indicator/Light.j`
**Purpose:** LED indicator control

**Constants (LID_* = LED IDs):**
`ANSWER_BACK`, `BATT_REMAIN`, `CHARGE`, `FINDER`, `MEDIA`, `MEDIA_2`, `MOVIE`, `NETWORK`, `POWER`, `RECORD`, `WIFI`

**Constants (PTN_* = Light patterns):**
`FAST`, `MIDDLE`, `OFF`, `ON`, `SLOW`, `PTN_FAST_MID_DSLR`, `PTN_ON_MID_DSLR`

**Methods:**
| Return Type | Method |
|---|---|
| `void` | `static initialize()` |
| `void` | `static release()` |
| `void` | `static setState(String, String, boolean)` |
| `void` | `static setState(String, String, boolean, String)` |
| `void` | `static setState(List)` |

---

### SubLCD
**File:** `stubs/src/main/jasmin/com/sony/scalar/hardware/indicator/SubLCD.j`
**Purpose:** Sub-LCD display control (top panel display on pro cameras)

**Inner Classes:**
- `Sub` - Sub-display segment (with extensive icon/value constants)
- `TopSurface` - Top surface display
- `IconParameter` - Icon display parameters (extends `Parameter`)
- `TextParameter` - Text display parameters (extends `Parameter`)

**Methods:**
| Return Type | Method |
|---|---|
| `int` | `static getStringLength(String)` |
| `void` | `static native initialize()` |
| `void` | `static native release()` |
| `void` | `static setAllSegmentState(boolean)` |
| `void` | `static setState(String, String, boolean)` |
| `void` | `static setState(String, String, boolean, String)` |
| `void` | `static setState(List)` |
| `void` | `static setString(String, boolean, String, String)` |

---

### Parameter
**File:** `stubs/src/main/jasmin/com/sony/scalar/hardware/indicator/Parameter.j`
**Purpose:** Base indicator parameter class

---

## 9. com.sony.scalar.media Package

### AudioManager
**File:** `stubs/src/main/jasmin/com/sony/scalar/media/AudioManager.j`
**Purpose:** Audio system management

**Inner Classes:**
- `Parameters` - Audio parameters
- `OnMicrophoneChangedListener` (interface)
- `SettingChangedListener` (interface)

**Methods:**
| Return Type | Method |
|---|---|
| `void` | `<init>()` |
| `void` | `disableSettingChangedTypes(int[])` |
| `void` | `enableSettingChangedTypes(int[])` |
| `Parameters` | `getParameters()` |
| `Parameters` | `getSupportedParameters()` |
| `void` | `release()` |
| `void` | `setMicrophoneChangedListener(OnMicrophoneChangedListener)` |
| `void` | `setParameters(Parameters)` |
| `void` | `setSettingChangedListener(SettingChangedListener)` |

---

### AudioRecord
**File:** `stubs/src/main/jasmin/com/sony/scalar/media/AudioRecord.j`
**Purpose:** Audio recording with hardware buffer support

**Inner Classes:**
- `AaugInfo` - Audio augmentation info
- `AudioBuffer` - Audio buffer
- `AudioData` - Audio data container
- `EncoderParameters` - Encoder configuration (constants: `AUDIO_ENCODER_ADDITIONAL=1`, `AUDIO_ENCODER_PREINSTALL=0`, etc.)
- `OnErrorNotifyListener` (interface)
- `OnRecordPositionUpdateListener` (interface)

**Constants:** `AUDIO_BUFFER_ATTACHED=11`, `AUDIO_BUFFER_DETACHED=10`, `RECORDING_STARTING=31`, `RECORDING_STOPPED=30`, `SAMPLING_STARTING=21`, `SAMPLING_STOPPED=20`, `STATE_INITIALIZED=1`, `STATE_UNINITIALIZED=0`

**Methods:**
| Return Type | Method |
|---|---|
| `void` | `<init>()` |
| `void` | `addNotificationMarkerPosition(long)` |
| `AudioBuffer` | `createAudioBuffer(DeviceBuffer, int, int, EncoderParameters)` |
| `int` | `getAudioBufferSize(EncoderParameters)` |
| `int` | `getErrorState()` |
| `int` | `getReadEsBufferSize(long, long)` |
| `long` | `getReadMarkerPosition()` |
| `PlainCalendar` | `getRecordingDateTime()` |
| `long` | `getWriteMarkerPosition()` |
| `void` | `loadZoomNrTable(DeviceBuffer)` |
| `AudioData` | `readEsBuffer(long, long, DeviceBuffer, int, int)` |
| `AudioData` | `readEsBuffer(long, long, ByteBuffer, int, int)` |
| `AudioData` | `readEsBuffer(long, long, byte[], int, int)` |
| `void` | `release()` |
| `void` | `setAudioBuffer(AudioBuffer)` |
| `void` | `setDspPlugin(int, DSP)` |
| `void` | `setErrorNotifyListener(OnErrorNotifyListener)` |
| `void` | `setErrorNotifyListener(OnErrorNotifyListener, Handler)` |
| `void` | `setPositionNotificationPeriod(long)` |
| `void` | `setReadMarkerPosition(long)` |
| `void` | `setRecordPositionUpdateListener(OnRecordPositionUpdateListener)` |
| `void` | `setRecordPositionUpdateListener(OnRecordPositionUpdateListener, Handler)` |
| `void` | `startEE()` / `startRecording()` / `stopEE()` / `stopRecording()` |

---

### AudioTrack
**File:** `stubs/src/main/jasmin/com/sony/scalar/media/AudioTrack.j`
**Purpose:** Audio playback with hardware buffer support

**Inner Classes:**
- `AudioBuffer`
- `OnErrorNotifyListener` (interface)
- `OnPlaybackPositionUpdateListener` (interface)

**Constants:** `CHANNEL_STEREO=1`, `FORMAT_PCM_16BIT=1`, `MODE_STATIC=1`, `MODE_STREAM=2`, `PLAY_STATE_PAUSED=2`, `PLAY_STATE_PLAYING=3`, `PLAY_STATE_STOPPED=1`

**Methods:**
| Return Type | Method |
|---|---|
| `void` | `<init>(int, int, int, int)` |
| `AudioBuffer` | `createAudioBuffer(DeviceBuffer, int, int)` |
| `void` | `flush()` / `pause()` / `play()` / `stop()` |
| `int` | `getAudioBufferSize(long)` / `getAudioFormat()` / `getBufferState()` / `getChannel()` |
| `int` | `getMaxAudioBufferSize()` / `getPlayState()` / `getSamplePerFrame()` / `getSampleRate()` / `getState()` |
| `void` | `release()` / `reloadStaticData()` |
| `void` | `setAudioBuffer(AudioBuffer)` |
| `void` | `write(DeviceBuffer, int, int, boolean)` / `write(ByteBuffer, int, int, boolean)` / `write(byte[], int, int, boolean)` |

---

### MediaRecorder
**File:** `stubs/src/main/jasmin/com/sony/scalar/media/MediaRecorder.j`
**Purpose:** Video/media recording

**Inner Classes:**
- `Parameters` - Recording parameters (with `TcFrameRateRangeInfo` and `TimeCodeInfo`)
- `CamcorderProfile` - Camera profile
- `StreamBuffer` (with `StreamMemoryArea`)
- `AudioSource` / `VideoSource`
- `InhibitedOperationException`
- `OnErrorListener` / `OnPreparedListener` / `OnRecRemainListener` / `OnRecTimeListener` / `OnRecordListener` / `OnStreamWriteListener`

**Constants:** `MEDIA_RECORDER_ERROR_SERVER_DIED=100`, `MEDIA_RECORDER_ERROR_UNKNOWN=1`, `PRIORITY_MAIN=0`, `PRIORITY_SUB=1`

**Methods:**
| Return Type | Method |
|---|---|
| `void` | `<init>()` |
| `Parameters` | `getParameters()` / `getSupportedParameters()` |
| `void` | `prepare()` / `prepareAsync()` / `release()` / `reset()` |
| `void` | `setAudioSource(int)` / `setVideoSource(int)` |
| `void` | `setCamera(CameraEx)` |
| `void` | `setLoopRecMode(boolean)` |
| `void` | `setOutputMedia(String)` |
| `void` | `setParameters(Parameters)` |
| `void` | `setPriority(int)` |
| `void` | `setStreamBuffer(StreamBuffer)` |
| `void` | `start()` / `stop()` |
| `void` | `setOnErrorListener(OnErrorListener)` / setOn*Listener(...) |

---

### MediaPlayer
**File:** `stubs/src/main/jasmin/com/sony/scalar/media/MediaPlayer.j`
**Purpose:** Media playback with speed control and step playback

**Inner Classes:**
- `PlayerConfig` - Player configuration
- `OnCompletionListener` / `OnErrorListener` / `OnLapTimeEventListener`

**Constants:** `DISPLAY_MODE_FULL_SCREEN=0`, `DISPLAY_MODE_HISTOGRAM=1`, `PLAYBACK_SPEED_FAST_FORWARD_1..4`, `PLAYBACK_SPEED_FAST_REWIND_1..4`, `PLAYBACK_SPEED_SLOW_FORWARD_1`, `PLAYBACK_SPEED_SLOW_REWIND_1`

**Methods:**
| Return Type | Method |
|---|---|
| `void` | `<init>()` |
| `int` | `native getCurrentPosition()` / `native getDuration()` |
| `int` | `getDisplayMode()` / `getPlaybackSpeed()` |
| `int` | `getVideoHeight()` / `getVideoWidth()` |
| `boolean` | `native isPlaying()` |
| `void` | `native prepare()` / `release()` / `reset()` |
| `void` | `native seekTo(int)` / `native seekAsyncTo(int)` |
| `void` | `setDataSource(String)` / `setDisplay(SurfaceHolder)` / `setSurface(Surface)` |
| `void` | `setDisplayMode(int)` / `setPlaybackSpeed(int)` |
| `void` | `start()` / `pause()` / `stop()` |
| `void` | `native stepForward()` / `native stepRewind()` |
| `boolean` | `setParameter(int, int)` / `setParameter(int, String)` / `native setParameter(int, Parcel)` |

---

### AvindexContentInfo (abstract)
**File:** `stubs/src/main/jasmin/com/sony/scalar/media/AvindexContentInfo.j`
**Purpose:** Content metadata with ~140+ TAG_* constants for EXIF, GPS, AVI, panorama, DCF, DPOF data

**Key TAG_ Constants (selected):**
`TAG_APERTURE="FNumber"`, `TAG_EXPOSURE_TIME="ExposureTime"`, `TAG_ISO="ISOSpeedRatings"`, `TAG_FOCAL_LENGTH="FocalLength"`, `TAG_FLASH="Flash"`, `TAG_WHITE_BALANCE="WhiteBalance"`, `TAG_DATETIME="DateTime"`, `TAG_IMAGE_WIDTH="ImageWidth"`, `TAG_IMAGE_LENGTH="ImageLength"`, `TAG_ORIENTATION="Orientation"`, `TAG_GPS_LATITUDE="GPSLatitude"`, `TAG_GPS_LONGITUDE="GPSLongitude"`, `TAG_CONTENT_TYPE="ContentType"`, `TAG_THUMBNAIL="thumbnail"`

**Methods:**
| Return Type | Method |
|---|---|
| `String` | `getAttribute(String)` |
| `double` | `getAttributeDouble(String, double)` |
| `int` | `getAttributeInt(String, int)` |
| `long` | `getAttributeLong(String, long)` |
| `int` | `abstract getAttributeType(String)` |
| `AvindexContentFaceInfo[]` | `abstract getFaceInfo()` |
| `byte[]` | `abstract getThumbnail()` |
| `boolean` | `abstract hasThumbnail()` |
| `boolean` | `isRecycled()` |
| `void` | `recycle()` |

---

### AvindexContentFaceInfo (abstract)
**File:** `stubs/src/main/jasmin/com/sony/scalar/media/AvindexContentFaceInfo.j`
**Purpose:** Face information from content index

**Methods:**
| Return Type | Method |
|---|---|
| `int` | `abstract getFacePriority(int)` / `abstract getFaceScore(int)` |
| `int` | `abstract getPositionX()` / `abstract getPositionY()` |
| `int` | `abstract getSizeHeight()` / `abstract getSizeWidth()` |
| `boolean` | `abstract hasFacePriority()` / `abstract hasFaceScore()` |

---

### AvindexFactory (abstract)
**File:** `stubs/src/main/jasmin/com/sony/scalar/media/AvindexFactory.j`

**Methods:**
| Return Type | Method |
|---|---|
| `AvindexFactory` | `static createAvindexFactory()` |
| `AvindexContentInfo` | `abstract createAvindexContentInfo(ByteBuffer)` |
| `AvindexContentInfo` | `abstract createAvindexContentInfo(byte[])` |
| `MediaInfo` | `abstract createMediaInfo(byte[])` |

---

### HighlightMovieMaker
**File:** `stubs/src/main/jasmin/com/sony/scalar/media/HighlightMovieMaker.j`
**Purpose:** Automatic highlight movie creation

**Inner Classes:** `Listener` (interface)

**Constants:** `MOVIE_BGM_MUSIC1..8`, `MOVIE_LENGTH_15SEC`, `MOVIE_LENGTH_30SEC`, `MOVIE_LENGTH_1MIN`, `MOVIE_LENGTH_2MIN`, `MOVIE_LENGTH_SAME_AS_MUSIC`, `RESULT_OK=0`, `RESULT_ERR_CANCELED=1`, etc.

**Methods:**
| Return Type | Method |
|---|---|
| `void` | `<init>()` |
| `void` | `cancelExecuting()` / `cancelPreparing()` / `clear()` / `execute()` |
| `List` | `getSupportedMovieBgms()` / `getSupportedMovieLengths()` |
| `boolean` | `isSupportedAudioMixRate()` / `isSupportedOpeningAndEnding()` |
| `void` | `prepare(String, List)` / `release()` |
| `void` | `setAudioMixRate(int)` / `setEndingMovie(String)` / `setOpeningMovie(String)` |
| `void` | `setListener(Listener)` / `setMovieBgm(String)` / `setMovieLength(String)` |

---

### MediaInfo (abstract)
**File:** `stubs/src/main/jasmin/com/sony/scalar/media/MediaInfo.j`
**Purpose:** Media card information

**Constants:** `MEDIA_TYPE_MS=2`, `MEDIA_TYPE_SD=1`, `MEDIA_TYPE_UNKNOWN=0`

**Methods:**
| Return Type | Method |
|---|---|
| `int` | `abstract getMediaType()` |
| `boolean` | `abstract isEyeFi()` / `abstract isFakeMs()` |

---

## 10. com.sony.scalar.meta Package

### TakenPhotoInfo
**File:** `stubs/src/main/jasmin/com/sony/scalar/meta/TakenPhotoInfo.j`
**Purpose:** Comprehensive EXIF metadata -- ~90 fields covering all capture parameters

**Key Fields (selected from ~90):**
| Type | Field |
|---|---|
| `short` | `FNumber` |
| `int` | `ISOSpeedRatings` |
| `int` | `ExposureTime_num` / `ExposureTime_den` |
| `short` | `Flash` |
| `int` | `FocalLength_num` / `FocalLength_den` |
| `short` | `WhiteBalance` |
| `short` | `LightSource` |
| `short` | `ExposureProgram` |
| `short` | `MeteringMode` |
| `int` | `ImageWidth` / `ImageLength` |
| `short` | `Orientation` |
| `short` | `SceneCaptureType` |
| `String` | `DateTimeOriginal` |
| `short` | `MakerNoteCreativeStyle` |
| `short` | `MakerNotePictureEffect` |
| `short` | `MakerNoteMultiFrameNR` |
| `short` | `MakerNoteAutoHDR` |
| `short` | `FocusMode` |
| `float` | `ExposureBiasValue` |
| `int` | `ImageStabilizer` |
| `float` | `DigitalZoomRatio` |
| `FaceInfo[]` | `faceInfo` |
| `int` | `faceNumber` |

---

### Histogram
Fields: `B short[]`, `G short[]`, `R short[]`, `Y short[]`

### FaceInfo
Fields: `rect Rect`, `leftEye Point`, `rightEye Point`, `mouth Point`, `faceAngle FaceAngle`, `smile SmileStatus`, `gender GenderStatus`, `generation GenerationStatus`, `eyeBlink EyeBlinkStatus`

### SmileStatus
Fields: `smile boolean`, `score int`

### GenderStatus
Fields: `gender int` (MALE/FEMALE), `score int`

### GenerationStatus
Fields: `generation int`, `score int`

### EyeBlinkStatus
Fields: `leftEyeBlink boolean`, `rightEyeBlink boolean`

### FaceAngle
Fields: `yaw int`

---

## 11. com.sony.scalar.provider Package

### AvindexStore
**File:** `stubs/src/main/jasmin/com/sony/scalar/provider/AvindexStore.j`
**Purpose:** Content provider for the AV index database (image/video catalog)

**Inner Classes:**
- `Images` (with `Folder`, `ImageColumns`, `Info`, `LastContent`, `LocalDate`, `Media`, `Thumbnails`, `Update`, `UtcDate`)
- `Video` (with `Folder`, `VideoColumns`, `LocalDate`, `Media`, `Update`, `UtcDate`)
- `Files` (with `FileColumns`, `Folder`, `Info`, `LocalDate`, `Media`, `Update`, `UtcDate`)
- `MediaColumns_Class` (with `Folder`, `Info`, `LocalDate`, `Media`, `MediaColumns`, `Update`, `UtcDate`)

**Constants:**
`AUTHORITY="com.sony.scalar.providers.avindex"`, `MEDIA_TYPE_EXTERNAL=1`, `MEDIA_TYPE_INTERNAL=2`, `MEDIA_TYPE_VIRTUAL=4`, `CONTENT_TYPE_LOAD_STILL=1`, `CONTENT_TYPE_LOAD_AVCHD=4`, `CONTENT_TYPE_LOAD_MP4=5`, `CONTENT_TYPE_LOAD_XAVC=7`

**Methods:**
| Return Type | Method |
|---|---|
| `boolean` | `static calculateAvailableSize(String)` |
| `boolean` | `static cancelWaitLoadMediaComplete(String)` |
| `long` | `static getAvailableSize(String)` |
| `String[]` | `static getExternalMediaIds()` / `getInternalMediaIds()` / `getVirtualMediaIds()` |
| `MediaInfo` | `static getMediaInfo(String)` |
| `boolean` | `static isExistMedia(String, int)` |
| `boolean` | `static loadMedia(String, int)` |
| `boolean` | `static waitLoadMediaComplete(String)` |

Each sub-class (Images, Video, Files) provides:
- `getContentUri(String) -> Uri` (for Media, LocalDate, UtcDate, Folder, Info, etc.)
- `getCursorIndex(Cursor, String) -> int`
- Many column name constants in their respective *Columns classes

---

### AvindexUpdateObserver (interface)
**Constants:** `COMPLETE_RESULT_OK=0`, `COMPLETE_RESULT_CANCEL=1`, `COMPLETE_RESULT_INTERNAL_ERROR=2`
**Method:** `onComplete(int)`

### AvindexDeleteObserver (interface)
**Constants:** Same as above
**Methods:** `onComplete(int)`, `onProgress(int, int)`

---

## 12. com.sony.scalar.sysutil Package

### PlainCalendar
**File:** `stubs/src/main/jasmin/com/sony/scalar/sysutil/PlainCalendar.j`
**Fields:** `day int`, `hour int`, `minute int`, `month int`, `second int`, `year int`
**Constructor:** `<init>()`

### PlainTimeZone
**File:** `stubs/src/main/jasmin/com/sony/scalar/sysutil/PlainTimeZone.j`
**Fields:** `gmtDiff int`, `summerTimeDiff int`
**Constructor:** `<init>()`

### TimeUtil
**File:** `stubs/src/main/jasmin/com/sony/scalar/sysutil/TimeUtil.j`
**Constants:** `OK=0`, `PRM_ERR=1`, `UNDER_SETUP=2`

**Methods:**
| Return Type | Method |
|---|---|
| `void` | `<init>()` |
| `PlainCalendar` | `static getCurrentCalendar()` |
| `PlainTimeZone` | `static getCurrentTimeZone()` |
| `int` | `setCurrentTime(PlainCalendar, PlainTimeZone)` |

---

### ScalarProperties
**File:** `stubs/src/main/jasmin/com/sony/scalar/sysutil/ScalarProperties.j`
**Purpose:** System properties access -- the main configuration store

**Inner Classes:**
- `PictureSize` - Fields: `height int`, `width int`

**Key PROP_* Constants (selected from ~80):**
`PROP_MODEL_NAME="model.name"`, `PROP_MODEL_CODE="model.code"`, `PROP_MODEL_SERIAL_CODE="model.serial.code"`, `PROP_MODEL_CATEGORY="model.category"`, `PROP_MODEL_GROUP="model.group"`, `PROP_VERSION_PLATFORM="version.platform"`, `PROP_SIGNAL_FREQUENCY="signal.frequency"`, `PROP_DEVICE_WIFI_SUPPORTED="device.wifi.supported"`, `PROP_DEVICE_GPS_SUPPORTED="device.gps.supported"`, `PROP_DEVICE_NFC_SUPPORTED="device.nfc.supported"`, `PROP_DEVICE_BLUETOOTH_SUPPORTED="device.bluetooth.supported"`, `PROP_UI_MODEL_NAME="ui.model.mame"`, `PROP_DEVICE_MECHANICAL_SHUTTER="device.mechanical.shutter"`

**Key INTVAL_* Constants (selected from ~150):**
`INTVAL_CATEGORY_ILDC_A=0`, `INTVAL_CATEGORY_ILDC_E=1`, `INTVAL_CATEGORY_DSC=2`, `INTVAL_CATEGORY_CAM=3`, `INTVAL_SIGNAL_FREQUENCY_PAL=0`, `INTVAL_SIGNAL_FREQUENCY_NTSC=1`, `INTVAL_SUPPORTED=1`, `INTVAL_NOT_SUPPORTED=0`

**Methods:**
| Return Type | Method |
|---|---|
| `byte[]` | `static getByteArray(String)` |
| `String` | `static getFirmwareVersion()` |
| `int` | `static getInt(String)` / `static getInt(String, int)` |
| `int[]` | `static getIntArray(String)` |
| `String` | `static getString(String)` / `static getString(String, String)` |
| `List` | `static getSupportedPictureSizes()` |
| `boolean` | `static setByteArray(String, byte[])` |
| `boolean` | `static setInt(String, int)` |
| `void` | `static setTimeTag(String)` |

---

### ScalarInput
**File:** `stubs/src/main/jasmin/com/sony/scalar/sysutil/ScalarInput.j`
**Purpose:** Physical key/dial/ring input event codes

**Inner Classes:**
- `KeyLogicCode`

**Key Constants (selected from ~160 ISV_* constants):**
`ISV_KEY_S1_1=516` (half-press shutter), `ISV_KEY_S1_2=517`, `ISV_KEY_S2=518` (full shutter), `ISV_KEY_STASTOP=515` (start/stop recording), `ISV_KEY_MENU=514`, `ISV_KEY_PLAY=207`, `ISV_KEY_ENTER=232`, `ISV_KEY_UP=103`, `ISV_KEY_DOWN=108`, `ISV_KEY_LEFT=105`, `ISV_KEY_RIGHT=106`, `ISV_KEY_FN=520`, `ISV_KEY_DELETE=595`, `ISV_KEY_CUSTOM1..3=622..659`, `ISV_KEY_WIFI=657`, `ISV_DIAL_1_CLOCKWISE..STATUS=525..524`, `ISV_RING_CLOCKWISE=648`, etc.

**Constants:** `STATUS_ON=1`, `STATUS_OFF=0`, `STATUS_INVALID=2147483647`, `VALID=1`, `INVALID=0`

**Methods:**
| Return Type | Method |
|---|---|
| `int` | `static getKeyLogicCode(int, int, int)` |
| `KeyStatus` | `static getKeyStatus(int)` |
| `int` | `static getMaxModeForKeyLogicCode()` |

---

### KeyStatus
**File:** `stubs/src/main/jasmin/com/sony/scalar/sysutil/KeyStatus.j`
**Constants:** `INVALID=0`, `VALID=1`, `STATUS_ON=1`, `STATUS_OFF=0`, `STATUS_INVALID=2147483647`
**Fields:** `status int`, `valid int`

---

## 13. com.sony.scalar.sysutil.didep Package

**"didep"** = Device-Independent Device Extension Platform

### Settings
**File:** `stubs/src/main/jasmin/com/sony/scalar/sysutil/didep/Settings.j`
**Purpose:** Camera settings management

**Inner Classes:**
- `AutoRotate`, `CustomKey`, `CustomLauncherFunction`, `DateFormat`, `DeleteConfirmation`, `Dial`, `DisplayMode`, `DisplayType`, `GridLine`, `KeyFunction`, `KeyLockInfo`, `LayoutWideImage`, `MovieButton`, `OSDOutputToHDMI`, `TouchPanelEnabled`

**Constants:** `MEDIA_EXTERNAL_1=1`, `MEDIA_INTERNAL_1=0`

**Methods:**
| Return Type | Method |
|---|---|
| `int` | `static native getAutoPowerOffTime()` |
| `int` | `static native getAutoRotate()` |
| `int[]` | `static native getCustomLauncherAssign()` |
| `int` | `static native getDateFormat()` |
| `int` | `static native getDeleteConfirmationMode()` |
| `int` | `static native getDialExposureCompensation()` |
| `int` | `static native getDialSetting()` |
| `int` | `static native getDispMode(int)` |
| `int` | `static native getGridLine()` |
| `int` | `static native getHDMIOSDOutput()` |
| `int` | `static native getIndexThumbNum()` |
| `int[]` | `static native getKeyFunction(int[])` |
| `KeyLockInfo` | `static native getKeyLock()` |
| `int` | `static native getMovieButtonMode()` |
| `List` | `static getSupportedAutoPowerOffTimes()` |
| `String` | `static getTargetMediaId()` |
| `int` | `static native getTouchPanelEnabled()` |
| `int` | `static getUsingMedia()` |
| `int` | `static native getWideImageLayout()` |
| `boolean` | `static isLiveAndRec()` |
| `void` | `static native setAutoPowerOffTime(int)` |
| `boolean` | `static native setKeyLock(int)` |
| `boolean` | `static native setTouchPanelEnabled(int)` |

---

### ScalarSystemManager
**File:** `stubs/src/main/jasmin/com/sony/scalar/sysutil/didep/ScalarSystemManager.j`
**Purpose:** System power management, reset, and lifecycle

**Inner Classes:**
- `SystemReadyEventListener` (interface) - `onSystemReady()`

**Constants (OFF_FACTOR_*):**
`BATT_THERMAL=0`, `BATT_INVALID=1`, `AC_INVALID=2`, `IM_THERMAL=3`, `BOX_THERMAL=4`, `SALVAGE=5`, `MECH_ERR=6`, `FACTORY=7`, `BATT_END=8`, `LENS_ERR=10`, `KEY=11`, `UPDATE=13`, `APO_NON_OPERATION=14`, `BATT_DEAD=19`, `BATT_OVERCURRENT=20`, `CHARGE_MOTOR_THERMAL=21`, `THERMAL_TIME_OUT=32`

**Methods:**
| Return Type | Method |
|---|---|
| `boolean` | `static changeSignalFrequency()` |
| `boolean` | `static native isSystemReady()` |
| `void` | `static keepSettingValue()` |
| `boolean` | `static requestPowerOff(int, int, int)` |
| `boolean` | `static resetAll()` |
| `boolean` | `static resetSettingValue(Context, List)` |
| `void` | `static revertSettingValue()` |
| `void` | `registerSystemReadyObserver(SystemReadyEventListener)` |
| `void` | `unregisterSystemReadyObserver()` |

---

### Power
**File:** `stubs/src/main/jasmin/com/sony/scalar/sysutil/didep/Power.j`
**Purpose:** Battery power management

**Inner Classes:** `BatteryRemainTimeListener` (interface)

**Constants:** `INVALID_BATTERY_REMAIN=-1`

**Methods:**
| Return Type | Method |
|---|---|
| `Power` | `static getInstance()` |
| `void` | `release()` |
| `void` | `setBatteryRemainTimeListener(BatteryRemainTimeListener)` |

---

### Status
**File:** `stubs/src/main/jasmin/com/sony/scalar/sysutil/didep/Status.j`
**Purpose:** System status and inhibit factor monitoring

**Inner Classes:**
- `InhFactorListener` (interface)
- `InhFactorListenerMulti` (interface)

**Methods:**
| Return Type | Method |
|---|---|
| `Status` | `static create()` |
| `byte[]` | `getFactors()` |
| `byte[]` | `getInhFactorFilter()` |
| `int[]` | `getMediaOffsets(String[])` |
| `void` | `release()` |
| `void` | `setInhFactorFilter(byte[])` |
| `void` | `setInhFactorListener(InhFactorListener)` |
| `void` | `setInhFactorListenerMulti(int, InhFactorListenerMulti)` |
| `void` | `unSetInhFactorListenerMulti(InhFactorListenerMulti)` |

---

### Temperature
**File:** `stubs/src/main/jasmin/com/sony/scalar/sysutil/didep/Temperature.j`
**Purpose:** Temperature monitoring with countdown warnings

**Inner Classes:**
- `Status` - Temperature status data
- `CountDownInfo` - Countdown information
- `StatusCallback` (interface)
- `CountDownInfoListener` (interface)

**Methods:**
| Return Type | Method |
|---|---|
| `Temperature.Status` | `getStatus()` |
| `void` | `release()` |
| `void` | `setCountDownInfoListener(CountDownInfoListener)` |
| `void` | `setStatusCallback(StatusCallback)` |

---

### Nfc
**File:** `stubs/src/main/jasmin/com/sony/scalar/sysutil/didep/Nfc.j`
**Purpose:** NFC control

**Constants:** `CONNECT_COMPLETED=1`, `CONNECT_NO_PROCESS=2`, `CONNECT_WAIT=0`

**Methods:**
| Return Type | Method |
|---|---|
| `boolean` | `isEnabled()` |
| `void` | `release()` / `start()` / `stop()` |
| `void` | `setNfcBootConnectStateListener(NfcBootConnectStateListener)` |

---

### Gps
**File:** `stubs/src/main/jasmin/com/sony/scalar/sysutil/didep/Gps.j`
**Purpose:** GPS control and location data

**Constants:** `GPS_POWER_OFF=0`, `GPS_POWER_ON=1`, `GPS_STATE_FIX=4`, `GPS_STATE_HOLD=6`, `GPS_STATE_INH=2`, `GPS_STATE_NG=0`, `GPS_STATE_NOFIX=5`, `GPS_STATE_OFF=1`, `GPS_STATE_OK=3`

**Inner Classes:** `GpsInfoListener` (interface), `gpsInfo`

**Methods:**
| Return Type | Method |
|---|---|
| `int` | `getGpsPowerState()` |
| `void` | `release()` / `start()` / `stop()` |
| `void` | `releaseGpsInfoListener()` |
| `void` | `setGpsInfo(ByteBuffer)` |
| `void` | `setGpsInfoListener(GpsInfoListener)` |

---

### Bluetooth
**File:** `stubs/src/main/jasmin/com/sony/scalar/sysutil/didep/Bluetooth.j`
**Purpose:** Bluetooth connectivity

**Inner Classes:** `BluetoothInfo`, `BluetoothInfoListener` (interface)

**Methods:**
| Return Type | Method |
|---|---|
| `BluetoothInfo` | `getBluetoothInfo()` |
| `void` | `release()` / `releaseBluetoothInfoListener()` |
| `void` | `setBluetoothInfoListener(BluetoothInfoListener)` |

---

### Media (didep)
**File:** `stubs/src/main/jasmin/com/sony/scalar/sysutil/didep/Media.j`
**Purpose:** Media card formatting and recovery

**Inner Classes:** `FormatListener` (interface), `RecoveryListener` (interface)

**Constants:** `RECOVERY=0`, `SALVAGE_RESTORE=1`, `REPAIR=2`, `MISMATCH_REPAIR=3`

**Methods:**
| Return Type | Method |
|---|---|
| `void` | `cancelRecovery()` / `execRecovery(int)` |
| `void` | `executeFormat()` / `exitFormat()` |
| `boolean` | `isMismatchRepair()` / `isSupportedFormat()` |
| `void` | `prepareFormat(String, FormatListener)` / `prepareRecovery(int)` |
| `void` | `rejectRecovery(int)` / `release()` |
| `void` | `setRecoveryListener(RecoveryListener)` |

---

### Gpelibrary
**File:** `stubs/src/main/jasmin/com/sony/scalar/sysutil/didep/Gpelibrary.j`
**Purpose:** Graphics Processing Engine library for display framebuffer control

**Inner Classes (enums):**
- `GS_FRAMEBUFFER_TYPE` - e.g. `ABGR8888`, `RGBA4444`
- `GS_SWITCH_TYPE`

**Constants:** `ACTIVITY_STATE_CREATE=0` through `ACTIVITY_STATE_DESTROY=6`

**Methods:**
| Return Type | Method |
|---|---|
| `void` | `static changeFrameBufferPixel(GS_FRAMEBUFFER_TYPE)` |
| `void` | `static onActivityStateChanged(int, int)` |
| `void` | `static onWindowFocusChanged(boolean, int)` |
| `void` | `static updateEnable(GS_SWITCH_TYPE)` |

---

### Caution
**File:** `stubs/src/main/jasmin/com/sony/scalar/sysutil/didep/Caution.j`
**Purpose:** Caution/warning system (very large -- ~12,000+ field constants for caution IDs)

**Inner Classes:** `CautionCallback` (interface)

**Methods:**
| Return Type | Method |
|---|---|
| `void` | `static EnableCaution(int, boolean)` |
| `void` | `static SetFactor(int, boolean)` |
| `void` | `static SetMode(int, String[])` |
| `void` | `static SetTrigger(int, int, boolean)` |
| `void` | `setCallback(CautionCallback)` |

---

### Kikilog
**File:** `stubs/src/main/jasmin/com/sony/scalar/sysutil/didep/Kikilog.j`
**Purpose:** User logging system (diagnostics)

**Inner Classes:** `Options`

**Methods:**
| Return Type | Method |
|---|---|
| `void` | `static setUserLog(int, Options)` |
| `void` | `static setUserLog(int, Options, byte[])` |
| `void` | `static setUserLogParam(int)` |

---

## 14. com.sony.scalar.widget Package

### OptimizedImageView
**File:** `stubs/src/main/jasmin/com/sony/scalar/widget/OptimizedImageView.j`
**Purpose:** Hardware-accelerated image view for camera display

**Inner Classes (~14):**
- `DisplayType` - Display mode enum
- `TranslationType` - Translation mode
- `BoundType` - Bound behavior
- `PositionType` - Position mode
- `RotationAngle` - Rotation angles
- `LayoutInfo` - Layout information
- `Translatability` - Translation capability
- `RedrawMode` - Redraw behavior
- `PixelFormatHW` - Hardware pixel format
- `ErrCd` - Error codes
- Various listener interfaces

---

## 15. com.sony.wifi Package

### wps/WpsError
**File:** `stubs/src/main/jasmin/com/sony/wifi/wps/WpsError.j`
**Inner Class:** `WpsConfigError` - WPS configuration error codes

### direct/*
WiFi Direct operations package

### p2p/*
P2P WiFi operations package

---

## 16. com.sony.scalar.sysnetutil Package

### ScalarWifiInfo
**File:** `stubs/src/main/jasmin/com/sony/scalar/sysnetutil/ScalarWifiInfo.j`
**Purpose:** WiFi product identification

**Methods:**
| Return Type | Method |
|---|---|
| `String` | `getProductCode()` |

---

## 17. com.sony.scalar.lib.ssdpdevice.v1_1 Package

### SsdpDevice
**File:** `stubs/src/main/jasmin/com/sony/scalar/lib/ssdpdevice/v1_1/SsdpDevice.j`
**Purpose:** SSDP (Simple Service Discovery Protocol) device for network discovery

**Inner Class:** `DdStatusListener` (interface)

**Methods:**
| Return Type | Method |
|---|---|
| `SsdpDevice` | `static getInstance()` |
| `void` | `enqueueStartServer()` / `enqueueStopServer()` |
| `void` | `initialize(ServerConf, DdStatusListener)` |

---

### ServerConf
**File:** `stubs/src/main/jasmin/com/sony/scalar/lib/ssdpdevice/v1_1/ServerConf.j`
**Fields:** `descriptionFile String`, `descriptionPath String`, `deviceDescription String`, `retry int`, `ssdpPort int`

---

### DdStatus (enum)
**File:** `stubs/src/main/jasmin/com/sony/scalar/lib/ssdpdevice/v1_1/DdStatus.j`
**Values:** `OK`, `ON_GOING`, `FINISHED`, `SKIPPED`, `ERROR_*`

---

## Architecture Summary

The OpenMemories-Framework reveals Sony's camera firmware architecture:

1. **CameraEx** is the central entry point -- all camera operations flow through it
2. **ParametersModifier** (1727 lines) provides fine-grained control over every camera setting
3. **ScalarProperties** stores persistent device configuration (~80 PROP_* keys)
4. **ScalarInput** maps physical buttons/dials to logical events (~160 ISV_KEY_* codes)
5. **AvindexStore** + **AvindexContentInfo** provide a content-provider-based media database
6. The **didep** subsystem handles device-independent concerns: power, temperature, GPS, NFC, Bluetooth, media formatting, caution/warnings
7. **Hardware.avio** handles display output including HDMI, finder, and sub-LCD
8. The entire system runs on Android (Gingerbread 2.3.7 or Jelly Bean 4.1.2) with Sony's proprietary native libraries
