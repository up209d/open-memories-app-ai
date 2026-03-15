package com.sony.imaging.app.lightgraffiti.shooting.state;

import android.util.Log;
import android.view.KeyEvent;
import com.sony.imaging.app.base.caution.CautionDisplayState;
import com.sony.imaging.app.lightgraffiti.shooting.LGStateHolder;
import com.sony.imaging.app.util.ApoWrapper;

/* loaded from: classes.dex */
public class LGCautionDisplayState extends CautionDisplayState {
    private final String TAG = "LGCautionDisplayState";

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onLensAttached(KeyEvent event) {
        Log.d("LGCautionDisplayState", "onLensAttached");
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onLensDetached(KeyEvent event) {
        Log.d("LGCautionDisplayState", "onLensDetached");
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int attachedLens() {
        Log.d("LGCautionDisplayState", "attachedLens");
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int detachedLens() {
        Log.d("LGCautionDisplayState", "detachedLens");
        return 0;
    }

    @Override // com.sony.imaging.app.base.caution.CautionDisplayState, com.sony.imaging.app.fw.State
    public ApoWrapper.APO_TYPE getApoType() {
        ApoWrapper.APO_TYPE type = ApoWrapper.APO_TYPE.NONE;
        String stage = LGStateHolder.getInstance().getShootingStage();
        if (LGStateHolder.SHOOTING_1ST.equals(stage)) {
            ApoWrapper.APO_TYPE type2 = super.getApoType();
            return type2;
        }
        return type;
    }
}
