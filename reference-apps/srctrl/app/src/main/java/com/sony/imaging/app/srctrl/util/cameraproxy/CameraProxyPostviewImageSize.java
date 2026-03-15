package com.sony.imaging.app.srctrl.util.cameraproxy;

import com.sony.imaging.app.srctrl.util.MediaObserverAggregator;
import com.sony.imaging.app.srctrl.webapi.availability.ParamsGenerator;

/* loaded from: classes.dex */
public class CameraProxyPostviewImageSize {
    private static final String SIZE_ORIGINAL = "Original";
    private static final String TAG = CameraProxyPostviewImageSize.class.getSimpleName();
    private static final String SIZE_2M = "2M";
    private static final String[] SUPPORTED = {"Original", SIZE_2M};
    private static final String[] SIZE_2M_ONLY = {SIZE_2M};
    private static String SIZE_CURRENT = SIZE_2M;

    public static boolean set(String size) {
        String[] available = getAvailable();
        for (String s : available) {
            if (s.equals(size)) {
                if (!SIZE_CURRENT.equals(size)) {
                    SIZE_CURRENT = size;
                    ParamsGenerator.updatePostviewImageSize();
                }
                return true;
            }
        }
        return false;
    }

    public static String get() {
        return isExternalMediaMounted() ? SIZE_CURRENT : SIZE_2M;
    }

    private static boolean isExternalMediaMounted() {
        return MediaObserverAggregator.isExternalMediaMounted();
    }

    public static String[] getAvailable() {
        return isExternalMediaMounted() ? SUPPORTED : SIZE_2M_ONLY;
    }

    public static String[] getSupported() {
        return SUPPORTED;
    }

    public static boolean isSizeOriginal() {
        return "Original".equals(SIZE_CURRENT);
    }
}
