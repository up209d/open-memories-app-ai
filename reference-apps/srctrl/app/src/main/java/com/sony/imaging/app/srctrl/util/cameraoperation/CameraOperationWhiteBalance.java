package com.sony.imaging.app.srctrl.util.cameraoperation;

import android.util.Log;
import com.sony.imaging.app.base.common.AudioVolumeController;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.srctrl.util.SRCtrlConstants;
import com.sony.imaging.app.srctrl.webapi.availability.ParamsGenerator;
import com.sony.imaging.app.srctrl.webapi.specific.ServerEventHandler;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.WhiteBalanceParamCandidate;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.WhiteBalanceParams;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class CameraOperationWhiteBalance {
    private static final String TAG = CameraOperationWhiteBalance.class.getSimpleName();
    private static final String WHITEBALANCE_AUTO = "Auto WB";
    private static final String WHITEBALANCE_DAYLIGHT = "Daylight";
    private static final String WHITEBALANCE_SHADE = "Shade";
    private static final String WHITEBALANCE_CLOUDY = "Cloudy";
    private static final String WHITEBALANCE_INCANDESCENT = "Incandescent";
    private static final String WHITEBALANCE_FLUORESCENT_WARMWHITE = "Fluorescent: Warm White (-1)";
    private static final String WHITEBALANCE_FLUORESCENT_COOLWHITE = "Fluorescent: Cool White (0)";
    private static final String WHITEBALANCE_FLUORESCENT_DAY = "Fluorescent: Day White (+1)";
    private static final String WHITEBALANCE_FLUORESCENT_DAYLIGHT = "Fluorescent: Daylight (+2)";
    private static final String WHITEBALANCE_FLASH = "Flash";
    private static final String WHITEBALANCE_COLOR_TEMPERATURE = "Color Temperature";
    private static final String WHITEBALANCE_CUSTOM = "Custom";
    private static final String WHITEBALANCE_CUSTOM1 = "Custom 1";
    private static final String WHITEBALANCE_CUSTOM2 = "Custom 2";
    private static final String WHITEBALANCE_CUSTOM3 = "Custom 3";
    private static final String[] WHITEBALANCE_MENU_ORDER = {WHITEBALANCE_AUTO, WHITEBALANCE_DAYLIGHT, WHITEBALANCE_SHADE, WHITEBALANCE_CLOUDY, WHITEBALANCE_INCANDESCENT, WHITEBALANCE_FLUORESCENT_WARMWHITE, WHITEBALANCE_FLUORESCENT_COOLWHITE, WHITEBALANCE_FLUORESCENT_DAY, WHITEBALANCE_FLUORESCENT_DAYLIGHT, WHITEBALANCE_FLASH, WHITEBALANCE_COLOR_TEMPERATURE, WHITEBALANCE_CUSTOM, WHITEBALANCE_CUSTOM1, WHITEBALANCE_CUSTOM2, WHITEBALANCE_CUSTOM3};
    private static WeakReference<NotificationListener> s_NotificationListenerRef = new WeakReference<>(null);
    public static final WhiteBalanceParamCandidate[] s_EMPTY_CANDIDATE = new WhiteBalanceParamCandidate[0];

    public static NotificationListener getNotificationListener() {
        NotificationListener notificationListener = s_NotificationListenerRef.get();
        if (notificationListener == null) {
            NotificationListener notificationListener2 = new NotificationListener() { // from class: com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationWhiteBalance.1
                @Override // com.sony.imaging.app.util.NotificationListener
                public String[] getTags() {
                    return new String[]{CameraNotificationManager.WB_MODE_CHANGE, CameraNotificationManager.WB_DETAIL_CHANGE};
                }

                @Override // com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    WhiteBalanceController wbc = WhiteBalanceController.getInstance();
                    String modeInBase = wbc.getValue();
                    int temperature = -1;
                    WhiteBalanceController.WhiteBalanceParam param = (WhiteBalanceController.WhiteBalanceParam) wbc.getDetailValue();
                    if (modeInBase.equals(WhiteBalanceController.COLOR_TEMP)) {
                        temperature = param.getColorTemp();
                    }
                    String mode = CameraOperationWhiteBalance.getIdFromBase(modeInBase);
                    WhiteBalanceParamCandidate[] available = CameraOperationWhiteBalance.getAvailable();
                    if (mode != null) {
                        boolean toBeNotified = ParamsGenerator.updateWhiteBalanceParams(mode, temperature, available);
                        if (toBeNotified) {
                            ServerEventHandler.getInstance().onServerStatusChanged();
                        }
                    }
                }
            };
            s_NotificationListenerRef = new WeakReference<>(notificationListener2);
            return notificationListener2;
        }
        return notificationListener;
    }

    private static String getBaseIdStr(String param) {
        if (WHITEBALANCE_AUTO.equals(param)) {
            return "auto";
        }
        if (WHITEBALANCE_DAYLIGHT.equals(param)) {
            return WhiteBalanceController.DAYLIGHT;
        }
        if (WHITEBALANCE_SHADE.equals(param)) {
            return WhiteBalanceController.SHADE;
        }
        if (WHITEBALANCE_CLOUDY.equals(param)) {
            return WhiteBalanceController.CLOUDY;
        }
        if (WHITEBALANCE_INCANDESCENT.equals(param)) {
            return WhiteBalanceController.INCANDESCENT;
        }
        if (WHITEBALANCE_FLUORESCENT_WARMWHITE.equals(param)) {
            return WhiteBalanceController.WARM_FLUORESCENT;
        }
        if (WHITEBALANCE_FLUORESCENT_COOLWHITE.equals(param)) {
            return WhiteBalanceController.FLUORESCENT_COOLWHITE;
        }
        if (WHITEBALANCE_FLUORESCENT_DAY.equals(param)) {
            return WhiteBalanceController.FLUORESCENT_DAYWHITE;
        }
        if (WHITEBALANCE_FLUORESCENT_DAYLIGHT.equals(param)) {
            return WhiteBalanceController.FLUORESCENT_DAYLIGHT;
        }
        if (WHITEBALANCE_FLASH.equals(param)) {
            return WhiteBalanceController.FLASH;
        }
        if (WHITEBALANCE_COLOR_TEMPERATURE.equals(param)) {
            return WhiteBalanceController.COLOR_TEMP;
        }
        if (WHITEBALANCE_CUSTOM.equals(param)) {
            return WhiteBalanceController.CUSTOM;
        }
        if (WHITEBALANCE_CUSTOM1.equals(param)) {
            return "custom1";
        }
        if (WHITEBALANCE_CUSTOM2.equals(param)) {
            return "custom2";
        }
        if (WHITEBALANCE_CUSTOM3.equals(param)) {
            return "custom3";
        }
        Log.e(TAG, "Unknown whitebalance parameter name: " + param);
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String getIdFromBase(String baseId) {
        if ("auto".equals(baseId)) {
            return WHITEBALANCE_AUTO;
        }
        if (WhiteBalanceController.DAYLIGHT.equals(baseId)) {
            return WHITEBALANCE_DAYLIGHT;
        }
        if (WhiteBalanceController.SHADE.equals(baseId)) {
            return WHITEBALANCE_SHADE;
        }
        if (WhiteBalanceController.CLOUDY.equals(baseId)) {
            return WHITEBALANCE_CLOUDY;
        }
        if (WhiteBalanceController.INCANDESCENT.equals(baseId)) {
            return WHITEBALANCE_INCANDESCENT;
        }
        if (WhiteBalanceController.WARM_FLUORESCENT.equals(baseId)) {
            return WHITEBALANCE_FLUORESCENT_WARMWHITE;
        }
        if (WhiteBalanceController.FLUORESCENT_COOLWHITE.equals(baseId)) {
            return WHITEBALANCE_FLUORESCENT_COOLWHITE;
        }
        if (WhiteBalanceController.FLUORESCENT_DAYWHITE.equals(baseId)) {
            return WHITEBALANCE_FLUORESCENT_DAY;
        }
        if (WhiteBalanceController.FLUORESCENT_DAYLIGHT.equals(baseId)) {
            return WHITEBALANCE_FLUORESCENT_DAYLIGHT;
        }
        if (WhiteBalanceController.FLASH.equals(baseId)) {
            return WHITEBALANCE_FLASH;
        }
        if (WhiteBalanceController.COLOR_TEMP.equals(baseId)) {
            return WHITEBALANCE_COLOR_TEMPERATURE;
        }
        if (WhiteBalanceController.CUSTOM.equals(baseId)) {
            return WHITEBALANCE_CUSTOM;
        }
        if ("custom1".equals(baseId)) {
            return WHITEBALANCE_CUSTOM1;
        }
        if ("custom2".equals(baseId)) {
            return WHITEBALANCE_CUSTOM2;
        }
        if ("custom3".equals(baseId)) {
            return WHITEBALANCE_CUSTOM3;
        }
        Log.e(TAG, "Unknown BaseApp whitebalance parameter name: " + baseId);
        return null;
    }

    private static int[] getMaxAndMinRange(WhiteBalanceController controller, boolean available) {
        List<String> optionRange;
        if (available) {
            optionRange = controller.getAvailableValue(WhiteBalanceController.SETTING_TEMPERATURE);
        } else {
            optionRange = controller.getSupportedValue(WhiteBalanceController.SETTING_TEMPERATURE);
        }
        if (optionRange == null) {
            return SRCtrlConstants.s_EMPTY_INT_ARRAY;
        }
        int min = AudioVolumeController.INVALID_VALUE;
        int max = AudioVolumeController.INVALID_VALUE;
        for (String option : optionRange) {
            int tmp = Integer.parseInt(option);
            if (Integer.MIN_VALUE == min) {
                min = tmp;
            }
            if (Integer.MIN_VALUE == max) {
                max = tmp;
            }
            if (max < tmp) {
                max = tmp;
            } else if (tmp < min) {
                min = tmp;
            }
        }
        return new int[]{max, min, 100};
    }

    private static boolean isAvailableTemperature(WhiteBalanceParamCandidate availableParam, int temperature) {
        if (availableParam == null || 3 != availableParam.colorTemperatureRange.length) {
            return false;
        }
        int max = availableParam.colorTemperatureRange[0];
        int min = availableParam.colorTemperatureRange[1];
        int step = availableParam.colorTemperatureRange[2];
        if (min > temperature || temperature > max || step == 0) {
            return false;
        }
        int residue = (temperature - min) % step;
        if (residue != 0) {
            return false;
        }
        return true;
    }

    public static boolean isAvailableParam(WhiteBalanceParams param, Boolean optionEnabled) {
        if (param.whiteBalanceMode == null) {
            return false;
        }
        WhiteBalanceParamCandidate[] avalableList = getAvailable();
        for (WhiteBalanceParamCandidate cur : avalableList) {
            if (cur.whiteBalanceMode.equals(param.whiteBalanceMode)) {
                if (WHITEBALANCE_COLOR_TEMPERATURE.equals(param.whiteBalanceMode) && optionEnabled.booleanValue() && !isAvailableTemperature(cur, param.colorTemperature.intValue())) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    public static boolean set(WhiteBalanceParams param, Boolean optionEnabled) {
        String baseId;
        if (!isAvailableParam(param, optionEnabled) || (baseId = getBaseIdStr(param.whiteBalanceMode)) == null) {
            return false;
        }
        WhiteBalanceController controller = WhiteBalanceController.getInstance();
        controller.setValue(WhiteBalanceController.WHITEBALANCE, baseId);
        String result = controller.getValue(WhiteBalanceController.WHITEBALANCE);
        if (!result.equals(baseId)) {
            Log.e(TAG, "Couldn't set white balance mode properly:" + baseId + "<-" + result);
            return false;
        }
        if (!WHITEBALANCE_COLOR_TEMPERATURE.equals(param.whiteBalanceMode) || !optionEnabled.booleanValue()) {
            return true;
        }
        WhiteBalanceController.WhiteBalanceParam detailedParam = (WhiteBalanceController.WhiteBalanceParam) controller.getDetailValue();
        detailedParam.setColorTemp(param.colorTemperature.intValue());
        controller.setDetailValue(detailedParam);
        WhiteBalanceController.WhiteBalanceParam resultParam = (WhiteBalanceController.WhiteBalanceParam) controller.getDetailValue();
        if (resultParam.getColorTemp() == param.colorTemperature.intValue()) {
            return true;
        }
        Log.e(TAG, "Couldn't set color temperature properly:" + param.colorTemperature + "<-" + resultParam.getColorTemp());
        return false;
    }

    public static WhiteBalanceParams get() {
        WhiteBalanceController controller = WhiteBalanceController.getInstance();
        String baseId = controller.getValue(WhiteBalanceController.WHITEBALANCE);
        String id = getIdFromBase(baseId);
        if (id == null) {
            return null;
        }
        WhiteBalanceParams ret = new WhiteBalanceParams();
        ret.whiteBalanceMode = id;
        if (WhiteBalanceController.COLOR_TEMP.equals(baseId)) {
            String temperature = controller.getValue(WhiteBalanceController.COLOR_TEMP);
            ret.colorTemperature = Integer.valueOf(Integer.parseInt(temperature));
            return ret;
        }
        ret.colorTemperature = -1;
        return ret;
    }

    public static WhiteBalanceParamCandidate[] getAvailable() {
        String exposureMode = CameraOperationExposureMode.get();
        if (exposureMode != null && !CameraOperationExposureMode.EXPOSURE_MODE_INTELLIGENT_AUTO.equals(exposureMode)) {
            return getAvailableOrSupportd(true);
        }
        Log.v(TAG, "Set empty array to the available value due to the exposure mode " + exposureMode);
        return s_EMPTY_CANDIDATE;
    }

    public static WhiteBalanceParamCandidate[] getSupportd() {
        String exposureMode = CameraOperationExposureMode.get();
        if (exposureMode != null && !CameraOperationExposureMode.EXPOSURE_MODE_INTELLIGENT_AUTO.equals(exposureMode)) {
            return getAvailableOrSupportd(false);
        }
        Log.v(TAG, "Set empty array to the supported value due to the exposure mode " + exposureMode);
        return s_EMPTY_CANDIDATE;
    }

    private static WhiteBalanceParamCandidate[] getAvailableOrSupportd(boolean bAvailable) {
        List<String> baseCandidate;
        List<WhiteBalanceParamCandidate> ret = new ArrayList<>();
        WhiteBalanceController controller = WhiteBalanceController.getInstance();
        if (bAvailable) {
            baseCandidate = controller.getAvailableValue(WhiteBalanceController.WHITEBALANCE);
        } else {
            baseCandidate = controller.getSupportedValue(WhiteBalanceController.WHITEBALANCE);
        }
        if (baseCandidate != null) {
            for (String baseId : baseCandidate) {
                String id = getIdFromBase(baseId);
                if (id != null) {
                    WhiteBalanceParamCandidate candidate = new WhiteBalanceParamCandidate();
                    candidate.whiteBalanceMode = id;
                    if (WhiteBalanceController.COLOR_TEMP.equals(baseId)) {
                        candidate.colorTemperatureRange = getMaxAndMinRange(controller, false);
                    } else {
                        candidate.colorTemperatureRange = SRCtrlConstants.s_EMPTY_INT_ARRAY;
                    }
                    ret.add(candidate);
                }
            }
        }
        int index = 0;
        int size = ret.size();
        WhiteBalanceParamCandidate[] tmp = new WhiteBalanceParamCandidate[size];
        String[] arr$ = WHITEBALANCE_MENU_ORDER;
        for (String aMode : arr$) {
            for (int i = 0; i < size && index < size; i++) {
                WhiteBalanceParamCandidate param = ret.get(i);
                String thisMode = param.whiteBalanceMode;
                if (aMode.equals(thisMode)) {
                    tmp[index] = param;
                    index++;
                }
            }
            if (index == size) {
                break;
            }
        }
        return tmp;
    }
}
