package com.sony.imaging.app.srctrl.shooting.camera;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.SilentShutterController;
import com.sony.imaging.app.srctrl.util.SRCtrlNotificationManager;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.Environment;

/* loaded from: classes.dex */
public class SRCtrlSilentShutterController extends SilentShutterController implements AvailableInfo.IInhFactorChange {
    private static final String TAG = SRCtrlSilentShutterController.class.getSimpleName();
    private static String INH_FACTOR_STILL_WRITING = "INH_FACTOR_STILL_WRITING";

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onCameraSet() {
        super.onCameraSet();
        if (Environment.isSilentShutterAPISupported()) {
            AvailableInfo.addInhFactorListener(INH_FACTOR_STILL_WRITING, this);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onCameraRemoving() {
        super.onCameraRemoving();
        if (Environment.isSilentShutterAPISupported()) {
            AvailableInfo.removeInhFactorListener(this);
        }
    }

    @Override // com.sony.imaging.app.util.AvailableInfo.IInhFactorChange
    public void onInhFactorChanged(String factorID, int value) {
        Log.v(TAG, "onInhFactorChanged : " + factorID);
        SRCtrlNotificationManager.getInstance().requestNotify(SRCtrlNotificationManager.SILENT_SHUTTER_INH_FACTOR_CHANGED);
    }
}
