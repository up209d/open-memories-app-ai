package com.sony.imaging.app.doubleexposure.playback;

import android.os.Bundle;
import android.view.KeyEvent;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.common.EventParcel;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.playback.PlayRootContainer;
import com.sony.imaging.app.base.shooting.AutoReviewState;
import com.sony.imaging.app.doubleexposure.common.AppLog;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureConstant;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureUtil;
import com.sony.imaging.app.doubleexposure.playback.state.PreparingPlayState;
import com.sony.imaging.app.doubleexposure.playback.state.ProcessingPlayState;
import com.sony.imaging.app.doubleexposure.shooting.state.DoubleExposureDevelopmentState;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class DoubleExposurePlayRootContainer extends PlayRootContainer {
    public static final String ID_DELETE_MULTIPLE = "ID_DELETE_MULTIPLE";
    private final String TAG = AppLog.getClassName();
    private String mPlayBackStateId = null;
    private Bundle mBundleAutoReview = null;

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer, com.sony.imaging.app.base.playback.base.PlayStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onResume();
        if (this.data != null) {
            this.mBundleAutoReview = (Bundle) this.data.getParcelable(AutoReviewState.STATE_NAME);
        }
        Bundle bundle = this.data;
        if (bundle != null) {
            this.mPlayBackStateId = bundle.getString(DoubleExposureDevelopmentState.sTransitShootingToPlayBack);
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mPlayBackStateId = null;
        this.mBundleAutoReview = null;
        DoubleExposureUtil.getInstance().setImageSelection(false);
        super.onDestroy();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer
    public String getStartFunction() {
        EventParcel userCodeAutoReview;
        AppLog.enter(this.TAG, AppLog.getMethodName());
        String retVal = super.getStartFunction();
        if (this.mBundleAutoReview != null && (userCodeAutoReview = (EventParcel) this.mBundleAutoReview.getParcelable(EventParcel.KEY_KEYCODE)) != null && isMemoryCardAvailable() && userCodeAutoReview.mKeyEvent == 207) {
            getRootContainer().setData(PlayRootContainer.PROP_ID_PB_MODE, PlayRootContainer.PB_MODE.SINGLE);
            if (this.mPlayBackStateId.equalsIgnoreCase(PreparingPlayState.ID_PREPAIRINGLAYOUT)) {
                retVal = PreparingPlayState.ID_PREPAIRINGLAYOUT;
            } else if (this.mPlayBackStateId.equalsIgnoreCase(ProcessingPlayState.ID_PROCESSINGLAYOUT)) {
                retVal = ProcessingPlayState.ID_PROCESSINGLAYOUT;
            }
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return retVal;
    }

    /* loaded from: classes.dex */
    private class DoubleExposureMediaMountListener implements NotificationListener {
        private DoubleExposureMediaMountListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            String[] tags = {MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE};
            return tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            AppLog.enter(DoubleExposurePlayRootContainer.this.TAG, AppLog.getMethodName());
            if (MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE.equals(tag)) {
                int state = DoubleExposurePlayRootContainer.this.mMediaNotifier.getMediaState();
                if (state == 0 && DoubleExposurePlayRootContainer.this.mPlayBackStateId != null) {
                    AppLog.info(DoubleExposurePlayRootContainer.this.TAG, "MediaNotificationManager.MEDIA_STATE_NOCARD == state && null != mPlayBackStateId");
                    DoubleExposureUtil.getInstance().setCurrentShootingScreen(DoubleExposureConstant.FIRST_SHOOTING);
                    DoubleExposureUtil.getInstance().setCurrentMenuSelectionScreen(null);
                    DoubleExposurePlayRootContainer.this.changeToShooting();
                } else if (state == 1) {
                    CautionUtilityClass.getInstance().disapperTrigger(Info.CAUTION_ID_DLAPP_PSEUDOREC_NOIMAGE);
                } else if (state == 3 || state == 4) {
                    DoubleExposurePlayRootContainer.this.onMediaError();
                } else {
                    DoubleExposurePlayRootContainer.this.onMediaChanged();
                }
            }
            AppLog.enter(DoubleExposurePlayRootContainer.this.TAG, AppLog.getMethodName());
        }
    }

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer
    protected NotificationListener getMediaMountListener() {
        return new DoubleExposureMediaMountListener();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        int keyCode2 = event.getScanCode();
        int result = super.onKeyDown(keyCode2, event);
        switch (keyCode2) {
            case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
                DoubleExposureUtil.getInstance().setTurnedEVDialInPlayback(true);
                result = 0;
                break;
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return result;
    }
}
