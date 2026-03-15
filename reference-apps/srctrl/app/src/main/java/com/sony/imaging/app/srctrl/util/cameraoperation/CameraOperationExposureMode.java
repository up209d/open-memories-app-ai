package com.sony.imaging.app.srctrl.util.cameraoperation;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.srctrl.shooting.ExposureModeControllerEx;
import com.sony.imaging.app.srctrl.util.SRCtrlConstants;
import com.sony.imaging.app.srctrl.webapi.availability.ParamsGenerator;
import com.sony.imaging.app.srctrl.webapi.specific.ServerEventHandler;
import com.sony.imaging.app.util.NotificationListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class CameraOperationExposureMode {
    public static final String EXPOSURE_MODE_APERATURE = "Aperture";
    public static final String EXPOSURE_MODE_INTELLIGENT_AUTO = "Intelligent Auto";
    public static final String EXPOSURE_MODE_MANUAL = "Manual";
    public static final String EXPOSURE_MODE_PROGRAM_AUTO = "Program Auto";
    public static final String EXPOSURE_MODE_SHUTTER = "Shutter";
    private static final String TAG = CameraOperationExposureMode.class.getSimpleName();
    private static WeakReference<NotificationListener> s_NotificationListenerRef = new WeakReference<>(null);

    private static String getBaseIdStr(String param) {
        int mode = ExecutorCreator.getInstance().getRecordingMode();
        if (mode == 2) {
            if (EXPOSURE_MODE_PROGRAM_AUTO.equals(param)) {
                return ExposureModeController.MOVIE_PROGRAM_AUTO_MODE;
            }
            if ("Aperture".equals(param)) {
                return ExposureModeController.MOVIE_APERATURE_MODE;
            }
            if ("Shutter".equals(param)) {
                return ExposureModeController.MOVIE_SHUTTER_MODE;
            }
            if ("Manual".equals(param)) {
                return ExposureModeController.MOVIE_MANUAL_MODE;
            }
            if (EXPOSURE_MODE_INTELLIGENT_AUTO.equals(param)) {
                return ExposureModeController.MOVIE_AUTO;
            }
            Log.e(TAG, "Unknown parameter name: " + param);
            return null;
        }
        if (EXPOSURE_MODE_PROGRAM_AUTO.equals(param)) {
            return ExposureModeController.PROGRAM_AUTO_MODE;
        }
        if ("Aperture".equals(param)) {
            return "Aperture";
        }
        if ("Shutter".equals(param)) {
            return "Shutter";
        }
        if ("Manual".equals(param)) {
            return "Manual";
        }
        if (EXPOSURE_MODE_INTELLIGENT_AUTO.equals(param)) {
            return ExposureModeController.INTELLIGENT_AUTO_MODE;
        }
        Log.e(TAG, "Unknown parameter name: " + param);
        return null;
    }

    private static List<String> getIdStrFromBase(List<String> baseIdList) {
        List<String> ret = new ArrayList<>();
        if (baseIdList != null) {
            for (String baseId : baseIdList) {
                String mode = getIdStrFromBase(baseId);
                if (mode != null && !"SceneSelectionMode".equals(mode)) {
                    ret.add(mode);
                }
            }
        }
        return ret;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String getIdStrFromBase(String baseId) {
        if (baseId.equals(ExposureModeController.PROGRAM_AUTO_MODE) || baseId.equals(ExposureModeController.MOVIE_PROGRAM_AUTO_MODE)) {
            return EXPOSURE_MODE_PROGRAM_AUTO;
        }
        if (baseId.equals("Aperture") || baseId.equals(ExposureModeController.MOVIE_APERATURE_MODE)) {
            return "Aperture";
        }
        if (baseId.equals("Shutter") || baseId.equals(ExposureModeController.MOVIE_SHUTTER_MODE)) {
            return "Shutter";
        }
        if (baseId.equals("Manual") || baseId.equals(ExposureModeController.MOVIE_MANUAL_MODE)) {
            return "Manual";
        }
        if (baseId.equals(ExposureModeController.INTELLIGENT_AUTO_MODE) || baseId.equals(ExposureModeController.MOVIE_AUTO)) {
            return EXPOSURE_MODE_INTELLIGENT_AUTO;
        }
        Log.e(TAG, "Unknown parameter name: " + baseId);
        return null;
    }

    public static NotificationListener getNotificationListener() {
        NotificationListener notificationListener = s_NotificationListenerRef.get();
        if (notificationListener == null) {
            NotificationListener notificationListener2 = new NotificationListener() { // from class: com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationExposureMode.1
                @Override // com.sony.imaging.app.util.NotificationListener
                public String[] getTags() {
                    return new String[]{CameraNotificationManager.SCENE_MODE, CameraNotificationManager.REC_MODE_CHANGED};
                }

                @Override // com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    String modeInBase;
                    ExposureModeController emc = ExposureModeController.getInstance();
                    int cmode = ExecutorCreator.getInstance().getRecordingMode();
                    if (cmode == 2) {
                        modeInBase = emc.getValue("movie");
                    } else {
                        modeInBase = emc.getValue(ExposureModeController.EXPOSURE_MODE);
                    }
                    String mode = CameraOperationExposureMode.getIdStrFromBase(modeInBase);
                    String[] candidatesList = CameraOperationExposureMode.getAvailable();
                    if (mode != null) {
                        boolean toBeNotified = ParamsGenerator.updateExposureModeParams(mode, candidatesList);
                        if (toBeNotified) {
                            CameraNotificationManager.getInstance().requestNotify(CameraNotificationManager.FOCUS_AREA_INFO);
                            CameraNotificationManager.getInstance().requestNotify("ExposureCompensation");
                            CameraNotificationManager.getInstance().requestNotify(CameraNotificationManager.SHUTTER_SPEED);
                            CameraNotificationManager.getInstance().requestNotify("Aperture");
                            CameraNotificationManager.getInstance().requestNotify(CameraNotificationManager.ISO_SENSITIVITY);
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

    public static boolean isAvailableIdStr(String mode) {
        if (mode == null) {
            return false;
        }
        String[] avalableList = getAvailable();
        for (String cur : avalableList) {
            if (cur.equals(mode)) {
                return true;
            }
        }
        return false;
    }

    public static boolean set(String mode) {
        String baseId;
        String check;
        int cmode = ExecutorCreator.getInstance().getRecordingMode();
        if (!isAvailableIdStr(mode) || (baseId = getBaseIdStr(mode)) == null) {
            return false;
        }
        ExposureModeControllerEx controller = (ExposureModeControllerEx) ExposureModeControllerEx.getInstance();
        if (cmode == 2) {
            controller.setValue("movie", baseId);
            check = controller.getValue("movie");
        } else {
            controller.setValue(ExposureModeController.EXPOSURE_MODE, baseId);
            check = controller.getValue(ExposureModeController.EXPOSURE_MODE);
        }
        return baseId.equals(check);
    }

    public static String get() {
        String baseId;
        ExposureModeControllerEx controller = (ExposureModeControllerEx) ExposureModeControllerEx.getInstance();
        int cmode = ExecutorCreator.getInstance().getRecordingMode();
        if (cmode == 2) {
            baseId = controller.getValue("movie");
        } else {
            baseId = controller.getValue(ExposureModeController.EXPOSURE_MODE);
        }
        String mode = getIdStrFromBase(baseId);
        return mode;
    }

    public static String[] getAvailable() {
        List<String> availableList;
        int cmode = ExecutorCreator.getInstance().getRecordingMode();
        if (ModeDialDetector.hasModeDial() && cmode != 2) {
            return SRCtrlConstants.s_EMPTY_STRING_ARRAY;
        }
        ExposureModeControllerEx controller = (ExposureModeControllerEx) ExposureModeControllerEx.getInstance();
        if (cmode == 2) {
            availableList = controller.getAvailableValue("movie");
        } else {
            availableList = controller.getAvailableValue(ExposureModeController.EXPOSURE_MODE);
        }
        List<String> ret = getIdStrFromBase(availableList);
        return (String[]) ret.toArray(SRCtrlConstants.s_EMPTY_STRING_ARRAY);
    }

    public static String[] getSupportd() {
        List<String> supportedList;
        int cmode = ExecutorCreator.getInstance().getRecordingMode();
        ExposureModeControllerEx controller = (ExposureModeControllerEx) ExposureModeControllerEx.getInstance();
        if (cmode == 2) {
            supportedList = controller.getAvailableValue("movie");
        } else {
            supportedList = controller.getAvailableValue(ExposureModeController.EXPOSURE_MODE);
        }
        List<String> ret = getIdStrFromBase(supportedList);
        return (String[]) ret.toArray(SRCtrlConstants.s_EMPTY_STRING_ARRAY);
    }

    public static String getConvertMovieMode(String stillExposureMode) {
        return getConvertMode("movie", stillExposureMode);
    }

    public static String getConvertStillMode(String movieExposureMode) {
        return getConvertMode(ExposureModeController.EXPOSURE_MODE, movieExposureMode);
    }

    public static String getConvertMode(String toMode, String fromExposureMode) {
        ExposureModeControllerEx controller = (ExposureModeControllerEx) ExposureModeControllerEx.getInstance();
        List<String> availableListFromBase = controller.getAvailableValue(toMode);
        List<String> availableList = getIdStrFromBase(availableListFromBase);
        if (!availableList.contains(fromExposureMode)) {
            if (availableList.contains(EXPOSURE_MODE_INTELLIGENT_AUTO)) {
                return EXPOSURE_MODE_INTELLIGENT_AUTO;
            }
            if (availableList.contains(EXPOSURE_MODE_PROGRAM_AUTO)) {
                return EXPOSURE_MODE_PROGRAM_AUTO;
            }
            return "";
        }
        return fromExposureMode;
    }
}
