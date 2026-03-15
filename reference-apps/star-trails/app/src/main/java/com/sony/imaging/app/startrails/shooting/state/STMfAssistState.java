package com.sony.imaging.app.startrails.shooting.state;

import android.os.Bundle;
import android.os.Message;
import com.sony.imaging.app.base.menu.MenuState;
import com.sony.imaging.app.base.shooting.MfAssistState;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.FocusMagnificationController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.HoldKeyServer;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class STMfAssistState extends MfAssistState implements NotificationListener {
    private static String[] TAGS = {CameraNotificationManager.DEVICE_LENS_AFMF_SW_CHANGED};
    boolean isNextStateMFAssist = false;

    @Override // com.sony.imaging.app.base.shooting.MfAssistState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        FocusMagnificationController.getInstance().setTimeoutDuration(0);
        this.isNextStateMFAssist = false;
        super.onResume();
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED, 3);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_CHANGED, 3);
        CameraNotificationManager.getInstance().setNotificationListener(this);
    }

    @Override // com.sony.imaging.app.base.shooting.MfAssistState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver
    public boolean handleMessage(Message msg) {
        Bundle bundle = msg.getData();
        bundle.putString(MenuState.ITEM_ID, ExposureModeController.EXPOSURE_MODE);
        msg.setData(bundle);
        return super.handleMessage(msg);
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
