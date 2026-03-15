# Manual Lens Compensation - API Reference

## 1. App Overview
| Field | Value |
|-------|-------|
| **Package** | `com.sony.imaging.app.manuallenscompensation` |
| **Purpose** | Manually adjust lens compensation (vignetting, chromatic aberration, distortion) for non-native lenses |
| **Description** | This app allows users to create, save, import/export, and apply lens correction profiles for third-party or manual lenses that lack native compensation data. It adjusts six optical parameters (light vignetting, red/blue color vignetting, red/blue chromatic aberration, and distortion) via CameraEx.ParametersModifier. Profiles are stored in a SQLite database via a custom ContentProvider (LensParameterProvider) and can be exported to/imported from memory card as binary .BIN files. The app also writes custom EXIF data (lens name, focal length, f-number) to captured images. |

## 2. Architecture
### Main Activity
- **OpticalCompensation** (`OpticalCompensation.java`) - Extends BaseApp, entry point for the application. Handles boot flow, menu initialization, and profile restoration.

### Key Classes
| Class | Path | Role |
|-------|------|------|
| OpticalCompensation | `.../manuallenscompensation/OpticalCompensation.java` | Main activity, boot logic, app lifecycle |
| OCController | `.../shooting/controller/OCController.java` | Core lens correction controller - get/set correction levels via CameraEx.ParametersModifier |
| LensParameterProvider | `.../database/LensParameterProvider.java` | SQLite ContentProvider for lens profiles (CRUD) |
| LensCompensationParameter | `.../database/LensCompensationParameter.java` | Parcelable data model for 6 correction parameters |
| LensParameterProviderDefinition | `.../database/LensParameterProviderDefinition.java` | ContentProvider URI and column definitions |
| OCUtil | `.../commonUtil/OCUtil.java` | Utility: EXIF writing, profile management, media card operations |
| SingleProcess | `.../shooting/SingleProcess.java` | Standard single-shot capture process |
| OCAntiHandBlurController | `.../shooting/controller/OCAntiHandBlurController.java` | SteadyShot/anti-handblur management for manual lenses |
| OCFocalLengthController | `.../shooting/controller/OCFocalLengthController.java` | Focal length management for manual lenses |
| ByteDataAnalyser | `.../commonUtil/ByteDataAnalyser.java` | Parses binary .BIN profile files from memory card |

## 3. Sony Scalar API Usage

### com.sony.scalar.hardware.CameraEx
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `burstableTakePicture()` | none | void | Triggers a single photo capture | SingleProcess.java |
| `startSelfTimerShutter()` | none | void | Starts self-timer countdown and capture | SingleProcess.java |
| `getNormalCamera()` | none | Camera | Gets underlying Android Camera object | (via base framework) |
| `setExifInfo(ExifInfo)` | ExifInfo | void | Writes custom EXIF data (lens name, focal length, f-number) to captured images | OCUtil.java |
| `getLensInfo()` | none | CameraEx.LensInfo | Checks if a native lens is attached (null = no native lens) | OCUtil.java |

### com.sony.scalar.hardware.CameraEx.ParametersModifier
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `setLensCorrection(boolean)` | onOff | void | Enables/disables lens correction globally | OCController.java |
| `setLensCorrectionLevel(String, int)` | tag, value | void | Sets correction level for a specific parameter tag | OCController.java |
| `getLensCorrectionLevel(String)` | tag | int | Gets current correction level for a parameter tag | OCController.java |
| `getSupportedLensCorrections()` | none | List\<String\> | Returns list of supported correction tags | OCController.java |
| `getMinLensCorrectionLevel(String)` | tag | int | Gets minimum allowed correction level | OCController.java |
| `getMaxLensCorrectionLevel(String)` | tag | int | Gets maximum allowed correction level | OCController.java |
| `setShootingPreviewMode(String)` | mode | void | Sets preview mode ("off", "iris_ss_iso_aeunlock") | OCUtil.java |
| `isSupportedExifInfo()` | none | boolean | Checks if custom EXIF writing is supported | OCUtil.java |

### com.sony.scalar.hardware.CameraEx.ExifInfo
| Field | Type | Description | Source File |
|-------|------|-------------|-------------|
| `writeMode` | boolean | Enable/disable custom EXIF writing | OCUtil.java |
| `lensName` | String | Lens name string (null-terminated ASCII) | OCUtil.java |
| `focalLengthNumer` | int | Focal length numerator (focal_mm * 10) | OCUtil.java |
| `focalLengthDenom` | int | Focal length denominator (always 10) | OCUtil.java |
| `fNumberNumer` | int | F-number numerator (f * 100) | OCUtil.java |
| `fNumberDenom` | int | F-number denominator (always 100) | OCUtil.java |
| `fNumberMinNumer` | int | Minimum f-number numerator | OCUtil.java |
| `fNumberMinDenom` | int | Minimum f-number denominator (always 1000) | OCUtil.java |

### com.sony.scalar.hardware.CameraEx.ShutterSpeedInfo
| Field | Type | Description | Source File |
|-------|------|-------------|-------------|
| `currentShutterSpeed_n` | float | Shutter speed numerator | (via base framework) |
| `currentShutterSpeed_d` | float | Shutter speed denominator | (via base framework) |

### com.sony.scalar.sysutil.ScalarInput
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `getKeyStatus(int)` | keyCode | KeyStatus | Gets physical key/housing status (used for water housing detection) | OpticalCompensation.java |

### com.sony.scalar.sysutil.ScalarProperties
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `getString(String)` | property | String | Gets system property (e.g., "version.platform") | OCUtil.java, OCAntiHandBlurController.java |

### com.sony.scalar.sysutil.didep.Kikilog
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `setUserLog(int, Options)` | kikilogId, options | void | Writes analytics/telemetry log entry | OpticalCompensation.java |

### com.sony.scalar.sysutil.didep.Settings
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| (used via import) | - | - | Display/grid line settings | OCGridLine.java |

### com.sony.scalar.provider.AvindexStore
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `getExternalMediaIds()` | none | String[] | Gets mounted media card IDs | OCUtil.java |
| `getMediaInfo(String)` | mediaId | MediaInfo | Gets media info (type: SD=1, MS=2) | OCUtil.java |

### com.sony.scalar.media.MediaInfo
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `getMediaType()` | none | int | Returns media type (1=SD, 2=MemoryStick) | OCUtil.java |

## 4. Image Processing Pipeline
1. User creates or selects a lens compensation profile (LensCompensationParameter) from the database or imports from .BIN file on memory card
2. Profile contains 6 correction parameters: light vignetting (shading-w), red color vignetting (shading-cr), blue color vignetting (shading-cb), red chromatic aberration (chroma-r), blue chromatic aberration (chroma-b), distortion (distotion)
3. `LensCompensationParameter.applyCompensationParameter()` is called, which invokes `OCController.setLensCorrectionLevel()` for each parameter
4. Each call goes through `CameraEx.ParametersModifier.setLensCorrectionLevel(tag, value)` to apply the correction in the camera hardware pipeline
5. Before capture, `OCUtil.setExifData()` writes lens name, focal length, and f-number into the image EXIF via `CameraEx.setExifInfo()`
6. Standard `SingleProcess.takePicture()` triggers `CameraEx.burstableTakePicture()` - the camera hardware applies the correction during image development
7. On app exit, `OCUtil.resetExifDataOff()` disables custom EXIF writing

## 5. Button Handling
| Key Constant | Action |
|-------------|--------|
| `ISV_KEY_S2` (full shutter) | Triggers photo capture via SingleProcess |
| `ISV_KEY_S1_1` (half-press) | Autofocus / MF assist activation |
| `ISV_KEY_ENTER` | Confirm selection in menus |
| `ISV_KEY_UP/DOWN/LEFT/RIGHT` | Navigate lens adjustment menus, change parameter values |
| `ISV_KEY_MENU` | Open/close menu |
| `ISV_KEY_DELETE` | Back/cancel, delete profile |
| `ISV_KEY_FN` | Open focus magnification (MF assist) |
| `ISV_KEY_PLAY` | Switch to playback mode |
| `WATER_HOUSING` | Housing detection via ScalarInput.getKeyStatus() |

## 6. Unique APIs
| API | Description |
|-----|-------------|
| `CameraEx.ParametersModifier.setLensCorrection(boolean)` | Global enable/disable of lens correction - unique to this app |
| `CameraEx.ParametersModifier.setLensCorrectionLevel(String, int)` | Per-parameter correction level setting with tags: "shading-w", "shading-cr", "shading-cb", "chroma-r", "chroma-b", "distotion" |
| `CameraEx.ParametersModifier.getSupportedLensCorrections()` | Enumerates which correction types the camera model supports |
| `CameraEx.ParametersModifier.getMinLensCorrectionLevel(String)` / `getMaxLensCorrectionLevel(String)` | Range queries for correction levels |
| `CameraEx.setExifInfo(ExifInfo)` | Custom EXIF metadata injection for manual lens name, focal length, and f-number |
| `CameraEx.LensInfo` (via `getLensInfo()`) | Null check to detect if a native lens is attached vs manual/adapted lens |
| `LensParameterProvider` (ContentProvider) | Custom SQLite database at `/data/data/lensparameter.db` storing up to 200 lens profiles with binary import/export to memory card |
