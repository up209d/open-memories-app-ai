package com.sony.imaging.app.base.shooting.movie;

import android.util.Log;
import com.sony.imaging.app.srctrl.webapi.definition.Name;

/* loaded from: classes.dex */
public class MovieStableExecutor extends MovieShootingExecutor {
    private static final String TAG = "MovieStableExecutor";

    @Override // com.sony.imaging.app.base.shooting.movie.MovieShootingExecutor, com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void startMovieRec() {
        Log.i(TAG, Name.START_MOVIE_REC);
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
