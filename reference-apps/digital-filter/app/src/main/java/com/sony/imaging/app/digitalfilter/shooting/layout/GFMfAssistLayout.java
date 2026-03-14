package com.sony.imaging.app.digitalfilter.shooting.layout;

import com.sony.imaging.app.base.shooting.layout.MfAssistLayout;

/* loaded from: classes.dex */
public class GFMfAssistLayout extends MfAssistLayout {
    public static boolean isOpen = false;

    @Override // com.sony.imaging.app.base.shooting.layout.MfAssistLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        isOpen = true;
        super.onResume();
    }

    @Override // com.sony.imaging.app.base.shooting.layout.MfAssistLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        isOpen = false;
        super.onPause();
    }
}
