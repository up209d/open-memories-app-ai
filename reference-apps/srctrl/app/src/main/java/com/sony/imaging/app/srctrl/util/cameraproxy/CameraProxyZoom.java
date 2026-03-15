package com.sony.imaging.app.srctrl.util.cameraproxy;

import com.sony.imaging.app.srctrl.util.HandoffOperationInfo;
import com.sony.imaging.app.srctrl.util.OperationRequester;

/* loaded from: classes.dex */
public class CameraProxyZoom {
    private static String TAG = CameraProxyZoom.class.getSimpleName();

    public static boolean actZoom(String direction, String movement) {
        Boolean result = (Boolean) new OperationRequester().requestPriority(26, HandoffOperationInfo.CameraSettings.ZOOM, direction, movement);
        if (result != null) {
            return result.booleanValue();
        }
        return false;
    }
}
