package com.sony.imaging.app.base.playback.pseudorec;

import android.os.Bundle;
import com.sony.imaging.app.base.playback.base.PlaySubApp;

/* loaded from: classes.dex */
public class PseudoRec extends PlaySubApp {
    @Override // com.sony.imaging.app.base.playback.base.PlaySubApp
    protected String getEntryState() {
        return PlaySubApp.ID_SINGLE_PB;
    }

    @Override // com.sony.imaging.app.base.playback.base.PlaySubApp
    protected Bundle getBundleToEntryState() {
        return this.data;
    }
}
