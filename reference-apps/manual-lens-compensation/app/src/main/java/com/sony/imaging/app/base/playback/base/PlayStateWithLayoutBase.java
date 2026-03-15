package com.sony.imaging.app.base.playback.base;

import android.os.Bundle;
import com.sony.imaging.app.base.playback.layout.PlayLayoutBase;
import com.sony.imaging.app.fw.Layout;

/* loaded from: classes.dex */
public abstract class PlayStateWithLayoutBase extends PlayStateBase {
    public static final String ID_DEFAULT_LAYOUT = "DEFAULT_LAYOUT";
    protected String mCurrentLayout;

    /* JADX INFO: Access modifiers changed from: protected */
    public String getResumeLayout() {
        return ID_DEFAULT_LAYOUT;
    }

    protected Bundle getBundleToLayout(String layoutTag) {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void openPlayLayout(String layoutTag) {
        Bundle bundle = getBundleToLayout(layoutTag);
        openLayout(layoutTag, bundle);
        onPlayLayoutOpened(layoutTag);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void closePlayLayout(String layoutTag) {
        onPlayLayoutClosing(layoutTag);
        closeLayout(layoutTag);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onPlayLayoutOpened(String layoutTag) {
        Layout layout = getLayout(layoutTag);
        if (PlayLayoutBase.class.isInstance(layout)) {
            ((PlayLayoutBase) layout).setPlaybackTrigger(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onPlayLayoutClosing(String layoutTag) {
        Layout layout = getLayout(layoutTag);
        if (PlayLayoutBase.class.isInstance(layout)) {
            ((PlayLayoutBase) layout).setPlaybackTrigger(null);
        }
    }

    @Override // com.sony.imaging.app.base.playback.base.PlayStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        this.mCurrentLayout = getResumeLayout();
        if (this.mCurrentLayout != null) {
            openPlayLayout(this.mCurrentLayout);
        }
    }

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        if (this.mCurrentLayout != null) {
            closePlayLayout(this.mCurrentLayout);
            this.mCurrentLayout = null;
        }
        super.onPause();
    }
}
