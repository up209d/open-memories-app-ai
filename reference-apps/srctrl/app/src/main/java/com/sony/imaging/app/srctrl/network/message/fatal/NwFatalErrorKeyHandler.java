package com.sony.imaging.app.srctrl.network.message.fatal;

import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.srctrl.network.base.NwKeyHandlerBase;
import com.sony.imaging.app.util.BeepUtility;

/* loaded from: classes.dex */
public class NwFatalErrorKeyHandler extends NwKeyHandlerBase {
    public static final String TAG = NwFatalErrorKeyHandler.class.getSimpleName();

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        BeepUtility.getInstance().playBeep("BEEP_ID_SELECT");
        ((BaseApp) this.target.getActivity()).finish(false);
        return 1;
    }
}
