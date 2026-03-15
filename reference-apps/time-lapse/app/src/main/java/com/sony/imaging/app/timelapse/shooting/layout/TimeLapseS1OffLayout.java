package com.sony.imaging.app.timelapse.shooting.layout;

import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.shooting.layout.S1OffLayout;
import com.sony.imaging.app.timelapse.R;
import com.sony.imaging.app.timelapse.shooting.base.TimeLapseDisplayModeObserver;
import com.sony.imaging.app.timelapse.shooting.controller.TestShotController;
import com.sony.imaging.app.util.Environment;

/* loaded from: classes.dex */
public class TimeLapseS1OffLayout extends S1OffLayout {
    private static String TAG = "TimeLapseS1OffLayout";

    @Override // com.sony.imaging.app.base.shooting.layout.S1OffLayout
    protected int getLayout(int device, int dispmode) {
        int dispmode2 = TimeLapseDisplayModeObserver.getInstance().getActiveDispMode(0);
        if (1 == device) {
            return -1;
        }
        if (1 == Environment.getVersionOfHW()) {
            if (11 == dispmode2) {
                return R.layout.tl_shooting_main_skguide_dispon_emount;
            }
            return R.layout.shooting_main_skguide_dispoff_for_eview;
        }
        if (11 != dispmode2 || !TestShotController.TESTSHOT_ON.equalsIgnoreCase(TestShotController.getInstance().getValue(TestShotController.TESTSHOT_ASSIGN_KEY))) {
            return -1;
        }
        return R.layout.tl_shooting_main_skguide_dispon_pone;
    }

    @Override // com.sony.imaging.app.base.shooting.layout.S1OffLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        MediaNotificationManager.getInstance().notifyRemainingAmountChange();
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void onReopened() {
        super.onReopened();
        updateView();
    }
}
