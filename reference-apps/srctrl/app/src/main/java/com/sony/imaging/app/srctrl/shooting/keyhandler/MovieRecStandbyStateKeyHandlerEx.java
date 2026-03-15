package com.sony.imaging.app.srctrl.shooting.keyhandler;

import com.sony.imaging.app.base.shooting.movie.trigger.MovieRecStandbyStateKeyHandler;

/* loaded from: classes.dex */
public class MovieRecStandbyStateKeyHandlerEx extends MovieRecStandbyStateKeyHandler {
    @Override // com.sony.imaging.app.base.shooting.trigger.S1OffEEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        return -1;
    }
}
