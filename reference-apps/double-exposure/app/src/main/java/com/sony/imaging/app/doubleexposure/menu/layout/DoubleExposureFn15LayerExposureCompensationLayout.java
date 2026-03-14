package com.sony.imaging.app.doubleexposure.menu.layout;

import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.menu.layout.Fn15LayerExposureCompensationLayout;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.doubleexposure.common.AppLog;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureConstant;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureUtil;

/* loaded from: classes.dex */
public class DoubleExposureFn15LayerExposureCompensationLayout extends Fn15LayerExposureCompensationLayout {
    public static final String ID_DOUBLEEXPOSUREFN15LAYEREXPOSURECOMPENSATIONLAYOUT = "ID_DOUBLEEXPOSUREFN15LAYEREXPOSURECOMPENSATIONLAYOUT";
    private final String TAG = AppLog.getClassName();
    private DoubleExposureUtil mDoubleExposureUtil = null;

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerCompensationLayout, com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mDoubleExposureUtil = DoubleExposureUtil.getInstance();
        AppLog.exit(this.TAG, AppLog.getMethodName());
        super.onResume();
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerCompensationLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onDestroyView();
        String exposureCompensation = null;
        try {
            exposureCompensation = ExposureCompensationController.getInstance().getValue("ExposureCompensation");
        } catch (IController.NotSupportedException e) {
            AppLog.error(this.TAG, e.toString());
        }
        if (DoubleExposureConstant.FIRST_SHOOTING.equals(this.mDoubleExposureUtil.getCurrentShootingScreen())) {
            this.mDoubleExposureUtil.setExposureCompensationFirstShooting(exposureCompensation);
        } else {
            this.mDoubleExposureUtil.setExposureCompensationSecondShooting(exposureCompensation);
        }
        this.mDoubleExposureUtil = null;
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }
}
