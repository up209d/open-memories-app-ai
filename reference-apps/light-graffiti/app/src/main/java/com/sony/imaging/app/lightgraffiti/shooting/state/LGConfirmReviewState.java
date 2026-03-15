package com.sony.imaging.app.lightgraffiti.shooting.state;

import android.util.Log;
import com.sony.imaging.app.base.shooting.StateBase;
import com.sony.imaging.app.lightgraffiti.shooting.LGStateHolder;
import com.sony.imaging.app.lightgraffiti.util.LGPreviewEffect;
import com.sony.imaging.app.util.ApoWrapper;

/* loaded from: classes.dex */
public class LGConfirmReviewState extends StateBase {
    private static final String TAG = LGConfirmReviewState.class.getSimpleName();

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();
        if (LGStateHolder.getInstance().isLendsProblem()) {
            LGStateHolder.getInstance().setLensProblemFlag(false);
            setNextState("FakeLensProblem", null);
        } else {
            openLayout("LGConfirmReviewLayout");
            LGPreviewEffect.getInstance().startPreviewEffect();
        }
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        LGPreviewEffect.getInstance().stopPreviewEffect();
        closeLayout("LGConfirmReviewLayout");
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        LGPreviewEffect.getInstance().stopPreviewEffect();
        super.onDestroy();
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State
    public ApoWrapper.APO_TYPE getApoType() {
        ApoWrapper.APO_TYPE type = ApoWrapper.APO_TYPE.NONE;
        return type;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        Log.d(TAG, "pushedMenuKey");
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        Log.d(TAG, "pushedCenterKey");
        removeState();
        return 1;
    }
}
