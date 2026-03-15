package com.sony.imaging.app.base.shooting;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.sony.imaging.app.base.menu.MenuTable;
import com.sony.imaging.app.base.shooting.MfAssistState;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.FocusMagnificationController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class FocusAdjustmentState extends MfAssistState {
    private static final String TAG = "FocusAdjustmentState";
    private static final String TRANSLATION_MODE = "NormalTransition";
    private String mCancelFocusMode;
    private int pushedKeyCode;

    @Override // com.sony.imaging.app.base.shooting.MfAssistState
    protected NotificationListener getCameraListener() {
        if (!FocusMagnificationController.isSupportedByPf()) {
            return null;
        }
        if (this.mListener == null) {
            this.mListener = new MfAssistState.MfAssistListener() { // from class: com.sony.imaging.app.base.shooting.FocusAdjustmentState.1
                private final String[] TAGS = {CameraNotificationManager.FOCUS_MAGNIFICATION_CHANGED, CameraNotificationManager.ZOOM_INFO_CHANGED, CameraNotificationManager.FOCUS_CHANGE, CameraNotificationManager.DEVICE_LENS_CHANGED};

                @Override // com.sony.imaging.app.base.shooting.MfAssistState.MfAssistListener, com.sony.imaging.app.util.NotificationListener
                public String[] getTags() {
                    return this.TAGS;
                }

                @Override // com.sony.imaging.app.base.shooting.MfAssistState.MfAssistListener, com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    FocusMagnificationController controller = FocusMagnificationController.getInstance();
                    if (CameraNotificationManager.FOCUS_MAGNIFICATION_CHANGED.equals(tag)) {
                        if (!controller.isMagnifying()) {
                            if (FocusAdjustmentState.this.pushedKeyCode == 514 && FocusAdjustmentState.this.mCancelFocusMode != null) {
                                FocusModeController.getInstance().setValue(FocusAdjustmentState.this.mCancelFocusMode);
                            }
                        } else {
                            controller.rescheduleTimeout();
                        }
                        super.onNotify(tag);
                        return;
                    }
                    if (CameraNotificationManager.ZOOM_INFO_CHANGED.equals(tag) || CameraNotificationManager.DEVICE_LENS_CHANGED.equals(tag)) {
                        if (!controller.isMagnifying()) {
                            FocusAdjustmentState.this.returnToNormalEE();
                        }
                    } else if (CameraNotificationManager.FOCUS_CHANGE.equals(tag) && !controller.isMagnifying()) {
                        String mode = FocusModeController.getInstance().getValue();
                        if (!FocusModeController.MANUAL.equals(mode) && !FocusModeController.SMF.equals(mode)) {
                            FocusAdjustmentState.this.returnToNormalEE();
                        }
                    }
                }
            };
        }
        return this.mListener;
    }

    @Override // com.sony.imaging.app.base.shooting.MfAssistState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        this.mCancelFocusMode = null;
        this.pushedKeyCode = 0;
        if (this.data != null && this.data.containsKey(FocusModeController.CANCEL_FOCUS_MODE)) {
            this.mCancelFocusMode = this.data.getString(FocusModeController.CANCEL_FOCUS_MODE);
        }
        FocusMagnificationController controller = FocusMagnificationController.getInstance();
        boolean isMfAssist = "off".equals(controller.getValue(FocusMagnificationController.TAG_IS_MFASSIST_AVAILABLE_IN_SETTINGS)) ? false : true;
        if (isMfAssist) {
            controller.startMfAssist();
        }
    }

    @Override // com.sony.imaging.app.base.shooting.MfAssistState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        this.mCancelFocusMode = null;
        this.pushedKeyCode = 0;
        super.onPause();
    }

    public void returnToNormalEE() {
        FocusMagnificationController controller = FocusMagnificationController.getInstance();
        if (!controller.isMagnifying()) {
            if (this.pushedKeyCode == 514 && this.mCancelFocusMode != null) {
                FocusModeController.getInstance().setValue(this.mCancelFocusMode);
            }
            removeState();
            Handler handler = getHandler();
            Message msg = Message.obtain(handler);
            Bundle bundle = new Bundle();
            bundle.putString(MenuTable.NEXT_STATE, TRANSLATION_MODE);
            msg.obj = bundle;
            handler.sendMessageAtFrontOfQueue(msg);
        }
    }

    public void setPushedMenuKeyFlg() {
        this.pushedKeyCode = AppRoot.USER_KEYCODE.MENU;
    }
}
