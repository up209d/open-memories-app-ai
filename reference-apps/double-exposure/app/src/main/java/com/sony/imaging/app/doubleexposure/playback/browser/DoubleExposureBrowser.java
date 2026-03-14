package com.sony.imaging.app.doubleexposure.playback.browser;

import android.os.Message;
import com.sony.imaging.app.base.playback.browser.Browser;
import com.sony.imaging.app.doubleexposure.common.AppLog;
import com.sony.imaging.app.doubleexposure.playback.state.PreparingPlayState;

/* loaded from: classes.dex */
public class DoubleExposureBrowser extends Browser {
    private final String TAG = AppLog.getClassName();

    @Override // com.sony.imaging.app.fw.KeyReceiver
    public boolean handleMessage(Message msg) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        switch (msg.what) {
            case 100:
                setNextState(PreparingPlayState.ID_PREPAIRINGLAYOUT, null);
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
