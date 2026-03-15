package com.sony.imaging.app.lightgraffiti.shooting.keyhandler;

import android.util.Log;
import com.sony.imaging.app.base.shooting.trigger.S1OnEEStateKeyHandler;
import com.sony.imaging.app.fw.State;
import com.sony.imaging.app.lightgraffiti.util.LGUtility;

/* loaded from: classes.dex */
public class LGS1OnEEStateKeyHandler extends S1OnEEStateKeyHandler {
    private final String TAG = "LGS1OnEEStateKeyHandler";

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OnEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int attachedLens() {
        Log.d("LGS1OnEEStateKeyHandler", "attachedLens");
        LGUtility.getInstance().isLensAttachEventReady = true;
        State state = (State) this.target;
        state.setNextState("LensProblem", null);
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OnEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int detachedLens() {
        Log.d("LGS1OnEEStateKeyHandler", "detachedLens");
        State state = (State) this.target;
        state.setNextState("LensProblem", null);
        return 1;
    }
}
