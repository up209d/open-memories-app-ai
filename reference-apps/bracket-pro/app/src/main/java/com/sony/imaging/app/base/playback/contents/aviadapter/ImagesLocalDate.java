package com.sony.imaging.app.base.playback.contents.aviadapter;

import android.database.Cursor;
import android.net.Uri;
import com.sony.scalar.provider.AvindexStore;

/* loaded from: classes.dex */
public class ImagesLocalDate implements ILocalDate {
    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.ICommonContainer
    public String convColumn(String column) {
        return column;
    }

    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.ICommonContainer
    public Uri getContentUri(String mediaId) {
        return AvindexStore.Images.LocalDate.getContentUri(mediaId);
    }

    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.ILocalDate
    public int getCursorIndex(Cursor c, String date) {
        return AvindexStore.Images.LocalDate.getCursorIndex(c, date);
    }
}
