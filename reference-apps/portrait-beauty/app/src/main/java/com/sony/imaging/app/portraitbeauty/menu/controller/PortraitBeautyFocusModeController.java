package com.sony.imaging.app.portraitbeauty.menu.controller;

import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.portraitbeauty.common.AppLog;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyBackUpKey;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class PortraitBeautyFocusModeController extends FocusModeController {
    private static final String TAG = AppLog.getClassName();
    private static PortraitBeautyFocusModeController sInstance = null;

    private PortraitBeautyFocusModeController() {
    }

    public static PortraitBeautyFocusModeController getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (sInstance == null) {
            sInstance = new PortraitBeautyFocusModeController();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return sInstance;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.FocusModeController
    public List<String> getAvailableValue() {
        new ArrayList();
        List<String> ret = super.getAvailableValue();
        String selfTimer = BackUpUtil.getInstance().getPreferenceString(PortraitBeautyBackUpKey.KEY_SELECTED_SELF_TIMER, SelfTimerIntervalPriorityController.SELFTIMERON5);
        System.out.println("self timer is : " + selfTimer);
        if (!SelfTimerIntervalPriorityController.SELFTIMEROFF.equals(selfTimer) && ret != null && ret.contains("af-c")) {
            ret.remove("af-c");
        }
        return ret;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.FocusModeController, com.sony.imaging.app.base.shooting.camera.ShootingModeController, com.sony.imaging.app.base.shooting.camera.AbstractController, com.sony.imaging.app.base.menu.IController
    public int getCautionIndex(String itemId) {
        String selfTimer = BackUpUtil.getInstance().getPreferenceString(PortraitBeautyBackUpKey.KEY_SELECTED_SELF_TIMER, SelfTimerIntervalPriorityController.SELFTIMERON5);
        if (SelfTimerIntervalPriorityController.SELFTIMEROFF.equals(selfTimer)) {
            return 0;
        }
        return super.getCautionIndex(itemId);
    }
}
