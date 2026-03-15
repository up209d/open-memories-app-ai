# Time Lapse - API Reference

## 1. App Overview
| Field | Value |
|-------|-------|
| **Package** | `com.sony.imaging.app.timelapse` |
| **Purpose** | Captures series of photos at set intervals and compiles them into a movie file in-camera |
| **Description** | Time Lapse automates interval shooting with configurable shot count and timing, develops each raw frame through the CameraSequence pipeline, optionally applies Miniature picture effects, scales/crops frames to 16:9, and writes them into an AVI movie file. Features automatic exposure (AE) tracking between shots via `TimelapseExposureController`, RAW+JPEG support, remote control mode, silent shutter integration, and an Angle Shift add-on for motion panning effects. Includes playback mode for reviewing recorded sessions stored in a private SQLite database. Supports "maybe phase diff" detection for specific lens/body combinations. Version: 3.4001. |

## 2. Architecture
### Main Activity
- **TimeLapse** (`com.sony.imaging.app.timelapse.TimeLapse`) extends `BaseApp`
- Entry point registered via `BootCompletedReceiver`; boots into shooting or playback
- Registers custom controllers for CreativeStyle, PictureEffect, and FocusMode
- Detects Angle Shift add-on at `/data/data/com.sony.imaging.app.angleshiftaddon`

### Key Classes
| Class | Path | Role |
|-------|------|------|
| `TimeLapse` | `timelapse/TimeLapse.java` | Main activity, lifecycle, boot logic, controller registration |
| `TLAviCaptureProcess` | `timelapse/shooting/TLAviCaptureProcess.java` | Core capture pipeline: raw develop, AE tracking, effects, AVI encoding |
| `TimelapseExecutorCreator` | `timelapse/shooting/TimelapseExecutorCreator.java` | Shooting executor factory |
| `TLCommonUtil` | `timelapse/metadatamanager/TLCommonUtil.java` | Utility: memory map config, file paths, interval/shot count, exposure settings |
| `DataBaseOperations` | `timelapse/databaseutil/DataBaseOperations.java` | SQLite CRUD for time lapse session records |
| `DataBaseAdapter` | `timelapse/databaseutil/DataBaseAdapter.java` | Low-level SQLite wrapper |
| `TimelapseExposureController` | `avi/timelapse/exposure/TimelapseExposureController.java` | AE tracking between interval shots |
| `YuvToRgbConversion` | `timelapse/metadatamanager/YuvToRgbConversion.java` | YUV-to-RGB conversion via DSP |
| `AngleShiftProgressLayout` | `timelapse/angleshift/layout/AngleShiftProgressLayout.java` | Angle Shift motion panning UI and processing |
| `PlayBackController` | `timelapse/playback/controller/PlayBackController.java` | Playback state management |
| `AviExporter` | `avi/AviExporter.java` | AVI movie file writer (shared native library) |
| `TimelapseFunctionTable` | `timelapse/shooting/TimelapseFunctionTable.java` | Key-to-function mapping |
| `MovieController` | `timelapse/shooting/controller/MovieController.java` | Movie resolution selection |
| `SelfTimerIntervalPriorityController` | `timelapse/shooting/controller/SelfTimerIntervalPriorityController.java` | Self-timer and interval priority |

## 3. Sony Scalar API Usage

### com.sony.scalar.hardware.CameraEx
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `burstableTakePicture()` | none | `void` | Triggers next burst capture in interval sequence | `TLAviCaptureProcess.java` |
| `cancelTakePicture()` | none | `void` | Cancels in-progress capture after shutter sequence | `TLAviCaptureProcess.java` |
| `setAutoPictureReviewControl(AutoPictureReviewControl)` | `AutoPictureReviewControl ctrl` | `void` | Disables auto picture review (time=0) during capture | `TLAviCaptureProcess.java` |
| `getNormalCamera()` | none | `Camera` | Returns underlying Android Camera for parameter access | `TLAviCaptureProcess.java` |

### com.sony.scalar.hardware.CameraEx.AutoPictureReviewControl
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `new AutoPictureReviewControl()` | none | instance | Constructs review control | `TLAviCaptureProcess.java` |
| `setPictureReviewTime(int)` | `0` (disabled) | `void` | Sets review display time | `TLAviCaptureProcess.java` |

### com.sony.scalar.hardware.CameraEx.ParametersModifier
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `setAutoWhiteBalanceLock(boolean)` | `boolean lock` | `void` | Locks/unlocks AWB during interval capture | `TLAviCaptureProcess.java` |
| `getPictureStorageFormat()` | none | `String` | Checks if RAW+JPEG mode is active | `TLAviCaptureProcess.java` |

### com.sony.scalar.hardware.CameraSequence
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `storeImage(OptimizedImage, boolean)` | `img, releaseRaw` | `void` | Stores developed image to memory card | `TLAviCaptureProcess.java` |
| `getCameraEx()` | none | `CameraEx` | Returns CameraEx from sequence (used by AE tracking) | `TLAviCaptureProcess.java` |

### com.sony.scalar.hardware.CameraSequence.Options
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `new Options()` | none | instance | Creates sequence options | `TLAviCaptureProcess.java` |
| `setOption("MEMORY_MAP_FILE", String)` | key, file path | `void` | Sets memory map .so path | `TLAviCaptureProcess.java` |
| `setOption("EXPOSURE_COUNT", int)` | key, `1` | `void` | One exposure per cycle | `TLAviCaptureProcess.java` |
| `setOption("RECORD_COUNT", int)` | key, `1` | `void` | One image recorded per cycle | `TLAviCaptureProcess.java` |
| `setOption("INTERIM_PRE_REVIEW_ENABLED", boolean)` | key, `true` | `void` | Enables interim preview | `TLAviCaptureProcess.java` |
| `setOption("AUTO_RELEASE_LOCK_ENABLED", boolean)` | key, `false` | `void` | Manual lock release control | `TLAviCaptureProcess.java` |

### com.sony.scalar.hardware.CameraSequence.DefaultDevelopFilter
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `new DefaultDevelopFilter()` | none | instance | Creates RAW development filter | `TLAviCaptureProcess.java` |
| `setRawFileStoreEnabled(boolean)` | `boolean isRAWJPEG` | `void` | Enables RAW file storage alongside JPEG (test shot mode) | `TLAviCaptureProcess.java` |
| `setSource(RawData, boolean)` | `raw, release` | `void` | Sets raw data source | `TLAviCaptureProcess.java` |
| `execute()` | none | `void` | Executes raw development | `TLAviCaptureProcess.java` |
| `getOutput()` | none | `OptimizedImage` | Returns developed image | `TLAviCaptureProcess.java` |
| `clearSources()` | none | `void` | Clears source references | `TLAviCaptureProcess.java` |
| `release()` | none | `void` | Releases filter resources | `TLAviCaptureProcess.java` |

### com.sony.scalar.hardware.CameraSequence.RawData
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `release()` | none | `void` | Releases raw data memory | `TLAviCaptureProcess.java` |
| `isValid()` | none | `boolean` | Checks if raw data is still valid | `TLAviCaptureProcess.java` |

### com.sony.scalar.hardware.DSP
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `DSP.createProcessor(String)` | `"sony-di-dsp"` | `DSP` | Creates DSP for YUV-to-RGB conversion | `TLAviCaptureProcess.java` |
| `setProgram(String)` | file path | `void` | Loads DSP program | `TLAviCaptureProcess.java` |
| `release()` | none | `void` | Releases DSP resources | `TLAviCaptureProcess.java` |

### com.sony.scalar.graphics.OptimizedImage
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `getWidth()` | none | `int` | Image width in pixels | `TLAviCaptureProcess.java` |
| `getHeight()` | none | `int` | Image height in pixels | `TLAviCaptureProcess.java` |
| `release()` | none | `void` | Releases native image memory | `TLAviCaptureProcess.java` |

### com.sony.scalar.graphics.imagefilter.ScaleImageFilter
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `new ScaleImageFilter()` | none | instance | Creates scaling filter | `TLAviCaptureProcess.java` |
| `setSource(OptimizedImage, boolean)` | `img, release` | `void` | Sets source image | `TLAviCaptureProcess.java` |
| `setDestSize(int, int)` | `width, height` | `void` | Target dimensions (1920x1080 or 1280x720) | `TLAviCaptureProcess.java` |
| `execute()` | none | `boolean` | Executes scaling | `TLAviCaptureProcess.java` |
| `getOutput()` | none | `OptimizedImage` | Returns scaled image | `TLAviCaptureProcess.java` |
| `clearSources()` | none | `void` | Clears sources | `TLAviCaptureProcess.java` |
| `release()` | none | `void` | Releases filter | `TLAviCaptureProcess.java` |

### com.sony.scalar.graphics.imagefilter.CropImageFilter
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `new CropImageFilter()` | none | instance | Creates crop filter | `TLAviCaptureProcess.java` |
| `setSrcRect(int, int, int, int)` | `left, top, right, bottom` | `void` | Sets 16:9 crop rectangle | `TLAviCaptureProcess.java` |
| `setSource(OptimizedImage, boolean)` | `img, release` | `void` | Sets source | `TLAviCaptureProcess.java` |
| `execute()` | none | `boolean` | Executes crop | `TLAviCaptureProcess.java` |
| `getOutput()` | none | `OptimizedImage` | Returns cropped image | `TLAviCaptureProcess.java` |
| `clearSources()` | none | `void` | Clears sources | `TLAviCaptureProcess.java` |
| `release()` | none | `void` | Releases filter | `TLAviCaptureProcess.java` |

### com.sony.scalar.graphics.imagefilter.MiniatureImageFilter
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `new MiniatureImageFilter()` | none | instance | Creates miniature (tilt-shift) filter | `TLAviCaptureProcess.java` |
| `isSupported()` | none | `boolean` | Checks hardware support | `TLAviCaptureProcess.java` |
| `setMiniatureArea(int)` | `int areaId` (1-6) | `void` | Sets focus area position | `TLAviCaptureProcess.java` |
| `setSource(OptimizedImage, boolean)` | `img, release` | `void` | Sets source | `TLAviCaptureProcess.java` |
| `execute()` | none | `void` | Applies miniature effect | `TLAviCaptureProcess.java` |
| `getOutput()` | none | `OptimizedImage` | Returns processed image | `TLAviCaptureProcess.java` |
| `clearSources()` | none | `void` | Clears sources | `TLAviCaptureProcess.java` |
| `release()` | none | `void` | Releases filter | `TLAviCaptureProcess.java` |

### com.sony.scalar.provider.AvindexStore
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `AvindexStore.getExternalMediaIds()` | none | `String[]` | External media IDs for storage | `TLAviCaptureProcess.java`, `TLCommonUtil.java` |
| `AvindexStore.calculateAvailableSize(String)` | `String mediaId` | `void` | Recalculates available storage space after capture | `TLAviCaptureProcess.java` |
| `AvindexStore.getMediaInfo(String)` | `String mediaId` | `MediaInfo` | Gets media type info | `TLCommonUtil.java` |
| `AvindexStore.waitLoadMediaComplete(String)` | `String mediaId` | `boolean` | Blocks until index loaded | `DataBaseOperations.java` |
| `AvindexStore.Images.Media.getContentUri(String)` | `String mediaId` | `Uri` | Content URI for image queries | `DataBaseOperations.java` |

### com.sony.scalar.sysutil.ScalarProperties
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `ScalarProperties.getInt("device.panel.aspect")` | `"device.panel.aspect"` | `int` | Panel aspect ratio (169 for 16:9) | `TLAviCaptureProcess.java` |
| `ScalarProperties.getString("version.platform")` | `"version.platform"` | `String` | Platform version for feature detection | `TLCommonUtil.java` |

### com.sony.scalar.sysutil.ScalarInput
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `ScalarInput.getKeyStatus(int)` | `640` (WATER_HOUSING) | `KeyStatus` | Checks housing attachment status | `TimeLapse.java` |

### com.sony.scalar.sysutil.KeyStatus
| Field | Type | Description | Source File |
|-------|------|-------------|-------------|
| `valid` | `int` | 1 if status reading is valid | `TimeLapse.java` |
| `status` | `int` | 1 if housing is attached | `TimeLapse.java` |

### com.sony.scalar.sysutil.PlainCalendar / TimeUtil / PlainTimeZone
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `TimeUtil.getCurrentCalendar()` | none | `PlainCalendar` | Camera clock time | `ScalarCalendar.java` |
| `TimeUtil.getCurrentTimeZone()` | none | `PlainTimeZone` | Timezone with GMT offset | `ScalarCalendar.java` |

### com.sony.scalar.hardware.avio.DisplayManager
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `new DisplayManager()` | none | instance | Creates display manager for OLED burn-in prevention | `OLEDBurning.java`, `HDMIInfoWrapper.java` |
| `getActiveDevice()` | none | `String` | Returns active display ("DEVICE_ID_PANEL", "DEVICE_ID_FINDER", "DEVICE_ID_HDMI") | `HDMIInfoWrapper.java` |
| `setDisplayStatusListener(DisplayEventListener)` | listener | `void` | Registers display change callback | `OLEDBurning.java` |
| `releaseDisplayStatusListener()` | none | `void` | Unregisters listener | `OLEDBurning.java` |
| `finish()` | none | `void` | Releases display manager resources | `OLEDBurning.java` |

### com.sony.scalar.sysutil.didep.Kikilog
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `Kikilog.setUserLog(int, Options)` | `int subId, Options opts` | `void` | Logs app start (4188 standard, 4190 lite) | `TimeLapse.java` |

### com.sony.scalar.sysutil.didep.Settings
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| (Used via HDMIInfoWrapper) | -- | -- | HDMI output settings detection | `HDMIInfoWrapper.java` |

### com.sony.scalar.hardware.MemoryMapConfig
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `MemoryMapConfig.setAllocationPolicy(int)` | `1` | `void` | Platform v2 memory allocation policy | `TLCommonUtil.java` |

## 4. Image/Video Processing Pipeline
1. **Pre-capture setup** -- `preTakePicture()` initializes counters, sets AWB lock, creates movie objects, starts AE metering
2. **AE tracking start** -- `TimelapseExposureController.open() / startMetering()` begins exposure metering
3. **Shutter triggered** -- `burstableTakePicture()` fires camera; optional self-timer delay
4. **Raw data received** -- `onShutterSequence(RawData, CameraSequence)` delivers raw sensor data
5. **AE resume** -- `mTLEX.resumeMetering()` resumes AE tracking for next shot
6. **Cancel pending** -- `cancelTakePicture()` cancels any in-progress capture
7. **Test shot branch** -- If test shot mode: develop raw, optionally apply Miniature, store to card, return
8. **Raw development** -- `DefaultDevelopFilter.setSource(raw) / execute() / getOutput()` with optional `setRawFileStoreEnabled(true)` for RAW+JPEG
9. **Optional Miniature** -- `MiniatureImageFilter.setMiniatureArea() / execute()` applies tilt-shift if configured
10. **Store to card** -- `CameraSequence.storeImage(img, false)` writes JPEG (and optionally RAW) to memory card
11. **Crop to 16:9** -- `CropImageFilter.setSrcRect()` extracts 16:9 region
12. **Scale for movie** -- `ScaleImageFilter.setDestSize(1920,1080)` or `(1280,720)`
13. **AVI encoding** -- `AviExporter.storeFrame()` writes frame; first frame also `storeThumbnail()`
14. **Preview bitmap** -- Every Nth frame converted to 160x90 bitmap via DSP YUV-to-RGB
15. **Memory check** -- `calculateAvailableSize()` checks remaining storage
16. **Interval delay** -- Waits for configured interval, then repeats from step 3
17. **AE stop** -- `mTLEX.stopMetering() / close()` on terminate
18. **Cleanup** -- All filters, DSP, AviExporter released; `AvindexStore.calculateAvailableSize()` refreshes

## 5. Button Handling
| Key Constant | Action |
|-------------|--------|
| `ISV_KEY_S1_1` (half-press) | Starts auto-focus; enters EE state |
| `ISV_KEY_S2` (full shutter) | Triggers test shot or starts interval capture sequence |
| `ISV_KEY_ENTER` | Confirms menu/settings; starts capture in some states |
| `ISV_KEY_PLAY` | Switches to playback mode |
| `ISV_KEY_DELETE/SK2` | Back/cancel; stops interval capture |
| `ISV_KEY_MENU/SK1` | Opens settings menu |
| `ISV_DIAL_1/2` | EV dial for exposure adjustment |
| Key 640 (WATER_HOUSING) | Housing detection via `ScalarInput.getKeyStatus(640)` |

## 6. Unique APIs
| API | Description |
|-----|-------------|
| `TimelapseExposureController` | AE tracking controller that meters exposure between interval shots, with `open()`, `startMetering()`, `resumeMetering()`, `stopMetering()`, `close()` lifecycle |
| `DefaultDevelopFilter.setRawFileStoreEnabled(boolean)` | Enables simultaneous RAW file storage during development (RAW+JPEG mode) |
| `RawData.isValid()` | Validity check on raw data before release |
| `AviExporter` | Custom AVI movie writer with 1920x1080/1280x720 resolution, 24/30 FPS, thumbnail support |
| `MemoryMapConfig.setAllocationPolicy(1)` | Platform v2 memory allocation policy for capture sequences |
| `DSP("sony-di-dsp")` | Hardware DSP for YUV-to-RGB preview thumbnail conversion |
| Angle Shift add-on | Optional panning motion effect detected at `/data/data/com.sony.imaging.app.angleshiftaddon`; changes boot logo and enables angle shift shooting mode |
| `CameraSequence.getCameraEx()` | Retrieves CameraEx from within a sequence callback (used by AE tracking) |
| `WatchDogTimerForMaybePhaseDiff` | Timer-based workaround for phase-detection focus issues on certain lens/body combinations |
| `AvailableInfo.addDatabase("InhInf_Additional.db")` | Registers additional capability database for feature detection |
