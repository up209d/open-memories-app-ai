package com.sony.imaging.app.bracketpro.shooting.state;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.IkeyDispatchEach;
import com.sony.imaging.app.base.common.EventParcel;
import com.sony.imaging.app.base.common.FocusModeDialDetector;
import com.sony.imaging.app.base.menu.MenuState;
import com.sony.imaging.app.base.shooting.S1OffEEState;
import com.sony.imaging.app.base.shooting.StateBase;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.bracketpro.AppLog;
import com.sony.imaging.app.bracketpro.caution.CautionInfo;
import com.sony.imaging.app.bracketpro.commonUtil.BracketMasterBackUpKey;
import com.sony.imaging.app.bracketpro.commonUtil.BracketMasterUtil;
import com.sony.imaging.app.bracketpro.menu.controller.BMMenuController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.Definition;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class BMEECautionStateChild extends StateBase implements NotificationListener {
    private static final String NEXT_STATE_MENU = "Menu";
    private static final String NEXT_STATE_SHOOTING = "EE";
    public static final String TAG = AppLog.getClassName();
    private static String[] TAGS = {CameraNotificationManager.DEVICE_LENS_AFMF_SW_CHANGED, CameraNotificationManager.DEVICE_LENS_CHANGED, CameraNotificationManager.FOCUS_CHANGE};
    private CameraSetting mCamSet;

    public String getSelectedBracket() {
        String currentBracket = BackUpUtil.getInstance().getPreferenceString(BracketMasterBackUpKey.TAG_CURRENT_BRACKET_TYPE, BMMenuController.FocusBracket);
        return currentBracket;
    }

    public void isValidLens() {
        CautionUtilityClass.getInstance().executeTerminate();
        this.mCamSet = CameraSetting.getInstance();
        CameraEx.LensInfo info = this.mCamSet.getLensInfo();
        if (info == null) {
            displayLensCaution(CautionInfo.CAUTION_ID_INH_FACTOR_DETACH_EMOUNT_LENS);
            return;
        }
        if (info.LensType.equalsIgnoreCase("A-mount")) {
            displayAMountLensCaution(CautionInfo.CAUTION_ID_INH_FACTOR_INVALID_WITH_MOUNT_ADAPTER);
        } else {
            if (isMFModeSet()) {
                displayCaution();
                return;
            }
            disappearAfMfCaution();
            BMEEState.isBMCautionStateBooted = false;
            transitNextState("EE");
        }
    }

    private void disappearAfMfCaution() {
        int[] cautionId = CautionUtilityClass.getInstance().CurrentCautionId();
        for (int i : cautionId) {
            if (i == 141203) {
                CautionUtilityClass.getInstance().disapperTrigger(i);
                return;
            }
        }
    }

    private void displayLensCaution(int cautionid) {
        IkeyDispatchEach mKey = new IkeyDispatchEach() { // from class: com.sony.imaging.app.bracketpro.shooting.state.BMEECautionStateChild.1
            @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
            public int onKeyDown(int keyCode, KeyEvent event) {
                switch (event.getScanCode()) {
                    case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
                        CautionUtilityClass.getInstance().executeTerminate();
                        return 0;
                    case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                        CautionUtilityClass.getInstance().executeTerminate();
                        BMEECautionStateChild.this.transitAppTopState();
                        return 1;
                    case AppRoot.USER_KEYCODE.MOVIE_REC /* 515 */:
                    case AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_CHANGED /* 597 */:
                        BMEECautionStateChild.this.isValidLens();
                        return 0;
                    case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
                        CameraEx.LensInfo info = BMEECautionStateChild.this.mCamSet.getLensInfo();
                        if (info != null && info.LensType.equalsIgnoreCase("A-mount")) {
                            BMEECautionStateChild.this.isValidLens();
                            return 1;
                        }
                        BMEEState.isBMCautionStateBooted = false;
                        BMEECautionStateChild.this.transitNextState("EE");
                        return 1;
                    case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
                        CautionUtilityClass.getInstance().executeTerminate();
                        BMEECautionStateChild.this.transitToExposureMode();
                        return 1;
                    case AppRoot.USER_KEYCODE.UM_MOVIE_REC /* 616 */:
                        CautionUtilityClass.getInstance().executeTerminate();
                        return 0;
                    default:
                        int result = super.onKeyDown(keyCode, event);
                        return result;
                }
            }
        };
        CautionUtilityClass.getInstance().setDispatchKeyEvent(cautionid, mKey);
        CautionUtilityClass.getInstance().requestTrigger(cautionid);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void transitAppTopState() {
        Bundle mBundle = new Bundle();
        mBundle.putString(MenuState.ITEM_ID, BMMenuController.BRACKETPRO);
        mBundle.putString(MenuState.LAYOUT_ID, "ID_BRACKETMASTERSUBMENU");
        BMEEState.isBMCautionStateBooted = true;
        setNextState("Menu", mBundle);
    }

    private void displayAMountLensCaution(int cautionid) {
        IkeyDispatchEach mKey = new IkeyDispatchEach() { // from class: com.sony.imaging.app.bracketpro.shooting.state.BMEECautionStateChild.2
            @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
            public int onKeyUp(int keyCode, KeyEvent event) {
                switch (event.getScanCode()) {
                    case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
                        CautionUtilityClass.getInstance().executeTerminate();
                        BMEECautionStateChild.this.isValidLens();
                        break;
                }
                return super.onKeyUp(keyCode, event);
            }

            @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
            public int onKeyDown(int keyCode, KeyEvent event) {
                switch (event.getScanCode()) {
                    case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
                        CautionUtilityClass.getInstance().executeTerminate();
                        return 0;
                    case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                        CautionUtilityClass.getInstance().executeTerminate();
                        BMEECautionStateChild.this.transitAppTopState();
                        return 0;
                    case AppRoot.USER_KEYCODE.MOVIE_REC /* 515 */:
                        BMEECautionStateChild.this.isValidLens();
                        return 0;
                    case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
                        CautionUtilityClass.getInstance().executeTerminate();
                        BMEECautionStateChild.this.transitToExposureMode();
                        return 0;
                    case AppRoot.USER_KEYCODE.UM_MOVIE_REC /* 616 */:
                        CautionUtilityClass.getInstance().executeTerminate();
                        return 0;
                    default:
                        int result = super.onKeyDown(keyCode, event);
                        return result;
                }
            }
        };
        CautionUtilityClass.getInstance().setDispatchKeyEvent(cautionid, mKey);
        CautionUtilityClass.getInstance().requestTrigger(cautionid);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void transitNextState(String state) {
        AppLog.enter(TAG, AppLog.getMethodName());
        setNextState(state, null);
        Message msg = Message.obtain(getHandler(), Definition.STATE_TREE_CHANGED, 0, 0);
        getHandler().sendMessageAtFrontOfQueue(msg);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void transitToExposureMode() {
        Bundle mBundle = new Bundle();
        mBundle.putString(MenuState.ITEM_ID, ExposureModeController.EXPOSURE_MODE);
        setNextState("Menu", mBundle);
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        CameraNotificationManager.getInstance().setNotificationListener(this);
        disappearCautionForFocusBracket();
        setFocusMode();
        if (getSelectedBracket().equalsIgnoreCase(BMMenuController.FocusBracket) && !FocusModeDialDetector.hasFocusModeDial()) {
            isValidLens();
            return;
        }
        if (getSelectedBracket().equalsIgnoreCase(BMMenuController.FocusBracket) && FocusModeDialDetector.hasFocusModeDial()) {
            FocusModeController.getInstance().setValue("af-s");
            BMEEState.isBMCautionStateBooted = false;
            transitNextState("EE");
        } else {
            if (BracketMasterUtil.isHoldFocus()) {
                FocusModeController.getInstance().toggleFocusControl();
                Log.i("mtmr", "BMEECautionStateChild onResume");
            }
            BMEEState.isBMCautionStateBooted = false;
            transitNextState("EE");
        }
    }

    private void disappearCautionForFocusBracket() {
        if (BracketMasterUtil.getCurrentBracketType().equalsIgnoreCase(BMMenuController.FocusBracket)) {
            CautionUtilityClass.getInstance().executeTerminate();
        }
    }

    private void setFocusMode() {
        if (!FocusModeDialDetector.hasFocusModeDial()) {
            String currentBracket = BackUpUtil.getInstance().getPreferenceString(BracketMasterBackUpKey.TAG_CURRENT_BRACKET_TYPE, BMMenuController.FocusBracket);
            String focusMode = FocusModeController.getInstance().getValue();
            if (currentBracket != null && focusMode != null && currentBracket.equalsIgnoreCase(BMMenuController.FocusBracket)) {
                if (FocusModeController.getInstance().isFocusControl()) {
                    FocusModeController.getInstance().setToggleFocusMode(false);
                }
                FocusModeController.getInstance().setValue("af-s");
            }
        }
    }

    private boolean isMFModeSet() {
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean isMFModeSet = false;
        String currentBracket = BackUpUtil.getInstance().getPreferenceString(BracketMasterBackUpKey.TAG_CURRENT_BRACKET_TYPE, BMMenuController.FocusBracket);
        if (!FocusModeDialDetector.hasFocusModeDial() && currentBracket.equalsIgnoreCase(BMMenuController.FocusBracket) && !"af-s".equals(FocusModeController.getInstance().getValue())) {
            isMFModeSet = true;
        } else if (FocusModeDialDetector.hasFocusModeDial() && !"af-s".equals(FocusModeController.getInstance().getValue())) {
            isMFModeSet = true;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return isMFModeSet;
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        disappearCautionForFocusBracket();
        Message msg = Message.obtain(getHandler(), Definition.STATE_TREE_CHANGED, 0, 0);
        getHandler().sendMessageAtFrontOfQueue(msg);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        CautionUtilityClass.getInstance().executeTerminate();
        Bundle b = new Bundle();
        Bundle data = new Bundle();
        data.putParcelable(EventParcel.KEY_KEYCODE, new EventParcel(AppRoot.USER_KEYCODE.PLAYBACK));
        b.putParcelable(S1OffEEState.STATE_NAME, data);
        Activity appRoot = getActivity();
        ((BaseApp) appRoot).changeApp(BaseApp.APP_PLAY, b);
        return 1;
    }

    private void displayCaution() {
        CautionUtilityClass.getInstance().setDispatchKeyEvent(CautionInfo.CAUTION_ID_INH_FACTOR_EMOUNT_LENS_WITH_MF, new IkeyDispatchEachPF2());
        CautionUtilityClass.getInstance().requestTrigger(CautionInfo.CAUTION_ID_INH_FACTOR_EMOUNT_LENS_WITH_MF);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class IkeyDispatchEachPF2 extends IkeyDispatchEach {
        IkeyDispatchEachPF2() {
        }

        @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
        public int onKeyDown(int keyCode, KeyEvent event) {
            switch (event.getScanCode()) {
                case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
                case AppRoot.USER_KEYCODE.UM_MOVIE_REC /* 616 */:
                    CautionUtilityClass.getInstance().executeTerminate();
                    return 0;
                case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                    CautionUtilityClass.getInstance().executeTerminate();
                    BMEECautionStateChild.this.transitAppTopState();
                    return 1;
                case AppRoot.USER_KEYCODE.MOVIE_REC /* 515 */:
                case AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_CHANGED /* 597 */:
                    BMEECautionStateChild.this.isValidLens();
                    return 0;
                case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
                    CameraEx.LensInfo info = BMEECautionStateChild.this.mCamSet.getLensInfo();
                    if (info != null && info.LensType.equalsIgnoreCase("A-mount")) {
                        BMEECautionStateChild.this.isValidLens();
                        return 1;
                    }
                    BMEEState.isBMCautionStateBooted = false;
                    BMEECautionStateChild.this.transitNextState("EE");
                    return 1;
                case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
                    CautionUtilityClass.getInstance().executeTerminate();
                    BMEECautionStateChild.this.transitToExposureMode();
                    return 1;
                default:
                    int result = super.onKeyDown(keyCode, event);
                    return result;
            }
        }
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        if (tag.equals(CameraNotificationManager.DEVICE_LENS_AFMF_SW_CHANGED) || tag.equals(CameraNotificationManager.DEVICE_LENS_CHANGED) || tag.equals(CameraNotificationManager.FOCUS_CHANGE)) {
            isValidLens();
        }
    }
}
