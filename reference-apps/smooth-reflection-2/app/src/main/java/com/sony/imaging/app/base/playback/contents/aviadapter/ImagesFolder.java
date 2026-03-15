package com.sony.imaging.app.base.playback.contents.aviadapter;

import android.database.Cursor;
import android.net.Uri;
import com.sony.scalar.provider.AvindexStore;

/* loaded from: classes.dex */
public class ImagesFolder implements IFolder {
    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.ICommonContainer
    public String convColumn(String column) {
        return column;
    }

    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.ICommonContainer
    public Uri getContentUri(String mediaId) {
        return AvindexStore.Images.Folder.getContentUri(mediaId);
    }

    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.IFolder
    public int getCursorIndex(Cursor c, int folderNumber) {
        return AvindexStore.Images.Folder.getCursorIndex(c, folderNumber);
    }
}
