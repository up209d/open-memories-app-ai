package com.sony.imaging.app.base.shooting.movie;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import com.sony.imaging.app.base.common.BaseProperties;
import com.sony.imaging.app.base.menu.MenuTable;
import com.sony.imaging.app.base.shooting.IUserChanging;
import com.sony.imaging.app.base.shooting.StateBase;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.layout.IStableLayout;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.HoldKeyServer;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.media.MediaRecorder;

/* loaded from: classes.dex */
public class MovieRecState extends MovieStateBase implements MediaRecorder.OnRecordListener, IUserChanging {
    protected static final int API_VER_MOVIE_REC_QUICK_RESTART_SUPPORTED = 6;
    private static final String KEY_CODE = "keyCode";
    private static final String KEY_CODE_MODEDIAL = "keyCodeModeDial";
    public static final String MOVIE_REC_STATE = "MOVIE_REC_STATE";
    private static final String NORMAL_TRANSTION = "NormalTransition";
    public static final String ON_MOVIE_REC_STATE = "OnMovieRecState";
    private static final String TAG = "MovieRecState";
    private IStableLayout mLayout = null;

    @Override // com.sony.imaging.app.base.shooting.movie.MovieStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        stopObserveEVDial();
        stopObserveFocusModeDial();
        MovieShootingExecutor.setOnRecordListener(this);
        this.mLayout = (IStableLayout) getLayout("DefaultLayout");
    }

    @Override // com.sony.imaging.app.base.shooting.movie.MovieStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        MovieShootingExecutor.setOnRecordListener(null);
        startObserveEVDial();
        startObserveFocusModeDial();
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver
    public boolean handleMessage(Message msg) {
        String nextState = null;
        if (msg.obj instanceof Bundle) {
            Bundle bundle = (Bundle) msg.obj;
            if (bundle != null && bundle.containsKey("keyCode")) {
                bundle.remove("keyCode");
            }
            nextState = bundle.getString(MenuTable.NEXT_STATE);
        }
        if (!NORMAL_TRANSTION.equals(nextState)) {
            return false;
        }
        Log.i(TAG, "handleMessage");
        openLayout("DefaultLayout");
        openLayout(StateBase.FOCUS_LAYOUT);
        return true;
    }

    public void onStarted(boolean isSuccess, MediaRecorder mr) {
        Log.i(TAG, "OnRecordListener.onStarted");
        if (!isSuccess) {
            finishRecording(true);
        }
    }

    public void onStopped(MediaRecorder mr) {
        boolean intelligentActive = CameraSetting.getInstance().isIntelligentActiveOn();
        boolean isLoopRec = 8 == ExecutorCreator.getInstance().getRecordingMode();
        boolean isRecDisabledOnStreamWriting = BaseProperties.isRecDisabledOnStreamWriting();
        boolean isSubLcdActionCam = Environment.hasSubLcd() && Environment.isNewBizDeviceActionCam();
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.EV_DIAL_CHANGED, 0);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_CHANGED, 0);
        if (true == intelligentActive || true == isRecDisabledOnStreamWriting || true == isLoopRec || true == isSubLcdActionCam) {
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
