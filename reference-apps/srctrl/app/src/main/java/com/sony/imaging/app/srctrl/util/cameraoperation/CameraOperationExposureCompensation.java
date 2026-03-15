package com.sony.imaging.app.srctrl.util.cameraoperation;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.fw.RunStatus;
import com.sony.imaging.app.srctrl.webapi.availability.ParamsGenerator;
import com.sony.imaging.app.srctrl.webapi.specific.ServerEventHandler;
import com.sony.imaging.app.srctrl.webapi.util.ExposureCompensationStep;
import com.sony.imaging.app.util.NotificationListener;
import java.lang.ref.WeakReference;
import java.util.List;

/* loaded from: classes.dex */
public class CameraOperationExposureCompensation {
    private static final String TAG = CameraOperationExposureCompensation.class.getSimpleName();
    private static WeakReference<NotificationListener> s_NotificationListenerRef = new WeakReference<>(null);
    private static int[] s_Zero3IntArray = {0, 0, 0};

    public static NotificationListener getNotificationListener() {
        NotificationListener notificationListener = s_NotificationListenerRef.get();
        if (notificationListener == null) {
            NotificationListener notificationListener2 = new NotificationListener() { // from class: com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationExposureCompensation.1
                @Override // com.sony.imaging.app.util.NotificationListener
                public String[] getTags() {
                    return new String[]{"ExposureCompensation"};
                }

                @Override // com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    if ("ExposureCompensation".equals(tag)) {
                        Integer current = CameraOperationExposureCompensation.get();
                        if (current == null) {
                            current = 0;
                        }
                        int[] available = CameraOperationExposureCompensation.getAvailable();
                        int maxAvailable = 0;
                        int minAvailable = 0;
                        int availableStep = 0;
                        if (available != null && 3 == available.length) {
                            maxAvailable = available[0];
                            minAvailable = available[1];
                            availableStep = available[2];
                        }
                        boolean toBeNotified = ParamsGenerator.updateExposureCompensationParams(current.intValue(), maxAvailable, minAvailable, availableStep);
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

    public static int[] getAvailable() {
        String exposureMode = ParamsGenerator.peekExposureModeParamsSnapshot().currentExposureMode;
        if (exposureMode == null || CameraOperationExposureMode.EXPOSURE_MODE_INTELLIGENT_AUTO.equals(exposureMode)) {
            Log.e(TAG, "Set invalid array to the available value due to the exposure mode " + exposureMode);
            return s_Zero3IntArray;
        }
        List<String> available = null;
        if (RunStatus.getStatus() == 4) {
            available = ExposureCompensationController.getInstance().getAvailableValue(null);
        }
        if (available == null || available.size() == 0) {
            return null;
        }
        try {
            int min = Integer.parseInt(available.get(0));
            int max = Integer.parseInt(available.get(available.size() - 1));
            Float stepObj = Float.valueOf(ExposureCompensationController.getInstance().getExposureCompensationStep());
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

    public static Integer get() {
        String exposureMode = ParamsGenerator.peekExposureModeParamsSnapshot().currentExposureMode;
        if (exposureMode == null || CameraOperationExposureMode.EXPOSURE_MODE_INTELLIGENT_AUTO.equals(exposureMode)) {
            Log.v(TAG, "Set zero to the current value due to the exposure mode " + exposureMode);
            return 0;
        }
        Integer result = Integer.valueOf(ExposureCompensationController.getInstance().getExposureCompensationIndex());
        if (result != null) {
            return Integer.valueOf(result.intValue());
        }
        return null;
    }
}
