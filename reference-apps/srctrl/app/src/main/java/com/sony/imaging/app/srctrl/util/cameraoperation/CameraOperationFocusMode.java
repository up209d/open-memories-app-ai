package com.sony.imaging.app.srctrl.util.cameraoperation;

import android.util.Log;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.srctrl.util.SRCtrlConstants;
import com.sony.imaging.app.srctrl.util.SRCtrlEnvironment;
import com.sony.imaging.app.srctrl.webapi.availability.ParamsGenerator;
import com.sony.imaging.app.srctrl.webapi.specific.ServerEventHandler;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/* loaded from: classes.dex */
public class CameraOperationFocusMode {
    private static final String AF_SELECT_AFA = "AF-A";
    public static final String AF_SELECT_AFC = "AF-C";
    public static final String AF_SELECT_AFS = "AF-S";
    private static final String EMPTY_STRING = "";
    public static final String FOCUS_MODE_DMF = "DMF";
    public static final String FOCUS_MODE_MF = "MF";
    public static final String FOCUS_MODE_SMF = "SMF";
    private static final String TAG = CameraOperationFocusMode.class.getSimpleName();
    private static WeakReference<NotificationListener> s_NotificationListenerRef = new WeakReference<>(null);

    static /* synthetic */ boolean access$100() {
        return isFocusControl();
    }

    public static NotificationListener getNotificationListener() {
        NotificationListener notificationListener = s_NotificationListenerRef.get();
        if (notificationListener != null) {
            Log.v(TAG, "getNotificationListener() notificationListener not null");
            return notificationListener;
        }
        NotificationListener notificationListener2 = new NotificationListener() { // from class: com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationFocusMode.1
            @Override // com.sony.imaging.app.util.NotificationListener
            public String[] getTags() {
                return new String[]{CameraNotificationManager.FOCUS_CHANGE, "AutoFocusMode", CameraNotificationManager.DRIVE_MODE, CameraNotificationManager.SCENE_MODE, CameraNotificationManager.DEVICE_LENS_CHANGED, CameraNotificationManager.DEVICE_LENS_AFMF_SW_CHANGED, CameraNotificationManager.REC_MODE_CHANGED};
            }

            @Override // com.sony.imaging.app.util.NotificationListener
            public void onNotify(String tag) {
                Log.v(CameraOperationFocusMode.TAG, "onNotify(" + tag + LogHelper.MSG_CLOSE_BRACKET);
                String mode = CameraOperationFocusMode.get();
                String[] candidatesList = SRCtrlConstants.s_EMPTY_STRING_ARRAY;
                if (!SRCtrlEnvironment.getInstance().isSystemApp()) {
                    candidatesList = CameraOperationFocusMode.getAvailable();
                }
                if (mode != null && candidatesList != null) {
                    boolean toBeNotified = ParamsGenerator.updateFocusModeParams(mode, candidatesList);
                    if (toBeNotified) {
                        ParamsGenerator.setFocusControl(CameraOperationFocusMode.access$100());
                        ParamsGenerator.updateAvailableApiList();
                        ServerEventHandler.getInstance().onServerStatusChanged();
                    }
                }
            }
        };
        s_NotificationListenerRef = new WeakReference<>(notificationListener2);
        return notificationListener2;
    }

    private static boolean isFocusControl() {
        FocusModeController controller = FocusModeController.getInstance();
        return controller.isFocusControl();
    }

    public static String getBaseIdStr(String param) {
        if (FOCUS_MODE_DMF.equals(param)) {
            return FocusModeController.DMF;
        }
        if (AF_SELECT_AFC.equals(param)) {
            return "af-c";
        }
        if (AF_SELECT_AFS.equals(param)) {
            return "af-s";
        }
        if (FOCUS_MODE_MF.equals(param)) {
            return FocusModeController.MANUAL;
        }
        Log.e(TAG, "Unknown focus mode parameter name: " + param);
        return null;
    }

    public static String getIdFromBase(String baseId) {
        if (FocusModeController.DMF.equals(baseId)) {
            return FOCUS_MODE_DMF;
        }
        if ("af-c".equals(baseId)) {
            return AF_SELECT_AFC;
        }
        if ("af-s".equals(baseId)) {
            return AF_SELECT_AFS;
        }
        if (FocusModeController.MANUAL.equals(baseId)) {
            return FOCUS_MODE_MF;
        }
        Log.e(TAG, "Unknown BaseApp focus mode parameter name: " + baseId);
        return null;
    }

    public static String[] getIdFromBase(List<String> baseIdList) {
        if (baseIdList == null) {
            return SRCtrlConstants.s_EMPTY_STRING_ARRAY;
        }
        ArrayList<String> ret = new ArrayList<>();
        for (String focusModeInBase : baseIdList) {
            String mode = getIdFromBase(focusModeInBase);
            if (mode != null) {
                ret.add(mode);
            }
        }
        Collections.sort(ret, new ComparatorFocusMode());
        return (String[]) ret.toArray(SRCtrlConstants.s_EMPTY_STRING_ARRAY);
    }

    public static boolean set(String mode) {
        String baseId = getBaseIdStr(mode);
        if (baseId == null) {
            return false;
        }
        FocusModeController controller = FocusModeController.getInstance();
        if (!controller.isAvailable(FocusModeController.TAG_FOCUS_MODE)) {
            return false;
        }
        controller.setValue("", baseId);
        return true;
    }

    public static boolean isSWMF() {
        String INH_AFMF_SW_MF;
        if (1 == Environment.getVersionOfHW()) {
            INH_AFMF_SW_MF = "INH_FACTOR_CAM_LENS_AFMF_SW_MF_TYPE_C";
        } else {
            INH_AFMF_SW_MF = "INH_FACTOR_CAM_LENS_AFMF_SW_MF_TYPE_P";
        }
        AvailableInfo.update();
        boolean SW_MF = AvailableInfo.isFactor(INH_AFMF_SW_MF);
        return SW_MF;
    }

    public static String get() {
        FocusModeController controller = FocusModeController.getInstance();
        String baseId = controller.getValue();
        String mode = getIdFromBase(baseId);
        return mode;
    }

    public static String[] getAvailable() {
        FocusModeController controller = FocusModeController.getInstance();
        if (!controller.isAvailable(FocusModeController.TAG_FOCUS_MODE)) {
            Log.v(TAG, "getAvailable: Not Available");
            return SRCtrlConstants.s_EMPTY_STRING_ARRAY;
        }
        if (controller.getSupportedValue("") == null) {
            Log.v(TAG, "getSupportedValue: empty");
            return SRCtrlConstants.s_EMPTY_STRING_ARRAY;
        }
        List<String> availableList = controller.getAvailableValue();
        Log.v(TAG, "getAvailable(Focus Mode):" + availableList.toString());
        return getIdFromBase(availableList);
    }

    public static String[] getSupported() {
        FocusModeController controller = FocusModeController.getInstance();
        List<String> supportedList = controller.getSupportedValue("");
        Log.v(TAG, "getSupportd:" + (supportedList != null ? supportedList.toString() : " n/a"));
        return getIdFromBase(supportedList);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ComparatorFocusMode implements Comparator<String> {
        private ComparatorFocusMode() {
        }

        @Override // java.util.Comparator
        public int compare(String arg0, String arg1) {
            if (numberFocusMode(arg0) == numberFocusMode(arg1)) {
                return 0;
            }
            if (numberFocusMode(arg0) > numberFocusMode(arg1)) {
                return 1;
            }
            return -1;
        }

        private int numberFocusMode(String aspect) {
            if (CameraOperationFocusMode.AF_SELECT_AFS.equals(aspect)) {
                return 0;
            }
            if (CameraOperationFocusMode.AF_SELECT_AFC.equals(aspect)) {
                return 1;
            }
            if (CameraOperationFocusMode.FOCUS_MODE_DMF.equals(aspect)) {
                return 2;
            }
            if (!CameraOperationFocusMode.FOCUS_MODE_MF.equals(aspect)) {
                return 4;
            }
            return 3;
        }
    }
}
