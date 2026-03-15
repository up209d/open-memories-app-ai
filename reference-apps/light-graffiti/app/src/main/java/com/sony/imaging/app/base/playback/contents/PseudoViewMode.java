package com.sony.imaging.app.base.playback.contents;

import com.sony.scalar.provider.AvindexStore;

/* loaded from: classes.dex */
public class PseudoViewMode extends ViewMode {
    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    protected String getMediaId() {
        String[] virtualMedias = AvindexStore.getVirtualMediaIds();
        return virtualMedias[0];
    }

    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    protected int getContentType() {
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    public boolean queryGroup() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    public boolean queryInGroup() {
        return false;
    }

    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    public boolean moveToEntryPosition() {
        return false;
    }
}
