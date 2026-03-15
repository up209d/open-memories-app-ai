package com.sony.imaging.app.startrails.shooting.layout;

import com.sony.imaging.app.base.shooting.layout.MfAssistLayout;
import com.sony.imaging.app.startrails.R;
import com.sony.imaging.app.util.Environment;

/* loaded from: classes.dex */
public class STMFassistLayout extends MfAssistLayout {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.layout.MfAssistLayout
    public int getLayout(int device) {
        if (device == 0 || device == 2) {
            if (Environment.getVersionOfHW() == 1) {
                return R.layout.shooting_main_sid_mf_assist_panel_emount;
            }
            return R.layout.shooting_main_sid_mf_assist_panel;
        }
        int layout = super.getLayout(device);
        return layout;
    }
}
