# Photo Retouch - API Reference

## 1. App Overview

| Field | Value |
|-------|-------|
| **Package** | `com.sony.imaging.app.photoretouch` |
| **Purpose** | Post-capture photo editing with comprehensive image filters |
| **Description** | Provides post-capture image editing capabilities including crop, rotate, resize, face smoothing (NR), and brightness/contrast/saturation (BCS) adjustment via DSP-based LCE (Luminance Contrast Enhancement). Works on existing photos from the camera's media storage. |

## 2. Architecture

### Main Activity
- Entry point managed via `BaseApp` framework
- Operates in playback mode (edits existing images) rather than shooting mode

### Key Classes

| Class | Path | Role |
|-------|------|------|
| `ImageEditor` | `common/ImageEditor.java` | Central image editing engine - manages image state, filter pipelines, DSP-based LCE, and JPEG export |
| `LCEControl` | `playback/control/LCEControl.java` | DSP-based Luminance/Contrast/Saturation Enhancement controller |
| `YuvToRgbConversion` | `playback/control/YuvToRgbConversion.java` | DSP-based YUV to RGB color space conversion for display |
| `BrightnessControlLayout` | `playback/layout/brightness/BrightnessControlLayout.java` | UI for brightness adjustment |
| `ContrastControlLayout` | `playback/layout/contrast/ContrastControlLayout.java` | UI for contrast adjustment |
| `SaturationControlLayout` | `playback/layout/saturation/SaturationControlLayout.java` | UI for saturation adjustment |
| `ResizeLayout` | `playback/layout/resize/ResizeLayout.java` | Defines resize dimensions per aspect ratio (M/S sizes) |

## 3. Sony Scalar API Usage

### com.sony.scalar.graphics.imagefilter - Image Filters

#### ScaleImageFilter

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `new ScaleImageFilter()` | None | `ScaleImageFilter` | Creates a scale/resize filter | `common/ImageEditor.java` |
| `filter.setSource(OptimizedImage, boolean)` | Image, release source flag | `void` | Sets the input image (false = keep original) | `common/ImageEditor.java` |
| `filter.setDestSize(int, int)` | Width, height | `void` | Sets target dimensions for scaling | `common/ImageEditor.java` |
| `filter.execute()` | None | `boolean` | Executes the scale operation | `common/ImageEditor.java` |
| `filter.getOutput()` | None | `OptimizedImage` | Gets the scaled result image | `common/ImageEditor.java` |
| `filter.clearSources()` | None | `void` | Clears input references | `common/ImageEditor.java` |
| `filter.release()` | None | `void` | Releases filter resources | `common/ImageEditor.java` |

#### CropImageFilter

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `new CropImageFilter()` | None | `CropImageFilter` | Creates a crop filter | `common/ImageEditor.java` |
| `filter.setSrcRect(Rect)` | Crop rectangle | `void` | Sets the crop area within the source image | `common/ImageEditor.java` |
| `filter.setSource(OptimizedImage, boolean)` | Image, release flag | `void` | Sets input image | `common/ImageEditor.java` |
| `filter.execute()` | None | `boolean` | Executes the crop | `common/ImageEditor.java` |
| `filter.getOutput()` | None | `OptimizedImage` | Gets the cropped result | `common/ImageEditor.java` |
| `filter.clearSources()` | None | `void` | Clears input references | `common/ImageEditor.java` |
| `filter.release()` | None | `void` | Releases filter resources | `common/ImageEditor.java` |

#### RotateImageFilter

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `new RotateImageFilter()` | None | `RotateImageFilter` | Creates a rotation filter | `common/ImageEditor.java` |
| `filter.setTrimMode(int)` | Mode (3) | `void` | Sets trim mode for rotation (3 = auto-trim to fit) | `common/ImageEditor.java` |
| `filter.setRotation(ROTATION_DEGREE)` | `DEGREE_0/90/180/270` | `void` | Sets rotation by 90-degree increments | `common/ImageEditor.java` |
| `filter.setRotation(double)` | Angle in degrees | `void` | Sets arbitrary rotation angle (for horizon adjustment) | `common/ImageEditor.java` |
| `filter.setSource(OptimizedImage, boolean)` | Image, release flag | `void` | Sets input image | `common/ImageEditor.java` |
| `filter.execute()` | None | `boolean` | Executes the rotation | `common/ImageEditor.java` |
| `filter.getOutput()` | None | `OptimizedImage` | Gets the rotated result | `common/ImageEditor.java` |
| `filter.clearSources()` | None | `void` | Clears input references | `common/ImageEditor.java` |
| `filter.release()` | None | `void` | Releases filter resources | `common/ImageEditor.java` |

#### FaceNRImageFilter (Face Noise Reduction)

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `new FaceNRImageFilter()` | None | `FaceNRImageFilter` | Creates a face-targeted noise reduction filter for skin smoothing | `common/ImageEditor.java` |
| `filter.setFaceList(Vector<Rect>)` | List of face rectangles | `void` | Sets detected face regions to apply NR to | `common/ImageEditor.java` |
| `filter.setISOValue(int)` | ISO sensitivity | `void` | Sets ISO for NR strength calibration | `common/ImageEditor.java` |
| `filter.setNRLevel(int)` | Level 1-5 | `void` | Sets noise reduction strength (1=max, 5=min) | `common/ImageEditor.java` |
| `filter.setSource(OptimizedImage, boolean)` | Image, release flag | `void` | Sets input image | `common/ImageEditor.java` |
| `filter.execute()` | None | `boolean` | Applies face-targeted noise reduction | `common/ImageEditor.java` |
| `filter.getOutput()` | None | `OptimizedImage` | Gets the smoothed result | `common/ImageEditor.java` |
| `filter.clearSources()` | None | `void` | Clears input references | `common/ImageEditor.java` |
| `filter.release()` | None | `void` | Releases filter resources | `common/ImageEditor.java` |

### com.sony.scalar.hardware.DSP - LCE (Luminance Contrast Enhancement)

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `DSP.createProcessor("sony-di-dsp")` | Processor name | `DSP` | Creates DSP for LCE processing | `common/ImageEditor.java` |
| `dsp.setProgram(String)` | `libsa_lce.so` or `libsa_lce_musashi.so` | `void` | Loads LCE SA program (HW-version dependent) | `common/ImageEditor.java` |
| `dsp.createBuffer(int)` | Size: 60 (boot), 1024 (table), 32 (params) | `DeviceBuffer` | Allocates parameter and tone-table buffers | `playback/control/LCEControl.java` |
| `dsp.getPropertyAsInt(String)` | `"program-descriptor"` | `int` | Gets program descriptor for boot params | `playback/control/LCEControl.java` |
| `dsp.getPropertyAsInt(OptimizedImage, String)` | Image + `"memory-address"`, `"image-data-offset"`, `"image-canvas-width"` | `int` | Gets hardware memory layout of image | `playback/control/LCEControl.java` |
| `dsp.getPropertyAsInt(DeviceBuffer, String)` | Buffer + `"memory-address"` | `int` | Gets hardware address of tone table buffer | `playback/control/LCEControl.java` |
| `dsp.createImage(int, int)` | Width, height | `OptimizedImage` | Creates a DSP-backed output image buffer | `playback/control/LCEControl.java` |
| `dsp.setArg(int, ...)` | Index 0-4: boot, params, input, output, table | `void` | Sets DSP execution arguments | `playback/control/LCEControl.java` |
| `dsp.execute()` | None | `boolean` | Executes LCE DSP program | `playback/control/LCEControl.java` |
| `dsp.clearProgram()` | None | `void` | Clears loaded program | `common/ImageEditor.java` |
| `dsp.release()` | None | `void` | Releases DSP processor | `common/ImageEditor.java` |

### DSP - YUV to RGB Conversion

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `DSP.createProcessor("sony-di-dsp")` | Processor name | `DSP` | Creates second DSP for YUV-to-RGB conversion | `common/ImageEditor.java` |
| `dsp.setProgram(String)` | `libsa_pack_yuv2rgb.so` or `libsa_yuv2rgb_musashi_03RGBA.so` | `void` | Loads YUV-to-RGB conversion program | `common/ImageEditor.java` |

### com.sony.scalar.graphics.JpegExporter

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `new JpegExporter()` | None | `JpegExporter` | Creates JPEG encoder for saving edited images | `common/ImageEditor.java` |
| `exporter.encode(OptimizedImage, String, Options)` | Image, media ID, options | `void` | Encodes and saves image to camera media storage | `common/ImageEditor.java` |
| `exporter.release()` | None | `void` | Releases encoder resources | `common/ImageEditor.java` |

### JpegExporter.Options

| Field | Value | Description | Source File |
|-------|-------|-------------|-------------|
| `options.quality` | `2` | JPEG quality level (2 = high quality) | `common/ImageEditor.java` |

### com.sony.scalar.graphics.OptimizedImage

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `image.getWidth()` | None | `int` | Gets image width | `common/ImageEditor.java` |
| `image.getHeight()` | None | `int` | Gets image height | `common/ImageEditor.java` |
| `image.isValid()` | None | `boolean` | Checks if image is still valid (not released) | `common/ImageEditor.java` |
| `image.release()` | None | `void` | Releases the image buffer from memory | `common/ImageEditor.java` |

### com.sony.scalar.provider.AvindexStore

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `AvindexStore.getExternalMediaIds()` | None | `String[]` | Gets external media (SD card) identifiers | `common/ImageEditor.java` |
| `AvindexStore.Images.Media.getContentUri(String)` | Media ID | `Uri` | Gets content URI for image database queries | `common/ImageEditor.java` |
| `AvindexStore.Images.waitAndUpdateDatabase(ContentResolver, String)` | Resolver, media ID | `void` | Waits for database update after saving (blocking) | `common/ImageEditor.java` |
| `AvindexStore.Images.Media.rotateImage(ContentResolver, Uri, long, int)` | Resolver, URI, image ID, orientation | `void` | Updates EXIF orientation in database after rotation | `common/ImageEditor.java` |

### com.sony.scalar.sysutil.ScalarProperties

| Method | Parameters | Return | Description | Source File |
|--------|-----------|--------|-------------|-------------|
| `ScalarProperties.getString("version.platform")` | Property key | `String` | Gets platform version to select HW-specific DSP binaries | `common/ImageEditor.java`, `playback/control/LCEControl.java` |

## 4. Image Processing Pipeline

### Edit Flow
1. **Image Selection** - User selects an image from playback/gallery
2. **Image Loading** - Image loaded as `OptimizedImage` via `ImageEditor.setImage()`
3. **Aspect Ratio Detection** - `retrieveAspectRatio()` determines 3:2, 16:9, 4:3, or 1:1 from dimensions
4. **Orientation Handling** - EXIF orientation (1/3/6/8) mapped to rotation degree

### Editing Operations
- **Crop**: `CropImageFilter.setSrcRect(Rect)` -> `execute()` -> `getOutput()`
- **Rotate (90-degree)**: `RotateImageFilter.setRotation(DEGREE_90/180/270)` with `setTrimMode(3)`
- **Rotate (arbitrary)**: `RotateImageFilter.setRotation(double)` for horizon correction
- **Resize**: `ScaleImageFilter.setDestSize(width, height)` with preset M/S sizes per aspect ratio
- **Skin Smoothing**: `FaceNRImageFilter` with face rectangles, ISO value, and NR level 1-5
- **BCS Adjustment**: DSP-based LCE pipeline (see below)

### LCE (Brightness/Contrast/Saturation) Pipeline
1. **JNI Tone Table** - `MakeLceTable(int[] bcs, int[] param2, int[] toneTable)` native call generates a 1024-byte tone table from BCS parameters
2. **DSP Setup** - Two DSP processors: one for LCE (`libsa_lce.so`), one for YUV-to-RGB (`libsa_pack_yuv2rgb.so`)
3. **Parameter Writing** - LCE params: input/output/table addresses, image dimensions, canvas widths written to 32-byte buffer
4. **DSP Execution** - `dsp.execute()` applies tone table transformation in YUV space
5. **Color Conversion** - Second DSP converts YUV result to RGB for display

### Save Flow
1. **Space Check** - `updateSpaceAvailableInMemoryCard()` checks AvindexStore inhibition factors and available disk space
2. **JPEG Encoding** - `JpegExporter.encode(image, mediaId, options)` with quality=2
3. **EXIF Update** - `AvindexStore.Images.Media.rotateImage()` updates orientation metadata
4. **Database Refresh** - `AvindexStore.Images.waitAndUpdateDatabase()` then `ContentsManager.requeryData()`

## 5. Button Handling

Button handling is managed by the shared `BaseApp` framework via `ScalarInput` key events:

| Key Constant | Action |
|-------------|--------|
| `ISV_KEY_UP/DOWN` | Adjust BCS slider value / navigate edit options |
| `ISV_KEY_LEFT/RIGHT` | Switch between edit modes (crop, rotate, BCS, etc.) |
| `ISV_KEY_ENTER` | Confirm current edit / apply changes |
| `ISV_KEY_DELETE` | Cancel edit / back to previous screen |
| `ISV_KEY_MENU` | Open edit menu |
| `ISV_DIAL_1` | Fine adjustment of current parameter |
| `ISV_DIAL_2` | Secondary parameter adjustment |
| `ISV_KEY_PLAY` | Return to playback view |

## 6. Unique APIs

### APIs unique to photo-retouch (not commonly used by other reference apps):

| API | Description |
|-----|-------------|
| **Full image filter pipeline** | Only app using all four image filters together: `ScaleImageFilter`, `CropImageFilter`, `RotateImageFilter`, `FaceNRImageFilter` |
| **`JpegExporter.encode()`** | Only app that exports edited images back to media storage as new JPEG files |
| **`AvindexStore.Images.Media.rotateImage()`** | Updates EXIF orientation in the media database after rotation edits |
| **`AvindexStore.Images.waitAndUpdateDatabase()`** | Blocking wait for database update after saving - ensures new image appears in gallery |
| **DSP-based LCE** (`libsa_lce.so`) | Unique Luminance/Contrast/Enhancement SA program with JNI tone table generation (`MakeLceTable` native) |
| **DSP-based YUV-to-RGB** (`libsa_pack_yuv2rgb.so`) | Separate DSP processor for color space conversion of LCE output |
| **`DSP.createImage(width, height)`** | Creates DSP-backed OptimizedImage as output buffer (not used in other apps) |
| **`RotateImageFilter.setRotation(double)`** | Arbitrary-angle rotation (not just 90-degree increments) for horizon straightening |
| **Space availability checks** via `AvindexStore` inhibition factors | Checks `INH_FACTOR_CONTENT_FULL_FOR_STILL`, `INH_FACTOR_FOLDER_FULL_FOR_STILL`, `INH_FACTOR_NEED_REPAIR_AVINDEX` before saving |
