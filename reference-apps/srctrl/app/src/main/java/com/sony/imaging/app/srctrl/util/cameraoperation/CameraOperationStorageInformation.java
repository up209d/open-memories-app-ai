package com.sony.imaging.app.srctrl.util.cameraoperation;

import android.text.TextUtils;
import android.util.Log;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.srctrl.webapi.availability.ParamsGenerator;
import com.sony.imaging.app.srctrl.webapi.specific.ServerEventHandler;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.StorageInformationParams;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public class CameraOperationStorageInformation {
    private static final String MEMORYCARD_1 = "Memory Card 1";
    private static final String NO_MEDIA = "No Media";
    private static final int ONE_MINITS = 60;
    public static final int s_INVALID_VALUE = -1;
    private static final String TAG = CameraOperationStorageInformation.class.getSimpleName();
    private static String prevStorageId = "No Media";
    private static int s_MediaSate = 0;
    public static final StorageInformationParams[] s_EMPTY_STORAGEINFO_ARRAY = new StorageInformationParams[0];
    public static final StorageInformationParams s_NO_CARD_PARAMS = new StorageInformationParams() { // from class: com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationStorageInformation.1
        {
            this.storageID = "No Media";
            this.recordTarget = true;
            this.numberOfRecordableImages = -1;
            this.recordableTime = -1;
            this.storageDescription = "Storage Media";
        }
    };
    public static final StorageInformationParams s_INVALID_PARAMS = new StorageInformationParams() { // from class: com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationStorageInformation.2
        {
            this.storageID = "Memory Card 1";
            this.recordTarget = true;
            this.numberOfRecordableImages = -1;
            this.recordableTime = -1;
            this.storageDescription = "Storage Media";
        }
    };
    private static WeakReference<NotificationListener> s_CameraNotificationListenerRef = new WeakReference<>(null);
    private static WeakReference<NotificationListener> s_MediaNotificationListenerRef = new WeakReference<>(null);

    public static NotificationListener getNotificationListener() {
        NotificationListener notificationListener = s_CameraNotificationListenerRef.get();
        if (notificationListener == null) {
            NotificationListener notificationListener2 = new NotificationListener() { // from class: com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationStorageInformation.3
                @Override // com.sony.imaging.app.util.NotificationListener
                public String[] getTags() {
                    return new String[]{CameraNotificationManager.APSC_MODE_CHANGED, CameraNotificationManager.REC_MODE_CHANGED};
                }

                @Override // com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    CameraOperationStorageInformation.notifyStorageInformation(tag);
                }
            };
            s_CameraNotificationListenerRef = new WeakReference<>(notificationListener2);
            return notificationListener2;
        }
        return notificationListener;
    }

    public static NotificationListener getMediaNotificationListener() {
        NotificationListener notificationListener = s_MediaNotificationListenerRef.get();
        if (notificationListener == null) {
            NotificationListener notificationListener2 = new NotificationListener() { // from class: com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationStorageInformation.4
                @Override // com.sony.imaging.app.util.NotificationListener
                public String[] getTags() {
                    return new String[]{MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE, MediaNotificationManager.TAG_MEDIA_REMAINING_CHANGE, MediaNotificationManager.TAG_MOVIE_REC_REMAIN_TIME_CHANGED};
                }

                @Override // com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    boolean changed = ParamsGenerator.updatePostviewImageSize();
                    if (changed) {
                        ServerEventHandler.getInstance().onServerStatusChanged();
                    }
                    CameraOperationStorageInformation.notifyStorageInformation(tag);
                }
            };
            s_MediaNotificationListenerRef = new WeakReference<>(notificationListener2);
            return notificationListener2;
        }
        return notificationListener;
    }

    public static void setMediaNotificationListener() {
        MediaNotificationManager mediaNotificationManager = MediaNotificationManager.getInstance();
        mediaNotificationManager.setNotificationListener(getMediaNotificationListener());
    }

    public static void removeMediaNotificationListener() {
        MediaNotificationManager mediaNotificationManager = MediaNotificationManager.getInstance();
        mediaNotificationManager.removeNotificationListener(getMediaNotificationListener());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void notifyStorageInformation(String tag) {
        Log.v(TAG, "onNotify(" + tag + LogHelper.MSG_CLOSE_BRACKET);
        StorageInformationParams[] storageInformationParam = get();
        if (storageInformationParam != null && storageInformationParam.length == 0) {
            storageInformationParam[0] = s_INVALID_PARAMS;
        }
        boolean toBeNotified = ParamsGenerator.updateStorageInformationParams(storageInformationParam[0].storageID, storageInformationParam[0].recordTarget.booleanValue(), storageInformationParam[0].numberOfRecordableImages.intValue(), storageInformationParam[0].recordableTime.intValue(), storageInformationParam[0].storageDescription);
        int mediaState = MediaNotificationManager.getInstance().getMediaState();
        boolean mediaStateChanged = s_MediaSate != mediaState;
        s_MediaSate = mediaState;
        if (toBeNotified || mediaStateChanged) {
            if (!TextUtils.equals(prevStorageId, storageInformationParam[0].storageID) || mediaStateChanged) {
                ParamsGenerator.updateAvailableApiList();
                prevStorageId = storageInformationParam[0].storageID;
            }
            ServerEventHandler.getInstance().onServerStatusChanged();
        }
    }

    public static StorageInformationParams[] get() {
        StorageInformationParams[] ret = new StorageInformationParams[1];
        MediaNotificationManager mediaNotifier = MediaNotificationManager.getInstance();
        if (mediaNotifier == null) {
            return s_EMPTY_STORAGEINFO_ARRAY;
        }
        if (mediaNotifier.isNoCard()) {
            ret[0] = s_NO_CARD_PARAMS;
            return ret;
        }
        if (mediaNotifier.isError()) {
            ret[0] = s_INVALID_PARAMS;
            return ret;
        }
        if (!mediaNotifier.isMounted()) {
            ret[0] = s_INVALID_PARAMS;
            return ret;
        }
        StorageInformationParams storageInformationParam = new StorageInformationParams();
        storageInformationParam.storageID = "Memory Card 1";
        storageInformationParam.recordTarget = true;
        if (1 != CameraSetting.getInstance().getCurrentMode()) {
            storageInformationParam.recordableTime = Integer.valueOf(mediaNotifier.getRemainMovieRecTime() / 60);
            storageInformationParam.numberOfRecordableImages = -1;
        } else {
            storageInformationParam.recordableTime = -1;
            storageInformationParam.numberOfRecordableImages = Integer.valueOf(mediaNotifier.getRemaining());
        }
        storageInformationParam.storageDescription = s_NO_CARD_PARAMS.storageDescription;
        ret[0] = storageInformationParam;
        return ret;
    }
}
