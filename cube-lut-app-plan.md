# Plan: LUT Cube File App for Sony A6500

## Context
Build a standalone app that loads .cube 3D LUT files from the SD card root `/luts/` folder, provides a visual menu to select between LUTs (with preview thumbnails), and applies them to camera output. Uses ISP hardware for real-time preview approximation, and JNI trilinear 33³ interpolation at capture time for accurate output. Prioritize memory efficiency and stability.

## Design Decisions
- **New standalone app**: `cube-lut-app/` copied from open-memories-app template
- **Memory-first**: 33³ trilinear at capture (~431KB per LUT in native heap)
- **One LUT in memory at a time**: Switch = free old → load new
- **LUT storage**: SD card root `/luts/` folder, scanned on app resume
- **Only 33×33×33 .cube files**: Skip/ignore any other cube sizes
- **Preview thumbnails**: If `lut_name.jpg` exists alongside `lut_name.cube`, show it; otherwise show placeholder "No Preview"
- **Preview tool built separately**: A separate tool renders preview JPGs for LUT files (not part of this app)
- **Trilinear interpolation**: Standard accurate method for capture phase
- **Bundled fallback LUTs**: APK assets contain default .cube files (copied from `./luts/` at build time). If SD card `/luts/` folder is missing, empty, or inaccessible → fall back to bundled LUTs and show message "LUTs folder not found, showing default LUTs"
- **Dev setup**: Project contains a `./luts/` folder with test .cube files, linked to emulator virtual SD card for local testing

## LUT Folder Structure (SD Card Root)

```
/luts/
  cinematic_warm.cube        ← 33×33×33 .cube file (TITLE "Cinematic Warm", DESCRIPTION "Warm orange shadows")
  cinematic_warm.jpg         ← optional preview, MUST be 128×128 pixels
  film_negative.cube
  film_negative.jpg          ← 128×128 preview
  custom_grade.cube          ← no .jpg → shows "No Preview" placeholder
  bad_preview.cube
  bad_preview.jpg            ← 1920×1080 → shows "Preview should be 128x128"
  tiny_8x8.cube              ← not 33³ → ignored by scanner
```

### .cube File Header Example
```
# Created by DaVinci Resolve
TITLE "Cinematic Warm"
DESCRIPTION "Warm orange shadows with teal highlights"
LUT_3D_SIZE 33
DOMAIN_MIN 0.0 0.0 0.0
DOMAIN_MAX 1.0 1.0 1.0

0.003906 0.003906 0.003906
0.019775 0.003906 0.003906
...
```

## Project Structure

```
cube-lut-app/                           ← new project (copied from open-memories-app)
├── luts/                               ← test LUT files (linked to emulator SD card)
│   ├── example.cube
│   └── example.jpg
├── app/src/main/
│   ├── AndroidManifest.xml
│   ├── assets/luts/                    ← bundled fallback LUTs (copied from ./luts/ at build)
│   │   ├── example.cube
│   │   └── example.jpg
│   ├── java/com/github/up209d/cubelut/
│   │   ├── CubeLutApp.java            ← Main: camera preview + capture with LUT
│   │   ├── LutMenuActivity.java       ← LUT selection menu with preview thumbnails
│   │   ├── LutListAdapter.java        ← Custom adapter: filename + thumbnail/placeholder
│   │   ├── CubeParser.java            ← Parse .cube → float[] (validates 33³ only)
│   │   ├── LutDecomposer.java         ← 3D LUT → 1D curve + 3×3 matrix for ISP
│   │   ├── IspController.java         ← Apply/clear GammaTable + RGBMatrix on ISP
│   │   ├── LutProcessor.java          ← JNI bridge: loadLut, applyLut, freeLut
│   │   ├── LutFileManager.java        ← Scan /luts/ folder, pair .cube with .jpg
│   │   └── FileLogger.java            ← SD card logging for camera debugging
│   ├── cpp/
│   │   ├── CMakeLists.txt
│   │   └── lut-processor.cpp          ← Native trilinear 33³ LUT processing
│   └── res/
│       ├── layout/
│       │   ├── activity_main.xml       ← Camera preview + LUT name overlay text
│       │   └── activity_menu.xml       ← LUT list with thumbnails
│       │   └── lut_list_item.xml       ← Row: [thumbnail] [filename] [active indicator]
│       ├── drawable/
│       │   └── no_preview.png          ← Placeholder for LUTs without .jpg
│       └── values/strings.xml
├── app/build.gradle
├── build.gradle
├── settings.gradle
├── gradle.properties
└── gradle/wrapper/gradle-wrapper.properties
```

## Key Classes

### LutFileManager.java
- `scanLuts()`: Attempts to list `/luts/*.cube` on SD card
  - **Fallback logic**:
    1. Try SD card `/luts/` folder first
    2. If folder doesn't exist, is empty, or not accessible → fall back to bundled assets
    3. Show toast/overlay message: "LUTs folder not found, showing default LUTs"
    4. Bundled LUTs loaded from APK `assets/luts/` (same .cube files as project `./luts/` folder)
  - For each .cube: call `CubeParser.parseMetadataOnly()` to scan first 50 lines:
    - `LUT_3D_SIZE 33` — must be present, must be 33. Skip file otherwise
    - `TITLE` — extract LUT title string (optional, fallback to filename)
    - `DESCRIPTION` — extract LUT description string (optional, fallback to empty)
  - Check if matching `.jpg` exists (same base name)
    - Validate .jpg: must be exactly 128×128 pixels (check via BitmapFactory.Options.inJustDecodeBounds)
    - If .jpg exists but wrong size → show placeholder "Preview should be 128x128"
    - If .jpg missing → show placeholder "No Preview"
  - Returns `List<LutEntry>` where LutEntry = {filename, title, description, cubePath, jpgPath (nullable), previewValid (boolean), isBundled (boolean)}
- `isBundled` flag distinguishes SD card files from APK assets (affects how they're opened: File vs AssetManager)
- SD card path: `Environment.getExternalStorageDirectory() + "/luts/"`

### LutMenuActivity.java (LUT Browser Screen)
- Full-screen detailed LUT list opened via MENU key from main screen
- **Header bar**: "LUT Browser" title + source info ("Reading from: /sdcard/luts/" or "Using bundled LUTs") + total count ("12 LUTs found")
- **List**: ListView with custom adapter (LutListAdapter), scrollable
- **Each row layout** (row-by-row, horizontal):
  ```
  [Preview 80x80] | Title: "Cinematic Warm"           [✓ active]
                   | File: cinematic_warm.cube
                   | Desc: "Warm orange shadows..."
                   | Source: SD Card
  ```
- "None (No LUT)" as first item to disable LUT processing
- Navigation: Up/Down or touch scroll, Enter or tap to select, Delete to go back
- On select: saves to SharedPreferences, returns to main screen with result
- Highlights currently active LUT row

#### LUT Browser Screen Layout (640×480 landscape)
```
+------------------------------------------+
| LUT Browser    /sdcard/luts/  (12 found) |  <- header: title + source + count
|------------------------------------------|
| [  ---  ] None (No LUT)                  |  <- always first, disable LUT
|           Passthrough - no grading       |
|------------------------------------------|
| [Preview] Cinematic Warm          [*]    |  <- active LUT highlighted
|           cinematic_warm.cube            |
|           Warm orange shadows...  SD     |
|------------------------------------------|
| [Preview] Film Negative                  |
|           film_negative.cube             |
|           Classic film look...    SD     |
|------------------------------------------|
| [No Prev] Custom Grade                   |
|           custom_grade.cube              |
|           (no description)        SD     |
|------------------------------------------|
| [Preview] Default Warm                   |  <- bundled LUT
|           default_warm.cube              |
|           Built-in warm preset  Bundled  |
+------------------------------------------+
  UP/DOWN = scroll | ENTER/TAP = select | DELETE = back
```

Each row shows:
- Left: 80×80 preview area. Three states:
  - **Has valid .jpg**: Show thumbnail image
  - **No .jpg**: Random dark color background (seeded from filename hash for consistency), large centered "No Preview" text, small centered abbreviation below (first letters of each word from title, e.g., "CW" for "Cinematic Warm")
  - **Wrong size .jpg**: Same as no .jpg but text says "128x128!"
- Right top: LUT title (from TITLE metadata, fallback: filename)
- Right middle: .cube filename
- Right bottom: description (from DESCRIPTION) + source tag ("SD" or "Bundled")
- Far right: [*] marker if this is the currently active LUT

### LutListAdapter.java
- Custom BaseAdapter for 640×480 landscape layout
- Each row: [thumbnail 80×80] [title + filename] [description] [✓ if active]
- Title from .cube TITLE metadata (fallback: filename without extension)
- Description from .cube DESCRIPTION metadata (fallback: empty)
- Loads .jpg thumbnail only if previewValid == true (already validated as 128×128)
  - Decode with inSampleSize=2 (128→64px displayed at 80×80) to minimize memory
- Shows "No Preview" placeholder when no valid .jpg
- Shows "Preview should be 128x128" placeholder when .jpg exists but wrong dimensions
- Highlights currently active LUT

### CubeParser.java
- Streaming parser: reads .cube line by line
- **Header scan** (first 50 lines): extracts metadata keywords
  - `LUT_3D_SIZE 33` — required, validates size == 33, rejects otherwise
  - `TITLE "..."` — optional, extracts LUT title string
  - `DESCRIPTION "..."` — optional, extracts LUT description string
  - `DOMAIN_MIN`, `DOMAIN_MAX` — optional, for range validation
  - `#` comment lines — skipped
- **Data section**: 3 space-separated floats per line (R G B, 0.0-1.0 range)
- `parseMetadataOnly()`: quick scan for TITLE/DESCRIPTION/LUT_3D_SIZE without reading data (used by LutFileManager during folder scan)
- `parseFull()`: reads metadata + all data lines, outputs `float[107811]` (33³ × 3) flat array
- Memory: ~431KB for the float array, no intermediate string buffering

### LutDecomposer.java
- Input: float[] cube data, cube size
- Sample R/G/B axis curves at neutral midpoint → 3 curves of 33 points each
- Interpolate to 1024 points for GammaTable (average of 3 channels since GammaTable is single-curve)
- Fit 3×3 matrix by sampling ~100 points from cube, least-squares regression
- Output: `short[1024]` gammaCurve, `int[9]` rgbMatrix

### IspController.java
- `apply(short[] curve, int[] matrix)`:
  - Creates GammaTable, sets PictureEffectGammaForceOff, writes curve, applies
  - Sets RGBMatrix via ParametersModifier
- `clear()`:
  - setExtendedGammaTable(null), setRGBMatrix(null)
- Wraps all calls with DeviceInfo.isCamera() check (dual-device pattern)

### CubeLutApp.java (Main Activity)
- Extends BaseActivity
- `onResume()`: opens CameraEx, loads last-selected LUT from preferences, applies ISP preview
- `onActivityResult()`: receives selected LUT, reloads & applies
- Shows LUT name + "RAW: ON/OFF" as touchable text overlays on camera preview
- "None" state: no LUT loaded, ISP cleared, capture is passthrough

#### Dual Input: Touch + Hardware Buttons
All actions accessible via BOTH touchscreen and physical camera buttons:

| Action | Hardware Button | Touch Gesture |
|--------|----------------|---------------|
| Open LUT menu | MENU key | Tap LUT name overlay |
| Capture photo | Shutter S2 | (physical only) |
| Toggle Keep RAW | FN key | Tap "RAW: ON/OFF" overlay |
| Toggle Preview | AEL key | Tap "PREVIEW: ON/OFF" overlay |
| Quick-cycle LUTs | Dial 1 rotation | Swipe left/right on preview |
| Open Help screen | C1 key | Tap "?" help button overlay |
| Back / Cancel | DELETE key | Tap back arrow overlay |
| Autofocus | Half-press S1_1 | Tap on preview area |

#### Main Screen Layout (640×480 landscape)
Match the A6500 native UI style: semi-transparent dark overlays (#000000 ~60% opacity), white text, compact icons positioned around the edges. Live preview fills the entire screen with overlays on top.

```
+------------------------------------------+
| [LUT: Cinematic Warm]      [PRV] [RAW]  |  <- top bar: LUT name + toggles
|                                          |
|                                          |
|          Full Camera Live Preview        |
|         (ISP LUT grading if PRV ON)      |
|                                          |
|                                          |
| [< prev LUT]               [next LUT >]  |  <- LUT nav (touch or dial)
| [?]                    LUT: 2/5 queued   |  <- help (left) + status (right)
+------------------------------------------+

Toggle states shown as:
  [PRV] = semi-transparent when OFF, highlighted when ON
  [RAW] = semi-transparent when OFF, red dot when ON
```

**UI Style Rules** (match A6500 native feel):
- **Theme**: Dark mode only. Sony camera apps always use black/dark backgrounds. The camera has no light/dark mode toggle — it's always dark. Our app matches this.
- Background: black (main screen = live preview fills it; menu screen = #000000 solid)
- Overlay text: white, ~12-14sp, semi-bold
- Overlay background: #000000 at 50-60% opacity, rounded corners
- Touch targets: min 48×48dp for reliable touch on the 3" screen
- No borders/outlines — just text on semi-transparent pills
- Active toggles: brighter/highlighted background
- Queue status text: only visible when pendingCount > 0

#### Focus Mode Handling
- **Inherit camera's focus mode**: Do NOT override. Respect whatever AF-S/AF-C/DMF/MF the user has set on the physical focus mode dial
- Half-press shutter (S1_1) or touch-on-preview → triggers `startOneShotFocusDrive()` (same as all reference apps)
- Touch AF: tap on preview area sends touch coordinates for focus point (standard Android Camera API `setFocusAreas()`)
- Focus mode dial changes (key code 597) → detected via BaseApp framework, focus controller updates automatically
- Focus magnification: available via physical controls (same as native camera behavior)
- The app is purely a color grading tool — it should be transparent to focus behavior

#### Two-Screen UX Flow
1. **Main Screen** (CubeLutApp): Full live preview with overlay controls for quick LUT cycling, toggle RAW/Preview, help. Dial or swipe cycles through LUTs instantly.
2. **LUT Browser Screen** (LutMenuActivity): Full detailed list view opened via MENU key. Shows all LUTs with preview thumbnails, titles, descriptions, source (bundled/SD card), total count, and active folder path.

#### Preference Persistence
- All settings saved to SharedPreferences: selected LUT path, Keep RAW, Preview ON/OFF
- On app start: restore last state. If saved LUT file no longer exists (deleted/removed), fall back to first bundled LUT and show toast "Previous LUT not found, using default"
- Bundled LUTs always available as fallback — cannot be deleted

#### Help Screen
A simple overlay/activity showing all controls. Triggered by C1 button or tap "?" button.

```
+------------------------------------------+
|              CUBE LUT HELP               |
|                                          |
|  MENU     - Open LUT selection           |
|  SHUTTER  - Capture with LUT applied     |
|  FN       - Toggle Keep RAW (ON/OFF)     |
|  AEL      - Toggle Preview (ON/OFF)      |
|  DIAL     - Quick-cycle through LUTs     |
|  C1 / ?   - This help screen             |
|  DELETE   - Back / Exit                   |
|                                          |
|  Touch: Tap overlays to toggle settings  |
|         Swipe L/R to cycle LUTs          |
|         Tap preview to autofocus         |
|                                          |
|  LUTs folder: /sdcard/luts/              |
|  Preview JPG: 128x128, same filename     |
|                                          |
|  Long-press C1: Toggle debug overlay     |
|  Logs saved to: /sdcard/UPCubeLutApp/logs|
|                                          |
|            [OK - Close]                  |
+------------------------------------------+
```
Dismissed by Enter key, DELETE key, or tap "OK".

#### LutMenuActivity Touch Support
- ListView items are touchable (tap to select) in addition to D-pad/Enter
- Scroll via touch drag or Up/Down keys
- Back via touch back button or DELETE key

### lut-processor.cpp (Native)
```cpp
static float* g_lut = NULL;
static int g_lut_size = 0;

// Called once per LUT change (~431KB malloc)
void Java_..._loadLut(JNIEnv*, jobject, jfloatArray data, jint size);

// Called per capture: iterates OptimizedImage YUV pixels
// YUV→RGB → trilinear 33³ lookup → RGB→YUV → write back
void Java_..._applyLut(JNIEnv*, jobject, jbyteArray imageData, jint width, jint height);

// Called on LUT switch or app exit
void Java_..._freeLut(JNIEnv*, jobject);
```

Trilinear interpolation: 8 corner reads + 7 lerps per pixel. BT.601 YUV↔RGB conversion coefficients. No NEON SIMD (stability first).

### LutProcessor.java (JNI Bridge)
```java
public class LutProcessor {
    static { System.loadLibrary("lut-processor"); }
    public static native void loadLut(float[] lutData, int cubeSize);
    public static native void applyLut(byte[] imageData, int width, int height);
    public static native void freeLut();
    public static native boolean isLoaded();
}
```

## Capture Pipeline

```
1. ISV_KEY_S2 (shutter) → onShutterKeyDown()
2. CameraSequence.ShutterSequenceCallback.onShutterSequence(RawData)
3. If "Keep RAW" toggle ON:
   DefaultDevelopFilter.setRawFileStoreEnabled(true) → RAW saved to SD card
4. DefaultDevelopFilter: RAW → OptimizedImage
   (ISP has our GammaTable + RGBMatrix active = approximate preview-quality grade)
5. If LUT loaded (LutProcessor.isLoaded()):
   a. Extract pixel data from OptimizedImage via DeviceBuffer
   b. JNI applyLut(): trilinear 33³ on every pixel (~300-500ms)
   c. Write corrected data back to OptimizedImage
6. sequence.storeImage(image, true) → LUT-applied JPEG on SD card
```

### Capture Rate Limiting & Burst Support

**Key insight**: The A6500's native pipeline (RAW → develop → JPEG store) handles 20-30 shot bursts with its own hardware buffer queue (~1-2s per image). The bottleneck is NOT the camera hardware — it's our JNI LUT processing step (~300-500ms) that we insert between development and storage.

**Architecture: Non-blocking capture with background LUT processing**

```
Shutter press → CameraSequence captures RAW (hardware, instant)
             → DefaultDevelopFilter develops (hardware, fast)
             → Queue developed OptimizedImage to LUT worker thread
             → Camera is FREE to accept next shutter press immediately
             → Worker thread: JNI applyLut() → storeImage() (background, ~300-500ms)
```

- **Shutter is never blocked**: Captures always go through immediately. The camera's hardware pipeline handles buffering.
- **LUT worker thread**: Single dedicated thread with a FIFO queue of OptimizedImages waiting for LUT processing.
- **Queue depth**: Max 5 pending LUT jobs.
- **Single shot mode**: No issue — user manually presses shutter each time, natural pacing.
- **Burst mode (holding shutter)**: When queue reaches 5, PAUSE capture (stop accepting new frames). When a slot frees up (one LUT job completes), automatically resume capturing. User keeps holding shutter → capture resumes seamlessly. This creates a natural "capture → pause → capture" rhythm matching LUT processing speed.
- **Display**: Show "LUT: 3/5" overlay showing queue fill level. When paused: "LUT queue full - processing..."
- **On queue drain**: overlay clears, burst resumes if shutter still held.

**Memory consideration:**
- Each queued OptimizedImage: managed by Sony framework's buffer pool (not our heap)
- LUT data: 431KB shared (one copy)
- Worker thread: minimal stack overhead
- The camera's buffer pool handles the heavy lifting — we just hold references

**Safety:**
- If JNI processing fails for any queued image → log error, store un-graded image (never lose a shot)
- Log memory at queue high-water marks
- If `Runtime.freeMemory() < 5MB` → skip LUT, store directly, log warning

**Pipeline note**: DefaultDevelopFilter (step 4) applies ALL inherited camera settings: lens correction, white balance, tone mapping, contrast/saturation — same as a normal camera JPEG. Our LUT is applied AFTER development (step 5), which is the correct order: lens correction → WB → tone → LUT color grading. We do not modify lens correction settings — inherited from user's camera config.

### LUT Metadata Embedding (for future re-grading app)
Every capture writes LUT info into the JPEG's EXIF UserComment tag (tag 0x9286).

**Implementation**: After `storeImage()` completes, use JNI to open the saved JPEG file and write the EXIF UserComment field:
```
EXIF UserComment content (UTF-8):
CUBELUT|cinematic_warm.cube|Cinematic Warm|Warm orange shadows|RAW=true
```
Format: pipe-delimited string with fields: app identifier, LUT filename, title, description, RAW flag.

**EXIF writer** using Apache Commons Imaging (pure Java, API 16 compatible, ~200KB JAR):
```gradle
implementation 'org.apache.commons:commons-imaging:1.0-alpha2'
```
```java
// Write UserComment to saved JPEG
File jpeg = new File(jpegPath);
TiffOutputSet outputSet = /* read existing EXIF from file */;
TiffOutputDirectory exifDir = outputSet.getOrCreateExifDirectory();
exifDir.removeField(ExifTagConstants.EXIF_TAG_USER_COMMENT);
exifDir.add(ExifTagConstants.EXIF_TAG_USER_COMMENT, comment);
// Rewrite EXIF (modifies only metadata, not image data)
ExifRewriter rewriter = new ExifRewriter();
rewriter.updateExifMetadataLossless(jpeg, outputJpeg, outputSet);
```
- Pure Java — no JNI/native complexity for this part
- Lossless EXIF update — does NOT re-encode image data
- If write fails → log error, don't fail capture (image already saved)
- **Performance**: ~10-20ms per file (slightly slower than JNI but still negligible vs 300-500ms LUT). Runs on worker thread, non-blocking
- **Fallback**: If library causes issues on camera's Android 4.x, we can strip it and skip EXIF writing (LUT info lost but captures still work)

**Future app** can read this tag with any EXIF library to identify which LUT was applied and re-grade from RAW if available.

### "Keep RAW" Toggle
- User-accessible option in menu (alongside LUT selection)
- Saved to SharedPreferences, persists across sessions
- When ON: saves both RAW + LUT-applied JPEG per capture
- When OFF: saves only LUT-applied JPEG (default)
- Status on main screen: small text overlay "RAW: ON" / "RAW: OFF"
- Uses `DefaultDevelopFilter.setRawFileStoreEnabled(boolean)` (from time-lapse app)

### "Preview" Toggle (ISP Preview ON/OFF)
- User-accessible toggle on main screen
- Saved to SharedPreferences, persists across sessions
- When ON: ISP GammaTable + RGBMatrix applied = approximate live color grading preview (uses hardware resources)
- When OFF: ISP cleared, live view shows neutral/ungraded image (saves hardware performance and battery)
- In BOTH modes, the final capture still applies the full 3D LUT via JNI — the toggle only affects live preview
- Status on main screen: "PREVIEW: ON" / "PREVIEW: OFF"
- Default: OFF (saves resources; preview is approximate anyway)

## Logging Strategy

### On Emulator (ADB available)
- Standard `adb logcat` for real-time log streaming
- `adb shell dumpsys meminfo` for memory analysis
- Filter: `adb logcat -s CubeLUT:* MEM:* LUT:*`

### On Camera (NO ADB - isolated device)
The Sony camera has no USB debug connection while running apps. All logs must be written to SD card.

**SD Card Log File**: `/sdcard/UPCubeLutApp/logs/`
- One log file PER app session: `run_2026-03-15_14-30-05.log` (timestamped at app start)
- New file created each time app launches; preserved on crash/exit
- Old log files kept on SD card for investigation (user can delete manually)
- Written by a custom `FileLogger` utility class
- Captures: app lifecycle, LUT load/switch events, capture pipeline steps, errors/crashes, memory snapshots

**FileLogger.java** (new utility class):
```java
// Usage: FileLogger.log("LUT", "Loaded cinematic_warm.cube (431KB, 28ms)");
//        FileLogger.logMemory("CAPTURE_START");
//        FileLogger.logError("PARSE", e);
```
- Timestamps every entry: `[2026-03-15 14:30:05.123] [LUT] Loaded cinematic_warm.cube`
- `logMemory(tag)`: logs used/free/max heap + native heap size
- `logError(tag, Exception)`: logs full stack trace
- Writes to SD card in background (async, doesn't block capture pipeline)
- On app start: logs device info, available memory, SD card free space

**Crash Handler**: Set `Thread.setDefaultUncaughtExceptionHandler()` to capture crashes to log file before app dies. Includes stack trace + memory state at crash time.

**Debug Overlay** (toggled via long-press C1 on camera):
- Shows on-screen: current memory usage, last log lines, FPS
- Useful for quick visual debugging without pulling SD card

**Log Retrieval Workflow**:
1. Use app on camera, reproduce issue
2. Power off camera
3. Remove SD card → insert in PC
4. Read `/UPCubeLutApp/logs/run_2026-03-15_14-30-05.log` (find the session by timestamp)

## Emulator Testing Setup

For local development, link the project's `./luts/` folder to the emulator's virtual SD card:
```bash
# Push test files to emulator SD card
adb push ./luts/ /sdcard/luts/
```

Or configure emulator to mount `./luts/` as part of SD card storage.

## Files to Reuse

| Source | File | What to Reuse |
|--------|------|---------------|
| open-memories-app | BaseActivity.java | Key event handling (extend) |
| open-memories-app | ListAdapter.java | Reference for LutListAdapter |
| open-memories-app | native-lib.cpp | JNI setup pattern (rewrite internals) |
| open-memories-app | CMakeLists.txt | NDK build template |
| open-memories-app | build.gradle (both) | Exact build config |
| open-memories-app | AndroidManifest.xml | Boot/exit receivers, permissions |
| live-view-grading | LVGEffectValueController.java | GammaTable creation + write pattern |
| live-view-grading | ColorGradingPresetSelectionScreen.java | Menu selection pattern |

## Implementation Order

1. Copy open-memories-app → cube-lut-app, update package/applicationId
2. Create LutFileManager + CubeParser (file scanning + parsing)
3. Create lut-processor.cpp with loadLut/applyLut/freeLut (native trilinear)
4. Create LutProcessor.java JNI bridge
5. Create LutDecomposer + IspController (ISP preview pipeline)
6. Create LutMenuActivity + LutListAdapter (menu UI with thumbnails)
7. Create CubeLutApp main activity (camera + capture + menu integration)
8. Create ./luts/ folder with test .cube files
9. Test on emulator, then camera

## Dual-Device Testing Strategy

### Emulator (Android 7.1.1 x86 API 25, 640×480 landscape)
- `DeviceInfo.getInstance().isCamera()` returns `false`
- OpenMemories-Framework stubs handle all Sony API calls without crashing:
  - `CameraEx` → uses standard Android Camera API (or dummy if no camera on emulator)
  - `GammaTable`, `setExtendedGammaTable()`, `setRGBMatrix()` → no-ops (ISP preview won't show grading, but won't crash)
  - `CameraSequence`, `DefaultDevelopFilter` → stubbed (capture pipeline uses standard Android capture)
  - `ScalarInput` key constants → mapped to standard Android key events
- **What DOES work on emulator**: Menu UI, .cube file parsing, LUT selection, JNI trilinear processing, file management, preferences
- **What is stubbed**: ISP preview grading, CameraSequence capture, hardware button mapping
- For JNI testing on emulator: capture a photo via standard Android Camera API → apply LUT in JNI → save → verify colors

### IspController Dual-Device Guard
```java
public void apply(short[] curve, int[] matrix) {
    if (!DeviceInfo.getInstance().isCamera()) {
        // On emulator: skip ISP calls, log instead
        Log.d(TAG, "ISP apply skipped (not camera device)");
        return;
    }
    // On camera: real ISP calls
    GammaTable table = cameraEx.createGammaTable();
    // ...
}
```

### CubeLutApp Capture Pipeline Dual-Device
```java
// On camera: CameraSequence → DefaultDevelopFilter → JNI applyLut → storeImage
// On emulator: Standard Camera.takePicture() → decode JPEG → JNI applyLut → save to file
```

Both paths exercise the JNI LUT processing — the only difference is how the input image is obtained.

## Verification

1. **Parse test**: Load a .cube file, verify float[] has correct values at known indices
2. **Scan test**: Put mix of 33³ and non-33³ .cube files in /luts/, verify only 33³ appear in menu
3. **Preview test**: .cube with .jpg shows thumbnail; .cube without .jpg shows placeholder
4. **Identity test**: Load identity LUT (all R,G,B = input), verify capture output unchanged
5. **JNI test**: Apply known LUT, compare pixel values against reference (ffmpeg/DaVinci)
6. **ISP preview test**: Verify live view changes color when LUT selected
7. **Memory test**: Switch LUTs 20+ times, verify no memory leak. Use `Runtime.getRuntime()` to log memory usage:
   ```java
   long used = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
   long max = Runtime.getRuntime().maxMemory();
   Log.d("MEM", "Used: " + (used/1024) + "KB / Max: " + (max/1024) + "KB");
   ```
   Also use `adb shell dumpsys meminfo <package>` from host PC for detailed breakdown.

   **Memory budget check**: Log at key moments (app start, LUT load, capture, LUT switch) to verify:
   - Baseline (no LUT): should be under camera's heap limit
   - With LUT loaded: +~431KB (33³×3 floats in native heap)
   - During capture: temporary spike for OptimizedImage buffer (managed by Sony framework)
   - After capture: returns to baseline + LUT

   **Camera heap limit**: A6500 likely has 48-64MB max heap. Our app should stay well under 20MB total.
   Add a debug overlay (toggled via long-press C1) showing real-time memory usage on screen during testing
8. **Stability test**: Capture 50 photos with LUT, no crashes
9. **Emulator**: `adb push ./luts/ /sdcard/luts/` → app shows files in menu
10. **Camera**: Deploy to A6500, verify real ISP + capture pipeline
11. **Keep RAW toggle**: Verify RAW file appears on SD card when ON, absent when OFF

## App Icon

Design a retro skeuomorphic camera icon (mimic style from One-Layer-Style-Retro-Camera-Icon-PSD.jpg):
- **Shape**: Rounded square
- **Top half**: Brushed silver/aluminum gradient
- **Bottom half**: Warm wood grain (brown/amber gradient with vertical grain lines)
- **Center**: Camera lens — concentric dark gray rings with blue/cyan reflective glass center
- **Border**: Thin white outline with subtle drop shadow
- **Differentiator**: Small "LUT" text or color cube overlay on the lens to distinguish from a generic camera icon

Generate as SVG, then export to required Android icon sizes:
- `res/drawable-mdpi/ic_launcher.png` (48×48)
- `res/drawable-hdpi/ic_launcher.png` (72×72)
- `res/drawable-xhdpi/ic_launcher.png` (96×96)
- `res/drawable-xxhdpi/ic_launcher.png` (144×144)

Note: Camera screen is 640×480 so mdpi/hdpi are the primary sizes needed.

## Documentation (Final Step)

### README.md for cube-lut-app
Create `cube-lut-app/README.md` covering:
- **What it does**: Apply .cube 3D LUT color grading to Sony A6500 camera output
- **Features**: LUT selection menu with thumbnails, real-time ISP preview, accurate capture via JNI trilinear, Keep RAW toggle, fallback to bundled LUTs
- **LUT folder setup**: Place .cube files in `/luts/` on SD card root, optional 128×128 .jpg preview with same name
- **Supported format**: .cube files with `LUT_3D_SIZE 33` only
- **How it works**: Technical overview of the dual pipeline (ISP preview vs JNI capture)
- **Button controls**: Menu = LUT selection, Dial = quick cycle, Shutter = capture, Fn = toggle Keep RAW
- **Building**: Gradle build instructions, emulator setup, `adb push ./luts/` for testing
- **Architecture**: Class diagram / file list with one-line descriptions
- **Limitations**: ISP preview is approximate (~2×2×2 to 3×3×3 equivalent), capture adds ~300-500ms

## Confidence Assessment

| Component | Confidence | Risk | Notes |
|-----------|-----------|------|-------|
| .cube parsing | 95% | Low | Standard text format, well-defined spec |
| Menu UI + navigation | 90% | Low | Follows proven reference app patterns |
| JNI trilinear LUT | 90% | Low | Pure math, tested pattern exists in open-memories-app |
| File scanning / SD card | 85% | Low-Med | SD card access well-tested in reference apps |
| ISP GammaTable + RGBMatrix | 80% | Medium | API calls proven in live-view-grading, but decomposition accuracy is approximate |
| CameraSequence capture pipeline | 75% | Medium | Proven pattern but our LUT injection step is new — untested on real hardware |
| Burst queue with background LUT | 70% | Medium-High | Concurrent OptimizedImage handling untested. May need to reduce queue depth if memory issues arise |
| EXIF UserComment writing | 80% | Low-Med | Apache Commons Imaging library (pure Java, lossless EXIF update). Fallback: skip EXIF if library fails on Android 4.x |
| Touch AF integration | 70% | Medium | Standard Android Camera API but Sony may handle it differently |

**Overall**: ~80% confident for first working version. Highest risk areas are the capture pipeline integration and burst queue — these need real camera testing early. Emulator testing covers ~60% of functionality (UI, parsing, JNI math). The remaining 40% (ISP, CameraSequence, hardware buttons) can only be validated on the A6500.

### Update project-level docs
- **CLAUDE.md**: Add `./cube-lut-app/` to directory structure section with description
- **memory/MEMORY.md**: Add cube-lut-app to directory map
- **Bible.md**: No changes needed (APIs already documented)

### Save project context for future sessions
Create `cube-lut-app/PROJECT.md` — the definitive catch-up document for any new Claude session:
- Link to this plan file
- Current implementation status (which classes are done, which are pending)
- Key design decisions and WHY (memory-first, 33³ trilinear, ISP preview approximate, burst queue)
- Known risks and mitigations
- Testing status (what's been verified, what hasn't)
- Open questions / TODO items

Also create a memory file at `memory/cube-lut-app.md`:
```markdown
---
name: cube-lut-app
description: Cube LUT color grading app for Sony A6500 - 3D LUT from .cube files, ISP preview + JNI capture
type: project
---
Active project: cube-lut-app/ in sony-apps workspace.
**Why:** Apply .cube 3D LUT color grading to Sony A6500 camera output.
**How to apply:** Read cube-lut-app/PROJECT.md and the plan at .claude/plans/witty-forging-bachman.md for full context. Key architecture: ISP hardware for approximate live preview (1D GammaTable + 3x3 RGBMatrix), JNI trilinear 33³ interpolation for accurate capture. LUT files on SD card /luts/ folder with 128x128 .jpg previews.
```
Update `memory/MEMORY.md` index to point to this file.
