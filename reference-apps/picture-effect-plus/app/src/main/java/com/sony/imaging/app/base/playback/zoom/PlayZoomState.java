package com.sony.imaging.app.base.playback.zoom;

import android.os.Bundle;

/* loaded from: classes.dex */
public class PlayZoomState extends ZoomStateBase {
    @Override // com.sony.imaging.app.base.playback.zoom.ZoomStateBase, com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase
    protected Bundle getBundleToLayout(String layoutTag) {
        return this.data;
    }
}
