package com.sony.imaging.app.srctrl.network.waiting.waiting;

import android.view.KeyEvent;
import com.sony.imaging.app.srctrl.network.NetworkRootState;
import com.sony.imaging.app.srctrl.network.base.NwKeyHandlerBase;
import com.sony.imaging.app.util.BeepUtility;

/* loaded from: classes.dex */
public class NwWaitingKeyHandler extends NwKeyHandlerBase {
    public static final String TAG = NwWaitingKeyHandler.class.getSimpleName();

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        BeepUtility.getInstance().playBeep("BEEP_ID_SELECT");
        ((NetworkRootState) this.target).getActivity().finish();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachKeyHandler
    public int onDeleteKeyPushed(KeyEvent event) {
        return this.target.onDeleteKeyPushed(event);
    }
}
