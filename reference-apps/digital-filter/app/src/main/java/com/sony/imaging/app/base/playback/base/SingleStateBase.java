package com.sony.imaging.app.base.playback.base;

import android.util.Log;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.playback.PlayRootContainer;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.util.OLEDWrapper;
import com.sony.imaging.app.util.PTag;

/* loaded from: classes.dex */
public abstract class SingleStateBase extends PlayStateWithLayoutBase implements ContentsManager.QueryCallback {
    public static final String ID_4K_PLAYBACK_LAYOUT = "ID_4K_PLAYBACK_LAYOUT";
    public static final String ID_BUFFER_PLAYBACK_LAYOUT = "BUFFER_PB";
    public static final String ID_NORMAL_PLAYBACK_LAYOUT = "NORMAL_PB";
    public static final String ID_NO_BUFFER_LAYOUT = "NO_BUFFER";
    public static final String ID_NO_FILE_LAYOUT = "NO_FILE";
    public static final String ID_VOLUME_ADJUST_LAYOUT = "ID_VOLUME_ADJUST_LAYOUT";
    private static final String MSG_TRANSITION_INDEXPB = "transition to IndexPb";
    protected QueryRunnable mRunnable = new QueryRunnable();

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
        if (mgr.getResumeId() != null) {
            return ID_BUFFER_PLAYBACK_LAYOUT;
        }
        return ID_NO_BUFFER_LAYOUT;
    }

    @Override // com.sony.imaging.app.fw.State
    public OLEDWrapper.OLED_TYPE getOledType() {
        return OLEDWrapper.OLED_TYPE.LUMINANCE_ALPHA;
    }

    /* loaded from: classes.dex */
    protected class QueryRunnable implements Runnable {
        protected QueryRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            ContentsManager mgr = ContentsManager.getInstance();
            if (!mgr.isInitialQueryDone()) {
                mgr.startInitialQuery(SingleStateBase.this, false);
            }
        }
    }

    @Override // com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase, com.sony.imaging.app.base.playback.base.PlayStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        ContentsManager mgr = ContentsManager.getInstance();
        mgr.setPbMode(1);
        super.onResume();
        if (!mgr.isInitialQueryDone()) {
            if (this.mRunnable != null) {
                getHandler().post(this.mRunnable);
                return;
            }
            if (mgr.getResumeId() != null) {
                mgr.startDecodingBuffer();
            }
            mgr.startInitialQuery(this, false);
        }
    }

    @Override // com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        ContentsManager.getInstance().cancelInitialQuery();
        getHandler().removeCallbacks(this.mRunnable);
        super.onPause();
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
    public boolean transitionIndexPb() {
        PTag.start(MSG_TRANSITION_INDEXPB);
        getRootContainer().setData(PlayRootContainer.PROP_ID_PB_MODE, PlayRootContainer.PB_MODE.INDEX);
        setNextState(PlaySubApp.ID_INDEX_PB, null);
        return true;
    }
}
