package com.sony.imaging.app.srctrl.shooting.state;

import android.util.Log;
import com.sony.imaging.app.base.shooting.movie.MovieRecStandbyState;
import com.sony.imaging.app.srctrl.util.StateController;

/* loaded from: classes.dex */
public class MovieRecStandbyStateEx extends MovieRecStandbyState {
    public static final String STATE_NAME = "MovieRecStandby";
    public static final String tag = MovieRecStandbyStateEx.class.getName();

    @Override // com.sony.imaging.app.base.shooting.movie.MovieRecStandbyState, com.sony.imaging.app.base.shooting.movie.MovieStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        StateController stateController = StateController.getInstance();
        stateController.setState(this);
        if (!StateController.AppCondition.DIAL_INHIBIT.equals(stateController.getAppCondition())) {
            stateController.setAppCondition(StateController.AppCondition.SHOOTING_MOVIE_EE);
        }
        super.onResume();
        if (stateController.isWaitingBackTransition()) {
            Log.v(tag, "Change to network state because of the back transition flag.");
            stateController.changeToNetworkState();
            stateController.setGoBackFlag(false);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.movie.MovieRecStandbyState, com.sony.imaging.app.base.shooting.movie.MovieStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        StateController.AppCondition appCondition = StateController.getInstance().getAppCondition();
        if (!StateController.AppCondition.DIAL_INHIBIT.equals(appCondition) && !StateController.AppCondition.SHOOTING_MOVIE_REC.equals(appCondition)) {
            StateController.getInstance().setAppCondition(StateController.AppCondition.SHOOTING_MOVIE_INHIBIT);
        }
        super.onPause();
    }
}
