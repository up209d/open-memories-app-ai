package com.sony.imaging.app.base.playback.contents;

import android.net.Uri;
import com.sony.imaging.app.base.playback.contents.aviadapter.IMedia;
import com.sony.imaging.app.base.playback.contents.aviadapter.ImagesMedia;

/* loaded from: classes.dex */
public class ContentsIdentifier {
    public long _id;
    public int contentType;
    public String data;
    protected IMedia mMediaAdapter;
    public String mediaId;

    public ContentsIdentifier(long anId, String aData, String aMediaId) {
        this._id = anId;
        this.data = aData;
        this.mediaId = aMediaId;
        this.contentType = 1;
        this.mMediaAdapter = new ImagesMedia();
    }

    public ContentsIdentifier(long anId, String aData, String aMediaId, int aContentType, IMedia adapter) {
        this._id = anId;
        this.data = aData;
        this.mediaId = aMediaId;
        this.contentType = aContentType;
        this.mMediaAdapter = adapter;
    }

    public Uri getContentUri() {
        return this.mMediaAdapter.getContentUri(this.mediaId);
    }

    public IMedia getMediaAdapter() {
        return this.mMediaAdapter;
    }
}
