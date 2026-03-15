package com.sony.imaging.app.base.shooting;

import android.os.Bundle;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.KikilogUtil;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor;
import com.sony.imaging.app.util.ApoWrapper;
import com.sony.imaging.app.util.PTag;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class CaptureStateBase extends StateBase {
    public static final String IS_PREVIEW_STARTED = "isPreviewStarted";
    private static final String NEXT_DEVELOPMENT_STATE = "Development";
    protected static final String NEXT_EE_STATE = "EE";
    private static final String PTAG_ON_SHUTTER = "onShutter is called";
    public static final String TAKE_PICTURE_BY_REMOTE = "TakePicByRemote";
    private boolean isPreviewStarted;
    protected int mCaptureStatus;
    protected String mReturnState = null;
    protected CameraEx.ShutterListener shutterListener = new CameraEx.ShutterListener() { // from class: com.sony.imaging.app.base.shooting.CaptureStateBase.1
        public void onShutter(int status, CameraEx cam) {
            CaptureStateBase.this.onShuttered(status, cam);
        }
    };
    protected CameraEx.PreviewStartListener previewListener = new CameraEx.PreviewStartListener() { // from class: com.sony.imaging.app.base.shooting.CaptureStateBase.2
        public void onStart(CameraEx cam) {
            CaptureStateBase.this.isPreviewStarted = true;
        }
    };

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        int displayMode = DisplayModeObserver.getInstance().getActiveDispMode(1);
        if (5 == displayMode && DriveModeController.getInstance().isBlackoutFreeShooting()) {
            displayMode = 1;
        }
        ExecutorCreator.getInstance().getSequence().setPictureReviewInfoHist(5 == displayMode);
        ShootingExecutor.setShutterListener(this.shutterListener);
        ShootingExecutor.setPreviewStartListener(this.previewListener);
        if (this.data != null) {
            this.mReturnState = this.data.getString(StateBase.RETURN_STATE_KEY);
        }
        this.mCaptureStatus = 2;
        this.isPreviewStarted = false;
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        ShootingExecutor.setShutterListener(null);
        ShootingExecutor.setPreviewStartListener(null);
        this.mReturnState = null;
        super.onPause();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onShuttered(int status, CameraEx cam) {
        this.mCaptureStatus = status;
        String state = getNextState();
        Bundle bundle = getBundle();
        if (status == 0) {
            writeKikiLog();
        }
        this.container.setNextState(state, bundle);
        PTag.start(PTAG_ON_SHUTTER);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Bundle getBundle() {
        if (this.mCaptureStatus == 0 || !this.isPreviewStarted) {
            return null;
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean(IS_PREVIEW_STARTED, this.isPreviewStarted);
        return bundle;
    }

    protected void writeKikiLog() {
        KikilogUtil.incrementShootingCount();
    }

    protected String getNextState() {
        int status = this.mCaptureStatus;
        if (status == 0) {
            return NEXT_DEVELOPMENT_STATE;
        }
        if (this.mReturnState == null) {
            return "EE";
        }
        String state = this.mReturnState;
        return state;
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase
    protected int getMyBeepPattern() {
        return 6;
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State
    public ApoWrapper.APO_TYPE getApoType() {
        return ApoWrapper.APO_TYPE.NONE;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isTakePictureByRemote() {
        if (this.data == null) {
            return false;
        }
        boolean ret = this.data.getBoolean("TakePicByRemote");
        return ret;
    }
}
