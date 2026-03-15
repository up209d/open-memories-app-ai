package com.sony.imaging.app.manuallenscompensation.shooting.layout;

import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.layout.S1OffLayout;
import com.sony.imaging.app.manuallenscompensation.R;
import com.sony.imaging.app.manuallenscompensation.commonUtil.AppLog;
import com.sony.imaging.app.manuallenscompensation.shooting.controller.DeleteFocusMagnifierController;
import com.sony.imaging.app.util.Environment;

/* loaded from: classes.dex */
public class OCS1OffLayout extends S1OffLayout {
    private static final String TAG = "OCS1OffLayout";

    @Override // com.sony.imaging.app.base.shooting.layout.S1OffLayout
    protected int getLayout(int device, int dispmode) {
        if (1 == device) {
            return -1;
        }
        if (1 == Environment.getVersionOfHW()) {
            int ret = initializeMFAssistViewForEMount(dispmode);
            return ret;
        }
        DeleteFocusMagnifierController controller = DeleteFocusMagnifierController.getInstance();
        String currentSetting = DeleteFocusMagnifierController.FOCUS_MAGNIFIER_OFF;
        try {
            currentSetting = controller.getValue(DeleteFocusMagnifierController.FOCUS_MAGNIFIER_SWITCH_IC);
        } catch (IController.NotSupportedException e) {
            AppLog.error(TAG, "Exception occuered :" + e.getMessage());
        }
        if (currentSetting == null || !currentSetting.equals(DeleteFocusMagnifierController.FOCUS_MAGNIFIER_ON)) {
            return -1;
        }
        int ret2 = initializeMFAssistViewForPone(dispmode);
        return ret2;
    }

    private int initializeMFAssistViewForEMount(int dispmode) {
        if (1 == dispmode) {
            return R.layout.lc_emount_shooting_main_skguide_dispon_for_eview;
        }
        return R.layout.shooting_main_skguide_dispoff_for_eview;
    }

    private int initializeMFAssistViewForPone(int dispmode) {
        if (1 != dispmode) {
            return -1;
        }
        return R.layout.lc_pone_shooting_main_skguide_dispon_for_eview;
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void onReopened() {
        super.onReopened();
        updateView();
    }
}
