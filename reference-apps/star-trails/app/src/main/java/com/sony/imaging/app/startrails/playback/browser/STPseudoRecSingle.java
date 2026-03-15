package com.sony.imaging.app.startrails.playback.browser;

import com.sony.imaging.app.base.playback.base.SingleStateBase;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.base.playback.pseudorec.PseudoRecSingle;
import com.sony.imaging.app.startrails.playback.controller.PlayBackController;

/* loaded from: classes.dex */
public class STPseudoRecSingle extends PseudoRecSingle {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.base.SingleStateBase, com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase
    public String getResumeLayout() {
        ContentsManager mgr = ContentsManager.getInstance();
        if (mgr.isInitialQueryDone()) {
            if (mgr.isEmpty()) {
                return "NO_FILE";
            }
            return "NORMAL_PB";
        }
        if (mgr.getResumeId() != null && PlayBackController.getInstance().getBufferPBState() == 0) {
            return SingleStateBase.ID_BUFFER_PLAYBACK_LAYOUT;
        }
        return SingleStateBase.ID_NO_BUFFER_LAYOUT;
    }
}
