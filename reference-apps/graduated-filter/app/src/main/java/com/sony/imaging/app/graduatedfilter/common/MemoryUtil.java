package com.sony.imaging.app.graduatedfilter.common;

import android.util.Log;

/* loaded from: classes.dex */
public class MemoryUtil {
    private static final String TAG;

    public native int MemoryCopyApplicationToDiadem(byte[] bArr, int i, int i2);

    public native int MemoryCopyDiademToApplication(int i, byte[] bArr, int i2);

    public native int MemoryCopyRaw(int i, int i2, int i3, int i4, int i5, int i6, int i7);

    static {
        System.loadLibrary("MemoryUtil_JNI");
        TAG = MemoryUtil.class.getSimpleName();
    }

    public void memoryCopyRaw(int src, int dist, int srcSize, int distSize, int canvasWidth, int width, int height) {
        Log.d(TAG, "MemoryCopyRaw called.");
        MemoryCopyRaw(src, dist, srcSize, distSize, canvasWidth, width, height);
    }

    public void memoryCopyDiademToApplication(int srcAddress, byte[] distArray, int size) {
        MemoryCopyDiademToApplication(srcAddress, distArray, size);
    }

    public void memoryCopyApplicationToDiadem(byte[] srcArray, int distAddress, int size) {
        MemoryCopyApplicationToDiadem(srcArray, distAddress, size);
    }
}
