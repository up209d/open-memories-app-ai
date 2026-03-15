package com.sony.imaging.app.base.shooting;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.sony.imaging.app.base.menu.MenuTable;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;
import com.sony.imaging.app.base.shooting.camera.FocusMagnificationController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.HoldKeyServer;
import com.sony.imaging.app.util.ApoWrapper;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class MfAssistState extends StateBase {
    private static final String[] TAGS = {CameraNotificationManager.FOCUS_MAGNIFICATION_CHANGED, CameraNotificationManager.FOCUS_RING_ROTATION_STARTED, CameraNotificationManager.FOCUS_RING_ROTATION_STOPPED};
    private static final String TRANSLATION_MODE = "NormalTransition";
    protected NotificationListener mListener;

    protected String getTranslationMode() {
        return TRANSLATION_MODE;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class MfAssistListener implements NotificationListener {
        /* JADX INFO: Access modifiers changed from: protected */
        public MfAssistListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return MfAssistState.TAGS;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            FocusMagnificationController focusMagnificationController = FocusMagnificationController.getInstance();
            if (CameraNotificationManager.FOCUS_MAGNIFICATION_CHANGED.equals(tag)) {
                if (!focusMagnificationController.isMagnifying()) {
                    MfAssistState.this.removeState();
                    Handler handler = MfAssistState.this.getHandler();
                    Message msg = Message.obtain(handler);
                    Bundle bundle = new Bundle();
                    bundle.putString(MenuTable.NEXT_STATE, MfAssistState.this.getTranslationMode());
                    msg.obj = bundle;
                    handler.sendMessageAtFrontOfQueue(msg);
                    return;
                }
                return;
            }
            if (CameraNotificationManager.FOCUS_RING_ROTATION_STARTED.equals(tag)) {
                focusMagnificationController.cancelTimeout();
            } else if (CameraNotificationManager.FOCUS_RING_ROTATION_STOPPED.equals(tag)) {
                focusMagnificationController.scheduleTimeout();
            }
        }
    }

    protected NotificationListener getCameraListener() {
        if (FocusMagnificationController.isSupportedByPf()) {
            return new MfAssistListener();
        }
        return null;
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED, 2);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_CHANGED, 1);
        this.mListener = getCameraListener();
        if (this.mListener != null) {
            CameraNotificationManager.getInstance().setNotificationListener(this.mListener, true);
        }
        FocusMagnificationController controller = FocusMagnificationController.getInstance();
        if (!controller.isFocusRingRotating()) {
            controller.scheduleTimeout();
        }
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        if (this.mListener != null) {
            CameraNotificationManager.getInstance().removeNotificationListener(this.mListener);
            this.mListener = null;
        }
        FocusMagnificationController focusMagnificationController = FocusMagnificationController.getInstance();
        if (focusMagnificationController.isMagnifying() || focusMagnificationController.isStarting()) {
            focusMagnificationController.stop();
        }
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_CHANGED, 0);
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase
    protected int getMyBeepPattern() {
        return 7;
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State
    public ApoWrapper.APO_TYPE getApoType() {
        ApoWrapper.APO_TYPE type = ApoWrapper.APO_TYPE.SPECIAL;
        if (DriveModeController.getInstance().isRemoteControl()) {
            ApoWrapper.APO_TYPE type2 = ApoWrapper.APO_TYPE.NONE;
            return type2;
        }
        return type;
    }
}
