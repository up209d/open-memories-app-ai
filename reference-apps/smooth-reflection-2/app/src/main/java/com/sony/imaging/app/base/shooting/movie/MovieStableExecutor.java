package com.sony.imaging.app.base.shooting.movie;

import android.util.Log;

/* loaded from: classes.dex */
public class MovieStableExecutor extends MovieShootingExecutor {
    private static final String TAG = "MovieStableExecutor";

    @Override // com.sony.imaging.app.base.shooting.movie.MovieShootingExecutor, com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void startMovieRec() {
        Log.i(TAG, "startMovieRec");
    }

    @Override // com.sony.imaging.app.base.shooting.movie.MovieShootingExecutor, com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void stopMovieRec() {
        Log.i(TAG, "stopMovieRecMode");
    }

    @Override // com.sony.imaging.app.base.shooting.movie.MovieShootingExecutor, com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public boolean needToBeStable(int flg) {
        return (flg & (-3)) != 0;
    }

    @Override // com.sony.imaging.app.base.shooting.movie.MovieShootingExecutor, com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    protected void myRelease() {
        tryRelease();
    }
}
