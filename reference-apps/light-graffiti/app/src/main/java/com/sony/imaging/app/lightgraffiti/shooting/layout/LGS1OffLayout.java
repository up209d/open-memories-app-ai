package com.sony.imaging.app.lightgraffiti.shooting.layout;

import com.sony.imaging.app.base.shooting.layout.S1OffLayout;
import com.sony.imaging.app.lightgraffiti.R;
import com.sony.imaging.app.util.Environment;

/* loaded from: classes.dex */
public class LGS1OffLayout extends S1OffLayout {
    @Override // com.sony.imaging.app.base.shooting.layout.S1OffLayout
    protected int getLayout(int device, int dispmode) {
        if (1 == device || 1 != Environment.getVersionOfHW() || 1 == dispmode) {
            return -1;
        }
        return R.layout.shooting_main_skguide_dispoff_for_eview;
    }
}
