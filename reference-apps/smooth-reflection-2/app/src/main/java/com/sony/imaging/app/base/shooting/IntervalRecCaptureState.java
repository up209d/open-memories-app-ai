package com.sony.imaging.app.base.shooting;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.camera.executor.IntervalRecExecutor;
import com.sony.imaging.app.util.ApoWrapper;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class IntervalRecCaptureState extends StateBase implements CameraEx.IntervalRecListener, CameraEx.ShutterListener {
    public static final String INTVLREC_RECORDING = "IntervalRecRecording";
    public static final String LOG_ON_PAUSE = "onPause";
    public static final String LOG_ON_RESUME = "onResume";
    public static final String LOG_ON_SHUTTER = "onShutter";
    public static final String LOG_ON_START_FAILED = "onStart failed";
    public static final String LOG_ON_START_SUCCESS = "onStart successed";
    public static final String LOG_ON_STOP = "onStop";
    public static final String NEXT_EE_STATE = "EE";
    public static final String TAG = "IntervalRecCaptureState";
    public static final String TAKE_PICTURE_BY_REMOTE = "TakePicByRemote";

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        BaseShootingExecutor executor = ExecutorCreator.getInstance().getSequence();
        IntervalRecExecutor.setIntervalRecListener(this);
        IntervalRecExecutor.setShutterListener(this);
        if (isTakePictureByRemote()) {
            executor.takePicture(1);
        } else {
            executor.takePicture();
        }
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        Log.i(TAG, "onPause");
        IntervalRecExecutor.setIntervalRecListener(null);
        IntervalRecExecutor.setShutterListener(null);
        super.onPause();
    }

    public void onStart(boolean isSuccess, CameraEx cameraEx) {
        if (isSuccess) {
            Log.i(TAG, "onStart successed");
            setNextState(INTVLREC_RECORDING, this.data);
        } else {
            Log.i(TAG, "onStart failed");
            setNextState("EE", this.data);
        }
    }

    public void onStop(CameraEx cameraEx) {
        Log.i(TAG, "onStop");
        setNextState("EE", this.data);
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase
    protected int getMyBeepPattern() {
        return 6;
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State
    public ApoWrapper.APO_TYPE getApoType() {
        return ApoWrapper.APO_TYPE.NONE;
    }

    protected boolean isTakePictureByRemote() {
        if (this.data == null) {
            return false;
        }
        boolean ret = this.data.getBoolean("TakePicByRemote");
        return ret;
    }

    public void onShutter(int status, CameraEx cameraEx) {
        Log.i(TAG, LOG_ON_SHUTTER);
        BaseShootingExecutor executor = ExecutorCreator.getInstance().getSequence();
        if (!(executor instanceof IntervalRecExecutor) && status != 0) {
            onStart(false, cameraEx);
        }
    }
}
