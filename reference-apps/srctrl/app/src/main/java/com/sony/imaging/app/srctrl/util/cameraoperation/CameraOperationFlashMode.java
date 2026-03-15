package com.sony.imaging.app.srctrl.util.cameraoperation;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.FlashController;
import com.sony.imaging.app.srctrl.util.SRCtrlConstants;
import com.sony.imaging.app.srctrl.webapi.availability.ParamsGenerator;
import com.sony.imaging.app.srctrl.webapi.specific.ServerEventHandler;
import com.sony.imaging.app.util.NotificationListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class CameraOperationFlashMode {
    private static final String FLASH_MODE_AUTO = "auto";
    private static final String FLASH_MODE_OFF = "off";
    private static final String FLASH_MODE_ON = "on";
    private static final String FLASH_MODE_WIRELESS = "wireless";
    private static final String TAG = CameraOperationFlashMode.class.getSimpleName();
    private static final String FLASH_MODE_SLOWSYNC = "slowSync";
    private static final String FLASH_MODE_REARSYNC = "rearSync";
    private static final String[] FLASH_MODE_MENU_ORDER = {"off", "auto", "on", FLASH_MODE_SLOWSYNC, FLASH_MODE_REARSYNC, "wireless"};
    private static WeakReference<NotificationListener> s_NotificationListenerRef = new WeakReference<>(null);

    public static NotificationListener getNotificationListener() {
        NotificationListener notificationListener = s_NotificationListenerRef.get();
        if (notificationListener == null) {
            NotificationListener notificationListener2 = new NotificationListener() { // from class: com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationFlashMode.1
                @Override // com.sony.imaging.app.util.NotificationListener
                public String[] getTags() {
                    return new String[]{CameraNotificationManager.FLASH_CHANGE, CameraNotificationManager.DEVICE_EXTERNAL_FLASH_ONOFF, CameraNotificationManager.DEVICE_INTERNAL_FLASH_ONOFF, CameraNotificationManager.SCENE_MODE, CameraNotificationManager.REC_MODE_CHANGED};
                }

                @Override // com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    if (CameraNotificationManager.FLASH_CHANGE.equals(tag) || CameraNotificationManager.DEVICE_EXTERNAL_FLASH_ONOFF.equals(tag) || CameraNotificationManager.DEVICE_INTERNAL_FLASH_ONOFF.equals(tag) || CameraNotificationManager.SCENE_MODE.equals(tag) || CameraNotificationManager.REC_MODE_CHANGED.equals(tag)) {
                        String mode = CameraOperationFlashMode.get();
                        String[] candidatesList = CameraOperationFlashMode.getAvailable();
                        if (mode != null) {
                            boolean toBeNotified = ParamsGenerator.updateFlashModeParams(mode, candidatesList);
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

    private static String getBaseIdStr(String param) {
        String baseId = null;
        if ("off".equals(param)) {
            return "off";
        }
        if ("auto".equals(param)) {
            return "auto";
        }
        if ("on".equals(param)) {
            return FlashController.FRONTSYNC;
        }
        if (FLASH_MODE_SLOWSYNC.equals(param)) {
            return FlashController.SLOWSYN;
        }
        if (FLASH_MODE_REARSYNC.equals(param)) {
            FlashController controller = FlashController.getInstance();
            List<String> supportedList = controller.getSupportedValue(FlashController.FLASHMODE);
            if (supportedList != null) {
                for (String s : supportedList) {
                    if (FlashController.REARSYN.equals(s) || FlashController.SLOWREARSYN.equals(s)) {
                        baseId = s;
                        break;
                    }
                }
            }
            if (baseId == null) {
                Log.e(TAG, "flashmode REARSYNC not supported: " + param);
                return baseId;
            }
            return baseId;
        }
        if ("wireless".equals(param)) {
            return "wireless";
        }
        Log.e(TAG, "Unknown flashmode parameter name: " + param);
        return null;
    }

    private static List<String> getIdStrFromBase(List<String> baseIdList) {
        List<String> tmp = new ArrayList<>();
        for (String baseId : baseIdList) {
            String mode = getIdStrFromBase(baseId);
            if (mode != null) {
                tmp.add(mode);
            }
        }
        List<String> ret = new ArrayList<>();
        if (tmp != null) {
            String[] arr$ = FLASH_MODE_MENU_ORDER;
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
        return ret;
    }

    private static String getIdStrFromBase(String baseId) {
        if ("off".equals(baseId)) {
            return "off";
        }
        if ("auto".equals(baseId)) {
            return "auto";
        }
        if (FlashController.FRONTSYNC.equals(baseId)) {
            return "on";
        }
        if (FlashController.SLOWSYN.equals(baseId)) {
            return FLASH_MODE_SLOWSYNC;
        }
        if (FlashController.REARSYN.equals(baseId)) {
            return FLASH_MODE_REARSYNC;
        }
        if (FlashController.SLOWREARSYN.equals(baseId)) {
            return FLASH_MODE_REARSYNC;
        }
        if ("wireless".equals(baseId)) {
            return "wireless";
        }
        Log.e(TAG, "Unknown BaseApp flashmode parameter name: " + baseId);
        return null;
    }

    public static boolean set(String mode) {
        String baseId = getBaseIdStr(mode);
        if (baseId == null) {
            return false;
        }
        String[] available = getAvailable();
        if (available != null) {
            List<String> availableList = Arrays.asList(available);
            if (!availableList.contains(mode)) {
                Log.e(TAG, "Set Error : mode=" + mode + " Available=" + availableList);
                return false;
            }
        }
        FlashController controller = FlashController.getInstance();
        controller.setValue(FlashController.FLASHMODE, baseId);
        return true;
    }

    public static String get() {
        FlashController controller = FlashController.getInstance();
        String baseId = controller.getValue(FlashController.FLASHMODE);
        String mode = getIdStrFromBase(baseId);
        return mode;
    }

    public static String[] getAvailable() {
        FlashController controller = FlashController.getInstance();
        if (!controller.isAvailable(FlashController.FLASHMODE)) {
            Log.v(TAG, "getAvailable: Not Available");
            return SRCtrlConstants.s_EMPTY_STRING_ARRAY;
        }
        CameraSetting cameraSetting = CameraSetting.getInstance();
        if (cameraSetting.getFlashExternalEnable() || cameraSetting.getFlashInternalEnable()) {
            List<String> availableList = controller.getAvailableValue(FlashController.FLASHMODE);
            List<String> ret = getIdStrFromBase(availableList);
            return (String[]) ret.toArray(SRCtrlConstants.s_EMPTY_STRING_ARRAY);
        }
        Log.v(TAG, "getAvailable: Flash Not Enabled");
        return SRCtrlConstants.s_EMPTY_STRING_ARRAY;
    }

    public static String[] getSupportd() {
        FlashController controller = FlashController.getInstance();
        List<String> supportedList = controller.getSupportedValue(FlashController.FLASHMODE);
        List<String> ret = getIdStrFromBase(supportedList);
        return (String[]) ret.toArray(SRCtrlConstants.s_EMPTY_STRING_ARRAY);
    }
}
