package com.sony.imaging.app.srctrl.util.cameraoperation;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;
import com.sony.imaging.app.srctrl.util.SRCtrlConstants;
import com.sony.imaging.app.srctrl.webapi.availability.ParamsGenerator;
import com.sony.imaging.app.srctrl.webapi.specific.ServerEventHandler;
import com.sony.imaging.app.util.NotificationListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class CameraOperationContShootingSpeed {
    public static final String SHOOT_SPEED_HI = "Hi";
    public static final String SHOOT_SPEED_LOW = "Low";
    public static final String SHOOT_SPEED_MID = "Mid";
    private static final String TAG = CameraOperationContShootingSpeed.class.getSimpleName();
    private static WeakReference<NotificationListener> s_NotificationListenerRef = new WeakReference<>(null);

    public static NotificationListener getNotificationListener() {
        NotificationListener notificationListener = s_NotificationListenerRef.get();
        if (notificationListener == null) {
            NotificationListener notificationListener2 = new NotificationListener() { // from class: com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationContShootingSpeed.1
                @Override // com.sony.imaging.app.util.NotificationListener
                public String[] getTags() {
                    return new String[]{CameraNotificationManager.DRIVE_MODE};
                }

                @Override // com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    String current = CameraOperationContShootingSpeed.get();
                    if (current == null) {
                        current = ParamsGenerator.s_INVALID_CONTSHOOTSPEED_VALUE;
                    }
                    String[] available = CameraOperationContShootingSpeed.getAvailable();
                    if (available == null) {
                        available = SRCtrlConstants.s_EMPTY_STRING_ARRAY;
                    }
                    boolean toBeNotified = ParamsGenerator.updateContShootingSpeedParams(current, available);
                    if (toBeNotified) {
                        ServerEventHandler.getInstance().onServerStatusChanged();
                    }
                }
            };
            s_NotificationListenerRef = new WeakReference<>(notificationListener2);
            return notificationListener2;
        }
        return notificationListener;
    }

    public static boolean set(String speed) {
        String cmd;
        if (speed.length() != 0 && (cmd = convCmdWebapiToDriveMode(speed)) != null) {
            DriveModeController.getInstance().setValue(DriveModeController.BURST, cmd);
            return true;
        }
        return false;
    }

    public static String get() {
        String getSpeed;
        if (!isContShootingSpeedSupported() || (getSpeed = DriveModeController.getInstance().getValue(DriveModeController.BURST)) == null) {
            return null;
        }
        String speed = convCmdDriveModeToWebapi(getSpeed);
        return speed;
    }

    public static String[] getSupportd() {
        int mode = CameraSetting.getInstance().getCurrentMode();
        List<String> dSpeedList = DriveModeController.getInstance().getSupportedValue(DriveModeController.BURST, mode);
        new ArrayList();
        List<String> supportedSpeedList = convList(dSpeedList);
        return (String[]) supportedSpeedList.toArray(SRCtrlConstants.s_EMPTY_STRING_ARRAY);
    }

    public static String[] getAvailable() {
        List<String> dSpeedList = DriveModeController.getInstance().getAvailableValue(DriveModeController.BURST);
        new ArrayList();
        List<String> availableSpeedList = convList(dSpeedList);
        return (String[]) availableSpeedList.toArray(SRCtrlConstants.s_EMPTY_STRING_ARRAY);
    }

    private static String convCmdWebapiToDriveMode(String req) {
        if (SHOOT_SPEED_HI.equals(req)) {
            return DriveModeController.BURST_SPEED_HIGH;
        }
        if (SHOOT_SPEED_MID.equals(req)) {
            return DriveModeController.BURST_SPEED_MID;
        }
        if (!SHOOT_SPEED_LOW.equals(req)) {
            return null;
        }
        return "low";
    }

    private static String convCmdDriveModeToWebapi(String req) {
        String res = null;
        if (DriveModeController.BURST_SPEED_HIGH.equals(req)) {
            res = SHOOT_SPEED_HI;
        } else if (DriveModeController.BURST_SPEED_MID.equals(req)) {
            res = SHOOT_SPEED_MID;
        } else if ("low".equals(req)) {
            res = SHOOT_SPEED_LOW;
        }
        Log.v(TAG, "ParamConvert : " + req + " -> " + res);
        return res;
    }

    private static List<String> convList(List<String> req) {
        List<String> res = new ArrayList<>();
        if (req != null) {
            for (String str : req) {
                String speed = convCmdDriveModeToWebapi(str);
                if (speed != null) {
                    res.add(speed);
                }
            }
        }
        return res;
    }

    public static boolean isContShootingSpeedSupported() {
        List<String> supported = DriveModeController.getInstance().getSupportedValue(DriveModeController.BURST);
        if (supported == null || supported.size() <= 0) {
            return false;
        }
        return true;
    }
}
