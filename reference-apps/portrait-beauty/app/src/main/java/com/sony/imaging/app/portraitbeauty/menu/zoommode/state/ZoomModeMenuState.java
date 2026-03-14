package com.sony.imaging.app.portraitbeauty.menu.zoommode.state;

import android.os.Bundle;
import android.os.Message;
import com.sony.imaging.app.base.menu.MenuState;
import com.sony.imaging.app.base.playback.PlayRootContainer;
import com.sony.imaging.app.util.ApoWrapper;

/* loaded from: classes.dex */
public class ZoomModeMenuState extends MenuState {
    @Override // com.sony.imaging.app.base.menu.MenuState
    protected void finishMenuState(Bundle bundle) {
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver
    public boolean handleMessage(Message msg) {
        if (msg.what == 1010) {
            setNextState(PlayRootContainer.ID_BROWSER, null);
        }
        return super.handleMessage(msg);
    }

    @Override // com.sony.imaging.app.base.menu.MenuState, com.sony.imaging.app.fw.State
    public ApoWrapper.APO_TYPE getApoType() {
        return ApoWrapper.APO_TYPE.SPECIAL;
    }
}
