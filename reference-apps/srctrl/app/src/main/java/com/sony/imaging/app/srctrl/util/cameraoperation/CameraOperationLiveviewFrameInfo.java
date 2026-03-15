package com.sony.imaging.app.srctrl.util.cameraoperation;

import android.util.Log;
import com.sony.imaging.app.srctrl.liveview.FrameInfoLoader;

/* loaded from: classes.dex */
public class CameraOperationLiveviewFrameInfo {
    private static final String TAG = CameraOperationLiveviewFrameInfo.class.getSimpleName();

    public static boolean set(Boolean frameInfo) {
        if (frameInfo == null) {
            Log.e(TAG, "set(). frameInfo is null");
            return false;
        }
        FrameInfoLoader.setEnabled(frameInfo.booleanValue());
        return true;
    }

    public static Boolean get() {
        return Boolean.valueOf(FrameInfoLoader.isEnabled());
    }
}
