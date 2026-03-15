package com.sony.imaging.app.srctrl.webapi.availability;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;
import com.sony.imaging.app.srctrl.util.HandoffOperationInfo;
import com.sony.imaging.app.srctrl.util.OperationRequester;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationExposureMode;
import com.sony.imaging.app.srctrl.webapi.definition.StatusCode;
import com.sony.imaging.app.srctrl.webapi.util.ExposureCompensationStep;
import java.util.List;

/* loaded from: classes.dex */
public class AvailableManager {
    private static String TAG = AvailableManager.class.getSimpleName();
    private static int[] s_Zero3IntArray = {0, 0, 0};
    private static int[][] s_Zero3x1IntArray = {new int[]{0}, new int[]{0}, new int[]{0}};

    public static boolean isAvailableSelfTimer() {
        Boolean result = (Boolean) new OperationRequester().request(11, DriveModeController.SELF_TIMER);
        if (result != null) {
            return result.booleanValue();
        }
        return false;
    }

    public static boolean isAvailableSingle() {
        Boolean result = (Boolean) new OperationRequester().request(11, DriveModeController.SINGLE);
        if (result != null) {
            return result.booleanValue();
        }
        return false;
    }

    public static boolean isAvailableBurst() {
        Boolean result = (Boolean) new OperationRequester().request(11, DriveModeController.BURST);
        if (result != null) {
            return result.booleanValue();
        }
        return false;
    }

    public static boolean isAvailableExposureCompensation() {
        Boolean result = (Boolean) new OperationRequester().request(0, (Object) null);
        if (result != null) {
            return result.booleanValue();
        }
        return false;
    }

    public static boolean isCameraSettingAvailable(HandoffOperationInfo.CameraSettings settingName) {
        List<Object> result = getCameraSettingAvailable(settingName);
        return (result == null || result.size() == 0) ? false : true;
    }

    public static Object getCameraSetting(HandoffOperationInfo.CameraSettings settingName) {
        return new OperationRequester().request(23, settingName);
    }

    public static List<Object> getCameraSettingAvailable(HandoffOperationInfo.CameraSettings settingName) {
        return (List) new OperationRequester().request(24, settingName);
    }

    public static List<Object> getCameraSettingSupported(HandoffOperationInfo.CameraSettings settingName) {
        return (List) new OperationRequester().request(25, settingName);
    }

    public static boolean setCameraSetting(HandoffOperationInfo.CameraSettings settingName, Object[] settings) {
        Boolean result = (Boolean) new OperationRequester().request(26, settingName, settings);
        if (result != null) {
            return result.booleanValue();
        }
        return false;
    }

    public static int setExposureCompensation(int exposure) {
        int i;
        List<String> available = (List) new OperationRequester().request(5, (Object) null);
        String exposureString = Integer.toString(exposure);
        if (available != null && available.contains(exposureString)) {
            try {
                Boolean booleanObj = (Boolean) new OperationRequester().request(1, exposureString);
                if (booleanObj != null && booleanObj.booleanValue()) {
                    i = StatusCode.OK.toInt();
                } else {
                    i = StatusCode.ANY.toInt();
                }
                return i;
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "IllegalArgumentException in setExposureCompensation");
                return StatusCode.ILLEGAL_ARGUMENT.toInt();
            }
        }
        Log.e(TAG, "EV:" + exposure + " is not available now");
        return StatusCode.ILLEGAL_ARGUMENT.toInt();
    }

    public static int[] getExopsureCompensationAvailable() {
        String exposureMode = ParamsGenerator.peekExposureModeParamsSnapshot().currentExposureMode;
        if (exposureMode == null || CameraOperationExposureMode.EXPOSURE_MODE_INTELLIGENT_AUTO.equals(exposureMode)) {
            Log.e(TAG, "Set invalid array to the available value due to the exposure mode " + exposureMode);
            return s_Zero3IntArray;
        }
        List<String> available = (List) new OperationRequester().request(5, (Object) null);
        if (available != null && available.size() != 0) {
            try {
                int min = Integer.parseInt(available.get(0));
                int max = Integer.parseInt(available.get(available.size() - 1));
                Float stepObj = (Float) new OperationRequester().request(3, (Object) null);
                if (stepObj == null) {
                    Log.e(TAG, "Couldn't obtain a well-formed index value of exposure compensation.");
                    return null;
                }
                float step = stepObj.floatValue();
                ExposureCompensationStep.EVStep stepIndex = ExposureCompensationStep.getStepIndex(step);
                Log.v(TAG, "Exposure step " + step + " is converted to index " + stepIndex.ordinal());
                return new int[]{max, min, stepIndex.ordinal()};
            } catch (NumberFormatException e) {
                Log.e(TAG, "NumberFormatException in getSupportedExposureCompensation");
                return null;
            }
        }
        return null;
    }

    public static int[][] getExopsureCompensationSupported() {
        String exposureMode = ParamsGenerator.peekExposureModeParamsSnapshot().currentExposureMode;
        if (exposureMode == null || CameraOperationExposureMode.EXPOSURE_MODE_INTELLIGENT_AUTO.equals(exposureMode)) {
            Log.e(TAG, "Set invalid array to the supported value due to the exposure mode " + exposureMode);
            return s_Zero3x1IntArray;
        }
        List<String> supported = (List) new OperationRequester().request(4, (Object) null);
        if (supported != null && supported.size() != 0) {
            int[] max = {0};
            int[] min = {0};
            float[] step = {0.0f};
            try {
                min[0] = Integer.parseInt(supported.get(0));
                max[0] = Integer.parseInt(supported.get(supported.size() - 1));
            } catch (NumberFormatException e) {
                Log.e(TAG, "NumberFormatException in getSupportedExposureCompensation");
            }
            Float floatObj = (Float) new OperationRequester().request(3, (Object) null);
            if (floatObj != null) {
                step[0] = floatObj.floatValue();
                ExposureCompensationStep.EVStep[] stepIndex = {ExposureCompensationStep.getStepIndex(step[0])};
                int[] stepIndexOrdinal = {stepIndex[0].ordinal()};
                int[][] supportedExposureCompensation = {max, min, stepIndexOrdinal};
                return supportedExposureCompensation;
            }
            return (int[][]) null;
        }
        return (int[][]) null;
    }

    public static Integer getExposureCompensationCurrent() {
        String exposureMode = ParamsGenerator.peekExposureModeParamsSnapshot().currentExposureMode;
        if (exposureMode == null || CameraOperationExposureMode.EXPOSURE_MODE_INTELLIGENT_AUTO.equals(exposureMode)) {
            Log.v(TAG, "Set zero to the current value due to the exposure mode " + exposureMode);
            return 0;
        }
        Integer result = (Integer) new OperationRequester().request(2, (Object) null);
        if (result != null) {
            return Integer.valueOf(result.intValue());
        }
        return null;
    }
}
