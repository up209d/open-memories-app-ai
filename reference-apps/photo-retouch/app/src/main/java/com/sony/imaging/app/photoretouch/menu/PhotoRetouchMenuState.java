package com.sony.imaging.app.photoretouch.menu;

import android.os.Bundle;
import com.sony.imaging.app.base.menu.MenuState;
import com.sony.imaging.app.util.ApoWrapper;

/* loaded from: classes.dex */
public class PhotoRetouchMenuState extends MenuState {
    @Override // com.sony.imaging.app.base.menu.MenuState, com.sony.imaging.app.fw.State
    public Integer getCautionMode() {
        return 13;
    }

    @Override // com.sony.imaging.app.base.menu.MenuState, com.sony.imaging.app.fw.State
    public ApoWrapper.APO_TYPE getApoType() {
        return ApoWrapper.APO_TYPE.NONE;
    }

    @Override // com.sony.imaging.app.base.menu.MenuState
    protected void finishMenuState(Bundle bundle) {
    }

    @Override // com.sony.imaging.app.base.menu.MenuState
    protected boolean canRemoveState() {
        return true;
    }
}
