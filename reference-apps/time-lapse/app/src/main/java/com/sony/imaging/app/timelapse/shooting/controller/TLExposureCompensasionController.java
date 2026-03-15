package com.sony.imaging.app.timelapse.shooting.controller;

import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;

/* loaded from: classes.dex */
public class TLExposureCompensasionController extends ExposureCompensationController {
    private static TLExposureCompensasionController mInstance = new TLExposureCompensasionController();

    public static TLExposureCompensasionController getInstance() {
        return mInstance;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.ExposureCompensationController
    public boolean isModeDialAvailable() {
        if (TLCommonUtil.getInstance().getCurrentState() != 7) {
            return true;
        }
        boolean isModeDialAvailable = super.isModeDialAvailable();
        return isModeDialAvailable;
    }
}
