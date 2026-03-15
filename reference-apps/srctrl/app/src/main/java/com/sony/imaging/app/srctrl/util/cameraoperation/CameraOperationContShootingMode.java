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
public class CameraOperationContShootingMode {
    public static final String SHOOT_MODE_CONTINUTOUS = "Continuous";
    public static final String SHOOT_MODE_SINGLE = "Single";
    public static final String SHOOT_MODE_SPEED_PRIORITY_BURST = "Spd Priority Cont.";
    private static final String TAG = CameraOperationContShootingMode.class.getSimpleName();
    private static WeakReference<NotificationListener> s_NotificationListenerRef = new WeakReference<>(null);

    public static NotificationListener getNotificationListener() {
        NotificationListener notificationListener = s_NotificationListenerRef.get();
        if (notificationListener == null) {
            NotificationListener notificationListener2 = new NotificationListener() { // from class: com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationContShootingMode.1
                @Override // com.sony.imaging.app.util.NotificationListener
                public String[] getTags() {
                    return new String[]{CameraNotificationManager.DRIVE_MODE, CameraNotificationManager.PICTURE_EFFECT_CHANGE, CameraNotificationManager.DRO_AUTO_HDR};
                }

                @Override // com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    String current = CameraOperationContShootingMode.get();
                    if (current == null) {
                        current = ParamsGenerator.s_INVALID_CONTSHOOTMODE_VALUE;
                    }
                    String[] available = CameraOperationContShootingMode.getAvailable();
                    if (available == null) {
                        available = SRCtrlConstants.s_EMPTY_STRING_ARRAY;
                    }
                    boolean toBeNotified = ParamsGenerator.updateContShootingModeParams(current, available);
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

    public static boolean set(String mode) {
        String cmd;
        if (mode.length() != 0 && (cmd = convCmdWebapiToDriveMode(mode)) != null) {
            DriveModeController.getInstance().setValue(DriveModeController.DRIVEMODE, cmd);
            return true;
        }
        return false;
    }

    public static String get() {
        String getMode = DriveModeController.getInstance().getValue(DriveModeController.DRIVEMODE);
        if (getMode == null) {
            return null;
        }
        String mode = convCmdDriveModeToWebapi(getMode);
        if (DriveModeController.SELF_TIMER.equals(getMode)) {
            return "Single";
        }
        return mode;
    }

    public static String[] getSupportd() {
        int mode = CameraSetting.getInstance().getCurrentMode();
        List<String> dModeList = DriveModeController.getInstance().getSupportedValue(DriveModeController.DRIVEMODE, mode);
        new ArrayList();
        List<String> supportedModeList = convList(dModeList);
        return (String[]) supportedModeList.toArray(SRCtrlConstants.s_EMPTY_STRING_ARRAY);
    }

    public static String[] getAvailable() {
        List<String> dModeList = DriveModeController.getInstance().getAvailableValue(DriveModeController.DRIVEMODE);
        new ArrayList();
        List<String> availableModeList = convList(dModeList);
        return (String[]) availableModeList.toArray(SRCtrlConstants.s_EMPTY_STRING_ARRAY);
    }

    public static boolean enableContShootingMode() {
        return DriveModeController.getInstance().isContShooting();
    }

    private static String convCmdWebapiToDriveMode(String req) {
        if ("Single".equals(req)) {
            return DriveModeController.SINGLE;
        }
        if (SHOOT_MODE_CONTINUTOUS.equals(req)) {
            return DriveModeController.BURST;
        }
        if (!SHOOT_MODE_SPEED_PRIORITY_BURST.equals(req)) {
            return null;
        }
        return DriveModeController.SPEED_PRIORITY_BURST;
    }

    public static String convCmdDriveModeToWebapi(String req) {
        String res = null;
        if (DriveModeController.SINGLE.equals(req)) {
            res = "Single";
        } else if (DriveModeController.BURST.equals(req)) {
            res = SHOOT_MODE_CONTINUTOUS;
        } else if (DriveModeController.SPEED_PRIORITY_BURST.equals(req)) {
            res = SHOOT_MODE_SPEED_PRIORITY_BURST;
        }
        Log.v(TAG, "ParamConvert : " + req + " -> " + res);
        return res;
    }

    private static List<String> convList(List<String> req) {
        List<String> res = new ArrayList<>();
        if (req != null) {
            for (String str : req) {
                String mode = convCmdDriveModeToWebapi(str);
                if (mode != null) {
                    res.add(mode);
                }
            }
        }
        return res;
    }
}
