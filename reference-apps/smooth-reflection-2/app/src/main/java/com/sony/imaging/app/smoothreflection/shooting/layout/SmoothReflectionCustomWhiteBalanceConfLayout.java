package com.sony.imaging.app.smoothreflection.shooting.layout;

import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.base.shooting.layout.CustomWhiteBalanceConfLayout;
import com.sony.imaging.app.smoothreflection.shooting.camera.SmoothReflectionWhiteBalanceController;

/* loaded from: classes.dex */
public class SmoothReflectionCustomWhiteBalanceConfLayout extends CustomWhiteBalanceConfLayout {
    @Override // com.sony.imaging.app.base.shooting.layout.CustomWhiteBalanceConfLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        String value = SmoothReflectionWhiteBalanceController.getInstance().getValue();
        SmoothReflectionWhiteBalanceController.getInstance().setValue(WhiteBalanceController.WHITEBALANCE, value);
    }
}
