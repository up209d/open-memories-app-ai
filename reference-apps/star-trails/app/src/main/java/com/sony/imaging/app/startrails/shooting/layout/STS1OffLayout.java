package com.sony.imaging.app.startrails.shooting.layout;

import com.sony.imaging.app.base.shooting.layout.S1OffLayout;
import com.sony.imaging.app.startrails.R;
import com.sony.imaging.app.startrails.common.STDisplayModeObserver;
import com.sony.imaging.app.startrails.util.AppLog;
import com.sony.imaging.app.util.Environment;

/* loaded from: classes.dex */
public class STS1OffLayout extends S1OffLayout {
    private static String TAG = "STS1OffLayout";

    @Override // com.sony.imaging.app.base.shooting.layout.S1OffLayout
    protected int getLayout(int device, int dispmode) {
        AppLog.enter(TAG, AppLog.getMethodName());
        int dispmode2 = STDisplayModeObserver.getInstance().getActiveDispMode(0);
        int layoutid = -1;
        if (1 != device) {
            if (1 == Environment.getVersionOfHW()) {
                layoutid = 11 == dispmode2 ? R.layout.st_emount_shooting_main_skguide_dispon_for_eview : R.layout.shooting_main_skguide_dispoff_for_eview;
            } else if (11 == dispmode2) {
                layoutid = R.layout.st_pone_shooting_main_skguide_dispon_for_eview;
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return layoutid;
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void onReopened() {
        super.onReopened();
        updateView();
    }
}
