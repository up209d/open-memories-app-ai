package com.sony.imaging.app.base.playback.browser;

import android.os.Bundle;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.EventParcel;
import com.sony.imaging.app.base.playback.base.SingleStateBase;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.base.playback.layout.I4kPlaybackTrigger;
import com.sony.imaging.app.base.playback.layout.I4kPlaybackTriggerFunction;
import com.sony.imaging.app.base.playback.layout.ISinglePlaybackTrigger;
import com.sony.imaging.app.base.playback.layout.ISinglePlaybackTriggerFunction;
import com.sony.imaging.app.fw.IEachKeyHandler;

/* loaded from: classes.dex */
public class BrowserSingle extends SingleStateBase implements I4kPlaybackTriggerFunction, ISinglePlaybackTriggerFunction {
    public static final String ID_DIALOG_STARTING_4K_PB_LAYOUT = "DIALOG_STARTING_4K_PB_LAYOUT";
    private Bundle mAutoReviewBundle = null;

    @Override // com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase
    protected Bundle getBundleToLayout(String layoutTag) {
        Bundle bundle = this.mAutoReviewBundle;
        this.mAutoReviewBundle = null;
        return bundle;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.base.SingleStateBase, com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase
    public String getResumeLayout() {
        ContentsManager mgr = ContentsManager.getInstance();
        return (((BaseApp) getActivity()).is4kPlaybackSupported() && mgr.isInitialQueryDone() && 2 == DisplayModeObserver.getInstance().get4kOutputStatus()) ? SingleStateBase.ID_4K_PLAYBACK_LAYOUT : super.getResumeLayout();
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

    @Override // com.sony.imaging.app.base.playback.base.SingleStateBase, com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        closeLayout(ID_DIALOG_STARTING_4K_PB_LAYOUT);
        closePlayLayout(SingleStateBase.ID_VOLUME_ADJUST_LAYOUT);
        super.onPause();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase
    public void onPlayLayoutOpened(String layoutTag) {
        super.onPlayLayoutOpened(layoutTag);
        IEachKeyHandler layout = getLayout(layoutTag);
        if (I4kPlaybackTrigger.class.isInstance(layout)) {
            ((I4kPlaybackTrigger) layout).set4kPlaybackFunction(this);
        }
        if (ISinglePlaybackTrigger.class.isInstance(layout)) {
            ((ISinglePlaybackTrigger) layout).setFunction(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase
    public void onPlayLayoutClosing(String layoutTag) {
        IEachKeyHandler layout = getLayout(layoutTag);
        if (I4kPlaybackTrigger.class.isInstance(layout)) {
            ((I4kPlaybackTrigger) layout).set4kPlaybackFunction(null);
        }
        if (ISinglePlaybackTrigger.class.isInstance(layout)) {
            ((ISinglePlaybackTrigger) layout).setFunction(null);
        }
        super.onPlayLayoutClosing(layoutTag);
    }

    @Override // com.sony.imaging.app.base.playback.layout.I4kPlaybackTriggerFunction
    public boolean open4kStartingDialog() {
        openPlayLayout(ID_DIALOG_STARTING_4K_PB_LAYOUT);
        return true;
    }

    @Override // com.sony.imaging.app.base.playback.layout.I4kPlaybackTriggerFunction
    public boolean close4kStartingDialog() {
        closePlayLayout(ID_DIALOG_STARTING_4K_PB_LAYOUT);
        return true;
    }

    @Override // com.sony.imaging.app.base.playback.layout.I4kPlaybackTriggerFunction
    public boolean start4kPlayback() {
        return DisplayModeObserver.getInstance().start4kPlayback();
    }

    @Override // com.sony.imaging.app.base.playback.layout.I4kPlaybackTriggerFunction
    public boolean stop4kPlayback() {
        return DisplayModeObserver.getInstance().stop4kPlayback();
    }

    @Override // com.sony.imaging.app.base.playback.layout.ISinglePlaybackTriggerFunction
    public boolean openVolumeAdjustment() {
        if (getLayout(SingleStateBase.ID_VOLUME_ADJUST_LAYOUT) == null) {
            return false;
        }
        openPlayLayout(SingleStateBase.ID_VOLUME_ADJUST_LAYOUT);
        return true;
    }

    @Override // com.sony.imaging.app.base.playback.layout.ISinglePlaybackTriggerFunction
    public boolean closeVolumeAdjustment() {
        if (getLayout(SingleStateBase.ID_VOLUME_ADJUST_LAYOUT) == null) {
            return false;
        }
        closePlayLayout(SingleStateBase.ID_VOLUME_ADJUST_LAYOUT);
        return true;
    }
}
