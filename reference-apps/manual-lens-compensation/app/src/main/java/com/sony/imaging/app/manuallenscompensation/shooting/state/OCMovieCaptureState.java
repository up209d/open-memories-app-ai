package com.sony.imaging.app.manuallenscompensation.shooting.state;

import android.os.Bundle;
import com.sony.imaging.app.base.shooting.movie.MovieCaptureState;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCUtil;

/* loaded from: classes.dex */
public class OCMovieCaptureState extends MovieCaptureState {
    private static final String MOVIE_STARTED = "MovieRec";

    @Override // com.sony.imaging.app.fw.State
    public void setNextState(String name, Bundle bundle) {
        if (MOVIE_STARTED.equals(name)) {
            OCUtil.getInstance().setLastCaptureMode(2);
        }
        super.setNextState(name, bundle);
    }

    @Override // com.sony.imaging.app.base.shooting.movie.MovieStateBase
    public void startMovieRec() {
        getHandler().post(this.mStartRunnable);
    }
}
