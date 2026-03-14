# Double Exposure - API Reference

## 1. App Overview

| Field | Value |
|-------|-------|
| **Package** | `com.sony.imaging.app.doubleexposure` |
| **Purpose** | Two-shot image compositing with 7 blend themes |
| **Description** | Captures or selects two images and composites them using DSP hardware acceleration with 7 different blend modes (Add, Mean, Lighten, Darken, Screen, Multiply, Weighted Mean). Features real-time live view overlay showing the blend result before capturing the second shot. |

## 2. Architecture

### Main Activity
- Entry point managed via `BaseApp` framework
- State machine driven by `com.sony.imaging.app.fw.State` subclasses

### Key Classes

| Class | Path | Role |
|-------|------|------|
| `DESA` | `common/DESA.java` | Double Exposure SA (Signal Accelerator) - main DSP blend processor with live view support |
| `DESAPre` | `common/DESAPre.java` | Pre-processing SA - handles image rotation/flip transforms before blending |
| `DoubleExposureUtil` | `common/DoubleExposureUtil.java` | Utility for managing first/second image state and blend mode selection |
| `DoubleExposureImageUtil` | `common/DoubleExposureImageUtil.java` | Utility for image memory management and Diadem memory transfers |
| `SAParam` (inner class) | `common/DESA.java` | Encapsulates blend mode, SFR mode, and weight mean value |

## 3. Sony Scalar API Usage

### com.sony.scalar.hardware.DSP

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `DSP.createProcessor(String)` | `"sony-di-dsp"` | `DSP` | Creates a DSP hardware processor for image blending | `common/DESA.java`, `common/DESAPre.java` |
| `dsp.setProgram(String)` | `libsa_mle.so` or `libsa_mle_mushashi.so` | `void` | Loads the blend program (different binary per HW generation) | `common/DESA.java` |
| `dsp.createBuffer(int)` | 60 (boot), 256 (SA params) | `DeviceBuffer` | Allocates parameter buffers for DSP communication | `common/DESA.java` |
| `dsp.getPropertyAsInt(String)` | `"program-descriptor"` | `int` | Gets DSP program descriptor for boot params | `common/DESA.java` |
| `dsp.getPropertyAsInt(OptimizedImage, String)` | Image + `"memory-address"`, `"image-data-offset"`, `"image-canvas-width"` | `int` | Gets hardware memory layout of an OptimizedImage | `common/DESA.java` |
| `dsp.getPropertyAsInt(DeviceMemory, String)` | Memory + `"memory-address"` | `int` | Gets hardware address of device memory for live view overlay | `common/DESA.java` |
| `dsp.setArg(int, DeviceBuffer)` | Index 0=boot, 1=params | `void` | Sets boot parameters and SA parameters | `common/DESA.java` |
| `dsp.setArg(int, DeviceMemory)` | Index 2=memory | `void` | Sets device memory reference for first image data | `common/DESA.java` |
| `dsp.execute()` | None | `boolean` | Executes the blend operation (returns true on success) | `common/DESA.java` |
| `dsp.release()` | None | `void` | Releases DSP processor resources | `common/DESA.java` |

### com.sony.scalar.hardware.CameraSequence

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `CameraSequence.open(CameraEx)` | Camera instance | `CameraSequence` | Opens camera sequence for live view blending | `common/DESA.java` |
| `sequence.setPreviewPlugin(DSP)` | DSP processor | `void` | Installs DESA as live preview plugin - shows real-time blend of first image with camera feed | `common/DESA.java` |
| `sequence.startPreviewSequence(Options)` | Sequence options | `void` | Starts live view with blend overlay active | `common/DESA.java` |
| `sequence.stopPreviewSequence()` | None | `void` | Stops the live view overlay | `common/DESA.java` |
| `sequence.setPreviewPlugin((DSP) null)` | null | `void` | Removes the preview plugin before release | `common/DESA.java` |
| `sequence.release()` | None | `void` | Releases the camera sequence | `common/DESA.java` |

### CameraSequence.Options

| Method | Parameters | Description | Source File |
|--------|-----------|-------------|-------------|
| `setOption("PREVIEW_FRAME_RATE", 30000)` | Frame rate | 30fps preview for live blend overlay | `common/DESA.java` |
| `setOption("PREVIEW_FRAME_WIDTH", int)` | Width (640 or 1024) | Preview width based on aspect ratio (1:1 vs others) | `common/DESA.java` |
| `setOption("PREVIEW_FRAME_HEIGHT", 0)` | 0 = auto | Auto-calculated preview height | `common/DESA.java` |
| `setOption("PREVIEW_PLUGIN_NOTIFY_MASK", 0)` | Mask | Disable plugin notifications | `common/DESA.java` |
| `setOption("PREVIEW_DEBUG_NOTIFY_ENABLED", false)` | Boolean | Disable debug output | `common/DESA.java` |
| `setOption("PREVIEW_PLUGIN_OUTPUT_ENABLED", true)` | Boolean | Enable plugin rendered output to display | `common/DESA.java` |

### com.sony.scalar.graphics.OptimizedImage

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `image.getWidth()` | None | `int` | Gets image width for DSP parameter setup | `common/DESA.java` |
| `image.getHeight()` | None | `int` | Gets image height for DSP parameter setup | `common/DESA.java` |

### com.sony.scalar.hardware.DeviceBuffer / DeviceMemory

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `buffer.write(ByteBuffer)` | Param data | `void` | Writes boot/SA parameters to hardware buffer | `common/DESA.java` |
| `buffer.release()` | None | `void` | Releases hardware buffer | `common/DESA.java` |
| `memory.release()` | None | `void` | Releases device memory (first image data) | `common/DESA.java` |

## 4. Image Processing Pipeline

1. **First Image Capture/Selection** - User captures or selects the first image from gallery
2. **Memory Transfer** - `DoubleExposureImageUtil.memoryCopyApplicationToDiadem()` copies first image to hardware DSP memory
3. **Live View Overlay** - `DESA.startLiveViewEffect()`:
   - Opens `CameraSequence` on the camera
   - Configures preview options (30fps, resolution based on aspect ratio)
   - Sets boot parameters (program descriptor, SFR mode)
   - Sets SA parameters (first image address, blend mode, weight)
   - Installs DSP as preview plugin via `setPreviewPlugin()`
   - Starts preview sequence - user sees live blend of first image + camera feed
4. **Blend Mode Selection** - User selects from 7 blend modes, `updateLiveViewEffect()` updates SA parameters in real-time
5. **Second Image Capture** - Standard camera capture for second image
6. **Final Compositing** - `DESA.execute()` blends both OptimizedImages via DSP:
   - Reads memory addresses and canvas widths of both input images and output
   - Configures blend mode and weight parameters
   - DSP hardware performs pixel-level blending
7. **Cleanup** - `DESA.terminate()` stops preview, releases all DSP/memory resources

### Blend Modes (SA Parameter Values)

| UI Mode | SA Value | Description |
|---------|----------|-------------|
| Add | 0 | Additive blending |
| Weighted Mean | 6 | Adjustable weight (0-255) between images |
| Screen | 4 | Screen blend mode |
| Multiply | 5 | Multiply blend mode |
| Lighten | 2 | Takes brighter pixel |
| Darken | 3 | Takes darker pixel |

### DESAPre Transform Modes

| Constant | Value | Description |
|----------|-------|-------------|
| `REVERSE_HORIZONTAL` | 0 | Horizontal flip |
| `REVERSE_VERTICAL` | 1 | Vertical flip |
| `ROTATION_180` | 2 | 180-degree rotation |

## 5. Button Handling

Button handling is managed by the shared `BaseApp` framework via `ScalarInput` key events:

| Key Constant | Action |
|-------------|--------|
| `ISV_KEY_S1_1` | Half-press shutter - autofocus |
| `ISV_KEY_S2` | Full shutter press - capture second image |
| `ISV_KEY_UP/DOWN` | Navigate blend mode selection |
| `ISV_KEY_LEFT/RIGHT` | Adjust blend weight (for Weighted Mean mode) |
| `ISV_KEY_ENTER` | Confirm selection / apply blend |
| `ISV_KEY_DELETE` | Back / cancel / discard |
| `ISV_KEY_MENU` | Open menu |
| `ISV_DIAL_1` | Adjust blend parameters |
| `ISV_KEY_PLAY` | Switch to playback to select first image from gallery |

## 6. Unique APIs

### APIs unique to double-exposure (not commonly used by other reference apps):

| API | Description |
|-----|-------------|
| **DESA (Double Exposure SA)** with live view overlay | Unique DSP program (`libsa_mle.so`) that blends a pre-loaded first image with real-time camera feed as a CameraSequence preview plugin |
| **DESAPre transform processor** | Separate DSP processor (`libsa_pre_mle.so`) for image rotation/flip transforms before blending |
| **7 blend modes via DSP** | Hardware-accelerated blend modes: Add(0), Mean(1), Lighten(2), Darken(3), Screen(4), Multiply(5), Weighted Mean(6) |
| **SFR Mode switching** | `SFR_MODE_LIVEVIEW = 1` vs `SFR_MODE_PLAYBACK = 0` - changes DSP behavior between real-time preview and final composite |
| **Hardware-generation-aware binary selection** | Uses `Environment.getVersionOfHW()` to load either standard or "mushashi" variant of DSP program |
| **AXI address masking** | `getAXIAddr()` masks memory addresses differently per hardware generation: `& 0x7FFFFFFF` (HW v1) vs `& 0x3FFFFFFF` (HW v2) |
| **DeviceMemory for live view** | Copies first image to `DeviceMemory` via DSP, then references it by AXI address during real-time preview blending |
