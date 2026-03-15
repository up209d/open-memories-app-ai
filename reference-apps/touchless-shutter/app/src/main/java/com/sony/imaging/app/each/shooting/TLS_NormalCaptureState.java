package com.sony.imaging.app.each.shooting;

import android.util.Log;
import com.sony.imaging.app.base.shooting.NormalCaptureState;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.each.AppLog;
import com.sony.imaging.app.each.EachApp;
import com.sony.scalar.hardware.avio.DisplayManager;
import com.sony.scalar.sysutil.ScalarProperties;

/* loaded from: classes.dex */
public class TLS_NormalCaptureState extends NormalCaptureState {
    OnDisplayEventListener mDispEventListener;
    private final String TAG = "TLS_NormalCaptureState";
    DisplayManager mDisplayManager = null;

    @Override // com.sony.imaging.app.base.shooting.NormalCaptureState, com.sony.imaging.app.base.shooting.CaptureStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        Log.d("TLS_NormalCaptureState", "★★★★★onResume()");
        AppLog.enter("TLS_NormalCaptureState", AppLog.getMethodName());
        this.mDispEventListener = new OnDisplayEventListener();
        this.mDisplayManager = new DisplayManager();
        this.mDisplayManager.setDisplayStatusListener(this.mDispEventListener);
        super.onResume();
        AppLog.exit("TLS_NormalCaptureState", AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.CaptureStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        Log.d("TLS_NormalCaptureState", "★★★★★onPause()");
        AppLog.enter("TLS_NormalCaptureState", AppLog.getMethodName());
        this.mDisplayManager.releaseDisplayStatusListener();
        this.mDisplayManager.finish();
        this.mDispEventListener = null;
        this.mDisplayManager = null;
        super.onPause();
        AppLog.exit("TLS_NormalCaptureState", AppLog.getMethodName());
    }

    /* loaded from: classes.dex */
    class OnDisplayEventListener implements DisplayManager.DisplayEventListener {
        OnDisplayEventListener() {
        }

        public void onDeviceStatusChanged(int eventId) {
            Log.i("TLS_NormalCaptureState", "★★ onDeviceStatusChanged");
            if (4096 == eventId) {
                String str = TLS_NormalCaptureState.this.mDisplayManager.getActiveDevice();
                if (str != null) {
                    Log.i("TLS_NormalCaptureState", "★★★★DEVICE not null");
                    if (str.equals("DEVICE_ID_FINDER")) {
                        Log.i("TLS_NormalCaptureState", "★★★★DEVICE_ID_FINDER");
                        Log.d("TLS_NormalCaptureState", "★★★★CancelAF, CancelTakePicture");
                        TouchLessShutter_go();
                        return;
                    } else if (str.equals("DEVICE_ID_HDMI")) {
                        Log.i("TLS_NormalCaptureState", "★★★★DEVICE_ID_HDMI");
                        return;
                    } else {
                        if (str.equals("DEVICE_ID_PANEL")) {
                            Log.i("TLS_NormalCaptureState", "★★★★DEVICE_ID_PANEL");
                            return;
                        }
                        return;
                    }
                }
                Log.e("TLS_NormalCaptureState", "★★★★DEVICE is null");
                Log.e("TLS_NormalCaptureState", "★★★★\u3000EVF?");
                if (Double.parseDouble(ScalarProperties.getString("version.platform")) > 2.0d) {
                    Log.d("TLS_NormalCaptureState", "★★★★\u3000DEVICE_ID_FINDER?");
                    TouchLessShutter_go();
                }
            }
        }

        private void TouchLessShutter_go() {
            Log.d("TLS_NormalCaptureState", "★★★★\u3000Canceling S1&S2");
            ExecutorCreator.getInstance().getSequence().cancelAutoFocus();
            ExecutorCreator.getInstance().getSequence().cancelTakePicture();
            EachApp.ExposingByTouchLessShutter = false;
        }
    }
}
