package com.sony.imaging.app.manuallenscompensation.shooting.state;

import android.os.Message;
import android.view.KeyEvent;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.shooting.ShootingMenuState;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.base.shooting.widget.AppNameConstantView;
import com.sony.imaging.app.base.shooting.widget.AppNameView;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.manuallenscompensation.OpticalCompensation;
import com.sony.imaging.app.manuallenscompensation.R;
import com.sony.imaging.app.manuallenscompensation.commonUtil.AppLog;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCBackUpKey;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCUtil;
import com.sony.imaging.app.manuallenscompensation.database.LensCompensationParameter;
import com.sony.imaging.app.manuallenscompensation.menu.controller.OCExposureModeController;
import com.sony.imaging.app.manuallenscompensation.widget.OCAppNameFocalValue;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.Environment;

/* loaded from: classes.dex */
public class OCShootingMenuState extends ShootingMenuState {
    private static final String ID_LASTBASTIONLAYOUT = "LastBastionLayout";
    private static final String JIRA_167 = "JIRA_167 ";
    private static final String MOVIE_REC_NOT_POSSIBLE = "ValidDialPosition not available so INVALID MOVIE KEY IN Supported Mode screen";
    private static final String TAG = "OCShootingMenuState";

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.base.menu.MenuState
    protected boolean canRemoveState() {
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean canRemoveState = true;
        if (ModeDialDetector.hasModeDial()) {
            AppLog.trace(TAG, "JIRA_167 ModeDialDetector.hasModeDial() Value = " + ModeDialDetector.hasModeDial());
            if (!OCExposureModeController.getInstance().isValidDialPosition(ModeDialDetector.getModeDialPosition())) {
                AppLog.trace(TAG, "JIRA_167 isValidDialPosition Value = false");
                canRemoveState = false;
            } else {
                AppLog.trace(TAG, "JIRA_167 isValidDialPosition Value = true");
            }
        }
        AppLog.trace(TAG, "JIRA_167 canRemoveState Value = " + canRemoveState);
        AppLog.exit(TAG, AppLog.getMethodName());
        return canRemoveState;
    }

    private void createDefaultProfileIfNotExist() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (!OCUtil.getInstance().isExitExecute()) {
            boolean isNoProfile = OCUtil.getInstance().checkforAppliedProfile();
            if (isNoProfile) {
                OCUtil.getInstance().creatDefaultProfile(null);
                AppNameView.setText("");
            } else {
                updateAppNameView();
            }
        } else {
            updateAppNameView();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieRecKey() {
        int returnState;
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean keyStatus = getMovieRecScenario();
        if (true == keyStatus) {
            returnState = super.pushedMovieRecKey();
        } else {
            returnState = 1;
        }
        AppLog.trace(TAG, AppLog.getMethodName() + returnState);
        AppLog.exit(TAG, AppLog.getMethodName());
        return returnState;
    }

    private boolean getMovieRecScenario() {
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean returnState = false;
        if (ModeDialDetector.hasModeDial()) {
            if (OCExposureModeController.getInstance().isValidDialPosition(ModeDialDetector.getModeDialPosition())) {
                AppLog.enter(TAG, MOVIE_REC_NOT_POSSIBLE);
                returnState = true;
            } else {
                displayMovieRecCaution();
            }
        } else {
            returnState = true;
        }
        AppLog.exit(TAG, AppLog.getMethodName() + returnState);
        return returnState;
    }

    private void displayMovieRecCaution() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (!Environment.isMovieAPISupported() || !ExecutorCreator.getInstance().isMovieModeSupported()) {
            CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_CANNOT_REC_MOVIE);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieRec2ndKey() {
        int returnState;
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean keyStatus = getMovieRecScenario();
        if (true == keyStatus) {
            returnState = super.pushedMovieRec2ndKey();
        } else {
            returnState = 1;
        }
        AppLog.trace(TAG, AppLog.getMethodName() + returnState);
        AppLog.exit(TAG, AppLog.getMethodName());
        return returnState;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIR2SecKey() {
        int returnState;
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean keyStatus = getMovieRecScenario();
        if (true == keyStatus) {
            returnState = super.pushedIR2SecKey();
        } else {
            returnState = 1;
        }
        AppLog.trace(TAG, AppLog.getMethodName() + returnState);
        AppLog.exit(TAG, AppLog.getMethodName());
        return returnState;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIRRecKey() {
        int returnState;
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean keyStatus = getMovieRecScenario();
        if (true == keyStatus) {
            returnState = super.pushedIRRecKey();
        } else {
            returnState = 1;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return returnState;
    }

    @Override // com.sony.imaging.app.base.menu.MenuState
    public void closeMenuLayouts() {
        OCUtil.getInstance().resetExifDataOff();
        super.closeMenuLayouts();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUmRemoteRecKey() {
        int returnState;
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean keyStatus = getMovieRecScenario();
        if (true == keyStatus) {
            returnState = super.pushedUmRemoteRecKey();
        } else {
            returnState = 1;
        }
        AppLog.trace(TAG, AppLog.getMethodName() + returnState);
        AppLog.exit(TAG, AppLog.getMethodName());
        return returnState;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.trace(TAG, "JIRA_167 onKeyDown Value = keyCode Value = " + keyCode);
        AppLog.trace(TAG, "JIRA_167 onKeyDown Value = keyevent Scancode = " + event.getScanCode());
        AppLog.exit(TAG, AppLog.getMethodName());
        return super.onKeyDown(keyCode, event);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.trace(TAG, "JIRA_167 onKeyDown Value = KeyEvent Value = " + event);
        AppLog.trace(TAG, "JIRA_167 onKeyDown Value = IKeyFunction func = " + func);
        AppLog.exit(TAG, AppLog.getMethodName());
        return super.onConvertedKeyDown(event, func);
    }

    private void setExposureMode() {
        if (ModeDialDetector.hasModeDial()) {
            OCExposureModeController.getInstance().setValue(ExposureModeController.EXPOSURE_MODE, ExposureModeController.scancode2Value(ModeDialDetector.getModeDialPosition()));
        }
    }

    @Override // com.sony.imaging.app.base.menu.MenuState
    protected String getLastBastionLayoutName() {
        return ID_LASTBASTIONLAYOUT;
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.base.menu.MenuState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean ovfMode = OCUtil.getInstance().isDiademOVfMode();
        resetExternalListPosition();
        OCUtil.getInstance().setCameraPreviewMode("off", ovfMode);
        AppNameConstantView.setText((String) getResources().getText(R.string.STRID_FUNC_OPTICAL_COMPENSATION));
        OCAppNameFocalValue.setText(null);
        OCAppNameFocalValue.show(true);
        super.onResume();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void resetExternalListPosition() {
        AppLog.enter(TAG, AppLog.getMethodName());
        BackUpUtil.getInstance().setPreference(OCBackUpKey.KEY_BKUP_SELECTED_POSITION_DELETE_EXPORTED_LIST, -1);
        BackUpUtil.getInstance().setPreference(OCBackUpKey.KEY_BKUP_SELECTED_POSITION_IMPORT_EXPORTED_LIST, -1);
        BackUpUtil.getInstance().setPreference(OCBackUpKey.KEY_BKUP_COUNT_IMPORT_EXPORTED_LIST, -1);
        BackUpUtil.getInstance().setPreference(OCBackUpKey.KEY_BKUP_COUNT_DELETE_EXPORTED_LIST, -1);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.base.menu.MenuState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean ovfMode = OCUtil.getInstance().isDiademOVfMode();
        OCUtil.getInstance().setCameraPreviewMode("iris_ss_iso_aeunlock", ovfMode);
        resetExternalListPosition();
        setExposureMode();
        super.onPause();
        createDefaultProfileIfNotExist();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void updateAppNameView() {
        LensCompensationParameter mParam = OCUtil.getInstance().getLensParameterObject();
        if (mParam != null) {
            AppNameView.setText(mParam.mLensName);
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver
    public boolean handleMessage(Message msg) {
        if (518 == msg.what) {
            AppLog.enter(TAG, "ignore S2 On");
            return true;
        }
        boolean ret = super.handleMessage(msg);
        return ret;
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        int returnState;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (OCUtil.getInstance().getLastCaptureRecMode() == 2) {
            OCUtil.getInstance().openMoviePlaybackDisabledCaution();
            returnState = 1;
        } else {
            returnState = super.pushedPlayBackKey();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return returnState;
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayIndexKey() {
        int returnState;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (OCUtil.getInstance().getLastCaptureRecMode() == 2) {
            OCUtil.getInstance().openMoviePlaybackDisabledCaution();
            returnState = 1;
        } else {
            returnState = super.pushedPlayIndexKey();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return returnState;
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedEVDial() {
        AppLog.info(TAG, AppLog.getMethodName() + "Transit to shooting Done");
        OpticalCompensation.sIsFirstTimeLaunched = false;
        return super.turnedEVDial();
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedFocusModeDial() {
        AppLog.info(TAG, AppLog.getMethodName() + "Transit to shooting Done");
        OpticalCompensation.sIsFirstTimeLaunched = false;
        return super.turnedFocusModeDial();
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int movedAelFocusSlideKey() {
        AppLog.info(TAG, AppLog.getMethodName() + "Transit to shooting Done");
        OpticalCompensation.sIsFirstTimeLaunched = false;
        return super.movedAelFocusSlideKey();
    }
}
