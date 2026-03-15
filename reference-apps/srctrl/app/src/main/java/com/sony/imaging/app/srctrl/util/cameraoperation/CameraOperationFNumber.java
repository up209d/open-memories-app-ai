package com.sony.imaging.app.srctrl.util.cameraoperation;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.srctrl.util.SRCtrlConstants;
import com.sony.imaging.app.srctrl.util.SRCtrlEnvironment;
import com.sony.imaging.app.srctrl.webapi.availability.ParamsGenerator;
import com.sony.imaging.app.srctrl.webapi.specific.ServerEventHandler;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.CameraEx;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* loaded from: classes.dex */
public class CameraOperationFNumber {
    private static final String FORMAT_BIG_DIGIT = "%.0f";
    private static final String FORMAT_ONE_DIGIT = "%.1f";
    private static final String INVALID_APERTURE_STRING = "--";
    private static final float INVALID_APERTURE_VALUE = 0.0f;
    private static final float THRESHODL_BIG_OR_ONE = 10.0f;
    private static final String TAG = CameraOperationFNumber.class.getSimpleName();
    private static String fNumberSetViaWebApi = null;
    private static WeakReference<NotificationListener> s_NotificationListenerRef = new WeakReference<>(null);

    public static NotificationListener getNotificationListener() {
        NotificationListener notificationListener = s_NotificationListenerRef.get();
        if (notificationListener == null) {
            NotificationListener notificationListener2 = new NotificationListener() { // from class: com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationFNumber.1
                @Override // com.sony.imaging.app.util.NotificationListener
                public String[] getTags() {
                    return 7 <= Environment.getVersionPfAPI() ? new String[]{CameraNotificationManager.DEVICE_LENS_CHANGED} : new String[0];
                }

                @Override // com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    if (CameraNotificationManager.DEVICE_LENS_CHANGED.equals(tag)) {
                        Log.v(CameraOperationFNumber.TAG, "onNotify:" + tag);
                        String fNumber = CameraOperationFNumber.get();
                        String[] candidatesList = CameraOperationFNumber.getAvailable();
                        if (fNumber != null) {
                            boolean toBeNotified = ParamsGenerator.updateFNumberParams(fNumber, candidatesList);
                            if (toBeNotified) {
                                ServerEventHandler.getInstance().onServerStatusChanged();
                            }
                        }
                    }
                }
            };
            s_NotificationListenerRef = new WeakReference<>(notificationListener2);
            return notificationListener2;
        }
        return notificationListener;
    }

    private static String getFNumberString(float value) {
        if (value == INVALID_APERTURE_VALUE) {
            return INVALID_APERTURE_STRING;
        }
        if (value < THRESHODL_BIG_OR_ONE) {
            String ret = String.format(FORMAT_ONE_DIGIT, Float.valueOf(value));
            return ret;
        }
        String ret2 = String.format(FORMAT_BIG_DIGIT, Float.valueOf(value));
        return ret2;
    }

    public static boolean set(String fnumber) {
        boolean result = false;
        try {
            fnumber = SRCtrlEnvironment.getInstance().escapeDecimalPoint(fnumber);
            float target = Float.parseFloat(fnumber);
            CameraSetting camera_setting = CameraSetting.getInstance();
            float current = camera_setting.getAperture() / 100.0f;
            if (current == target) {
                result = true;
            } else {
                float[] F_TABLE = getFNumberTable();
                int targetIndex = Arrays.binarySearch(F_TABLE, target);
                int currentIndex = Arrays.binarySearch(F_TABLE, current);
                if (targetIndex < 0) {
                    Log.v(TAG, "[FNumber] Invalid parameters specified: " + fnumber);
                } else if (currentIndex < 0) {
                    Log.v(TAG, "[FNumber] Current F Number is invalid: " + current);
                } else {
                    int adjustOfIndex = targetIndex - currentIndex;
                    Log.v(TAG, "[FNumber] Index diff between " + current + " and " + fnumber + " is " + adjustOfIndex);
                    camera_setting.getCamera().adjustAperture(adjustOfIndex);
                    result = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (result) {
            setFNumberSetViaWebApi(fnumber);
        }
        return result;
    }

    public static String get() {
        CameraSetting setting = CameraSetting.getInstance();
        return getFNumberString(setting.getAperture() / 100.0f);
    }

    public static String[] getAvailable() {
        List<String> result;
        CameraEx.LensInfo lensInfo;
        String exposureMode = CameraOperationExposureMode.get();
        if (exposureMode == null || CameraOperationExposureMode.EXPOSURE_MODE_INTELLIGENT_AUTO.equals(exposureMode) || CameraOperationExposureMode.EXPOSURE_MODE_PROGRAM_AUTO.equals(exposureMode) || "Shutter".equals(exposureMode)) {
            Log.v(TAG, "Set empty string array to the available value due to the exposure mode " + exposureMode);
            return SRCtrlConstants.s_EMPTY_STRING_ARRAY;
        }
        CameraSetting cameraSetting = CameraSetting.getInstance();
        CameraEx.ApertureInfo fNumberInfo = cameraSetting.getApertureInfo();
        if (fNumberInfo == null) {
            Log.v(TAG, "Aperture Info is null. ");
            return SRCtrlConstants.s_EMPTY_STRING_ARRAY;
        }
        if (INVALID_APERTURE_VALUE == fNumberInfo.currentAperture) {
            Log.v(TAG, "fNumberInfo.currentAperture is invalid. Set the available list to empty.");
            return SRCtrlConstants.s_EMPTY_STRING_ARRAY;
        }
        boolean isLensStatusFixedAperture = false;
        if (7 <= Environment.getVersionPfAPI() && (lensInfo = cameraSetting.getLensInfo()) != null && lensInfo.FixedAperture) {
            isLensStatusFixedAperture = true;
        }
        if (isLensStatusFixedAperture) {
            result = new ArrayList<String>() { // from class: com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationFNumber.2
                {
                    add(CameraOperationFNumber.get());
                }
            };
        } else {
            float fnMIN = fNumberInfo.currentAvailableMin / 100.0f;
            float fnMAX = fNumberInfo.currentAvailableMax / 100.0f;
            result = createFNumberList(fnMIN, fnMAX);
        }
        return (String[]) result.toArray(SRCtrlConstants.s_EMPTY_STRING_ARRAY);
    }

    public static String[] getSupportd() {
        String exposureMode = CameraOperationExposureMode.get();
        if (exposureMode == null || CameraOperationExposureMode.EXPOSURE_MODE_INTELLIGENT_AUTO.equals(exposureMode) || CameraOperationExposureMode.EXPOSURE_MODE_PROGRAM_AUTO.equals(exposureMode) || "Shutter".equals(exposureMode)) {
            Log.v(TAG, "Set empty string array to the available value due to the exposure mode " + exposureMode);
            return SRCtrlConstants.s_EMPTY_STRING_ARRAY;
        }
        CameraSetting cameraSetting = CameraSetting.getInstance();
        CameraEx.LensInfo lensInfo = cameraSetting.getLensInfo();
        if (lensInfo == null) {
            Log.e(TAG, "Lens information is null.");
            return SRCtrlConstants.s_EMPTY_STRING_ARRAY;
        }
        float fnMIN = Math.min(lensInfo.MinFValue.wide, lensInfo.MinFValue.tele) / THRESHODL_BIG_OR_ONE;
        float fnMAX = Math.max(lensInfo.MaxFValue.wide, lensInfo.MaxFValue.tele) / THRESHODL_BIG_OR_ONE;
        List<String> result = createFNumberList(fnMIN, fnMAX);
        return (String[]) result.toArray(SRCtrlConstants.s_EMPTY_STRING_ARRAY);
    }

    public static String getFNumberSetViaWebApi() {
        return fNumberSetViaWebApi;
    }

    public static void setFNumberSetViaWebApi(String fnumber) {
        fNumberSetViaWebApi = fnumber;
    }

    private static List<String> createFNumberList(float min, float max) {
        List<String> result = new ArrayList<>();
        float[] fNumberTable = getFNumberTable();
        boolean minValueFound = false;
        for (float cur : fNumberTable) {
            if (minValueFound) {
                if (max < cur) {
                    break;
                }
                result.add(getFNumberString(cur));
            } else if (min <= cur) {
                minValueFound = true;
                result.add(getFNumberString(cur));
            }
        }
        return result;
    }

    private static float[] getFNumberTable() {
        int minValue = 0;
        CameraEx.ApertureInfo fNumberInfo = CameraSetting.getInstance().getApertureInfo();
        if (fNumberInfo != null) {
            minValue = fNumberInfo.currentAvailableMin;
        }
        return SRCtrlConstants.getFNumberTable(minValue);
    }
}
