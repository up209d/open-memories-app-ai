package com.sony.imaging.app.each.shooting;

import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.movie.MovieRecState;
import com.sony.imaging.app.each.AppLog;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.scalar.hardware.avio.DisplayManager;
import com.sony.scalar.sysutil.ScalarProperties;

/* loaded from: classes.dex */
public class TLS_MovieRecState extends MovieRecState {
    private static final String TAG = "TLS_MovieRecState";
    private String mCurrentDisplay;
    OnDisplayEventListener mDispEventListener;
    DisplayManager mDisplayManager = null;

    @Override // com.sony.imaging.app.base.shooting.movie.MovieRecState, com.sony.imaging.app.base.shooting.movie.MovieStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(TAG, AppLog.getMethodName());
        super.onResume();
        this.mDispEventListener = new OnDisplayEventListener();
        this.mDisplayManager = new DisplayManager();
        this.mDisplayManager.setDisplayStatusListener(this.mDispEventListener);
        this.mCurrentDisplay = this.mDisplayManager.getActiveDevice();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.movie.MovieRecState, com.sony.imaging.app.base.shooting.movie.MovieStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
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
            Log.i(TLS_MovieRecState.TAG, "★★ onDeviceStatusChanged");
            if (4096 == eventId) {
                String str = TLS_MovieRecState.this.mDisplayManager.getActiveDevice();
                if (str != null) {
                    Log.i(TLS_MovieRecState.TAG, "★★★★DEVICE not null");
                    if (TLS_MovieRecState.this.mDisplayManager.getActiveDevice().equals("DEVICE_ID_FINDER")) {
                        Log.i(TLS_MovieRecState.TAG, "★★★★DEVICE_ID_FINDER");
                        TouchLessShutter_go();
                    } else if (str.equals("DEVICE_ID_HDMI")) {
                        Log.i(TLS_MovieRecState.TAG, "★★★★DEVICE_ID_HDMI");
                    } else if (str.equals("DEVICE_ID_PANEL")) {
                        Log.i(TLS_MovieRecState.TAG, "★★★★DEVICE_ID_PANEL");
                    }
                    TLS_MovieRecState.this.mCurrentDisplay = TLS_MovieRecState.this.mDisplayManager.getActiveDevice();
                    Log.i(TLS_MovieRecState.TAG, "★★★★mCurrentDisplay is updated: " + TLS_MovieRecState.this.mCurrentDisplay.toString());
                    return;
                }
                Log.e(TLS_MovieRecState.TAG, "★★★★DEVICE is null");
                if (Double.parseDouble(ScalarProperties.getString("version.platform")) > 2.0d) {
                    Log.d(TLS_MovieRecState.TAG, "★★★★\u3000DEVICE_ID_FINDER?");
                    TouchLessShutter_go();
                }
            }
        }

        private void TouchLessShutter_go() {
            if ("DEVICE_ID_PANEL".equals(TLS_MovieRecState.this.mCurrentDisplay)) {
                Log.d(TLS_MovieRecState.TAG, "★★★★ Push Rec");
                if (CautionUtilityClass.getInstance().getCurrentCautionData() == null) {
                    TLS_MovieRecState.this.getActivity().dispatchKeyEvent(new KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 0, 1, 0, 0, 0, AppRoot.USER_KEYCODE.MOVIE_REC));
                    int[] currentCautionId = CautionUtilityClass.getInstance().CurrentCautionId();
                    for (int i : currentCautionId) {
                        if (i == 1399) {
                            ExecutorCreator.getInstance().getSequence().cancelTakePicture();
                            ExecutorCreator.getInstance().getSequence().cancelAutoFocus();
                        }
                    }
                }
            }
        }
    }
}
