package com.sony.imaging.app.base.caution;

import com.sony.imaging.app.fw.Layout;

/* loaded from: classes.dex */
public class IkeyDispatchEach extends IKeyDispatch {
    protected Layout layout;

    public IkeyDispatchEach(CautionProcessingFunction p, Layout l) {
        super(p);
        this.layout = null;
        this.layout = l;
    }

    public IkeyDispatchEach() {
        this.layout = null;
    }

    @Override // com.sony.imaging.app.fw.BaseKeyHandler
    protected String getKeyConvCategory() {
        return "Menu";
    }
}
