package com.sony.imaging.app.bracketpro.playback;

import android.view.KeyEvent;
import com.sony.imaging.app.base.playback.PlayRootContainer;
import com.sony.imaging.app.bracketpro.shooting.state.BMEEState;
import com.sony.imaging.app.bracketpro.shooting.state.BMShootingStateKeyHandler;
import com.sony.imaging.app.fw.IKeyFunction;

/* loaded from: classes.dex */
public class BMPlayRoot extends PlayRootContainer {
    public static final String ID_DELETE_MULTIPLE = "ID_DELETE_MULTIPLE";

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onLensDetached(KeyEvent event) {
        BMEEState.isBMCautionStateBooted = true;
        return super.onLensDetached(event);
    }

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        int code = event.getScanCode();
        if (code == 516 || code == 613) {
            BMShootingStateKeyHandler.isS2PressedOnPB = true;
        } else {
            BMShootingStateKeyHandler.isS2PressedOnPB = false;
        }
        return super.onConvertedKeyDown(event, func);
    }
}
