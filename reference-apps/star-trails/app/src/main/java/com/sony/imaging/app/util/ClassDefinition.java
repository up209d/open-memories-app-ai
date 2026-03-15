package com.sony.imaging.app.util;

import android.content.Context;
import dalvik.system.DexFile;
import java.io.File;

/* loaded from: classes.dex */
public class ClassDefinition {
    private static final String DEX = "/mnt/wv_cache/%s@libmediarecorder.dex";
    private static final String DEX_TRACKINGFOCUSINFO = "/mnt/wv_cache/%s@libtrackingfocusinfo.dex";
    private static final String JAR = "/data/data/%s/lib/libmediarecorder.so";
    private static final String JAR_TRACKINGFOCUSINFO = "/data/data/%s/lib/libtrackingfocusinfo.so";

    public static void resolve(Context context) {
        if (Environment.getVersionPfAPI() <= 1) {
            String packagename = context.getPackageName();
            String jarPath = String.format(JAR, packagename);
            String dexPath = String.format(DEX, packagename);
            try {
                File file = new File(dexPath);
                if (true == file.exists()) {
                    file.delete();
                }
                DexFile dexFile = DexFile.loadDex(jarPath, dexPath, 0);
                dexFile.loadClass("com.sony.scalar.media.MediaRecorder", context.getClassLoader());
                dexFile.loadClass("com.sony.scalar.media.MediaRecorder$Parameters", context.getClassLoader());
                dexFile.loadClass("com.sony.scalar.media.MediaRecorder$CamcorderProfile", context.getClassLoader());
                dexFile.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (Environment.getVersionPfAPI() < 8) {
            String packagename2 = context.getPackageName();
            String jarPath2 = String.format(JAR_TRACKINGFOCUSINFO, packagename2);
            String dexPath2 = String.format(DEX_TRACKINGFOCUSINFO, packagename2);
            try {
                File file2 = new File(dexPath2);
                if (true == file2.exists()) {
                    file2.delete();
                }
                DexFile dexFile2 = DexFile.loadDex(jarPath2, dexPath2, 0);
                dexFile2.loadClass("com.sony.scalar.hardware.CameraEx$TrackingFocusInfo", context.getClassLoader());
                dexFile2.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }
}
