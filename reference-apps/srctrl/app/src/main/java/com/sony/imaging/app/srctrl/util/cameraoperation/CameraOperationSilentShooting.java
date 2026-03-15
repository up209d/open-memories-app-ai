package com.sony.imaging.app.srctrl.util.cameraoperation;

import android.util.Log;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.SilentShutterController;
import com.sony.imaging.app.srctrl.util.SRCtrlConstants;
import com.sony.imaging.app.srctrl.util.SRCtrlNotificationManager;
import com.sony.imaging.app.srctrl.webapi.availability.ParamsGenerator;
import com.sony.imaging.app.srctrl.webapi.specific.ServerEventHandler;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class CameraOperationSilentShooting {
    private static final String SILENT_SHOOTING_OFF = "Off";
    private static final String SILENT_SHOOTING_ON = "On";
    private static final String TAG = CameraOperationSilentShooting.class.getSimpleName();
    private static final String[] SILENT_SHOOTING_MENU_ORDER = {"On", "Off"};
    private static WeakReference<NotificationListener> s_NotificationListenerRef = new WeakReference<>(null);

    public static NotificationListener getNotificationListener() {
        NotificationListener notificationListener = s_NotificationListenerRef.get();
        if (notificationListener == null) {
            NotificationListener notificationListener2 = new NotificationListener() { // from class: com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationSilentShooting.1
                @Override // com.sony.imaging.app.util.NotificationListener
                public String[] getTags() {
                    return new String[]{CameraNotificationManager.SILENT_SHUTTER_SETTING_CHANGED, SRCtrlNotificationManager.SILENT_SHUTTER_INH_FACTOR_CHANGED};
                }

                @Override // com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    Log.v(CameraOperationSilentShooting.TAG, "onNotify + " + tag);
                    String mode = CameraOperationSilentShooting.get();
                    String[] candidatesList = CameraOperationSilentShooting.getAvailable();
                    if (mode != null) {
                        boolean toBeNotified = ParamsGenerator.updateSilentShootingParams(mode, candidatesList);
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
        if ("On".equals(param)) {
            return "on";
        }
        if ("Off".equals(param)) {
            return "off";
        }
        Log.e(TAG, "Unknown flashmode parameter name: " + param);
        return null;
    }

    private static List<String> getIdStrFromBase(List<String> baseIdList) {
        List<String> ret = new ArrayList<>();
        if (baseIdList != null) {
            List<String> tmp = new ArrayList<>();
            for (String baseId : baseIdList) {
                String mode = getIdStrFromBase(baseId);
                if (mode != null) {
                    tmp.add(mode);
                }
            }
            if (tmp != null) {
                String[] arr$ = SILENT_SHOOTING_MENU_ORDER;
                for (String orderMode : arr$) {
                    Iterator i$ = tmp.iterator();
                    while (true) {
                        if (!i$.hasNext()) {
                            break;
                        }
                        if (orderMode.equals(i$.next())) {
                            ret.add(orderMode);
                            break;
                        }
                    }
                }
            }
        }
        return ret;
    }

    private static String getIdStrFromBase(String baseId) {
        if ("on".equals(baseId)) {
            return "On";
        }
        if ("off".equals(baseId)) {
            return "Off";
        }
        Log.e(TAG, "Unknown BaseApp flashmode parameter name: " + baseId);
        return null;
    }

    public static boolean set(String mode) {
        String baseId = getBaseIdStr(mode);
        if (baseId == null) {
            return false;
        }
        SilentShutterController controller = SilentShutterController.getInstance();
        if (!controller.isAvailable(SilentShutterController.TAG_SILENT_SHUTTER)) {
            Log.v(TAG, "isAvailable: false");
            return false;
        }
        controller.setValue(SilentShutterController.TAG_SILENT_SHUTTER, baseId);
        return true;
    }

    public static String get() {
        SilentShutterController controller = SilentShutterController.getInstance();
        String baseId = null;
        try {
            baseId = controller.getValue(SilentShutterController.TAG_SILENT_SHUTTER);
        } catch (IController.NotSupportedException e) {
            e.printStackTrace();
        }
        String mode = getIdStrFromBase(baseId);
        return mode;
    }

    public static String[] getAvailable() {
        SilentShutterController controller = SilentShutterController.getInstance();
        if (12 <= Environment.getVersionPfAPI() && !controller.isAvailable(SilentShutterController.TAG_SILENT_SHUTTER)) {
            Log.v(TAG, "isAvailable: false");
            return SRCtrlConstants.s_EMPTY_STRING_ARRAY;
        }
        List<String> availableList = controller.getAvailableValue(SilentShutterController.TAG_SILENT_SHUTTER);
        List<String> ret = getIdStrFromBase(availableList);
        return (String[]) ret.toArray(SRCtrlConstants.s_EMPTY_STRING_ARRAY);
    }

    public static String[] getSupportd() {
        SilentShutterController controller = SilentShutterController.getInstance();
        List<String> supportedList = controller.getSupportedValue(SilentShutterController.TAG_SILENT_SHUTTER);
        List<String> ret = getIdStrFromBase(supportedList);
        return (String[]) ret.toArray(SRCtrlConstants.s_EMPTY_STRING_ARRAY);
    }
}
