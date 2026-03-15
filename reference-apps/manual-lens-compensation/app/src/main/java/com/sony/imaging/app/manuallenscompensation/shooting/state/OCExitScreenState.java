package com.sony.imaging.app.manuallenscompensation.shooting.state;

import com.sony.imaging.app.base.common.ExitScreenState;
import com.sony.imaging.app.manuallenscompensation.commonUtil.AppLog;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCUtil;

/* loaded from: classes.dex */
public class OCExitScreenState extends ExitScreenState {
    private String TAG = "OCExitScreenState";

    @Override // com.sony.imaging.app.base.common.ExitScreenState, com.sony.imaging.app.base.common.layout.ExitScreenLayout.Callback
    public void onFinish() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        OCUtil.getInstance().setExitExecute(true);
        super.onFinish();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.common.ExitScreenState, com.sony.imaging.app.base.common.layout.ExitScreenLayout.Callback
    public void onClose() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        OCUtil.getInstance().setExitExecute(false);
        super.onClose();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }
}
