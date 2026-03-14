package com.sony.imaging.app.base.shooting;

import android.os.Bundle;
import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.executor.IntervalRecExecutor;
import com.sony.imaging.app.util.ApoWrapper;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class IntervalRecRecordingState extends StateBase implements CameraEx.IntervalRecListener {
    public static final String INTERVALREC_RECORDING_STATE = "INTERVALREC_RECORDING_STATE";
    public static final String LOG_ON_PAUSE = "onPause";
    public static final String LOG_ON_RESUME = "onResume";
    public static final String LOG_ON_START_FAILED = "onStart failed";
    public static final String LOG_ON_START_SUCCESS = "onStart successed";
    public static final String LOG_ON_STOP = "onStop";
    public static final String NEXT_EE_STATE = "EE";
    public static final String TAG = "IntervalRecRecordingState";

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        IntervalRecExecutor.setIntervalRecListener(this);
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        Log.i(TAG, "onPause");
        IntervalRecExecutor.setIntervalRecListener(null);
        super.onPause();
    }

    public void onStart(boolean isSuccess, CameraEx cameraEx) {
        if (isSuccess) {
            Log.i(TAG, "onStart successed");
        } else {
            Log.i(TAG, "onStart failed");
        }
    }

    public void onStop(CameraEx cameraEx) {
        Log.i(TAG, "onStop");
        setNextState("EE", null);
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State
    public ApoWrapper.APO_TYPE getApoType() {
        return ApoWrapper.APO_TYPE.NONE;
    }

    @Override // com.sony.imaging.app.fw.State
    public void setNextState(String name, Bundle bundle) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        super.setNextState(name, bundle);
    }
}
