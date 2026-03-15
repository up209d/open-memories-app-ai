package com.sony.imaging.app.srctrl.util.cameraoperation;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.srctrl.shooting.ExposureModeControllerEx;
import com.sony.imaging.app.srctrl.util.SRCtrlConstants;
import com.sony.imaging.app.srctrl.webapi.availability.ParamsGenerator;
import com.sony.imaging.app.srctrl.webapi.specific.ServerEventHandler;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.CameraEx;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class CameraOperationSelfTimer {
    private static final String TAG = CameraOperationSelfTimer.class.getSimpleName();
    private static WeakReference<NotificationListener> s_NotificationListenerRef = new WeakReference<>(null);

    public static NotificationListener getNotificationListener() {
        NotificationListener notificationListener = s_NotificationListenerRef.get();
        if (notificationListener == null) {
            NotificationListener notificationListener2 = new NotificationListener() { // from class: com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationSelfTimer.1
                @Override // com.sony.imaging.app.util.NotificationListener
                public String[] getTags() {
                    return new String[]{CameraNotificationManager.SELFTIMER_COUNTDOWN_STATUS, CameraNotificationManager.DRIVE_MODE, CameraNotificationManager.DRO_AUTO_HDR, CameraNotificationManager.PICTURE_EFFECT_CHANGE};
                }

                @Override // com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    int i;
                    List<String> availableBaseValue = DriveModeController.getInstance().getAvailableValue(DriveModeController.SELF_TIMER);
                    if (!availableBaseValue.isEmpty()) {
                        availableBaseValue.add(DriveModeController.SELF_TIMER_OFF);
                    }
                    Integer current = null;
                    if (CameraOperationSelfTimer._isAvailable(availableBaseValue)) {
                        current = CameraOperationSelfTimer.get();
                    }
                    if (current == null) {
                        current = 0;
                    }
                    Integer[] availeblevalue = CameraOperationSelfTimer._getAvailable(availableBaseValue);
                    if (availeblevalue != null) {
                    }
                    int[] available = new int[availeblevalue.length];
                    int len$ = availeblevalue.length;
                    int i$ = 0;
                    int i2 = 0;
                    while (i$ < len$) {
                        Integer value = availeblevalue[i$];
                        if (value != null) {
                            i = i2 + 1;
                            available[i2] = value.intValue();
                        } else {
                            i = i2;
                        }
                        i$++;
                        i2 = i;
                    }
                    if (availeblevalue == null) {
                        available = SRCtrlConstants.s_EMPTY_INT_ARRAY;
                    }
                    boolean toBeNotified = ParamsGenerator.updateSelfTimerParams(current.intValue(), available);
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

    public static Boolean set(int timer) {
        String cmd = convCmdWebapiToDriveMode(timer);
        if (cmd != null) {
            if (DriveModeController.SELF_TIMER_OFF.equals(cmd)) {
                DriveModeController.getInstance().setValue(DriveModeController.DRIVEMODE, DriveModeController.SINGLE);
            } else {
                DriveModeController.getInstance().setValue(DriveModeController.SELF_TIMER, cmd);
            }
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean _isAvailable(List<String> getAvailableValue) {
        return (getAvailableValue == null || getAvailableValue.isEmpty()) ? false : true;
    }

    public static Integer get() {
        Boolean result = Boolean.valueOf(DriveModeController.getInstance().isSelfTimer());
        if (result == null) {
            return null;
        }
        if (result.booleanValue()) {
            Integer ret = Integer.valueOf(((CameraEx.ParametersModifier) CameraSetting.getInstance().getParameters().second).getSelfTimer());
            return ret;
        }
        return 0;
    }

    public static Integer[] getSupported() {
        List<String> dModeList = DriveModeController.getInstance().getSupportedValue(DriveModeController.SELF_TIMER);
        if (!dModeList.isEmpty()) {
            dModeList.add(DriveModeController.SELF_TIMER_OFF);
        }
        Integer[] supportedList = convList(dModeList);
        return supportedList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Integer[] _getAvailable(List<String> getAvailableValue) {
        List<String> list;
        if (_isAvailable(getAvailableValue)) {
            return convList(getAvailableValue);
        }
        ArrayList<Integer> available = new ArrayList<>();
        boolean isSingleAvailable = true;
        String tmpMode = ExposureModeControllerEx.getInstance().getValue(ExposureModeController.EXPOSURE_MODE);
        if (tmpMode != null && "SceneSelectionMode".equals(tmpMode) && ExposureModeController.SPORTS.equals(ExposureModeControllerEx.getInstance().getValue("SceneSelectionMode"))) {
            isSingleAvailable = false;
        }
        boolean isTimerAvailable = false;
        List<String> list2 = DriveModeController.getInstance().getAvailableValue(DriveModeController.DRIVEMODE);
        if (isSingleAvailable) {
            if (list2 != null) {
                for (String str : list2) {
                    if (DriveModeController.SINGLE.equals(str)) {
                        available.add(0);
                    } else if (DriveModeController.SELF_TIMER.equals(str)) {
                        isTimerAvailable = true;
                    }
                }
            }
        } else if (list2 != null) {
            for (String str2 : list2) {
                if (DriveModeController.BURST.equals(str2)) {
                    available.add(0);
                } else if (DriveModeController.SELF_TIMER.equals(str2)) {
                    isTimerAvailable = true;
                }
            }
        }
        if (isTimerAvailable && (list = DriveModeController.getInstance().getAvailableValue(DriveModeController.SELF_TIMER)) != null) {
            for (String str3 : list) {
                if (DriveModeController.SELF_TIMER_2S.equals(str3)) {
                    available.add(2);
                } else if (DriveModeController.SELF_TIMER_10S.equals(str3)) {
                    available.add(10);
                }
                Collections.sort(available);
            }
        }
        Integer[] availableArray = new Integer[available.size()];
        int i = 0;
        Iterator i$ = available.iterator();
        while (i$.hasNext()) {
            Integer value = i$.next();
            availableArray[i] = value;
            i++;
        }
        return availableArray;
    }

    public static Integer[] getAvailable() {
        List<String> availeblevalue = DriveModeController.getInstance().getAvailableValue(DriveModeController.SELF_TIMER);
        if (!availeblevalue.isEmpty()) {
            availeblevalue.add(DriveModeController.SELF_TIMER_OFF);
        }
        return _getAvailable(availeblevalue);
    }

    private static String convCmdWebapiToDriveMode(int req) {
        switch (req) {
            case 0:
                return DriveModeController.SELF_TIMER_OFF;
            case 2:
                return DriveModeController.SELF_TIMER_2S;
            case 10:
                return DriveModeController.SELF_TIMER_10S;
            default:
                Log.e(TAG, "Illegal value " + req);
                return null;
        }
    }

    private static Integer convCmdDriveModeToWebapi(String req) {
        if (DriveModeController.SELF_TIMER_OFF.equals(req)) {
            return 0;
        }
        if (DriveModeController.SELF_TIMER_2S.equals(req)) {
            return 2;
        }
        if (DriveModeController.SELF_TIMER_10S.equals(req)) {
            return 10;
        }
        Log.e(TAG, "Illegal value " + req);
        return null;
    }

    private static Integer[] convList(List<String> req) {
        if (req == null) {
            return null;
        }
        Integer[] res = new Integer[req.size()];
        int i = 0;
        for (String str : req) {
            res[i] = convCmdDriveModeToWebapi(str);
            i++;
        }
        Arrays.sort(res);
        return res;
    }
}
