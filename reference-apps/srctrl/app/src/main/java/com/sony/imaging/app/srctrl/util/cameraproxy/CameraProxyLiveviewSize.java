package com.sony.imaging.app.srctrl.util.cameraproxy;

import com.sony.imaging.app.srctrl.liveview.LiveviewContainer;
import com.sony.imaging.app.srctrl.liveview.LiveviewLoader;

/* loaded from: classes.dex */
public class CameraProxyLiveviewSize {
    private static String TAG = CameraProxyLiveviewSize.class.getSimpleName();

    public static boolean set(String size) {
        LiveviewContainer container = LiveviewContainer.getInstance();
        boolean ret = container.setLiveviewSize(size);
        return ret;
    }

    public static String get() {
        LiveviewContainer container = LiveviewContainer.getInstance();
        return container.getLiveviewSize();
    }

    public static String[] getAvailable() {
        LiveviewContainer container = LiveviewContainer.getInstance();
        return LiveviewLoader.isLoadingPreview() ? new String[]{container.getLiveviewSize()} : container.getAvailableLiveviewSize();
    }

    public static String[] getSupported() {
        LiveviewContainer container = LiveviewContainer.getInstance();
        return container.getSupportedLiveviewSize();
    }
}
