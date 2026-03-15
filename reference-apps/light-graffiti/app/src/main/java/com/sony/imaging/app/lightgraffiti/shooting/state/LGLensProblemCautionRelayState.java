package com.sony.imaging.app.lightgraffiti.shooting.state;

import android.os.Handler;
import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.fw.State;
import com.sony.imaging.app.lightgraffiti.shooting.LGStateHolder;
import com.sony.imaging.app.lightgraffiti.shooting.controller.LGMenuDataInitializer;

/* loaded from: classes.dex */
public class LGLensProblemCautionRelayState extends State {
    private static final String TAG = "LGLensProblemCautionRelayState";
    public static boolean isLensProblemState = false;
    private final int CLOSE_WAITING_DELAY_TIME = 500;

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        isLensProblemState = true;
        Log.d(TAG, "onResume() ");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() { // from class: com.sony.imaging.app.lightgraffiti.shooting.state.LGLensProblemCautionRelayState.1
            @Override // java.lang.Runnable
            public void run() {
                Log.d(LGLensProblemCautionRelayState.TAG, "LGLensProblemCautionRelayState postdelayd()");
                try {
                    String stage = LGStateHolder.getInstance().getShootingStage();
                    LGMenuDataInitializer.initMenuData(stage);
                    LGStateHolder.getInstance().prepareShootingStage1st();
                    LGLensProblemCautionRelayState.this.setNextState("EE", null);
                    ExecutorCreator.getInstance().stableSequence();
                    ExecutorCreator.getInstance().updateSequence();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 500L);
    }
}
