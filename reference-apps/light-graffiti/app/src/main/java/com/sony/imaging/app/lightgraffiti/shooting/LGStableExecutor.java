package com.sony.imaging.app.lightgraffiti.shooting;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.executor.StableExecutor;
import com.sony.imaging.app.lightgraffiti.util.AppLog;
import com.sony.imaging.app.lightgraffiti.util.LGPreviewEffect;

/* loaded from: classes.dex */
public class LGStableExecutor extends StableExecutor {
    private static final String TAG = LGStableExecutor.class.getSimpleName();

    @Override // com.sony.imaging.app.base.shooting.camera.executor.NormalExecutor, com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void myRelease() {
        Log.d(TAG, AppLog.getMethodName());
        LGPreviewEffect.getInstance().stopPreviewEffect();
        super.myRelease();
    }
}
