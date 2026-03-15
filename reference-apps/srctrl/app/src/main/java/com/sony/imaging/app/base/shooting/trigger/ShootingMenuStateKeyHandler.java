package com.sony.imaging.app.base.shooting.trigger;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.common.EventParcel;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.State;
import com.sony.imaging.app.util.PTag;

/* loaded from: classes.dex */
public class ShootingMenuStateKeyHandler extends ShootingKeyHandlerBase {
    private static final String PTAG_TRANSTION_TO_PLAYBACK = "transition from EE to playback";

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS1Key() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachKeyHandler
    public int onAELKeyPushed(KeyEvent event) {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachKeyHandler
    public int onAELKeyReleased(KeyEvent event) {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        PTag.start(PTAG_TRANSTION_TO_PLAYBACK);
        State state = (State) this.target;
        Bundle b = new Bundle();
        Bundle data = new Bundle();
        data.putParcelable(EventParcel.KEY_KEYCODE, new EventParcel(AppRoot.USER_KEYCODE.PLAYBACK));
        b.putParcelable("S1OffEE", data);
        Activity appRoot = state.getActivity();
        ((BaseApp) appRoot).changeApp(BaseApp.APP_PLAY, b);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayIndexKey() {
        PTag.start(PTAG_TRANSTION_TO_PLAYBACK);
        State state = (State) this.target;
        Bundle b = new Bundle();
        Bundle data = new Bundle();
        data.putParcelable(EventParcel.KEY_KEYCODE, new EventParcel(CustomizableFunction.PlayIndex));
        b.putParcelable("S1OffEE", data);
        Activity appRoot = state.getActivity();
        ((BaseApp) appRoot).changeApp(BaseApp.APP_PLAY, b);
        return 1;
    }
}
