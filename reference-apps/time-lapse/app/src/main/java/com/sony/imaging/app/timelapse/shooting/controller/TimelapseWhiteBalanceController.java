package com.sony.imaging.app.timelapse.shooting.controller;

import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;

/* loaded from: classes.dex */
public class TimelapseWhiteBalanceController extends WhiteBalanceController {
    private static TimelapseWhiteBalanceController mInstance = new TimelapseWhiteBalanceController();

    public static TimelapseWhiteBalanceController getInstance() {
        return mInstance;
    }

    public void setTimelapseValue(String itemId, String value) {
        TLCommonUtil util = TLCommonUtil.getInstance();
        int theme = util.getCurrentState();
        setValue(itemId, value);
        if (7 != theme) {
            WhiteBalanceController.WhiteBalanceParam param = (WhiteBalanceController.WhiteBalanceParam) super.getDetailValue();
            param.setColorComp(0);
            param.setLightBalance(0);
            param.setColorTemp(0);
            if ("color-temp".equals(value) || WhiteBalanceController.CUSTOM.equals(value)) {
                param.setColorTemp(WhiteBalanceController.DEF_TEMP);
            }
            super.setDetailValue(param);
        }
    }
}
