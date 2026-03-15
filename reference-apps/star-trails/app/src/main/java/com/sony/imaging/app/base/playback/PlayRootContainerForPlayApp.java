package com.sony.imaging.app.base.playback;

import android.view.KeyEvent;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;

/* loaded from: classes.dex */
public class PlayRootContainerForPlayApp extends PlayRootContainer {
    @Override // com.sony.imaging.app.base.playback.PlayRootContainer
    public void changeToShooting() {
    }

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        int code = event.getScanCode();
        if (!CustomizableFunction.Unchanged.equals(func)) {
            return 0;
        }
        switch (code) {
            case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
            case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
            case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
            case AppRoot.USER_KEYCODE.UM_S1 /* 613 */:
            case AppRoot.USER_KEYCODE.SHOOTING_MODE /* 624 */:
            case AppRoot.USER_KEYCODE.MODE_P /* 628 */:
            case AppRoot.USER_KEYCODE.MODE_A /* 629 */:
            case AppRoot.USER_KEYCODE.MODE_S /* 630 */:
            case AppRoot.USER_KEYCODE.MODE_M /* 631 */:
                return -1;
            case AppRoot.USER_KEYCODE.MOVIE_REC /* 515 */:
            case AppRoot.USER_KEYCODE.UM_MOVIE_REC /* 616 */:
            case AppRoot.USER_KEYCODE.MOVIE_REC_2ND /* 637 */:
                openRecDisabledCaution();
                return 1;
            default:
                int result = super.onConvertedKeyDown(event, func);
                return result;
        }
    }
}
