package com.sony.imaging.app.lightgraffiti.shooting.state;

import com.sony.imaging.app.fw.State;
import com.sony.imaging.app.lightgraffiti.shooting.LGStateHolder;

/* loaded from: classes.dex */
public class LGFakeS1OnEEState extends State implements LGStateHolder.ValueChangedListener {
    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        openLayout("LGFakeS1OnEELayout");
        openLayout("LGFakeFocusLayout");
        LGStateHolder.getInstance().setValueChangedListener(this);
    }

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        closeLayout("LGFakeS1OnEELayout");
        closeLayout("LGFakeFocusLayout");
        LGStateHolder.getInstance().removeValueChangedListener(this);
    }

    @Override // com.sony.imaging.app.lightgraffiti.shooting.LGStateHolder.ValueChangedListener
    public void onValueChanged(String tag) {
        if (tag.equals(LGStateHolder.SHOOTING_STAGE) && !LGStateHolder.getInstance().getShootingStage().equals(LGStateHolder.SELFTIMER_COUNTING) && !LGStateHolder.getInstance().getShootingStage().equals(LGStateHolder.SHOOTING_1ST) && !LGStateHolder.getInstance().getShootingStage().equals(LGStateHolder.SHOOTING_3RD_BEFORE_SHOOT) && !LGStateHolder.getInstance().getShootingStage().equals(LGStateHolder.SHOOTING_3RD_AFTER_SHOOT)) {
            removeState();
        }
    }
}
