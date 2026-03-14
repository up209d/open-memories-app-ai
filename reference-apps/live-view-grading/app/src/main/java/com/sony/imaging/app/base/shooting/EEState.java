package com.sony.imaging.app.base.shooting;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.caution.CautionID;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.common.FocusModeDialDetector;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.MenuController;
import com.sony.imaging.app.base.menu.MenuState;
import com.sony.imaging.app.base.menu.MenuTable;
import com.sony.imaging.app.base.menu.layout.BaseMenuLayout;
import com.sony.imaging.app.base.shooting.camera.AELController;
import com.sony.imaging.app.base.shooting.camera.AutoFocusModeController;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.FocusMagnificationController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.movie.MovieCaptureState;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomKeyMgr;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.HoldKeyServer;
import com.sony.imaging.app.fw.ICustomKey;
import com.sony.imaging.app.fw.StateHandle;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.provider.AvindexStore;
import com.sony.scalar.sysutil.KeyStatus;
import com.sony.scalar.sysutil.ScalarInput;
import com.sony.scalar.sysutil.didep.Settings;
import java.util.List;

/* loaded from: classes.dex */
public class EEState extends StateBase {
    private static final String CAPTURE_TRANSITION = "Capture";
    private static final String CHILD_S1_OFF_STATE = "S1OffEE";
    private static final String CHILD_S1_ON_STATE = "S1OnEE";
    private static final String CUSTOM_WB_TRANSITION = "CustomWhiteBalance";
    private static final String DATA_TAG_ITEMID = "ItemId";
    private static final String EXIT_TO_CONFIG = "ExitToConfig";
    private static final String EXIT_TO_PLAY = "ExitToPlay";
    private static final String EXIT_TO_POWEROFF = "ExitToPowerOff";
    private static final String EXIT_TO_SETUP = "ExitToSetup";
    private static final String EXIT_TO_WIFI = "ExitToWifi";
    private static final String FOCUS_ADJUSTMENT_MF = "FocusAdjustmentMf";
    private static final String FOCUS_ADJUSTMENT_SMF = "FocusAdjustmentSmf";
    private static final String FOCUS_ADJUSTMENT_STATE = "FocusAdjustment";
    private static final String FOCUS_MAGNIFIER = "MfAssist";
    private static final String INH_FACTOR_STILL_WRITING = "INH_FACTOR_STILL_WRITING";
    private static final String KEY_CODE = "keyCode";
    private static final String KEY_CODE_MODEDIAL = "keyCodeModeDial";
    private static final String KEY_CODE_SHOOTINGMODE = "keyCodeShootingMode";
    private static final String LOG_S2_ON = "handling S2 on";
    private static final String MENU_STATE = "Menu";
    private static final String MOVIE_CAPTURE_STATE = "MovieCapture";
    private static final String MOVIE_REC_STANDBY_STATE = "MovieRecStandby";
    private static final String MSG_RELEASE_AEL_HOLD = "release AEL hold";
    private static final String MSG_RELEASE_AFMF_HOLD = "release AF/MF hold";
    private static final String NEXT_STATE_MENU = "Menu";
    private static final String NORMAL_TRANSTION = "NormalTransition";
    private static final String TAG = "EEState";
    private static final String TRANSITION_FROM = "from";
    private StateHandle handle;
    private String mItemId = null;
    private Bundle mBundle = null;

    @Override // com.sony.imaging.app.fw.KeyReceiver
    protected String getKeyConvCategory() {
        ExposureModeController cntl = ExposureModeController.getInstance();
        String expMode = cntl.getValue(null);
        if (ExposureModeController.PROGRAM_AUTO_MODE.equals(expMode)) {
            return ICustomKey.CATEGORY_SHOOTING_P;
        }
        if ("Aperture".equals(expMode)) {
            return ICustomKey.CATEGORY_SHOOTING_A;
        }
        if ("Shutter".equals(expMode)) {
            return ICustomKey.CATEGORY_SHOOTING_S;
        }
        if (ExposureModeController.MANUAL_MODE.equals(expMode)) {
            return ICustomKey.CATEGORY_SHOOTING_M;
        }
        return ICustomKey.CATEGORY_SHOOTING_OTHER;
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        BaseShootingExecutor shootingExecutor = ExecutorCreator.getInstance().getSequence();
        shootingExecutor.startPreview();
        stopObserveEVDial();
        stopObserveFocusModeDial();
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.SLIDE_AEL_FOCUS_CHANGED, 0);
        if (this.data != null) {
            this.mItemId = this.data.getString("ItemId");
        }
        boolean startMovie = false;
        Bundle b = this.data;
        if (b != null && b.containsKey("keyCode")) {
            String from = b.getString("from");
            if (BaseApp.APP_PLAY.equals(from)) {
                int keycode = b.getInt("keyCode");
                if ((515 == keycode || 637 == keycode || 616 == keycode || 643 == keycode) && Environment.isMovieAPISupported() && ExecutorCreator.getInstance().isMovieModeSupported() && (!ModeDialDetector.hasModeDial() || ExposureModeController.getInstance().isValidDialPosition())) {
                    if (Settings.getMovieButtonMode() == 0) {
                        boolean writing = false;
                        if (AvindexStore.getExternalMediaIds()[0].equals(ExecutorCreator.getInstance().getSequence().getRecordingMedia())) {
                            AvailableInfo.update();
                            writing = AvailableInfo.isFactor(INH_FACTOR_STILL_WRITING);
                        }
                        if (!writing) {
                            String expMode = ExposureModeController.getInstance().getValue(null);
                            if (ExposureModeController.PROGRAM_AUTO_MODE.equals(expMode)) {
                                CameraSetting.getInstance().resetProgramLine();
                            }
                            Bundle data = null;
                            if (1 == ExecutorCreator.getInstance().getRecordingMode()) {
                                data = new Bundle();
                                data.putString(MovieCaptureState.MOVIE_REC_FROM, MovieCaptureState.FROM_STILL);
                            } else if (4 == ExecutorCreator.getInstance().getRecordingMode()) {
                                data = new Bundle();
                                data.putString(MovieCaptureState.MOVIE_REC_FROM, "Interval");
                            }
                            setNextState(MOVIE_CAPTURE_STATE, data);
                            startMovie = true;
                        } else {
                            CautionUtilityClass.getInstance().requestTrigger(1410);
                        }
                    } else {
                        CautionUtilityClass.getInstance().requestTrigger(2057);
                    }
                }
                KeyStatus status = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.S1_ON);
                if (1 == status.status) {
                    ExecutorCreator executorCreator = ExecutorCreator.getInstance();
                    if (!executorCreator.isAElockedOnAutoFocus()) {
                        String behavior = null;
                        String focusMode = AutoFocusModeController.getInstance().getValue();
                        if ("af-s".equals(focusMode)) {
                            behavior = "af_woaf";
                        } else if ("af-c".equals(focusMode)) {
                            behavior = "afc_woaf";
                        }
                        executorCreator.getSequence().autoFocus(null, behavior);
                    } else {
                        executorCreator.getSequence().autoFocus(null);
                    }
                }
            }
            if (624 == b.getInt("keyCode")) {
                if (Environment.isMovieAPISupported() && ExecutorCreator.getInstance().isMovieModeSupported()) {
                    AvailableInfo.update();
                    if (!AvailableInfo.isFactor(INH_FACTOR_STILL_WRITING)) {
                        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED, 2);
                        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.SLIDE_AF_MF, 1);
                        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.SLIDE_AEL_FOCUS_CHANGED, 1);
                        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.IRIS_DIAL_CHANGED, 1);
                        String expMode2 = ExposureModeController.getInstance().getValue(null);
                        if (ExposureModeController.PROGRAM_AUTO_MODE.equals(expMode2)) {
                            CameraSetting.getInstance().resetProgramLine();
                        }
                        ExecutorCreator ec = ExecutorCreator.getInstance();
                        ec.setRecordingMode(2, null);
                        this.data.putInt(KEY_CODE_SHOOTINGMODE, AppRoot.USER_KEYCODE.SHOOTING_MODE);
                    } else {
                        CautionUtilityClass.getInstance().requestTrigger(1410);
                    }
                } else {
                    CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_CANNOT_REC_MOVIE);
                }
            } else if (597 == b.getInt("keyCode") && FocusModeDialDetector.getFocusModeDialPosition() != -1) {
                FocusModeController focusModeController = FocusModeController.getInstance();
                focusModeController.setValue(focusModeController.getFocusModeFromFocusModeDial());
            }
            b.remove("keyCode");
            this.mBundle = null;
        }
        AELController aelController = AELController.getInstance();
        if (aelController.isAELockByAELHold()) {
            int status2 = CustomKeyMgr.getInstance().getKeyStatusByCustomFuncCode(getKeyConvCategory(), CustomizableFunction.AelHold);
            if (1 != status2) {
                Log.i(TAG, MSG_RELEASE_AEL_HOLD);
                aelController.holdAELock(false);
            }
        }
        FocusModeController focusController = FocusModeController.getInstance();
        if (focusController.isFocusHeld()) {
            int status3 = CustomKeyMgr.getInstance().getKeyStatusByCustomFuncCode(getKeyConvCategory(), CustomizableFunction.AfMfHold);
            if (1 != status3) {
                Log.i(TAG, MSG_RELEASE_AFMF_HOLD);
                focusController.holdFocusControl(false);
            }
        }
        boolean openMenu = false;
        if (this.data != null && this.data.containsKey(BaseApp.DA_MODE_TO_OPEN_MENU)) {
            int mode = this.data.getInt(BaseApp.DA_MODE_TO_OPEN_MENU);
            MenuController.MenuSelection menuSelection = MenuController.getInstance().convertFromScalarProperty(mode);
            if (!startMovie && menuSelection != null) {
                BaseMenuService service = new BaseMenuService(getActivity());
                String layoutID = service.getMenuItemCustomStartLayoutID(menuSelection.containerId);
                Bundle bundle = new Bundle();
                bundle.putString(BaseMenuLayout.BUNDLE_KEY_SELECTED_ITEM, menuSelection.itemId);
                bundle.putString("ItemId", menuSelection.containerId);
                bundle.putString(MenuState.LAYOUT_ID, layoutID);
                bundle.putBoolean(MenuState.IS_HISTORY_NEEDED, true);
                this.handle = addChildState(ICustomKey.CATEGORY_MENU, bundle);
                openMenu = true;
            }
            this.data.remove(BaseApp.DA_MODE_TO_OPEN_MENU);
        }
        if (!startMovie && !openMenu) {
            this.handle = addChildState(getNextChildState(), setStateBundle());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Bundle setStateBundle() {
        if (this.mBundle == null) {
            this.mBundle = new Bundle();
        }
        return this.mBundle;
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase
    protected int getMyBeepPattern() {
        return 6;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver
    public boolean handleMessage(Message msg) {
        Bundle bundle = null;
        String nextState = null;
        if (msg.obj instanceof Bundle) {
            bundle = (Bundle) msg.obj;
            nextState = bundle.getString(MenuTable.NEXT_STATE);
        }
        if (518 == msg.what) {
            Log.i(TAG, LOG_S2_ON);
            setNextState(CAPTURE_TRANSITION, bundle);
            return true;
        }
        if (CUSTOM_WB_TRANSITION.equals(nextState)) {
            setNextState(CUSTOM_WB_TRANSITION, bundle);
            return true;
        }
        if (NORMAL_TRANSTION.equals(nextState)) {
            addChildState(getNextChildState(), bundle);
            return false;
        }
        if ("MfAssist".equals(nextState)) {
            FocusMagnificationController controller = FocusMagnificationController.getInstance();
            List<Integer> supported = controller.getSupportedValueInt(FocusMagnificationController.TAG_MAGNIFICATION_RATIO);
            if (supported != null && supported.size() > 0 && controller.isAvailable(FocusMagnificationController.ITEM_ID_FOCUS_MAGNIFICATION)) {
                controller.startFocusMagnifier();
                addChildState("MfAssist", bundle);
                return false;
            }
            if (2 == CameraSetting.getInstance().getCurrentMode()) {
                CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_INVALID_MOVIE_MODE);
            } else {
                CautionUtilityClass.getInstance().requestTrigger(CautionID.CAUTION_GRP_ID_FOCUS_MAGNIFIER_IMAGER_INVALID_GUIDE);
            }
            addChildState(getNextChildState(), bundle);
            return false;
        }
        if (FOCUS_ADJUSTMENT_MF.equals(nextState) || FOCUS_ADJUSTMENT_SMF.equals(nextState)) {
            if (FOCUS_ADJUSTMENT_MF.equals(nextState)) {
                FocusModeController.getInstance().setValue(FocusModeController.MANUAL);
            } else {
                FocusModeController.getInstance().setValue(FocusModeController.SMF);
            }
            addChildState(FOCUS_ADJUSTMENT_STATE, bundle);
            return false;
        }
        if (EXIT_TO_PLAY.equals(nextState)) {
            if (!Environment.isNewBizDeviceActionCam() || !Environment.hasSubLcd()) {
                return false;
            }
            setNextState(EXIT_TO_PLAY, bundle);
            return true;
        }
        if (EXIT_TO_POWEROFF.equals(nextState)) {
            if (!Environment.isNewBizDeviceActionCam() || !Environment.hasSubLcd()) {
                return false;
            }
            setNextState(EXIT_TO_POWEROFF, bundle);
            return true;
        }
        if (EXIT_TO_CONFIG.equals(nextState)) {
            if (!Environment.isNewBizDeviceActionCam() || !Environment.hasSubLcd()) {
                return false;
            }
            setNextState(EXIT_TO_CONFIG, bundle);
            return true;
        }
        if (EXIT_TO_WIFI.equals(nextState)) {
            if (!Environment.isNewBizDeviceActionCam() || !Environment.hasSubLcd()) {
                return false;
            }
            setNextState(EXIT_TO_WIFI, bundle);
            return true;
        }
        if (!EXIT_TO_SETUP.equals(nextState) || !Environment.isNewBizDeviceActionCam() || !Environment.hasSubLcd()) {
            return false;
        }
        setNextState(EXIT_TO_SETUP, bundle);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getNextChildState() {
        if (this.data != null && 572 == this.data.getInt(KEY_CODE_MODEDIAL)) {
            this.data.remove(KEY_CODE_MODEDIAL);
            this.mBundle = new Bundle();
            this.mBundle.putString("ItemId", ExposureModeController.EXPOSURE_MODE);
            return ICustomKey.CATEGORY_MENU;
        }
        if (this.data != null && 624 == this.data.getInt(KEY_CODE_SHOOTINGMODE)) {
            return MOVIE_REC_STANDBY_STATE;
        }
        if (ExposureModeController.EXPOSURE_MODE.equals(this.mItemId)) {
            this.mBundle = new Bundle();
            this.mBundle.putString("ItemId", this.mItemId);
            this.mItemId = null;
            return ICustomKey.CATEGORY_MENU;
        }
        if (DriveModeController.DRIVEMODE.equals(this.mItemId)) {
            this.mBundle = new Bundle(this.data);
            this.mItemId = null;
            return ICustomKey.CATEGORY_MENU;
        }
        if (1 == CameraSetting.getInstance().getCurrentMode()) {
            KeyStatus key = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.S1_ON);
            if (key.valid == 1) {
                if (key.status == 1) {
                    return CHILD_S1_ON_STATE;
                }
                if (this.data == null) {
                    return "S1OffEE";
                }
                this.mBundle = new Bundle(this.data);
                return "S1OffEE";
            }
            if (key.valid != 0) {
                return "S1OffEE";
            }
            Log.w(TAG, "KeyCode:516 is not existed");
            return "S1OffEE";
        }
        if (2 != CameraSetting.getInstance().getCurrentMode()) {
            return "S1OffEE";
        }
        return MOVIE_REC_STANDBY_STATE;
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        this.mBundle = null;
        startObserveEVDial();
        startObserveFocusModeDial();
        super.onPause();
    }
}
