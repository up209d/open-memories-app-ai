package com.sony.imaging.app.base.playback.contents.aviadapter;

import android.content.ContentResolver;
import com.sony.scalar.provider.AvindexStore;

/* loaded from: classes.dex */
public class AvindexStoreImages implements IAvindexStore {
    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.IAvindexStore
    public boolean loadMedia(String mediaId, int contentType) {
        return AvindexStore.loadMedia(mediaId, contentType);
    }

    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.IAvindexStore
    public boolean loadMedia(String mediaId, int[] contentType) {
        return AvindexStore.loadMedia(mediaId, contentType[0]);
    }

    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.IAvindexStore
    public boolean waitLoadMediaComplete(String mediaId) {
        return AvindexStore.waitLoadMediaComplete(mediaId);
    }

    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.IAvindexStore
    public boolean waitLoadMediaComplete(String mediaId, int[] contentType) {
        return waitLoadMediaComplete(mediaId);
    }

    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.IAvindexStore
    public boolean cancelWaitLoadMediaComplete(String mediaId) {
        return AvindexStore.cancelWaitLoadMediaComplete(mediaId);
    }

    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.IAvindexStore
    public boolean waitAndUpdateDatabase(ContentResolver cr, String mediaId) {
        return AvindexStore.Images.waitAndUpdateDatabase(cr, mediaId);
    }

    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.IAvindexStore
    public boolean cancelWaitAndUpdateDatabase(ContentResolver cr, String mediaId) {
        return AvindexStore.Images.cancelWaitAndUpdateDatabase(cr, mediaId);
    }
}
