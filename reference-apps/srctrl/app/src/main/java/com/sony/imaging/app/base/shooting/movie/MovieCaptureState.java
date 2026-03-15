package com.sony.imaging.app.base.shooting.movie;

import android.util.Log;
import com.sony.imaging.app.base.BaseBackUpKey;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.common.SubLcdManager;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.HoldKeyServer;
import com.sony.imaging.app.srctrl.shooting.state.MovieRecStateEx;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.media.MediaRecorder;

/* loaded from: classes.dex */
public class MovieCaptureState extends MovieStateBase implements MediaRecorder.OnRecordListener, CameraEx.OpenCallback {
    public static final String FROM_INTERVAL = "Interval";
    public static final String FROM_MOVIE = "Movie";
    public static final String FROM_STILL = "Still";
    public static final String MOVIE_CAPTURE_STATE = "MOVIE_CAPTURE_STATE";
    public static final String MOVIE_REC_FROM = "MovieRecFrom";
    private static final String TAG = "MovieCaptureState";

    @Override // com.sony.imaging.app.base.shooting.movie.MovieStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        MovieShootingExecutor.setOnRecordListener(this);
        int recmode = ExecutorCreator.getInstance().getRecordingMode();
        if (8 == recmode || 2 == recmode) {
            ExecutorCreator.getInstance().setRecordingMode(recmode, this);
        } else {
            ExecutorCreator.getInstance().setRecordingMode(2, this);
        }
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED, 2);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.SLIDE_AF_MF, 1);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.SLIDE_AEL_FOCUS_CHANGED, 1);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.IRIS_DIAL_CHANGED, 1);
    }

    @Override // com.sony.imaging.app.base.shooting.movie.MovieStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED, 0);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.SLIDE_AF_MF, 0);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.SLIDE_AEL_FOCUS_CHANGED, 0);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.IRIS_DIAL_CHANGED, 0);
        MovieShootingExecutor.setOnRecordListener(null);
        if (Environment.isNewBizDeviceActionCam() && Environment.hasSubLcd()) {
            SubLcdManager.getInstance().holdDrawing(SubLcdManager.HOLD_MODE.OFF_REDRAW_IF_REQUESTED);
        }
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.shooting.movie.MovieStateBase
    public void finishRecording(boolean error) {
        if (Environment.isNewBizDeviceActionCam() && Environment.hasSubLcd()) {
            SubLcdManager.getInstance().holdDrawing(SubLcdManager.HOLD_MODE.OFF_REDRAW_IF_REQUESTED);
        }
        super.finishRecording(error);
    }

    public void onStarted(boolean isSuccess, MediaRecorder mr) {
        if (!isSuccess) {
            finishRecording(true);
            return;
        }
        if (Environment.isSupportingDefaultBootMode()) {
            ExecutorCreator creator = ExecutorCreator.getInstance();
            String bootMode = creator.recModeTodefaultBootMode(creator.getRecordingMode());
            BackUpUtil.getInstance().setPreference(BaseBackUpKey.ID_AK_DEFAULT_MODE, bootMode);
            Log.d(TAG, "set default boot mode : " + bootMode);
        }
        setNextState(MovieRecStateEx.STATE_NAME, this.data);
    }

    public void onStopped(MediaRecorder mr) {
        Log.i(TAG, "OnRecordListener.onStopped");
    }

    public void onReopened(CameraEx cameraEx) {
        Log.i(TAG, "onReopened");
        int state = MediaNotificationManager.getInstance().getMediaState();
        int type = MediaNotificationManager.getInstance().getInsertedMediaType();
        String[] m = ExecutorCreator.getInstance().getCautionMedia(state, type);
        CautionUtilityClass.getInstance().setModeAndMedia(getCautionMode().intValue(), m);
        startMovieRec();
        if (Environment.isNewBizDeviceActionCam() && Environment.hasSubLcd()) {
            SubLcdManager.getInstance().holdDrawing(SubLcdManager.HOLD_MODE.ON);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.movie.MovieStateBase
    protected void onLensChanged() {
        finishRecording();
    }
}
