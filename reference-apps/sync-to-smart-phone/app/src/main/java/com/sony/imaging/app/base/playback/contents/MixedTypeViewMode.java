package com.sony.imaging.app.base.playback.contents;

import android.database.Cursor;
import android.util.Log;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.base.playback.contents.ViewMode;
import com.sony.imaging.app.base.playback.contents.aviadapter.AvindexStoreFiles;
import com.sony.imaging.app.base.playback.contents.aviadapter.AvindexStoreImages;
import com.sony.imaging.app.base.playback.contents.aviadapter.FilesMedia;
import com.sony.imaging.app.base.playback.contents.aviadapter.IAvindexStore;
import com.sony.imaging.app.base.playback.contents.aviadapter.IFolder;
import com.sony.imaging.app.base.playback.contents.aviadapter.ILocalDate;
import com.sony.imaging.app.base.playback.contents.aviadapter.IMedia;
import com.sony.imaging.app.base.playback.contents.aviadapter.IUtcDate;
import com.sony.imaging.app.base.playback.contents.aviadapter.ImagesMedia;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.provider.AvindexStore;

/* loaded from: classes.dex */
public abstract class MixedTypeViewMode extends ViewMode {
    private static final String[] CONTENTS_QUERY_PROJECTION_FOR_FILES = {"_id", "_data", "dcf_file_number", IFolder.DCF_FOLDER_NUMBER, ILocalDate.CONTENT_CREATED_LOCAL_DATE, IUtcDate.CONTENT_CREATED_UTC_DATE, "content_created_local_date_time", "content_created_utc_date_time", "exist_jpeg", "exist_raw", "exist_mpo", "rec_order", "content_type"};
    protected static final int[] IMAGE_CONTENT_TYPES;
    protected static final int[] MIXED_CONTENT_TYPES;
    private static final String MSG_MOVE_TO_ENTRY_NOT_FOUND = "moveToEntryPosition resume contents not found";
    private static final String MSG_SEARCH_CONTENTS = "searchContentsPosition ";
    protected static final String MSG_SET_CONTENT_POS_NOT_FOUND = "setContentPos : current content NOT found";
    private static final String TAG = "MixedTypeViewMode";
    protected static final IAvindexStore sAvindexStoreAdapter;
    private static StringBuilder sBuilder;
    protected static final IMedia sMediaAdapter;

    static {
        sAvindexStoreAdapter = Environment.isAvindexFilesSupported() ? new AvindexStoreFiles() : new AvindexStoreImages();
        sMediaAdapter = Environment.isAvindexFilesSupported() ? new FilesMedia() : new ImagesMedia();
        IMAGE_CONTENT_TYPES = new int[]{1};
        MIXED_CONTENT_TYPES = new int[]{1, 5, 7};
        sBuilder = new StringBuilder();
    }

    public MixedTypeViewMode() {
        this.mQueryProjection = sMediaAdapter.convColumns(CONTENTS_QUERY_PROJECTION_FOR_FILES);
    }

    public IAvindexStore getAvindexStoreAdapter() {
        return sAvindexStoreAdapter;
    }

    public IMedia getAvindexStoreMediaAdapter() {
        return sMediaAdapter;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    public String getMediaId() {
        String[] externalMedias = AvindexStore.getExternalMediaIds();
        return externalMedias[0];
    }

    protected int[] getContentTypes() {
        return Environment.isAvindexFilesSupported() ? MIXED_CONTENT_TYPES : IMAGE_CONTENT_TYPES;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static final String createClauseToSelectSomeValue(String column, int count) {
        if (count == 0) {
            return null;
        }
        StringBuilder builder = sBuilder.replace(0, sBuilder.length(), LogHelper.MSG_OPEN_BRACKET).append(column).append(ViewMode.EQUAL_ARGS);
        for (int i = 1; i < count; i++) {
            builder.append(ViewMode.OR).append(column).append(ViewMode.EQUAL_ARGS);
        }
        builder.append(LogHelper.MSG_CLOSE_BRACKET);
        return builder.toString();
    }

    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    @Deprecated
    protected final int getContentType() {
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    public void initialize(ProviderHelper helper) {
        this.mHelper = helper;
        this.mIsQueryDone = false;
        this.mTotalCount = -1;
        Log.i(TAG, "starts loadMedia");
        sAvindexStoreAdapter.loadMedia(getMediaId(), getContentTypes());
        Log.i(TAG, "ends loadMedia");
        this.mHelper.getResolver().registerContentObserver(sMediaAdapter.getContentUri(getMediaId()), true, this.mObserver);
    }

    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    public void terminate() {
        cancelInitialQuery();
        synchronized (this) {
            this.mHandler.removeCallbacksAndMessages(null);
            this.mHelper.getResolver().unregisterContentObserver(this.mObserver);
            if (this.mGroupCursor != null) {
                this.mGroupCursor.close();
                this.mGroupCursor = null;
            }
            if (this.mContentsCursor != null) {
                this.mContentsCursor.close();
                this.mContentsCursor = null;
            }
            if (this.mWorkCursor != null) {
                this.mWorkCursor.close();
                this.mWorkCursor = null;
            }
        }
        this.mTotalCount = -1;
    }

    /* loaded from: classes.dex */
    protected class InitialQueryThread extends ViewMode.InitialQueryThread {
        protected InitialQueryThread(ContentsManager.QueryCallback callback) {
            super(callback);
        }

        @Override // com.sony.imaging.app.base.playback.contents.ViewMode.InitialQueryThread, java.lang.Thread, java.lang.Runnable
        public void run() {
            boolean aResult = false;
            Log.i(MixedTypeViewMode.TAG, "InitialQueryTask start");
            if (!MixedTypeViewMode.sAvindexStoreAdapter.waitLoadMediaComplete(MixedTypeViewMode.this.getMediaId(), MixedTypeViewMode.this.getContentTypes())) {
                Log.i(MixedTypeViewMode.TAG, "waitLoadMediaComplete returns false");
            } else {
                Log.i(MixedTypeViewMode.TAG, "waitLoadMediaComplete ends");
                if (!MixedTypeViewMode.sAvindexStoreAdapter.waitAndUpdateDatabase(MixedTypeViewMode.this.mHelper.getResolver(), MixedTypeViewMode.this.getMediaId())) {
                    Log.i(MixedTypeViewMode.TAG, "waitAndUpdateDatabase returns false");
                } else {
                    Log.i(MixedTypeViewMode.TAG, "waitAndUpdateDatabase ends");
                    if (!this.mCancelled) {
                        aResult = MixedTypeViewMode.this.queryGroup();
                        if (!this.mCancelled && aResult && MixedTypeViewMode.this.getGroupCount() > 0) {
                            aResult = MixedTypeViewMode.this.moveToEntryPosition();
                        }
                    }
                }
            }
            if (this.mCancelled) {
                Log.i(MixedTypeViewMode.TAG, "InitialQueryTask cancelled");
                return;
            }
            MixedTypeViewMode.this.mIsQueryDone = aResult;
            Runnable runnable = new Runnable() { // from class: com.sony.imaging.app.base.playback.contents.MixedTypeViewMode.InitialQueryThread.1
                @Override // java.lang.Runnable
                public void run() {
                    Log.d(MixedTypeViewMode.TAG, "InitialQueryTask onCallback");
                    MixedTypeViewMode.this.mInitThread = null;
                    if (!InitialQueryThread.this.mCancelled) {
                        InitialQueryThread.this.mCallback.onCallback(MixedTypeViewMode.this.mIsQueryDone);
                    } else {
                        Log.i(MixedTypeViewMode.TAG, "InitialQueryTask cancelled");
                    }
                }
            };
            MixedTypeViewMode.this.mHandler.post(runnable);
            Log.d(MixedTypeViewMode.TAG, "InitialQueryTask end");
            LogHelper.onThreadEnd(this);
        }
    }

    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    protected ViewMode.InitialQueryThread getInitialQueryThread(ContentsManager.QueryCallback callback) {
        return new InitialQueryThread(callback);
    }

    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    public void cancelInitialQuery() {
        if (this.mInitThread != null) {
            Log.i(TAG, "cancelling init thread");
            synchronized (this) {
                this.mInitThread.mCancelled = true;
            }
            sAvindexStoreAdapter.cancelWaitLoadMediaComplete(getMediaId());
            sAvindexStoreAdapter.cancelWaitAndUpdateDatabase(this.mHelper.getResolver(), getMediaId());
            try {
                this.mInitThread.join();
                Log.i(TAG, "join init thread");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.mInitThread = null;
    }

    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    public synchronized long getId() {
        long j = -1;
        synchronized (this) {
            if (this.mGroupCursor != null && this.mContentsCursor != null && !this.mContentsCursor.isBeforeFirst() && !this.mContentsCursor.isAfterLast()) {
                j = this.mHelper.getLong(this.mContentsCursor, sMediaAdapter.convColumn("_id"));
            }
        }
        return j;
    }

    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    public synchronized String getData() {
        String str = null;
        synchronized (this) {
            if (this.mGroupCursor != null && this.mContentsCursor != null && !this.mContentsCursor.isBeforeFirst() && !this.mContentsCursor.isAfterLast()) {
                str = this.mHelper.getString(this.mContentsCursor, sMediaAdapter.convColumn("_data"));
            }
        }
        return str;
    }

    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    public synchronized ContentsIdentifier getContentsIdentifier() {
        return new ContentsIdentifier(getId(), getData(), getMediaId(), getInt(sMediaAdapter.convColumn("content_type")), sMediaAdapter);
    }

    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    public synchronized ContentsIdentifier getContentsIdentifierAt(int position) {
        ContentsIdentifier contentsIdentifier;
        if (this.mWorkCursor == null) {
            contentsIdentifier = new ContentsIdentifier(-1L, null, null);
        } else if (!this.mWorkCursor.moveToPosition(position)) {
            Log.w(TAG, LogHelper.getScratchBuilder("failed to move : ").append(position).toString());
            contentsIdentifier = new ContentsIdentifier(-1L, null, null);
        } else {
            long workId = this.mHelper.getLong(this.mWorkCursor, "_id");
            String workData = this.mHelper.getString(this.mWorkCursor, sMediaAdapter.convColumn("_data"));
            String workMediaId = getMediaId();
            int contentType = getInt(sMediaAdapter.convColumn("content_type"));
            contentsIdentifier = new ContentsIdentifier(workId, workData, workMediaId, contentType, sMediaAdapter);
        }
        return contentsIdentifier;
    }

    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    public ContentsIdentifier getResumeId() {
        Cursor c = queryLastContent();
        if (c == null) {
            return null;
        }
        String uniqId = c.getString(c.getColumnIndex(sMediaAdapter.convColumn("_data")));
        Log.i(TAG, LogHelper.getScratchBuilder("getResumeId found : ").append(uniqId).toString());
        return new ContentsIdentifier(-1L, uniqId, getMediaId());
    }

    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    protected Cursor queryLastContent() {
        Cursor c = sMediaAdapter.getContentFocusPoint(this.mHelper.getResolver(), sMediaAdapter.getContentUri(getMediaId()));
        if (c != null && !c.moveToFirst()) {
            Log.i(TAG, LogHelper.getScratchBuilder("queryLastContent NOT null but no contents : ").append(c.getCount()).toString());
            c.close();
            return null;
        }
        return c;
    }

    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    public void setContentPos() {
        ContentsIdentifier id = getContentsIdentifier();
        if (id != null) {
            boolean result = sMediaAdapter.setContentFocusPoint(this.mHelper.getResolver(), id);
            Log.i(TAG, LogHelper.getScratchBuilder("setContentPosId :").append(id._id).append(" -> ").append(result).toString());
        } else {
            Log.i(TAG, MSG_SET_CONTENT_POS_NOT_FOUND);
        }
    }

    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    public synchronized int findContents(SearchInfo info) {
        int result;
        int folderNumber = info.folderNumber;
        int fileNumber = info.fileNumber;
        if (folderNumber < 0 || fileNumber < 0) {
            result = -1;
        } else {
            result = sMediaAdapter.getCursorIndex(this.mContentsCursor, folderNumber, fileNumber);
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
            result = this.mContentsCursor.moveToLast() && this.mWorkCursor.moveToLast();
            if (!result && moveGroupToPreviousNonEmpty(false)) {
                if (this.mContentsCursor.moveToLast()) {
                    if (this.mWorkCursor.moveToLast()) {
                        result = true;
                    }
                }
                result = false;
            }
        }
        return result;
    }

    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    public synchronized boolean moveToNext() {
        boolean result = false;
        synchronized (this) {
            if (this.mGroupCursor != null && this.mContentsCursor != null) {
                result = false;
                if (this.mContentsCursor.isLast() || this.mContentsCursor.getCount() == 0) {
                    if (moveGroupToNextNonEmpty(false)) {
                        result = this.mContentsCursor.moveToFirst();
                    }
                } else {
                    result = this.mContentsCursor.moveToNext();
                }
            }
        }
        return result;
    }

    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    public synchronized boolean moveToPrevious() {
        boolean result = false;
        synchronized (this) {
            if (this.mGroupCursor != null && this.mContentsCursor != null) {
                result = false;
                if (this.mContentsCursor.isFirst() || this.mContentsCursor.getCount() == 0) {
                    if (moveGroupToPreviousNonEmpty(false)) {
                        result = this.mContentsCursor.moveToLast();
                    }
                } else {
                    result = this.mContentsCursor.moveToPrevious();
                }
            }
        }
        return result;
    }

    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    public synchronized boolean moveToFirst() {
        boolean result = false;
        synchronized (this) {
            if (this.mGroupCursor != null && (result = this.mGroupCursor.moveToFirst()) && (result = queryInGroup()) && !(result = this.mContentsCursor.moveToFirst()) && moveGroupToNextNonEmpty(false)) {
                result = this.mContentsCursor.moveToFirst();
            }
        }
        return result;
    }

    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    public synchronized boolean moveToLast() {
        boolean result = false;
        synchronized (this) {
            if (this.mGroupCursor != null && (result = this.mGroupCursor.moveToLast()) && (result = queryInGroup()) && !(result = this.mContentsCursor.moveToLast()) && moveGroupToPreviousNonEmpty(false)) {
                result = this.mContentsCursor.moveToLast();
            }
        }
        return result;
    }

    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    public synchronized boolean isFirst() {
        return super.isFirst();
    }

    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    public synchronized boolean isLast() {
        return super.isLast();
    }

    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    protected SearchInfo SearchInfoFromCursor(Cursor cursor, SearchInfo info, boolean useDateTime) {
        if (cursor == null || -1 == cursor.getPosition()) {
            return null;
        }
        int folderNumber = -1;
        int fileNumber = -1;
        String localDate = null;
        String utcDate = null;
        int index = cursor.getColumnIndex(IFolder.DCF_FOLDER_NUMBER);
        if (-1 != index) {
            folderNumber = cursor.getInt(index);
        }
        int index2 = cursor.getColumnIndex("dcf_file_number");
        if (-1 != index2) {
            fileNumber = cursor.getInt(index2);
        }
        int index3 = cursor.getColumnIndex(ILocalDate.CONTENT_CREATED_LOCAL_DATE);
        if (-1 != index3) {
            localDate = cursor.getString(index3);
        }
        int index4 = cursor.getColumnIndex(IUtcDate.CONTENT_CREATED_UTC_DATE);
        if (-1 != index4) {
            utcDate = cursor.getString(index4);
        }
        if (info == null) {
            info = new SearchInfo(folderNumber, fileNumber, localDate, utcDate);
        } else {
            info.fileNumber = fileNumber;
            info.folderNumber = folderNumber;
            info.localDate = localDate;
            info.utcDate = utcDate;
        }
        if (useDateTime) {
            int index5 = cursor.getColumnIndex("content_created_local_date_time");
            if (-1 != index5) {
                info.localDateTime = cursor.getLong(index5);
            }
            int index6 = cursor.getColumnIndex("content_created_utc_date_time");
            if (-1 != index6) {
                info.utcDateTime = cursor.getLong(index6);
            }
        }
        return info;
    }
}
