package com.sony.imaging.app.srctrl.util.cameraoperation;

import android.os.SystemClock;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.srctrl.util.Fraction;
import com.sony.imaging.app.srctrl.util.SRCtrlConstants;
import com.sony.imaging.app.srctrl.util.SRCtrlEnvironment;
import com.sony.imaging.app.srctrl.webapi.availability.ParamsGenerator;
import com.sony.imaging.app.srctrl.webapi.definition.ResponseParams;
import com.sony.imaging.app.srctrl.webapi.specific.ServerEventHandler;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.CameraEx;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* loaded from: classes.dex */
public class CameraOperationShutterSpeed {
    private static final String FORMAT_BIG_DIGIT = "%.0f\"";
    private static final String FORMAT_ONE_DIGIT = "%.1f\"";
    private static String FORMAT_SMALL_DIGIT = null;
    private static final int SET_BULB_TIMEOUT = 500;
    private static final float THRESHODL_BIG_OR_ONE = 10.0f;
    private static final float THRESHODL_ONE_OR_SMALL = 0.4f;
    private static Boolean longExposureNRSettingValue;
    private static WeakReference<NotificationListener> s_NotificationListenerRef;
    private static final String TAG = CameraOperationShutterSpeed.class.getSimpleName();
    private static final int[][] SS_TABLE = SRCtrlConstants.getShutterSpeedTable();
    private static final Fraction[] SS_TABLE_FRACTION = new Fraction[SS_TABLE.length];

    static /* synthetic */ boolean access$100() {
        return isLongExposureNR();
    }

    static {
        for (int i = 0; i < SS_TABLE_FRACTION.length; i++) {
            SS_TABLE_FRACTION[i] = new Fraction(SS_TABLE[i][0], SS_TABLE[i][1]);
        }
        longExposureNRSettingValue = null;
        s_NotificationListenerRef = new WeakReference<>(null);
        FORMAT_SMALL_DIGIT = "%s/%s";
    }

    public static NotificationListener getNotificationListener() {
        NotificationListener notificationListener = s_NotificationListenerRef.get();
        if (notificationListener == null) {
            NotificationListener notificationListener2 = new NotificationListener() { // from class: com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationShutterSpeed.1
                @Override // com.sony.imaging.app.util.NotificationListener
                public String[] getTags() {
                    return new String[]{CameraNotificationManager.SHUTTER_SPEED, CameraNotificationManager.SCENE_MODE};
                }

                @Override // com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    String shutterSpeed = CameraOperationShutterSpeed.get();
                    if (shutterSpeed != null) {
                        String[] shutterSpeedCandidates = CameraOperationShutterSpeed.getAvailable();
                        boolean update = ParamsGenerator.updateShutterSpeedParams(shutterSpeed, shutterSpeedCandidates);
                        if (update) {
                            if (!Environment.isCancelExposureSupported) {
                                if ("BULB".equals(shutterSpeed)) {
                                    if (CameraOperationShutterSpeed.longExposureNRSettingValue == null) {
                                        Boolean unused = CameraOperationShutterSpeed.longExposureNRSettingValue = Boolean.valueOf(CameraOperationShutterSpeed.access$100());
                                        Log.v(CameraOperationShutterSpeed.TAG, "longExposureNRSettingValue update. value=" + CameraOperationShutterSpeed.longExposureNRSettingValue);
                                    }
                                    if (CameraOperationShutterSpeed.longExposureNRSettingValue.booleanValue()) {
                                        ParamsGenerator.addContinuousErrorParams(ResponseParams.ERROR_STRING_LONG_EXPOSURE_NOISE_REDUCTION_NOT_ACTIVATED);
                                    } else {
                                        ParamsGenerator.removeContinuousErrorParams(ResponseParams.ERROR_STRING_LONG_EXPOSURE_NOISE_REDUCTION_NOT_ACTIVATED);
                                    }
                                } else {
                                    ParamsGenerator.removeContinuousErrorParams(ResponseParams.ERROR_STRING_LONG_EXPOSURE_NOISE_REDUCTION_NOT_ACTIVATED);
                                }
                            }
                            ParamsGenerator.updateAvailableApiList();
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

    public static void clearExposureNRSettingValueCache() {
        longExposureNRSettingValue = null;
    }

    private static boolean isLongExposureNR() {
        return ((CameraEx.ParametersModifier) CameraSetting.getInstance().getParameters().second).getLongExposureNR();
    }

    public static boolean set(String speed) {
        boolean result = false;
        try {
            String speed2 = SRCtrlEnvironment.getInstance().escapeDecimalPoint(speed);
            if ("0.6\"".equals(speed2)) {
                speed2 = "0.625\"";
            }
            if ("BULB".equals(speed2)) {
                speed2 = SRCtrlConstants.SHUTTER_SPEED_BULB_VALUE;
            }
            Fraction target = new Fraction(speed2);
            CameraSetting camera_setting = CameraSetting.getInstance();
            Pair<Integer, Integer> ss = camera_setting.getShutterSpeed();
            Fraction current = new Fraction(ss);
            if (current.compare(target) == 0) {
                result = true;
            } else {
                int targetIndex = Arrays.binarySearch(SS_TABLE_FRACTION, target, Fraction.COMPARATOR);
                int currentIndex = Arrays.binarySearch(SS_TABLE_FRACTION, current, Fraction.COMPARATOR);
                if (targetIndex < 0) {
                    Log.v(TAG, "[ShutterSpeed] Invalid parameters specified: " + target);
                } else if (currentIndex < 0) {
                    Log.v(TAG, "[ShutterSpeed] Current Shutter Speed is invalid: " + current);
                } else {
                    int adjustOfIndex = currentIndex - targetIndex;
                    Log.v(TAG, "[ShutterSpeed] Index diff between " + current + " and " + target + " is " + adjustOfIndex);
                    camera_setting.getCamera().adjustShutterSpeed(adjustOfIndex);
                    result = true;
                    if (SRCtrlConstants.SHUTTER_SPEED_BULB_VALUE.equals(speed2)) {
                        long startTime = SystemClock.uptimeMillis();
                        while (!"BULB".equals(get())) {
                            Thread.sleep(20L, 0);
                            if (500 + startTime < SystemClock.uptimeMillis()) {
                                result = false;
                                Log.v(TAG, "BULB Setting Error.");
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String get() {
        CameraSetting cameraSetting = CameraSetting.getInstance();
        Pair<Integer, Integer> ss = cameraSetting.getShutterSpeed();
        if (ss != null) {
            return getShutterSpeedStr(((Integer) ss.first).intValue(), ((Integer) ss.second).intValue());
        }
        Log.v(TAG, "ShutterSpeed is null. ");
        return null;
    }

    public static String[] getAvailable() {
        String exposureMode = CameraOperationExposureMode.get();
        if (exposureMode == null || CameraOperationExposureMode.EXPOSURE_MODE_INTELLIGENT_AUTO.equals(exposureMode) || CameraOperationExposureMode.EXPOSURE_MODE_PROGRAM_AUTO.equals(exposureMode) || "Aperture".equals(exposureMode)) {
            Log.v(TAG, "Set empty string array to the available value due to the exposure mode " + exposureMode);
            return SRCtrlConstants.s_EMPTY_STRING_ARRAY;
        }
        List<String> result = new ArrayList<>();
        CameraSetting cameraSetting = CameraSetting.getInstance();
        CameraEx.ShutterSpeedInfo shutterSpeedInfo = cameraSetting.getShutterSpeedInfo();
        if (shutterSpeedInfo == null) {
            Log.v(TAG, "ShutterSpeed Info is null. ");
            return SRCtrlConstants.s_EMPTY_STRING_ARRAY;
        }
        Fraction ssMIN = new Fraction(shutterSpeedInfo.currentAvailableMin_n, shutterSpeedInfo.currentAvailableMin_d);
        Fraction ssMAX = new Fraction(shutterSpeedInfo.currentAvailableMax_n, shutterSpeedInfo.currentAvailableMax_d);
        int[][] shutterSpeedTable = SRCtrlConstants.getShutterSpeedTable();
        boolean maxValueFound = false;
        for (int i = 0; i < shutterSpeedTable.length; i++) {
            Integer ssNumerator = Integer.valueOf(shutterSpeedTable[i][0]);
            Integer ssDenominator = Integer.valueOf(shutterSpeedTable[i][1]);
            Fraction ssCUR = new Fraction(ssNumerator.intValue(), ssDenominator.intValue());
            if (!maxValueFound) {
                if (ssCUR.compare(ssMAX) >= 0) {
                    maxValueFound = true;
                    result.add(0, getShutterSpeedStr(ssNumerator.intValue(), ssDenominator.intValue()));
                }
            } else {
                if (ssCUR.compare(ssMIN) > 0) {
                    break;
                }
                result.add(0, getShutterSpeedStr(ssNumerator.intValue(), ssDenominator.intValue()));
            }
        }
        if (ExecutorCreator.getInstance().isBulbEnabled()) {
            int mode = ExecutorCreator.getInstance().getRecordingMode();
            if (mode == 1 && "Manual".equals(exposureMode)) {
                result.add(0, "BULB");
            }
        }
        return (String[]) result.toArray(SRCtrlConstants.s_EMPTY_STRING_ARRAY);
    }

    public static String[] getSupportd() {
        String exposureMode = CameraOperationExposureMode.get();
        if (exposureMode != null && !CameraOperationExposureMode.EXPOSURE_MODE_INTELLIGENT_AUTO.equals(exposureMode) && !CameraOperationExposureMode.EXPOSURE_MODE_PROGRAM_AUTO.equals(exposureMode) && !"Aperture".equals(exposureMode)) {
            return getAvailable();
        }
        Log.v(TAG, "Set empty string array to the supported value due to the exposure mode " + exposureMode);
        return SRCtrlConstants.s_EMPTY_STRING_ARRAY;
    }

    private static String getShutterSpeedStr(int numerator, int denominator) {
        if (numerator == 65535 && denominator == 1) {
            return "BULB";
        }
        float value = 0.0f;
        if (denominator != 0) {
            float value2 = numerator / denominator;
            BigDecimal bi = new BigDecimal(String.valueOf(value2));
            value = bi.setScale(1, 4).floatValue();
        }
        if (value < THRESHODL_ONE_OR_SMALL) {
            String displayValue = String.format(FORMAT_SMALL_DIGIT, Integer.valueOf(numerator), Integer.valueOf(denominator));
            return displayValue;
        }
        if (value < THRESHODL_BIG_OR_ONE && denominator != 1.0f) {
            String displayValue2 = String.format(FORMAT_ONE_DIGIT, Float.valueOf(value));
            return displayValue2;
        }
        String displayValue3 = String.format(FORMAT_BIG_DIGIT, Float.valueOf(value));
        return displayValue3;
    }
}
