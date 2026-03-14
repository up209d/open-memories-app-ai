package com.sony.imaging.app.base.playback.browser;

import android.os.Bundle;
import com.sony.imaging.app.base.common.EventParcel;
import com.sony.imaging.app.base.playback.base.SingleStateBase;

/* loaded from: classes.dex */
public class BrowserSingle extends SingleStateBase {
    private Bundle mAutoReviewBundle = null;

    @Override // com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase
    protected Bundle getBundleToLayout(String layoutTag) {
        Bundle bundle = this.mAutoReviewBundle;
        this.mAutoReviewBundle = null;
        return bundle;
    }

    @Override // com.sony.imaging.app.base.playback.base.SingleStateBase, com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase, com.sony.imaging.app.base.playback.base.PlayStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        if (this.data != null) {
            Bundle bundle = this.data;
            if (bundle.getParcelable(EventParcel.KEY_KEYCODE) != null || bundle.getParcelable(EventParcel.KEY_TOUCH) != null) {
                this.mAutoReviewBundle = bundle;
            }
        }
        super.onResume();
    }
}
