package com.sony.imaging.app.srctrl.shooting.state;

import com.sony.imaging.app.base.shooting.MovieMenuState;
import com.sony.imaging.app.srctrl.util.StateController;

/* loaded from: classes.dex */
public class MovieMenuStateEx extends MovieMenuState {
    public static final String STATE_NAME = "Menu";
    public static final String tag = MovieMenuStateEx.class.getName();

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.base.menu.MenuState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        StateController stateController = StateController.getInstance();
        stateController.setState(this);
        stateController.setAppCondition(StateController.AppCondition.SHOOTING_MOVIE_REC_MENU);
        super.onResume();
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.base.menu.MenuState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        StateController stateController = StateController.getInstance();
        stateController.setState(this);
        stateController.setAppCondition(StateController.AppCondition.SHOOTING_MOVIE_REC);
        super.onPause();
    }
}
