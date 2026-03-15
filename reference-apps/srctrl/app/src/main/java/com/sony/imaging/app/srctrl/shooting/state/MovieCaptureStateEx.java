package com.sony.imaging.app.srctrl.shooting.state;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.movie.MovieCaptureState;
import com.sony.imaging.app.base.shooting.movie.MovieStateBase;
import com.sony.imaging.app.srctrl.util.StateController;
import com.sony.imaging.app.srctrl.webapi.specific.MovieCaptureListenerNotifier;
import com.sony.imaging.app.srctrl.webapi.specific.ServerEventHandler;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.media.MediaRecorder;

/* loaded from: classes.dex */
public class MovieCaptureStateEx extends MovieCaptureState {
    public static final String STATE_NAME = "MovieCapture";
    public static final String tag = MovieCaptureStateEx.class.getName();
    private CameraNotificationManager mCamNtfy = CameraNotificationManager.getInstance();
    private MovieCaptureListenerNotifier notifier = null;
    private NotificationListener mNotificationListener = new NotificationListener() { // from class: com.sony.imaging.app.srctrl.shooting.state.MovieCaptureStateEx.1
        private String[] TAGS = {CameraNotificationManager.MOVIE_REC_START_EXECUTION_ERROR};

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.TAGS;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag2) {
            Log.e(tag2, "MOVIE_REC_START_EXECUTION_ERROR onNotify.");
            MovieCaptureStateEx.this.onCaptureStartError();
        }
    };

    @Override // com.sony.imaging.app.base.shooting.movie.MovieCaptureState, com.sony.imaging.app.base.shooting.movie.MovieStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        this.mCamNtfy.setNotificationListener(this.mNotificationListener);
        StateController stateController = StateController.getInstance();
        stateController.setState(this);
        stateController.setNotifierOnCaptureState();
        ServerEventHandler.getInstance().beginServerStatusChanged();
        super.onResume();
    }

    @Override // com.sony.imaging.app.base.shooting.movie.MovieCaptureState, com.sony.imaging.app.base.shooting.movie.MovieStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        ServerEventHandler.getInstance().commitServerStatusChanged();
        ServerEventHandler.getInstance().onServerStatusChanged();
        this.mCamNtfy.removeNotificationListener(this.mNotificationListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onCaptureStartError() {
        if (this.data == null || (!MovieCaptureState.FROM_STILL.equals(this.data.getString(MovieCaptureState.MOVIE_REC_FROM)) && !MovieStateBase.CHANGE_STILL.equals(this.data.getString(MovieStateBase.CHANGE_MODE)))) {
            setNextState("EE", this.data);
        }
        onCaptureNotify(1);
    }

    @Override // com.sony.imaging.app.base.shooting.movie.MovieCaptureState
    public void onStarted(boolean isSuccess, MediaRecorder mr) {
        Log.i(tag, "onStarted. isSuccess = " + isSuccess);
        if (isSuccess) {
            onCaptureNotify(0);
            StateController stateController = StateController.getInstance();
            stateController.setAppCondition(StateController.AppCondition.SHOOTING_MOVIE_WAIT_REC_START);
            ServerEventHandler.getInstance().commitServerStatusChanged();
            ServerEventHandler.getInstance().onServerStatusChanged();
            ServerEventHandler.getInstance().beginServerStatusChanged();
        } else {
            onCaptureStartError();
        }
        super.onStarted(isSuccess, mr);
    }

    private void onCaptureNotify(int status) {
        if (this.notifier != null) {
            this.notifier.onNotify(status);
            this.notifier = null;
        }
    }

    public void setNotifier(MovieCaptureListenerNotifier notifier) {
        Log.v(tag, "setNotifier in MovieCaptureStateEx");
        this.notifier = notifier;
    }
}
