# Sony Scalar Hardware API - Detailed Reference

## CameraEx (com.sony.scalar.hardware.CameraEx)
- Core class for all camera operations, used by ALL 7 reference apps
- Static: open(int cameraId, OpenOptions), createParametersModifier(Camera.Parameters)
- Instance: getNormalCamera(), release(), reopen(), createEmptyParameters()
- Capture: burstableTakePicture(), startDirectShutter/stopDirectShutter, startSelfTimerShutter/cancelSelfTimerShutter
- Focus: startOneShotFocusDrive(), startFocusHold/stopFocusHold
- Exposure: incrementShutterSpeed/decrementShutterSpeed, incrementAperture/decrementAperture
- Zoom: setPreviewMagnification/stopPreviewMagnification, getPreviewMagnificationRatio
- Gamma: createGammaTable(), setExtendedGammaTable(GammaTable)
- Review: setAutoPictureReviewControl(ctrl)
- Info: getLensInfo(), getFocusAreaInfos(), getExternalFlashInfo()

### OpenOptions
- setPreview(bool), setInheritSetting(bool), setRecordingMode(0=still/1=movie), setTargetMedia(mediaId)

### ParametersModifier (~1727 lines, 100+ getter/setter pairs)
- **Constants**: AE_LOCK_ON="locked"/OFF="unlocked", FOCUS_MODE_AUTO/CONTINUOUS="continuous-picture"/DMF/MANUAL, FLASH_MODE_AUTO/OFF/ON, WHITE_BALANCE_AUTO="auto", CREATIVE_STYLE_STANDARD="Standard"
- ISO, shutter speed, aperture, auto focus mode, drive mode, flash type
- Image size/aspect ratio, metering mode, color mode, picture effect
- White balance (mode, temperature, compensation, custom)
- Contrast, saturation, sharpness, DRO mode/level, HDR mode
- Picture effects (miniature, partial color, posterization, toy camera, etc.)
- Long exposure NR, ND filter, silent shutter, face/smile detection
- All have getSupportedXxx() -> List for capability queries

### Data Objects
- ShutterSpeedInfo: currentShutterSpeed_n/_d, currentAvailableMax_n/_d
- ApertureInfo: currentAperture, currentAvailableMax/Min
- LensInfo: LensType (A/E-mount), PhaseShiftSensor, FocalLength (WideTele)
- GammaTable: setPictureEffectGammaForceOff(bool), write(byte[])
- AutoPictureReviewControl: getPictureReviewTime(), setPictureReviewTime(int)

### 30+ Listener Interfaces
- Shutter, Preview, JPEG, SettingChanged, ShutterSpeed, Aperture
- FocusDrive, AutoFocusDone, AutoFocusStart, FaceDetection
- Equipment, Error, Zoom, PreviewAnalize, StoreImageComplete
- PictureReviewStart, IntervalRec, TrackingFocus, NDFilter, MotionShot
- ProgramLine, FlashEmitting, FlashCharging, FocalLength, PowerZoom

## CameraSequence (com.sony.scalar.hardware.CameraSequence)
- open(CameraEx), storeImage(OptimizedImage/RawData, bool), release()
- ShutterSequenceCallback: onShutterSequence(RawData, CameraSequence)
- DefaultDevelopFilter: setSource(RawData, bool) -> execute() -> getOutput() -> OptimizedImage
- Options: MEMORY_MAP_FILE, EXPOSURE_COUNT, RECORD_COUNT, PREVIEW_FRAME_RATE/WIDTH/HEIGHT
- Preview DSP: setPreviewPlugin(DSP), startPreviewSequence(Options), stopPreviewSequence()
- RawDataInfo: canvasSizeX/Y, marginOffset/Size, validOffset/Size, WB, clipping, compression params

## DSP (com.sony.scalar.hardware.DSP)
- createProcessor("sony-di-dsp")
- setProgram("/path/to/lib.so"), clearProgram()
- createBuffer(size), createImage(w,h)
- setArg(index, DeviceMemory/Buffer/Image)
- execute(), cancel(), release()
- getPropertyAsInt("program-descriptor"/"memory-address"/"image-canvas-width"/"image-canvas-height")
- Memory: ARM addr & 0x3FFFFFFF = AXI addr (Musashi), ARM & 0x7FFFFFFF = AXI (AVIP)

## DisplayManager (com.sony.scalar.hardware.avio.DisplayManager)
- Devices: DEVICE_ID_PANEL/FINDER/HDMI/NONE
- Aspects: ASPECT_RATIO_3_2/4_3/5_3/11_9/16_9
- Methods: getActiveDevice(), switchDisplayOutputTo(), getDeviceInfo()
- OSD: setOSDOutput(device, "OSD_OUTPUT_ON"/"OSD_OUTPUT_OFF")
- Gain: getSupportedScreenGainControlTypes(), setScreenGainControlType()
- Listeners: DisplayEventListener (EVENT_SWITCH_DEVICE), OnScreenDisplayListener (OLED burn-in)

## Light (com.sony.scalar.hardware.indicator.Light)
- setState(ledId, on, pattern) / setState(ledId, on)
- Patterns: "PTN_FRONT_08" (0.8s blink), "PTN_FRONT_32" (3.2s blink)

## SubLCD (com.sony.scalar.hardware.indicator.SubLCD)
- initialize(), release(), setAllSegmentState(bool), setState(List<Parameter>)
- TextParameter(lid, text, visible, pattern), IconParameter(lid, lkid, visible, pattern)
