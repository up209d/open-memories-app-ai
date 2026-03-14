package com.sony.imaging.app.smoothreflection.shooting.state;

import com.sony.imaging.app.base.shooting.FocusAdjustmentState;
import com.sony.imaging.app.smoothreflection.common.AppLog;

/* loaded from: classes.dex */
public class SmoothReflectionFocusAdjustmentState extends FocusAdjustmentState {
    private final String TAG = AppLog.getClassName();

    @Override // com.sony.imaging.app.base.shooting.FocusAdjustmentState, com.sony.imaging.app.base.shooting.MfAssistState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
        super.onResume();
    }

    @Override // com.sony.imaging.app.base.shooting.FocusAdjustmentState, com.sony.imaging.app.base.shooting.MfAssistState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onPause();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
        super.onDestroy();
    }
}
