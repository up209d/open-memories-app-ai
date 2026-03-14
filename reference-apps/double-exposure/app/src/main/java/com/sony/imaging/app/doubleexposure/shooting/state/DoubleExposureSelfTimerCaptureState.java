package com.sony.imaging.app.doubleexposure.shooting.state;

import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.shooting.SelfTimerCaptureState;
import com.sony.imaging.app.doubleexposure.caution.DoubleExposureInfo;
import com.sony.imaging.app.doubleexposure.common.AppLog;

/* loaded from: classes.dex */
public class DoubleExposureSelfTimerCaptureState extends SelfTimerCaptureState {
    private final String TAG = AppLog.getClassName();
    private int mCautionId = 0;
    private final String NEXT_EE_STATE = "EE";

    @Override // com.sony.imaging.app.base.shooting.SelfTimerCaptureState, com.sony.imaging.app.base.shooting.CaptureStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        getCautionId();
        if (this.mCautionId != 0) {
            AppLog.checkIf(this.TAG, " mCautionId = " + this.mCautionId);
            CautionUtilityClass.getInstance().requestTrigger(this.mCautionId);
            setNextState("EE", null);
        } else {
            super.onResume();
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.SelfTimerCaptureState, com.sony.imaging.app.base.shooting.CaptureStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (this.mCautionId != 0) {
            AppLog.checkIf(this.TAG, " mCautionId = " + this.mCautionId);
        } else {
            super.onPause();
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mCautionId = 0;
        super.onDestroy();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    private void getCautionId() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mCautionId = 0;
        if (!MediaNotificationManager.getInstance().isError()) {
            if (!MediaNotificationManager.getInstance().isMounted()) {
                AppLog.info(this.TAG, "No Memory Card.");
                this.mCautionId = DoubleExposureInfo.CAUTION_ID_DLAPP_NO_CARD_ON_SHOOTING;
            } else if (MediaNotificationManager.getInstance().isMounted() && 1 == MediaNotificationManager.getInstance().getRemaining()) {
                AppLog.info(this.TAG, "No Space in Memory");
                this.mCautionId = DoubleExposureInfo.CAUTION_ID_DLAPP_NO_STORAGE_SPACE;
            }
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }
}
