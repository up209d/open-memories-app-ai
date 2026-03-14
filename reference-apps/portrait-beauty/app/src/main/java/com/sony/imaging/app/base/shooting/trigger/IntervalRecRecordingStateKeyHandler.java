package com.sony.imaging.app.base.shooting.trigger;

import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.util.Environment;

/* loaded from: classes.dex */
public class IntervalRecRecordingStateKeyHandler extends IntervalRecStateBaseKeyHandler {
    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS2Key() {
        ExecutorCreator.getInstance().getSequence().stopIntervalRec();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieRecKey() {
        return Environment.isNewBizDeviceActionCam() ? pushedS2Key() : super.pushedMovieRecKey();
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedMovieRecKey() {
        return Environment.isNewBizDeviceActionCam() ? super.releasedS2Key() : super.releasedMovieRecKey();
    }
}
