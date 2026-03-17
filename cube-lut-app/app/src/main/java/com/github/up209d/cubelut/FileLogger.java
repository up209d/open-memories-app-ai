package com.github.up209d.cubelut;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileLogger {
    private static final String TAG = "CubeLUT";
    private static final String APP_DIR = "UPCubeLutApp";
    private static final String LOG_DIR = "logs";

    private static File logFile;
    private static final SimpleDateFormat TIMESTAMP_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
    private static final SimpleDateFormat FILE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US);

    private static final Object LOCK = new Object();

    public static void init() {
        try {
            File dir = new File(Environment.getExternalStorageDirectory(),
                    APP_DIR + File.separator + LOG_DIR);
            dir.mkdirs();
            String filename = "run_" + FILE_FORMAT.format(new Date()) + ".log";
            logFile = new File(dir, filename);
            log("INIT", "=== CubeLUT App Session Started ===");
            log("INIT", "Device: " + android.os.Build.BRAND + " " + android.os.Build.MODEL
                    + " (" + android.os.Build.DEVICE + ")");
            log("INIT", "Android: " + android.os.Build.VERSION.RELEASE
                    + " (SDK " + android.os.Build.VERSION.SDK_INT + ")");
            logMemory("INIT");
        } catch (Exception e) {
            Log.e(TAG, "Failed to init logger", e);
        }
    }

    public static void log(String tag, String message) {
        String timestamp = TIMESTAMP_FORMAT.format(new Date());
        String entry = "[" + timestamp + "] [" + tag + "] " + message;
        Log.d(TAG, entry);
        writeAsync(entry);
    }

    public static void logMemory(String tag) {
        Runtime rt = Runtime.getRuntime();
        long used = rt.totalMemory() - rt.freeMemory();
        long max = rt.maxMemory();
        long nativeHeap = android.os.Debug.getNativeHeapAllocatedSize();
        log(tag, "Memory - Used: " + (used / 1024) + "KB / Max: " + (max / 1024)
                + "KB | Native: " + (nativeHeap / 1024) + "KB");
    }

    public static void logError(String tag, Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        log(tag, "ERROR: " + e.getMessage() + "\n" + sw.toString());
    }

    private static void writeAsync(final String entry) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (LOCK) {
                    if (logFile == null) return;
                    BufferedWriter writer = null;
                    try {
                        writer = new BufferedWriter(new FileWriter(logFile, true));
                        writer.write(entry);
                        writer.newLine();
                    } catch (IOException e) {
                        Log.e(TAG, "Failed to write log", e);
                    } finally {
                        if (writer != null) {
                            try { writer.close(); } catch (IOException e) { /* ignore */ }
                        }
                    }
                }
            }
        }).start();
    }

    public static void installCrashHandler() {
        final Thread.UncaughtExceptionHandler defaultHandler =
                Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                try {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    ex.printStackTrace(pw);
                    log("CRASH", "Uncaught exception in " + thread.getName()
                            + ": " + ex.getMessage() + "\n" + sw.toString());
                    logMemory("CRASH");
                    // Give async write a moment
                    Thread.sleep(500);
                } catch (Exception e) {
                    // Best effort
                }
                if (defaultHandler != null) {
                    defaultHandler.uncaughtException(thread, ex);
                }
            }
        });
    }
}
