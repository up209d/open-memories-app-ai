package com.sony.imaging.app.manuallenscompensation.shooting.state;

import android.os.Bundle;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.movie.MovieSaveState;
import com.sony.imaging.app.base.shooting.movie.MovieStateBase;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.HoldKeyServer;
import com.sony.imaging.app.manuallenscompensation.commonUtil.AppLog;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class OCMovieSaveState extends MovieSaveState {
    private static final String TAG = AppLog.getClassName();

    @Override // com.sony.imaging.app.base.shooting.movie.MovieStateBase
    public void transitionStillMode() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.data == null) {
            this.data = new Bundle();
        }
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED, 2);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.SLIDE_AF_MF, 1);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.SLIDE_AEL_FOCUS_CHANGED, 1);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.IRIS_DIAL_CHANGED, 1);
        ExecutorCreator.getInstance().setRecordingMode(1, new OCBackupToEE());
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    /* loaded from: classes.dex */
    protected class OCBackupToEE extends MovieStateBase.BackupToEE {
        protected OCBackupToEE() {
            super();
        }

        @Override // com.sony.imaging.app.base.shooting.movie.MovieStateBase.BackupToEE
        public void onReopened(CameraEx arg0) {
            AppLog.enter(OCMovieSaveState.TAG, AppLog.getMethodName());
            HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED, 0);
            HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.SLIDE_AF_MF, 0);
            HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.SLIDE_AEL_FOCUS_CHANGED, 0);
            HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.IRIS_DIAL_CHANGED, 0);
            AppLog.exit(OCMovieSaveState.TAG, AppLog.getMethodName());
        }
    }
}
