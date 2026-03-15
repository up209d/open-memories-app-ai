package com.sony.imaging.app.base.shooting;

import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.HoldKeyServer;

/* loaded from: classes.dex */
public class CaptureState extends StateBase {
    private static final String NEXT_INTERVAL_REC_CAPTURE_STATE = "IntervalRecCapture";
    private static final String NEXT_NORMAL_CAPTURE_STATE = "NormalCapture";
    private static final String NEXT_SELF_TIMER_STATE = "SelfTimerCapture";

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        String state;
        super.onResume();
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED, 2);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.SLIDE_AF_MF, 1);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.SLIDE_AEL_FOCUS_CHANGED, 1);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.IRIS_DIAL_CHANGED, 1);
        DriveModeController cntl = DriveModeController.getInstance();
        if (cntl.isSelfTimer()) {
            state = NEXT_SELF_TIMER_STATE;
        } else {
            state = NEXT_NORMAL_CAPTURE_STATE;
        }
        if (ExecutorCreator.isIntervalRecEnable() && 4 == ExecutorCreator.getInstance().getRecordingMode()) {
            state = NEXT_INTERVAL_REC_CAPTURE_STATE;
        }
        addChildState(state, this.data);
        MediaNotificationManager.getInstance().hold(true);
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase
    protected int getMyBeepPattern() {
        return 6;
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        MediaNotificationManager.getInstance().hold(false);
        super.onPause();
    }
}
