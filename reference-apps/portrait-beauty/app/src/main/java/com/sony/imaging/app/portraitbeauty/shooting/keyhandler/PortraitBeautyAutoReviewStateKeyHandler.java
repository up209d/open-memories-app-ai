package com.sony.imaging.app.portraitbeauty.shooting.keyhandler;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.shooting.trigger.AutoReviewStateKeyHandler;
import com.sony.imaging.app.fw.State;
import com.sony.imaging.app.portraitbeauty.playback.browser.PortraitBeautyCatchLightState;
import com.sony.imaging.app.portraitbeauty.shooting.state.PortraitBeautyDevelopmentState;

/* loaded from: classes.dex */
public class PortraitBeautyAutoReviewStateKeyHandler extends AutoReviewStateKeyHandler {
    public static boolean bIsAdjustModeGuide = false;

    @Override // com.sony.imaging.app.base.shooting.trigger.AutoReviewStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        Bundle bundle = new Bundle();
        State state = (State) this.target;
        bundle.putString(PortraitBeautyDevelopmentState.sTransitShootingToPlayBack, PortraitBeautyCatchLightState.ID_CATCH_LIGHT_PB);
        Activity appRoot = state.getActivity();
        ((BaseApp) appRoot).changeApp(BaseApp.APP_PLAY, bundle);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        if (event.getScanCode() == 232) {
            Bundle bundle = new Bundle();
            State state = (State) this.target;
            bundle.putString(PortraitBeautyDevelopmentState.sTransitShootingToPlayBack, PortraitBeautyCatchLightState.ID_CATCH_LIGHT_PB);
            Activity appRoot = state.getActivity();
            ((BaseApp) appRoot).changeApp(BaseApp.APP_PLAY, bundle);
            return 1;
        }
        int returnState = super.onKeyDown(keyCode, event);
        return returnState;
    }

    @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        if (event.getScanCode() == 232) {
            return 1;
        }
        int returnState = super.onKeyDown(keyCode, event);
        return returnState;
    }
}
