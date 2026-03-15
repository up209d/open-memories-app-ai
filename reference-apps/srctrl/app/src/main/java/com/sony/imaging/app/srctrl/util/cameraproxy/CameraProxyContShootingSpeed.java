package com.sony.imaging.app.srctrl.util.cameraproxy;

import android.util.Log;
import com.sony.imaging.app.srctrl.util.HandoffOperationInfo;
import com.sony.imaging.app.srctrl.util.OperationRequester;
import com.sony.imaging.app.srctrl.webapi.definition.Name;

/* loaded from: classes.dex */
public class CameraProxyContShootingSpeed {
    private static String TAG = CameraProxyContShootingSpeed.class.getSimpleName();

    public static boolean set(String speed) {
        Log.v(TAG, Name.PREFIX_SET);
        Boolean result = (Boolean) new OperationRequester().request(26, HandoffOperationInfo.CameraSettings.CONT_SHOOT_SPEED, speed);
        if (result != null) {
            return result.booleanValue();
        }
        return false;
    }

    public static String get() {
        Log.v(TAG, Name.PREFIX_GET);
        String result = (String) new OperationRequester().request(23, HandoffOperationInfo.CameraSettings.CONT_SHOOT_SPEED);
        return result;
    }

    public static String[] getAvailable() {
        Log.v(TAG, Name.PREFIX_GET_AVAILABLE);
        String[] result = (String[]) new OperationRequester().request(24, HandoffOperationInfo.CameraSettings.CONT_SHOOT_SPEED);
        return result;
    }

    public static String[] getSupported() {
        Log.v(TAG, Name.PREFIX_GET_SUPPORTED);
        String[] result = (String[]) new OperationRequester().request(25, HandoffOperationInfo.CameraSettings.CONT_SHOOT_SPEED);
        return result;
    }
}
