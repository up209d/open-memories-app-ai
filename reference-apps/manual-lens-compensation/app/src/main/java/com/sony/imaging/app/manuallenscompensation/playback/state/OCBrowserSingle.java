package com.sony.imaging.app.manuallenscompensation.playback.state;

import com.sony.imaging.app.base.playback.browser.BrowserSingle;
import com.sony.imaging.app.manuallenscompensation.commonUtil.AppLog;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCUtil;

/* loaded from: classes.dex */
public class OCBrowserSingle extends BrowserSingle {
    public static final String ID_REC_MODE_MOVIE_NOT_SUPPORTED = "ID_REC_MODE_MOVIE_NOT_SUPPORTED";

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.browser.BrowserSingle, com.sony.imaging.app.base.playback.base.SingleStateBase, com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase
    public String getResumeLayout() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (OCUtil.getInstance().getLastCaptureRecMode() == 2) {
            return ID_REC_MODE_MOVIE_NOT_SUPPORTED;
        }
        String ret = super.getResumeLayout();
        return ret;
    }

    @Override // com.sony.imaging.app.base.playback.browser.BrowserSingle, com.sony.imaging.app.base.playback.base.SingleStateBase, com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        OCUtil.getInstance().setLastCaptureMode(1);
        super.onPause();
    }
}
