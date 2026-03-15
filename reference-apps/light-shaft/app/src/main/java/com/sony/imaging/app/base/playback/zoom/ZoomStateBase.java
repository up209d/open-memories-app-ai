package com.sony.imaging.app.base.playback.zoom;

import android.os.Bundle;
import android.os.Handler;
import com.sony.imaging.app.base.playback.base.SingleStateBase;
import com.sony.imaging.app.base.playback.layout.ZoomLayoutBase;

/* loaded from: classes.dex */
public class ZoomStateBase extends SingleStateBase {
    Handler mHandler;
    ZoomLayoutBase mLayout;

    @Override // com.sony.imaging.app.base.playback.base.SingleStateBase, com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase, com.sony.imaging.app.base.playback.base.PlayStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        this.mLayout = (ZoomLayoutBase) getLayout(getResumeLayout());
        this.mHandler = new Handler();
    }

    @Override // com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase
    protected Bundle getBundleToLayout(String layoutTag) {
        return this.data;
    }
}
