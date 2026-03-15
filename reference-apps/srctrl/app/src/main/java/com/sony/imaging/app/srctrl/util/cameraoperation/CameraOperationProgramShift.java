package com.sony.imaging.app.srctrl.util.cameraoperation;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.srctrl.webapi.availability.ParamsGenerator;
import com.sony.imaging.app.srctrl.webapi.specific.ServerEventHandler;
import com.sony.imaging.app.util.NotificationListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class CameraOperationProgramShift {
    private static final String TAG = CameraOperationProgramShift.class.getSimpleName();
    private static WeakReference<NotificationListener> s_NotificationListenerRef = new WeakReference<>(null);

    public static NotificationListener getNotificationListener() {
        NotificationListener notificationListener = s_NotificationListenerRef.get();
        if (notificationListener == null) {
            NotificationListener notificationListener2 = new NotificationListener() { // from class: com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationProgramShift.1
                @Override // com.sony.imaging.app.util.NotificationListener
                public String[] getTags() {
                    return new String[]{CameraNotificationManager.PROGRAM_LINE_CHANGE};
                }

                @Override // com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    if (tag.equals(CameraNotificationManager.PROGRAM_LINE_CHANGE)) {
                        boolean adjusted = CameraOperationProgramShift.isAdjusted();
                        boolean toBeNotified = ParamsGenerator.updateProgramShiftParams(adjusted);
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

    public static boolean isAdjusted() {
        String exposureMode = CameraOperationExposureMode.get();
        if (CameraOperationExposureMode.EXPOSURE_MODE_PROGRAM_AUTO.equals(exposureMode)) {
            return CameraSetting.getInstance().isProgramLineAdjusted();
        }
        Log.v(TAG, "Set FALSE to the adjusted value due to the exposure mode " + exposureMode);
        return false;
    }

    public static List<Boolean> set(int step) {
        List<Boolean> result = new ArrayList<>();
        boolean ret = false;
        try {
            CameraSetting camera_setting = CameraSetting.getInstance();
            if (step > 0) {
                for (int i = 0; i < step; i++) {
                    camera_setting.incrementProgramLine();
                    Thread.sleep(5L);
                }
            } else if (step < 0) {
                int r_step = step * (-1);
                for (int i2 = 0; i2 < r_step; i2++) {
                    camera_setting.decrementProgramLine();
                    Thread.sleep(5L);
                }
            }
            ret = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.add(Boolean.valueOf(ret));
        result.add(Boolean.valueOf(isAdjusted()));
        return result;
    }
}
