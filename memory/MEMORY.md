# Sony Camera Apps - Project Memory

## Project Context
- Workspace: project root directory (`sony-apps/`)
- Target: Sony A6500 (ILCE-6500, CXD90014) running Android 4.1-4.4 internally
- Camera detection: Build.BRAND="sony", Build.MODEL="ScalarA", Build.DEVICE="dslr-diadem"
- Screen: 640x480 landscape, physical buttons only (no touch)
- Build: compileSdk 16, targetSdk 10, Java 8, Gradle plugin 4.2.2, BuildTools 30.0.2, NDK 21.4.7075529, CMake 3.10.2
- ABIs: armeabi-v7a (camera) + x86 (emulator)
- Emulator: Android 7.1.1 x86 API 25, 640x480 landscape

## Directory Map (all paths relative to project root `sony-apps/`)
- `./open-memories-app/` - Working template app (base for new projects)
- `./reference-apps/` - 19 decompiled Sony 1st-party apps (read-only): bracket-pro, digital-filter, double-exposure, graduated-filter, light-graffiti, light-shaft, live-view-grading, manual-lens-compensation, photo-retouch, picture-effect-plus, portrait-beauty, smooth-reflection, smooth-reflection-2, sound-photo, srctrl, star-trails, sync-to-smart-phone, time-lapse, touchless-shutter
- `./docs/index.html` - Generated API documentation website (searchable, dark theme)
- `./Bible.md` - Complete Sony native API reference (~1,900+ lines)
- `./CLAUDE.md` - Project build rules and conventions
- `./memory/` - Project knowledge base (this directory)
  - `README.md` - Self-instruction file for Claude (how to use these files)
  - `sony-scalar-hardware.md` - CameraEx, CameraSequence, DSP, DisplayManager, Indicators
  - `sony-scalar-graphics.md` - OptimizedImage, ImageFilters, JpegExporter, OptimizedImageView
  - `sony-scalar-system.md` - ScalarInput, ScalarProperties, AvindexStore, didep, Networking, Media
  - `app-architecture.md` - Reference app architecture, dual-device pattern, build config
- `./tmp/OpenMemories-Framework/` - Cloned framework repo (with API_REFERENCE.md)

## Sony Native API Summary (com.sony.scalar.*)
- **hardware.CameraEx**: Core camera API. open(0, opts), ParametersModifier (100+ get/set methods for ISO, shutter, aperture, WB, effects, etc.), ShutterSpeedInfo, ApertureInfo, LensInfo, GammaTable, 30+ listener interfaces
- **hardware.CameraSequence**: Capture pipeline. open(cameraEx), ShutterSequenceCallback (gets RawData), DefaultDevelopFilter (Raw→OptimizedImage), storeImage(), Options (EXPOSURE_COUNT, MEMORY_MAP_FILE, etc.)
- **hardware.DSP**: Hardware image processor. createProcessor("sony-di-dsp"), setProgram(.so), createBuffer/Image, setArg, execute. Properties: memory-address, image-canvas-width/height
- **hardware.DeviceBuffer/DeviceMemory**: DSP memory. write(ByteBuffer), read(ByteBuffer), release()
- **hardware.avio.DisplayManager**: LCD/EVF/HDMI. DEVICE_ID_PANEL/FINDER/HDMI, switchDisplayOutputTo(), setScreenGainControlType()
- **hardware.indicator.Light**: LED control. setState(id, on/off, blinkPattern)
- **hardware.indicator.SubLCD**: Top LCD. initialize(), TextParameter, IconParameter, setState()
- **graphics.OptimizedImage**: Core image container (YUV422). getWidth/Height, isValid, release. Never constructed directly
- **graphics.OptimizedImageFactory**: JPEG decoder. decodeImage(path, Options). Options.imageType: 2=screen, 3=full
- **graphics.ImageAnalyzer**: Face detection. findFaces(image, AnalyzedFace[8]). Face rect range: -1000 to +1000
- **graphics.JpegExporter**: JPEG encoder. encode(image, mediaId, Options). Options.quality: 1=Standard, 2=Fine, 3=ExtraFine
- **graphics.imagefilter.***: ScaleImageFilter, CropImageFilter, RotateImageFilter, FaceNRImageFilter, MiniatureImageFilter. All: setSource→configure→execute→getOutput→clearSources→release
- **widget.OptimizedImageView**: Display OptimizedImage. setOptimizedImage(), setScale(), setDisplayRotationAngle(), zoom/pan
- **media.AvindexContentInfo**: Image EXIF from AvindexStore. TAG_* constants for filename, date, ISO, aperture, etc.
- **provider.AvindexStore**: Replaces MediaStore. Images.Media.EXTERNAL_CONTENT_URI, getExternalMediaIds()[0]=SD card
- **sysutil.ScalarInput**: Button constants. ISV_KEY_UP/DOWN/LEFT/RIGHT/ENTER/MENU/DELETE/FN/AEL/S1_1/S2/PLAY/STASTOP/CUSTOM1/LENS_ATTACH, ISV_DIAL_1/2, ISV_KEY_MODE_DIAL. getKeyStatus()
- **sysutil.ScalarProperties**: Device info. getString(PROP_MODEL_NAME), getFirmwareVersion(), INTVAL_CATEGORY_ILDC_E
- **sysutil.didep**: Settings, Status, Temperature, Bluetooth, Gps, Gpelibrary (framebuffer: ABGR8888/RGBA4444)
- **sysutil.TimeUtil**: Camera time. getCurrentCalendar() → PlainCalendar, getCurrentTimeZone() → PlainTimeZone

## Active Projects
- `./cube-lut-app-plan.md` - Full implementation plan for Cube LUT color grading app
- `./cube-lut-app/` - App directory (once implementation starts)

## Critical Patterns
- **Dual-device**: DeviceInfo.getInstance().isCamera() → CameraXxx or fallback impl
- **Image pipeline**: RawData → DefaultDevelopFilter → OptimizedImage → imagefilters → JpegExporter/storeImage
- **Button handling**: onKeyDown(scanCode) → ScalarInput.ISV_KEY_* constants. Delete = Back
- **Color depth**: Gpelibrary.changeFrameBufferPixel(ABGR8888) for quality, RGBA4444 for performance
- **Camera intents**: DAConnectionManagerService.BootCompleted/ExitCompleted/AppInfoReceive/apo
- **APO disable**: sendBroadcast("apo", "APO/NO") to prevent auto power off
- **Base library**: com.sony.imaging.app.base.* shared across all 19 reference apps (BaseApp Activity, State machine, Menu system, Caution system)

## User Preferences
- Batch questions for similar decisions
- Use own judgment for routine decisions, report after
- Skill available: `/simplify` for code review after writing code
- Legacy compatibility is paramount
