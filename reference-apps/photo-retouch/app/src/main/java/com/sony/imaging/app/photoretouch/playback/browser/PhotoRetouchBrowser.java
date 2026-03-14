package com.sony.imaging.app.photoretouch.playback.browser;

import com.sony.imaging.app.base.playback.base.PlaySubApp;
import com.sony.imaging.app.photoretouch.common.ImageEditor;
import com.sony.imaging.app.util.ApoWrapper;

/* loaded from: classes.dex */
public class PhotoRetouchBrowser extends PlaySubApp {
    @Override // com.sony.imaging.app.base.playback.base.PlaySubApp, com.sony.imaging.app.base.playback.base.PlayStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        ImageEditor.updateSpaceAvailableInMemoryCard();
    }

    @Override // com.sony.imaging.app.base.playback.base.PlaySubApp
    protected String getEntryState() {
        return PlaySubApp.ID_INDEX_PB;
    }

    @Override // com.sony.imaging.app.fw.State
    public Integer getCautionMode() {
        return 9;
    }

    @Override // com.sony.imaging.app.fw.State
    public ApoWrapper.APO_TYPE getApoType() {
        return ApoWrapper.APO_TYPE.NONE;
    }
}
