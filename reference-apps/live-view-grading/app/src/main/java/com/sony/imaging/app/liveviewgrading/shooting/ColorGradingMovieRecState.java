package com.sony.imaging.app.liveviewgrading.shooting;

import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.movie.MovieRecState;
import com.sony.imaging.app.liveviewgrading.menu.controller.ColorGradingController;
import com.sony.scalar.media.MediaRecorder;

/* loaded from: classes.dex */
public class ColorGradingMovieRecState extends MovieRecState {
    @Override // com.sony.imaging.app.base.shooting.movie.MovieRecState
    public void onStopped(MediaRecorder mr) {
        ColorGradingController.getInstance().setRecStopped(true);
        CameraNotificationManager.getInstance().requestNotify("movieStopped");
        super.onStopped(mr);
    }

    @Override // com.sony.imaging.app.base.shooting.movie.MovieRecState, com.sony.imaging.app.base.shooting.movie.MovieStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
    }
}
