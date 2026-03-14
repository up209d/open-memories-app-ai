package com.sony.imaging.app.base.playback.browser;

import android.os.Bundle;
import com.sony.imaging.app.base.playback.base.PlaySubApp;

/* loaded from: classes.dex */
public class Browser extends PlaySubApp {
    @Override // com.sony.imaging.app.base.playback.base.PlaySubApp
    protected Bundle getBundleToEntryState() {
        return this.data;
    }
}
