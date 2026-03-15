package com.sony.imaging.app.manuallenscompensation.shooting.keyhandler;

import com.sony.imaging.app.base.shooting.movie.trigger.MovieRecStandbyStateKeyHandler;
import com.sony.imaging.app.manuallenscompensation.commonUtil.AppLog;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCUtil;

/* loaded from: classes.dex */
public class OCMovieRecStandbyStateKeyHandler extends MovieRecStandbyStateKeyHandler {
    private static final String TAG = "OCMovieRecStandbyStateKeyHandler";

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        int returnState;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (OCUtil.getInstance().getLastCaptureRecMode() == 2) {
            OCUtil.getInstance().openMoviePlaybackDisabledCaution();
            returnState = 1;
        } else {
            returnState = super.pushedPlayBackKey();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return returnState;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayIndexKey() {
        int returnState;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (OCUtil.getInstance().getLastCaptureRecMode() == 2) {
            OCUtil.getInstance().openMoviePlaybackDisabledCaution();
            returnState = 1;
        } else {
            returnState = super.pushedPlayIndexKey();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return returnState;
    }
}
