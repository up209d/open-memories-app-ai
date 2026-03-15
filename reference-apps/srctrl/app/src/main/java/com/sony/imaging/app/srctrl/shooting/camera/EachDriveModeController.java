package com.sony.imaging.app.srctrl.shooting.camera;

import com.sony.imaging.app.base.shooting.camera.DriveModeController;
import com.sony.imaging.app.srctrl.util.SRCtrlEnvironment;

/* loaded from: classes.dex */
public class EachDriveModeController extends DriveModeController {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.DriveModeController
    public boolean isSupported(String value, int mode) {
        if (((SRCtrlEnvironment.getInstance().isSystemApp() || !SRCtrlEnvironment.getInstance().isEnableContinuousShooting()) && (DriveModeController.BURST.equals(value) || DriveModeController.BURST_SPEED_HIGH.equals(value) || DriveModeController.BURST_SPEED_MID.equals(value) || "low".equals(value) || DriveModeController.SPEED_PRIORITY_BURST.equals(value))) || DriveModeController.SELF_TIMER_BURST.equals(value) || DriveModeController.SELF_TIMER_BURST_10S_3SHOT.equals(value) || DriveModeController.SELF_TIMER_BURST_10S_5SHOT.equals(value) || DriveModeController.BRACKET.equals(value)) {
            return false;
        }
        return super.isSupported(value, mode);
    }
}
