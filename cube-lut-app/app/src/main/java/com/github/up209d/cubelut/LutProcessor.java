package com.github.up209d.cubelut;

public class LutProcessor {
    static {
        System.loadLibrary("lut-processor");
    }

    public static native void loadLut(float[] lutData, int cubeSize);
    public static native void applyLut(byte[] imageData, int width, int height);
    public static native void freeLut();
    public static native boolean isLoaded();
}
