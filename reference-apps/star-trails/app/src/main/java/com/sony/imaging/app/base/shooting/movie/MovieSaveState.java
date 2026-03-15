package com.sony.imaging.app.base.shooting.movie;

import android.util.Log;
import com.sony.imaging.app.base.common.BaseProperties;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.HoldKeyServer;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.media.MediaRecorder;

/* loaded from: classes.dex */
public class MovieSaveState extends MovieStateBase implements MediaRecorder.OnStreamWriteListener {
    private static final String TAG = "MovieSaveState";

    @Override // com.sony.imaging.app.base.shooting.movie.MovieStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        MovieShootingExecutor.setOnStreamWriteListener(this);
        openLayout(getLayout());
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED, 2);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.SLIDE_AF_MF, 1);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.SLIDE_AEL_FOCUS_CHANGED, 1);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.IRIS_DIAL_CHANGED, 1);
    }

    @Override // com.sony.imaging.app.base.shooting.movie.MovieStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        Log.i(TAG, "MovieSaveState onPause");
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED, 0);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.SLIDE_AF_MF, 0);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.SLIDE_AEL_FOCUS_CHANGED, 0);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.IRIS_DIAL_CHANGED, 0);
        closeLayout(getLayout());
        MovieShootingExecutor.setOnStreamWriteListener(null);
        super.onPause();
    }

    protected String getLayout() {
        boolean isSubLcdActionCam = Environment.hasSubLcd() && Environment.isNewBizDeviceActionCam();
        if (isSubLcdActionCam) {
            boolean isRecDisabledOnStreamWriting = BaseProperties.isRecDisabledOnStreamWriting();
            boolean isLoopRec = 8 == ExecutorCreator.getInstance().getRecordingMode();
            return (isRecDisabledOnStreamWriting || isLoopRec) ? "MovieSaveLayout" : "StableLayout";
        }
        boolean intelligentActive = CameraSetting.getInstance().isIntelligentActiveOn();
        return intelligentActive ? "MovieSaveLayout" : "StableLayout";
    }

    public void onStarted(MediaRecorder mr) {
        Log.i(TAG, "OnStreamWriteListener.onStarted");
    }

    public void onCompleted(MediaRecorder arg0) {
        Log.i(TAG, "OnStreamWriteListener.onCompleted");
        finishRecording();
    }
}
