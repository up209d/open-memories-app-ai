package com.sony.imaging.app.base.playback.pseudorec;

import android.os.Bundle;
import com.sony.imaging.app.base.common.EventParcel;
import com.sony.imaging.app.base.playback.PlayRootContainer;
import com.sony.imaging.app.base.playback.base.SingleStateBase;

/* loaded from: classes.dex */
public class PseudoRecSingle extends SingleStateBase {
    @Override // com.sony.imaging.app.base.playback.base.PlayStateBase, com.sony.imaging.app.base.playback.layout.IPlaybackTriggerFunction
    public boolean transitionZoom(EventParcel event) {
        if (event != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(EventParcel.KEY_TOUCH, event);
            getRootContainer().changeApp(PlayRootContainer.ID_PSEUDOREC_ZOOM, bundle);
            return true;
        }
        getRootContainer().changeApp(PlayRootContainer.ID_PSEUDOREC_ZOOM);
        return true;
    }

    @Override // com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase
    protected Bundle getBundleToLayout(String layoutTag) {
        return this.data;
    }
}
