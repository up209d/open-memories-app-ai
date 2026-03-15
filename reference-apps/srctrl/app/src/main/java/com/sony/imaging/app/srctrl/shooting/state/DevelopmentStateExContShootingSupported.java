package com.sony.imaging.app.srctrl.shooting.state;

import android.os.Bundle;
import com.sony.imaging.app.base.shooting.AutoReviewState;
import com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor;
import com.sony.imaging.app.srctrl.shooting.state.CaptureStateUtilContShootingSupported;
import com.sony.imaging.app.srctrl.util.SRCtrlNotificationManager;
import com.sony.imaging.app.srctrl.util.StateController;
import com.sony.imaging.app.srctrl.webapi.specific.ShootingHandler;
import com.sony.imaging.app.util.PTag;

/* loaded from: classes.dex */
public class DevelopmentStateExContShootingSupported extends DevelopmentStateExBase {
    protected static final String CURRENT_STATE_NAME = "DevelopmentEx";
    private static final String TAG = DevelopmentStateExContShootingSupported.class.getSimpleName();
    private CaptureStateUtilContShootingSupported.SRCtrlShootEndListener mShootEndListener = null;
    private String mNextState = null;

    @Override // com.sony.imaging.app.srctrl.shooting.state.DevelopmentStateExBase, com.sony.imaging.app.base.shooting.DevelopmentState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        this.mNextState = null;
        this.mShootEndListener = new CaptureStateUtilContShootingSupported.SRCtrlShootEndListener() { // from class: com.sony.imaging.app.srctrl.shooting.state.DevelopmentStateExContShootingSupported.1
            @Override // com.sony.imaging.app.srctrl.shooting.state.CaptureStateUtilContShootingSupported.SRCtrlShootEndListener, com.sony.imaging.app.util.NotificationListener
            public void onNotify(String tag) {
                DevelopmentStateExContShootingSupported.this.setNextState("EE", null);
            }
        };
        SRCtrlNotificationManager.getInstance().setNotificationListener(this.mShootEndListener);
        super.onResume();
    }

    @Override // com.sony.imaging.app.srctrl.shooting.state.DevelopmentStateExBase, com.sony.imaging.app.base.shooting.DevelopmentState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        SRCtrlNotificationManager.getInstance().removeNotificationListener(this.mShootEndListener);
        this.mShootEndListener = null;
        if (!AutoReviewState.STATE_NAME.equals(this.mNextState) && !"AutoReviewForLimitedContShooting".equals(this.mNextState) && !CaptureStateEx.STATE_NAME.equals(this.mNextState)) {
            ShootingHandler.getInstance().setShootingStatus(ShootingHandler.ShootingStatus.FINISHED);
        }
        removeData(CaptureStateUtil.KEY_NUMOFBURSTPICTURES);
        removeData(CaptureStateUtil.KEY_NUMOFFINISHEDPICTURES);
        removeData(CaptureStateUtil.KEY_USEFINALPICTURE);
    }

    @Override // com.sony.imaging.app.base.shooting.DevelopmentState
    protected void onPreviewStart() {
        PTag.end("ee start is called");
        if (CaptureStateUtil.isContinuousShooting()) {
            StateController stateController = StateController.getInstance();
            stateController.setAppCondition(StateController.AppCondition.SHOOTING_STILL_SAVING);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.DevelopmentState
    protected void onPictureReveiwStart() {
        PTag.end("autoreview start is called");
        if (CaptureStateUtil.isContinuousShooting()) {
            StateController stateController = StateController.getInstance();
            stateController.setAppCondition(StateController.AppCondition.SHOOTING_STILL_SAVING);
        }
        int reviewtype = ShootingExecutor.getReviewType();
        if (2 == reviewtype) {
            setNextState("AutoReviewForLimitedContShooting", null);
        } else {
            setNextState(AutoReviewState.STATE_NAME, null);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.DevelopmentState, com.sony.imaging.app.fw.State
    public void setNextState(String name, Bundle bundle) {
        this.mNextState = name;
        super.setNextState(name, bundle);
    }
}
