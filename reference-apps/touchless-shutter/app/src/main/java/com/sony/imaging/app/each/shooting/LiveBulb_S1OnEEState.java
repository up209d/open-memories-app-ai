package com.sony.imaging.app.each.shooting;

import android.util.Log;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.shooting.S1OnEEState;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.each.AppLog;
import com.sony.imaging.app.each.EachApp;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.scalar.hardware.avio.DisplayManager;
import com.sony.scalar.sysutil.ScalarProperties;
import com.sony.scalar.sysutil.didep.Kikilog;

/* loaded from: classes.dex */
public class LiveBulb_S1OnEEState extends S1OnEEState {
    private static final String TAG = AppLog.getClassName();
    private String mCurrentDisplay;
    OnDisplayEventListener mDispEventListener;
    DisplayManager mDisplayManager = null;

    @Override // com.sony.imaging.app.base.shooting.S1OnEEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mDispEventListener = new OnDisplayEventListener();
        this.mDisplayManager = new DisplayManager();
        this.mDisplayManager.setDisplayStatusListener(this.mDispEventListener);
        this.mCurrentDisplay = this.mDisplayManager.getActiveDevice();
        super.onResume();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.S1OnEEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mDisplayManager.releaseDisplayStatusListener();
        this.mDisplayManager.finish();
        this.mDispEventListener = null;
        this.mDisplayManager = null;
        super.onPause();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    /* loaded from: classes.dex */
    class OnDisplayEventListener implements DisplayManager.DisplayEventListener {
        OnDisplayEventListener() {
        }

        public void onDeviceStatusChanged(int eventId) {
            Log.i(LiveBulb_S1OnEEState.TAG, "★★ onDeviceStatusChanged");
            if (4096 == eventId) {
                String str = LiveBulb_S1OnEEState.this.mDisplayManager.getActiveDevice();
                if (str != null) {
                    Log.i(LiveBulb_S1OnEEState.TAG, "★★★★DEVICE not null");
                    if (str.equals("DEVICE_ID_FINDER")) {
                        Log.i(LiveBulb_S1OnEEState.TAG, "★★★★DEVICE_ID_FINDER");
                        TouchLessShutter_go();
                        return;
                    } else if (str.equals("DEVICE_ID_HDMI")) {
                        Log.i(LiveBulb_S1OnEEState.TAG, "★★★★DEVICE_ID_HDMI");
                        return;
                    } else {
                        if (str.equals("DEVICE_ID_PANEL")) {
                            Log.i(LiveBulb_S1OnEEState.TAG, "★★★★DEVICE_ID_PANEL");
                            ExecutorCreator.getInstance().getSequence().cancelAutoFocus();
                            ExecutorCreator.getInstance().getSequence().cancelTakePicture();
                            EachApp.ExposingByTouchLessShutter = false;
                            return;
                        }
                        return;
                    }
                }
                Log.e(LiveBulb_S1OnEEState.TAG, "★★★★DEVICE is null");
                Log.d(LiveBulb_S1OnEEState.TAG, "★★★★\u3000EVF?");
                if (Double.parseDouble(ScalarProperties.getString("version.platform")) > 2.0d) {
                    Log.d(LiveBulb_S1OnEEState.TAG, "★★★★\u3000DEVICE_ID_FINDER?");
                    TouchLessShutter_go();
                }
            }
        }

        private void TouchLessShutter_go() {
            if (LiveBulb_S1OnEEState.this.mCurrentDisplay == null) {
                Log.w(LiveBulb_S1OnEEState.TAG, "★★★★ mCurrentDisplay was null return now.");
                return;
            }
            if ("DEVICE_ID_PANEL".equals(LiveBulb_S1OnEEState.this.mCurrentDisplay)) {
                Log.d(LiveBulb_S1OnEEState.TAG, "★★★★ Push S2");
                if (CautionUtilityClass.getInstance().getCurrentCautionData() == null) {
                    EachApp.ExposingByTouchLessShutter = true;
                    pushedS2Key();
                    if (CameraSetting.getInstance().isShutterSpeedBulb()) {
                        Kikilog.setUserLog(EachApp.kikilog_subid_BulbCaptureTimes, EachApp.kikilog_options);
                    } else {
                        Kikilog.setUserLog(EachApp.kikilog_subid_NormalCaptureTimes, EachApp.kikilog_options);
                    }
                    int[] currentCautionId = CautionUtilityClass.getInstance().CurrentCautionId();
                    for (int i : currentCautionId) {
                        if (i == 1399) {
                            EachApp.ExposingByTouchLessShutter = false;
                            ExecutorCreator.getInstance().getSequence().cancelTakePicture();
                            ExecutorCreator.getInstance().getSequence().cancelAutoFocus();
                        }
                    }
                }
            }
        }

        public int pushedS2Key() {
            ExecutorCreator.getInstance().getSequence().inquireKey(AppRoot.USER_KEYCODE.S2_ON);
            return 1;
        }
    }
}
