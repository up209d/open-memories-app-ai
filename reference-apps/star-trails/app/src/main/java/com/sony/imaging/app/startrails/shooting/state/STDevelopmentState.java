package com.sony.imaging.app.startrails.shooting.state;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.EventParcel;
import com.sony.imaging.app.base.shooting.AutoReviewState;
import com.sony.imaging.app.base.shooting.DevelopmentState;
import com.sony.imaging.app.startrails.util.STUtility;
import com.sony.scalar.hardware.avio.DisplayManager;

/* loaded from: classes.dex */
public class STDevelopmentState extends DevelopmentState {
    private static final String NEXT_STATE = "MFModeCheckState";

    @Override // com.sony.imaging.app.base.shooting.DevelopmentState, com.sony.imaging.app.fw.KeyReceiver
    public boolean handleMessage(Message msg) {
        return false;
    }

    @Override // com.sony.imaging.app.base.shooting.DevelopmentState, com.sony.imaging.app.fw.State
    public void setNextState(String name, Bundle bundle) {
        if (!STUtility.getInstance().isPreTakePictureTestShot() && name.equals("EE")) {
            name = "MFModeCheckState";
        }
        super.setNextState(name, bundle);
    }

    @Override // com.sony.imaging.app.base.shooting.DevelopmentState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        if (STUtility.getInstance().isPreTakePictureTestShot()) {
            Log.i("kamata", "hereA");
            STUtility.getInstance().setPreTakePictureTestShot(false);
            switchToPlayback();
        }
    }

    private void switchToPlayback() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("PRETAKE_SHOOTING", true);
        Activity appRoot = getActivity();
        ((BaseApp) appRoot).changeApp(BaseApp.APP_PLAY, bundle);
        this.data = null;
    }

    private void moveToPlaybackZoom(EventParcel event) {
        Bundle b = new Bundle();
        Bundle data = new Bundle();
        DisplayModeObserver dispObserver = DisplayModeObserver.getInstance();
        DisplayManager.VideoRect v = (DisplayManager.VideoRect) dispObserver.getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE);
        Rect r = new Rect(v.pxLeft, v.pxTop, v.pxRight, v.pxBottom);
        data.putParcelable(EventParcel.KEY_TOUCH, new EventParcel(EventParcel.TOUCH_UP, null, null, true, -1.0f, -1.0f, -1.0f, -1.0f, r));
        b.putParcelable(AutoReviewState.STATE_NAME, data);
        Activity appRoot = getActivity();
        ((BaseApp) appRoot).changeApp(BaseApp.APP_PLAY, b);
    }
}
