package com.sony.imaging.app.base.playback.base;

import android.os.Bundle;
import android.util.Log;
import com.sony.imaging.app.base.playback.PlayRootContainer;

/* loaded from: classes.dex */
public class PlaySubApp extends PlayStateBase {
    public static final String ID_DEFAULT_STATE = "ID_DEFAULT_STATE";
    public static final String ID_INDEX_PB = "ID_INDEX_PB";
    public static final String ID_SINGLE_PB = "ID_SINGLE_PB";
    private static final String MSG_W_GET_CONTAINER = "getContainer should not be called for SubApp";

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.base.PlayStateBase
    public PlaySubApp getContainer() {
        Log.w(this.TAG, MSG_W_GET_CONTAINER);
        return super.getContainer();
    }

    @Override // com.sony.imaging.app.base.playback.base.PlayStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        String state = getEntryState();
        if (state != null && !state.equals("")) {
            addChildState(state, getBundleToEntryState());
        }
    }

    protected String getEntryState() {
        PlayRootContainer root = getRootContainer();
        PlayRootContainer.PB_MODE mode = (PlayRootContainer.PB_MODE) root.getData(PlayRootContainer.PROP_ID_PB_MODE);
        if (mode == PlayRootContainer.PB_MODE.INDEX) {
            return ID_INDEX_PB;
        }
        return ID_SINGLE_PB;
    }

    protected Bundle getBundleToEntryState() {
        return null;
    }
}
