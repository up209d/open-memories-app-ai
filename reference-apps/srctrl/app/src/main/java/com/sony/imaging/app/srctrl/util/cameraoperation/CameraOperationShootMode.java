package com.sony.imaging.app.srctrl.util.cameraoperation;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.srctrl.SRCtrl;
import com.sony.imaging.app.srctrl.shooting.state.CaptureStateUtil;
import com.sony.imaging.app.srctrl.util.StateController;
import com.sony.imaging.app.srctrl.webapi.availability.ParamsGenerator;
import com.sony.imaging.app.srctrl.webapi.specific.ServerEventHandler;
import com.sony.imaging.app.srctrl.webapi.specific.ShootingHandler;
import com.sony.imaging.app.util.NotificationListener;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public class CameraOperationShootMode {
    private static final String SHOOT_MODE_MOVIE = "movie";
    private static final String SHOOT_MODE_STILL = "still";
    private static final String TAG = CameraOperationShootMode.class.getSimpleName();
    public static String lastStillExposureMode = null;
    private static WeakReference<NotificationListener> s_NotificationListenerRef = new WeakReference<>(null);

    public static NotificationListener getNotificationListener() {
        NotificationListener notificationListener = s_NotificationListenerRef.get();
        if (notificationListener == null) {
            NotificationListener notificationListener2 = new NotificationListener() { // from class: com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationShootMode.1
                @Override // com.sony.imaging.app.util.NotificationListener
                public String[] getTags() {
                    return new String[]{CameraNotificationManager.REC_MODE_CHANGED, CameraNotificationManager.REC_MODE_CHANGING};
                }

                @Override // com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    if (CameraNotificationManager.REC_MODE_CHANGED.equals(tag) && 1 == ExecutorCreator.getInstance().getRecordingMode()) {
                        CaptureStateUtil.getUtil().term();
                        CaptureStateUtil.getUtil().init();
                        ShootingHandler.getInstance().setShootingStatus(ShootingHandler.ShootingStatus.READY);
                    }
                    if (CameraNotificationManager.REC_MODE_CHANGING.equals(tag)) {
                        StateController.getInstance().setRecordingMode(StateController.RecordingMode.REC_MODE_CHANGING);
                        ParamsGenerator.updateAvailableApiList();
                        ServerEventHandler.getInstance().onServerStatusChanged(true);
                        return;
                    }
                    if (2 == ExecutorCreator.getInstance().getRecordingMode()) {
                        StateController.getInstance().setRecordingMode(StateController.RecordingMode.MOVIE);
                    } else {
                        StateController.getInstance().setRecordingMode(StateController.RecordingMode.STILL);
                    }
                    String mode = CameraOperationShootMode.get();
                    String[] candidatesList = CameraOperationShootMode.getAvailable();
                    if (mode != null) {
                        boolean toBeNotified = ParamsGenerator.updateShootModeParams(mode, candidatesList);
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

    public static void resetLastStillExposureMode() {
        lastStillExposureMode = null;
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
        String currentMode = get();
        if (currentMode.equals(mode)) {
            Log.v(TAG, "The same mode as the present mode was specified. ");
            return true;
        }
        if (!isAvailableIdStr(mode)) {
            return false;
        }
        String currentExposureMode = CameraOperationExposureMode.get();
        ExecutorCreator ec = ExecutorCreator.getInstance();
        if ("still".equals(mode)) {
            Log.v(TAG, "set MOVIE -> STILL");
            ec.setRecordingMode(1, null);
            if (lastStillExposureMode != null) {
                Log.v(TAG, "zenkai-chi set = " + lastStillExposureMode);
                CameraOperationExposureMode.set(lastStillExposureMode);
            } else {
                String stillExposureMode = CameraOperationExposureMode.getConvertStillMode(currentExposureMode);
                Log.v(TAG, "Still ExposureMode from(movie)=" + currentExposureMode + " set(still)=" + stillExposureMode);
                CameraOperationExposureMode.set(stillExposureMode);
            }
            return StateController.getInstance().changeToS1OffEEState();
        }
        Log.v(TAG, "set STILL -> MOVIE");
        if (CameraOperationExposureMode.EXPOSURE_MODE_PROGRAM_AUTO.equals(currentExposureMode)) {
            CameraSetting.getInstance().resetProgramLine();
        }
        ec.setRecordingMode(2, null);
        String movieExposureMode = CameraOperationExposureMode.getConvertMovieMode(currentExposureMode);
        Log.v(TAG, "Movie ExposureMode from(still)=" + currentExposureMode + " set(movie)=" + movieExposureMode);
        CameraOperationExposureMode.set(movieExposureMode);
        lastStillExposureMode = currentExposureMode;
        Log.v(TAG, "current mode = " + lastStillExposureMode);
        return StateController.getInstance().changeToMovieRecStandbyState();
    }

    public static String get() {
        if ((SRCtrl.getRecMode() & 2) == 0) {
            Log.v(TAG, "Movie Mode is Not supported.");
            return "still";
        }
        int mode = ExecutorCreator.getInstance().getRecordingMode();
        if (mode == 2) {
            return "movie";
        }
        if (mode == 1) {
            return "still";
        }
        int backupMode = ExecutorCreator.getInstance().getRecordingModeFromBackup();
        if (backupMode == 2) {
            return "movie";
        }
        if (backupMode == 1) {
            return "still";
        }
        Log.e(TAG, "[Error] shootMode is Not correct!");
        return null;
    }

    public static String[] getAvailable() {
        if ((SRCtrl.getRecMode() & 2) == 0) {
            Log.v(TAG, "Movie Mode is Not supported.");
            return new String[]{"still"};
        }
        if (ModeDialDetector.hasModeDial()) {
            String mode = get();
            if (mode != null) {
                return new String[]{mode};
            }
            return new String[]{"still"};
        }
        return new String[]{"still", "movie"};
    }

    public static String[] getSupportd() {
        if ((SRCtrl.getRecMode() & 2) != 0) {
            return new String[]{"still", "movie"};
        }
        Log.v(TAG, "Movie Mode is Not supported.");
        return new String[]{"still"};
    }
}
