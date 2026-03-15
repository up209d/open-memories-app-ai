package com.sony.imaging.app.smoothreflection.playback;

import android.view.KeyEvent;
import com.sony.imaging.app.base.playback.PlayRootContainer;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.smoothreflection.common.AppLog;
import com.sony.imaging.app.smoothreflection.common.SmoothReflectionConstants;

/* loaded from: classes.dex */
public class SmoothRefelctionPlayRootContainer extends PlayRootContainer {
    public static final String ID_DELETE_MULTIPLE = "ID_DELETE_MULTIPLE";
    private static final String TAG = AppLog.getClassName();
    public static boolean playback_pushed_S1_flg = false;

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer
    public String getStartFunction() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return super.getStartFunction();
    }

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (event.getScanCode() == 620) {
            SmoothReflectionConstants.slastStateExposureCompansasion = 0;
            SmoothReflectionConstants.sISEVDIALTURN = true;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return super.onConvertedKeyDown(event, func);
    }

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer
    public void changeToShooting(int keyCode) {
        if (keyCode == 516) {
            playback_pushed_S1_flg = true;
        }
        super.changeToShooting(keyCode);
    }
}
