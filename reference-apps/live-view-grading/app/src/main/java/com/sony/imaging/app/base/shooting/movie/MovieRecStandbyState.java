package com.sony.imaging.app.base.shooting.movie;

import android.os.Bundle;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.shooting.IUserChanging;
import com.sony.imaging.app.base.shooting.StateBase;
import com.sony.imaging.app.base.shooting.layout.IStableLayout;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.HoldKeyServer;
import com.sony.imaging.app.fw.RunStatus;
import com.sony.imaging.app.util.Environment;

/* loaded from: classes.dex */
public class MovieRecStandbyState extends MovieStateBase implements IUserChanging {
    private static final String KEY_CODE = "keyCode";
    private static final String TAG = "MovieRecStandbyState";
    private IStableLayout mLayout = null;

    @Override // com.sony.imaging.app.base.shooting.StateBase
    protected int getMyBeepPattern() {
        return 6;
    }

    @Override // com.sony.imaging.app.base.shooting.movie.MovieStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        this.mLayout = (IStableLayout) getLayout(StateBase.DEFAULT_LAYOUT);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED, 0);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.SLIDE_AF_MF, 0);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.SLIDE_AEL_FOCUS_CHANGED, 0);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.IRIS_DIAL_CHANGED, 0);
        Bundle b = this.data;
        if (b != null && b.containsKey("keyCode")) {
            if (624 == b.getInt("keyCode")) {
                transitionStillMode();
            }
            b.remove("keyCode");
        }
    }

    @Override // com.sony.imaging.app.base.shooting.movie.MovieStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        rememberDefaultBootMode();
    }

    protected void rememberDefaultBootMode() {
        if (Environment.isSupportingDefaultBootMode() && 2 == RunStatus.getStatus()) {
            BaseApp.rememberDefaultBootModeOnPullingBack();
        }
    }

    @Override // com.sony.imaging.app.base.shooting.IUserChanging
    public void changeAperture() {
        this.mLayout.setUserChanging(0);
    }

    @Override // com.sony.imaging.app.base.shooting.IUserChanging
    public void changeShutterSpeed() {
        this.mLayout.setUserChanging(1);
    }

    @Override // com.sony.imaging.app.base.shooting.IUserChanging
    public void changeExposure() {
        this.mLayout.setUserChanging(2);
    }

    @Override // com.sony.imaging.app.base.shooting.IUserChanging
    public void changeOther() {
        this.mLayout.setUserChanging(-1);
    }

    @Override // com.sony.imaging.app.base.shooting.IUserChanging
    public void changeISOSensitivity() {
        this.mLayout.setUserChanging(3);
    }
}
