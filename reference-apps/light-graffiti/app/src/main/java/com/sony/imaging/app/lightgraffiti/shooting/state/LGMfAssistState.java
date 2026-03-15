package com.sony.imaging.app.lightgraffiti.shooting.state;

import com.sony.imaging.app.base.shooting.MfAssistState;
import com.sony.imaging.app.lightgraffiti.shooting.LGStateHolder;
import com.sony.imaging.app.lightgraffiti.util.LGPreviewEffect;
import com.sony.imaging.app.util.ApoWrapper;

/* loaded from: classes.dex */
public class LGMfAssistState extends MfAssistState {
    @Override // com.sony.imaging.app.base.shooting.MfAssistState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        LGShootingState.startFirstTimeLensCare();
        if (LGStateHolder.getInstance().isLendsProblem()) {
            LGStateHolder.getInstance().setLensProblemFlag(false);
            setNextState("LensProblem", null);
        } else {
            super.onResume();
            if (!LGStateHolder.getInstance().getShootingStage().equals(LGStateHolder.SHOOTING_1ST)) {
                LGPreviewEffect.getInstance().stopPreviewEffect();
            }
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        if (!LGStateHolder.getInstance().getShootingStage().equals(LGStateHolder.SHOOTING_1ST)) {
            LGPreviewEffect.getInstance().startPreviewEffect();
        }
        super.onDestroy();
    }

    @Override // com.sony.imaging.app.base.shooting.MfAssistState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State
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
