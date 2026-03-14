package com.sony.imaging.app.doubleexposure.shooting.state;

import com.sony.imaging.app.base.shooting.MfAssistState;
import com.sony.imaging.app.doubleexposure.common.AppContext;
import com.sony.imaging.app.doubleexposure.common.AppLog;
import com.sony.imaging.app.doubleexposure.common.DESA;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureConstant;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureUtil;

/* loaded from: classes.dex */
public class DoubleExposureMfAssistState extends MfAssistState {
    private final String TAG = AppLog.getClassName();
    private DESA mDESA = null;

    @Override // com.sony.imaging.app.base.shooting.MfAssistState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        String currentShootingScreen = DoubleExposureUtil.getInstance().getCurrentShootingScreen();
        if (DoubleExposureConstant.SECOND_SHOOTING.equals(currentShootingScreen)) {
            this.mDESA = DESA.getInstance();
            this.mDESA.terminate();
        }
        super.onResume();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onDestroy();
        String currentShootingScreen = DoubleExposureUtil.getInstance().getCurrentShootingScreen();
        if (DoubleExposureConstant.SECOND_SHOOTING.equals(currentShootingScreen) && this.mDESA != null) {
            this.mDESA.setPackageName(AppContext.getAppContext().getPackageName());
            this.mDESA.intialize();
            this.mDESA.setSFRMode(1);
            this.mDESA.startLiveViewEffect();
            this.mDESA = null;
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }
}
