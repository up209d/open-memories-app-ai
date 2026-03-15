package com.sony.imaging.app.srctrl.util.cameraproxy;

import com.sony.imaging.app.srctrl.util.HandoffOperationInfo;
import com.sony.imaging.app.srctrl.util.OperationRequester;

/* loaded from: classes.dex */
public class CameraProxySelftimer {
    private static String TAG = CameraProxySelftimer.class.getSimpleName();

    public static boolean setSelfTimer(int timer) {
        Boolean result = (Boolean) new OperationRequester().request(26, HandoffOperationInfo.CameraSettings.SELF_TIMER, Integer.valueOf(timer));
        if (result.booleanValue()) {
            return result.booleanValue();
        }
        return false;
    }

    public static Integer getSelftimerCurrent() {
        Integer result = (Integer) new OperationRequester().request(23, HandoffOperationInfo.CameraSettings.SELF_TIMER);
        return result;
    }

    public static int[] getSelftimerSupported() {
        int i;
        Integer[] result = (Integer[]) new OperationRequester().request(25, HandoffOperationInfo.CameraSettings.SELF_TIMER);
        int[] supported = new int[result.length];
        int len$ = result.length;
        int i$ = 0;
        int i2 = 0;
        while (i$ < len$) {
            Integer value = result[i$];
            if (value != null) {
                i = i2 + 1;
                supported[i2] = value.intValue();
            } else {
                i = i2;
            }
            i$++;
            i2 = i;
        }
        return supported;
    }

    public static int[] getSelftimerAvailable() {
        int i;
        Integer[] result = (Integer[]) new OperationRequester().request(24, HandoffOperationInfo.CameraSettings.SELF_TIMER);
        int[] available = new int[result.length];
        int len$ = result.length;
        int i$ = 0;
        int i2 = 0;
        while (i$ < len$) {
            Integer value = result[i$];
            if (value != null) {
                i = i2 + 1;
                available[i2] = value.intValue();
            } else {
                i = i2;
            }
            i$++;
            i2 = i;
        }
        return available;
    }
}
