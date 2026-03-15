package com.sony.imaging.app.each.shooting;

import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.shooting.S1OffEEState;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.each.AppLog;
import com.sony.imaging.app.each.EachApp;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.scalar.hardware.avio.DisplayManager;
import com.sony.scalar.sysutil.ScalarProperties;
import com.sony.scalar.sysutil.didep.Kikilog;

/* loaded from: classes.dex */
public class LiveBulb_S1OffEEState extends S1OffEEState {
    private static final String TAG = AppLog.getClassName();
    private String mCurrentDisplay;
    OnDisplayEventListener mDispEventListener;
    DisplayManager mDisplayManager = null;

    @Override // com.sony.imaging.app.base.shooting.S1OffEEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(TAG, AppLog.getMethodName());
        super.onResume();
        this.mDispEventListener = new OnDisplayEventListener();
        this.mDisplayManager = new DisplayManager();
        this.mDisplayManager.setDisplayStatusListener(this.mDispEventListener);
        this.mCurrentDisplay = this.mDisplayManager.getActiveDevice();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.S1OffEEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
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
            Log.i(LiveBulb_S1OffEEState.TAG, "★★ onDeviceStatusChanged");
            if (4096 == eventId) {
                String str = LiveBulb_S1OffEEState.this.mDisplayManager.getActiveDevice();
                if (str != null) {
                    Log.i(LiveBulb_S1OffEEState.TAG, "★★★★DEVICE not null");
                    if (LiveBulb_S1OffEEState.this.mDisplayManager.getActiveDevice().equals("DEVICE_ID_FINDER")) {
                        Log.i(LiveBulb_S1OffEEState.TAG, "★★★★DEVICE_ID_FINDER");
                        TouchLessShutter_go();
                    } else if (str.equals("DEVICE_ID_HDMI")) {
                        Log.i(LiveBulb_S1OffEEState.TAG, "★★★★DEVICE_ID_HDMI");
                    } else if (str.equals("DEVICE_ID_PANEL")) {
                        Log.i(LiveBulb_S1OffEEState.TAG, "★★★★DEVICE_ID_PANEL");
                    }
                    LiveBulb_S1OffEEState.this.mCurrentDisplay = LiveBulb_S1OffEEState.this.mDisplayManager.getActiveDevice();
                    Log.i(LiveBulb_S1OffEEState.TAG, "★★★★mCurrentDisplay is updated: " + LiveBulb_S1OffEEState.this.mCurrentDisplay.toString());
                    return;
                }
                Log.e(LiveBulb_S1OffEEState.TAG, "★★★★DEVICE is null");
                if (Double.parseDouble(ScalarProperties.getString("version.platform")) > 2.0d) {
                    Log.d(LiveBulb_S1OffEEState.TAG, "★★★★\u3000DEVICE_ID_FINDER?");
                    TouchLessShutter_go();
                }
            }
        }

        private void TouchLessShutter_go() {
            if (LiveBulb_S1OffEEState.this.mCurrentDisplay == null) {
                Log.w(LiveBulb_S1OffEEState.TAG, "★★★★ mCurrentDisplay was null return now.");
                return;
            }
            if ("DEVICE_ID_PANEL".equals(LiveBulb_S1OffEEState.this.mCurrentDisplay)) {
                Log.d(LiveBulb_S1OffEEState.TAG, "★★★★ Push S1, S2");
                if (CautionUtilityClass.getInstance().getCurrentCautionData() == null) {
                    EachApp.ExposingByTouchLessShutter = true;
                    if (CameraSetting.getInstance().getCurrentMode() == 1) {
                        pushedS1Key();
                        pushedS2Key();
                        if (CameraSetting.getInstance().isShutterSpeedBulb()) {
                            Kikilog.setUserLog(EachApp.kikilog_subid_BulbCaptureTimes, EachApp.kikilog_options);
                        } else {
                            Kikilog.setUserLog(EachApp.kikilog_subid_NormalCaptureTimes, EachApp.kikilog_options);
                        }
                    } else if (CameraSetting.getInstance().getCurrentMode() == 2) {
                        LiveBulb_S1OffEEState.this.getActivity().dispatchKeyEvent(new KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 0, 1, 0, 0, 0, AppRoot.USER_KEYCODE.MOVIE_REC));
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

        public int pushedS1Key() {
            ExecutorCreator executorCreator = ExecutorCreator.getInstance();
            if (!executorCreator.isAElockedOnAutoFocus()) {
                String behavior = null;
                String focusMode = FocusModeController.getInstance().getValue();
                if ("af-s".equals(focusMode)) {
                    behavior = "af_woaf";
                } else if ("af-c".equals(focusMode)) {
                    behavior = "afc_woaf";
                }
                executorCreator.getSequence().autoFocus(null, behavior);
                return 1;
            }
            executorCreator.getSequence().autoFocus(null);
            return 1;
        }

        public int pushedS2Key() {
            ExecutorCreator.getInstance().getSequence().inquireKey(AppRoot.USER_KEYCODE.S2_ON);
            return 1;
        }
    }
}
