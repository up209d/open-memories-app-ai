package com.sony.imaging.app.graduatedfilter.menu.layout;

import com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.graduatedfilter.GFBackUpKey;
import com.sony.imaging.app.graduatedfilter.common.AppLog;
import com.sony.imaging.app.graduatedfilter.common.GFCommonUtil;
import com.sony.imaging.app.graduatedfilter.common.GFConstants;
import com.sony.imaging.app.graduatedfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.graduatedfilter.shooting.camera.GFExposureModeController;
import com.sony.imaging.app.util.BackUpUtil;

/* loaded from: classes.dex */
public class GFExposureModeMenuLayout extends ExposureModeMenuLayout {
    private static final String TAG = AppLog.getClassName();

    @Override // com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout, com.sony.imaging.app.base.menu.layout.SubMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        params.setExpMode(GFExposureModeController.getInstance().getValue(ExposureModeController.EXPOSURE_MODE));
        super.closeLayout();
        boolean isShownIntroGuide = BackUpUtil.getInstance().getPreferenceBoolean(GFBackUpKey.KEY_SHOW_INTRODUCTION, false);
        boolean isShown1stStepGuide = BackUpUtil.getInstance().getPreferenceBoolean(GFBackUpKey.KEY_SHOW_INTRODUCTION, false);
        if (isShownIntroGuide && !isShown1stStepGuide) {
            CameraNotificationManager.getInstance().requestNotify(GFConstants.SHOW_STEP1_GUIDE);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout, com.sony.imaging.app.base.menu.layout.SubMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        if (!GFCommonUtil.getInstance().isFilterSetting()) {
            return super.pushedMenuKey();
        }
        openPreviousMenu();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS1Key() {
        GFCommonUtil.getInstance().setInvalidShutter(true);
        return super.pushedS1Key();
    }
}
