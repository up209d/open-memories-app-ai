package com.sony.imaging.app.base.shooting;

import com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;

/* loaded from: classes.dex */
public class NormalCaptureState extends CaptureStateBase {
    @Override // com.sony.imaging.app.base.shooting.CaptureStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        BaseShootingExecutor executor = ExecutorCreator.getInstance().getSequence();
        if (isTakePictureByRemote()) {
            executor.takePicture(1);
        } else {
            executor.takePicture();
        }
    }
}
