package com.sony.imaging.app.base.shooting.trigger;

import android.view.KeyEvent;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.BaseBackUpKey;
import com.sony.imaging.app.base.caution.CautionID;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.DigitalZoomController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.BaseInvalidKeyHandler;
import com.sony.imaging.app.fw.HoldKeyServer;
import com.sony.imaging.app.fw.ICustomKey;
import com.sony.imaging.app.fw.State;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.sysutil.KeyStatus;
import com.sony.scalar.sysutil.ScalarInput;
import com.sony.scalar.sysutil.ScalarProperties;

/* loaded from: classes.dex */
public class ShootingKeyHandlerBase extends BaseInvalidKeyHandler {
    private static final String INH_FACTOR_STILL_WRITING = "INH_FACTOR_STILL_WRITING";
    private static final String MOVIE_CAPTURE_STATE = "MovieCapture";
    public static final String PROP_MODEL_CATEGORY = "model.category";
    protected static KeyEvent firstEvent = null;
    protected static KeyEvent secondEvent = null;

    @Override // com.sony.imaging.app.fw.BaseKeyHandler
    protected boolean canRepeat(int KeyCode) {
        return false;
    }

    @Override // com.sony.imaging.app.fw.BaseKeyHandler
    protected String getKeyConvCategory() {
        ExposureModeController cntl = ExposureModeController.getInstance();
        String expMode = cntl.getValue(null);
        if (ExposureModeController.PROGRAM_AUTO_MODE.equals(expMode) || ExposureModeController.MOVIE_PROGRAM_AUTO_MODE.equals(expMode)) {
            return ICustomKey.CATEGORY_SHOOTING_P;
        }
        if ("Aperture".equals(expMode) || ExposureModeController.MOVIE_APERATURE_MODE.equals(expMode)) {
            return ICustomKey.CATEGORY_SHOOTING_A;
        }
        if ("Shutter".equals(expMode) || ExposureModeController.MOVIE_SHUTTER_MODE.equals(expMode)) {
            return ICustomKey.CATEGORY_SHOOTING_S;
        }
        if ("Manual".equals(expMode) || ExposureModeController.MOVIE_MANUAL_MODE.equals(expMode)) {
            return ICustomKey.CATEGORY_SHOOTING_M;
        }
        return ICustomKey.CATEGORY_SHOOTING_OTHER;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS2Key() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedS1Key() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedMovieRecKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUmRemoteRecKey() {
        return pushedMovieRecKey();
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedUmRemoteRecKey() {
        return releasedMovieRecKey();
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIRRecKey() {
        return pushedMovieRecKey();
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieRecKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedShootingModeKey() {
        if (Environment.isMovieAPISupported() && ((BaseApp) this.target.getActivity()).isMovieModeSupported()) {
            AvailableInfo.update();
            if (!AvailableInfo.isFactor(INH_FACTOR_STILL_WRITING)) {
                HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED, 2);
                HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.SLIDE_AF_MF, 1);
                HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.SLIDE_AEL_FOCUS_CHANGED, 1);
                HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.IRIS_DIAL_CHANGED, 1);
                String expMode = ExposureModeController.getInstance().getValue(null);
                if (ExposureModeController.PROGRAM_AUTO_MODE.equals(expMode)) {
                    CameraSetting.getInstance().resetProgramLine();
                }
                ExecutorCreator ec = ExecutorCreator.getInstance();
                ec.setRecordingMode(2, null);
                State state = (State) this.target;
                state.setNextState("EE", null);
                return 1;
            }
            CautionUtilityClass.getInstance().requestTrigger(1410);
            return -1;
        }
        CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_CANNOT_REC_MOVIE);
        return -1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedModePKey() {
        ExposureModeController.getInstance().setValue(ExposureModeController.EXPOSURE_MODE, ExposureModeController.PROGRAM_AUTO_MODE);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedModeAKey() {
        ExposureModeController.getInstance().setValue(ExposureModeController.EXPOSURE_MODE, "Aperture");
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedModeSKey() {
        ExposureModeController.getInstance().setValue(ExposureModeController.EXPOSURE_MODE, "Shutter");
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedModeMKey() {
        ExposureModeController.getInstance().setValue(ExposureModeController.EXPOSURE_MODE, "Manual");
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachKeyHandler
    public int onAelFocusSlideKeyMoved(KeyEvent event) {
        KeyStatus key;
        KeyStatus key2;
        int first;
        int firstAction;
        int second;
        int secondAction;
        if (firstEvent == null && secondEvent == null && (key = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.AF_MF_AEL)) != null && 1 == key.valid && 1 == key.status && (key2 = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.SLIDE_AEL_FOCUS_CHANGED)) != null && 1 == key2.valid) {
            switch (key2.status) {
                case AppRoot.USER_KEYCODE.AEL /* 532 */:
                    first = AppRoot.USER_KEYCODE.AF_MF;
                    firstAction = 1;
                    second = AppRoot.USER_KEYCODE.AEL;
                    secondAction = 0;
                    break;
                case AppRoot.USER_KEYCODE.AF_MF /* 533 */:
                    first = AppRoot.USER_KEYCODE.AEL;
                    firstAction = 1;
                    second = AppRoot.USER_KEYCODE.AF_MF;
                    secondAction = 0;
                    break;
                default:
                    first = AppRoot.USER_KEYCODE.AEL;
                    firstAction = 1;
                    second = AppRoot.USER_KEYCODE.AF_MF;
                    secondAction = 1;
                    break;
            }
            firstEvent = new KeyEvent(event.getDownTime(), event.getEventTime(), firstAction, 0, 0, event.getMetaState(), event.getDeviceId(), first, event.getFlags());
            secondEvent = new KeyEvent(event.getDownTime(), event.getEventTime(), secondAction, 0, 0, event.getMetaState(), event.getDeviceId(), second, event.getFlags());
        }
        if (firstEvent != null) {
            int handle = this.mKeyConverter.apply(firstEvent, getKeyConvCategory());
            if (handle != 0) {
                firstEvent = null;
            }
        }
        if (secondEvent != null) {
            int handle2 = this.mKeyConverter.apply(secondEvent, getKeyConvCategory());
            if (handle2 != 0) {
                secondEvent = null;
            }
        }
        if (firstEvent == null && secondEvent == null) {
            return -1;
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int showZoomLeverInvalidCaution() {
        int category = ScalarProperties.getInt("model.category");
        if (2 != category) {
            if (1 <= CameraSetting.getPfApiVersion()) {
                int powerZoomStatus = CameraSetting.getInstance().getPowerZoomStatus();
                if (!DigitalZoomController.getInstance().isZoomAvailable() && powerZoomStatus != 1) {
                    CautionUtilityClass.getInstance().requestTrigger(CautionID.CAUTION_ID_INVALID_THIS_FUNCTION);
                }
            }
            if (2 <= CameraSetting.getPfApiVersion() && !ExecutorCreator.getInstance().isSpinalZoom()) {
                CautionUtilityClass.getInstance().requestTrigger(CautionID.CAUTION_ID_INVALID_THIS_FUNCTION);
            }
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void toggleDialTvAv() {
        boolean isAv = BackUpUtil.getInstance().getPreferenceBoolean(BaseBackUpKey.ID_DIAL_AVTV_TOGGLE, false);
        BackUpUtil.getInstance().setPreference(BaseBackUpKey.ID_DIAL_AVTV_TOGGLE, Boolean.valueOf(isAv ? false : true));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isDialTvAvToggled() {
        return BackUpUtil.getInstance().getPreferenceBoolean(BaseBackUpKey.ID_DIAL_AVTV_TOGGLE, false);
    }
}
