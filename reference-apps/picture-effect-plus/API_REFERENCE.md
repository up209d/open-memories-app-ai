# Picture Effect Plus - API Reference

## 1. App Overview

| Field | Value |
|-------|-------|
| **Package** | `com.sony.imaging.app.pictureeffectplus` |
| **Purpose** | Creative effects applied during capture |
| **Description** | Extends the camera's built-in picture effects with enhanced modes: Part Color Plus (color select with 2 channels), Miniature Plus (tilt-shift with color toning), and Toy Camera Plus. Uses CameraSequence with DefaultDevelopFilter for raw-to-processed pipeline, MiniatureImageFilter for tilt-shift blur, and the color select hardware API for partial color extraction. |

## 2. Architecture

### Main Activity
- Entry point managed via `BaseApp` framework
- State machine driven by `com.sony.imaging.app.fw.State` subclasses

### Key Classes

| Class | Path | Role |
|-------|------|------|
| `EffectProcess` | `shooting/EffectProcess.java` | Combined IImagingProcess + ICaptureProcess - handles raw develop and miniature filter chain |
| `SingleProcess` | `shooting/SingleProcess.java` | Simple capture process - uses `burstableTakePicture()` and `startSelfTimerShutter()` |
| `PictureEffectPlusForceSettingState` | `shooting/PictureEffectPlusForceSettingState.java` | Forces picture effect settings based on selected mode |
| `PictureEffectPlusController` | `shooting/camera/PictureEffectPlusController.java` | Core controller - manages color select mode, miniature areas, and effect options |
| `PictureEffectPlusExecutorCreator` | `shooting/PictureEffectPlusExecutorCreator.java` | Creates appropriate executor (SpinalExecutor or NormalExecutor) based on effect mode |
| `SpinalExecutor` | `base/shooting/camera/executor/SpinalExecutor.java` | Uses `startDirectShutter()` / `stopDirectShutter()` for direct capture mode |

## 3. Sony Scalar API Usage

### com.sony.scalar.hardware.CameraEx - Color Select API

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `camera.setColorSelectToChannel(int, SelectedColor)` | Channel (0 or 1), color | `void` | Assigns a captured color to one of 2 color selection channels for partial color effect | `shooting/camera/PictureEffectPlusController.java` |
| `camera.getPreviewDisplayColor(int, int)` | x, y screen coordinates | `CameraEx.SelectedColor` | Samples the color at a point in the live preview display for color picking | `shooting/camera/PictureEffectPlusController.java` |
| `camera.startDirectShutter()` | None | `void` | Starts direct shutter mode for immediate capture without normal shutter sequence | `base/shooting/camera/executor/SpinalExecutor.java` |
| `camera.stopDirectShutter(Callback)` | Stop callback | `void` | Stops direct shutter mode | `base/shooting/camera/executor/SpinalExecutor.java` |
| `camera.burstableTakePicture()` | None | `void` | Takes a picture in burst-capable mode | `shooting/SingleProcess.java`, `shooting/EffectProcess.java` |
| `camera.startSelfTimerShutter()` | None | `void` | Starts self-timer shutter sequence | `shooting/SingleProcess.java` |

### CameraEx.ParametersModifier - Effect Settings

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `modifier.setColorSelectMode(String, int)` | Mode (`"extract"`, `"off"`), channel bitmask | `void` | Sets color selection mode: "extract" enables partial color, "off" disables; bitmask selects which channels are active | `shooting/camera/PictureEffectPlusController.java` |
| `modifier.setPictureEffect(String)` | Effect name | `void` | Sets the active picture effect (e.g., `"part_color"`, `"toy_camera"`, `"miniature"`, `"retro_photo"`) | `shooting/PictureEffectPlusForceSettingState.java` |

### CameraEx.SelectedColor

| Usage | Description | Source File |
|-------|-------------|-------------|
| Returned by `getPreviewDisplayColor()` | Encapsulates a sampled color from the live preview, used to configure color select channels | `shooting/camera/PictureEffectPlusController.java` |
| Passed to `setColorSelectToChannel()` | Assigns the picked color to channel 0 or 1 | `shooting/camera/PictureEffectPlusController.java` |

### com.sony.scalar.hardware.CameraSequence

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `CameraSequence.Options.setOption("MEMORY_MAP_FILE", String)` | Path to memory map | `void` | Sets memory map file for the capture sequence | `shooting/EffectProcess.java` |
| `CameraSequence.Options.setOption("EXPOSURE_COUNT", int)` | `1` | `void` | Sets number of exposures per capture | `shooting/EffectProcess.java` |
| `CameraSequence.Options.setOption("RECORD_COUNT", int)` | `1` | `void` | Sets number of images to record per capture | `shooting/EffectProcess.java` |

### CameraSequence.DefaultDevelopFilter

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `new CameraSequence.DefaultDevelopFilter()` | None | `DefaultDevelopFilter` | Creates the standard raw-to-processed image development filter | `shooting/EffectProcess.java` |
| `filter.isSupported()` | None | `boolean` | Checks if DefaultDevelopFilter is available on this hardware | `shooting/EffectProcess.java` |
| `filter.setSource(RawData, boolean)` | Raw data, release flag | `void` | Sets raw sensor data as input | `shooting/EffectProcess.java` |
| `filter.getNumberOfSources()` | None | `int` | Returns number of configured source inputs (expect 1) | `shooting/EffectProcess.java` |
| `filter.getNumberOfOutputs()` | None | `int` | Returns number of available outputs (expect 0 before execute) | `shooting/EffectProcess.java` |
| `filter.execute()` | None | `boolean` | Develops raw data with active picture effect applied | `shooting/EffectProcess.java` |
| `filter.getOutput()` | None | `OptimizedImage` | Gets the developed image with effects | `shooting/EffectProcess.java` |
| `filter.release()` | None | `void` | Releases the filter | `shooting/EffectProcess.java` |

### com.sony.scalar.graphics.imagefilter.MiniatureImageFilter

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `new MiniatureImageFilter()` | None | `MiniatureImageFilter` | Creates a tilt-shift blur filter for miniature/diorama effect | `shooting/EffectProcess.java` |
| `filter.setMiniatureArea(int)` | Area code (1-6) | `void` | Sets the focus area for tilt-shift: 1=H-Center, 2=V-Center, 3=Left, 4=Right, 5=Upper, 6=Lower | `shooting/EffectProcess.java` |
| `filter.setSource(OptimizedImage, boolean)` | Image, release flag | `void` | Sets the developed image as input | `shooting/EffectProcess.java` |
| `filter.execute()` | None | `boolean` | Applies the miniature blur effect | `shooting/EffectProcess.java` |
| `filter.getOutput()` | None | `OptimizedImage` | Gets the miniaturized result | `shooting/EffectProcess.java` |
| `filter.release()` | None | `void` | Releases the filter | `shooting/EffectProcess.java` |

### Miniature Area Codes

| Constant | Value | Description |
|----------|-------|-------------|
| `MINIATURE_HCENTER` | 1 | Horizontal center strip in focus |
| `MINIATURE_VCENTER` | 2 | Vertical center strip in focus |
| `MINIATURE_LEFT` | 3 | Left side in focus |
| `MINIATURE_RIGHT` | 4 | Right side in focus |
| `MINIATURE_UPPER` | 5 | Upper region in focus |
| `MINIATURE_LOWER` | 6 | Lower region in focus |

### com.sony.scalar.sysutil.ScalarProperties

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `ScalarProperties.getString("device.memory")` | Key | `String` | Gets device memory info for sequence configuration | `shooting/EffectProcess.java` |
| `ScalarProperties.getString("mem.rawimage.size.in.mega.pixel")` | Key | `String` | Gets raw image megapixel count for memory allocation | `shooting/EffectProcess.java` |

### CameraSequence (capture flow)

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `sequence.storeImage(OptimizedImage, boolean)` | Image, release flag | `void` | Stores the final processed image to media | `shooting/EffectProcess.java` |

## 4. Image Processing Pipeline

### Part Color Plus Mode
1. **Color Selection** - `getPreviewDisplayColor(x, y)` samples color from live preview at user-specified point
2. **Channel Assignment** - `setColorSelectToChannel(0, color)` and `setColorSelectToChannel(1, color)` assign up to 2 color channels
3. **Mode Activation** - `setColorSelectMode("extract", channelBitmask)` enables partial color extraction in hardware
4. **Capture** - Standard capture via `burstableTakePicture()` - hardware applies partial color during image processing
5. **Storage** - Direct store, as the effect is baked in at the hardware level

### Miniature Plus Mode
1. **Force Settings** - `PictureEffectPlusForceSettingState` sets picture effect to `"miniature"`, `"toy_camera"`, or `"retro_photo"` based on sub-option
2. **Capture Configuration** - `EffectProcess.prepare()` sets `MEMORY_MAP_FILE`, `EXPOSURE_COUNT=1`, `RECORD_COUNT=1`
3. **Raw Capture** - `burstableTakePicture()` captures raw data
4. **Raw Development** - `DefaultDevelopFilter.setSource(raw)` -> `execute()` -> `getOutput()` produces developed image with active picture effect
5. **Miniature Filter** - `MiniatureImageFilter.setMiniatureArea(area)` -> `setSource(img)` -> `execute()` -> `getOutput()` applies tilt-shift blur
6. **Storage** - `sequence.storeImage(img, true)` saves the final composited image

### Toy Camera Plus Mode
1. **Force Settings** - Sets `setPictureEffect("toy_camera")` with color variant (cool/warm/normal/green/magenta)
2. **Direct Shutter** - Uses `SpinalExecutor` with `startDirectShutter()` / `stopDirectShutter()` for immediate capture
3. **Hardware Processing** - Toy camera effect is applied at the hardware level during capture

## 5. Button Handling

Button handling is managed by the shared `BaseApp` framework via `ScalarInput` key events:

| Key Constant | Action |
|-------------|--------|
| `ISV_KEY_S1_1` | Half-press shutter - autofocus |
| `ISV_KEY_S2` | Full shutter press - capture with active effect |
| `ISV_KEY_UP/DOWN` | Navigate effect options / adjust parameters |
| `ISV_KEY_LEFT/RIGHT` | Switch between effect modes or color channels |
| `ISV_KEY_ENTER` | Confirm color pick / accept parameter |
| `ISV_KEY_DELETE` | Cancel / back |
| `ISV_KEY_MENU` | Open effect selection menu |
| `ISV_KEY_FN` | Quick access to effect parameters |
| `ISV_DIAL_1` | Adjust color hue for Part Color |
| `ISV_DIAL_2` | Adjust miniature focus position |

## 6. Unique APIs

### APIs unique to picture-effect-plus (not commonly used by other reference apps):

| API | Description |
|-----|-------------|
| **`CameraEx.setColorSelectToChannel(int, SelectedColor)`** | Hardware color channel assignment for partial color extraction - 2 independent color channels |
| **`CameraEx.getPreviewDisplayColor(int, int)`** | Samples color from live preview at screen coordinates for color picking |
| **`ParametersModifier.setColorSelectMode(String, int)`** | Enables/disables color select hardware with mode (`"extract"`, `"off"`) and channel bitmask |
| **`CameraEx.SelectedColor`** | Data type representing a sampled color from the preview display |
| **`MiniatureImageFilter`** | Tilt-shift blur filter with 6 configurable focus areas |
| **`MiniatureImageFilter.setMiniatureArea(int)`** | 6 focus positions: H-Center, V-Center, Left, Right, Upper, Lower |
| **`CameraSequence.DefaultDevelopFilter`** | Raw-to-processed development filter used in the capture pipeline (also used by live-view-grading) |
| **`DefaultDevelopFilter.isSupported()`** | Hardware capability check for develop filter availability |
| **`CameraEx.startDirectShutter()` / `stopDirectShutter()`** | Direct shutter mode bypassing normal capture sequence for immediate shooting |
| **`CameraSequence.Options` with EXPOSURE_COUNT/RECORD_COUNT** | Fine control over the number of exposures and recorded images per capture sequence |
