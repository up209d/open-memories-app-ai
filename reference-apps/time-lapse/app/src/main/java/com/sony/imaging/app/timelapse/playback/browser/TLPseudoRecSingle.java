package com.sony.imaging.app.timelapse.playback.browser;

import com.sony.imaging.app.base.playback.base.SingleStateBase;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.base.playback.pseudorec.PseudoRecSingle;
import com.sony.imaging.app.timelapse.AppLog;
import com.sony.imaging.app.timelapse.playback.controller.PlayBackController;
import com.sony.imaging.app.util.ApoWrapper;

/* loaded from: classes.dex */
public class TLPseudoRecSingle extends PseudoRecSingle {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.base.SingleStateBase, com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase
    public String getResumeLayout() {
        ContentsManager mgr = ContentsManager.getInstance();
        if (mgr.isInitialQueryDone()) {
            if (mgr.isEmpty()) {
                AppLog.info(this.TAG, "ID_NO_FILE_LAYOUT");
                return "NO_FILE";
            }
            AppLog.info(this.TAG, "NORMAL_PB");
            return "NORMAL_PB";
        }
        if (mgr.getResumeId() != null && PlayBackController.getInstance().getBufferPBState() == 0) {
            AppLog.info(this.TAG, SingleStateBase.ID_BUFFER_PLAYBACK_LAYOUT);
            return SingleStateBase.ID_BUFFER_PLAYBACK_LAYOUT;
        }
        AppLog.info(this.TAG, "NO_BUFFER  " + mgr.getResumeId());
        return SingleStateBase.ID_NO_BUFFER_LAYOUT;
    }

    @Override // com.sony.imaging.app.fw.State
    public ApoWrapper.APO_TYPE getApoType() {
        return ApoWrapper.APO_TYPE.NONE;
    }
}
