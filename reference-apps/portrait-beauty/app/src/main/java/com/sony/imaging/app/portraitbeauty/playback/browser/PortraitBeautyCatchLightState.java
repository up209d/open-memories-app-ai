package com.sony.imaging.app.portraitbeauty.playback.browser;

import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import com.sony.imaging.app.base.playback.PlayRootContainer;
import com.sony.imaging.app.base.playback.base.PlayStateBase;
import com.sony.imaging.app.portraitbeauty.common.AppLog;

/* loaded from: classes.dex */
public class PortraitBeautyCatchLightState extends PlayStateBase {
    public static final String ID_CATCH_LIGHT_PB = "ID_CATCH_LIGHT_PB";
    public static final String ID_CONFIRMATIONLAYOUT = "ID_CONFIRMATIONLAYOUT";
    public static final String ID_FACE_SELECT_PB = "ID_FACE_SELECT_PB";
    public static final String ID_PREVIEW_AFTER_PB = "ID_PREVIEW_AFTER_PB";
    public static final String ID_PREVIEW_BEFORE_PB = "ID_PREVIEW_BEFORE_PB";
    public static final String ID_SAVING_PB = "ID_SAVING_PB";
    public static final String ID_ZOOMMODEMENULAYOUT = "ID_ZOOMMODEMENULAYOUT";
    public static final String ID_ZOOM_MODE_PB = "ID_ZOOM_MODE_PB";
    String childStateName = null;

    @Override // com.sony.imaging.app.base.playback.base.PlayStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        addChildState(ID_ZOOM_MODE_PB, (Bundle) null);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode1, KeyEvent event) {
        int scanCode = event.getScanCode();
        switch (scanCode) {
            case 232:
                setNextState(PlayRootContainer.ID_BROWSER, null);
                return 1;
            default:
                return 0;
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver
    public boolean handleMessage(Message msg) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        switch (msg.what) {
            case 100:
                setNextState(ID_CATCH_LIGHT_PB, null);
                break;
            case 101:
                setNextState(PlayRootContainer.ID_BROWSER, null);
                break;
        }
        return super.handleMessage(msg);
    }
}
