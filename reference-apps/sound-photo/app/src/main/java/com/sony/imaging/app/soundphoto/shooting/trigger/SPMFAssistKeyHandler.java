package com.sony.imaging.app.soundphoto.shooting.trigger;

import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.shooting.trigger.MfAssistKeyHandler;
import com.sony.imaging.app.soundphoto.common.caution.SPInfo;
import com.sony.imaging.app.soundphoto.shooting.audiorecorder.AudioRecorder;
import com.sony.imaging.app.soundphoto.shooting.state.layout.SPAudioBufferAnimationController;

/* loaded from: classes.dex */
public class SPMFAssistKeyHandler extends MfAssistKeyHandler {
    @Override // com.sony.imaging.app.base.shooting.trigger.MfAssistKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        int returnState;
        SPAudioBufferAnimationController.getInstance().executeTask();
        int audiobufferState = SPAudioBufferAnimationController.getInstance().getAudiobufferStatus();
        if (audiobufferState == 2) {
            CautionUtilityClass.getInstance().requestTrigger(SPInfo.CAUTION_ID_SOUNDPHOTO_WAITING_SOUND_RECORDING);
            returnState = -1;
        } else {
            if (AudioRecorder.getInstance().getCreateSoundStatus()) {
                CautionUtilityClass.getInstance().requestTrigger(SPInfo.CAUTION_ID_SOUNDPHOTO_WAITING_SOUND_RECORDING);
                return -1;
            }
            returnState = super.pushedPlayBackKey();
        }
        return returnState;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.MfAssistKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayIndexKey() {
        int returnState;
        SPAudioBufferAnimationController.getInstance().executeTask();
        int audiobufferState = SPAudioBufferAnimationController.getInstance().getAudiobufferStatus();
        if (audiobufferState == 2) {
            CautionUtilityClass.getInstance().requestTrigger(SPInfo.CAUTION_ID_SOUNDPHOTO_WAITING_SOUND_RECORDING);
            returnState = -1;
        } else {
            if (AudioRecorder.getInstance().getCreateSoundStatus()) {
                CautionUtilityClass.getInstance().requestTrigger(SPInfo.CAUTION_ID_SOUNDPHOTO_WAITING_SOUND_RECORDING);
                return -1;
            }
            returnState = super.pushedPlayIndexKey();
        }
        return returnState;
    }
}
