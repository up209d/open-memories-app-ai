package com.sony.imaging.app.each.shooting;

import android.util.Log;
import com.sony.imaging.app.base.shooting.DevelopmentState;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.each.AppLog;
import com.sony.imaging.app.each.EachApp;
import com.sony.scalar.hardware.avio.DisplayManager;
import com.sony.scalar.sysutil.ScalarProperties;

/* loaded from: classes.dex */
public class TLS_DevelopmentState extends DevelopmentState {
    OnDisplayEventListener mDispEventListener;
    private final String TAG = "TouchLessShutter_DevelopmentState";
    DisplayManager mDisplayManager = null;

    @Override // com.sony.imaging.app.base.shooting.DevelopmentState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        Log.d("TouchLessShutter_DevelopmentState", "★★★★★onResume()");
        AppLog.enter("TouchLessShutter_DevelopmentState", AppLog.getMethodName());
        this.mDispEventListener = new OnDisplayEventListener();
        this.mDisplayManager = new DisplayManager();
        this.mDisplayManager.setDisplayStatusListener(this.mDispEventListener);
        super.onResume();
        AppLog.exit("TouchLessShutter_DevelopmentState", AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.DevelopmentState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        Log.d("TouchLessShutter_DevelopmentState", "★★★★★onPause()");
        AppLog.enter("TouchLessShutter_DevelopmentState", AppLog.getMethodName());
        ExecutorCreator.getInstance().getSequence().cancelAutoFocus();
        ExecutorCreator.getInstance().getSequence().cancelTakePicture();
        this.mDisplayManager.releaseDisplayStatusListener();
        this.mDisplayManager.finish();
        this.mDispEventListener = null;
        this.mDisplayManager = null;
        EachApp.ExposingByTouchLessShutter = false;
        super.onPause();
        AppLog.exit("TouchLessShutter_DevelopmentState", AppLog.getMethodName());
    }

    /* loaded from: classes.dex */
    class OnDisplayEventListener implements DisplayManager.DisplayEventListener {
        OnDisplayEventListener() {
        }

        public void onDeviceStatusChanged(int eventId) {
            Log.i("TouchLessShutter_DevelopmentState", "★★ onDeviceStatusChanged");
            if (!EachApp.ExposingByTouchLessShutter) {
                Log.d("TouchLessShutter_DevelopmentState", "Not touchless shutter capture");
                return;
            }
            if (4096 == eventId) {
                String str = TLS_DevelopmentState.this.mDisplayManager.getActiveDevice();
                if (str != null) {
                    Log.i("TouchLessShutter_DevelopmentState", "★★★★DEVICE not null");
                    if (str.equals("DEVICE_ID_FINDER")) {
                        Log.i("TouchLessShutter_DevelopmentState", "★★★★DEVICE_ID_FINDER");
                        Log.d("TouchLessShutter_DevelopmentState", "★★★★CancelAF, CancelTakePicture");
                        TouchLessShutter_go();
                        return;
                    } else if (str.equals("DEVICE_ID_HDMI")) {
                        Log.i("TouchLessShutter_DevelopmentState", "★★★★DEVICE_ID_HDMI");
                        return;
                    } else {
                        if (str.equals("DEVICE_ID_PANEL")) {
                            Log.i("TouchLessShutter_DevelopmentState", "★★★★DEVICE_ID_PANEL");
                            return;
                        }
                        return;
                    }
                }
                Log.e("TouchLessShutter_DevelopmentState", "★★★★DEVICE is null");
                Log.e("TouchLessShutter_DevelopmentState", "★★★★\u3000EVF?");
                if (Double.parseDouble(ScalarProperties.getString("version.platform")) > 2.0d) {
                    Log.d("TouchLessShutter_DevelopmentState", "★★★★\u3000DEVICE_ID_FINDER?");
                    TouchLessShutter_go();
                }
            }
        }

        private void TouchLessShutter_go() {
            Log.d("TouchLessShutter_DevelopmentState", "★★★★\u3000Canceling S1&S2");
            CameraNotificationManager.getInstance().requestNotify(CameraNotificationManager.PROCESSING_PROGRESS, Double.valueOf(0.0d));
            ExecutorCreator.getInstance().getSequence().cancelAutoFocus();
            ExecutorCreator.getInstance().getSequence().cancelTakePicture();
            EachApp.ExposingByTouchLessShutter = false;
        }
    }
}
