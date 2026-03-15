package com.sony.imaging.app.lightgraffiti.shooting.state;

import android.util.Log;
import com.sony.imaging.app.base.shooting.ShootingMenuState;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.lightgraffiti.shooting.LGAbstractLensStateChangeListener;
import com.sony.imaging.app.lightgraffiti.shooting.LGStateHolder;
import com.sony.imaging.app.lightgraffiti.util.AppLog;
import com.sony.imaging.app.util.ApoWrapper;

/* loaded from: classes.dex */
public class LGShootingMenuState extends ShootingMenuState {
    private static String TAG = LGShootingMenuState.class.getSimpleName();
    private LGShootingMenuStateLensStateListener mLensListener = new LGShootingMenuStateLensStateListener();

    /* loaded from: classes.dex */
    private class LGShootingMenuStateLensStateListener extends LGAbstractLensStateChangeListener {
        private LGShootingMenuStateLensStateListener() {
        }

        @Override // com.sony.imaging.app.lightgraffiti.shooting.LGAbstractLensStateChangeListener
        protected void onLensStateChanged() {
            Log.d("LGShootingMenuStateLensStateListener", "onLensStateChanged");
            LGStateHolder.getInstance().setLensProblemFlag(true);
            LGShootingMenuState.this.setNextState("EE", null);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.base.menu.MenuState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        CameraNotificationManager.getInstance().setNotificationListener(this.mLensListener);
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.base.menu.MenuState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        CameraNotificationManager.getInstance().removeNotificationListener(this.mLensListener);
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        AppLog.enter(TAG, "pushedPlayBackKey");
        AppLog.exit(TAG, "pushedPlayBackKey returnState = 1");
        if (lgPushedPBKey()) {
            return 1;
        }
        return super.pushedPlayBackKey();
    }

    public boolean lgPushedPBKey() {
        Log.d(TAG, "pushedPBKey()");
        if (!LGStateHolder.getInstance().isShootingStage3rd()) {
            return false;
        }
        LGDiscardLightTrailDialogueState.showDiscardCaution = true;
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.MenuState
    public void openMenuLayout(String itemId, String layoutID) {
        Log.d(TAG, "Next manu ID is " + itemId);
        super.openMenuLayout(itemId, layoutID);
    }

    @Override // com.sony.imaging.app.base.menu.MenuState, com.sony.imaging.app.fw.State
    public ApoWrapper.APO_TYPE getApoType() {
        String stage = LGStateHolder.getInstance().getShootingStage();
        return LGStateHolder.SHOOTING_1ST.equals(stage) ? ApoWrapper.APO_TYPE.SPECIAL : ApoWrapper.APO_TYPE.NONE;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int attachedLens() {
        Log.d(TAG, "attachedLens");
        LGStateHolder.getInstance().setLensProblemFlag(true);
        return super.attachedLens();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int detachedLens() {
        Log.d(TAG, "detachedLens");
        LGStateHolder.getInstance().setLensProblemFlag(true);
        return super.detachedLens();
    }
}
