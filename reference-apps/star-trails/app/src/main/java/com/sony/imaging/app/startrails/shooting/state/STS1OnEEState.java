package com.sony.imaging.app.startrails.shooting.state;

import com.sony.imaging.app.base.shooting.S1OnEEState;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.startrails.util.STUtility;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class STS1OnEEState extends S1OnEEState implements NotificationListener {
    public static String TAG = "STS1OnEEState";
    private static String[] TAGS = {CameraNotificationManager.DEVICE_LENS_AFMF_SW_CHANGED};

    @Override // com.sony.imaging.app.base.shooting.S1OnEEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        CameraNotificationManager.getInstance().setNotificationListener(this);
    }

    @Override // com.sony.imaging.app.base.shooting.S1OnEEState, com.sony.imaging.app.base.shooting.IUserChanging
    public void changeShutterSpeed() {
        if (STUtility.getInstance().getCurrentTrail() != 0) {
            super.changeShutterSpeed();
        }
    }

    @Override // com.sony.imaging.app.base.shooting.S1OnEEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        super.onPause();
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        if (tag.equals(CameraNotificationManager.DEVICE_LENS_AFMF_SW_CHANGED)) {
            setNextState(STMFModeCheckState.TAG, null);
        }
    }
}
