package com.sony.imaging.app.base.caution;

import com.sony.imaging.app.fw.BaseInvalidKeyHandler;

/* loaded from: classes.dex */
public abstract class IKeyDispatch extends BaseInvalidKeyHandler {
    protected CautionProcessingFunction cautionfunc;

    /* JADX INFO: Access modifiers changed from: package-private */
    public IKeyDispatch(CautionProcessingFunction p) {
        this.cautionfunc = p;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public IKeyDispatch() {
    }
}
