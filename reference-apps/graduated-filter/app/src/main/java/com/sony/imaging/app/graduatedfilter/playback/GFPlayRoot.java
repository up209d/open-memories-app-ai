package com.sony.imaging.app.graduatedfilter.playback;

import com.sony.imaging.app.base.playback.PlayRootContainer;
import com.sony.imaging.app.graduatedfilter.common.GFCommonUtil;

/* loaded from: classes.dex */
public class GFPlayRoot extends PlayRootContainer {
    public static final String ID_DELETE_MULTIPLE = "ID_DELETE_MULTIPLE";

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        GFCommonUtil.getInstance().setHoldKey();
        super.onPause();
    }
}
