package com.sony.imaging.app.startrails.shooting.state;

import com.sony.imaging.app.base.shooting.NormalCaptureState;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.HoldKeyServer;
import com.sony.imaging.app.startrails.common.STCaptureDisplayModeObserver;
import com.sony.imaging.app.startrails.common.STDisplayModeObserver;
import com.sony.imaging.app.startrails.shooting.STExecutorCreator;
import com.sony.imaging.app.startrails.shooting.layout.AutoReviewBlackLayout;
import com.sony.imaging.app.startrails.shooting.layout.SelfTimerDisplayLayout;
import com.sony.imaging.app.startrails.util.STUtility;

/* loaded from: classes.dex */
public class STNormalCaptureState extends NormalCaptureState {
    @Override // com.sony.imaging.app.base.shooting.NormalCaptureState, com.sony.imaging.app.base.shooting.CaptureStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        STUtility.getInstance().setVirtualMediaIds();
        if (!STUtility.getInstance().isPreTakePictureTestShot()) {
            STExecutorCreator creator = (STExecutorCreator) STExecutorCreator.getInstance();
            creator.createSTBOData();
        }
        super.onResume();
        STUtility.getInstance().setCaptureStatus(true);
        STUtility.getInstance().setDetachedLensStatus(false);
        setCapturingStarTrailDisplayMode();
        openLayout(SelfTimerDisplayLayout.TAG);
        openLayout(AutoReviewBlackLayout.TAG);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED, 3);
    }

    private void setStarTrailDisplayMode() {
        if (11 != STDisplayModeObserver.getInstance().getActiveDispMode(0)) {
            STDisplayModeObserver.getInstance().setStarTrailDisplayMode();
        }
    }

    private void setCapturingStarTrailDisplayMode() {
        STCaptureDisplayModeObserver.getInstance().setStarTrailDisplayMode();
    }

    @Override // com.sony.imaging.app.base.shooting.CaptureStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        closeLayout(SelfTimerDisplayLayout.TAG);
        closeLayout(AutoReviewBlackLayout.TAG);
        super.onPause();
        STUtility.getInstance().setCaptureStatus(false);
        STUtility.getInstance().setDetachedLensStatus(false);
        setStarTrailDisplayMode();
        if (!STUtility.getInstance().isPreTakePictureTestShot()) {
            STUtility.getInstance().setActualMediaIds();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.CaptureStateBase
    public String getNextState() {
        String nextState = super.getNextState();
        if (nextState.equals("EE")) {
            return MFModeCheckState.TAG;
        }
        return nextState;
    }
}
