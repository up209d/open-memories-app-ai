package com.sony.imaging.app.lightshaft.playback;

import android.view.KeyEvent;
import com.sony.imaging.app.base.playback.PlayRootContainer;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.lightshaft.LightShaft;
import com.sony.imaging.app.lightshaft.LightShaftConstants;

/* loaded from: classes.dex */
public class LightShaftPlayRoot extends PlayRootContainer {
    public static final String ID_DELETE_MULTIPLE = "ID_DELETE_MULTIPLE";

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer, com.sony.imaging.app.base.playback.base.PlayStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        LightShaft.setMenuBoot(false);
    }

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        if (event.getScanCode() == 620) {
            LightShaftConstants.getInstance().setEVDialRotated(true);
        }
        return super.onConvertedKeyDown(event, func);
    }
}
