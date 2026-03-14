package com.sony.imaging.app.doubleexposure.shooting.layout;

import com.sony.imaging.app.base.shooting.layout.S1OffLayout;
import com.sony.imaging.app.doubleexposure.R;
import com.sony.imaging.app.doubleexposure.common.AppLog;
import com.sony.imaging.app.util.Environment;

/* loaded from: classes.dex */
public class DoubleExposureS1OffLayout extends S1OffLayout {
    private final String TAG = AppLog.getClassName();

    @Override // com.sony.imaging.app.base.shooting.layout.S1OffLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onResume();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.layout.S1OffLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onPause();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onDestroy();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.layout.S1OffLayout
    protected int getLayout(int device, int dispmode) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        int layoutid = -1;
        if (1 != device) {
            if (1 == Environment.getVersionOfHW()) {
                layoutid = 1 == dispmode ? R.layout.double_exposure_emount_shooting_main_skguide_dispon_for_eview : R.layout.shooting_main_skguide_dispoff_for_eview;
            } else if (1 == dispmode) {
                layoutid = R.layout.double_exposure_projectone_shooting_main_skguide_dispon_for_eview;
            }
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return layoutid;
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void onReopened() {
        super.onReopened();
        updateView();
    }
}
