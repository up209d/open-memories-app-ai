package com.sony.imaging.app.manuallenscompensation.shooting.state;

import com.sony.imaging.app.base.shooting.S1OnEEState;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.FocusMagnificationController;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCConstants;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCUtil;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class OCS1OnEEState extends S1OnEEState {
    private static final String[] TAGS = {CameraNotificationManager.FOCUS_MAGNIFICATION_CHANGED, CameraNotificationManager.FOCUS_RING_ROTATION_STARTED};

    @Override // com.sony.imaging.app.base.shooting.S1OnEEState
    protected NotificationListener getListener() {
        if (FocusMagnificationController.isSupportedByPf()) {
            return new MLCMfAssistListener();
        }
        return null;
    }

    /* loaded from: classes.dex */
    protected class MLCMfAssistListener implements NotificationListener {
        protected MLCMfAssistListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return OCS1OnEEState.TAGS;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (CameraNotificationManager.FOCUS_RING_ROTATION_STARTED.equals(tag)) {
                OCUtil.getInstance().openMFAssitState(OCConstants.OC_S1ON_EE_STATE);
            } else if (CameraNotificationManager.FOCUS_MAGNIFICATION_CHANGED.equals(tag) && FocusMagnificationController.getInstance().isMagnifying()) {
                OCS1OnEEState.this.setNextState("MfAssist", null);
            }
        }
    }
}
