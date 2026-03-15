package com.sony.imaging.app.bracketpro.shooting.state;

import com.sony.imaging.app.base.shooting.S1OnEEState;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.bracketpro.commonUtil.BracketMasterBackUpKey;
import com.sony.imaging.app.bracketpro.menu.controller.BMMenuController;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class BMS1OnEEState extends S1OnEEState {
    private static final String[] TAGS = {CameraNotificationManager.DEVICE_LENS_CHANGED, CameraNotificationManager.DEVICE_LENS_AFMF_SW_CHANGED, CameraNotificationManager.FOCUS_CHANGE};
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
            return BMS1OnEEState.TAGS;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            String currentBracket = BackUpUtil.getInstance().getPreferenceString(BracketMasterBackUpKey.TAG_CURRENT_BRACKET_TYPE, BMMenuController.FocusBracket);
            if (currentBracket.equalsIgnoreCase(BMMenuController.FocusBracket)) {
                if (CameraNotificationManager.DEVICE_LENS_CHANGED.equals(tag) || CameraNotificationManager.DEVICE_LENS_AFMF_SW_CHANGED.equals(tag) || CameraNotificationManager.FOCUS_CHANGE.equals(tag)) {
                    BMS1OnEEState.this.setNextState(BMS1OnEEState.this.getLensAssistStateName(), null);
                }
            }
        }
    }

    @Override // com.sony.imaging.app.base.shooting.S1OnEEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        this.mLensListener = new LensAssistListener();
        if (this.mLensListener != null) {
            CameraNotificationManager.getInstance().setNotificationListener(this.mLensListener, true);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.S1OnEEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        if (this.mLensListener != null) {
            CameraNotificationManager.getInstance().removeNotificationListener(this.mLensListener);
            this.mLensListener = null;
        }
        super.onPause();
    }
}
