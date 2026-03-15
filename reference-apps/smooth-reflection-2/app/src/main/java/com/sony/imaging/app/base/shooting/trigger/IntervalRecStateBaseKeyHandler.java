package com.sony.imaging.app.base.shooting.trigger;

import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.fw.BaseInvalidKeyHandler;
import com.sony.imaging.app.fw.ICustomKey;

/* loaded from: classes.dex */
public class IntervalRecStateBaseKeyHandler extends BaseInvalidKeyHandler {
    @Override // com.sony.imaging.app.fw.BaseKeyHandler
    protected String getKeyConvCategory() {
        ExposureModeController cntl = ExposureModeController.getInstance();
        String expMode = cntl.getValue(null);
        if (ExposureModeController.PROGRAM_AUTO_MODE.equals(expMode) || ExposureModeController.MOVIE_PROGRAM_AUTO_MODE.equals(expMode)) {
            return ICustomKey.CATEGORY_SHOOTING_P;
        }
        if ("Aperture".equals(expMode) || ExposureModeController.MOVIE_APERATURE_MODE.equals(expMode)) {
            return ICustomKey.CATEGORY_SHOOTING_A;
        }
        if ("Shutter".equals(expMode) || ExposureModeController.MOVIE_SHUTTER_MODE.equals(expMode)) {
            return ICustomKey.CATEGORY_SHOOTING_S;
        }
        if (ExposureModeController.MANUAL_MODE.equals(expMode) || ExposureModeController.MOVIE_MANUAL_MODE.equals(expMode)) {
            return ICustomKey.CATEGORY_SHOOTING_M;
        }
        return ICustomKey.CATEGORY_SHOOTING_OTHER;
    }
}
