package com.sony.imaging.app.pictureeffectplus.shooting;

import android.os.Message;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.shooting.ExposureModeCheckState;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.fw.Definition;
import com.sony.imaging.app.pictureeffectplus.AppLog;
import com.sony.imaging.app.pictureeffectplus.shooting.camera.PictureEffectPlusExposureModeController;

/* loaded from: classes.dex */
public class PictureEffectPlusExposureModeCheckState extends ExposureModeCheckState {
    private static final String EE_STATE = "EE";
    private static final String TAG = AppLog.getClassName();
    private static boolean mIsSetExpMode = false;

    @Override // com.sony.imaging.app.base.shooting.ExposureModeCheckState
    protected String getNextState() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return EE_STATE;
    }

    @Override // com.sony.imaging.app.base.shooting.ExposureModeCheckState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(TAG, AppLog.getMethodName());
        PictureEffectPlusExposureModeController emc = PictureEffectPlusExposureModeController.getInstance();
        emc.initializeAgain();
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
                PictureEffectPlusExecutorCreator creator = (PictureEffectPlusExecutorCreator) PictureEffectPlusExecutorCreator.getInstance();
                if (!creator.isUpdated()) {
                    creator.updateSequence();
                }
            }
        } else {
            if (!emc.isAvailable(expMode)) {
                PictureEffectPlusExposureModeController.getInstance().setValue(ExposureModeController.EXPOSURE_MODE, expMode);
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
        PictureEffectPlusExecutorCreator creator = (PictureEffectPlusExecutorCreator) PictureEffectPlusExecutorCreator.getInstance();
        creator.setIsCautionDisplaying(false);
        PictureEffectPlusExposureModeController emc = PictureEffectPlusExposureModeController.getInstance();
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
}
