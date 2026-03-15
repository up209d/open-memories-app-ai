package com.sony.imaging.app.lightgraffiti.shooting.keyhandler;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import com.sony.imaging.app.base.shooting.trigger.FocusAdjustmentKeyHandler;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.State;
import com.sony.imaging.app.lightgraffiti.shooting.LGStateHolder;
import com.sony.imaging.app.lightgraffiti.util.LGConstants;

/* loaded from: classes.dex */
public class LGFocusAdjustmentKeyHandler extends FocusAdjustmentKeyHandler {
    public static final String TAG = LGMfAssistStateKeyHandler.class.getSimpleName();

    @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int result = 0;
        int scanCode = event.getScanCode();
        switch (scanCode) {
            case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
                result = lgPushedPBKey();
                break;
        }
        if (result == 0) {
            int result2 = super.onKeyDown(keyCode, event);
            return result2;
        }
        return result;
    }

    public int lgPushedPBKey() {
        Log.d(TAG, "pushedPBKey()");
        if (!LGStateHolder.getInstance().isShootingStage3rd()) {
            return 0;
        }
        State state = (State) this.target;
        Bundle bundle = new Bundle();
        bundle.putString(LGConstants.TRANSIT_FROM_WHERE, LGConstants.TRANSIT_FROM_3RD_BY_PB);
        state.setNextState("DiscardDialogue", bundle);
        return 1;
    }
}
