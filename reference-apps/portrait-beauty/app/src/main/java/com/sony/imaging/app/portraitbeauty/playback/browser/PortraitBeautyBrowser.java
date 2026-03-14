package com.sony.imaging.app.portraitbeauty.playback.browser;

import android.os.Message;
import com.sony.imaging.app.base.playback.browser.Browser;
import com.sony.imaging.app.portraitbeauty.common.AppLog;

/* loaded from: classes.dex */
public class PortraitBeautyBrowser extends Browser {
    private final String TAG = AppLog.getClassName();

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

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onDestroy();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }
}
