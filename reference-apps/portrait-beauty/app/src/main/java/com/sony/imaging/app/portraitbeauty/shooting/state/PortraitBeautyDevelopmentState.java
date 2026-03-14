package com.sony.imaging.app.portraitbeauty.shooting.state;

import android.app.Activity;
import android.os.Bundle;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.shooting.DevelopmentState;
import com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor;
import com.sony.imaging.app.portraitbeauty.common.AppLog;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyBackUpKey;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyUtil;
import com.sony.imaging.app.portraitbeauty.playback.PortraitBeautyAdjustEffectState;
import com.sony.imaging.app.portraitbeauty.playback.PortraitBeautyReview;
import com.sony.imaging.app.portraitbeauty.shooting.PortraitBeautyDisplayModeObserver;
import com.sony.imaging.app.portraitbeauty.shooting.PortraitBeautyEffectProcess;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class PortraitBeautyDevelopmentState extends DevelopmentState {
    public static String sTransitShootingToPlayBack = null;
    private final String TAG = AppLog.getClassName();
    int cautionId = 0;
    private CameraEx.PreviewStartListener previewListenerToPreparing = new CameraEx.PreviewStartListener() { // from class: com.sony.imaging.app.portraitbeauty.shooting.state.PortraitBeautyDevelopmentState.1
        public void onStart(CameraEx cam) {
            PortraitBeautyDevelopmentState.this.switchToPlayback();
        }
    };

    @Override // com.sony.imaging.app.base.shooting.DevelopmentState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onResume();
        ShootingExecutor.setPreviewStartListener(this.previewListenerToPreparing);
        ShootingExecutor.setPictureReviewStartListener(null);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.DevelopmentState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (true == PortraitBeautyUtil.bIsAdjustModeGuide) {
            ShootingExecutor.setPreviewStartListener(null);
            ShootingExecutor.setPictureReviewStartListener(null);
        }
        super.onPause();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
        this.cautionId = 0;
        super.onDestroy();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void switchToPlayback() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        PortraitBeautyEffectProcess.sIsCaptureStarted = PortraitBeautyEffectProcess.CAPTURE_NOT_STARTED;
        Bundle bundle = new Bundle();
        if (true == PortraitBeautyUtil.bIsAdjustModeGuide) {
            int device = PortraitBeautyDisplayModeObserver.getInstance().getActiveDevice();
            if (device == 0) {
                String backupDispMode = BackUpUtil.getInstance().getPreferenceString(PortraitBeautyBackUpKey.KEY_SELECTED_DISPLAY_MODE_NORMAL_SHOOTING, "1");
                PortraitBeautyDisplayModeObserver.getInstance().setDisplayMode(0, Integer.parseInt(backupDispMode));
            }
            if (device == 1) {
                String backupDispMode2 = BackUpUtil.getInstance().getPreferenceString(PortraitBeautyBackUpKey.KEY_SELECTED_DISPLAY_MODE_NORMAL_SHOOTING_EVF, "1");
                PortraitBeautyDisplayModeObserver.getInstance().setDisplayMode(0, Integer.parseInt(backupDispMode2));
            }
            if (device == 2) {
                String backupDispMode3 = BackUpUtil.getInstance().getPreferenceString(PortraitBeautyBackUpKey.KEY_SELECTED_DISPLAY_MODE_NORMAL_SHOOTING_HDMI, "1");
                PortraitBeautyDisplayModeObserver.getInstance().setDisplayMode(0, Integer.parseInt(backupDispMode3));
            }
            bundle.putString(sTransitShootingToPlayBack, PortraitBeautyAdjustEffectState.ID_ADJUST_EFFECT_STATE);
            bundle.putBoolean("ADJUSTMODE_SHOOTING", true);
            Activity appRoot = getActivity();
            ((BaseApp) appRoot).changeApp(BaseApp.APP_PLAY, bundle);
            this.cautionId = 0;
        } else {
            bundle.putString(sTransitShootingToPlayBack, PortraitBeautyReview.ID_REVIEW);
            Activity appRoot2 = getActivity();
            ((BaseApp) appRoot2).changeApp(BaseApp.APP_PLAY, bundle);
            this.cautionId = 0;
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }
}
