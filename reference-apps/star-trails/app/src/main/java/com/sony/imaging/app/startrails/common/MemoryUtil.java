package com.sony.imaging.app.startrails.common;

/* loaded from: classes.dex */
public class MemoryUtil {
    public native int MemoryCopyApplicationToDiadem(byte[] bArr, int i, int i2);

    public native int MemoryCopyDiademToApplication(int i, byte[] bArr, int i2);

    static {
        System.loadLibrary("MemoryUtil_JNI");
    }

    public void memoryCopyDiademToApplication(int srcAddress, byte[] distArray, int size) {
        MemoryCopyDiademToApplication(srcAddress, distArray, size);
    }

    public void memoryCopyApplicationToDiadem(byte[] srcArray, int distAddress, int size) {
        MemoryCopyApplicationToDiadem(srcArray, distAddress, size);
    }
}
