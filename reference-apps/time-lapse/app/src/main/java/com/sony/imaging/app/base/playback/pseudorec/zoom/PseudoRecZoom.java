package com.sony.imaging.app.base.playback.pseudorec.zoom;

import android.os.Bundle;
import com.sony.imaging.app.base.playback.base.PlaySubApp;

/* loaded from: classes.dex */
public class PseudoRecZoom extends PlaySubApp {
    @Override // com.sony.imaging.app.base.playback.base.PlaySubApp
    protected String getEntryState() {
        return PlaySubApp.ID_DEFAULT_STATE;
    }

    @Override // com.sony.imaging.app.base.playback.base.PlaySubApp
    protected Bundle getBundleToEntryState() {
        return this.data;
    }
}
