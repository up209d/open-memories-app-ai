# Star Trails - API Reference

## 1. App Overview
| Field | Value |
|-------|-------|
| **Package** | `com.sony.imaging.app.startrails` |
| **Purpose** | Automates capture and merging of photos to create star trail videos or stills |
| **Description** | Star Trails captures a configurable series of long-exposure photos at timed intervals, develops each raw frame through the CameraSequence pipeline, optionally applies Miniature or Soft Focus picture effects, scales/crops frames to 16:9, and writes them into an AVI movie file on the memory card. Supports three shooting themes (Bright Night, Dark Night, Custom) with per-theme shutter speed and ISO presets. A built-in playback mode lets users review and manage recorded star trail sessions stored in a private SQLite database. |

## 2. Architecture
### Main Activity
- **StarTrails** (`com.sony.imaging.app.startrails.StarTrails`) extends `BaseApp`
- Entry point registered via `BootCompletedReceiver`; boots into shooting or playback mode depending on `BootFactor.bootKey`
- Version: 1.5001

### Key Classes
| Class | Path | Role |
|-------|------|------|
| `StarTrails` | `startrails/StarTrails.java` | Main activity, lifecycle, boot logic |
| `STAVICaptureProcess` | `startrails/shooting/STAVICaptureProcess.java` | Core capture pipeline: raw develop, effects, AVI encoding, interval shooting |
| `STUtility` | `startrails/util/STUtility.java` | Utility singleton: memory map config, file paths, calendar, shutter speed lists, remaining memory |
| `DataBaseOperations` | `startrails/database/DataBaseOperations.java` | SQLite database CRUD for star trail session records |
| `DataBaseAdapter` | `startrails/database/DataBaseAdapter.java` | Low-level SQLite open/close/query wrapper |
| `STExecutorCreator` | `startrails/shooting/STExecutorCreator.java` | Shooting executor factory, manages capture counter |
| `STNormalFunctionTable` | `startrails/shooting/trigger/custom/STNormalFunctionTable.java` | Key-to-function mapping table |
| `STCaptureDisplayModeObserver` | `startrails/common/STCaptureDisplayModeObserver.java` | Display mode persistence for star trails |
| `ThemeParameterSettingUtility` | `startrails/util/ThemeParameterSettingUtility.java` | Theme parameter management (shot count, recording mode, streak level) |
| `StillSAExec` | `startrails/metadatamanager/StillSAExec.java` | Still image metadata execution |
| `YuvToRgbConversion` | `startrails/metadatamanager/YuvToRgbConversion.java` | YUV-to-RGB bitmap conversion via DSP |
| `PlayBackController` | `startrails/playback/controller/PlayBackController.java` | Playback state management |
| `AviExporter` | `avi/AviExporter.java` | AVI movie file writer (shared library) |
| `ThumExporter` | `avi/thum/ThumExporter.java` | Thumbnail exporter using DSP/DeviceBuffer |

## 3. Sony Scalar API Usage

### com.sony.scalar.hardware.CameraEx
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `burstableTakePicture()` | none | `void` | Triggers next burst capture in interval sequence | `STAVICaptureProcess.java` |
| `cancelTakePicture()` | none | `void` | Cancels in-progress capture | `STAVICaptureProcess.java` |
| `setAutoPictureReviewControl(AutoPictureReviewControl)` | `AutoPictureReviewControl ctrl` | `void` | Configures auto picture review (disabled: time=0) | `STAVICaptureProcess.java` |
| `getNormalCamera()` | none | `Camera` | Returns underlying Android Camera object | `STAVICaptureProcess.java`, `STUtility.java` |
| `createEmptyParameters()` | none | `Camera.Parameters` | Creates empty parameter set for modification | `STUtility.java` |

### com.sony.scalar.hardware.CameraEx.AutoPictureReviewControl
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `new AutoPictureReviewControl()` | none | instance | Constructs review control object | `STAVICaptureProcess.java` |
| `setPictureReviewTime(int)` | `int time` (0=disabled) | `void` | Sets picture review display time in seconds | `STAVICaptureProcess.java` |

### com.sony.scalar.hardware.CameraEx.ParametersModifier
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `setAutoWhiteBalanceLock(boolean)` | `boolean lock` | `void` | Locks/unlocks AWB during interval capture | `STAVICaptureProcess.java` |
| `setPictureEffect(String)` | `String effect` | `void` | Sets picture effect mode (e.g., "off", miniature, soft focus) | `STAVICaptureProcess.java` |

### com.sony.scalar.hardware.CameraSequence
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `storeImage(OptimizedImage, boolean)` | `OptimizedImage img, boolean releaseRaw` | `void` | Stores developed image to card; boolean controls raw release | `STAVICaptureProcess.java` |

### com.sony.scalar.hardware.CameraSequence.Options
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `new Options()` | none | instance | Creates capture sequence options | `STAVICaptureProcess.java` |
| `setOption("MEMORY_MAP_FILE", String)` | key, file path | `void` | Sets memory map .so file path for capture sequence | `STAVICaptureProcess.java` |
| `setOption("EXPOSURE_COUNT", int)` | key, count (1) | `void` | Number of exposures per sequence cycle | `STAVICaptureProcess.java` |
| `setOption("RECORD_COUNT", int)` | key, count (1) | `void` | Number of images to record per cycle | `STAVICaptureProcess.java` |
| `setOption("INTERIM_PRE_REVIEW_ENABLED", boolean)` | key, true | `void` | Enables interim preview between captures | `STAVICaptureProcess.java` |
| `setOption("AUTO_RELEASE_LOCK_ENABLED", boolean)` | key, false | `void` | Disables auto lock release (manual control) | `STAVICaptureProcess.java` |

### com.sony.scalar.hardware.CameraSequence.DefaultDevelopFilter
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `new DefaultDevelopFilter()` | none | instance | Creates RAW-to-image development filter | `STAVICaptureProcess.java` |
| `setSource(RawData, boolean)` | `RawData raw, boolean releaseOnExecute` | `void` | Sets raw data source for development | `STAVICaptureProcess.java` |
| `execute()` | none | `void` | Executes raw development | `STAVICaptureProcess.java` |
| `getOutput()` | none | `OptimizedImage` | Returns developed OptimizedImage | `STAVICaptureProcess.java` |
| `release()` | none | `void` | Releases filter resources | `STAVICaptureProcess.java` |
| `clearSources()` | none | `void` | Clears source references without full release | `STAVICaptureProcess.java` |

### com.sony.scalar.hardware.CameraSequence.RawData
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `release()` | none | `void` | Releases raw data memory | `STAVICaptureProcess.java` |

### com.sony.scalar.hardware.DSP
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `DSP.createProcessor(String)` | `"sony-di-dsp"` | `DSP` | Creates DSP processor instance for YUV-to-RGB conversion | `STAVICaptureProcess.java` |
| `setProgram(String)` | file path to DSP program | `void` | Loads DSP program from file | `STAVICaptureProcess.java` |
| `release()` | none | `void` | Releases DSP resources | `STAVICaptureProcess.java` |

### com.sony.scalar.hardware.MemoryMapConfig
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `MemoryMapConfig.setAllocationPolicy(int)` | `1` (VERSION_1) | `void` | Sets memory allocation policy for platform version 2 | `STUtility.java` |

### com.sony.scalar.graphics.OptimizedImage
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `getWidth()` | none | `int` | Returns image width in pixels | `STAVICaptureProcess.java` |
| `getHeight()` | none | `int` | Returns image height in pixels | `STAVICaptureProcess.java` |
| `release()` | none | `void` | Releases native image memory | `STAVICaptureProcess.java` |

### com.sony.scalar.graphics.imagefilter.ScaleImageFilter
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `new ScaleImageFilter()` | none | instance | Creates scaling filter | `STAVICaptureProcess.java` |
| `setSource(OptimizedImage, boolean)` | `OptimizedImage img, boolean release` | `void` | Sets source image | `STAVICaptureProcess.java` |
| `setDestSize(int, int)` | `int width, int height` | `void` | Sets target dimensions (1920x1080 or 1280x720) | `STAVICaptureProcess.java` |
| `execute()` | none | `boolean` | Executes scaling; returns success | `STAVICaptureProcess.java` |
| `getOutput()` | none | `OptimizedImage` | Returns scaled image | `STAVICaptureProcess.java` |
| `clearSources()` | none | `void` | Clears source references | `STAVICaptureProcess.java` |
| `release()` | none | `void` | Releases filter resources | `STAVICaptureProcess.java` |

### com.sony.scalar.graphics.imagefilter.CropImageFilter
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `new CropImageFilter()` | none | instance | Creates cropping filter | `STAVICaptureProcess.java` |
| `setSrcRect(int, int, int, int)` | `left, top, right, bottom` | `void` | Sets crop rectangle for 16:9 extraction | `STAVICaptureProcess.java` |
| `setSource(OptimizedImage, boolean)` | `OptimizedImage img, boolean release` | `void` | Sets source image | `STAVICaptureProcess.java` |
| `execute()` | none | `boolean` | Executes crop; returns success | `STAVICaptureProcess.java` |
| `getOutput()` | none | `OptimizedImage` | Returns cropped image | `STAVICaptureProcess.java` |
| `clearSources()` | none | `void` | Clears source references | `STAVICaptureProcess.java` |
| `release()` | none | `void` | Releases filter resources | `STAVICaptureProcess.java` |

### com.sony.scalar.graphics.imagefilter.MiniatureImageFilter
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `new MiniatureImageFilter()` | none | instance | Creates miniature (tilt-shift) filter | `STAVICaptureProcess.java` |
| `isSupported()` | none | `boolean` | Checks if miniature effect is available | `STAVICaptureProcess.java` |
| `setMiniatureArea(int)` | `int areaId` (1-6) | `void` | Sets focus area: 1=H-center, 2=V-center, 3=left, 4=right, 5=upper, 6=lower | `STAVICaptureProcess.java` |
| `setSource(OptimizedImage, boolean)` | `OptimizedImage img, boolean release` | `void` | Sets source image | `STAVICaptureProcess.java` |
| `execute()` | none | `void` | Executes miniature effect | `STAVICaptureProcess.java` |
| `getOutput()` | none | `OptimizedImage` | Returns processed image | `STAVICaptureProcess.java` |
| `clearSources()` | none | `void` | Clears source references | `STAVICaptureProcess.java` |
| `release()` | none | `void` | Releases filter resources | `STAVICaptureProcess.java` |

### com.sony.scalar.graphics.imagefilter.SoftFocusImageFilter
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `new SoftFocusImageFilter()` | none | instance | Creates soft focus blur filter | `STAVICaptureProcess.java` |
| `isSupported()` | none | `boolean` | Checks if soft focus effect is available | `STAVICaptureProcess.java` |
| `setEffectLevel(int)` | `int level` (1=low, 2=medium, 3=high) | `void` | Sets blur intensity level | `STAVICaptureProcess.java` |
| `setSource(OptimizedImage, boolean)` | `OptimizedImage img, boolean release` | `void` | Sets source image | `STAVICaptureProcess.java` |
| `execute()` | none | `void` | Executes soft focus effect | `STAVICaptureProcess.java` |
| `getOutput()` | none | `OptimizedImage` | Returns processed image | `STAVICaptureProcess.java` |
| `clearSources()` | none | `void` | Clears source references | `STAVICaptureProcess.java` |
| `release()` | none | `void` | Releases filter resources | `STAVICaptureProcess.java` |

### com.sony.scalar.provider.AvindexStore
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `AvindexStore.getExternalMediaIds()` | none | `String[]` | Returns array of external media IDs (SD/MS card) | `STUtility.java`, `STAVICaptureProcess.java`, `DataBaseOperations.java` |
| `AvindexStore.getMediaInfo(String)` | `String mediaId` | `MediaInfo` | Gets media info (type: SD=1, MS=2) | `STUtility.java` |
| `AvindexStore.calculateAvailableSize(String)` | `String mediaId` | `void` | Recalculates available storage space | `STAVICaptureProcess.java` |
| `AvindexStore.waitLoadMediaComplete(String)` | `String mediaId` | `boolean` | Blocks until media index is fully loaded | `DataBaseOperations.java` |
| `AvindexStore.Images.Media.getContentUri(String)` | `String mediaId` | `Uri` | Gets content URI for image queries | `DataBaseOperations.java` |

### com.sony.scalar.media.MediaInfo
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `getMediaType()` | none | `int` | Returns media type: 1=SD, 2=MemoryStick | `STUtility.java` |

### com.sony.scalar.sysutil.ScalarInput
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `ScalarInput.getKeyStatus(int)` | `640` (WATER_HOUSING) | `KeyStatus` | Gets physical key status (for housing detection) | `StarTrails.java` |

### com.sony.scalar.sysutil.KeyStatus
| Field | Type | Description | Source File |
|-------|------|-------------|-------------|
| `valid` | `int` | 1 if status is valid | `StarTrails.java` |
| `status` | `int` | 1 if key/housing is attached | `StarTrails.java` |

### com.sony.scalar.sysutil.ScalarProperties
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `ScalarProperties.getInt(String)` | `"device.panel.aspect"` | `int` | Gets panel aspect ratio (e.g., 169 for 16:9) | `STAVICaptureProcess.java` |
| `ScalarProperties.getString(String)` | `"version.platform"` | `String` | Gets platform version string (e.g., "2.0") | `STUtility.java` |

### com.sony.scalar.sysutil.PlainCalendar / TimeUtil / PlainTimeZone
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `TimeUtil.getCurrentCalendar()` | none | `PlainCalendar` | Gets current date/time from camera clock | `STUtility.java` |
| `TimeUtil.getCurrentTimeZone()` | none | `PlainTimeZone` | Gets current timezone with GMT offset and summer time diff | `STUtility.java` |
| `PlainCalendar.year/month/day/hour/minute/second` | fields | `int` | Calendar field access | `STUtility.java` |
| `PlainTimeZone.gmtDiff` | field | `int` | GMT offset in minutes | `STUtility.java` |
| `PlainTimeZone.summerTimeDiff` | field | `int` | Summer time offset in minutes | `STUtility.java` |

### com.sony.scalar.sysutil.didep.Kikilog
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `Kikilog.setUserLog(int, Options)` | `int subId, Options opts` | `void` | Writes diagnostic log entry (app start=4238, theme switch=4239-4241, test shot=4242) | `StarTrails.java`, `STUtility.java`, `STAVICaptureProcess.java` |
| `Kikilog.Options.recType` | field (32) | `int` | Record type for log entries | `StarTrails.java` |

## 4. Image/Video Processing Pipeline
1. **Shutter triggered** -- `burstableTakePicture()` fires the camera shutter
2. **Raw data received** -- `onShutterSequence(RawData, CameraSequence)` delivers raw sensor data
3. **Raw development** -- `DefaultDevelopFilter.setSource(raw) / execute() / getOutput()` produces an `OptimizedImage`
4. **Optional picture effect** -- If Miniature or Soft Focus is active, applies `MiniatureImageFilter` or `SoftFocusImageFilter`
5. **Store to card** -- `CameraSequence.storeImage(img)` writes the JPEG to the memory card
6. **Crop to 16:9** -- `CropImageFilter.setSrcRect()` extracts 16:9 region from the developed image
7. **Scale for movie** -- `ScaleImageFilter.setDestSize(1920,1080)` or `(1280,720)` scales for video resolution
8. **AVI frame storage** -- `AviExporter.storeFrame(optImage)` writes the frame into the AVI file
9. **Thumbnail** -- First frame also stored via `AviExporter.storeThumbnail()`
10. **Preview bitmap** -- Every Nth frame is scaled to 160x90 and converted to `Bitmap` via DSP YUV-to-RGB for on-screen progress thumbnails
11. **Interval repeat** -- Steps 1-10 repeat for the configured number of shots with delay calculated from shutter speed
12. **Cleanup** -- All filters, DSP, AviExporter released; `AvindexStore.calculateAvailableSize()` refreshes free space

## 5. Button Handling
| Key Constant | Action |
|-------------|--------|
| `ISV_KEY_S1_1` (half-press) | Starts auto-focus; enters EE state |
| `ISV_KEY_S2` (full shutter) | Triggers capture sequence (test shot or interval series) |
| `ISV_KEY_ENTER` | Confirms menu selections, starts capture in some states |
| `ISV_KEY_PLAY` | Switches to playback mode (`changeApp(APP_PLAY)`) |
| `ISV_KEY_DELETE/SK2` | Back/cancel in menus; stops capture |
| `ISV_KEY_MENU/SK1` | Opens settings menu |
| `ISV_DIAL_1/2` | EV dial rotation (tracked via `setEVDialRotated()`) |
| Key 640 (WATER_HOUSING) | Housing detection via `ScalarInput.getKeyStatus(640)` |

## 6. Unique APIs
| API | Description |
|-----|-------------|
| `AviExporter` | Custom AVI movie writer with configurable resolution (1920x1080 / 1280x720), FPS (24/30), and thumbnail support. Uses native library at `STConstants.LIB_PATH`. |
| `MemoryMapConfig.setAllocationPolicy(1)` | Platform v2-specific memory allocation policy for capture sequence memory maps |
| `MemoryMapFile` | Generates memory map file paths for `CameraSequence.Options("MEMORY_MAP_FILE")` using app name "Startrails" |
| `DSP("sony-di-dsp")` | Hardware DSP processor used for YUV-to-RGB color space conversion of preview thumbnails |
| `SoftFocusImageFilter` | Three-level soft focus effect (low/medium/high) applied in the raw development pipeline |
| `MiniatureImageFilter` | Six-position tilt-shift miniature effect with configurable focus area |
| `Kikilog` | Sony diagnostic logging with numeric sub-IDs per event type (app start, theme switch, test shot) |
| `PlainCalendar` / `TimeUtil` | Camera-native time system used for UTC calculation, AVI file naming, and session timestamps |
