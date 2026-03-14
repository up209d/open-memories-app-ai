package com.sony.imaging.app.base.playback.base;

import android.os.Bundle;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.EventParcel;
import com.sony.imaging.app.base.playback.PlayRootContainer;
import com.sony.imaging.app.base.playback.contents.ContentInfo;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.base.playback.layout.IPlaybackTriggerFunction;
import com.sony.imaging.app.fw.ContainerState;
import com.sony.imaging.app.util.PTag;

/* loaded from: classes.dex */
public class PlayStateBase extends ContainerState implements IPlaybackTriggerFunction {
    private static final String MSG_TRANSITION_INDEXPB = "transition to IndexPb";
    private static final String MSG_TRANSITION_SINGLEPB = "transition to SinglePb";
    protected final String TAG = getClass().getSimpleName();

    /* JADX INFO: Access modifiers changed from: protected */
    public PlaySubApp getContainer() {
        return (PlaySubApp) this.container;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public PlayRootContainer getRootContainer() {
        return (PlayRootContainer) getData(PlayRootContainer.PROP_ID_APP_ROOT);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getLogTag() {
        return this.TAG;
    }

    protected int getResumeKeyBeepPattern() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        setKeyBeepPattern(getResumeKeyBeepPattern());
    }

    @Override // com.sony.imaging.app.base.playback.layout.IPlaybackTriggerFunction
    public boolean transitionBrowser() {
        getRootContainer().changeApp(PlayRootContainer.ID_BROWSER);
        return true;
    }

    public boolean transitionSinglePb() {
        PTag.start(MSG_TRANSITION_SINGLEPB);
        PlayRootContainer root = getRootContainer();
        root.setData(PlayRootContainer.PROP_ID_PB_MODE, PlayRootContainer.PB_MODE.SINGLE);
        root.changeApp(PlayRootContainer.ID_BROWSER);
        return true;
    }

    @Override // com.sony.imaging.app.base.playback.layout.IPlaybackTriggerFunction
    public boolean transitionIndexPb() {
        PTag.start(MSG_TRANSITION_INDEXPB);
        PlayRootContainer root = getRootContainer();
        root.setData(PlayRootContainer.PROP_ID_PB_MODE, PlayRootContainer.PB_MODE.INDEX);
        root.changeApp(PlayRootContainer.ID_BROWSER);
        return true;
    }

    @Override // com.sony.imaging.app.base.playback.layout.IPlaybackTriggerFunction
    public boolean transitionZoom(EventParcel event) {
        if (event != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(EventParcel.KEY_TOUCH, event);
            getRootContainer().changeApp(PlayRootContainer.ID_PLAYZOOM, bundle);
            return true;
        }
        getRootContainer().changeApp(PlayRootContainer.ID_PLAYZOOM);
        return true;
    }

    @Override // com.sony.imaging.app.base.playback.layout.IPlaybackTriggerFunction
    public boolean transitionDeleteThis() {
        ContentsManager mgr = ContentsManager.getInstance();
        ContentInfo info = mgr.getContentInfo(mgr.getContentsId());
        if (info != null) {
            if (info.getInt("FILE_SYSTEMProtectInfo") == 0) {
                addChildState(PlayRootContainer.ID_DELETE_SINGLE, (Bundle) null);
                return true;
            }
            CautionUtilityClass.getInstance().requestTrigger(1740);
        }
        return false;
    }

    @Override // com.sony.imaging.app.base.playback.layout.IPlaybackTriggerFunction
    public boolean transitionShooting(EventParcel event) {
        getRootContainer().changeToShooting();
        return true;
    }
}
