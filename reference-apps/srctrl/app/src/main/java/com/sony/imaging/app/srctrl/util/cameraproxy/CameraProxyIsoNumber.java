package com.sony.imaging.app.srctrl.util.cameraproxy;

import android.util.Log;
import com.sony.imaging.app.srctrl.util.HandoffOperationInfo;
import com.sony.imaging.app.srctrl.util.OperationRequester;
import com.sony.imaging.app.srctrl.util.SRCtrlConstants;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationExposureMode;
import com.sony.imaging.app.srctrl.webapi.availability.ParamsGenerator;

/* loaded from: classes.dex */
public class CameraProxyIsoNumber {
    private static String TAG = CameraProxyIsoNumber.class.getSimpleName();

    public static boolean set(String isonumber) {
        Boolean result = (Boolean) new OperationRequester().request(26, HandoffOperationInfo.CameraSettings.ISO_NUMBER, isonumber);
        if (result != null) {
            return result.booleanValue();
        }
        return false;
    }

    public static String get() {
        String result = (String) new OperationRequester().request(23, HandoffOperationInfo.CameraSettings.ISO_NUMBER);
        return result;
    }

    public static String[] getAvailable() {
        String exposureMode = ParamsGenerator.peekExposureModeParamsSnapshot().currentExposureMode;
        if (exposureMode != null && !CameraOperationExposureMode.EXPOSURE_MODE_INTELLIGENT_AUTO.equals(exposureMode)) {
            return (String[]) new OperationRequester().request(24, HandoffOperationInfo.CameraSettings.ISO_NUMBER);
        }
        Log.v(TAG, "Set empty string array to the available value due to the exposure mode " + exposureMode);
        return SRCtrlConstants.s_EMPTY_STRING_ARRAY;
    }

    public static String[] getSupported() {
        String exposureMode = ParamsGenerator.peekExposureModeParamsSnapshot().currentExposureMode;
        if (exposureMode != null && !CameraOperationExposureMode.EXPOSURE_MODE_INTELLIGENT_AUTO.equals(exposureMode)) {
            return (String[]) new OperationRequester().request(25, HandoffOperationInfo.CameraSettings.ISO_NUMBER);
        }
        Log.v(TAG, "Set empty string array to the supported value due to the exposure mode " + exposureMode);
        return SRCtrlConstants.s_EMPTY_STRING_ARRAY;
    }
}
