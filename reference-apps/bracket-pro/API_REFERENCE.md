# Bracket Pro - API Reference

## 1. App Overview

| Field | Value |
|-------|-------|
| **Package** | `com.sony.imaging.app.bracketpro` |
| **Purpose** | Multi-exposure bracketing across aperture, shutter speed, focus, and flash |
| **Description** | Captures 3-shot bracketed sequences with automatic parameter adjustment between shots. Supports four bracket modes: Aperture Bracket (adjusts f-stop between shots), Shutter Speed Bracket (adjusts exposure time), Focus Bracket (shifts focus position), and Flash Bracket (toggles flash on/off between frames). Uses `burstableTakePicture()` for rapid sequential capture with delayed callbacks to allow camera parameter changes between shots. Includes auto white balance locking, AE lock control, and auto picture review time management. |

## 2. Architecture

### Main Activity
- `BracketMasterMain` extends `BaseApp`, registered as `singleInstance` launcher activity
- Version: 1.4029
- Boot logo: `p_16_dd_parts_bp_launchericon`
- Supports still recording mode only (`getSupportingRecMode() = 1`)

### Key Classes

| Class | Path | Role |
|-------|------|------|
| `BracketMasterMain` | `BracketMasterMain.java` | Main activity - handles boot, lifecycle, registers custom FlashController |
| `BMCaptureProcess` | `shooting/BMCaptureProcess.java` | Core bracketing engine - manages 3-shot sequences for all bracket types |
| `BMExecutorCreator` | `shooting/BMExecutorCreator.java` | Creates capture executor, routes to NormalBurstExecutor for speed-priority burst |
| `NormalBurstExecutor` | `shooting/NormalBurstExecutor.java` | Burst-mode executor with custom ShutterListener |
| `BMMenuController` | `menu/controller/BMMenuController.java` | Manages bracket type selection (Aperture/Shutter/Focus/Flash) |
| `BracketMasterController` | `menu/controller/BracketMasterController.java` | Controls bracketing range/position settings |
| `BMShootingState` | `shooting/state/BMShootingState.java` | Shooting state - forces flash off for non-flash bracket modes |
| `BMEEState` | `shooting/state/BMEEState.java` | Electronic viewfinder state management |
| `BMFlashController` | `menu/controller/BMFlashController.java` | Custom flash controller for bracket-specific flash behavior |
| `BracketMasterUtil` | `commonUtil/BracketMasterUtil.java` | Utility for iris ring detection, bracket type string resolution |
| `BMAperture` | `BMAperture.java` | Aperture step calculation for aperture bracket mode |

## 3. Sony Scalar API Usage

### com.sony.scalar.hardware.CameraEx

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `cameraEx.burstableTakePicture()` | None | `void` | Takes picture in burstable mode for sequential bracket capture | `shooting/BMCaptureProcess.java` |
| `cameraEx.cancelTakePicture()` | None | `void` | Cancels current take picture to prepare for next bracket shot | `shooting/BMCaptureProcess.java` |
| `cameraEx.adjustAperture(int)` | Steps (positive=open, negative=close) | `void` | Adjusts aperture by relative steps between aperture bracket shots | `shooting/BMCaptureProcess.java` |
| `cameraEx.adjustShutterSpeed(int)` | Steps (positive=faster, negative=slower) | `void` | Adjusts shutter speed by relative steps between shutter bracket shots | `shooting/BMCaptureProcess.java` |
| `cameraEx.shiftFocusPosition(int)` | Steps (positive=far, negative=near) | `void` | Shifts focus position by relative steps between focus bracket shots | `shooting/BMCaptureProcess.java` |
| `cameraEx.startSelfTimerShutter()` | None | `void` | Starts self-timer shutter for delayed bracket sequence | `shooting/BMCaptureProcess.java` |
| `cameraEx.getNormalCamera()` | None | `Camera` | Gets underlying Android Camera object for direct parameter control | `shooting/BMCaptureProcess.java` |
| `cameraEx.createEmptyParameters()` | None | `Camera.Parameters` | Creates empty parameters for flash mode changes | `shooting/BMCaptureProcess.java` |
| `cameraEx.setAutoPictureReviewControl(AutoPictureReviewControl)` | Control object | `void` | Sets auto picture review controller for review time management | `shooting/BMCaptureProcess.java` |

### CameraEx.AutoPictureReviewControl

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `new CameraEx.AutoPictureReviewControl()` | None | `AutoPictureReviewControl` | Creates review control instance | `shooting/BMCaptureProcess.java` |
| `control.getPictureReviewTime()` | None | `int` | Gets current auto review time (ms) | `shooting/BMCaptureProcess.java` |
| `control.setPictureReviewTime(int)` | Time in ms (0=off) | `void` | Sets auto review time - set to 0 during flash bracket to avoid delays | `shooting/BMCaptureProcess.java` |

### CameraEx.ParametersModifier

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `modifier.isAutoWhiteBalanceLockSupported()` | None | `boolean` | Checks if AWB lock is available | `shooting/BMCaptureProcess.java` |
| `modifier.setAutoWhiteBalanceLock(boolean)` | `true`/`false` | `void` | Locks/unlocks AWB across bracket shots for consistent white balance | `shooting/BMCaptureProcess.java` |
| `modifier.isSupportedLongExposureNR()` | None | `boolean` | Checks if long exposure noise reduction is supported | `shooting/BMCaptureProcess.java` |
| `modifier.getLongExposureNR()` | None | `boolean` | Gets long exposure NR state (affects delay time calculation) | `shooting/BMCaptureProcess.java` |
| `modifier.getRemoteControlMode()` | None | `boolean` | Gets whether remote control mode is active | `shooting/BMCaptureProcess.java` |

### CameraEx.ShutterListener

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `onShutter(int status, CameraEx cameraEx)` | Status (0=OK, 1=canceled), CameraEx | `void` | Callback when shutter fires - triggers next bracket shot | `shooting/NormalBurstExecutor.java` |

### com.sony.scalar.hardware.CameraSetting (via CameraSetting wrapper)

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `CameraSetting.getInstance().getShutterSpeed()` | None | `Pair<Integer,Integer>` | Gets current shutter speed as numerator/denominator | `shooting/BMCaptureProcess.java` |
| `CameraSetting.getInstance().getSupportedParameters()` | None | `Pair<Parameters, ParametersModifier>` | Gets supported camera parameters | `shooting/BMCaptureProcess.java` |
| `CameraSetting.getInstance().getEmptyParameters()` | None | `Pair<Parameters, ParametersModifier>` | Gets empty parameter set for modifications | `shooting/BMCaptureProcess.java` |
| `CameraSetting.getInstance().setParameters(Pair)` | Parameters pair | `void` | Applies parameter changes to camera | `shooting/BMCaptureProcess.java` |
| `CameraSetting.getInstance().getParameters()` | None | `Pair<Parameters, ParametersModifier>` | Gets current camera parameters | `shooting/BMCaptureProcess.java` |
| `CameraSetting.getInstance().getFlashExternalEnable()` | None | `boolean` | Checks if external flash is connected | `shooting/BMCaptureProcess.java` |
| `CameraSetting.getInstance().getFlashInternalEnable()` | None | `boolean` | Checks if internal flash is available | `shooting/BMCaptureProcess.java` |
| `CameraSetting.getPfApiVersion()` | None | `int` | Gets platform firmware API version | `shooting/BMCaptureProcess.java` |
| `CameraSetting.registController(String, Class)` | Controller name, class | `void` | Registers custom controller override (BMFlashController) | `BracketMasterMain.java` |

### com.sony.scalar.sysutil.ScalarInput

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `ScalarInput.getKeyStatus(int)` | Key code (e.g., `WATER_HOUSING`) | `KeyStatus` | Gets physical key/housing status for underwater housing detection | `BracketMasterMain.java` |

### com.sony.scalar.sysutil.ScalarProperties

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `ScalarProperties.getString("device.memory")` | Property key | `String` | Gets device memory model identifier (e.g., "ZNS") for timing adjustments | `shooting/BMCaptureProcess.java` |
| `ScalarProperties.getString("model.category")` | Property key | `String` | Gets device model category for focus bracket delay calculation | `shooting/BMCaptureProcess.java` |

### AELController (com.sony.scalar.hardware.CameraEx indirect)

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `AELController.getInstance().holdAELock(boolean)` | `true`/`false` | `void` | Locks/unlocks auto exposure for focus bracket mode | `shooting/BMCaptureProcess.java` |

## 4. Image Processing Pipeline

1. **preTakePicture()**: Initializes bracket mode - locks AWB (non-flash modes), saves/disables auto review (flash mode), initializes step calculations for aperture/shutter/focus bracket
2. **takePicture()**: Calls `cancelTakePicture()` then `burstableTakePicture()` to start the 3-shot sequence. For flash bracket, disables flash before first shot
3. **onShutter(status=0) - Shot 1**: Routes to bracket-specific handler:
   - **Aperture**: Adjusts aperture by `-stepsForSecondPhoto`, triggers delayed `burstableTakePicture()`
   - **Shutter Speed**: Adjusts shutter by `shutterSpeedThirdShootingSteps`, triggers delayed capture
   - **Focus**: Shifts focus by `mPictureRangeSteps`, immediately calls `burstableTakePicture()`
   - **Flash**: Enables flash via `FlashRunnable`, waits for charge + shutter speed match, then captures
4. **onShutter(status=0) - Shot 2**: Adjusts parameters in opposite direction for the third shot
5. **onShutter(status=0) - Shot 3**: Reverts all parameters to original values, unlocks AWB/AE, calls `enableNextCapture()`
6. **onShutter(status!=0)**: Error/cancel - reverts parameters, enables iris ring, calls `enableNextCapture()`

## 5. Button Handling

| Key Constant | Action |
|-------------|--------|
| `ISV_KEY_S1_1` | Half-press shutter - enters EE state, starts focus |
| `ISV_KEY_S2` | Full shutter press - starts bracket sequence |
| `ISV_KEY_MENU` / `ISV_KEY_SK1` | Opens menu for bracket type/range selection |
| `ISV_KEY_DELETE` / `ISV_KEY_SK2` | Trash/Back button |
| `ISV_KEY_PLAY` | Switch to playback mode |
| `ISV_DIAL_1` / `ISV_DIAL_2` | Upper/lower dials for parameter adjustment |
| `ISV_KEY_MODE_DIAL` | Mode dial detection |

## 6. Unique APIs

| API | Description |
|-----|-------------|
| `CameraEx.adjustAperture(int steps)` | Relative aperture adjustment in discrete steps - unique to bracket/filter apps |
| `CameraEx.adjustShutterSpeed(int steps)` | Relative shutter speed adjustment in discrete steps |
| `CameraEx.shiftFocusPosition(int steps)` | Relative focus position shift for focus bracketing |
| `CameraEx.AutoPictureReviewControl` | Controls auto-review delay between burst shots - disabled during bracket to speed up sequence |
| `ScalarProperties.getString("device.memory")` | Device memory model detection for timing workarounds (e.g., ZNS model gets fixed 1200ms delay) |
| `ScalarProperties.getString("model.category")` | Device category for focus bracket delay calculation |
| `CameraSetting.registController()` | Registers custom controller class to override default behavior (flash control) |
| `BracketMasterUtil.isIRISRingEnabledDevice()` | Detects lens iris ring for aperture bracket compatibility |
| `BracketMasterUtil.disableIrisRing()` / `enableIrisRing()` | Disables/enables lens iris ring during aperture bracket |
