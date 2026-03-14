package com.sony.imaging.app.portraitbeauty.playback.browser;

import android.os.Message;
import com.sony.imaging.app.base.playback.pseudorec.PseudoRec;
import com.sony.imaging.app.portraitbeauty.common.AppLog;

/* loaded from: classes.dex */
public class PortraitBeautyPseudoRec extends PseudoRec {
    @Override // com.sony.imaging.app.fw.KeyReceiver
    public boolean handleMessage(Message msg) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        switch (msg.what) {
            case 100:
                setNextState(PortraitBeautyCatchLightState.ID_CATCH_LIGHT_PB, null);
                break;
        }
        return super.handleMessage(msg);
    }
}
