# Cube LUT App - Project Status

## Plan
See `../cube-lut-app-plan.md` for the full design document.

## Implementation Status

### Completed
- [x] Project structure (build files, gradle, manifest)
- [x] CubeParser.java - .cube file parser (metadata-only + full parse)
- [x] LutFileManager.java - SD card + asset LUT scanning with fallback
- [x] lut-processor.cpp - Native trilinear 33³ interpolation (NV21 YUV)
- [x] LutProcessor.java - JNI bridge (loadLut, applyLut, freeLut, isLoaded)
- [x] LutDecomposer.java - 3D LUT → 1D curve + 3x3 matrix (least-squares)
- [x] IspController.java - GammaTable + RGBMatrix ISP control with dual-device guard
- [x] FileLogger.java - SD card logging with crash handler
- [x] LutMenuActivity.java - LUT browser with header, list, navigation
- [x] LutListAdapter.java - Custom adapter with thumbnails, placeholders, active marker
- [x] CubeLutApp.java - Main activity with camera, capture queue, ISP preview
- [x] HelpActivity.java - Help overlay showing all controls
- [x] Layouts (activity_main, activity_menu, lut_list_item, activity_help)
- [x] App icon (XML drawable)
- [x] Build succeeds (APK generated)

### Not Yet Tested
- [ ] Emulator testing (UI, LUT parsing, JNI processing)
- [ ] Camera testing (ISP preview, CameraSequence capture, hardware buttons)
- [ ] Burst queue behavior under load
- [ ] EXIF metadata writing on Android 4.x
- [ ] Memory stability (50+ captures, LUT switching)

## Key Design Decisions

1. **Memory-first**: One LUT in memory at a time (~431KB native heap). Switch = free old → load new.
2. **33³ trilinear only**: Simpler, consistent, sufficient quality. Non-33 .cube files ignored.
3. **ISP preview is approximate**: 3D LUT decomposed to 1D gamma + 3x3 matrix. Looks different from final output but gives a color direction preview.
4. **Background capture processing**: Shutter never blocked. LUT applied on worker thread with max queue depth of 5.
5. **Dual-device**: All Sony API calls guarded by `DeviceInfo.getInstance().isCamera()`. Emulator uses standard Android Camera API.
6. **EXIF metadata**: LUT info written to UserComment tag via Apache Commons Imaging. Non-fatal if it fails.

## Known Risks
- **CameraSequence capture**: Current emulator path uses standard `Camera.takePicture()`. Real camera needs CameraSequence + DefaultDevelopFilter pipeline. May need adaptation.
- **EXIF library**: Apache Commons Imaging 1.0-alpha2 may have issues on Android 4.x KitKat. Fallback: skip EXIF writing.
- **ISP decomposition quality**: Least-squares 3x3 matrix fit may not represent complex color transforms well. Preview will always be approximate.
