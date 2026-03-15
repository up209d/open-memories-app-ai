package com.sony.imaging.app.soundphoto.shooting.trigger;

import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.trigger.ShootingStateKeyHandler;
import com.sony.imaging.app.soundphoto.shooting.audiorecorder.AudioRecorder;
import com.sony.imaging.app.soundphoto.shooting.state.SPShootingState;
import com.sony.imaging.app.soundphoto.util.AppLog;
import com.sony.imaging.app.soundphoto.util.SPConstants;
import com.sony.imaging.app.util.Environment;

/* loaded from: classes.dex */
public class SPShootingStateKeyHandler extends ShootingStateKeyHandler {
    private static final int PF_VERSION_IGNORE_FIRST_S2_FROM_PLAY = 8;
    private static final String TAG = "SPShootingStateKeyHandler";

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingStateKeyHandler, com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS2Key() {
        AppLog.enter(TAG, "pushedS2Key() start ");
        AppLog.info(TAG, " SPShootingState.getInstance().getTakePictureEnable() call : " + SPShootingState.getInstance().getTakePictureEnable());
        AppLog.info(TAG, " AudioRecorder.getInstance().getEsBufferRest() call : " + AudioRecorder.getInstance().getEsBufferRest());
        if (!SPShootingState.getInstance().getTakePictureEnable() || !AudioRecorder.getInstance().getEsBufferRest() || !AudioRecorder.getInstance().getRecStatus() || (SPShootingState.getInstance().getFirstSequenceLockState() && Environment.getVersionPfAPI() <= 8)) {
            AppLog.info(TAG, "pushedS2Key() end by disable ");
            if (SPShootingState.getInstance().getFirstSequenceLockState() && Environment.getVersionPfAPI() <= 8) {
                CameraNotificationManager.getInstance().requestNotify(SPConstants.TAG_PROCESSING_LAYOUT);
            }
            return -1;
        }
        AppLog.info(TAG, "pushedS2Key() end");
        return super.pushedS2Key();
    }
}
