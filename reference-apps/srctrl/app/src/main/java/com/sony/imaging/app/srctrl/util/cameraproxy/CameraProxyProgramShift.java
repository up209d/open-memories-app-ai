package com.sony.imaging.app.srctrl.util.cameraproxy;

import android.util.Log;
import com.sony.imaging.app.srctrl.util.HandoffOperationInfo;
import com.sony.imaging.app.srctrl.util.OperationRequester;
import com.sony.imaging.app.srctrl.util.SRCtrlConstants;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationExposureMode;
import com.sony.imaging.app.srctrl.webapi.availability.ParamsGenerator;
import java.util.List;

/* loaded from: classes.dex */
public class CameraProxyProgramShift {
    private static final int MAX_PROGRAM_SHIFT_STEP = 5;
    private static final int MIN_PROGRAM_SHIFT_STEP = -5;
    private static final int[] DEFAULT_SUPPORTED_RANGE = {5, -5};
    private static String TAG = CameraProxyProgramShift.class.getSimpleName();

    public static boolean[] set(int step) throws IllegalArgumentException {
        if (step < -5 || 5 < step || step == 0) {
            Log.v(TAG, "Invalid parameter was specified to Program Shift: " + step);
            return null;
        }
        List<Boolean> result = (List) new OperationRequester().request(26, HandoffOperationInfo.CameraSettings.PROGRAM_SHIFT, Integer.valueOf(step));
        if (result != null) {
            return OperationRequester.copy(result);
        }
        return null;
    }

    public static int[] getSupported() {
        String exposureMode = ParamsGenerator.peekExposureModeParamsSnapshot().currentExposureMode;
        if (exposureMode != null && CameraOperationExposureMode.EXPOSURE_MODE_PROGRAM_AUTO.equals(exposureMode)) {
            return DEFAULT_SUPPORTED_RANGE;
        }
        Log.v(TAG, "Set empty int array to the supported value due to the exposure mode " + exposureMode);
        return SRCtrlConstants.s_EMPTY_INT_ARRAY;
    }
}
