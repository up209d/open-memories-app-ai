package com.sony.imaging.app.srctrl.util.cameraproxy;

import android.util.Log;
import com.sony.imaging.app.srctrl.util.HandoffOperationInfo;
import com.sony.imaging.app.srctrl.util.OperationRequester;

/* loaded from: classes.dex */
public class CameraProxyLiveviewFrameInfo {
    private static String TAG = CameraProxyLiveviewFrameInfo.class.getSimpleName();

    public static void set(boolean frameInfo) {
        Boolean result = (Boolean) new OperationRequester().request(26, HandoffOperationInfo.CameraSettings.LIVEVIEW_FRAME_INFO, Boolean.valueOf(frameInfo));
        if (result == null) {
            Log.e(TAG, "set(). result is null");
        }
    }

    public static boolean get() {
        Boolean frameInfo = (Boolean) new OperationRequester().request(23, HandoffOperationInfo.CameraSettings.LIVEVIEW_FRAME_INFO);
        if (frameInfo != null) {
            boolean ret = frameInfo.booleanValue();
            return ret;
        }
        Log.e(TAG, "get(). frameInfo is null");
        return false;
    }
}
