package com.sony.imaging.app.base.playback.zoom;

import android.os.Bundle;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.playback.base.SingleStateBase;
import com.sony.imaging.app.base.playback.contents.ContentsManager;

/* loaded from: classes.dex */
public class PlayZoomState extends ZoomStateBase {
    @Override // com.sony.imaging.app.base.playback.zoom.ZoomStateBase, com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase
    protected Bundle getBundleToLayout(String layoutTag) {
        return this.data;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.base.SingleStateBase, com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase
    public String getResumeLayout() {
        ContentsManager mgr = ContentsManager.getInstance();
        return (((BaseApp) getActivity()).is4kPlaybackSupported() && mgr.isInitialQueryDone() && 2 == DisplayModeObserver.getInstance().get4kOutputStatus()) ? SingleStateBase.ID_4K_PLAYBACK_LAYOUT : super.getResumeLayout();
    }
}
