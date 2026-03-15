package com.sony.imaging.app.bracketpro.shooting.state;

import android.view.KeyEvent;
import com.sony.imaging.app.base.common.FocusModeDialDetector;
import com.sony.imaging.app.base.shooting.S1OffEEState;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.bracketpro.commonUtil.BracketMasterBackUpKey;
import com.sony.imaging.app.bracketpro.menu.controller.BMMenuController;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class BMS1OffEEState extends S1OffEEState {
    private static final String[] TAGS = {CameraNotificationManager.DEVICE_LENS_CHANGED, CameraNotificationManager.FOCUS_CHANGE, CameraNotificationManager.DEVICE_LENS_AFMF_SW_CHANGED};
    protected NotificationListener mLensListener;

    protected String getLensAssistStateName() {
        return BMEEState.NEXT_STATE_CHILD_LENS_CAUTION_STATE;
    }

    /* loaded from: classes.dex */
    protected class LensAssistListener implements NotificationListener {
        protected LensAssistListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return BMS1OffEEState.TAGS;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            String currentBracket = BackUpUtil.getInstance().getPreferenceString(BracketMasterBackUpKey.TAG_CURRENT_BRACKET_TYPE, BMMenuController.FocusBracket);
            if (currentBracket.equalsIgnoreCase(BMMenuController.FocusBracket)) {
                if (CameraNotificationManager.DEVICE_LENS_CHANGED.equals(tag) || CameraNotificationManager.DEVICE_LENS_AFMF_SW_CHANGED.equals(tag) || CameraNotificationManager.FOCUS_CHANGE.equals(tag)) {
                    if (FocusModeDialDetector.hasFocusModeDial() && !FocusModeController.getInstance().getValue().equalsIgnoreCase("af-s")) {
                        FocusModeController.getInstance().setValue("af-s");
                    } else {
                        BMS1OffEEState.this.setNextState(BMS1OffEEState.this.getLensAssistStateName(), null);
                    }
                }
            }
        }
    }

    @Override // com.sony.imaging.app.base.shooting.S1OffEEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        this.mLensListener = new LensAssistListener();
        if (this.mLensListener != null) {
            CameraNotificationManager.getInstance().setNotificationListener(this.mLensListener, true);
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        super.onDestroy();
    }

    @Override // com.sony.imaging.app.base.shooting.S1OffEEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        if (this.mLensListener != null) {
            CameraNotificationManager.getInstance().removeNotificationListener(this.mLensListener);
            this.mLensListener = null;
        }
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onAfMfSlideKeyMoved(KeyEvent event) {
        String currentBracket = BackUpUtil.getInstance().getPreferenceString(BracketMasterBackUpKey.TAG_CURRENT_BRACKET_TYPE, BMMenuController.FocusBracket);
        if (FocusModeDialDetector.hasFocusModeDial() || !currentBracket.equalsIgnoreCase(BMMenuController.FocusBracket)) {
            return super.onAfMfSlideKeyMoved(event);
        }
        setNextState(getLensAssistStateName(), null);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onLensAttached(KeyEvent event) {
        String currentBracket = BackUpUtil.getInstance().getPreferenceString(BracketMasterBackUpKey.TAG_CURRENT_BRACKET_TYPE, BMMenuController.FocusBracket);
        if (FocusModeDialDetector.hasFocusModeDial() || !currentBracket.equalsIgnoreCase(BMMenuController.FocusBracket)) {
            return super.onAfMfSlideKeyMoved(event);
        }
        setNextState(getLensAssistStateName(), null);
        return 1;
    }
}
