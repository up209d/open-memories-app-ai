package com.sony.imaging.app.startrails.shooting.state;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import com.sony.imaging.app.avi.DatabaseUtil;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.IkeyDispatchEach;
import com.sony.imaging.app.base.common.EVDialDetector;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.menu.MenuState;
import com.sony.imaging.app.base.shooting.EEState;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.camera.PictureEffectController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.fw.State;
import com.sony.imaging.app.startrails.base.menu.controller.STPictureEffectController;
import com.sony.imaging.app.startrails.common.caution.STInfo;
import com.sony.imaging.app.startrails.database.DataBaseAdapter;
import com.sony.imaging.app.startrails.database.DataBaseOperations;
import com.sony.imaging.app.startrails.menu.controller.ThemeSelectionController;
import com.sony.imaging.app.startrails.shooting.STExecutorCreator;
import com.sony.imaging.app.startrails.util.AppLog;
import com.sony.imaging.app.startrails.util.STBackUpKey;
import com.sony.imaging.app.startrails.util.STConstants;
import com.sony.imaging.app.startrails.util.STUtility;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.BeepUtility;
import com.sony.imaging.app.util.BeepUtilityRsrcTable;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class STEEState extends EEState {
    private static final String NEXT_STATE_MENU = "Menu";
    private static final String TAG = "STEEState";
    private String mItemId;
    private static STUtility sSTUtility = null;
    private static int sCautionId = 0;
    private Bundle mBundle = null;
    boolean isNextStateMFAssist = false;
    protected RemainingMemoryLessDispatcher mRemainingMemoryLessDispatcher = new RemainingMemoryLessDispatcher(this);
    protected NotificationListener mediaStatusChangedListener = new MediaNotificationListener();

    @Override // com.sony.imaging.app.base.shooting.EEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        if (STUtility.getInstance().getCurrentTrail() == 2) {
            String pictureEffect = BackUpUtil.getInstance().getPreferenceString(STBackUpKey.CUSTOM_PICTURE_EFFECT_KEY, "off");
            if (PictureEffectController.MODE_SOFT_FOCUS.equals(pictureEffect)) {
                String value = BackUpUtil.getInstance().getPreferenceString(STBackUpKey.CUSTOM_PICTURE_EFFECT_KEY_OPTION, PictureEffectController.SOFT_FOCUS_MEDIUM);
                if (PictureEffectController.SOFT_FOCUS_HIGH.equals(value) || PictureEffectController.SOFT_FOCUS_MEDIUM.equals(value) || PictureEffectController.SOFT_FOCUS_LOW.equals(value)) {
                    STPictureEffectController.getInstance().setValue(PictureEffectController.PICTUREEFFECT, PictureEffectController.MODE_SOFT_FOCUS);
                    STPictureEffectController.getInstance().setValue(PictureEffectController.MODE_SOFT_FOCUS, value);
                }
            }
        }
        sSTUtility = STUtility.getInstance();
        STExecutorCreator creator = (STExecutorCreator) STExecutorCreator.getInstance();
        creator.saveStarTrailsBOData();
        if (this.data != null) {
            this.mItemId = this.data.getString(MenuState.ITEM_ID);
            this.data.putString(MenuState.ITEM_ID, null);
        }
        super.onResume();
        MediaNotificationManager.getInstance().setNotificationListener(this.mediaStatusChangedListener);
        displayDBErrorCaution();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.EEState
    public String getNextChildState() {
        String nextChildState;
        if (ExposureModeController.EXPOSURE_MODE.equals(this.mItemId)) {
            AppLog.info(TAG, "for menu state mItemId" + this.mItemId);
            this.mBundle = new Bundle();
            this.mBundle.putString(ExposureModeController.EXPOSURE_MODE, this.mItemId);
            this.mBundle.putString(MenuState.ITEM_ID, this.mItemId);
            this.mItemId = null;
            nextChildState = "Menu";
        } else if (STUtility.getInstance().isAppTopBooted()) {
            STUtility.getInstance().setMenuBoot(false);
            AppLog.info(TAG, "for menu state mItemId" + this.mItemId);
            this.mBundle = new Bundle();
            this.mItemId = ThemeSelectionController.APP_TOP;
            this.mBundle.putString(MenuState.ITEM_ID, this.mItemId);
            this.mItemId = null;
            nextChildState = "Menu";
        } else if (isMFModeNotSet()) {
            nextChildState = STMFModeCheckState.TAG;
        } else {
            if (!STUtility.getInstance().isPlayBackKeyPressed() && STUtility.getInstance().isUpdateThemePropertyRequired()) {
                CameraNotificationManager.getInstance().requestNotify(STConstants.THEME_TAG_UPDATED);
            }
            STUtility.getInstance().setUpdateThemeProperty(false);
            nextChildState = super.getNextChildState();
        }
        this.data = null;
        return nextChildState;
    }

    private boolean isMFModeNotSet() {
        if (FocusModeController.MANUAL.equals(FocusModeController.getInstance().getValue())) {
            return false;
        }
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.EEState
    protected Bundle setStateBundle() {
        return this.mBundle;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        this.mBundle = null;
        super.onDestroy();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void displayDBErrorCaution() {
        if (DataBaseAdapter.getInstance().isDBStatusCorrupt()) {
            CautionUtilityClass.getInstance().setDispatchKeyEvent(STInfo.CAUTION_ID_DLAPP_DB_ERROR, null);
            CautionUtilityClass.getInstance().requestTrigger(STInfo.CAUTION_ID_DLAPP_DB_ERROR);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.EEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        STUtility.getInstance().setPlayBackKeyPressed(true);
        if (STUtility.getInstance().getCurrentTrail() == 2) {
            String value = STPictureEffectController.getInstance().getValue();
            if (PictureEffectController.SOFT_FOCUS_HIGH.equals(value) || PictureEffectController.SOFT_FOCUS_MEDIUM.equals(value) || PictureEffectController.SOFT_FOCUS_LOW.equals(value)) {
                PictureEffectController.getInstance().setValue(PictureEffectController.PICTUREEFFECT, "off");
                BackUpUtil.getInstance().setPreference(STBackUpKey.CUSTOM_PICTURE_EFFECT_KEY_OPTION, value);
                BackUpUtil.getInstance().setPreference(STBackUpKey.CUSTOM_PICTURE_EFFECT_KEY, PictureEffectController.MODE_SOFT_FOCUS);
            }
        }
        super.onPause();
        disapearDisplayingCaution(STInfo.CAUTION_ID_DLAPP_NO_MEMEORY_CARD_SHOOTING);
        disapearDisplayingCaution(STInfo.CAUTION_ID_DLAPP_REMAINING_MEMORY_LESS);
        MediaNotificationManager.getInstance().removeNotificationListener(this.mediaStatusChangedListener);
    }

    @Override // com.sony.imaging.app.base.shooting.EEState, com.sony.imaging.app.fw.KeyReceiver
    public boolean handleMessage(Message msg) {
        sCautionId = 0;
        if (518 == msg.what) {
            sSTUtility.pinCalendar();
            sCautionId = sSTUtility.getCautionId();
            if (sCautionId != 0) {
                AppLog.trace(TAG, "handleMessage() sCautionId" + sCautionId);
                if (sCautionId == 131205) {
                    CautionUtilityClass.getInstance().setDispatchKeyEvent(sCautionId, this.mRemainingMemoryLessDispatcher);
                }
                CautionUtilityClass.getInstance().requestTrigger(sCautionId);
            }
        }
        if (sCautionId != 0) {
            return false;
        }
        AppLog.trace(TAG, "handleMessage() sCautionId" + sCautionId);
        boolean ret = super.handleMessage(msg);
        return ret;
    }

    /* loaded from: classes.dex */
    private class RemainingMemoryLessDispatcher extends IkeyDispatchEach {
        private State mState;

        public RemainingMemoryLessDispatcher(State state) {
            this.mState = state;
        }

        @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IConvertibleKeyHandler
        public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
            int result = 0;
            if (CustomizableFunction.Unknown.equals(func)) {
                result = 1;
            }
            if (CustomizableFunction.Unchanged.equals(func)) {
                int code = event.getScanCode();
                if (code == 0) {
                    code = event.getKeyCode();
                }
                if (event.getAction() == 0) {
                    switch (code) {
                        case 23:
                        case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                            if (STEEState.sSTUtility.isOkFocusedOnRemainingMemroryCaution()) {
                                startCapturing();
                                return 1;
                            }
                            cancelCapturing();
                            return 1;
                        case 82:
                        case AppRoot.USER_KEYCODE.SK1 /* 229 */:
                        case AppRoot.USER_KEYCODE.MENU /* 514 */:
                            cancelCapturing();
                            return 1;
                        case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
                        case AppRoot.USER_KEYCODE.MOVIE_REC /* 515 */:
                        case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
                        case AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_CHANGED /* 597 */:
                        case AppRoot.USER_KEYCODE.UM_MOVIE_REC /* 616 */:
                        case AppRoot.USER_KEYCODE.LENS_DETACH /* 786 */:
                            break;
                        case AppRoot.USER_KEYCODE.S2_ON /* 518 */:
                        case AppRoot.USER_KEYCODE.S1_OFF /* 772 */:
                        case AppRoot.USER_KEYCODE.S2_OFF /* 774 */:
                            return result;
                        case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
                            if (STUtility.getInstance().getCurrentTrail() == 2) {
                                CautionUtilityClass.getInstance().disapperTrigger(STInfo.CAUTION_ID_DLAPP_REMAINING_MEMORY_LESS);
                                return 0;
                            }
                            return -1;
                        case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
                            STUtility.getInstance().setEVDialRotated(true);
                            ExposureCompensationController.getInstance().setEvDialValue(EVDialDetector.getEVDialPosition());
                            break;
                        default:
                            return 1;
                    }
                    CautionUtilityClass.getInstance().disapperTrigger(STInfo.CAUTION_ID_DLAPP_REMAINING_MEMORY_LESS);
                    return 0;
                }
                return result;
            }
            return 1;
        }

        private void cancelCapturing() {
            STEEState.disapearDisplayingCaution(STInfo.CAUTION_ID_DLAPP_REMAINING_MEMORY_LESS);
            BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_SELECT);
        }

        private void startCapturing() {
            this.mState.setNextState("Capture", null);
            STEEState.disapearDisplayingCaution(STInfo.CAUTION_ID_DLAPP_REMAINING_MEMORY_LESS);
        }

        @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IConvertibleKeyHandler
        public int onConvertedKeyUp(KeyEvent event, IKeyFunction func) {
            switch (event.getScanCode()) {
                case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
                case AppRoot.USER_KEYCODE.LENS_DETACH /* 786 */:
                    CautionUtilityClass.getInstance().executeTerminate();
                    STEEState.this.onResume();
                    break;
            }
            return super.onConvertedKeyUp(event, func);
        }
    }

    /* loaded from: classes.dex */
    private static class MediaNotificationListener implements NotificationListener {
        private final String CAUTION_NOT_EXIST;
        private final String INDEX_NOT_EXIST;
        private String[] tags;

        private MediaNotificationListener() {
            this.CAUTION_NOT_EXIST = "onNotify called and caution CautionUtilityClass.getInstance().getCurrentCautionData() not exist";
            this.INDEX_NOT_EXIST = "onNotify called and caution not exist  CautionUtilityClass.getInstance().getCurrentCautionData().maxPriorityId index  not in range";
            this.tags = new String[]{MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE};
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            DataBaseAdapter.getInstance().setDBCorruptStatus(false);
            int caution_ID = 0;
            try {
                caution_ID = CautionUtilityClass.getInstance().getCurrentCautionData().maxPriorityId;
            } catch (IndexOutOfBoundsException e) {
                Log.i(STEEState.TAG, "onNotify called and caution not exist  CautionUtilityClass.getInstance().getCurrentCautionData().maxPriorityId index  not in range");
            } catch (NullPointerException e2) {
                Log.i(STEEState.TAG, "onNotify called and caution CautionUtilityClass.getInstance().getCurrentCautionData() not exist");
            }
            switch (caution_ID) {
                case 3289:
                    CautionUtilityClass.getInstance().executeTerminate();
                    break;
                case STInfo.CAUTION_ID_DLAPP_NO_MEMEORY_CARD_SHOOTING /* 131204 */:
                case STInfo.CAUTION_ID_DLAPP_REMAINING_MEMORY_LESS /* 131205 */:
                case STInfo.CAUTION_ID_DLAPP_MEMORY_CARD_FULL /* 131207 */:
                case STInfo.CAUTION_ID_DLAPP_SAME_FILE_EXIST_RETRY /* 131208 */:
                case STInfo.CAUTION_ID_DLAPP_DB_FULL /* 131209 */:
                case STInfo.CAUTION_ID_DLAPP_DB_ERROR /* 131210 */:
                    int state = MediaNotificationManager.getInstance().getMediaState();
                    if (2 == state) {
                        STEEState.disapearDisplayingCaution(STInfo.CAUTION_ID_DLAPP_NO_MEMEORY_CARD_SHOOTING);
                    }
                    STEEState.disapearDisplayingCaution(caution_ID);
                    break;
            }
            DatabaseUtil.DbResult result = DataBaseOperations.getInstance().importDatabase();
            if (result == DatabaseUtil.DbResult.DB_ERROR) {
                DataBaseAdapter.getInstance().setDBCorruptStatus(true);
                STEEState.displayDBErrorCaution();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void disapearDisplayingCaution(int caution_ID) {
        CautionUtilityClass.getInstance().disapperTrigger(caution_ID);
    }
}
