# Sony Scalar System/Media/Provider APIs - Detailed Reference

## ScalarInput (com.sony.scalar.sysutil.ScalarInput)
Camera button/dial input. Used via event.getScanCode() in onKeyDown/onKeyUp.

### Key Constants
- Navigation: ISV_KEY_UP, ISV_KEY_DOWN, ISV_KEY_LEFT, ISV_KEY_RIGHT, ISV_KEY_ENTER
- Menu: ISV_KEY_MENU, ISV_KEY_SK1 (same button)
- Delete/Back: ISV_KEY_DELETE, ISV_KEY_SK2 (same button)
- Function: ISV_KEY_FN, ISV_KEY_AEL, ISV_KEY_CUSTOM1
- Shutter: ISV_KEY_S1_1 (half-press/focus), ISV_KEY_S1_2 (intermediate), ISV_KEY_S2 (full)
- Media: ISV_KEY_PLAY (playback), ISV_KEY_STASTOP (movie record)
- Lens: ISV_KEY_LENS_ATTACH (attach/detach event)
- Dials: ISV_DIAL_1_CLOCKWISE/COUNTERCW (upper), ISV_DIAL_2_CLOCKWISE/COUNTERCW (lower)
- Status: ISV_DIAL_1_STATUS, ISV_DIAL_2_STATUS, ISV_KEY_MODE_DIAL

### Key Status
- ScalarInput.getKeyStatus(ISV_DIAL_1_STATUS).status / 22 = normalized dial position

## ScalarProperties (com.sony.scalar.sysutil.ScalarProperties)
- getString(key), getInt(key), getFirmwareVersion()
- PROP_MODEL_NAME -> "ILCE-6500"
- PROP_MODEL_CODE -> product code
- PROP_MODEL_SERIAL_CODE -> serial number
- PROP_MODEL_CATEGORY -> int (INTVAL_CATEGORY_ILDC_E for E-mount)
- PROP_VERSION_PLATFORM -> "2.17" (major.minor)

## AvindexStore (com.sony.scalar.provider.AvindexStore)
Replaces android.provider.MediaStore for Sony cameras.
- getExternalMediaIds() -> String[] (index 0 = SD card)
- Images.Media.EXTERNAL_CONTENT_URI -> content provider URI
- Images.Media._ID, DATA, CONTENT_CREATED_UTC_DATE_TIME
- Images.Media.getImageInfo(filePath) -> AvindexContentInfo

## AvindexContentInfo (com.sony.scalar.media.AvindexContentInfo)
- getAttribute(TAG_*) -> String, getAttributeInt(TAG_*, default), getAttributeDouble(TAG_*, default)
- getThumbnail() -> byte[]
- Tags: TAG_DCF_TBL_FILE_NAME, TAG_DCF_TBL_DIR_NAME, TAG_DATETIME, TAG_IMAGE_WIDTH, TAG_IMAGE_LENGTH, TAG_APERTURE, TAG_EXPOSURE_TIME, TAG_FOCAL_LENGTH, TAG_ISO, TAG_ORIENTATION

## AvindexGraphics
- getScreenNail(filePath) -> byte[] (~1616x1080 preview)

## Gpelibrary (com.sony.scalar.sysutil.didep.Gpelibrary)
- changeFrameBufferPixel(GS_FRAMEBUFFER_TYPE.ABGR8888) = 32-bit high quality
- changeFrameBufferPixel(GS_FRAMEBUFFER_TYPE.RGBA4444) = 16-bit low quality

## TimeUtil / PlainCalendar / PlainTimeZone (com.sony.scalar.sysutil)
- TimeUtil.getCurrentCalendar() -> PlainCalendar (year, month 1-based, day, hour, minute, second)
- TimeUtil.getCurrentTimeZone() -> PlainTimeZone (gmtDiff + summerTimeDiff in minutes)

## didep Utilities (com.sony.scalar.sysutil.didep) - "Device-Independent Device Extension Platform"
- **Settings**: getAutoPowerOffTime/set, getAutoRotate, getDialSetting, getGridLine, getTargetMediaId, getUsingMedia, setKeyLock, getTouchPanelEnabled -- 15 inner classes
- **ScalarSystemManager**: isSystemReady(), requestPowerOff(int,int,int), resetAll() -- 32 OFF_FACTOR constants (BATT_THERMAL=0, APO=14, etc.)
- **Power**: getInstance(), setBatteryRemainTimeListener()
- **Status**: create(), getFactors(), setInhFactorListener()
- **Temperature**: getStatus(), setCountDownInfoListener(), setStatusCallback()
- **Nfc**: isEnabled(), start()/stop() -- CONNECT_COMPLETED=1
- **Gps**: getGpsPowerState(), setGpsInfoListener() -- GPS_STATE_FIX=4, OFF=1
- **Bluetooth**: getBluetoothInfo(), setBluetoothInfoListener()
- **Media**: executeFormat(), execRecovery(RECOVERY=0, SALVAGE=1, REPAIR=2)
- **Caution**: EnableCaution(), SetFactor(), SetTrigger() -- ~12,000 constant fields
- **Kikilog**: setUserLog(int, Options)

## KeyStatus (com.sony.scalar.sysutil.KeyStatus)
- Hardware key state queries for detecting physical button states
- Used by touchless-shutter for key-level trigger detection

## Networking APIs
- ScalarWifiInfo (com.sony.scalar.sysnetutil): getProductCode()
- SsdpDevice (com.sony.scalar.lib.ssdpdevice.v1_1): getInstance(), initialize(ServerConf), enqueueStartServer/Stop
- WiFi Direct/P2P: com.sony.wifi.direct.*, com.sony.wifi.p2p.*, com.sony.wifi.wps.WpsError
- **WebAPI** (com.sony.scalar.webapi.*): Camera-hosted HTTP API for smartphone remote control (used by srctrl, sync-to-smart-phone)
  - Leza HTTP Server (com.sony.scalar.lib.leza.*): ServerBuilder, HttpProcessBuilder, servlet routing
  - AuthLibManager (com.sony.scalar.webapi.lib.authlib): Device authentication/pairing
  - DdController (com.sony.scalar.lib.ddserver): Device Description server for UPnP
  - Services: camera control v1.0-v1.4, content sync v1.0, access control

## Media APIs (com.sony.scalar.media)
- AudioManager: getParameters/setParameters, setSettingChangedListener, setMicrophoneChangedListener
- AudioRecord: hardware audio recording with DeviceBuffer, startRecording/stopRecording
- AudioTrack: audio playback, play/pause/write(DeviceBuffer)
- MediaRecorder: setCamera(CameraEx), setOutputMedia, prepare/start/stop, setLoopRecMode
- MediaPlayer: setDataSource, stepForward/stepRewind, FAST_FORWARD_1..4, setDisplayMode(FULL_SCREEN=0/HISTOGRAM=1)
- AvindexContentInfo: ~140+ TAG_ constants, getAttribute/getAttributeInt/getAttributeDouble
- HighlightMovieMaker: auto highlight movie - prepare, execute, setMovieBgm(MUSIC1..8), setMovieLength(15SEC/30SEC/1MIN/2MIN)
- MediaInfo: MEDIA_TYPE_SD=1, MEDIA_TYPE_MS=2, isEyeFi()

## Broadcast Intents
- com.android.server.DAConnectionManagerService.BootCompleted - camera boots app
- com.android.server.DAConnectionManagerService.ExitCompleted - app exit done
- com.android.server.DAConnectionManagerService.AppInfoReceive - register with OS (extras: package_name, class_name)
- com.android.server.DAConnectionManagerService.apo - auto power off (extra: apo_info = "APO/NORMAL" or "APO/NO")
