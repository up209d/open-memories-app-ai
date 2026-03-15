package com.sony.imaging.app.lightshaft.shooting.layout;

import com.sony.imaging.app.base.shooting.layout.S1OffLayout;
import com.sony.imaging.app.lightshaft.R;
import com.sony.imaging.app.util.Environment;

/* loaded from: classes.dex */
public class LSS1OffLayout extends S1OffLayout {
    @Override // com.sony.imaging.app.base.shooting.layout.S1OffLayout
    protected int getLayout(int device, int dispmode) {
        if (1 == device) {
            return -1;
        }
        if (1 == Environment.getVersionOfHW()) {
            if (1 == dispmode) {
                return R.layout.ls_emount_shooting_main_skguide_dispon_for_eview;
            }
            return R.layout.shooting_main_skguide_dispoff_for_eview;
        }
        if (1 != dispmode) {
            return -1;
        }
        return R.layout.ls_pone_shooting_main_skguide_dispon_for_eview;
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void onReopened() {
        super.onReopened();
        updateView();
    }
}
