package com.sony.imaging.app.bracketpro.shooting.state;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.IkeyDispatchEach;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.MenuState;
import com.sony.imaging.app.base.shooting.EEState;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.bracketpro.AppLog;
import com.sony.imaging.app.bracketpro.caution.CautionInfo;
import com.sony.imaging.app.bracketpro.commonUtil.BracketMasterBackUpKey;
import com.sony.imaging.app.bracketpro.menu.controller.BMExposureModeController;
import com.sony.imaging.app.bracketpro.menu.controller.BMMenuController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.util.ApoWrapper;
import com.sony.imaging.app.util.BackUpUtil;

/* loaded from: classes.dex */
public class BMEEState extends EEState {
    private static final String CHILD_S1_OFF_STATE = "S1OffEE";
    private static final String DATA_TAG_ITEMID = "ItemId";
    public static final String NEXT_STATE_CHILD_LENS_CAUTION_STATE = "BMLensCautionStateChild";
    private static final String NEXT_STATE_MENU = "Menu";
    private Bundle mBundle = null;
    private String mItemId;
    private static final String TAG = AppLog.getClassName();
    public static boolean mMenuStateAdd = true;
    public static boolean isBMCautionStateBooted = true;

    @Override // com.sony.imaging.app.base.shooting.EEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.data == null) {
            this.mItemId = BMMenuController.BRACKETPRO;
        } else {
            AppLog.enter(TAG, "Bundle is not null");
            this.mItemId = this.data.getString("ItemId");
            this.data.putString("ItemId", null);
        }
        AppLog.enter(TAG, AppLog.getMethodName());
        super.onResume();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.EEState
    public String getNextChildState() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (ExposureModeController.EXPOSURE_MODE.equals(this.mItemId)) {
            AppLog.info(TAG, "Mode Dial  screen opened ");
            this.mBundle = new Bundle();
            this.mBundle.putString("ItemId", this.mItemId);
            this.mBundle.putString(ExposureModeController.EXPOSURE_MODE, this.mItemId);
            this.mItemId = null;
            AppLog.exit(TAG, AppLog.getMethodName());
            isBMCautionStateBooted = true;
            return "Menu";
        }
        if (mMenuStateAdd) {
            AppLog.info(TAG, "Menu  screen opened ");
            this.mBundle = new Bundle();
            this.mItemId = BMMenuController.BRACKETPRO;
            BaseMenuService service = new BaseMenuService(getActivity());
            String layoutID = service.getMenuItemCustomStartLayoutID(this.mItemId);
            this.mBundle.putString("ItemId", this.mItemId);
            this.mBundle.putString(MenuState.LAYOUT_ID, layoutID);
            setIsMenuStateAdd(false);
            this.mItemId = null;
            isBMCautionStateBooted = true;
            return "Menu";
        }
        if (isBMCautionStateBooted) {
            return NEXT_STATE_CHILD_LENS_CAUTION_STATE;
        }
        AppLog.info(TAG, "Default  screen opened ");
        if (!ModeDialDetector.hasModeDial()) {
            updateExposureModeAutomatically();
        }
        String currentBracket = BackUpUtil.getInstance().getPreferenceString(BracketMasterBackUpKey.TAG_CURRENT_BRACKET_TYPE, BMMenuController.FocusBracket);
        String focusMode = FocusModeController.getInstance().getValue();
        Log.i("mtmr", "getNextChildState currentBracket:" + currentBracket);
        Log.i("mtmr", "getNextChildState focusMode:" + focusMode);
        if (currentBracket != null && focusMode != null && BMMenuController.FocusBracket.equalsIgnoreCase(currentBracket)) {
            boolean isfocus = FocusModeController.getInstance().isFocusControl();
            if (isfocus) {
                FocusModeController.getInstance().cancelFocusControl();
            }
            if (!"af-s".equals(focusMode)) {
                FocusModeController.getInstance().setValue(FocusModeController.API_NAME, "af-s");
                Log.i("mtmr", "getNextChildState:AF_S");
            }
        } else if (BMMenuController.FlashBracket.equalsIgnoreCase(currentBracket)) {
            checkFlashCaution();
        }
        String state = super.getNextChildState();
        BMMenuController.getInstance().setShootingScreenOpened(true);
        BMMenuController.getInstance().setRangeStatus(false);
        AppLog.exit(TAG, AppLog.getMethodName());
        return state;
    }

    private void checkFlashCaution() {
        boolean isExternalFlashAvailable = CameraSetting.getInstance().getFlashExternalEnable();
        boolean isInternalFlashAvailable = CameraSetting.getInstance().getFlashInternalEnable();
        boolean isFlashBracet = BMMenuController.getInstance().getSelectedBracket().equalsIgnoreCase(BMMenuController.FlashBracket);
        boolean isShowCaution = false;
        if (!isExternalFlashAvailable) {
            isShowCaution = true;
        } else if (isExternalFlashAvailable) {
            isShowCaution = false;
        }
        if (!isExternalFlashAvailable) {
            if (isInternalFlashAvailable) {
                isShowCaution = false;
            } else if (!isInternalFlashAvailable) {
                isShowCaution = true;
            }
        }
        if (isFlashBracet && isShowCaution && BMMenuController.getInstance().isShowFlashCaution()) {
            BMMenuController.getInstance().setShowFlashCaution(false);
            displayCaution();
        }
    }

    private void displayCaution() {
        IkeyDispatchEach mKey = new IkeyDispatchEach() { // from class: com.sony.imaging.app.bracketpro.shooting.state.BMEEState.1
            @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
            public int onKeyUp(int keyCode, KeyEvent event) {
                switch (event.getScanCode()) {
                    case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
                        CautionUtilityClass.getInstance().executeTerminate();
                        return 0;
                    default:
                        return -1;
                }
            }

            @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
            public int onKeyDown(int keyCode, KeyEvent event) {
                switch (event.getScanCode()) {
                    case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
                    case AppRoot.USER_KEYCODE.MOVIE_REC /* 515 */:
                    case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
                    case AppRoot.USER_KEYCODE.S2_ON /* 518 */:
                    case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
                    case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
                    case AppRoot.USER_KEYCODE.UM_MOVIE_REC /* 616 */:
                    case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
                    case AppRoot.USER_KEYCODE.LENS_DETACH /* 786 */:
                        CautionUtilityClass.getInstance().executeTerminate();
                        return 0;
                    case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                        CautionUtilityClass.getInstance().executeTerminate();
                        return 1;
                    default:
                        return -1;
                }
            }
        };
        CautionUtilityClass.getInstance().setDispatchKeyEvent(CautionInfo.CAUTION_ID_CAU_FLASH_INVALID, mKey);
        CautionUtilityClass.getInstance().requestTrigger(CautionInfo.CAUTION_ID_CAU_FLASH_INVALID);
    }

    private void updateExposureModeAutomatically() {
        AppLog.enter(TAG, AppLog.getMethodName());
        String expMode = BMExposureModeController.getInstance().getValue(null);
        BMExposureModeController emc = BMExposureModeController.getInstance();
        if (!emc.isValidExpoMode(expMode)) {
            AppLog.enter(TAG, "Invalid exposure mode for current bracket....Automatically update to valid one.");
            String currentBracket = BackUpUtil.getInstance().getPreferenceString(BracketMasterBackUpKey.TAG_CURRENT_BRACKET_TYPE, BMMenuController.FocusBracket);
            AppLog.enter(TAG, "currentBracket:" + currentBracket);
            if (currentBracket.equalsIgnoreCase(BMMenuController.ApertureBracket)) {
                emc.setValue(ExposureModeController.EXPOSURE_MODE, "Aperture");
            } else if (currentBracket.equalsIgnoreCase(BMMenuController.ShutterSpeedBracket)) {
                emc.setValue(ExposureModeController.EXPOSURE_MODE, "Shutter");
            } else {
                emc.setValue(ExposureModeController.EXPOSURE_MODE, ExposureModeController.PROGRAM_AUTO_MODE);
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.EEState
    protected Bundle setStateBundle() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.mBundle;
    }

    public static void setIsMenuStateAdd(boolean isAdd) {
        AppLog.enter(TAG, AppLog.getMethodName());
        mMenuStateAdd = isAdd;
        AppLog.exit(TAG, AppLog.getMethodName());
        Log.i(TAG, "setIsMenuStateAdd:" + mMenuStateAdd);
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State
    public ApoWrapper.APO_TYPE getApoType() {
        ApoWrapper.APO_TYPE type = ApoWrapper.APO_TYPE.NORMAL;
        if (DriveModeController.getInstance().isRemoteControl()) {
            ApoWrapper.APO_TYPE type2 = ApoWrapper.APO_TYPE.NONE;
            return type2;
        }
        return type;
    }
}
