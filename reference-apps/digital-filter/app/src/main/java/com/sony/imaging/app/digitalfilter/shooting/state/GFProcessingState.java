package com.sony.imaging.app.digitalfilter.shooting.state;

import android.os.Handler;
import com.sony.imaging.app.base.shooting.StateBase;
import com.sony.imaging.app.digitalfilter.shooting.GFCompositProcess;

/* loaded from: classes.dex */
public class GFProcessingState extends StateBase {
    private static final String NEXT_STATE = "Development";

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        Handler mHandler = new Handler();
        Runnable mRunnable = new Runnable() { // from class: com.sony.imaging.app.digitalfilter.shooting.state.GFProcessingState.1
            @Override // java.lang.Runnable
            public void run() {
                GFCompositProcess.storeCompositImage();
                GFProcessingState.this.setNextState(GFProcessingState.NEXT_STATE, null);
            }
        };
        mHandler.postDelayed(mRunnable, 50L);
    }
}
