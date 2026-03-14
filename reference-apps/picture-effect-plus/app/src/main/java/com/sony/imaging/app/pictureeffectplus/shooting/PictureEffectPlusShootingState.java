package com.sony.imaging.app.pictureeffectplus.shooting;

import android.util.Log;
import com.sony.imaging.app.base.shooting.ShootingState;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;
import com.sony.imaging.app.pictureeffectplus.shooting.camera.PictureEffectPlusController;

/* loaded from: classes.dex */
public class PictureEffectPlusShootingState extends ShootingState {
    private static final String TAG = "PictureEffectPlusShootingState";

    @Override // com.sony.imaging.app.base.shooting.ShootingState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        if (PictureEffectPlusController.getDriveModeBeforeSAEffect() == null && PictureEffectPlusController.getInstance().getMiniatureStatus() == 0) {
            String driveMode = DriveModeController.getInstance().getValue();
            PictureEffectPlusController.setDriveModeBeforeSAEffect(driveMode);
            Log.i(TAG, "DriveMode:" + driveMode);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        PictureEffectPlusController.getInstance().onTerminate();
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingState
    protected String getNextState() {
        return "ExposureModeCheck";
    }
}
