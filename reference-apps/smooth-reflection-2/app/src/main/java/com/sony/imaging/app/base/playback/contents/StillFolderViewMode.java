package com.sony.imaging.app.base.playback.contents;

import android.util.Log;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.base.playback.contents.aviadapter.IFolder;
import com.sony.scalar.provider.AvindexStore;

/* loaded from: classes.dex */
public class StillFolderViewMode extends ViewMode {
    private static final String[] GROUP_QUERY_PROJECTION = {"_id", "_count", "count_of_one_before", IFolder.DCF_FOLDER_NUMBER};
    private static final String MSG_MOVE_TO_ENTRY_NOT_FOUND = "moveToEntryPosition resume contents not found";
    private static final String MSG_QUERY_CONTENTS_FAILED = "Contents query failed";
    private static final String MSG_QUERY_GP_FAILED = "Group query returns null";
    private static final String MSG_RESULT_QUERY_GROUP = "queryInGroup dcf ";
    private static final String MSG_SEARCH_CONTENTS = "searchContentsPosition ";
    private static final String MSG_SEARCH_GP = "searchGroupPosition ";
    private static final String SELECTION_CLAUSE = "dcf_folder_number=?";
    private static final String SORT_ORDER = "dcf_file_number ASC";
    private static final String TAG = "StillFolderViewMode";

    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    protected String getMediaId() {
        String[] externalMedias = AvindexStore.getExternalMediaIds();
        return externalMedias[0];
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    public String[] getGroupProjection() {
        return GROUP_QUERY_PROJECTION;
    }

    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    protected int getContentType() {
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    public synchronized boolean queryGroup() {
        boolean z;
        if (this.mGroupCursor != null) {
            this.mGroupCursor.close();
        }
        if (this.mContentsCursor != null) {
            this.mContentsCursor.close();
            this.mContentsCursor = null;
        }
        this.mGroupCursor = this.mHelper.getResolver().query(AvindexStore.Images.Folder.getContentUri(getMediaId()), getGroupProjection(), null, null, null);
        if (this.mGroupCursor == null) {
            Log.w(TAG, MSG_QUERY_GP_FAILED);
            z = false;
        } else {
            Log.i(TAG, LogHelper.getScratchBuilder(LogHelper.MSG_GROUP).append(this.mGroupCursor.getCount()).toString());
            z = true;
        }
        return z;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    public synchronized boolean queryInGroup() {
        boolean z;
        int dcfFolderNum = this.mGroupCursor.getInt(this.mGroupCursor.getColumnIndex(IFolder.DCF_FOLDER_NUMBER));
        Log.d(TAG, LogHelper.getScratchBuilder(MSG_RESULT_QUERY_GROUP).append(dcfFolderNum).toString());
        String[] selectionArgs = {String.valueOf(dcfFolderNum)};
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
        int folderNumber = info.folderNumber;
        if (folderNumber < 0) {
            result = -1;
        } else {
            result = AvindexStore.Images.Folder.getCursorIndex(this.mGroupCursor, folderNumber);
            Log.i(TAG, LogHelper.getScratchBuilder(MSG_SEARCH_GP).append(folderNumber).append(LogHelper.MSG_COLON).append(result).toString());
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
