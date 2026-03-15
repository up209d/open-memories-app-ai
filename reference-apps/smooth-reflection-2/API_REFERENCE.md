# Smooth Reflection 2 - API Reference

## 1. App Overview
| Field | Value |
|-------|-------|
| **Package** | `com.sony.imaging.app.smoothreflection` |
| **Purpose** | Multi-frame raw averaging v2 with model-specific offset handling for long exposure noise reduction |
| **Description** | Smooth Reflection 2 captures multiple raw frames and averages them using a custom DSP program to produce long-exposure effects (smooth water, light trails, twilight reflections). This v2 release adds model-specific raw data offset correction for cameras with BFNR (Bayer Frame Noise Reduction) including DSC-HX400V, DSC-HX60V, DSC-RX100M3, and ILCE-5000. It also adds AVIP (Application Virtual Image Processor) support for NEX-5R/5T/6 models with direct memory address manipulation. The app supports configurable "themes" (Twilight Reflection, Custom) with user-selectable shot counts. |

## 2. Architecture
### Main Activity
- **SmoothReflectionApp** (`SmoothReflectionApp.java`) - Extends BaseApp, handles boot, firmware version checking, and theme selection.

### Key Classes
| Class | Path | Role |
|-------|------|------|
| SmoothReflectionApp | `.../smoothreflection/SmoothReflectionApp.java` | Main activity, firmware check, boot flow |
| SmoothReflectionCompositProcess | `.../shooting/SmoothReflectionCompositProcess.java` | Core multi-frame capture + DSP compositing pipeline |
| SaRawComp | `.../shooting/SaRawComp.java` | DSP raw averaging engine - creates DSP, manages buffers, executes SA programs |
| SaUtil | `.../common/SaUtil.java` | DSP utility: memory address conversion, parameter buffers, SA execution |
| SmoothReflectionUtil | `.../common/SmoothReflectionUtil.java` | App utilities: theme management, camera settings, gamma tables |
| ThemeController | `.../shooting/camera/ThemeController.java` | Theme selection controller (Twilight Reflection, Custom) |
| MemoryUtil | `.../common/MemoryUtil.java` | Memory management utilities |
| Factory | `.../Factory.java` | Factory class for app component creation |

## 3. Sony Scalar API Usage

### com.sony.scalar.hardware.CameraEx
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `getNormalCamera()` | none | Camera | Gets underlying Android Camera object for parameter access | SmoothReflectionCompositProcess.java |
| `burstableTakePicture()` | none | void | Triggers each frame capture in the multi-shot sequence | SmoothReflectionCompositProcess.java |
| `startSelfTimerShutter()` | none | void | Starts self-timer triggered multi-shot sequence | SmoothReflectionCompositProcess.java |
| `setAutoPictureReviewControl(AutoPictureReviewControl)` | control | void | Configures auto picture review behavior | SmoothReflectionCompositProcess.java |

### com.sony.scalar.hardware.CameraEx.ParametersModifier
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `getLongExposureNR()` | none | boolean | Checks if long exposure noise reduction is enabled (affects raw offset) | SaRawComp.java |
| `getRemoteControlMode()` | none | boolean | Checks if camera is in remote control mode | SmoothReflectionCompositProcess.java |

### com.sony.scalar.hardware.CameraEx.AutoPictureReviewControl
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `getPictureReviewTime()` | none | int | Gets auto review time setting | SmoothReflectionCompositProcess.java |
| `setPictureReviewTime(int)` | time | void | Sets auto review time (0=off during capture, restored after) | SmoothReflectionCompositProcess.java |

### com.sony.scalar.hardware.CameraEx.ShutterSpeedInfo
| Field | Type | Description | Source File |
|-------|------|-------------|-------------|
| `currentShutterSpeed_n` | float | Shutter speed numerator | SaRawComp.java |
| `currentShutterSpeed_d` | float | Shutter speed denominator | SaRawComp.java |

### com.sony.scalar.hardware.CameraSequence
| Method/Class | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `CameraSequence.Options()` | none | Options | Creates sequence options for multi-frame capture | SmoothReflectionCompositProcess.java |
| `Options.setOption("MEMORY_MAP_FILE", path)` | key, value | void | Sets memory map file for special capture sequence | SmoothReflectionCompositProcess.java |
| `Options.setOption("EXPOSURE_COUNT", 1)` | key, value | void | Sets exposures per sequence step | SmoothReflectionCompositProcess.java |
| `Options.setOption("RECORD_COUNT", 1)` | key, value | void | Sets number of images to record | SmoothReflectionCompositProcess.java |
| `Options.setOption("INTERIM_PRE_REVIEW_ENABLED", true)` | key, value | void | Enables interim preview between shots | SmoothReflectionCompositProcess.java |
| `Options.setOption("DETECTION_OFF", false)` | key, value | void | Controls face/object detection during sequence (non-AVIP only) | SmoothReflectionCompositProcess.java |
| `CameraSequence.DefaultDevelopFilter()` | none | DefaultDevelopFilter | Creates default raw-to-JPEG development filter | SmoothReflectionCompositProcess.java |
| `DefaultDevelopFilter.setSource(RawData, boolean)` | raw, flag | void | Sets raw data source for development | SmoothReflectionCompositProcess.java |
| `DefaultDevelopFilter.execute()` | none | void | Executes raw-to-JPEG development | SmoothReflectionCompositProcess.java |
| `DefaultDevelopFilter.getOutput()` | none | OptimizedImage | Gets developed JPEG output | SmoothReflectionCompositProcess.java |
| `sequence.storeImage(OptimizedImage, boolean)` | img, flag | void | Saves final composited image to media | SmoothReflectionCompositProcess.java |
| `RawData.getRawDataInfo()` | none | RawDataInfo | Gets raw data metadata (canvas size, margins, color info) | SaRawComp.java |
| `RawData.release()` | none | void | Releases raw data buffer | SmoothReflectionCompositProcess.java |

### com.sony.scalar.hardware.CameraSequence.RawDataInfo
| Field | Type | Description | Source File |
|-------|------|-------------|-------------|
| `canvasSizeX` / `canvasSizeY` | int | Raw image dimensions | SaRawComp.java |
| `marginOffsetX` / `marginOffsetY` | int | Margin offset in raw buffer | SaRawComp.java |
| `marginSizeX` / `marginSizeY` | int | Margin size in raw buffer | SaRawComp.java |
| `validOffsetX` / `validOffsetY` | int | Valid image area offset | SaRawComp.java |
| `validSizeX` / `validSizeY` | int | Valid image area size | SaRawComp.java |
| `firstColor` | int | Bayer pattern first color | SaRawComp.java |
| `clpR` / `clpGr` / `clpGb` / `clpB` | int | Clipping values per Bayer channel | SaRawComp.java |
| `clpOfst` | int | Clipping offset | SaRawComp.java |
| `wbR` / `wbB` | int | White balance coefficients | SaRawComp.java |
| `ddithRstmod` / `ddithOn` | int | Decompression dither parameters | SaRawComp.java |
| `expBit` | int | Expansion bit count | SaRawComp.java |
| `decompMode` | int | Decompression mode | SaRawComp.java |
| `dth0-3` / `dp0-3` | int | Decompression threshold/delta params | SaRawComp.java |
| `dithRstmod` / `dithOn` | int | Compression dither parameters | SaRawComp.java |
| `rndBit` | int | Rounding bit count | SaRawComp.java |
| `compMode` | int | Compression mode | SaRawComp.java |
| `th0-3` / `p0-3` | int | Compression threshold/delta params | SaRawComp.java |

### com.sony.scalar.hardware.DSP
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `DSP.createProcessor(String)` | type ("sony-di-dsp") | DSP | Creates a DSP processor instance | SaRawComp.java |
| `setProgram(String)` | path | void | Loads DSP program (`.so` library) | SaRawComp.java |
| `clearProgram()` | none | void | Unloads current DSP program | SaRawComp.java |
| `createBuffer(int)` | size | DeviceBuffer | Allocates DSP-accessible buffer | SaRawComp.java |
| `release()` | none | void | Releases DSP processor | SaRawComp.java |

### com.sony.scalar.hardware.DeviceBuffer
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `release()` | none | void | Releases device buffer memory | SaRawComp.java |

### com.sony.scalar.hardware.DeviceMemory
| Usage | Description | Source File |
|-------|-------------|-------------|
| Base class for RawData, DeviceBuffer | Used in DSP execution device memory array | SaRawComp.java |

### com.sony.scalar.hardware.MemoryMapConfig
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `setAllocationPolicy(int)` | policy (1=VERSION_1) | void | Sets memory allocation policy for PF v2 cameras | SmoothReflectionCompositProcess.java |

### com.sony.scalar.graphics.OptimizedImage
| Usage | Description | Source File |
|-------|-------------|-------------|
| Output of DefaultDevelopFilter | Holds developed JPEG image for storage | SmoothReflectionCompositProcess.java |

### com.sony.scalar.provider.AvindexStore
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| (used via base framework) | - | - | Media storage operations | SmoothReflectionCompositProcess.java |

### com.sony.scalar.sysutil.ScalarProperties
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `getString(String)` | property | String | Gets model name ("model.name"), platform version ("version.platform") | SaRawComp.java, SmoothReflectionApp.java, SmoothReflectionCompositProcess.java |
| `getFirmwareVersion()` | none | String | Gets firmware version string for version checking | SmoothReflectionApp.java |
| `getSupportedPictureSizes()` | none | List\<PictureSize\> | Gets max picture size for DSP buffer allocation | SaRawComp.java |

### com.sony.scalar.sysutil.ScalarInput
| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `getKeyStatus(int)` | keyCode | KeyStatus | Water housing detection | SmoothReflectionApp.java |

## 4. Image Processing Pipeline
1. **Prepare**: `SmoothReflectionCompositProcess.prepare()` sets up CameraSequence options (MEMORY_MAP_FILE, EXPOSURE_COUNT=1, RECORD_COUNT=1), creates `SaRawComp` instance, detects model for AVIP memory addresses
2. **Pre-capture**: `preTakePicture()` disables auto-review, locks camera settings (AE, WB), opens DSP via `SaRawComp.open()` which creates `DSP.createProcessor("sony-di-dsp")` and loads the smooth photography program (`libsa_smoothphotography.so` or `libsa_smoothphotography_avip.so`)
3. **Multi-frame capture loop**: `takePicture()` calls `CameraEx.burstableTakePicture()` repeatedly for N shots (configurable per theme, up to 100+)
4. **Raw compositing**: On each `onShutterSequence()`, raw data is passed to `SaRawComp.execute()`:
   - Gets `RawDataInfo` from raw data (canvas size, margins, Bayer info, compression params)
   - Applies model-specific raw address offset for BFNR cameras (HX400V/HX60V: +canvasX*6, RX100M3: +(canvasX+64)*6, ILCE-5000: +canvasX*8)
   - Builds 256-byte SA parameter buffer with command (6=first frame, 4=accumulate), addresses, and all raw metadata
   - Calls `SaUtil.executeSA(dsp, mboxParam, saParam, deviceMemoryArray)` to run DSP averaging
5. **Final development**: After all frames captured, `CameraSequence.DefaultDevelopFilter` develops the averaged raw into OptimizedImage
6. **Store**: `sequence.storeImage(img, true)` saves the final composited image

## 5. Button Handling
| Key Constant | Action |
|-------------|--------|
| `ISV_KEY_S2` (full shutter) | Starts multi-frame capture sequence |
| `ISV_KEY_S1_1` (half-press) | Autofocus lock |
| `ISV_KEY_MENU` | Cancel shooting / open menu (sets `sCancelPicturebyPressingMenu`) |
| `ISV_KEY_ENTER` | Confirm theme/shot count selection |
| `ISV_KEY_UP/DOWN/LEFT/RIGHT` | Navigate theme selection, adjust shot count |
| `ISV_KEY_DELETE` | Back/cancel |
| `ISV_KEY_PLAY` | Switch to playback |
| `WATER_HOUSING` | Housing detection via ScalarInput.getKeyStatus() |

## 6. Unique APIs
| API | Description |
|-----|-------------|
| `DSP.createProcessor("sony-di-dsp")` + custom `.so` programs | Uses dedicated image signal processor with custom shared libraries for raw averaging |
| `DSP.createBuffer(size)` | Allocates hardware DMA-accessible memory buffers for DSP processing |
| `SaUtil.executeSA(dsp, mboxParam, saParam, dmlist)` | Executes DSP program with mailbox parameters and device memory array |
| `SaUtil.getMemoryAddressAxi(DeviceMemory)` | Gets physical AXI bus address of device memory for DSP access |
| `SaUtil.convArmAddr2AxiAddr(int)` | Converts ARM virtual address to AXI physical address (AVIP models) |
| Model-specific raw offset calculation | BFNR offset lines vary by camera: RX100M3 adds `(canvasX+64)*6`, ILCE-5000 adds `canvasX*8`, HX400V/HX60V add `canvasX*6` |
| `CameraSequence.RawDataInfo` full field set | Exposes 30+ raw metadata fields including Bayer pattern, clipping, white balance, compression/decompression parameters |
| `MemoryMapConfig.setAllocationPolicy(1)` | Platform v2 memory allocation policy configuration |
| AVIP direct memory access | On NEX-5R/5T/6 models, reads raw data parameters directly from hardcoded memory addresses via MemoryMapFile |
