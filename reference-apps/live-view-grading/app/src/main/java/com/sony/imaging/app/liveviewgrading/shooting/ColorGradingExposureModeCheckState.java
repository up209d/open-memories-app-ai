package com.sony.imaging.app.liveviewgrading.shooting;

import android.os.Bundle;
import android.os.Message;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.IkeyDispatchEach;
import com.sony.imaging.app.base.menu.MenuState;
import com.sony.imaging.app.base.shooting.ExposureModeCheckState;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.fw.Definition;
import com.sony.imaging.app.liveviewgrading.AppLog;
import com.sony.imaging.app.liveviewgrading.menu.controller.ColorGradingExposureModeController;
import com.sony.imaging.app.util.BeepUtility;
import com.sony.imaging.app.util.BeepUtilityRsrcTable;

/* loaded from: classes.dex */
public class ColorGradingExposureModeCheckState extends ExposureModeCheckState {
    private static final String EE_STATE = "EE";
    private static final String TAG = AppLog.getClassName();
    private static boolean mIsSetExpMode = false;

    @Override // com.sony.imaging.app.base.shooting.ExposureModeCheckState
    protected String getNextState() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return "EE";
    }

    @Override // com.sony.imaging.app.base.shooting.ExposureModeCheckState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(TAG, AppLog.getMethodName());
        ColorGradingExposureModeController emc = ColorGradingExposureModeController.getInstance();
        String expMode = emc.getValue(null);
        if (ModeDialDetector.hasModeDial()) {
            AppLog.info(TAG, "Has Mode Dial.");
            if (!emc.isValidDialPosition(ModeDialDetector.getModeDialPosition())) {
                if (emc.getCautionId() != 65535) {
                    ExecutorCreator.getInstance().stableSequence((BaseShootingExecutor.ReleasedListener) null);
                    CautionUtilityClass.getInstance().setDispatchKeyEvent(emc.getCautionId(), getKeyHandler());
                    CautionUtilityClass.getInstance().requestTrigger(emc.getCautionId());
                    ExecutorCreator.getInstance().stableSequence();
                }
            } else {
                setNextTransition();
                ColorGradingExecutorCreator creator = (ColorGradingExecutorCreator) ColorGradingExecutorCreator.getInstance();
                if (!creator.isUpdated()) {
                    creator.updateSequence();
                }
            }
        } else {
            if (!emc.isAvailable(expMode)) {
                ColorGradingExposureModeController.getInstance().setValue(ExposureModeController.EXPOSURE_MODE, expMode);
            }
            setNextTransition();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void setNextTransition() {
        AppLog.enter(TAG, AppLog.getMethodName());
        setNextState(getNextState(), this.data);
        Message msg = Message.obtain(getHandler(), Definition.STATE_TREE_CHANGED, 0, 0);
        getHandler().sendMessageAtFrontOfQueue(msg);
        ExecutorCreator.getInstance().updateSequence();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.ExposureModeCheckState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(TAG, AppLog.getMethodName());
        ColorGradingExposureModeController emc = ColorGradingExposureModeController.getInstance();
        CautionUtilityClass.getInstance().setDispatchKeyEvent(emc.getCautionId(), null);
        Message msg = Message.obtain(getHandler(), Definition.STATE_TREE_CHANGED, 0, 0);
        getHandler().sendMessageAtFrontOfQueue(msg);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public static void setIsSetExpMode(boolean isSetExpMode) {
        AppLog.enter(TAG, AppLog.getMethodName());
        mIsSetExpMode = isSetExpMode;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public static boolean getIsSetExpMode() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return mIsSetExpMode;
    }

    @Override // com.sony.imaging.app.base.shooting.ExposureModeCheckState
    protected IkeyDispatchEach getKeyHandler() {
        IkeyDispatchEach changeModeKeyHandler = new IkeyDispatchEach() { // from class: com.sony.imaging.app.liveviewgrading.shooting.ColorGradingExposureModeCheckState.1
            @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
            public int pushedPlayBackKey() {
                return 1;
            }

            @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
            public int pushedMovieRecKey() {
                return 0;
            }

            @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
            public int pushedUmRemoteRecKey() {
                return 0;
            }

            @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
            public int pushedIRRecKey() {
                return 0;
            }

            @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
            public int pushedShootingModeKey() {
                return -1;
            }

            @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
            public int pushedFnKey() {
                return 0;
            }

            @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
            public int turnedModeDial() {
                if (ModeDialDetector.getModeDialPosition() != -1) {
                    CautionUtilityClass.getInstance().executeTerminate();
                    ColorGradingExposureModeCheckState.this.transitState(ExposureModeController.EXPOSURE_MODE);
                    return 0;
                }
                return 0;
            }

            @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
            public int pushedMenuKey() {
                ColorGradingExposureModeCheckState.this.getActivity().finish();
                BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_SELECT);
                return 1;
            }

            @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
            public int turnedEVDial() {
                return 0;
            }

            @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
            public int turnedFocusModeDial() {
                return 0;
            }
        };
        return changeModeKeyHandler;
    }

    public void transitState(String itemId) {
        Bundle b = this.data;
        if (b == null) {
            b = new Bundle();
        }
        b.putString(MenuState.ITEM_ID, itemId);
        setNextState(getNextState(), b);
        ExecutorCreator.getInstance().updateSequence();
    }
}
