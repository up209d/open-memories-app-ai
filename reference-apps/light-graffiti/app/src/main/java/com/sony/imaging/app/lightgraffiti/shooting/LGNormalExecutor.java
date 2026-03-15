package com.sony.imaging.app.lightgraffiti.shooting;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.executor.NormalExecutor;
import com.sony.imaging.app.lightgraffiti.util.AppLog;
import com.sony.imaging.app.lightgraffiti.util.LGPreviewEffect;

/* loaded from: classes.dex */
public class LGNormalExecutor extends NormalExecutor {
    private static final String TAG = LGNormalExecutor.class.getSimpleName();

    @Override // com.sony.imaging.app.base.shooting.camera.executor.NormalExecutor, com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void myRelease() {
        Log.d(TAG, AppLog.getMethodName());
        LGPreviewEffect.getInstance().stopPreviewEffect();
        super.myRelease();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor, com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void takePicture(int shutterType) {
        super.takePicture(2);
    }

    public void myLockAutoFocus(boolean isLock) {
        lockAutoFocus(isLock);
    }
}
