package com.sony.imaging.app.srctrl.util.cameraproxy;

import android.util.Log;
import com.sony.imaging.app.srctrl.util.OperationRequester;

/* loaded from: classes.dex */
public class CameraProxyHalfPressShutter {
    private static final String TAG = CameraProxyHalfPressShutter.class.getName();

    public static void action() {
        Boolean result = (Boolean) new OperationRequester().request(31, new Object[0]);
        Log.v(TAG, "action() result=" + result);
    }

    public static void cancel() {
        Boolean result = (Boolean) new OperationRequester().request(32, new Object[0]);
        Log.v(TAG, "cancel() result=" + result);
    }
}
