package com.sony.imaging.app.srctrl.util.cameraoperation;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.DigitalZoomController;
import com.sony.imaging.app.srctrl.util.SRCtrlConstants;
import com.sony.imaging.app.srctrl.webapi.availability.ParamsGenerator;
import com.sony.imaging.app.srctrl.webapi.specific.ServerEventHandler;
import com.sony.imaging.app.util.NotificationListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class CameraOperationZoomSetting {
    private static final String TAG = CameraOperationZoomSetting.class.getSimpleName();
    private static final String ZOOM_SETTING_OPTICAL_ONLY = "Optical Zoom Only";
    private static final String ZOOM_SETTING_SMART = "Smart Zoom Only";
    private static final String ZOOM_SETTING_SUPER_RESOLUTION = "On:Clear Image Zoom";
    private static final String ZOOM_SETTING_DIZITAL_ZOOM = "On:Digital Zoom";
    private static final String[] ZOOM_SETTING_MENU_ORDER = {ZOOM_SETTING_OPTICAL_ONLY, ZOOM_SETTING_SMART, ZOOM_SETTING_SUPER_RESOLUTION, ZOOM_SETTING_DIZITAL_ZOOM};
    private static WeakReference<NotificationListener> s_NotificationListenerRef = new WeakReference<>(null);

    public static NotificationListener getNotificationListener() {
        NotificationListener notificationListener = s_NotificationListenerRef.get();
        if (notificationListener == null) {
            NotificationListener notificationListener2 = new NotificationListener() { // from class: com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationZoomSetting.1
                @Override // com.sony.imaging.app.util.NotificationListener
                public String[] getTags() {
                    return new String[]{CameraNotificationManager.PICTURE_QUALITY, CameraNotificationManager.PICTURE_SIZE, CameraNotificationManager.DIGITAL_ZOOM_MODE_CHANGED};
                }

                @Override // com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    Log.e(CameraOperationZoomSetting.TAG, "onNotify: " + tag);
                    String mode = CameraOperationZoomSetting.get();
                    String[] candidatesList = CameraOperationZoomSetting.getAvailable();
                    if (mode != null) {
                        boolean toBeNotified = ParamsGenerator.updateZoomSettingParams(mode, candidatesList);
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
        if (ZOOM_SETTING_OPTICAL_ONLY.equals(param)) {
            return "digitalZoomModeNotSet";
        }
        if (ZOOM_SETTING_SUPER_RESOLUTION.equals(param)) {
            return DigitalZoomController.DIGITAL_ZOOM_TYPE_SUPER_RESOLUTION;
        }
        if (ZOOM_SETTING_DIZITAL_ZOOM.equals(param)) {
            return DigitalZoomController.DIGITAL_ZOOM_TYPE_ALL;
        }
        if (ZOOM_SETTING_SMART.equals(param)) {
            return DigitalZoomController.DIGITAL_ZOOM_TYPE_SMART;
        }
        Log.e(TAG, "Unknown zoomsetting parameter name: " + param);
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
            String[] arr$ = ZOOM_SETTING_MENU_ORDER;
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
        if ("digitalZoomModeNotSet".equals(baseId)) {
            return ZOOM_SETTING_OPTICAL_ONLY;
        }
        if (DigitalZoomController.DIGITAL_ZOOM_TYPE_SUPER_RESOLUTION.equals(baseId)) {
            return ZOOM_SETTING_SUPER_RESOLUTION;
        }
        if (DigitalZoomController.DIGITAL_ZOOM_TYPE_ALL.equals(baseId)) {
            return ZOOM_SETTING_DIZITAL_ZOOM;
        }
        if (DigitalZoomController.DIGITAL_ZOOM_TYPE_SMART.equals(baseId)) {
            return ZOOM_SETTING_SMART;
        }
        Log.e(TAG, "Unknown BaseApp zoom setting parameter name: " + baseId);
        return null;
    }

    public static boolean set(String mode) {
        String baseId = getBaseIdStr(mode);
        if (baseId == null) {
            return false;
        }
        DigitalZoomController controller = DigitalZoomController.getInstance();
        controller.setValue(DigitalZoomController.TAG_DIGITAL_ZOOM_TYPE, baseId);
        return true;
    }

    public static String get() {
        DigitalZoomController controller = DigitalZoomController.getInstance();
        String baseId = controller.getValue(DigitalZoomController.TAG_DIGITAL_ZOOM_TYPE);
        String mode = getIdStrFromBase(baseId);
        return mode;
    }

    public static String[] getAvailable() {
        DigitalZoomController controller = DigitalZoomController.getInstance();
        if (!controller.isAvailable(DigitalZoomController.TAG_DIGITAL_ZOOM_TYPE)) {
            Log.v(TAG, "getAvailable: Not Available");
            return SRCtrlConstants.s_EMPTY_STRING_ARRAY;
        }
        List<String> availableList = controller.getAvailableValue(DigitalZoomController.TAG_DIGITAL_ZOOM_TYPE);
        List<String> ret = getIdStrFromBase(availableList);
        return (String[]) ret.toArray(SRCtrlConstants.s_EMPTY_STRING_ARRAY);
    }

    public static String[] getSupported() {
        DigitalZoomController controller = DigitalZoomController.getInstance();
        List<String> supportedList = controller.getSupportedValue(DigitalZoomController.TAG_DIGITAL_ZOOM_TYPE);
        List<String> ret = getIdStrFromBase(supportedList);
        return (String[]) ret.toArray(SRCtrlConstants.s_EMPTY_STRING_ARRAY);
    }
}
