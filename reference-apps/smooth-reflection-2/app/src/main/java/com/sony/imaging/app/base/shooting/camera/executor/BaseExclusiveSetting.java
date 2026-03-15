package com.sony.imaging.app.base.shooting.camera.executor;

import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.DigitalZoomController;

/* loaded from: classes.dex */
public class BaseExclusiveSetting {
    private CameraSetting mCamSet;

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setCameraSetting(CameraSetting camSet) {
        this.mCamSet = camSet;
    }

    protected CameraSetting getCameraSetting() {
        return this.mCamSet;
    }

    public void executeExlusiveSetting(boolean isSpecialSeq) {
        executeDigitalZoomExclusiveSetting(isSpecialSeq);
    }

    private final void executeDigitalZoomExclusiveSetting(boolean isSpecialSeq) {
        DigitalZoomController.getInstance().setDigitalZoomOnSpecialSeq(isSpecialSeq);
    }
}
