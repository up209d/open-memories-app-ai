package com.sony.imaging.app.srctrl.util.cameraoperation;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.AELController;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.srctrl.util.SRCtrlConstants;
import com.sony.imaging.app.srctrl.util.SRCtrlEnvironment;
import com.sony.imaging.app.srctrl.webapi.availability.ParamsGenerator;
import com.sony.imaging.app.srctrl.webapi.specific.ServerEventHandler;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.webapi.service.camera.v1_4.common.struct.GetEventIsoSpeedRateParams;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

/* loaded from: classes.dex */
public class CameraOperationIsoNumber {
    private static final String TAG = CameraOperationIsoNumber.class.getSimpleName();
    private static WeakReference<NotificationListener> s_NotificationListenerRef = new WeakReference<>(null);

    public static NotificationListener getNotificationListener() {
        NotificationListener notificationListener = s_NotificationListenerRef.get();
        if (notificationListener == null) {
            NotificationListener notificationListener2 = new NotificationListener() { // from class: com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationIsoNumber.1
                @Override // com.sony.imaging.app.util.NotificationListener
                public String[] getTags() {
                    return new String[]{CameraNotificationManager.ISO_SENSITIVITY, CameraNotificationManager.ISO_SENSITIVITY_AUTO, CameraNotificationManager.AE_LOCK, CameraNotificationManager.SILENT_SHUTTER_SETTING_CHANGED};
                }

                @Override // com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    String value;
                    boolean toBeNotified;
                    if (SRCtrlEnvironment.getInstance().isSystemApp()) {
                        String value2 = CameraOperationIsoNumber.get();
                        if (value2 != null) {
                            boolean toBeNotified2 = ParamsGenerator.updateIsoSpeedRateParams(value2, SRCtrlConstants.s_EMPTY_STRING_ARRAY);
                            if (toBeNotified2) {
                                ServerEventHandler.getInstance().onServerStatusChanged();
                                return;
                            }
                            return;
                        }
                        return;
                    }
                    if (CameraNotificationManager.ISO_SENSITIVITY.equals(tag) || CameraNotificationManager.SILENT_SHUTTER_SETTING_CHANGED.equals(tag)) {
                        String value3 = CameraOperationIsoNumber.get();
                        String[] available = CameraOperationIsoNumber.getAvailable();
                        if (value3 != null) {
                            boolean toBeNotified3 = ParamsGenerator.updateIsoSpeedRateParams(value3, available);
                            if (toBeNotified3) {
                                ServerEventHandler.getInstance().onServerStatusChanged();
                                return;
                            }
                            return;
                        }
                        return;
                    }
                    if ((CameraNotificationManager.ISO_SENSITIVITY_AUTO.equals(tag) || CameraNotificationManager.AE_LOCK.equals(tag)) && (value = CameraOperationIsoNumber.get()) != null) {
                        if (ParamsGenerator.peekIsoSpeedRateParamsSnapshot().isoSpeedRateCandidates == null) {
                            String[] available2 = CameraOperationIsoNumber.getAvailable();
                            toBeNotified = ParamsGenerator.updateIsoSpeedRateParams(value, available2);
                        } else {
                            toBeNotified = ParamsGenerator.updateIsoSpeedRateParams(value);
                        }
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

    public static String get() {
        ISOSensitivityController iso = ISOSensitivityController.getInstance();
        String value = iso.getValue();
        if (value == null) {
            Log.v(TAG, "ISO Sensitivity Info is null. ");
            return null;
        }
        if (value.equals(ISOSensitivityController.ISO_AUTO)) {
            if (AELController.getInstance().isAELock()) {
                value = iso.getAutoValue(null);
                if (value.equals(ISOSensitivityController.ISO_AUTO)) {
                    value = "AUTO";
                }
            } else {
                value = "AUTO";
            }
        }
        return value;
    }

    public static boolean set(String isoNumber) {
        GetEventIsoSpeedRateParams pramas = ParamsGenerator.peekIsoSpeedRateParamsSnapshot();
        if (pramas != null && pramas.isoSpeedRateCandidates != null) {
            List<String> availableList = Arrays.asList(pramas.isoSpeedRateCandidates);
            if (!availableList.contains(isoNumber)) {
                Log.e(TAG, "Set Error : isoNumber=" + isoNumber + " Available=" + availableList);
                return false;
            }
        }
        if ("AUTO".equals(isoNumber)) {
            isoNumber = ISOSensitivityController.ISO_AUTO;
        }
        ISOSensitivityController iso_controller = ISOSensitivityController.getInstance();
        iso_controller.setValue(null, isoNumber);
        return true;
    }

    public static String[] getAvailable() {
        String exposureMode = CameraOperationExposureMode.get();
        if (exposureMode == null || CameraOperationExposureMode.EXPOSURE_MODE_INTELLIGENT_AUTO.equals(exposureMode)) {
            Log.v(TAG, "Set empty string array to the available value due to the exposure mode " + exposureMode);
            return SRCtrlConstants.s_EMPTY_STRING_ARRAY;
        }
        ISOSensitivityController iso_controller = ISOSensitivityController.getInstance();
        List<String> ret = iso_controller.getAvailableValue(null);
        if (ret == null) {
            return SRCtrlConstants.s_EMPTY_STRING_ARRAY;
        }
        String[] array = (String[]) ret.toArray(SRCtrlConstants.s_EMPTY_STRING_ARRAY);
        return changeAutoValueInArray(array);
    }

    public static String[] getSupportd() {
        String exposureMode = CameraOperationExposureMode.get();
        if (exposureMode == null || CameraOperationExposureMode.EXPOSURE_MODE_INTELLIGENT_AUTO.equals(exposureMode)) {
            Log.v(TAG, "Set empty string array to the available value due to the exposure mode " + exposureMode);
            return SRCtrlConstants.s_EMPTY_STRING_ARRAY;
        }
        ISOSensitivityController iso_controller = ISOSensitivityController.getInstance();
        List<String> ret = iso_controller.getSupportedValue(null);
        if (ret == null) {
            return SRCtrlConstants.s_EMPTY_STRING_ARRAY;
        }
        String[] array = (String[]) ret.toArray(SRCtrlConstants.s_EMPTY_STRING_ARRAY);
        return changeAutoValueInArray(array);
    }

    private static String[] changeAutoValueInArray(String[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(ISOSensitivityController.ISO_AUTO)) {
                array[i] = "AUTO";
            }
        }
        return array;
    }
}
