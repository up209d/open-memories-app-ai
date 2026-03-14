package com.sony.imaging.app.base.playback.contents;

import android.net.Uri;
import com.sony.scalar.provider.AvindexStore;

/* loaded from: classes.dex */
public class ContentsIdentifier {
    public long _id;
    public String data;
    public String mediaId;

    public ContentsIdentifier(long anId, String aData, String aMediaId) {
        this._id = anId;
        this.data = aData;
        this.mediaId = aMediaId;
    }

    public Uri getContentUri() {
        return AvindexStore.Images.Media.getContentUri(this.mediaId);
    }
}
