package com.sony.imaging.app.timelapse.shooting.layout;

import com.sony.imaging.app.base.shooting.layout.MfAssistLayout;
import com.sony.imaging.app.timelapse.R;
import com.sony.imaging.app.timelapse.shooting.controller.TestShotController;
import com.sony.imaging.app.util.Environment;

/* loaded from: classes.dex */
public class TimeLapseMfAssistLayout extends MfAssistLayout {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.layout.MfAssistLayout
    public int getLayout(int device) {
        if (device == 0 || device == 2) {
            if (Environment.getVersionOfHW() == 1) {
                return R.layout.shooting_main_sid_mf_assist_panel_emount;
            }
            if (TestShotController.TESTSHOT_ON.equalsIgnoreCase(TestShotController.getInstance().getValue(TestShotController.TESTSHOT_ASSIGN_KEY))) {
                return R.layout.tl_shooting_main_sid_mf_assist_panel;
            }
            int layout = super.getLayout(device);
            return layout;
        }
        int layout2 = super.getLayout(device);
        return layout2;
    }
}
