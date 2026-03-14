package com.sony.imaging.app.doubleexposure.shooting.state;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.common.EventParcel;
import com.sony.imaging.app.base.shooting.AutoReviewState;
import com.sony.imaging.app.base.shooting.DevelopmentState;
import com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor;
import com.sony.imaging.app.doubleexposure.common.AppLog;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureConstant;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureUtil;
import com.sony.imaging.app.doubleexposure.playback.state.PreparingPlayState;
import com.sony.imaging.app.doubleexposure.playback.state.ProcessingPlayState;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class DoubleExposureDevelopmentState extends DevelopmentState {
    public static String sTransitShootingToPlayBack = null;
    private final String TAG = AppLog.getClassName();
    private CameraEx.PreviewStartListener previewListenerToPreparing = new CameraEx.PreviewStartListener() { // from class: com.sony.imaging.app.doubleexposure.shooting.state.DoubleExposureDevelopmentState.1
        public void onStart(CameraEx cam) {
            DoubleExposureDevelopmentState.this.switchToPlayback();
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
        ShootingExecutor.setPreviewStartListener(null);
        ShootingExecutor.setPictureReviewStartListener(null);
        super.onPause();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
        super.onDestroy();
    }

    @Override // com.sony.imaging.app.base.shooting.DevelopmentState, com.sony.imaging.app.fw.KeyReceiver
    public boolean handleMessage(Message msg) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void switchToPlayback() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        Bundle bundle = new Bundle();
        Bundle bundleAutoReview = new Bundle();
        if (DoubleExposureUtil.getInstance().getCurrentShootingScreen().equalsIgnoreCase(DoubleExposureConstant.FIRST_SHOOTING)) {
            DoubleExposureUtil.getInstance().setFirstImageTransition(DoubleExposureConstant.FIRST_IMAGE_TRANSIT_SHOOTING);
            DoubleExposureUtil.getInstance().setTransitionFlag(false);
            bundle.putString(sTransitShootingToPlayBack, PreparingPlayState.ID_PREPAIRINGLAYOUT);
        } else {
            bundle.putString(sTransitShootingToPlayBack, ProcessingPlayState.ID_PROCESSINGLAYOUT);
        }
        bundle.putParcelable(AutoReviewState.STATE_NAME, bundleAutoReview);
        bundleAutoReview.putParcelable(EventParcel.KEY_KEYCODE, new EventParcel(AppRoot.USER_KEYCODE.PLAYBACK));
        Activity appRoot = getActivity();
        ((BaseApp) appRoot).changeApp(BaseApp.APP_PLAY, bundle);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }
}
