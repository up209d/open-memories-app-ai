package com.sony.imaging.app.base.playback.browser;

import android.os.Bundle;
import com.sony.imaging.app.base.playback.base.IndexStateBase;

/* loaded from: classes.dex */
public class BrowserIndex extends IndexStateBase {
    @Override // com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase
    protected Bundle getBundleToLayout(String layoutTag) {
        return this.data;
    }
}
