package com.sony.imaging.app.base.shooting;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.common.EVDialDetector;
import com.sony.imaging.app.base.common.EventParcel;
import com.sony.imaging.app.base.common.FocusModeDialDetector;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.MenuState;
import com.sony.imaging.app.base.menu.MenuTable;
import com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout;
import com.sony.imaging.app.base.shooting.camera.AELController;
import com.sony.imaging.app.base.shooting.camera.DigitalZoomController;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.FaceDetectionController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.util.PTag;

/* loaded from: classes.dex */
public class ShootingMenuState extends MenuState {
    private static final String FOCUS_MAGNIFIER = "MfAssist";
    private static final String MSG_CAN_NOT_OPEN_LAST_BASTION_LAYOUT = "Can not open the last bastion layout. MenuState is sending message for transition";
    private static final String MSG_IS_NOT_FOUND = " is not found.";
    private static final String PTAG_TRANSTION_TO_PLAYBACK = "transition from EE to playback";
    private static final String TAG = "ShootingMenuState";
    private static final String TRANSLATION_MODE = "NormalTransition";
    private boolean doneFinishMenuState = false;

    @Override // com.sony.imaging.app.base.menu.MenuState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        FaceDetectionController.getInstance().setFaceFrameRendering(false);
        ExecutorCreator.getInstance().stableSequence();
        this.doneFinishMenuState = false;
        DigitalZoomController.getInstance().stopSpinalZoom();
        super.onResume();
    }

    @Override // com.sony.imaging.app.base.menu.MenuState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        FaceDetectionController.getInstance().setFaceFrameRendering(true);
        ExecutorCreator.getInstance().updateSequence();
        DigitalZoomController.getInstance().startSpinalZoom();
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.menu.MenuState
    protected void finishMenuState(Bundle bundle) {
        if (!this.doneFinishMenuState) {
            this.doneFinishMenuState = true;
            Handler handler = getHandler();
            Message msg = Message.obtain(handler);
            String nextState = null;
            if (bundle != null) {
                nextState = bundle.getString(MenuTable.NEXT_STATE);
            } else {
                bundle = new Bundle();
            }
            if (nextState == null || nextState.isEmpty()) {
                bundle.putString(MenuTable.NEXT_STATE, TRANSLATION_MODE);
            }
            msg.obj = bundle;
            handler.sendMessageAtFrontOfQueue(msg);
            BaseMenuService.flushAllRunnable();
            removeState();
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS1Key() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        PTag.start(PTAG_TRANSTION_TO_PLAYBACK);
        Bundle b = new Bundle();
        Bundle data = new Bundle();
        data.putParcelable(EventParcel.KEY_KEYCODE, new EventParcel(AppRoot.USER_KEYCODE.PLAYBACK));
        b.putParcelable(S1OffEEState.STATE_NAME, data);
        Activity appRoot = getActivity();
        ((BaseApp) appRoot).changeApp(BaseApp.APP_PLAY, b);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedModeDial() {
        closeMenuLayouts();
        openMenuLayout(ExposureModeController.EXPOSURE_MODE, ExposureModeMenuLayout.MENU_ID);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayIndexKey() {
        PTag.start(PTAG_TRANSTION_TO_PLAYBACK);
        Bundle b = new Bundle();
        Bundle data = new Bundle();
        data.putParcelable(EventParcel.KEY_KEYCODE, new EventParcel(CustomizableFunction.PlayIndex));
        b.putParcelable(S1OffEEState.STATE_NAME, data);
        Activity appRoot = getActivity();
        ((BaseApp) appRoot).changeApp(BaseApp.APP_PLAY, b);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELToggleCustomKey() {
        AELController cntl = AELController.getInstance();
        cntl.repressAELock();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELHoldCustomKey() {
        AELController cntl = AELController.getInstance();
        cntl.holdAELock(true);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAELHoldCustomKey() {
        AELController cntl = AELController.getInstance();
        cntl.holdAELock(false);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAfMfHoldCustomKey() {
        FocusModeController.getInstance().holdFocusControl(true);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAfMfHoldCustomKey() {
        FocusModeController.getInstance().holdFocusControl(false);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAfMfToggleCustomKey() {
        FocusModeController.getInstance().toggleFocusControl();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMfAssistCustomKey() {
        if (ExecutorCreator.getInstance().getRecordingMode() == 1) {
            Bundle bundle = new Bundle();
            bundle.putString(MenuTable.NEXT_STATE, "MfAssist");
            bundle.putBoolean(MenuState.BOOLEAN_FINISH_MENU_STATE, true);
            onClosed(bundle);
        }
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedEVDial() {
        ExposureCompensationController.getInstance().setEvDialValue(EVDialDetector.getEVDialPosition());
        onClosed(null);
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.MenuState
    protected boolean canRemoveState() {
        if (!ModeDialDetector.hasModeDial() || ExposureModeController.getInstance().getCautionId() == 65535) {
            return true;
        }
        boolean ret = ExposureModeController.getInstance().isValidDialPosition();
        return ret;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedFocusModeDial() {
        if (FocusModeDialDetector.getFocusModeDialPosition() != -1) {
            FocusModeController focusModeController = FocusModeController.getInstance();
            focusModeController.setValue(focusModeController.getFocusModeFromFocusModeDial());
        }
        onClosed(null);
        return 1;
    }
}
