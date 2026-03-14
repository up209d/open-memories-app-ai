package com.sony.imaging.app.base.playback.pseudorec.zoom;

import android.os.Bundle;
import com.sony.imaging.app.base.playback.PlayRootContainer;
import com.sony.imaging.app.base.playback.zoom.ZoomStateBase;

/* loaded from: classes.dex */
public class PseudoRecZoomState extends ZoomStateBase {
    @Override // com.sony.imaging.app.base.playback.base.PlayStateBase, com.sony.imaging.app.base.playback.layout.IPlaybackTriggerFunction
    public boolean transitionBrowser() {
        getRootContainer().changeApp(PlayRootContainer.ID_PSEUDOREC);
        return true;
    }

    @Override // com.sony.imaging.app.base.playback.zoom.ZoomStateBase, com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase
    protected Bundle getBundleToLayout(String layoutTag) {
        return this.data;
    }
}
