# Live View Grading - API Reference

## 1. App Overview

| Field | Value |
|-------|-------|
| **Package** | `com.sony.imaging.app.liveviewgrading` |
| **Purpose** | Real-time color grading via gamma tables and RGB matrix |
| **Description** | Applies real-time color grading effects to the live view and captured images using extended gamma tables (loaded from CSV asset files), RGB color matrices, 6-channel color depth control, and white balance shift. Includes 12 presets across three categories: Standard (4), Cinema (4), and Extreme (4). |

## 2. Architecture

### Main Activity
- Entry point managed via `BaseApp` framework
- State machine driven by `com.sony.imaging.app.fw.State` subclasses

### Key Classes

| Class | Path | Role |
|-------|------|------|
| `EffectProcess` | `shooting/EffectProcess.java` | Capture pipeline - uses DefaultDevelopFilter to process raw data with active grading |
| `LVGEffectValueController` | `shooting/LVGEffectValueController.java` | Core controller - applies gamma tables, RGB matrix, color depth, and parameter values |
| `LVGParameterValueController` | `menu/controller/LVGParameterValueController.java` | Manages preset selection and user-adjustable parameter values |
| `ColorGradingConstants` | `ColorGradingConstants.java` | Defines all 12 preset configurations: RGB matrices, fixed values, CSV filenames |
| `ColorGradingController` | `menu/controller/ColorGradingController.java` | Menu controller for preset selection UI |
| `LVGForceSettingState` | `shooting/LVGForceSettingState.java` | Forces picture effect and camera settings before shooting |

## 3. Sony Scalar API Usage

### com.sony.scalar.hardware.CameraEx - Extended Gamma Table API

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `camera.createGammaTable()` | None | `CameraEx.GammaTable` | Creates a new gamma table object for custom tone curve | `shooting/LVGEffectValueController.java` |
| `camera.setExtendedGammaTable(GammaTable)` | GammaTable or null | `void` | Applies custom gamma table to live view and capture pipeline; pass null to clear | `shooting/LVGEffectValueController.java` |

### CameraEx.GammaTable

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `table.setPictureEffectGammaForceOff(boolean)` | `true` | `void` | Disables the camera's built-in picture effect gamma so custom gamma takes full control | `shooting/LVGEffectValueController.java` |
| `table.write(InputStream)` | Byte stream of 1024 short values | `void` | Writes the gamma curve data (1024 entries, each a 16-bit value) to the table | `shooting/LVGEffectValueController.java` |
| `table.release()` | None | `void` | Releases the gamma table after applying | `shooting/LVGEffectValueController.java` |

### CameraEx.ParametersModifier - Color Grading Parameters

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `modifier.setContrast(int)` | Value (adjustable range) | `void` | Sets contrast level for color grade | `shooting/LVGEffectValueController.java` |
| `modifier.setSaturation(int)` | Value | `void` | Sets saturation level for color grade | `shooting/LVGEffectValueController.java` |
| `modifier.isSharpnessGainModeSupported()` | None | `boolean` | Checks if sharpness gain mode is available | `shooting/LVGEffectValueController.java` |
| `modifier.setSharpnessGainMode(boolean)` | `true` | `void` | Enables sharpness gain mode for fine control | `shooting/LVGEffectValueController.java` |
| `modifier.setSharpnessGain(int)` | Value | `void` | Sets sharpness gain level | `shooting/LVGEffectValueController.java` |
| `modifier.isWhiteBalanceShiftModeSupported()` | None | `boolean` | Checks if WB shift is available | `shooting/LVGEffectValueController.java` |
| `modifier.setWhiteBalanceShiftMode(boolean)` | `true` | `void` | Enables white balance shift for color temperature/tint control | `shooting/LVGEffectValueController.java` |
| `modifier.setWhiteBalanceShiftLB(int)` | Value (inverted) | `void` | Sets white balance shift on amber-blue axis (color temp) | `shooting/LVGEffectValueController.java` |
| `modifier.setWhiteBalanceShiftCC(int)` | Value (inverted) | `void` | Sets white balance shift on green-magenta axis (color tint) | `shooting/LVGEffectValueController.java` |
| `modifier.getSupportedColorDepthTypes()` | None | `List<String>` | Returns available color depth channels (red, green, blue, cyan, magenta, yellow) | `shooting/LVGEffectValueController.java` |
| `modifier.setColorDepth(String, int)` | Channel name + value | `void` | Sets per-channel color depth. Channels: `"red"`, `"green"`, `"blue"`, `"cyan"`, `"magenta"`, `"yellow"` | `shooting/LVGEffectValueController.java` |
| `modifier.isRGBMatrixSupported()` | None | `boolean` | Checks if RGB color matrix transformation is available | `shooting/LVGEffectValueController.java` |
| `modifier.setRGBMatrix(int[])` | 3x3 matrix as flat array | `void` | Applies a 3x3 RGB color transformation matrix | `shooting/LVGEffectValueController.java` |
| `modifier.isExtendedGammaTableSupported()` | None | `boolean` | Checks if custom gamma table API is available | `shooting/LVGEffectValueController.java` |
| `modifier.isPictureControlExposureShiftSupported()` | None | `boolean` | Checks if exposure shift in picture control is available | `shooting/LVGEffectValueController.java` |
| `modifier.setPictureControlExposureShift(int)` | Shift value | `void` | Adjusts exposure brightness as part of color grade | `shooting/LVGEffectValueController.java` |
| `modifier.setPictureEffect(String)` | Effect name | `void` | Sets picture effect mode (e.g., `"toy_camera"` for vignette shading) | `shooting/LVGEffectValueController.java` |

### com.sony.scalar.hardware.CameraSequence

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `CameraSequence.Options.setOption("MEMORY_MAP_FILE", String)` | Path to memory map `.so` | `void` | Sets memory map for single-capture sequence | `shooting/EffectProcess.java` |

### CameraSequence.DefaultDevelopFilter

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `filter.setSource(RawData, boolean)` | Raw data, release flag | `void` | Sets raw sensor data as input to develop filter | `shooting/EffectProcess.java` |
| `filter.execute()` | None | `boolean` | Develops raw data to OptimizedImage with active color grading applied | `shooting/EffectProcess.java` |
| `filter.getOutput()` | None | `OptimizedImage` | Gets the developed image | `shooting/EffectProcess.java` |
| `filter.release()` | None | `void` | Releases the filter | `shooting/EffectProcess.java` |

### CameraSequence (capture flow)

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `sequence.storeImage(OptimizedImage, boolean)` | Image, release flag | `void` | Stores the graded image to media storage | `shooting/EffectProcess.java` |

## 4. Image Processing Pipeline

1. **Preset Selection** - User selects one of 12 color grading presets from the menu
2. **Parameter Application** - `LVGEffectValueController.setParamValues()` applies 8 adjustable parameters to camera:
   - Contrast, Saturation, Sharpness (via sharpness gain mode)
   - Color Temperature, Color Tint (via white balance shift)
   - Color Depth R, G, B (per-channel)
3. **Fixed Value Application** - `setFixedValueParameters()` applies preset-specific fixed values:
   - Color Depth for Cyan, Magenta, Yellow channels
   - Exposure shift
   - Shading effect (uses toy camera picture effect for vignette)
4. **RGB Matrix** - `setRGBMatrix()` applies a 3x3 color transformation matrix specific to each preset
5. **Gamma Table** - `applyGammaTable()` reads a CSV file from assets (1024 short values), creates a `GammaTable`, and applies it via `setExtendedGammaTable()`
6. **Live Preview** - All settings are applied to the camera's image processing pipeline, so the live view shows the graded look in real-time
7. **Capture** - `EffectProcess.onShutterSequence()` receives raw data, applies `DefaultDevelopFilter` (which includes active grading), and stores the result
8. **Cleanup** - `clearGammaTable()` passes null to `setExtendedGammaTable()` to restore default tone curve

### 12 Presets

| Category | Preset | CSV File | Has RGB Matrix | Has Shading |
|----------|--------|----------|----------------|-------------|
| Standard | Clear | `std_clear.csv` | Yes | No |
| Standard | Vivid | `std_vivid.csv` | Yes | No |
| Standard | Monochrome | `std_monochrome.csv` | Yes | No |
| Standard | Bold | `std_bold.csv` | Yes | No |
| Cinema | Coast | `cin_coast_side_light.csv` | Yes | Varies |
| Cinema | Silky | `cin_silky.csv` | Yes | Varies |
| Cinema | Misty | `cin_misty_blue.csv` | Yes | Varies |
| Cinema | Velvety | `cin_velvety_dew.csv` | Yes | Varies |
| Extreme | 180 | `ex_180.csv` | Yes | Varies |
| Extreme | Surf | `ex_surf_trip.csv` | Yes | Varies |
| Extreme | Big Air | `ex_big_air.csv` | Yes | Varies |
| Extreme | Snow | `ex_snow_tricks.csv` | Yes | Varies |

## 5. Button Handling

Button handling is managed by the shared `BaseApp` framework via `ScalarInput` key events:

| Key Constant | Action |
|-------------|--------|
| `ISV_KEY_S1_1` | Half-press shutter - autofocus |
| `ISV_KEY_S2` | Full shutter press - capture with active color grade |
| `ISV_KEY_UP/DOWN` | Navigate preset list / adjust parameter value |
| `ISV_KEY_LEFT/RIGHT` | Switch between parameter adjustment screens |
| `ISV_KEY_ENTER` | Confirm preset / parameter selection |
| `ISV_KEY_DELETE` | Back / cancel |
| `ISV_KEY_MENU` | Open preset selection menu |
| `ISV_DIAL_1` | Scroll through presets |
| `ISV_DIAL_2` | Fine-adjust current parameter |

## 6. Unique APIs

### APIs unique to live-view-grading (not commonly used by other reference apps):

| API | Description |
|-----|-------------|
| **`CameraEx.createGammaTable()`** | Creates custom gamma table objects - only this app and smooth-reflection use this API |
| **`GammaTable.write(InputStream)`** | Writes 1024 short values from CSV files to define custom tone curves |
| **`GammaTable.setPictureEffectGammaForceOff(true)`** | Overrides camera's built-in picture effect gamma with custom curve |
| **`CameraEx.setExtendedGammaTable(GammaTable)`** | Applies the custom gamma table to the entire image pipeline (live view + capture) |
| **`ParametersModifier.setRGBMatrix(int[])`** | Applies a 3x3 RGB color transformation matrix for color grading |
| **6-channel color depth** (`setColorDepth` for red, green, blue, cyan, magenta, yellow) | Full 6-channel independent color depth control |
| **`setWhiteBalanceShiftLB()` / `setWhiteBalanceShiftCC()`** | Fine white balance shift on amber-blue and green-magenta axes for color temperature/tint |
| **`setSharpnessGainMode()` / `setSharpnessGain()`** | Sharpness gain mode for precise sharpness control beyond the standard sharpness setting |
| **`setPictureControlExposureShift()`** | Exposure brightness adjustment integrated into the color grading pipeline |
| **CSV-based gamma table loading** | Gamma curves stored as CSV assets, parsed at runtime into short arrays |
