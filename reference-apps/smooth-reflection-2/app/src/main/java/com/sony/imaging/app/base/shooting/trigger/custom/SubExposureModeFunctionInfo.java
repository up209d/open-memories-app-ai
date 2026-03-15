package com.sony.imaging.app.base.shooting.trigger.custom;

import com.sony.imaging.app.base.shooting.camera.ExposureModeController;

/* loaded from: classes.dex */
public class SubExposureModeFunctionInfo extends FunctionInfo {
    public SubExposureModeFunctionInfo(int imageid, String itemid, Class<?> cntlClass) {
        super(imageid, itemid, cntlClass);
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.custom.FunctionInfo, com.sony.imaging.app.fw.IFunctionTable.IFunctionInfo
    public String getItemId() {
        return ExposureModeController.EXPOSURE_MODE;
    }
}
