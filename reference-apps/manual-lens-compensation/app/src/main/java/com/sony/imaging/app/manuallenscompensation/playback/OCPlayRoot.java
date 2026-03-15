package com.sony.imaging.app.manuallenscompensation.playback;

import android.util.Log;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.playback.PlayRootContainer;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.manuallenscompensation.commonUtil.AppLog;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCBackUpKey;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCUtil;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class OCPlayRoot extends PlayRootContainer {
    private static String TAG = "OCPlayRoot";
    private NotificationListener mMediaNotificationListener = null;

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer, com.sony.imaging.app.base.playback.base.PlayStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(TAG, AppLog.getMethodName());
        OCUtil.getInstance().setLauncherBooted(false);
        super.onResume();
        if (this.mMediaNotificationListener == null) {
            this.mMediaNotificationListener = new MediaNotificationListener();
        }
        MediaNotificationManager.getInstance().setNotificationListener(this.mMediaNotificationListener);
        int state = MediaNotificationManager.getInstance().getMediaState();
        BackUpUtil.getInstance().setPreference(OCBackUpKey.KEY_BKUP_LAST_MEDIASTATE_FOR_START, Integer.valueOf(state));
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(TAG, AppLog.getMethodName());
        OCUtil.getInstance().setLastCaptureMode(1);
        if (this.mMediaNotificationListener != null) {
            MediaNotificationManager.getInstance().removeNotificationListener(this.mMediaNotificationListener);
            this.mMediaNotificationListener = null;
        }
        super.onPause();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    /* loaded from: classes.dex */
    private class MediaNotificationListener implements NotificationListener {
        private String[] tags;

        private MediaNotificationListener() {
            this.tags = new String[]{MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE};
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            AppLog.info(OCPlayRoot.TAG, AppLog.getMethodName() + " " + tag);
            if (tag.equals(MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE)) {
                int mLastMediaStateForStart = BackUpUtil.getInstance().getPreferenceInt(OCBackUpKey.KEY_BKUP_LAST_MEDIASTATE_FOR_START, 0);
                int state = MediaNotificationManager.getInstance().getMediaState();
                if (mLastMediaStateForStart != state) {
                    BackUpUtil.getInstance().setPreference(OCBackUpKey.KEY_BKUP_LAST_MEDIASTATE_FOR_START, Integer.valueOf(state));
                    AppLog.info(OCPlayRoot.TAG, AppLog.getMethodName() + " " + state);
                    if (state == 0) {
                        OCUtil.getInstance().deleteAllProfile();
                        AppLog.info("SyncDB", "SyncDB " + AppLog.getMethodName() + "  SyncDB  called from service media removed");
                    } else if (state == 2) {
                        OCUtil.getInstance().synchDBonMediaChange();
                    }
                }
            }
        }
    }

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer
    public void changeToShooting(int keyCode) {
        switch (keyCode) {
            case AppRoot.USER_KEYCODE.MOVIE_REC /* 515 */:
            case AppRoot.USER_KEYCODE.UM_MOVIE_REC /* 616 */:
            case AppRoot.USER_KEYCODE.MOVIE_REC_2ND /* 637 */:
            case AppRoot.USER_KEYCODE.IR_MOVIE_REC /* 643 */:
                OCUtil.getInstance().setMovieRecStarted(true);
                break;
            default:
                Log.d(TAG, "changeToShooting keyCode = " + keyCode);
                break;
        }
        super.changeToShooting(keyCode);
    }
}
