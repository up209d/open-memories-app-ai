package com.sony.imaging.app.util;

import android.content.Context;
import dalvik.system.DexFile;
import java.io.File;

/* loaded from: classes.dex */
public class ClassDefinition {
    private static final String DEX = "/mnt/wv_cache/%s@libmediarecorder.dex";
    private static final String JAR = "/data/data/%s/lib/libmediarecorder.so";

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
    }
}
