package com.sony.imaging.app.liveviewgrading.shooting;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.movie.MovieCaptureState;
import com.sony.imaging.app.liveviewgrading.menu.controller.ColorGradingController;

/* loaded from: classes.dex */
public class ColorGradingMovieCaptureState extends MovieCaptureState {
    @Override // com.sony.imaging.app.base.shooting.movie.MovieCaptureState, com.sony.imaging.app.base.shooting.movie.MovieStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
    }

    @Override // com.sony.imaging.app.base.shooting.movie.MovieStateBase
    public void startMovieRec() {
        Log.e("", "START");
        super.startMovieRec();
        ColorGradingController.getInstance().setRecStopped(false);
        CameraNotificationManager.getInstance().requestNotify("movieStarted");
    }

    @Override // com.sony.imaging.app.base.shooting.movie.MovieCaptureState, com.sony.imaging.app.base.shooting.movie.MovieStateBase
    public void finishRecording(boolean error) {
        ColorGradingController.getInstance().setRecStopped(true);
        CameraNotificationManager.getInstance().requestNotify("movieStopped");
        super.finishRecording(error);
    }
}
