package com.sony.imaging.app.portraitbeauty.playback.catchlight.state;

import com.sony.imaging.app.base.playback.base.PlayStateBase;
import com.sony.imaging.app.util.ApoWrapper;

/* loaded from: classes.dex */
public class FaceSelectState extends PlayStateBase {
    @Override // com.sony.imaging.app.fw.State
    public ApoWrapper.APO_TYPE getApoType() {
        return ApoWrapper.APO_TYPE.SPECIAL;
    }
}
