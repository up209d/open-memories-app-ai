package com.github.up209d.cubelut;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ma1co.openmemories.framework.DeviceInfo;
import com.sony.scalar.hardware.CameraEx;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.LinkedBlockingQueue;

public class CubeLutApp extends BaseActivity implements SurfaceHolder.Callback {
    private static final String TAG = "CubeLutApp";
    private static final int REQUEST_LUT_SELECT = 1;
    private static final int MAX_QUEUE_DEPTH = 5;
    private static final String PREFS_NAME = "CubeLutPrefs";
    private static final String PREF_LUT_PATH = "lut_path";
    private static final String PREF_LUT_INDEX = "lut_index";
    private static final String PREF_KEEP_RAW = "keep_raw";
    private static final String PREF_PREVIEW_ON = "preview_on";

    // Camera
    private SurfaceHolder surfaceHolder;
    private CameraEx cameraEx;
    private Camera normalCamera;

    // LUT state
    private LutFileManager fileManager;
    private List<LutEntry> lutEntries;
    private int activeLutIndex = -1; // -1 = no LUT
    private CubeParser.LutData activeLutData;

    // ISP preview
    private IspController ispController;
    private boolean previewOn;

    // Capture state
    private boolean keepRaw;
    private boolean capturing;
    private final LinkedBlockingQueue<CaptureJob> captureQueue = new LinkedBlockingQueue<CaptureJob>(MAX_QUEUE_DEPTH);
    private Thread workerThread;
    private volatile boolean workerRunning;

    // UI
    private TextView lutNameOverlay;
    private TextView prvToggle;
    private TextView rawToggle;
    private TextView prevLutBtn;
    private TextView nextLutBtn;
    private TextView helpBtn;
    private TextView queueStatus;

    // Debug overlay
    private TextView debugOverlay;
    private boolean debugVisible;
    private long c1PressTime;

    private static class CaptureJob {
        byte[] imageData;
        int width;
        int height;
        String lutFilename;
        String lutTitle;
        String lutDescription;
        boolean keepRaw;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FileLogger.init();
        FileLogger.installCrashHandler();
        FileLogger.log("APP", "CubeLutApp onCreate");

        setContentView(R.layout.activity_main);

        // Surface for camera preview
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        // UI overlays
        lutNameOverlay = (TextView) findViewById(R.id.lut_name);
        prvToggle = (TextView) findViewById(R.id.prv_toggle);
        rawToggle = (TextView) findViewById(R.id.raw_toggle);
        prevLutBtn = (TextView) findViewById(R.id.prev_lut);
        nextLutBtn = (TextView) findViewById(R.id.next_lut);
        helpBtn = (TextView) findViewById(R.id.help_btn);
        queueStatus = (TextView) findViewById(R.id.queue_status);
        debugOverlay = (TextView) findViewById(R.id.debug_overlay);

        // Touch handlers
        lutNameOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { openLutMenu(); }
        });
        prvToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { togglePreview(); }
        });
        rawToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { toggleKeepRaw(); }
        });
        prevLutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { cycleLut(-1); }
        });
        nextLutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { cycleLut(1); }
        });
        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { openHelp(); }
        });

        // Touch-to-focus on preview area
        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP && normalCamera != null) {
                    try {
                        normalCamera.autoFocus(null);
                    } catch (Exception e) {
                        // Ignore if AF not available
                    }
                }
                return true;
            }
        });

        // Init managers
        fileManager = new LutFileManager(this);
        ispController = new IspController();

        // Load preferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        keepRaw = prefs.getBoolean(PREF_KEEP_RAW, false);
        previewOn = prefs.getBoolean(PREF_PREVIEW_ON, false);
        activeLutIndex = prefs.getInt(PREF_LUT_INDEX, -1);

        // Start capture worker thread
        startWorkerThread();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAutoPowerOffMode(false);
        FileLogger.log("APP", "onResume");
        FileLogger.logMemory("RESUME");

        // Scan LUTs
        lutEntries = fileManager.scanLuts();
        if (fileManager.isUsingBundled()) {
            Toast.makeText(this, "LUTs folder not found, showing default LUTs",
                    Toast.LENGTH_LONG).show();
        }

        // Validate saved LUT index
        if (activeLutIndex >= lutEntries.size()) {
            activeLutIndex = lutEntries.isEmpty() ? -1 : 0;
            Toast.makeText(this, "Previous LUT not found, using default",
                    Toast.LENGTH_SHORT).show();
        }

        // Open camera (dual-device: CameraEx on Sony, standard Camera on emulator)
        try {
            if (isCamera()) {
                cameraEx = CameraEx.open(0, null);
                normalCamera = cameraEx.getNormalCamera();
                ispController.setCameraEx(cameraEx);
                FileLogger.log("CAMERA", "Opened CameraEx (Sony device)");
            } else {
                normalCamera = Camera.open(0);
                FileLogger.log("CAMERA", "Opened standard Camera (non-Sony device)");
            }
            surfaceHolder.addCallback(this);
        } catch (Exception e) {
            FileLogger.logError("CAMERA", e);
            Toast.makeText(this, "Failed to open camera", Toast.LENGTH_LONG).show();
        }

        // Load active LUT
        if (activeLutIndex >= 0 && activeLutIndex < lutEntries.size()) {
            loadLut(activeLutIndex);
        }

        updateUI();
    }

    @Override
    protected void onPause() {
        FileLogger.log("APP", "onPause");
        super.onPause();

        // Clear ISP
        ispController.clear();

        // Release camera
        surfaceHolder.removeCallback(this);
        if (cameraEx != null) {
            cameraEx.release();
            cameraEx = null;
            normalCamera = null;
        } else if (normalCamera != null) {
            normalCamera.stopPreview();
            normalCamera.release();
            normalCamera = null;
        }

        // Save preferences
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putInt(PREF_LUT_INDEX, activeLutIndex);
        editor.putBoolean(PREF_KEEP_RAW, keepRaw);
        editor.putBoolean(PREF_PREVIEW_ON, previewOn);
        if (activeLutIndex >= 0 && activeLutIndex < lutEntries.size()) {
            editor.putString(PREF_LUT_PATH, lutEntries.get(activeLutIndex).cubePath);
        } else {
            editor.putString(PREF_LUT_PATH, "");
        }
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        FileLogger.log("APP", "onDestroy");
        stopWorkerThread();
        try {
            LutProcessor.freeLut();
        } catch (Exception e) {
            // Native lib may not be available
        }
        super.onDestroy();
    }

    // ---- SurfaceHolder.Callback ----

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (normalCamera != null) {
            try {
                normalCamera.setPreviewDisplay(holder);
                normalCamera.startPreview();
                FileLogger.log("CAMERA", "Preview started");
            } catch (IOException e) {
                FileLogger.logError("CAMERA", e);
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {}

    // ---- Key Handlers ----

    @Override
    protected boolean onMenuKeyDown() {
        openLutMenu();
        return true;
    }

    @Override
    protected boolean onShutterKeyDown() {
        capturePhoto();
        return true;
    }

    @Override
    protected boolean onShutterKeyUp() {
        return true;
    }

    @Override
    protected boolean onFnKeyDown() {
        toggleKeepRaw();
        return true;
    }

    @Override
    protected boolean onAelKeyDown() {
        togglePreview();
        return true;
    }

    @Override
    protected boolean onFocusKeyDown() {
        if (normalCamera != null) {
            try {
                normalCamera.autoFocus(null);
            } catch (Exception e) {
                // Ignore
            }
        }
        return true;
    }

    @Override
    protected boolean onFocusKeyUp() {
        if (normalCamera != null) {
            try {
                normalCamera.cancelAutoFocus();
            } catch (Exception e) {
                // Ignore
            }
        }
        return true;
    }

    @Override
    protected boolean onUpperDialChanged(int value) {
        cycleLut(value > 0 ? 1 : -1);
        return true;
    }

    @Override
    protected boolean onC1KeyDown() {
        c1PressTime = System.currentTimeMillis();
        return true;
    }

    // Handle C1 key up for help vs debug toggle
    @Override
    public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        try {
            if (event.getScanCode() == com.sony.scalar.sysutil.ScalarInput.ISV_KEY_CUSTOM1) {
                long pressDuration = System.currentTimeMillis() - c1PressTime;
                if (pressDuration > 1000) {
                    toggleDebugOverlay();
                } else {
                    openHelp();
                }
                return true;
            }
        } catch (Exception e) {
            // ScalarInput may not be available
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected boolean onDeleteKeyDown() {
        return true;
    }

    @Override
    protected boolean onDeleteKeyUp() {
        finish();
        return true;
    }

    // ---- LUT Management ----

    private void loadLut(int index) {
        if (index < 0 || index >= lutEntries.size()) {
            clearLut();
            return;
        }

        LutEntry entry = lutEntries.get(index);
        FileLogger.log("LUT", "Loading: " + entry.filename);
        long startTime = System.currentTimeMillis();

        InputStream is = null;
        try {
            is = fileManager.openLutStream(entry);
            CubeParser.LutData data = CubeParser.parseFull(is);
            if (data.data != null && data.metadata.valid) {
                // Free previous and load new into native memory
                LutProcessor.loadLut(data.data, 33);
                activeLutData = data;
                activeLutIndex = index;

                long elapsed = System.currentTimeMillis() - startTime;
                FileLogger.log("LUT", "Loaded " + entry.filename + " ("
                        + (data.data.length * 4 / 1024) + "KB, " + elapsed + "ms)");
                FileLogger.logMemory("LUT_LOADED");

                // Apply ISP preview if enabled
                if (previewOn) {
                    applyIspPreview();
                }
            } else {
                FileLogger.log("LUT", "Failed to parse: " + entry.filename);
                Toast.makeText(this, "Failed to load LUT: " + entry.filename,
                        Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            FileLogger.logError("LUT", e);
        } finally {
            if (is != null) {
                try { is.close(); } catch (IOException e) { /* ignore */ }
            }
        }
    }

    private void clearLut() {
        LutProcessor.freeLut();
        activeLutIndex = -1;
        activeLutData = null;
        ispController.clear();
        FileLogger.log("LUT", "Cleared");
    }

    private void cycleLut(int direction) {
        if (lutEntries == null || lutEntries.isEmpty()) return;

        int total = lutEntries.size();
        if (activeLutIndex == -1) {
            // Currently "None" - go to first or last
            activeLutIndex = direction > 0 ? 0 : total - 1;
        } else {
            int next = activeLutIndex + direction;
            if (next < -1) next = total - 1;
            if (next >= total) next = -1;
            activeLutIndex = next;
        }

        if (activeLutIndex >= 0) {
            loadLut(activeLutIndex);
        } else {
            clearLut();
        }
        updateUI();
    }

    private void applyIspPreview() {
        if (activeLutData == null || activeLutData.data == null) {
            ispController.clear();
            return;
        }
        LutDecomposer.IspParams params = LutDecomposer.decompose(activeLutData.data, 33);
        if (params != null) {
            ispController.apply(params.gammaCurve, params.rgbMatrix);
        }
    }

    // ---- Capture Pipeline ----

    private void capturePhoto() {
        if (normalCamera == null) return;
        if (capturing) return;

        // Check queue depth
        if (captureQueue.size() >= MAX_QUEUE_DEPTH) {
            FileLogger.log("CAPTURE", "Queue full - waiting for processing");
            return;
        }

        capturing = true;
        FileLogger.log("CAPTURE", "Shutter pressed");

        normalCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                capturing = false;

                if (data == null) {
                    FileLogger.log("CAPTURE", "No image data received");
                    camera.startPreview();
                    return;
                }

                FileLogger.log("CAPTURE", "Image received: " + data.length + " bytes");

                if (LutProcessor.isLoaded() && activeLutIndex >= 0) {
                    // Queue for LUT processing
                    CaptureJob job = new CaptureJob();
                    job.imageData = data;

                    // Get image dimensions from JPEG header
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    opts.inJustDecodeBounds = true;
                    BitmapFactory.decodeByteArray(data, 0, data.length, opts);
                    job.width = opts.outWidth;
                    job.height = opts.outHeight;

                    LutEntry entry = lutEntries.get(activeLutIndex);
                    job.lutFilename = entry.filename;
                    job.lutTitle = entry.getDisplayTitle();
                    job.lutDescription = entry.description;
                    job.keepRaw = keepRaw;

                    boolean queued = captureQueue.offer(job);
                    if (queued) {
                        FileLogger.log("CAPTURE", "Queued for LUT processing ("
                                + captureQueue.size() + "/" + MAX_QUEUE_DEPTH + ")");
                    } else {
                        // Queue full, save without LUT
                        saveJpegDirect(data, null);
                        FileLogger.log("CAPTURE", "Queue full, saved without LUT");
                    }
                } else {
                    // No LUT active, save directly
                    saveJpegDirect(data, null);
                }

                updateQueueStatus();
                camera.startPreview();
            }
        });
    }

    private void startWorkerThread() {
        workerRunning = true;
        workerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                FileLogger.log("WORKER", "LUT worker thread started");
                while (workerRunning) {
                    try {
                        CaptureJob job = captureQueue.take();
                        processCapture(job);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() { updateQueueStatus(); }
                        });
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                FileLogger.log("WORKER", "LUT worker thread stopped");
            }
        }, "LUT-Worker");
        workerThread.start();
    }

    private void stopWorkerThread() {
        workerRunning = false;
        if (workerThread != null) {
            workerThread.interrupt();
        }
    }

    private void processCapture(CaptureJob job) {
        long startTime = System.currentTimeMillis();
        FileLogger.log("WORKER", "Processing capture: " + job.width + "x" + job.height);

        // Check memory before processing
        Runtime rt = Runtime.getRuntime();
        long freeMemory = rt.freeMemory();
        if (freeMemory < 5 * 1024 * 1024) {
            FileLogger.log("WORKER", "WARNING: Low memory (" + (freeMemory / 1024)
                    + "KB free), saving without LUT");
            saveJpegDirect(job.imageData, null);
            return;
        }

        try {
            // Decode JPEG to bitmap
            Bitmap bitmap = BitmapFactory.decodeByteArray(job.imageData, 0, job.imageData.length);
            if (bitmap == null) {
                FileLogger.log("WORKER", "Failed to decode JPEG");
                saveJpegDirect(job.imageData, null);
                return;
            }

            int w = bitmap.getWidth();
            int h = bitmap.getHeight();

            // Get pixels as ARGB
            int[] pixels = new int[w * h];
            bitmap.getPixels(pixels, 0, w, 0, 0, w, h);
            bitmap.recycle();

            // Convert ARGB to NV21 YUV for JNI processing
            byte[] yuv = argbToNv21(pixels, w, h);
            pixels = null; // Free early

            // Apply LUT via JNI
            LutProcessor.applyLut(yuv, w, h);

            // Convert back to ARGB
            int[] outPixels = nv21ToArgb(yuv, w, h);
            yuv = null; // Free early

            // Create output bitmap and encode to JPEG
            Bitmap outBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            outBitmap.setPixels(outPixels, 0, w, 0, 0, w, h);
            outPixels = null;

            // Save to SD card
            String path = saveJpegBitmap(outBitmap, job);
            outBitmap.recycle();

            long elapsed = System.currentTimeMillis() - startTime;
            FileLogger.log("WORKER", "LUT applied and saved in " + elapsed + "ms");
            FileLogger.logMemory("CAPTURE_DONE");

            // Write EXIF metadata if path available
            if (path != null) {
                writeExifMetadata(path, job);
            }
        } catch (OutOfMemoryError e) {
            FileLogger.log("WORKER", "OOM during LUT processing, saving original");
            saveJpegDirect(job.imageData, null);
            System.gc();
        } catch (Exception e) {
            FileLogger.logError("WORKER", e);
            saveJpegDirect(job.imageData, null);
        }
    }

    private String saveJpegBitmap(Bitmap bitmap, CaptureJob job) {
        File dir = new File(Environment.getExternalStorageDirectory(), "DCIM/CubeLUT");
        dir.mkdirs();
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.US).format(new Date());
        String filename = "LUT_" + timestamp + ".jpg";
        File file = new File(dir, filename);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, fos);
            fos.flush();
            FileLogger.log("SAVE", "Saved: " + file.getAbsolutePath());
            return file.getAbsolutePath();
        } catch (IOException e) {
            FileLogger.logError("SAVE", e);
            return null;
        } finally {
            if (fos != null) {
                try { fos.close(); } catch (IOException e) { /* ignore */ }
            }
        }
    }

    private void saveJpegDirect(byte[] jpegData, CaptureJob job) {
        File dir = new File(Environment.getExternalStorageDirectory(), "DCIM/CubeLUT");
        dir.mkdirs();
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.US).format(new Date());
        String filename = "LUT_" + timestamp + ".jpg";
        File file = new File(dir, filename);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(jpegData);
            fos.flush();
            FileLogger.log("SAVE", "Saved (direct): " + file.getAbsolutePath());
        } catch (IOException e) {
            FileLogger.logError("SAVE", e);
        } finally {
            if (fos != null) {
                try { fos.close(); } catch (IOException e) { /* ignore */ }
            }
        }
    }

    // EXIF metadata writing disabled - commons-imaging library caused boot loop on A6500.
    // TODO: Implement lightweight EXIF writer without external library dependency.
    private void writeExifMetadata(String jpegPath, CaptureJob job) {
        // Log the LUT info to file instead
        FileLogger.log("EXIF", "LUT metadata (not written to EXIF): CUBELUT|"
                + job.lutFilename + "|" + job.lutTitle + "|RAW=" + job.keepRaw);
    }

    // ---- YUV Conversion Helpers ----

    private static byte[] argbToNv21(int[] argb, int width, int height) {
        int ySize = width * height;
        int uvSize = ySize / 2;
        byte[] nv21 = new byte[ySize + uvSize];

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                int pixel = argb[j * width + i];
                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = pixel & 0xFF;

                int y = ((66 * r + 129 * g + 25 * b + 128) >> 8) + 16;
                nv21[j * width + i] = (byte) clamp(y, 16, 235);

                if ((j & 1) == 0 && (i & 1) == 0) {
                    int u = ((-38 * r - 74 * g + 112 * b + 128) >> 8) + 128;
                    int v = ((112 * r - 94 * g - 18 * b + 128) >> 8) + 128;
                    int uvIdx = ySize + (j / 2) * width + i;
                    nv21[uvIdx] = (byte) clamp(v, 16, 240);
                    nv21[uvIdx + 1] = (byte) clamp(u, 16, 240);
                }
            }
        }
        return nv21;
    }

    private static int[] nv21ToArgb(byte[] nv21, int width, int height) {
        int ySize = width * height;
        int[] argb = new int[ySize];

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                int y = nv21[j * width + i] & 0xFF;
                int uvIdx = ySize + (j / 2) * width + (i & ~1);
                int v = nv21[uvIdx] & 0xFF;
                int u = nv21[uvIdx + 1] & 0xFF;

                float yf = (y - 16) / 219.0f;
                float uf = (u - 128) / 224.0f;
                float vf = (v - 128) / 224.0f;

                int r = clamp((int) ((yf + 1.402f * vf) * 255), 0, 255);
                int g = clamp((int) ((yf - 0.344136f * uf - 0.714136f * vf) * 255), 0, 255);
                int bl = clamp((int) ((yf + 1.772f * uf) * 255), 0, 255);

                argb[j * width + i] = 0xFF000000 | (r << 16) | (g << 8) | bl;
            }
        }
        return argb;
    }

    private static int clamp(int val, int min, int max) {
        return val < min ? min : (val > max ? max : val);
    }

    // ---- UI Updates ----

    private void updateUI() {
        // LUT name
        if (activeLutIndex >= 0 && activeLutIndex < lutEntries.size()) {
            lutNameOverlay.setText("LUT: " + lutEntries.get(activeLutIndex).getDisplayTitle());
        } else {
            lutNameOverlay.setText("LUT: None");
        }

        // Toggle states
        prvToggle.setText("PRV");
        prvToggle.setAlpha(previewOn ? 1.0f : 0.5f);

        rawToggle.setText("RAW");
        rawToggle.setAlpha(keepRaw ? 1.0f : 0.5f);

        // Nav buttons
        boolean hasLuts = lutEntries != null && !lutEntries.isEmpty();
        prevLutBtn.setVisibility(hasLuts ? View.VISIBLE : View.GONE);
        nextLutBtn.setVisibility(hasLuts ? View.VISIBLE : View.GONE);

        updateQueueStatus();

        // Update debug overlay if visible
        if (debugVisible) {
            updateDebugOverlay();
        }
    }

    private void updateQueueStatus() {
        int pending = captureQueue.size();
        if (pending > 0) {
            queueStatus.setVisibility(View.VISIBLE);
            queueStatus.setText("LUT: " + pending + "/" + MAX_QUEUE_DEPTH + " queued");
        } else {
            queueStatus.setVisibility(View.GONE);
        }
    }

    // ---- Actions ----

    private void openLutMenu() {
        Intent intent = new Intent(this, LutMenuActivity.class);
        intent.putExtra(LutMenuActivity.EXTRA_SELECTED_INDEX, activeLutIndex);
        startActivityForResult(intent, REQUEST_LUT_SELECT);
    }

    private void openHelp() {
        startActivity(new Intent(this, HelpActivity.class));
    }

    private void toggleKeepRaw() {
        keepRaw = !keepRaw;
        FileLogger.log("TOGGLE", "Keep RAW: " + keepRaw);
        updateUI();
    }

    private void togglePreview() {
        previewOn = !previewOn;
        if (previewOn) {
            applyIspPreview();
        } else {
            ispController.clear();
        }
        FileLogger.log("TOGGLE", "Preview: " + previewOn);
        updateUI();
    }

    private void toggleDebugOverlay() {
        debugVisible = !debugVisible;
        debugOverlay.setVisibility(debugVisible ? View.VISIBLE : View.GONE);
        if (debugVisible) {
            updateDebugOverlay();
        }
    }

    private void updateDebugOverlay() {
        Runtime rt = Runtime.getRuntime();
        long used = rt.totalMemory() - rt.freeMemory();
        long max = rt.maxMemory();
        long nativeHeap = android.os.Debug.getNativeHeapAllocatedSize();
        String text = "MEM: " + (used / 1024) + "KB / " + (max / 1024) + "KB"
                + "\nNAT: " + (nativeHeap / 1024) + "KB"
                + "\nLUT: " + (LutProcessor.isLoaded() ? "loaded" : "none")
                + "\nISP: " + (ispController.isGammaActive() ? "active" : "off")
                + "\nQ: " + captureQueue.size() + "/" + MAX_QUEUE_DEPTH;
        debugOverlay.setText(text);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LUT_SELECT && resultCode == LutMenuActivity.RESULT_LUT_SELECTED && data != null) {
            int selectedIndex = data.getIntExtra(LutMenuActivity.EXTRA_SELECTED_INDEX, -1);
            if (selectedIndex == -1) {
                clearLut();
            } else {
                loadLut(selectedIndex);
            }
            updateUI();
        }
    }

    @Override
    protected void setColorDepth(boolean highQuality) {
        super.setColorDepth(false); // Camera preview needs low color depth
    }
}
