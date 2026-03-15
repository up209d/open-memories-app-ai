package com.sony.imaging.app.srctrl.util.cameraproxy;

import android.util.Log;
import com.sony.imaging.app.srctrl.util.HandoffOperationInfo;
import com.sony.imaging.app.srctrl.util.OperationRequester;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationTouchAFPosition;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.TouchAFCurrentPositionParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.TouchAFPositionParams;

/* loaded from: classes.dex */
public class CameraProxyTouchAFPosition {
    private static final String tag = CameraProxyTouchAFPosition.class.getName();

    public static TouchAFPositionParams set(double x, double y) {
        if (0.0d <= x && x <= 100.0d && 0.0d <= y && y <= 100.0d) {
            CameraOperationTouchAFPosition.CameraNotificationListener focusNotifier = new CameraOperationTouchAFPosition.CameraNotificationListener();
            Boolean result = (Boolean) new OperationRequester().request(26, HandoffOperationInfo.CameraSettings.TOUCH_AF, new Double(x), new Double(y), focusNotifier);
            TouchAFPositionParams params = null;
            if (result != null && result.booleanValue()) {
                synchronized (focusNotifier) {
                    if (!focusNotifier.mReturned) {
                        try {
                            Log.v(tag, "Wait the autofocus finished.");
                            focusNotifier.wait(15000L);
                            Log.v(tag, "Autofocus wait canceled.");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (focusNotifier.mReturned) {
                        Log.v(tag, "Autofocus completed successfully.");
                        params = focusNotifier.params;
                    } else {
                        Log.e(tag, "Touch AF timed out.  Cancel it");
                        focusNotifier.unregister();
                    }
                }
                return params;
            }
            Log.e(tag, "Failed to set Touch AF position: x=" + x + ", y=" + y);
            return null;
        }
        Log.e(tag, " Touch AF Range Error: x=" + x + ", y=" + y);
        return null;
    }

    public static TouchAFCurrentPositionParams get() {
        Log.d(tag, "Invoke and wait a TouchAF *GET* call...");
        TouchAFCurrentPositionParams params = (TouchAFCurrentPositionParams) new OperationRequester().request(23, HandoffOperationInfo.CameraSettings.TOUCH_AF);
        Log.d(tag, "TouchAF *GET* call was returned.");
        return params;
    }

    public static boolean cancel() {
        Log.d(tag, " Invoke and wait a TouchAF *CANCEL* call...");
        Boolean result = (Boolean) new OperationRequester().request(26, HandoffOperationInfo.CameraSettings.TOUCH_AF, null, null, null);
        Log.d(tag, "] TouchAF *CANCEL* call was returned.");
        if (result != null) {
            return result.booleanValue();
        }
        return false;
    }
}
