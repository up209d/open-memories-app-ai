package com.sony.imaging.app.base.playback.contents.aviadapter;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import com.sony.imaging.app.base.playback.contents.ContentsIdentifier;
import com.sony.scalar.media.AvindexContentInfo;
import com.sony.scalar.provider.AvindexStore;

/* loaded from: classes.dex */
public class ImagesMedia implements IMedia {
    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.IMedia
    public Uri getContentUri(String mediaId) {
        return AvindexStore.Images.Media.getContentUri(mediaId);
    }

    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.IMedia
    public int getCursorIndex(Cursor c, int folderNumber, int fileNumber) {
        return AvindexStore.Images.Media.getCursorIndex(c, folderNumber, fileNumber);
    }

    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.IMedia
    public int getCursorIndex(Cursor c, String selection, String[] selectionArgs) {
        return AvindexStore.Images.Media.getCursorIndex(c, selection, selectionArgs);
    }

    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.IMedia
    public Cursor getContentFocusPoint(ContentResolver cr, Uri baseUri) {
        return AvindexStore.Images.Media.getContentFocusPoint(cr, baseUri);
    }

    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.IMedia
    public Cursor getContentFocusPoint(ContentResolver cr, Uri baseUri, String contentType) {
        return AvindexStore.Images.Media.getContentFocusPoint(cr, baseUri, contentType);
    }

    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.IMedia
    public boolean setContentFocusPoint(ContentResolver cr, ContentsIdentifier id) {
        return AvindexStore.Images.Media.setContentFocusPoint(cr, id.getContentUri(), id._id);
    }

    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.IMedia
    public boolean deleteImage(ContentResolver cr, ContentsIdentifier id) {
        return AvindexStore.Images.Media.deleteImage(cr, id.getContentUri(), id._id);
    }

    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.IMedia
    public AvindexContentInfo getContentInfo(String uniqId) {
        return AvindexStore.Images.Media.getImageInfo(uniqId);
    }

    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.IMedia
    public AvindexContentInfo getContentInfoFull(String uniqId) {
        return AvindexStore.Images.Media.getImageInfoFull(uniqId);
    }

    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.IMedia
    public String convColumn(String column) {
        return column;
    }

    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.IMedia
    public String[] convColumns(String[] columns) {
        return columns;
    }

    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.IMedia
    public int convContentType(int type) {
        return type;
    }
}
