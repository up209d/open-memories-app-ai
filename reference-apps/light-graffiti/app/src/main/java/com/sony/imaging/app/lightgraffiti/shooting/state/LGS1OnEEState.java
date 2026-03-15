package com.sony.imaging.app.lightgraffiti.shooting.state;

import android.util.Log;
import com.sony.imaging.app.base.shooting.S1OnEEState;
import com.sony.imaging.app.base.shooting.camera.AutoFocusModeController;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.lightgraffiti.shooting.LGAbstractLensStateChangeListener;
import com.sony.imaging.app.lightgraffiti.shooting.LGStateHolder;
import com.sony.imaging.app.lightgraffiti.util.LGConstants;
import com.sony.scalar.sysutil.KeyStatus;
import com.sony.scalar.sysutil.ScalarInput;

/* loaded from: classes.dex */
public class LGS1OnEEState extends S1OnEEState {
    private static final String TAG = LGS1OnEEState.class.getSimpleName();
    private LGS1OnEEStateLensStateListener mLensListener = new LGS1OnEEStateLensStateListener();

    /* loaded from: classes.dex */
    private class LGS1OnEEStateLensStateListener extends LGAbstractLensStateChangeListener {
        private LGS1OnEEStateLensStateListener() {
        }

        @Override // com.sony.imaging.app.lightgraffiti.shooting.LGAbstractLensStateChangeListener
        protected void onLensStateChanged() {
            Log.d("LGS1OnEEStateLensStateListener", "onLensStateChanged");
            if (LGStateHolder.getInstance().isShootingStage3rd()) {
                Log.d("LGS1OnEEStateLensStateListener", "LGS1OnEEState detected the lens problem. App will ");
                LGS1OnEEState.this.setNextState("LensProblem", null);
            }
        }
    }

    @Override // com.sony.imaging.app.base.shooting.S1OnEEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        Log.d(TAG, "onResume. setIsStartFocusing(true)");
        CameraNotificationManager.getInstance().setNotificationListener(this.mLensListener);
        LGShootingState.startFirstTimeLensCare();
        if (LGLensProblemCautionRelayState.isLensProblemState) {
            LGLensProblemCautionRelayState.isLensProblemState = false;
        }
        if (LGStateHolder.getInstance().isLendsProblem()) {
            LGStateHolder.getInstance().setLensProblemFlag(false);
            setNextState("LensProblem", null);
            return;
        }
        LGStateHolder.getInstance().setIsStartFocusing(true);
        LGStateHolder.getInstance().setShootingEnable(true);
        KeyStatus status = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.S1_ON);
        if (1 == status.status) {
            closeLayout(LGConstants.LAYOUT_EE_GUIDE_DETAIL);
            LGStateHolder.getInstance().setShootingEnable(true);
            ExecutorCreator executorCreator = ExecutorCreator.getInstance();
            if (!executorCreator.isAElockedOnAutoFocus()) {
                String behavior = null;
                String focusMode = AutoFocusModeController.getInstance().getValue();
                if ("af-s".equals(focusMode)) {
                    behavior = "af_woaf";
                } else if ("af-c".equals(focusMode)) {
                    behavior = "af_woaf";
                }
                executorCreator.getSequence().autoFocus(null, behavior);
            } else {
                executorCreator.getSequence().autoFocus(null);
            }
        }
        super.onResume();
    }

    @Override // com.sony.imaging.app.base.shooting.S1OnEEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        CameraNotificationManager.getInstance().removeNotificationListener(this.mLensListener);
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        super.onDestroy();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedS1Key() {
        Log.d(TAG, "releasedS1Key. setIsStartFocusing(false)");
        LGStateHolder.getInstance().setIsStartFocusing(false);
        return super.releasedS1Key();
    }
}
