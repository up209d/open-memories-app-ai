package com.sony.imaging.app.portraitbeauty.shooting.camera.executor;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.executor.NormalExecutor;

/* loaded from: classes.dex */
public class PBNormalExecutor extends NormalExecutor {
    private static final String LOG_CANCEL_TAKE_PICTURE = "cancelTakePicture";
    private static final String TAG = "PBNormalExecutor";

    @Override // com.sony.imaging.app.base.shooting.camera.executor.NormalExecutor, com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor
    public void myTakePicture() {
        setTempRemoteControl(1);
        enableTermination(false);
        this.mAdapter.takePicture();
        if (CameraSetting.getInstance().isShutterSpeedBulb()) {
            isBulbOpened = true;
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.NormalExecutor, com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor
    public void myCancelTakePicture() {
        Log.d(TAG, LOG_CANCEL_TAKE_PICTURE);
        sCameraEx.cancelTakePicture();
        if (isBulbOpened) {
            isBulbOpened = false;
            updateFocusLock();
        }
        setTempRemoteControl(0);
    }
}
