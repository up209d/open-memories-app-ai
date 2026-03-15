package com.sony.imaging.app.base.playback.contents;

import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.base.playback.contents.aviadapter.FilesLocalDate;
import com.sony.imaging.app.base.playback.contents.aviadapter.IFolder;
import com.sony.imaging.app.base.playback.contents.aviadapter.ILocalDate;
import com.sony.imaging.app.base.playback.contents.aviadapter.ImagesLocalDate;
import com.sony.imaging.app.util.Environment;

/* loaded from: classes.dex */
public class MixedTypeDateViewMode extends MixedTypeViewMode {
    protected static final String CONTENT_ORDER;
    protected static final String GROUP_ORDER;
    private static final String MSG_QUERY_CONTENTS_FAILED = "Contents query failed";
    private static final String MSG_QUERY_GP_FAILED = "Group query returns null";
    private static final String MSG_RESULT_QUERY_GROUP = "queryInGroup date ";
    private static final String MSG_SEARCH_GP = "searchGroupPosition ";
    protected static final String RESUME_CONTENTS_SELECTION_CLAUSE;
    protected static final String SELECTION_CLAUSE;
    private static final String TAG = "DateViewMode";
    protected static final ILocalDate sLocalDateAdapter;
    protected String[] mContentTypeFilterForContent;

    static {
        sLocalDateAdapter = Environment.isAvindexFilesSupported() ? new FilesLocalDate() : new ImagesLocalDate();
        SELECTION_CLAUSE = sMediaAdapter.convColumn(ILocalDate.CONTENT_CREATED_LOCAL_DATE) + ViewMode.EQUAL_ARGS;
        RESUME_CONTENTS_SELECTION_CLAUSE = sMediaAdapter.convColumn(IFolder.DCF_FOLDER_NUMBER) + "=? AND " + sMediaAdapter.convColumn("dcf_file_number") + ViewMode.EQUAL_ARGS;
        GROUP_ORDER = sLocalDateAdapter.convColumn(ILocalDate.CONTENT_CREATED_LOCAL_DATE) + ViewMode.ASC;
        CONTENT_ORDER = sMediaAdapter.convColumn("content_created_local_date_time") + ViewMode.ASC;
    }

    protected ILocalDate getLocalDate() {
        return sLocalDateAdapter;
    }

    @Override // com.sony.imaging.app.base.playback.contents.MixedTypeViewMode, com.sony.imaging.app.base.playback.contents.ViewMode
    public void initialize(ProviderHelper helper) {
        this.mContentTypeFilterForContent = null;
        super.initialize(helper);
    }

    public boolean filterByContentType(String[] types) {
        if (this.mInitThread != null) {
            return false;
        }
        this.mContentTypeFilterForContent = types;
        if (isInitialQueryDone()) {
            SearchInfo info = getSearchInfo(null);
            ContentsManager.MoveHelper helper = new MoveHelperToSearchInfo(info);
            queryGroup();
            if (!helper.apply(this)) {
                moveToLast();
            }
        }
        return true;
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
        Uri uri = sLocalDateAdapter.getContentUri(getMediaId());
        this.mGroupCursor = this.mHelper.getResolver().query(uri, getGroupProjection(), null, null, getGroupOrder());
        if (this.mGroupCursor == null) {
            Log.w(TAG, MSG_QUERY_GP_FAILED);
            z = false;
        } else {
            Log.i(TAG, LogHelper.getScratchBuilder(LogHelper.MSG_GROUP).append(this.mGroupCursor.getCount()).toString());
            z = true;
        }
        return z;
    }

    protected String getGroupOrder() {
        return GROUP_ORDER;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    public synchronized boolean queryInGroup() {
        boolean z;
        String date = this.mGroupCursor.getString(this.mGroupCursor.getColumnIndex(ILocalDate.CONTENT_CREATED_LOCAL_DATE));
        Log.d(TAG, LogHelper.getScratchBuilder(MSG_RESULT_QUERY_GROUP).append(date).toString());
        if (this.mContentsCursor != null) {
            this.mContentsCursor.close();
        }
        int numOfContentType = this.mContentTypeFilterForContent == null ? 0 : this.mContentTypeFilterForContent.length;
        String[] selectionArgs = new String[numOfContentType + 1];
        selectionArgs[0] = date;
        String selection = SELECTION_CLAUSE;
        if (this.mContentTypeFilterForContent != null) {
            String selectionForContent = createClauseToSelectSomeValue(sMediaAdapter.convColumn("content_type"), numOfContentType);
            selection = LogHelper.getScratchBuilder(SELECTION_CLAUSE).append(ViewMode.AND).append(selectionForContent).toString();
            System.arraycopy(this.mContentTypeFilterForContent, 0, selectionArgs, 1, numOfContentType);
        }
        this.mContentsCursor = this.mHelper.getResolver().query(sMediaAdapter.getContentUri(getMediaId()), getContentsProjection(), selection, selectionArgs, getContentOrder());
        if (this.mContentsCursor != null) {
            Log.i(TAG, LogHelper.getScratchBuilder(LogHelper.MSG_CONTENTS).append(this.mContentsCursor.getCount()).toString());
        } else {
            Log.w(TAG, MSG_QUERY_CONTENTS_FAILED);
        }
        if (this.mWorkCursor != null) {
            this.mWorkCursor.close();
        }
        this.mWorkCursor = this.mHelper.getResolver().query(sMediaAdapter.getContentUri(getMediaId()), getContentsProjection(), selection, selectionArgs, getContentOrder());
        if (this.mContentsCursor != null) {
            if (this.mWorkCursor != null) {
                z = true;
            }
        }
        z = false;
        return z;
    }

    protected String getContentOrder() {
        return CONTENT_ORDER;
    }

    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    public synchronized int findGroup(SearchInfo info) {
        int result;
        String date = info.localDate;
        if (date == null) {
            result = -1;
        } else {
            result = sLocalDateAdapter.getCursorIndex(this.mGroupCursor, date);
            Log.i(TAG, LogHelper.getScratchBuilder(MSG_SEARCH_GP).append(date).append(" : ").append(result).toString());
        }
        return result;
    }

    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    public SearchInfo getSearchInfoFromResumeContents(SearchInfo info) {
        SearchInfo info2 = super.getSearchInfoFromResumeContents(info);
        if (info2 != null) {
            String[] selectionArgs = {String.valueOf(info2.folderNumber), String.valueOf(info2.fileNumber)};
            Cursor cursor = this.mHelper.getResolver().query(sMediaAdapter.getContentUri(getMediaId()), getContentsProjection(), RESUME_CONTENTS_SELECTION_CLAUSE, selectionArgs, getContentOrder());
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        int index = cursor.getColumnIndex(sMediaAdapter.convColumn(ILocalDate.CONTENT_CREATED_LOCAL_DATE));
                        if (-1 != index) {
                            info2.localDate = cursor.getString(index);
                        }
                        int index2 = cursor.getColumnIndex(sMediaAdapter.convColumn("content_created_utc_date_time"));
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
    protected String getColumnGroupCountOfOneBefore() {
        return sLocalDateAdapter.convColumn("count_of_one_before");
    }

    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    protected String getColumnGroupCount() {
        return sLocalDateAdapter.convColumn("_count");
    }
}
