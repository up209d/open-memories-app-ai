package com.sony.imaging.app.srctrl.streaming;

import android.util.Log;
import com.sony.imaging.app.base.playback.contents.ContentsIdentifier;
import com.sony.imaging.app.base.playback.player.MediaPlayerManager;
import com.sony.imaging.app.srctrl.liveview.LiveviewLoader;
import com.sony.imaging.app.srctrl.util.SRCtrlConstants;
import com.sony.imaging.app.srctrl.util.SRCtrlPlaybackUtil;
import com.sony.imaging.app.srctrl.util.StateController;
import com.sony.imaging.app.srctrl.webapi.availability.ParamsGenerator;
import com.sony.imaging.app.srctrl.webapi.servlet.ContentsTransfer;
import com.sony.imaging.app.srctrl.webapi.specific.StreamingStatusEventHandler;
import com.sony.imaging.app.util.NotificationListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class StreamingController {
    private static final String FACTOR_ENDEDGE = "endEdge";
    private static final String FACTOR_FILEERROR = "fileError";
    private static final String FACTOR_MEDIAERROR = "mediaError";
    private static final String FACTOR_OTHERERROR = "otherError";
    private static final String FACTOR_STARTEDGE = "startEdge";
    private static final String FACTOR_UNKNOWN = "";
    private static final String STATUS_ERROR = "error";
    private static final String STATUS_IDLE = "idle";
    private static final String STATUS_PAUSED = "paused";
    private static final String STATUS_PAUSED_BYEDGE = "pausedByEdge";
    private static final String STATUS_STARTED = "started";
    private static String notifyStatus;
    private static final String TAG = StreamingController.class.getSimpleName();
    protected static StreamingController sInstance = new StreamingController();
    private static WeakReference<NotificationListener> s_NotificationListenerRef = new WeakReference<>(null);

    public static StreamingController getInstance() {
        return sInstance;
    }

    public static NotificationListener getNotificationListener() {
        NotificationListener notificationListener = s_NotificationListenerRef.get();
        if (notificationListener == null) {
            NotificationListener notificationListener2 = new NotificationListener() { // from class: com.sony.imaging.app.srctrl.streaming.StreamingController.1
                @Override // com.sony.imaging.app.util.NotificationListener
                public String[] getTags() {
                    return new String[]{MediaPlayerManager.TAG_STATUS_CHANGED};
                }

                @Override // com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    String[] streamingStatus;
                    if (MediaPlayerManager.TAG_STATUS_CHANGED.equals(tag) && (streamingStatus = StreamingController.getStreamingStatus()) != null) {
                        boolean toBeNotified = ParamsGenerator.updateStreamingStatus(streamingStatus);
                        if (toBeNotified) {
                            String unused = StreamingController.notifyStatus = streamingStatus[0];
                            StreamingStatusEventHandler.getInstance().onServerStatusChanged();
                        }
                    }
                }
            };
            s_NotificationListenerRef = new WeakReference<>(notificationListener2);
            return notificationListener2;
        }
        return notificationListener;
    }

    public boolean setContent(String uri) {
        String decUrl;
        if (!StateController.AppCondition.PLAYBACK_CONTENTS_TRANSFER.equals(StateController.getInstance().getAppCondition()) && !StateController.AppCondition.PLAYBACK_STREAMING.equals(StateController.getInstance().getAppCondition())) {
            Log.e(TAG, "Not Availble AppCondition : " + StateController.getInstance().getAppCondition());
            return false;
        }
        if (uri == null || uri.length() == 0 || (decUrl = SRCtrlPlaybackUtil.urlDecode(uri)) == null) {
            return false;
        }
        if (LiveviewLoader.isGeneratingPreview()) {
            Log.e(TAG, "LiveViewLoader started");
            boolean toBeNotified = ParamsGenerator.updateStreamingStatus(getStreamingErrorStatus());
            if (!toBeNotified) {
                return false;
            }
            StreamingStatusEventHandler.getInstance().onServerStatusChanged();
            return false;
        }
        Log.v(TAG, "decUrl:" + decUrl);
        ContentsTransfer.getInstance().terminate();
        MediaPlayerManager.getInstance().initialize(true);
        ContentsIdentifier id = SRCtrlPlaybackUtil.getContentsIdentifier(decUrl);
        boolean setContentResult = false;
        if (id != null) {
            setContentResult = MediaPlayerManager.getInstance().setContent(id);
        } else {
            MediaPlayerManager.getInstance().setStatus(MediaPlayerManager.Status.Error);
        }
        if (setContentResult) {
            boolean success = StreamingLoader.startObtainingImages();
            if (success) {
                StreamingLoader.setLoadingPreview(true);
            } else {
                Log.e(TAG, "startObtainingImages is failed");
                MediaPlayerManager.getInstance().terminate();
            }
            return pause();
        }
        ContentsTransfer.getInstance().initialize();
        return false;
    }

    public boolean start() {
        if (!StateController.AppCondition.PLAYBACK_STREAMING.equals(StateController.getInstance().getAppCondition())) {
            Log.e(TAG, "Not Availble AppCondition : " + StateController.getInstance().getAppCondition());
            return false;
        }
        if (STATUS_PAUSED_BYEDGE.equals(getStatus())) {
            boolean result = MediaPlayerManager.getInstance().moveToFirstFrame();
            if (!result) {
                return false;
            }
        }
        return MediaPlayerManager.getInstance().start();
    }

    public boolean pause() {
        if (!StateController.AppCondition.PLAYBACK_STREAMING.equals(StateController.getInstance().getAppCondition())) {
            Log.e(TAG, "Not Availble AppCondition : " + StateController.getInstance().getAppCondition());
            return false;
        }
        if (STATUS_STARTED.equals(notifyStatus) && STATUS_PAUSED_BYEDGE.equals(getStatus())) {
            return true;
        }
        if (STATUS_PAUSED_BYEDGE.equals(getStatus())) {
            boolean result = MediaPlayerManager.getInstance().moveToFirstFrame();
            if (!result) {
                return false;
            }
        }
        return MediaPlayerManager.getInstance().pause();
    }

    public boolean stop() {
        if (!StateController.AppCondition.PLAYBACK_CONTENTS_TRANSFER.equals(StateController.getInstance().getAppCondition()) && !StateController.AppCondition.PLAYBACK_STREAMING.equals(StateController.getInstance().getAppCondition())) {
            Log.e(TAG, "Not Availble AppCondition : " + StateController.getInstance().getAppCondition());
            return false;
        }
        MediaPlayerManager.getInstance().terminate();
        ContentsTransfer.getInstance().initialize();
        return true;
    }

    public static String getStatus() {
        MediaPlayerManager.Status baseStatus = MediaPlayerManager.getInstance().getStatus();
        if (MediaPlayerManager.Status.Uninitialized.equals(baseStatus) || MediaPlayerManager.Status.Idle.equals(baseStatus) || MediaPlayerManager.Status.Prepared.equals(baseStatus)) {
            return STATUS_IDLE;
        }
        if (MediaPlayerManager.Status.Started.equals(baseStatus)) {
            return STATUS_STARTED;
        }
        if (MediaPlayerManager.Status.PlaybackCompleted.equals(baseStatus)) {
            return STATUS_PAUSED_BYEDGE;
        }
        if (MediaPlayerManager.Status.Paused.equals(baseStatus) || MediaPlayerManager.Status.PausedAtTop.equals(baseStatus)) {
            return STATUS_PAUSED;
        }
        if (MediaPlayerManager.Status.Error.equals(baseStatus)) {
            return STATUS_ERROR;
        }
        Log.v(TAG, "Unknow MediaPalyerManager Status = " + baseStatus);
        return "";
    }

    public static String getFactor() {
        String factor;
        if (MediaPlayerManager.Status.PlaybackCompleted.equals(MediaPlayerManager.getInstance().getStatus())) {
            factor = FACTOR_ENDEDGE;
        } else {
            factor = "";
        }
        Object baseFactor = MediaPlayerManager.getInstance().getValue(MediaPlayerManager.TAG_ON_ERROR);
        MediaPlayerManager.ErrorStatus errorStatus = (MediaPlayerManager.ErrorStatus) baseFactor;
        if (errorStatus != null) {
            if (1 == errorStatus.what) {
                return FACTOR_OTHERERROR;
            }
            if (100 == errorStatus.what) {
                return FACTOR_OTHERERROR;
            }
            if (300 == errorStatus.what) {
                return FACTOR_FILEERROR;
            }
            return FACTOR_OTHERERROR;
        }
        return factor;
    }

    public static String[] getStreamingStatus() {
        ArrayList<String> ret = new ArrayList<>();
        ret.add(getStatus());
        ret.add(getFactor());
        return (String[]) ret.toArray(SRCtrlConstants.s_EMPTY_STRING_ARRAY);
    }

    public String[] getStreamingErrorStatus() {
        ArrayList<String> ret = new ArrayList<>();
        ret.add(STATUS_ERROR);
        ret.add(FACTOR_OTHERERROR);
        return (String[]) ret.toArray(SRCtrlConstants.s_EMPTY_STRING_ARRAY);
    }

    public boolean seekPotision(int seekmsec) {
        if (!StateController.AppCondition.PLAYBACK_STREAMING.equals(StateController.getInstance().getAppCondition())) {
            Log.e(TAG, "Not Availble AppCondition : " + StateController.getInstance().getAppCondition());
            return false;
        }
        if (seekmsec < 0) {
            Log.e(TAG, "Invalid data. seekmsec = " + seekmsec);
            return false;
        }
        if (STATUS_PAUSED.equals(getStatus()) || STATUS_PAUSED_BYEDGE.equals(getStatus())) {
            boolean result = MediaPlayerManager.getInstance().seekTo(seekmsec);
            return result;
        }
        Log.v(TAG, "seek not available. status = " + getStatus());
        return false;
    }
}
