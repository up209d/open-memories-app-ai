package com.sony.imaging.app.srctrl.playback.contents;

import com.sony.imaging.app.base.playback.contents.ViewMode;
import com.sony.scalar.provider.AvindexStore;

/* loaded from: classes.dex */
public class NeutralViewMode extends ViewMode {
    public NeutralViewMode() {
        this.mGroupCursor = null;
        this.mContentsCursor = null;
    }

    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    protected String getMediaId() {
        String[] externalMedias = AvindexStore.getExternalMediaIds();
        return externalMedias[0];
    }

    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    protected int getContentType() {
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    public boolean queryGroup() {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    public boolean queryInGroup() {
        return true;
    }

    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    public boolean moveToEntryPosition() {
        return true;
    }
}
