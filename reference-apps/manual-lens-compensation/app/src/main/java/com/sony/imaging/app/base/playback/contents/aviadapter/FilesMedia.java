package com.sony.imaging.app.base.playback.contents.aviadapter;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.util.SparseIntArray;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.playback.contents.ContentsIdentifier;
import com.sony.scalar.media.AvindexContentInfo;
import com.sony.scalar.provider.AvindexStore;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class FilesMedia implements IMedia {
    protected static final String MSG_UNKNOWN_COLUMN = "unknown column : ";
    protected static final String MSG_UNKNOWN_TYPE = "unknown type : ";
    protected static final String TAG = "FilesMedia";
    protected static SparseIntArray sContTypeToFiles;
    protected static Map<String, String> sTableToFiles = new HashMap();

    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.IMedia
    public Uri getContentUri(String mediaId) {
        return AvindexStore.Files.Media.getContentUri(mediaId);
    }

    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.IMedia
    public int getCursorIndex(Cursor c, int folderNumber, int fileNumber) {
        return AvindexStore.Files.Media.getCursorIndex(c, folderNumber, fileNumber);
    }

    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.IMedia
    public int getCursorIndex(Cursor c, String selection, String[] selectionArgs) {
        return getCursorIndex(c, selection, selectionArgs);
    }

    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.IMedia
    public Cursor getContentFocusPoint(ContentResolver cr, Uri baseUri) {
        return null;
    }

    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.IMedia
    public Cursor getContentFocusPoint(ContentResolver cr, Uri baseUri, String contentType) {
        return null;
    }

    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.IMedia
    public boolean setContentFocusPoint(ContentResolver cr, ContentsIdentifier id) {
        return false;
    }

    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.IMedia
    public boolean deleteImage(ContentResolver cr, ContentsIdentifier id) {
        return AvindexStore.Files.Media.delete(id.data);
    }

    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.IMedia
    public AvindexContentInfo getContentInfo(String uniqId) {
        return AvindexStore.Files.Media.getContentInfo(uniqId);
    }

    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.IMedia
    public AvindexContentInfo getContentInfoFull(String uniqId) {
        return AvindexStore.Files.Media.getContentInfoFull(uniqId);
    }

    static {
        sTableToFiles.put("_id", "_id");
        sTableToFiles.put("_data", "_data");
        sTableToFiles.put(IFolder.DCF_FOLDER_NUMBER, IFolder.DCF_FOLDER_NUMBER);
        sTableToFiles.put("dcf_file_number", "dcf_file_number");
        sTableToFiles.put("content_type", "content_type");
        sTableToFiles.put("exist_jpeg", "exist_jpeg");
        sTableToFiles.put("exist_mpo", "exist_mpo");
        sTableToFiles.put("exist_raw", "exist_raw");
        sTableToFiles.put("content_created_local_date_time", "content_created_local_date_time");
        sTableToFiles.put(ILocalDate.CONTENT_CREATED_LOCAL_DATE, ILocalDate.CONTENT_CREATED_LOCAL_DATE);
        sTableToFiles.put("content_created_local_time", "content_created_utc_date_time");
        sTableToFiles.put(IUtcDate.CONTENT_CREATED_UTC_DATE, IUtcDate.CONTENT_CREATED_UTC_DATE);
        sTableToFiles.put("content_created_utc_time", "content_created_utc_time");
        sTableToFiles.put("has_gps_info", "has_gps_info");
        sTableToFiles.put("time_zone", "time_zone");
        sTableToFiles.put("latitude", "latitude");
        sTableToFiles.put("longitude", "longitude");
        sTableToFiles.put("rec_order", "rec_order");
        sContTypeToFiles = new SparseIntArray();
        sContTypeToFiles.put(8, 8);
        sContTypeToFiles.put(1, 1);
        sContTypeToFiles.put(2, 2);
        sContTypeToFiles.put(4, 4);
        sContTypeToFiles.put(8, 8);
        sContTypeToFiles.put(0, 0);
    }

    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.IMedia
    public String convColumn(String column) {
        String result = sTableToFiles.get(column);
        if (result == null) {
            Log.w(TAG, LogHelper.getScratchBuilder(MSG_UNKNOWN_COLUMN).append(column).toString());
            return column;
        }
        return result;
    }

    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.IMedia
    public String[] convColumns(String[] columns) {
        if (columns == null) {
            return null;
        }
        String[] result = new String[columns.length];
        int c = columns.length;
        for (int i = 0; i < c; i++) {
            result[i] = sTableToFiles.get(columns[i]);
        }
        return result;
    }

    @Override // com.sony.imaging.app.base.playback.contents.aviadapter.IMedia
    public int convContentType(int type) {
        int result = sContTypeToFiles.get(type, 0);
        if (result == 0) {
            Log.w(TAG, LogHelper.getScratchBuilder(MSG_UNKNOWN_TYPE).append(type).toString());
            return type;
        }
        return result;
    }
}
