package com.sony.imaging.app.base.shooting.movie;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import com.sony.imaging.app.base.common.EVDialDetector;
import com.sony.imaging.app.base.menu.MenuTable;
import com.sony.imaging.app.base.shooting.IUserChanging;
import com.sony.imaging.app.base.shooting.StateBase;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.base.shooting.layout.StableLayout;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.scalar.media.MediaRecorder;

/* loaded from: classes.dex */
public class MovieRecState extends MovieStateBase implements MediaRecorder.OnRecordListener, IUserChanging {
    private static final String KEY_CODE = "keyCode";
    private static final String KEY_CODE_MODEDIAL = "keyCodeModeDial";
    private static final String NORMAL_TRANSTION = "NormalTransition";
    private static final String TAG = "MovieRecState";
    private StableLayout mLayout = null;

    @Override // com.sony.imaging.app.base.shooting.movie.MovieStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        MovieShootingExecutor.setOnRecordListener(this);
        this.mLayout = (StableLayout) getLayout(StateBase.DEFAULT_LAYOUT);
    }

    @Override // com.sony.imaging.app.base.shooting.movie.MovieStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        MovieShootingExecutor.setOnRecordListener(null);
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver
    public boolean handleMessage(Message msg) {
        String nextState = null;
        if (msg.obj instanceof Bundle) {
            Bundle bundle = (Bundle) msg.obj;
            if (bundle != null && bundle.containsKey("keyCode")) {
                if (620 == bundle.getInt("keyCode")) {
                    ExposureCompensationController.getInstance().setEvDialValue(EVDialDetector.getEVDialPosition());
                }
                bundle.remove("keyCode");
            }
            nextState = bundle.getString(MenuTable.NEXT_STATE);
        }
        if (!NORMAL_TRANSTION.equals(nextState)) {
            return false;
        }
        openLayout(StateBase.DEFAULT_LAYOUT);
        return true;
    }

    public void onStarted(boolean isSuccess, MediaRecorder mr) {
        Log.i(TAG, "OnRecordListener.onStarted");
    }

    public void onStopped(MediaRecorder mr) {
        if (1 == 0) {
            setNextState("MovieSave", this.data);
        } else {
            finishRecording();
        }
    }

    @Override // com.sony.imaging.app.base.shooting.movie.MovieStateBase
    protected void onLensChanged() {
        stopMovieRec();
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

    public void turnModeDail() {
        if (this.data == null) {
            this.data = new Bundle();
        }
        this.data.putInt(KEY_CODE_MODEDIAL, AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED);
    }
}
