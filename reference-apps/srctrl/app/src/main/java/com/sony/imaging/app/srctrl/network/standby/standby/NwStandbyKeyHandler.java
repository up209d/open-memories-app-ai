package com.sony.imaging.app.srctrl.network.standby.standby;

import com.sony.imaging.app.srctrl.network.NetworkRootState;
import com.sony.imaging.app.srctrl.network.base.NwKeyHandlerBase;
import com.sony.imaging.app.util.BeepUtility;

/* loaded from: classes.dex */
public class NwStandbyKeyHandler extends NwKeyHandlerBase {
    public static final String TAG = NwStandbyKeyHandler.class.getSimpleName();

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        BeepUtility.getInstance().playBeep("BEEP_ID_SELECT");
        ((NetworkRootState) this.target).getActivity().finish();
        return 1;
    }
}
