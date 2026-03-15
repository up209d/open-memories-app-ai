package com.sony.imaging.app.graduatedfilter.shooting.state;

import android.os.Handler;
import com.sony.imaging.app.base.shooting.S1OffEEState;
import com.sony.imaging.app.base.shooting.S1OnEEState;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.graduatedfilter.common.AppContext;
import com.sony.imaging.app.graduatedfilter.common.AppLog;
import com.sony.imaging.app.graduatedfilter.common.GFCommonUtil;
import com.sony.imaging.app.graduatedfilter.common.GFConstants;
import com.sony.imaging.app.graduatedfilter.sa.GFHistgramTask;
import com.sony.imaging.app.graduatedfilter.sa.SFRSA;
import com.sony.imaging.app.graduatedfilter.shooting.base.GFDisplayModeObserver;
import com.sony.imaging.app.graduatedfilter.shooting.camera.GFWhiteBalanceController;
import com.sony.imaging.app.graduatedfilter.shooting.camera.TouchLessShutterController;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class GFS1OnEEState extends S1OnEEState implements NotificationListener {
    private static final String TAG = AppLog.getClassName();
    private static Handler myHandler = null;
    private static Runnable myRunnable = null;

    @Override // com.sony.imaging.app.base.shooting.S1OnEEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        GFWhiteBalanceController.getInstance().setWBDetailValueFromPF(true);
        CameraNotificationManager.getInstance().setNotificationListener(this);
        super.onResume();
        if (SFRSA.getInstance().getCameraSequence() != null) {
            startLiveveiwEffect();
        }
        if (TouchLessShutterController.ExposingByTouchLessShutter) {
            if (myHandler == null) {
                myHandler = new Handler();
            }
            if (myRunnable == null) {
                myRunnable = new Runnable() { // from class: com.sony.imaging.app.graduatedfilter.shooting.state.GFS1OnEEState.1
                    @Override // java.lang.Runnable
                    public void run() {
                        ExecutorCreator.getInstance().getSequence().inquireKey(AppRoot.USER_KEYCODE.S2_ON);
                    }
                };
            }
            myHandler.postDelayed(myRunnable, 400L);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.S1OnEEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        GFWhiteBalanceController.getInstance().setWBDetailValueFromPF(false);
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        GFCommonUtil.getInstance().setInvalidShutter(false);
        super.onPause();
    }

    private void startLiveveiwEffect() {
        if (!SFRSA.getInstance().isAvailableHistgram()) {
            SFRSA.getInstance().setPackageName(AppContext.getAppContext().getPackageName());
            SFRSA.getInstance().initialize();
            SFRSA.getInstance().setCommand(16);
            SFRSA.getInstance().startLiveViewEffect(false);
        }
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return new String[]{GFConstants.RESTART_COMPOSIT_PROCESS, GFConstants.CANCEL_TOUCHLESS_SHOOTING};
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        AppLog.info(TAG, "onNotify: " + tag);
        if (GFConstants.RESTART_COMPOSIT_PROCESS.equalsIgnoreCase(tag)) {
            GFHistgramTask.getInstance().stopHistgramUpdating();
            SFRSA.getInstance().terminate();
            startLiveveiwEffect();
            GFHistgramTask.getInstance().startHistgramUpdating();
            int displayMode = GFDisplayModeObserver.getInstance().getActiveDispMode(0);
            if (displayMode != 5) {
                GFHistgramTask.getInstance().stopHistgramUpdating();
                return;
            }
            return;
        }
        if (GFConstants.CANCEL_TOUCHLESS_SHOOTING.equalsIgnoreCase(tag)) {
            setNextState(S1OffEEState.STATE_NAME, null);
        }
    }
}
