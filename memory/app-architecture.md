# App Architecture & Build Config

## Reference App Architecture (com.sony.imaging.app.base.*)
All 7 Sony 1st-party apps share identical base library:

### App Lifecycle (single-Activity, Fragment-based)
1. BaseApp extends Activity - the main entry point
2. ExecutorCreator opens CameraEx with OpenOptions
3. CameraSetting singleton holds CameraEx + parameters
4. State machine drives screens: EEState (viewfinder) → CaptureState → ReviewState
5. Fragments for UI: ShootingFragment, MenuFragment, CautionFragment, PlaybackFragment

### Key Pattern: Factory
Each app provides Factory class for wiring:
- Custom states (capture, review, composite)
- Custom layouts (app-specific UI)
- Custom controllers (app-specific settings)

### Menu System
- BaseMenuService → MenuController → MenuTable (XML-defined trees)
- Hierarchical menus with BeltView (horizontal scroll), CursorableGridView
- ~40 menu layout classes for different settings

### Caution System
- CautionUtilityClass manages warnings (overheat, low battery, no card)
- CautionDisplayState shows warning overlays
- CautionProcessingFunction handles LED blinking, beeps
- IKeyDispatch routes key events during caution display

### Key Dispatch
- BaseApp.onKeyDown → checks caution state → routes to current state
- State handles key or delegates to ShootingKeyHandlerBase
- ScalarInput scan codes matched in switch/case
- Dial events normalized: status / 22

## Dual-Device Implementation Pattern
From OpenMemories-Framework (ma1co):

### Detection
```java
private static final Boolean isCamera = "sony".equals(Build.BRAND)
    && "ScalarA".equals(Build.MODEL)
    && "dslr-diadem".equals(Build.DEVICE);
```

### Factory Method Pattern
```java
public static XxxManager create(Context ctx) {
    if (DeviceInfo.getInstance().isCamera())
        return new CameraXxxManager(ctx);  // Real Sony API
    else
        return new XxxManager(ctx);        // Dummy fallback
}
```

### Implemented Wrappers in OpenMemories-Framework
- DeviceInfo: ScalarProperties (camera) vs Build.* (android)
- DisplayManager: avio.DisplayManager (camera) vs WindowManager (android)
- MediaManager: AvindexStore (camera) vs MediaStore (android)
- ImageInfo: AvindexContentInfo (camera) vs ExifInterface (android)
- DateTime: TimeUtil (camera) vs GregorianCalendar (android)

## Build Config (open-memories-app template)
```gradle
// Root build.gradle
classpath 'com.android.tools.build:gradle:4.2.2'
// Repos: maven.google.com, repo1.maven.org/maven2, jitpack.io

// app/build.gradle
compileSdkVersion 16
buildToolsVersion '30.0.2'
ndkVersion '21.4.7075529'
minSdkVersion 10
targetSdkVersion 10
abiFilters 'armeabi-v7a', 'x86'
sourceCompatibility JavaVersion.VERSION_1_8
targetCompatibility JavaVersion.VERSION_1_8
cmake version '3.10.2'

// Dependencies
implementation 'com.github.ma1co.OpenMemories-Framework:framework:-SNAPSHOT'
compileOnly 'com.github.ma1co.OpenMemories-Framework:stubs:-SNAPSHOT'
implementation 'com.nanohttpd:nanohttpd:2.1.1'
```

## Project Template Checklist (new app from open-memories-app)
1. Copy project structure
2. Update applicationId in app/build.gradle
3. Extend BaseActivity for all activities
4. Register BootCompletedReceiver + ExitCompletedReceiver in manifest
5. Add permissions: CAMERA, WRITE_EXTERNAL_STORAGE, INTERNET, ACCESS_WIFI_STATE, CHANGE_WIFI_STATE
6. Call notifyAppInfo() in onResume, setAutoPowerOffMode(false)
7. Handle key events via ScalarInput in BaseActivity
8. Implement dual-device pattern for all Sony API usage
9. All layouts: 640x480 landscape
10. Test on emulator first, then camera
