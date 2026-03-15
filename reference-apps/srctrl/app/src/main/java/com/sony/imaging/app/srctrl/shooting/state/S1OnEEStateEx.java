package com.sony.imaging.app.srctrl.shooting.state;

import android.util.Log;
import com.sony.imaging.app.base.shooting.S1OnEEState;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.srctrl.util.StateController;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.sysutil.KeyStatus;
import com.sony.scalar.sysutil.ScalarInput;

/* loaded from: classes.dex */
public class S1OnEEStateEx extends S1OnEEState {
    private static final String RETURN_STATE = "S1OffEE";
    public static final String STATE_NAME = "S1OnEE";
    private static final String TAG = S1OnEEStateEx.class.getSimpleName();
    private final String[] FOCUS_TAGS = {CameraNotificationManager.DONE_AUTO_FOCUS};

    protected void setAppCondition() {
        if (StateController.getInstance().getAppCondition().equals(StateController.AppCondition.SHOOTING_START_FOCUSING_REMOTE) || StateController.getInstance().getAppCondition().equals(StateController.AppCondition.SHOOTING_FOCUSING_REMOTE)) {
            StateController.getInstance().setAppCondition(StateController.AppCondition.SHOOTING_FOCUSING_REMOTE);
        } else {
            StateController.getInstance().setAppCondition(StateController.AppCondition.SHOOTING_FOCUSING);
        }
        StateController.getInstance().setState(this);
    }

    @Override // com.sony.imaging.app.base.shooting.S1OnEEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        setAppCondition();
    }

    @Override // com.sony.imaging.app.base.shooting.S1OnEEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
    }

    /* loaded from: classes.dex */
    protected class SRFocusInfoListener implements NotificationListener {
        protected SRFocusInfoListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return S1OnEEStateEx.this.FOCUS_TAGS;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            S1OnEEStateEx.this.refresh((CameraNotificationManager.OnFocusInfo) CameraNotificationManager.getInstance().getValue(tag));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refresh(CameraNotificationManager.OnFocusInfo info) {
        Log.v(TAG, "S1OnEEStateEx.refresh info.status : " + info.status);
        if (info.status == 0) {
            KeyStatus key = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.S1_ON);
            if (key.valid == 1 && key.status == 0 && !StateController.getInstance().getAppCondition().equals(StateController.AppCondition.SHOOTING_FOCUSING_REMOTE)) {
                setNextState("S1OffEE", null);
            }
        }
    }

    @Override // com.sony.imaging.app.base.shooting.S1OnEEState
    protected NotificationListener getFocusInfoListener() {
        if (CameraSetting.getInstance().isFocusHoldSupported()) {
            return new SRFocusInfoListener();
        }
        return null;
    }
}
