package com.sony.imaging.app.base.shooting.movie.trigger;

import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.fw.BaseInvalidKeyHandler;
import com.sony.imaging.app.fw.ICustomKey;

/* loaded from: classes.dex */
public class MovieSaveStateKeyHandler extends BaseInvalidKeyHandler {
    @Override // com.sony.imaging.app.fw.BaseKeyHandler
    protected String getKeyConvCategory() {
        ExposureModeController cntl = ExposureModeController.getInstance();
        String expMode = cntl.getValue(null);
        if (ExposureModeController.MOVIE_PROGRAM_AUTO_MODE.equals(expMode)) {
            return ICustomKey.CATEGORY_MOVIE_P;
        }
        if (ExposureModeController.MOVIE_APERATURE_MODE.equals(expMode)) {
            return ICustomKey.CATEGORY_MOVIE_A;
        }
        if (ExposureModeController.MOVIE_SHUTTER_MODE.equals(expMode)) {
            return ICustomKey.CATEGORY_MOVIE_S;
        }
        if (ExposureModeController.MOVIE_MANUAL_MODE.equals(expMode)) {
            return ICustomKey.CATEGORY_MOVIE_M;
        }
        return ICustomKey.CATEGORY_MOVIE_OTHER;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedS1Key() {
        return 0;
    }
}
