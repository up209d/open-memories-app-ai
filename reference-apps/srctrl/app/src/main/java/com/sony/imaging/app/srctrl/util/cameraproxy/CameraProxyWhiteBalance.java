package com.sony.imaging.app.srctrl.util.cameraproxy;

import android.util.Log;
import com.sony.imaging.app.srctrl.util.HandoffOperationInfo;
import com.sony.imaging.app.srctrl.util.OperationRequester;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationExposureMode;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationWhiteBalance;
import com.sony.imaging.app.srctrl.webapi.availability.ParamsGenerator;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.WhiteBalanceParamCandidate;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.WhiteBalanceParams;

/* loaded from: classes.dex */
public class CameraProxyWhiteBalance {
    private static String TAG = CameraProxyWhiteBalance.class.getSimpleName();

    public static boolean set(WhiteBalanceParams param, boolean colorTemperatureEnabled) {
        Boolean result = (Boolean) new OperationRequester().request(26, HandoffOperationInfo.CameraSettings.WHITE_BALANCE, param, new Boolean(colorTemperatureEnabled));
        if (result != null) {
            return result.booleanValue();
        }
        return false;
    }

    public static WhiteBalanceParams get() {
        WhiteBalanceParams result = (WhiteBalanceParams) new OperationRequester().request(23, HandoffOperationInfo.CameraSettings.WHITE_BALANCE);
        return result;
    }

    public static WhiteBalanceParamCandidate[] getAvailable() {
        String exposureMode = ParamsGenerator.peekExposureModeParamsSnapshot().currentExposureMode;
        if (exposureMode != null && !CameraOperationExposureMode.EXPOSURE_MODE_INTELLIGENT_AUTO.equals(exposureMode)) {
            return (WhiteBalanceParamCandidate[]) new OperationRequester().request(24, HandoffOperationInfo.CameraSettings.WHITE_BALANCE);
        }
        Log.v(TAG, "Set empty array to the available value due to the exposure mode: " + exposureMode);
        return CameraOperationWhiteBalance.s_EMPTY_CANDIDATE;
    }

    public static WhiteBalanceParamCandidate[] getSupported() {
        String exposureMode = ParamsGenerator.peekExposureModeParamsSnapshot().currentExposureMode;
        if (exposureMode != null && !CameraOperationExposureMode.EXPOSURE_MODE_INTELLIGENT_AUTO.equals(exposureMode)) {
            return (WhiteBalanceParamCandidate[]) new OperationRequester().request(25, HandoffOperationInfo.CameraSettings.WHITE_BALANCE);
        }
        Log.v(TAG, "Set empty array to the supported value due to the exposure mode " + exposureMode);
        return CameraOperationWhiteBalance.s_EMPTY_CANDIDATE;
    }
}
