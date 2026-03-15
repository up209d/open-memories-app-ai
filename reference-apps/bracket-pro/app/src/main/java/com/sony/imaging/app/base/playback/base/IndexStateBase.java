package com.sony.imaging.app.base.playback.base;

import android.util.Log;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.playback.PlayRootContainer;
import com.sony.imaging.app.base.playback.contents.ContentsManager;

/* loaded from: classes.dex */
public abstract class IndexStateBase extends PlayStateWithLayoutBase implements ContentsManager.QueryCallback {
    public static final String ID_DATA_UNREADY_LAYOUT = "DATA_UNREADY_LAYOUT";
    public static final String ID_NORMAL_PLAYBACK_LAYOUT = "NORMAL_PB";
    public static final String ID_NO_FILE_LAYOUT = "NO_FILE";
    private static final String MSG_ARROW = " -> ";

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase
    public String getResumeLayout() {
        ContentsManager mgr = ContentsManager.getInstance();
        if (mgr.isInitialQueryDone()) {
            if (mgr.isEmpty()) {
                return "NO_FILE";
            }
            return "NORMAL_PB";
        }
        return ID_DATA_UNREADY_LAYOUT;
    }

    @Override // com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase, com.sony.imaging.app.base.playback.base.PlayStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        ContentsManager mgr = ContentsManager.getInstance();
        mgr.setPbMode(2);
        super.onResume();
        if (!mgr.isInitialQueryDone()) {
            mgr.startInitialQuery(this, false);
        }
    }

    @Override // com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        ContentsManager.getInstance().cancelInitialQuery();
        if (needRememberContentPos()) {
            ContentsManager.getInstance().rememberContentPos();
        }
        super.onPause();
    }

    protected boolean needRememberContentPos() {
        return true;
    }

    @Override // com.sony.imaging.app.base.playback.contents.ContentsManager.QueryCallback
    public void onCallback(boolean result) {
        if (result) {
            String layoutTag = getResumeLayout();
            Log.i(this.TAG, LogHelper.getScratchBuilder(this.mCurrentLayout).append(" -> ").append(layoutTag).toString());
            if (this.mCurrentLayout != null) {
                closePlayLayout(this.mCurrentLayout);
            }
            if (layoutTag != null) {
                openPlayLayout(layoutTag);
            }
            this.mCurrentLayout = layoutTag;
        }
    }

    @Override // com.sony.imaging.app.base.playback.base.PlayStateBase, com.sony.imaging.app.base.playback.layout.IPlaybackTriggerFunction
    public boolean transitionSinglePb() {
        getRootContainer().setData(PlayRootContainer.PROP_ID_PB_MODE, PlayRootContainer.PB_MODE.SINGLE);
        setNextState(PlaySubApp.ID_SINGLE_PB, null);
        return true;
    }
}
