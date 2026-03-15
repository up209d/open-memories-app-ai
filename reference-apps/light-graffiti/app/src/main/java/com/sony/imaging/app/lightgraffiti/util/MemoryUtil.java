package com.sony.imaging.app.lightgraffiti.util;

import android.util.Log;

/* loaded from: classes.dex */
public class MemoryUtil {
    private static final String TAG;

    public native int MemoryCopyDeviceBufferToOptimizedImage(int i, int i2, int i3, int i4, int i5, int i6, int i7);

    public native int MemoryCopyOptimizedImageToDeviceBuffer(int i, int i2, int i3, int i4, int i5, int i6, int i7);

    static {
        System.loadLibrary("MemoryUtil_JNI");
        TAG = MemoryUtil.class.getSimpleName();
    }

    public void memoryCopyOpitimizedImageToDeviceBuffer(int src, int dist, int srcSize, int distSize, int canvasWidth, int width, int height) {
        Log.d(TAG, "memoryCopyOpitimizedImageToDeviceBuffer called.");
        MemoryCopyOptimizedImageToDeviceBuffer(src, dist, srcSize, distSize, canvasWidth, width, height);
    }

    public void memoryCopyDeviceBufferToOpitimizedImage(int src, int dist, int srcSize, int distSize, int canvasWidth, int width, int height) {
        Log.d(TAG, "memoryCopyDeviceBufferToOpitimizedImage called.");
        MemoryCopyDeviceBufferToOptimizedImage(src, dist, srcSize, distSize, canvasWidth, width, height);
    }
}
