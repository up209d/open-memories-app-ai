# Touchless Shutter - API Reference

## 1. App Overview
| Field | Value |
|-------|-------|
| **Package** | `com.sony.imaging.app.each` (internal name "each") |
| **Purpose** | Uses EVF eye sensor as touchless trigger; also supports live bulb mode |
| **Description** | Touchless Shutter repurposes the camera's electronic viewfinder (EVF) eye proximity sensor as a shutter trigger. When the active display device switches from the LCD panel (`DEVICE_ID_PANEL`) to the EVF (`DEVICE_ID_FINDER`), the app simulates a full shutter press (S2 key). This enables hands-free photography -- the photographer brings their eye to the viewfinder to trigger capture. Supports multiple shooting modes: normal single capture, composite (3-exposure burst with development), picture effect processing, bracket capture, speed-priority burst, and live bulb with touchless trigger/cancel. Also supports movie recording triggered by EVF proximity. Includes bulb exposure cancel support on platform API >= 6 (excluding DSC-RX100M3). |

## 2. Architecture
### Main Activity
- **EachApp** (`com.sony.imaging.app.each.EachApp`) extends `BaseApp`
- Entry point via `BootCompletedReceiver`; supports shooting and playback modes
- Static flag `ExposingByTouchLessShutter` tracks whether current exposure was triggered by EVF sensor

### Key Classes
| Class | Path | Role |
|-------|------|------|
| `EachApp` | `each/EachApp.java` | Main activity, lifecycle, platform version check, Kikilog |
| `LiveBulb_S1OnEEState` | `each/shooting/LiveBulb_S1OnEEState.java` | EVF touchless trigger in live bulb half-press state: triggers S2 on FINDER detection |
| `LiveBulb_S1OffEEState` | `each/shooting/LiveBulb_S1OffEEState.java` | EVF touchless trigger in bulb off-EE state (similar logic) |
| `TLS_DevelopmentState` | `each/shooting/TLS_DevelopmentState.java` | Cancels AF/capture when EVF detected during development (image processing) |
| `TLS_NormalCaptureState` | `each/shooting/TLS_NormalCaptureState.java` | Cancels AF/capture when EVF detected during normal capture |
| `TLS_MovieRecStandbyState` | `each/shooting/TLS_MovieRecStandbyState.java` | Starts movie recording when EVF detected in standby |
| `TLS_MovieRecState` | `each/shooting/TLS_MovieRecState.java` | EVF handling during active movie recording |
| `TLS_MovieCaptureState` | `each/shooting/TLS_MovieCaptureState.java` | EVF handling during movie capture |
| `EachExecutorCreator` | `each/shooting/EachExecutorCreator.java` | Shooting executor factory: selects SingleProcess, CompositProcess, EffectProcess, or BracketProcess |
| `SingleProcess` | `each/shooting/SingleProcess.java` | Single-shot capture: `burstableTakePicture()`, `cancelExposure()` for bulb |
| `CompositProcess` | `each/shooting/CompositProcess.java` | 3-exposure composite: burst capture, raw development, image store |
| `EffectProcess` | `each/shooting/EffectProcess.java` | Picture effect capture with raw development pipeline |
| `BracketProcess` | `each/shooting/BracketProcess.java` | Bracket exposure capture |
| `NormalBurstExecutor` | `each/shooting/NormalBurstExecutor.java` | Speed-priority continuous burst executor |
| `EachForceSettingState` | `each/shooting/EachForceSettingState.java` | Forces camera settings on entry |
| `DeleteMultipleIndexLayout` | `each/playback/layout/DeleteMultipleIndexLayout.java` | Playback deletion with display management |

## 3. Sony Scalar API Usage

### com.sony.scalar.hardware.CameraEx
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `burstableTakePicture()` | none | `void` | Triggers capture (single, burst, or composite sequence) | `SingleProcess.java`, `CompositProcess.java` |
| `cancelTakePicture()` | none | `void` | Cancels in-progress capture | `SingleProcess.java` |
| `cancelExposure()` | none | `void` | Cancels bulb exposure (API >= 6, excludes RX100M3) | `SingleProcess.java` |
| `startSelfTimerShutter()` | none | `void` | Starts self-timer shutter sequence | `SingleProcess.java`, `CompositProcess.java` |
| `getNormalCamera()` | none | `Camera` | Gets underlying Android Camera object | `EachExecutorCreator.java` |

### com.sony.scalar.hardware.CameraSequence
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `storeImage(OptimizedImage, boolean)` | `img, releaseRaw` | `void` | Stores developed image to memory card | `CompositProcess.java`, `EffectProcess.java` |

### com.sony.scalar.hardware.CameraSequence.Options
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `new Options()` | none | instance | Creates sequence options | `CompositProcess.java`, `EffectProcess.java` |
| `setOption("MEMORY_MAP_FILE", String)` | key, `.so` path | `void` | Memory map file: `libSingleCap.so` | `CompositProcess.java`, `EffectProcess.java` |
| `setOption("EXPOSURE_COUNT", int)` | key, `3` (composite) or default | `void` | Exposures per sequence (3 for composite) | `CompositProcess.java` |
| `setOption("RECORD_COUNT", int)` | key, `1` | `void` | Images to record per sequence | `CompositProcess.java` |
| `setOption("INTERIM_PRE_REVIEW_ENABLED", boolean)` | key, `true` | `void` | Enables interim preview | `CompositProcess.java` |
| `setOption("AUTO_RELEASE_LOCK_ENABLED", boolean)` | key, `false` | `void` | Manual lock release | `CompositProcess.java` |

### com.sony.scalar.hardware.CameraSequence.DefaultDevelopFilter
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `new DefaultDevelopFilter()` | none | instance | Creates RAW development filter | `CompositProcess.java`, `EffectProcess.java` |
| `setSource(RawData, boolean)` | `raw, true` | `void` | Sets raw source, releases on execute | `CompositProcess.java`, `EffectProcess.java` |
| `execute()` | none | `void` | Executes raw-to-image development | `CompositProcess.java`, `EffectProcess.java` |
| `getOutput()` | none | `OptimizedImage` | Returns developed image | `CompositProcess.java`, `EffectProcess.java` |
| `release()` | none | `void` | Releases filter resources | `CompositProcess.java` |

### com.sony.scalar.hardware.CameraSequence.RawData
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `release()` | none | `void` | Releases raw data memory | `CompositProcess.java` |

### com.sony.scalar.graphics.OptimizedImage
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| (Used as output from DefaultDevelopFilter) | -- | -- | Developed image passed to `storeImage()` | `CompositProcess.java`, `EffectProcess.java` |

### com.sony.scalar.hardware.avio.DisplayManager
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `new DisplayManager()` | none | instance | Creates display manager for EVF sensor monitoring | `LiveBulb_S1OnEEState.java`, `TLS_DevelopmentState.java`, `TLS_NormalCaptureState.java`, `TLS_MovieRecStandbyState.java`, `TLS_MovieRecState.java`, `TLS_MovieCaptureState.java` |
| `setDisplayStatusListener(DisplayEventListener)` | listener | `void` | Registers display device change callback | All TLS state classes |
| `getActiveDevice()` | none | `String` | Returns active display: `"DEVICE_ID_PANEL"`, `"DEVICE_ID_FINDER"`, `"DEVICE_ID_HDMI"`, or `null` | All TLS state classes |
| `releaseDisplayStatusListener()` | none | `void` | Unregisters display listener | All TLS state classes |
| `finish()` | none | `void` | Releases display manager resources | All TLS state classes |

### com.sony.scalar.hardware.avio.DisplayManager.DisplayEventListener
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `onDeviceStatusChanged(int eventId)` | `4096` = device changed | `void` | Callback when active display device changes; core touchless trigger mechanism | All TLS state classes |

### com.sony.scalar.sysutil.ScalarProperties
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `ScalarProperties.getString("version.platform")` | `"version.platform"` | `String` | Gets platform version for `cancelExposure` availability check (>= 6.0) | `EachApp.java`, `LiveBulb_S1OnEEState.java`, `TLS_DevelopmentState.java`, `TLS_NormalCaptureState.java`, `TLS_MovieRecStandbyState.java` |
| `ScalarProperties.getString("model.name")` | `"model.name"` | `String` | Gets model name to exclude DSC-RX100M3 from `cancelExposure` | `EachApp.java` |

### com.sony.scalar.sysutil.ScalarInput
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `ScalarInput.getKeyStatus(int)` | `AppRoot.USER_KEYCODE.WATER_HOUSING` (640) | `KeyStatus` | Checks underwater housing attachment | `EachApp.java` |

### com.sony.scalar.sysutil.KeyStatus
| Field | Type | Description | Source File |
|-------|------|-------------|-------------|
| `valid` | `int` | 1 if status is valid | `EachApp.java` |
| `status` | `int` | 1 if housing attached | `EachApp.java` |

### com.sony.scalar.sysutil.didep.Kikilog
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `Kikilog.setUserLog(int, Options)` | `int subId, Options opts` | `void` | Logs events with recType=32 | `EachApp.java`, `LiveBulb_S1OnEEState.java`, `TLS_MovieRecStandbyState.java` |

**Kikilog Sub-IDs:**
| Constant | Value | Event |
|----------|-------|-------|
| `kikilog_subid_BootTime` | `327776` | App boot |
| `kikilog_subid_NormalCaptureTimes` | `327777` | Normal capture triggered by touchless |
| `kikilog_subid_BulbCaptureTimes` | `327778` | Bulb capture triggered by touchless |
| `kikilog_subid_MovieCaptureTimes` | `327779` | Movie recording triggered by touchless |

## 4. Image/Video Processing Pipeline

### Normal Single Capture (via EVF trigger)
1. **EVF detection** -- `DisplayManager.onDeviceStatusChanged(4096)` fires when eye approaches viewfinder
2. **Device check** -- `getActiveDevice()` returns `"DEVICE_ID_FINDER"` (or `null` on platform > 2.0)
3. **Previous display check** -- Only triggers if `mCurrentDisplay` was `"DEVICE_ID_PANEL"` (LCD)
4. **Caution check** -- `CautionUtilityClass.getInstance().getCurrentCautionData()` must be null
5. **S2 simulation** -- `ExecutorCreator.getInstance().getSequence().inquireKey(USER_KEYCODE.S2_ON)` triggers full shutter
6. **Capture** -- `SingleProcess.burstableTakePicture()` fires the shutter
7. **Store** -- Camera firmware handles normal image storage

### Composite Capture (3-exposure)
1. **EVF triggers S2** -- Same as normal capture steps 1-5
2. **Multi-burst** -- `CompositProcess.burstableTakePicture()` fires 3 times (EXPOSURE_COUNT=3)
3. **Raw accumulation** -- First 2 raw data objects queued; on 3rd, development begins
4. **Development** -- `DefaultDevelopFilter.setSource(raw) / execute() / getOutput()`
5. **Store** -- `sequence.storeImage(img, true)` saves final composite image

### Effect Capture
1. **Setup** -- `EffectProcess.prepare()` sets MEMORY_MAP_FILE to `libSingleCap.so`
2. **Capture** -- Normal shutter trigger
3. **Development** -- `DefaultDevelopFilter` processes raw data with progress callbacks
4. **Store** -- `sequence.storeImage(img, true)` saves processed image

### Bulb Capture (cancel via EVF leave)
1. **EVF trigger** -- Same as normal but with bulb shutter speed detected via `CameraSetting.isShutterSpeedBulb()`
2. **Exposure starts** -- Bulb exposure begins
3. **EVF leave** -- When display switches back to `DEVICE_ID_PANEL`, `cancelExposure()` ends bulb (API >= 6)

### Movie Recording (via EVF trigger)
1. **EVF detection** -- `TLS_MovieRecStandbyState.onDeviceStatusChanged(4096)` detects FINDER
2. **Key dispatch** -- `dispatchKeyEvent(new KeyEvent(..., MOVIE_REC))` simulates record button press
3. **Recording** -- Camera firmware handles movie recording

## 5. Button Handling
| Key Constant | Action |
|-------------|--------|
| `ISV_KEY_S1_1` (half-press) | Standard auto-focus (also handled normally alongside touchless) |
| `ISV_KEY_S2` (full shutter) | Normal shutter trigger; also simulated by EVF sensor |
| `ISV_KEY_ENTER` | Confirms menus |
| `ISV_KEY_PLAY` | Switches to playback mode |
| `ISV_KEY_DELETE/SK2` | Back/cancel |
| `ISV_KEY_MENU/SK1` | Opens settings menu |
| `ISV_KEY_STASTOP` / `MOVIE_REC` | Movie recording; simulated by EVF in movie standby state |
| Key 640 (WATER_HOUSING) | Housing detection via `ScalarInput.getKeyStatus()` |
| `USER_KEYCODE.S2_ON` | Virtual shutter key injected via `inquireKey()` when EVF triggers |
| `USER_KEYCODE.MOVIE_REC` | Virtual movie record key injected via `dispatchKeyEvent()` |

## 6. Unique APIs
| API | Description |
|-----|-------------|
| `DisplayManager.onDeviceStatusChanged(4096)` | Core touchless mechanism: event ID 4096 indicates display device change; detecting FINDER = eye at viewfinder |
| `DisplayManager.getActiveDevice()` | Returns display device strings: `"DEVICE_ID_PANEL"` (LCD), `"DEVICE_ID_FINDER"` (EVF), `"DEVICE_ID_HDMI"` (external); `null` treated as FINDER on platform > 2.0 |
| `CameraEx.cancelExposure()` | Bulb exposure termination; only available on platform API >= 6, excluding DSC-RX100M3 |
| `ExecutorCreator.getSequence().inquireKey(USER_KEYCODE.S2_ON)` | Programmatic shutter trigger by injecting S2 key event into the camera's shooting sequence |
| `dispatchKeyEvent(new KeyEvent(..., MOVIE_REC))` | Programmatic movie record trigger via Android key event dispatch |
| `CameraSetting.isShutterSpeedBulb()` | Detects whether current shutter speed is BULB mode for Kikilog differentiation |
| `ExposingByTouchLessShutter` static flag | Global boolean tracking whether current exposure was initiated by touchless sensor vs. physical button |
| Composite 3-exposure capture | `CompositProcess` queues 3 raw exposures before developing final image, using `EXPOSURE_COUNT=3` |
| `EachApp.is_cancelExposure_available()` | Platform version + model check: `version.platform >= 6 && !model.equals("DSC-RX100M3")` |
| Memory map `libSingleCap.so` | Capture sequence memory map at `/android/data/data/com.sony.imaging.app.each/lib/libSingleCap.so` (CompositProcess) or `/android/data/lib/com.sony.imaging.app.each/lib/libSingleCap.so` (EffectProcess) |
