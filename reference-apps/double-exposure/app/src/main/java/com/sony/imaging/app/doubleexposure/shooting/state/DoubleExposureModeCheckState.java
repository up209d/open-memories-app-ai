package com.sony.imaging.app.doubleexposure.shooting.state;

import android.os.Message;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.shooting.ExposureModeCheckState;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.doubleexposure.common.AppLog;
import com.sony.imaging.app.doubleexposure.menu.controller.DoubleExposureModeController;
import com.sony.imaging.app.doubleexposure.menu.controller.ThemeSelectionController;
import com.sony.imaging.app.fw.Definition;

/* loaded from: classes.dex */
public class DoubleExposureModeCheckState extends ExposureModeCheckState {
    private static final String EE_STATE = "EE";
    private final String TAG = AppLog.getClassName();

    @Override // com.sony.imaging.app.base.shooting.ExposureModeCheckState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        DoubleExposureModeController doubleExposureModeController = DoubleExposureModeController.getInstance();
        doubleExposureModeController.initializeToSetCameraSettings();
        if (ModeDialDetector.hasModeDial()) {
            String selectedTheme = ThemeSelectionController.getInstance().getValue(ThemeSelectionController.THEMESELECTION);
            if ("Manual".equalsIgnoreCase(selectedTheme)) {
                if (!doubleExposureModeController.isValidDialPosition(ModeDialDetector.getModeDialPosition())) {
                    if (doubleExposureModeController.getCautionId() != 65535) {
                        ExecutorCreator.getInstance().stableSequence((BaseShootingExecutor.ReleasedListener) null);
                        CautionUtilityClass.getInstance().setDispatchKeyEvent(doubleExposureModeController.getCautionId(), getKeyHandler());
                        CautionUtilityClass.getInstance().requestTrigger(doubleExposureModeController.getCautionId());
                        ExecutorCreator.getInstance().stableSequence();
                    }
                } else {
                    setNextTransition();
                }
            } else {
                setNextTransition();
            }
        } else {
            setNextTransition();
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.ExposureModeCheckState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        ExposureModeController exposureModeController = ExposureModeController.getInstance();
        CautionUtilityClass.getInstance().setDispatchKeyEvent(exposureModeController.getCautionId(), null);
        Message msg = Message.obtain(getHandler(), Definition.STATE_TREE_CHANGED, 0, 0);
        getHandler().sendMessageAtFrontOfQueue(msg);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
        super.onDestroy();
    }

    @Override // com.sony.imaging.app.base.shooting.ExposureModeCheckState
    protected String getNextState() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return EE_STATE;
    }

    private void setNextTransition() {
        setNextState(getNextState(), null);
        Message msg = Message.obtain(getHandler(), Definition.STATE_TREE_CHANGED, 0, 0);
        getHandler().sendMessageAtFrontOfQueue(msg);
        ExecutorCreator.getInstance().updateSequence();
    }
}
