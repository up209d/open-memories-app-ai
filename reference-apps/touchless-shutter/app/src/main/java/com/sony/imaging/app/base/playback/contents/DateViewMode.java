package com.sony.imaging.app.base.playback.contents;

import android.database.Cursor;
import android.util.Log;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.scalar.provider.AvindexStore;

/* loaded from: classes.dex */
public class DateViewMode extends ViewMode {
    private static final String MSG_MOVE_TO_ENTRY_NOT_FOUND = "moveToEntryPosition resume contents not found";
    private static final String MSG_QUERY_CONTENTS_FAILED = "Contents query failed";
    private static final String MSG_QUERY_GP_FAILED = "Group query returns null";
    private static final String MSG_RESULT_QUERY_GROUP = "queryInGroup date ";
    private static final String MSG_SEARCH_CONTENTS = "searchContentsPosition ";
    private static final String MSG_SEARCH_GP = "searchGroupPosition ";
    private static final String RESUME_CONTENTS_SELECTION_CLAUSE = "dcf_folder_number=? AND dcf_file_number=?";
    private static final String SELECTION_CLAUSE = "content_created_local_date=?";
    private static final String SORT_ORDER = "content_created_local_date_time ASC";
    private static final String TAG = "DateViewMode";

    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    protected String getMediaId() {
        String[] externalMedias = AvindexStore.getExternalMediaIds();
        return externalMedias[0];
    }

    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    protected int getContentType() {
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    protected synchronized boolean queryGroup() {
        boolean z;
        if (this.mGroupCursor != null) {
            this.mGroupCursor.close();
        }
        if (this.mContentsCursor != null) {
            this.mContentsCursor.close();
            this.mContentsCursor = null;
        }
        this.mGroupCursor = this.mHelper.getResolver().query(AvindexStore.Images.LocalDate.getContentUri(getMediaId()), getGroupProjection(), null, null, null);
        if (this.mGroupCursor == null) {
            Log.w(TAG, MSG_QUERY_GP_FAILED);
            z = false;
        } else {
            Log.i(TAG, LogHelper.getScratchBuilder(LogHelper.MSG_GROUP).append(this.mGroupCursor.getCount()).toString());
            z = true;
        }
        return z;
    }

    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    protected synchronized boolean queryInGroup() {
        boolean z;
        String date = this.mGroupCursor.getString(this.mGroupCursor.getColumnIndex("content_created_local_date"));
        Log.d(TAG, LogHelper.getScratchBuilder(MSG_RESULT_QUERY_GROUP).append(date).toString());
        String[] selectionArgs = {date};
        if (this.mContentsCursor != null) {
            this.mContentsCursor.close();
        }
        this.mContentsCursor = this.mHelper.getResolver().query(AvindexStore.Images.Media.getContentUri(getMediaId()), getContentsProjection(), SELECTION_CLAUSE, selectionArgs, SORT_ORDER);
        if (this.mContentsCursor != null) {
            Log.i(TAG, LogHelper.getScratchBuilder(LogHelper.MSG_CONTENTS).append(this.mContentsCursor.getCount()).toString());
        } else {
            Log.w(TAG, MSG_QUERY_CONTENTS_FAILED);
        }
        if (this.mWorkCursor != null) {
            this.mWorkCursor.close();
        }
        this.mWorkCursor = this.mHelper.getResolver().query(AvindexStore.Images.Media.getContentUri(getMediaId()), getContentsProjection(), SELECTION_CLAUSE, selectionArgs, SORT_ORDER);
        if (this.mContentsCursor != null) {
            if (this.mWorkCursor != null) {
                z = true;
            }
        }
        z = false;
        return z;
    }

    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    public synchronized int findGroup(SearchInfo info) {
        int result;
        String date = info.localDate;
        if (date == null) {
            result = -1;
        } else {
            result = AvindexStore.Images.LocalDate.getCursorIndex(this.mGroupCursor, date);
            Log.i(TAG, LogHelper.getScratchBuilder(MSG_SEARCH_GP).append(date).append(LogHelper.MSG_COLON).append(result).toString());
        }
        return result;
    }

    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    public synchronized int findContents(SearchInfo info) {
        int result;
        int folderNumber = info.folderNumber;
        int fileNumber = info.fileNumber;
        if (folderNumber < 0 || fileNumber < 0) {
            result = -1;
        } else {
            result = AvindexStore.Images.Media.getCursorIndex(this.mContentsCursor, folderNumber, fileNumber);
            Log.i(TAG, LogHelper.getScratchBuilder(MSG_SEARCH_CONTENTS).append(info.folderNumber).append(", ").append(info.fileNumber).append(LogHelper.MSG_COLON).append(result).toString());
        }
        return result;
    }

    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    public SearchInfo getSearchInfoFromResumeContents(SearchInfo info) {
        SearchInfo info2 = super.getSearchInfoFromResumeContents(info);
        if (info2 != null) {
            String[] selectionArgs = {String.valueOf(info2.folderNumber), String.valueOf(info2.fileNumber)};
            Cursor cursor = this.mHelper.getResolver().query(AvindexStore.Images.Media.getContentUri(getMediaId()), getContentsProjection(), RESUME_CONTENTS_SELECTION_CLAUSE, selectionArgs, SORT_ORDER);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        int index = cursor.getColumnIndex("content_created_local_date");
                        if (-1 != index) {
                            info2.localDate = cursor.getString(index);
                        }
                        int index2 = cursor.getColumnIndex("content_created_utc_date_time");
                        if (-1 != index2) {
                            info2.utcDate = cursor.getString(index2);
                        }
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
                cursor.close();
            } else {
                Log.w(TAG, MSG_QUERY_CONTENTS_FAILED);
            }
        }
        return info2;
    }

    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    public synchronized boolean moveToEntryPosition() {
        boolean result;
        result = false;
        SearchInfo info = getSearchInfoFromResumeContents(null);
        if (info != null) {
            ContentsManager.MoveHelper helper = new MoveHelperToSearchInfo(info);
            result = helper.apply(this);
            if (!result) {
                Log.w(TAG, MSG_MOVE_TO_ENTRY_NOT_FOUND);
            }
        }
        if (!result && (result = this.mGroupCursor.moveToLast()) && (result = queryInGroup())) {
            if (this.mContentsCursor.moveToLast()) {
                if (this.mWorkCursor.moveToLast()) {
                    result = true;
                }
            }
            result = false;
        }
        return result;
    }
}
