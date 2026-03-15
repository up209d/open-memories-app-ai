package com.sony.imaging.app.base.playback.contents.aviadapter;

import android.content.ContentResolver;
import com.sony.scalar.provider.AvindexStore;

/* loaded from: classes.dex */
public class AvindexStoreFiles implements IAvindexStore {
    public int[] mLastLoadedTypes;

    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.IAvindexStore
    public boolean loadMedia(String mediaId, int contentType) {
        this.mLastLoadedTypes = new int[]{contentType};
        return AvindexStore.Files.loadMedia(mediaId, this.mLastLoadedTypes);
    }

    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.IAvindexStore
    public boolean loadMedia(String mediaId, int[] contentType) {
        this.mLastLoadedTypes = contentType;
        return AvindexStore.Files.loadMedia(mediaId, contentType);
    }

    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.IAvindexStore
    public boolean waitLoadMediaComplete(String mediaId) {
        if (this.mLastLoadedTypes == null) {
            return false;
        }
        return AvindexStore.Files.waitLoadMediaComplete(mediaId, this.mLastLoadedTypes);
    }

    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.IAvindexStore
    public boolean waitLoadMediaComplete(String mediaId, int[] contentType) {
        return AvindexStore.Files.waitLoadMediaComplete(mediaId, contentType);
    }

    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.IAvindexStore
    public boolean cancelWaitLoadMediaComplete(String mediaId) {
        return AvindexStore.Files.cancelWaitLoadMediaComplete(mediaId);
    }

    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.IAvindexStore
    public boolean waitAndUpdateDatabase(ContentResolver cr, String mediaId) {
        return AvindexStore.Files.waitAndUpdateDatabase(cr, mediaId);
    }

    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.IAvindexStore
    public boolean cancelWaitAndUpdateDatabase(ContentResolver cr, String mediaId) {
        return AvindexStore.Files.cancelWaitAndUpdateDatabase(cr, mediaId);
    }
}
