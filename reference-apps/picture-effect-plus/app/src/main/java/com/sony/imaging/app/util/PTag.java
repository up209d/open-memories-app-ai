package com.sony.imaging.app.util;

import android.os.Debug;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;
import java.io.FileOutputStream;
import java.io.OutputStream;

/* loaded from: classes.dex */
public class PTag {
    private static final String LOG_END = "End   ";
    private static final String LOG_START = "Start ";
    private static final String PERFORMANCE = "Performance";
    private static final String TAG = "PTag";
    private static final String TMONITOR = "/proc/tmonitor";
    public static final int TRACE_BOOT_TIME = 1;
    public static final int TRACE_OFF = 0;
    public static int TraceMode = 0;

    public static void start(String tag) {
        Log.i(PERFORMANCE, LOG_START + tag);
    }

    public static void end(String tag) {
        Log.i(PERFORMANCE, LOG_END + tag);
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
            Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() { // from class: com.sony.imaging.app.util.PTag.1
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
