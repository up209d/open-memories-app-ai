package com.sony.imaging.app.smoothreflection.shooting.state;

import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.shooting.NormalCaptureState;
import com.sony.imaging.app.base.shooting.StateBase;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.smoothreflection.caution.SmoothReflectionInfo;
import com.sony.imaging.app.smoothreflection.common.AppLog;
import com.sony.imaging.app.smoothreflection.shooting.SmoothReflectionCompositProcess;

/* loaded from: classes.dex */
public class SmoothReflectionNormalCaptureState extends NormalCaptureState {
    private final String TAG = AppLog.getClassName();
    private int mCautionId = 0;
    private final String NEXT_EE_STATE = "EE";

    @Override // com.sony.imaging.app.base.shooting.NormalCaptureState, com.sony.imaging.app.base.shooting.CaptureStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        getCautionId();
        if (this.mCautionId != 0) {
            CautionUtilityClass.getInstance().requestTrigger(this.mCautionId);
            setNextState("EE", null);
        } else {
            super.onResume();
            openLayout(StateBase.DEFAULT_LAYOUT);
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.CaptureStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onPause();
        CameraNotificationManager.OnFocusInfo onFocusInfo = new CameraNotificationManager.OnFocusInfo(0, null);
        CameraNotificationManager.getInstance().requestSyncNotify(CameraNotificationManager.DONE_AUTO_FOCUS, onFocusInfo);
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
                this.mCautionId = SmoothReflectionInfo.CAUTION_ID_DLAPP_NO_CARD_ON_SHOOTING;
            } else if (1 > SmoothReflectionCompositProcess.mRemaining) {
                AppLog.info(this.TAG, "No Space in Memory");
                this.mCautionId = SmoothReflectionInfo.CAUTION_ID_DLAPP_NO_STORAGE_SPACE;
            } else {
                AppLog.info(this.TAG, "Space in Memory");
            }
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.CaptureStateBase
    public boolean isTakePictureByRemote() {
        return false;
    }
}
