package com.sony.imaging.app.base.playback.contents.aviadapter;

import android.database.Cursor;
import android.net.Uri;
import com.sony.scalar.provider.AvindexStore;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class FilesLocalDate implements ILocalDate {
    protected static Map<String, String> sTableToFiles = new HashMap();

    static {
        sTableToFiles.put("_id", "_id");
        sTableToFiles.put(ILocalDate.CONTENT_CREATED_LOCAL_DATE, ILocalDate.CONTENT_CREATED_LOCAL_DATE);
        sTableToFiles.put("_count", "_count");
        sTableToFiles.put("count_of_one_before", "count_of_one_before");
        sTableToFiles.put(ILocalDate.CONTENT_TYPE, ILocalDate.CONTENT_TYPE);
    }

    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.ICommonContainer
    public String convColumn(String column) {
        return sTableToFiles.get(column);
    }

    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.ICommonContainer
    public Uri getContentUri(String mediaId) {
        return AvindexStore.Files.LocalDate.getContentUri(mediaId);
    }

    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.ILocalDate
    public int getCursorIndex(Cursor c, String date) {
        return AvindexStore.Files.LocalDate.getCursorIndex(c, date);
    }
}
