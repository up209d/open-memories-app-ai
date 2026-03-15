package com.sony.imaging.app.base.shooting;

import android.os.Bundle;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;
import com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class SelfTimerCaptureState extends CaptureStateBase {
    protected Bundle mBundle;
    private BaseShootingExecutor mExecutor;
    private boolean mIsShuttered = false;

    @Override // com.sony.imaging.app.base.shooting.CaptureStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        openLayout(StateBase.FOCUS_LAYOUT);
        MediaNotificationManager.getInstance().hold(false);
        this.mExecutor = ExecutorCreator.getInstance().getSequence();
        if (isTakePictureByRemote()) {
            this.mExecutor.startSelfTimerShutter(1);
        } else {
            this.mExecutor.startSelfTimerShutter();
        }
        CameraNotificationManager.getInstance().requestNotify(CameraNotificationManager.SELFTIMER_COUNTDOWN_STATUS, true);
        this.mIsShuttered = false;
        this.mBundle = null;
    }

    @Override // com.sony.imaging.app.base.shooting.CaptureStateBase
    public void onShuttered(int status, CameraEx cam) {
        super.onShuttered(status, cam);
        if (!ExecutorCreator.getInstance().getSequence().isBulbOpened()) {
            DriveModeController cntl = DriveModeController.getInstance();
            if (cntl.isRemoteControl()) {
                cntl.setTempSelfTimer(0);
            }
        }
        this.mIsShuttered = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.CaptureStateBase
    public Bundle getBundle() {
        Bundle bundle = super.getBundle();
        if (this.mCaptureStatus != 0 && bundle == null) {
            return this.mBundle;
        }
        return bundle;
    }

    public void cancelSelfTimer(Bundle bundle) {
        this.mExecutor.cancelSelfTimerShutter();
        this.mBundle = bundle;
    }

    @Override // com.sony.imaging.app.base.shooting.CaptureStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        if (!this.mIsShuttered) {
            this.mExecutor.cancelSelfTimerShutter();
        }
        this.mBundle = null;
        closeLayout(StateBase.FOCUS_LAYOUT);
        CameraNotificationManager.getInstance().requestNotify(CameraNotificationManager.SELFTIMER_COUNTDOWN_STATUS, false);
        super.onPause();
    }
}
