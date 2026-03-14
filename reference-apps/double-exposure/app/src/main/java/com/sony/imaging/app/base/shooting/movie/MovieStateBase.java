package com.sony.imaging.app.base.shooting.movie;

import android.os.Bundle;
import android.util.Log;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.shooting.StateBase;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.MovieFormatController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.HoldKeyServer;
import com.sony.imaging.app.fw.RunStatus;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class MovieStateBase extends StateBase {
    public static final String CHANGE_MODE = "ChangeMode";
    public static final String CHANGE_STILL = "ChangeStillMode";
    private static final String TAG = "MovieStateBase";
    private static final String[] TAGS = {CameraNotificationManager.DEVICE_LENS_CHANGED};
    protected ExecutionErrorListener mErrorListener = new ExecutionErrorListener();
    protected String mRecFrom = null;
    protected Runnable mStartRunnable = new Runnable() { // from class: com.sony.imaging.app.base.shooting.movie.MovieStateBase.1
        @Override // java.lang.Runnable
        public void run() {
            ExecutorCreator ec = ExecutorCreator.getInstance();
            if (2 == ec.getRecordingMode() && !ec.isRecordingModeChanging()) {
                ec.getSequence().startMovieRec();
            }
        }
    };
    protected CautionUtilityClass.triggerCautionCallback mCautionCallback = new CautionCallback();
    private NotificationListener mLensChangedListener = new LensChangedListener();

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        if (this.data != null) {
            this.mRecFrom = this.data.getString(MovieCaptureState.MOVIE_REC_FROM);
        }
        super.onResume();
        CameraNotificationManager.getInstance().setNotificationListener(this.mErrorListener, true);
        CameraNotificationManager.getInstance().setNotificationListener(this.mLensChangedListener);
        openLayout(StateBase.FOCUS_LAYOUT);
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        getHandler().removeCallbacks(this.mStartRunnable);
        getHandler().removeCallbacks((Runnable) this.mCautionCallback);
        CautionUtilityClass.getInstance().unregisterCallbackTriggerDisapper(this.mCautionCallback);
        CameraNotificationManager.getInstance().removeNotificationListener(this.mLensChangedListener);
        CameraNotificationManager.getInstance().removeNotificationListener(this.mErrorListener);
        super.onPause();
        closeLayout(StateBase.FOCUS_LAYOUT);
        if (4 != RunStatus.getStatus() && MovieCaptureState.FROM_STILL.equals(this.mRecFrom)) {
            ExecutorCreator.getInstance().setRecordingModeToBackup(1);
        }
        this.mRecFrom = null;
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State
    public Integer getCautionMode() {
        int ret = 6;
        String format = MovieFormatController.getInstance().getValue(MovieFormatController.MOVIE_FORMAT);
        if (MovieFormatController.AVCHD.equals(format)) {
            ret = 6;
        } else if (MovieFormatController.MP4.equals(format)) {
            ret = 7;
        }
        return Integer.valueOf(ret);
    }

    public void startMovieRec() {
        getHandler().post(this.mStartRunnable);
    }

    public void stopMovieRec() {
        ExecutorCreator ec = ExecutorCreator.getInstance();
        if (2 == ec.getRecordingMode() && !ec.isRecordingModeChanging()) {
            ec.getSequence().stopMovieRec();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class ExecutionErrorListener implements NotificationListener {
        protected final String[] TAGS = {CameraNotificationManager.MOVIE_REC_START_EXECUTION_ERROR};

        protected ExecutionErrorListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.TAGS;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            MovieStateBase.this.finishRecording(true);
        }
    }

    /* loaded from: classes.dex */
    protected class CautionCallback implements CautionUtilityClass.triggerCautionCallback, Runnable {
        protected CautionCallback() {
        }

        @Override // com.sony.imaging.app.base.caution.CautionUtilityClass.triggerCautionCallback
        public void onCallback() {
            MovieStateBase.this._finishRecording();
        }

        @Override // java.lang.Runnable
        public void run() {
            if (CautionUtilityClass.getInstance().getCurrentCautionData() == null) {
                Log.i(MovieStateBase.TAG, toString() + " calls _finishRecording() because the caution was not displayed.");
                MovieStateBase.this._finishRecording();
            }
        }
    }

    public void finishRecording(boolean error) {
        if (true == error) {
            CautionUtilityClass.getInstance().registerCallbackTriggerDisapper(this.mCautionCallback);
            getHandler().postDelayed((Runnable) this.mCautionCallback, 200L);
        } else {
            _finishRecording();
        }
    }

    public void finishRecording() {
        finishRecording(false);
    }

    protected void _finishRecording() {
        getHandler().removeCallbacks((Runnable) this.mCautionCallback);
        if (this.data != null && (MovieCaptureState.FROM_STILL.equals(this.data.getString(MovieCaptureState.MOVIE_REC_FROM)) || CHANGE_STILL.equals(this.data.getString(CHANGE_MODE)))) {
            transitionStillMode();
        } else {
            setNextState("EE", this.data);
        }
    }

    public void transitionStillMode() {
        if (this.data == null) {
            this.data = new Bundle();
        }
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED, 2);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.EV_DIAL_CHANGED, 1);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.SLIDE_AF_MF, 1);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.SLIDE_AEL_FOCUS_CHANGED, 1);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_CHANGED, 1);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.IRIS_DIAL_CHANGED, 1);
        ExecutorCreator.getInstance().setRecordingMode(1, new BackupToEE());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class BackupToEE implements CameraEx.OpenCallback {
        protected BackupToEE() {
        }

        public void onReopened(CameraEx arg0) {
            MovieStateBase.this.setNextState("EE", MovieStateBase.this.data);
            HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.EV_DIAL_CHANGED, 0);
            HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED, 0);
            HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.SLIDE_AF_MF, 0);
            HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.SLIDE_AEL_FOCUS_CHANGED, 0);
            HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_CHANGED, 0);
            HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.IRIS_DIAL_CHANGED, 0);
        }
    }

    protected void onLensChanged() {
    }

    /* loaded from: classes.dex */
    private class LensChangedListener implements NotificationListener {
        private CameraEx.LensInfo mLens = CameraSetting.getInstance().getLensInfo();

        LensChangedListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return MovieStateBase.TAGS;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (CameraNotificationManager.DEVICE_LENS_CHANGED.equals(tag)) {
                CameraEx.LensInfo lens = CameraSetting.getInstance().getLensInfo();
                boolean previous = this.mLens == null;
                boolean current = lens == null;
                this.mLens = lens;
                if (previous != current) {
                    MovieStateBase.this.onLensChanged();
                }
            }
        }
    }
}
