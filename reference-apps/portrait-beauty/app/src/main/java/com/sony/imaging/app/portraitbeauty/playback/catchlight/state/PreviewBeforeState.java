package com.sony.imaging.app.portraitbeauty.playback.catchlight.state;

import android.view.KeyEvent;
import com.sony.imaging.app.base.playback.base.PlayStateBase;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.portraitbeauty.common.AppLog;
import com.sony.imaging.app.portraitbeauty.playback.browser.PortraitBeautyCatchLightState;

/* loaded from: classes.dex */
public class PreviewBeforeState extends PlayStateBase {
    public static final int ID_ID_PREVIEWBEFORELAYOUT = 101;
    public static final String ID_PREVIEW_BEFORE_LAYOUT = "ID_PREVIEW_BEFORE_LAYOUT";

    @Override // com.sony.imaging.app.base.playback.base.PlayStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onResume();
        openLayout(ID_PREVIEW_BEFORE_LAYOUT);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        closeLayout(ID_PREVIEW_BEFORE_LAYOUT);
        super.onDestroy();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int scanCode = event.getScanCode();
        switch (scanCode) {
            case AppRoot.USER_KEYCODE.SK2 /* 513 */:
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                setNextState(PortraitBeautyCatchLightState.ID_PREVIEW_AFTER_PB, null);
                return 1;
            case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
            case AppRoot.USER_KEYCODE.S2_ON /* 518 */:
            case AppRoot.USER_KEYCODE.AF_MF /* 533 */:
            case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
            case AppRoot.USER_KEYCODE.DIGITAL_ZOOM /* 609 */:
            case AppRoot.USER_KEYCODE.ZOOM_LEVER_TELE /* 610 */:
            case AppRoot.USER_KEYCODE.ZOOM_LEVER_WIDE /* 611 */:
            case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
            case AppRoot.USER_KEYCODE.S1_OFF /* 772 */:
            case AppRoot.USER_KEYCODE.S2_OFF /* 774 */:
                return 0;
            default:
                return -1;
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        return -1;
    }
}
