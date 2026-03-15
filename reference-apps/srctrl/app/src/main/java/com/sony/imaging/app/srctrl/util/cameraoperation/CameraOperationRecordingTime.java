package com.sony.imaging.app.srctrl.util.cameraoperation;

import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.srctrl.webapi.availability.ParamsGenerator;
import com.sony.imaging.app.srctrl.webapi.specific.ServerEventHandler;
import com.sony.imaging.app.util.NotificationListener;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public class CameraOperationRecordingTime {
    private static final boolean DBG = false;
    private static final int HOUR = 3600;
    private static final int MINUTE = 60;
    private static final String TAG = CameraOperationRecordingTime.class.getSimpleName();
    private static WeakReference<NotificationListener> s_CameraNotificationListenerRef = new WeakReference<>(null);

    public static NotificationListener getNotificationListener() {
        NotificationListener notificationListener = s_CameraNotificationListenerRef.get();
        if (notificationListener == null) {
            NotificationListener notificationListener2 = new NotificationListener() { // from class: com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationRecordingTime.1
                @Override // com.sony.imaging.app.util.NotificationListener
                public String[] getTags() {
                    return new String[]{CameraNotificationManager.MOVIE_REC_TIME_CHANGED, CameraNotificationManager.MOVIE_REC_START_SUCCEEDED, "stopMovieRec", CameraNotificationManager.MOVIE_REC_STOP_EXECUTION_ERROR, CameraNotificationManager.REC_MODE_CHANGED};
                }

                @Override // com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    Integer sec = null;
                    if (CameraNotificationManager.MOVIE_REC_TIME_CHANGED.equals(tag)) {
                        Object obj = CameraNotificationManager.getInstance().getValue(CameraNotificationManager.MOVIE_REC_TIME_CHANGED);
                        if (obj instanceof Integer) {
                            sec = (Integer) obj;
                        }
                    }
                    int second = 0;
                    if (sec != null && (second = sec.intValue()) < 0) {
                        second = 0;
                    }
                    boolean toBeNotified = ParamsGenerator.updateRecordingTimeParams(second);
                    if (toBeNotified) {
                        ServerEventHandler.getInstance().onServerStatusChanged();
                    }
                }
            };
            s_CameraNotificationListenerRef = new WeakReference<>(notificationListener2);
            return notificationListener2;
        }
        return notificationListener;
    }
}
