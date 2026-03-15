package com.sony.imaging.app.smoothreflection.shooting.state;

import android.os.Message;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.ExposureModeCheckState;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.fw.Definition;
import com.sony.imaging.app.smoothreflection.common.AppLog;
import com.sony.imaging.app.smoothreflection.common.SaUtil;
import com.sony.imaging.app.smoothreflection.common.SmoothReflectionUtil;
import com.sony.imaging.app.smoothreflection.shooting.camera.SmoothReflectionExposureModeController;
import com.sony.imaging.app.smoothreflection.shooting.camera.ThemeController;

/* loaded from: classes.dex */
public class SmoothReflectionModeCheckState extends ExposureModeCheckState {
    private static final String EE_STATE = "EE";
    private final String TAG = AppLog.getClassName();

    @Override // com.sony.imaging.app.base.shooting.ExposureModeCheckState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        SmoothReflectionExposureModeController smoothReflectionExposureModeController = SmoothReflectionExposureModeController.getInstance();
        smoothReflectionExposureModeController.initializeToSetCameraSettings();
        if (ModeDialDetector.hasModeDial()) {
            String selectedTheme = null;
            try {
                selectedTheme = ThemeController.getInstance().getValue(ThemeController.THEMESELECTION);
            } catch (IController.NotSupportedException e) {
                AppLog.error(this.TAG, e.toString());
            }
            if (ThemeController.CUSTOM.equalsIgnoreCase(selectedTheme)) {
                if (!smoothReflectionExposureModeController.isValidDialPosition(ModeDialDetector.getModeDialPosition())) {
                    if (smoothReflectionExposureModeController.getCautionId() != 65535) {
                        ExecutorCreator.getInstance().stableSequence((BaseShootingExecutor.ReleasedListener) null);
                        CautionUtilityClass.getInstance().setDispatchKeyEvent(smoothReflectionExposureModeController.getCautionId(), getKeyHandler());
                        CautionUtilityClass.getInstance().requestTrigger(smoothReflectionExposureModeController.getCautionId());
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
        String selectedTheme_avip = null;
        try {
            selectedTheme_avip = ThemeController.getInstance().getValue(ThemeController.THEMESELECTION);
        } catch (IController.NotSupportedException e2) {
            AppLog.error(this.TAG, e2.toString());
        }
        if (ThemeController.MONOTONE.equalsIgnoreCase(selectedTheme_avip) && SaUtil.isAVIP()) {
            SmoothReflectionUtil.getInstance().setRecommandedCameraSettings(selectedTheme_avip);
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
        return "EE";
    }

    private void setNextTransition() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        setNextState(getNextState(), null);
        Message msg = Message.obtain(getHandler(), Definition.STATE_TREE_CHANGED, 0, 0);
        getHandler().sendMessageAtFrontOfQueue(msg);
        ExecutorCreator.getInstance().updateSequence();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }
}
