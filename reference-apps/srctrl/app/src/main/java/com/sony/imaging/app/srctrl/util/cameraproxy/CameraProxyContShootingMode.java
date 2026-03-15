package com.sony.imaging.app.srctrl.util.cameraproxy;

import com.sony.imaging.app.srctrl.util.HandoffOperationInfo;
import com.sony.imaging.app.srctrl.util.OperationRequester;

/* loaded from: classes.dex */
public class CameraProxyContShootingMode {
    private static String TAG = CameraProxyContShootingMode.class.getSimpleName();

    public static boolean set(String mode) {
        Boolean result = (Boolean) new OperationRequester().request(26, HandoffOperationInfo.CameraSettings.CONT_SHOOT_MODE, mode);
        if (result != null) {
            return result.booleanValue();
        }
        return false;
    }

    public static String get() {
        String result = (String) new OperationRequester().request(23, HandoffOperationInfo.CameraSettings.CONT_SHOOT_MODE);
        return result;
    }

    public static String[] getAvailable() {
        String[] result = (String[]) new OperationRequester().request(24, HandoffOperationInfo.CameraSettings.CONT_SHOOT_MODE);
        return result;
    }

    public static String[] getSupported() {
        String[] result = (String[]) new OperationRequester().request(25, HandoffOperationInfo.CameraSettings.CONT_SHOOT_MODE);
        return result;
    }
}
