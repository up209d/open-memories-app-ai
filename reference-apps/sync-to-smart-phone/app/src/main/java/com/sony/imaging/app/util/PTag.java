package com.sony.imaging.app.util;

import android.os.Debug;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;
import com.sony.scalar.sysutil.ScalarProperties;
import java.io.FileOutputStream;
import java.io.OutputStream;

/* loaded from: classes.dex */
public class PTag {
    private static final String LOG_END = "End   ";
    private static final String LOG_START = "Start ";
    private static final String PERFORMANCE = "Performance";
    private static final int PF_VER_SUPPORT_TIMETAG = 8;
    private static final String TAG = "PTag";
    private static final String TIMETAG_END = "Base:E ";
    private static final int TIMETAG_MAX = 64;
    private static final String TIMETAG_PREFIX = "Base: ";
    private static final String TIMETAG_START = "Base:B ";
    private static final String TMONITOR = "/proc/tmonitor";
    public static final int TRACE_BOOT_TIME = 1;
    public static final int TRACE_OFF = 0;
    private static ThreadLocal<StringBuilder> sStringBuilders = new ThreadLocal<StringBuilder>() { // from class: com.sony.imaging.app.util.PTag.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.lang.ThreadLocal
        public StringBuilder initialValue() {
            return new StringBuilder();
        }
    };
    public static int TraceMode = 0;

    public static void start(String tag) {
        StringBuilder builder = sStringBuilders.get();
        if (builder != null) {
            builder.replace(0, builder.length(), LOG_START).append(tag);
            Log.i(PERFORMANCE, builder.toString());
        }
    }

    public static void end(String tag) {
        StringBuilder builder = sStringBuilders.get();
        if (builder != null) {
            builder.replace(0, builder.length(), LOG_END).append(tag);
            Log.i(PERFORMANCE, builder.toString());
        }
    }

    public static void startTimeTag(String tag) {
        StringBuilder builder;
        if (Environment.getVersionPfAPI() >= 8 && (builder = sStringBuilders.get()) != null) {
            builder.replace(0, builder.length(), TIMETAG_START).append(tag);
            if (builder.length() > TIMETAG_MAX) {
                builder.delete(TIMETAG_MAX, builder.length());
            }
            ScalarProperties.setTimeTag(builder.toString());
        }
    }

    public static void endTimeTag(String tag) {
        StringBuilder builder;
        if (Environment.getVersionPfAPI() >= 8 && (builder = sStringBuilders.get()) != null) {
            builder.replace(0, builder.length(), TIMETAG_END).append(tag);
            if (builder.length() > TIMETAG_MAX) {
                builder.delete(TIMETAG_MAX, builder.length());
            }
            ScalarProperties.setTimeTag(builder.toString());
        }
    }

    public static void setTimeTag(String tag) {
        StringBuilder builder;
        if (Environment.getVersionPfAPI() >= 8 && (builder = sStringBuilders.get()) != null) {
            builder.replace(0, builder.length(), TIMETAG_PREFIX).append(tag);
            if (builder.length() > TIMETAG_MAX) {
                builder.delete(TIMETAG_MAX, builder.length());
            }
            ScalarProperties.setTimeTag(builder.toString());
        }
    }

    public static void traceStart(int mode, String name) {
        if (TraceMode == mode) {
            Debug.startMethodTracing("/data/local/tmp/" + name);
        }
    }

    public static void traceStop(int mode) {
        if (TraceMode == mode) {
            Debug.stopMethodTracing();
        }
    }

    public static void traceStopWithEmptyQueue(int mode) {
        if (TraceMode == mode) {
            Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() { // from class: com.sony.imaging.app.util.PTag.2
                @Override // android.os.MessageQueue.IdleHandler
                public boolean queueIdle() {
                    Debug.stopMethodTracing();
                    return false;
                }
            });
        }
    }

    public static void TMTag(String tag) {
        OutputStream f;
        byte[] buf = tag.getBytes();
        try {
            f = new FileOutputStream(TMONITOR);
        } catch (Exception e) {
        }
        try {
            f.write(buf, 0, buf.length);
            f.close();
        } catch (Exception e2) {
            Log.i(TAG, "Maybe the file permission err. Please check the permission of /proc/tmonitor");
        }
    }
}
