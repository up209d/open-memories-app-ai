package com.sony.imaging.app.base.playback.contents.aviadapter;

import android.database.Cursor;
import android.net.Uri;
import com.sony.scalar.provider.AvindexStore;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class FilesFolder implements IFolder {
    protected static Map<String, String> sTableToFiles = new HashMap();

    static {
        sTableToFiles.put("_id", "_id");
        sTableToFiles.put(IFolder.DCF_FOLDER_NUMBER, IFolder.DCF_FOLDER_NUMBER);
        sTableToFiles.put("_count", "_count");
        sTableToFiles.put("count_of_one_before", "count_of_one_before");
        sTableToFiles.put(IFolder.CONTENT_TYPE, IFolder.CONTENT_TYPE);
    }

    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.ICommonContainer
    public String convColumn(String column) {
        return sTableToFiles.get(column);
    }

    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.ICommonContainer
    public Uri getContentUri(String mediaId) {
        return AvindexStore.Files.Folder.getContentUri(mediaId);
    }

    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.IFolder
    public int getCursorIndex(Cursor c, int folderNumber) {
        return AvindexStore.Files.Folder.getCursorIndex(c, folderNumber);
    }
}
