package com.sony.imaging.app.base.playback.contents;

import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.util.Log;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.scalar.provider.AvindexStore;

/* loaded from: classes.dex */
public abstract class ViewMode {
    private static final String AV_SEPARATOR = "|";
    private static final String[] CONTENTS_QUERY_PROJECTION = {"_id", "_data", "dcf_file_number", "dcf_folder_number", "content_created_local_date", "content_created_utc_date", "content_created_local_date_time", "content_created_utc_date_time", "exist_jpeg", "exist_raw", "exist_mpo", "rec_order", "content_type"};
    private static final String MSG_CANCEL_INITIAL_QUERY = "cancelling init thread";
    private static final String MSG_END_INITIAL_QUERY = "InitialQueryTask end";
    private static final String MSG_END_LOAD = "ends loadMedia";
    private static final String MSG_END_WAIT_LOAD = "waitLoadMediaComplete ends";
    private static final String MSG_END_WAIT_UPDATE = "waitAndUpdateDatabase ends";
    private static final String MSG_FAILED_TO_MOVE = "failed to move : ";
    private static final String MSG_FAIL_SET_PROJECTION_ALREADY_QUERIED = "setContentsProjection IGNORED query has already done";
    private static final String MSG_FAIL_SET_PROJECTION_INVALID_ARG = "setContentsProjection FAIL invalid column set : ";
    private static final String MSG_GET_RESUME_ID = "getResumeId found : ";
    private static final String MSG_GET_TOTAL_COUNT_GROUP_IS_NONE = "getContentsTotalCount : group count is zero";
    private static final String MSG_GET_TOTAL_POSITION_GROUP_IS_NONE = "getContentsTotalPosition : group count is zero";
    private static final String MSG_INITIAL_QUERY_CALLBACK = "InitialQueryTask onCallback";
    private static final String MSG_INITIAL_QUERY_CANCELLED = "InitialQueryTask cancelled";
    private static final String MSG_INITIAL_QUERY_STARTED = "Initial Query already started";
    private static final String MSG_JOIN_INITIAL_QUERY_THREAD = "join init thread";
    private static final String MSG_LAST_CONTENT_NOT_FOUND = "queryLastContent NOT null but no contents : ";
    private static final String MSG_OBSERVER_EXISTS = "registerObserver observer already exists";
    private static final String MSG_OBSERVER_ONCHANGE = "ContentObserver.onChange";
    private static final String MSG_SET_CONTENT_POS = "setContentPosId :";
    private static final String MSG_START_INITIAL_QUERY = "InitialQueryTask start";
    private static final String MSG_START_LOAD = "starts loadMedia";
    private static final String MSG_WAIT_LOAD_CANCELLED = "waitLoadMediaComplete returns false";
    private static final String MSG_WAIT_UPDATE_CANCELLED = "waitAndUpdateDatabase returns false";
    private static final String TAG = "ViewMode";
    protected Cursor mContentsCursor;
    protected Cursor mGroupCursor;
    protected ProviderHelper mHelper;
    protected InitialQueryThread mInitThread;
    protected volatile boolean mIsQueryDone;
    private ContentObserver mUserObserver;
    protected Cursor mWorkCursor;
    private String[] mQueryProjection = CONTENTS_QUERY_PROJECTION;
    private int mTotalCount = -1;
    protected Handler mHandler = new Handler();
    protected ContentObserver mObserver = new ContentObserver(this.mHandler) { // from class: com.sony.imaging.app.base.playback.contents.ViewMode.1
        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            Log.d(ViewMode.TAG, ViewMode.MSG_OBSERVER_ONCHANGE);
            if (ViewMode.this.mIsQueryDone) {
                if (ViewMode.this.mUserObserver != null) {
                    ViewMode.this.mUserObserver.dispatchChange(selfChange);
                }
            } else if (ViewMode.this.mInitThread != null) {
                synchronized (ViewMode.this.mInitThread) {
                    ViewMode.this.mInitThread.mWantsRetry = true;
                    ViewMode.this.mInitThread.notifyAll();
                }
            }
        }
    };

    protected abstract int getContentType();

    protected abstract String getMediaId();

    public abstract boolean moveToEntryPosition();

    protected abstract boolean queryGroup();

    protected abstract boolean queryInGroup();

    public void initialize(ProviderHelper helper) {
        this.mHelper = helper;
        this.mIsQueryDone = false;
        this.mTotalCount = -1;
        Log.i(TAG, MSG_START_LOAD);
        AvindexStore.loadMedia(getMediaId(), getContentType());
        Log.i(TAG, MSG_END_LOAD);
        this.mHelper.getResolver().registerContentObserver(AvindexStore.Images.Media.getContentUri(getMediaId()), true, this.mObserver);
    }

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

    public void registerObserver(ContentObserver observer) {
        if (this.mUserObserver != null) {
            Log.w(TAG, MSG_OBSERVER_EXISTS);
        } else {
            this.mUserObserver = observer;
        }
    }

    public void unregisterObserver(ContentObserver observer) {
        if (this.mUserObserver == observer) {
            this.mUserObserver = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class InitialQueryThread extends Thread {
        private ContentsManager.QueryCallback mCallback;
        public volatile boolean mCancelled;
        private volatile boolean mWantsRetry;

        protected InitialQueryThread(ContentsManager.QueryCallback callback) {
            super(InitialQueryThread.class.getSimpleName());
            this.mCancelled = false;
            this.mCallback = callback;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            boolean aResult = false;
            Log.i(ViewMode.TAG, ViewMode.MSG_START_INITIAL_QUERY);
            this.mWantsRetry = false;
            if (!AvindexStore.waitLoadMediaComplete(ViewMode.this.getMediaId())) {
                Log.i(ViewMode.TAG, ViewMode.MSG_WAIT_LOAD_CANCELLED);
            } else {
                Log.i(ViewMode.TAG, ViewMode.MSG_END_WAIT_LOAD);
                if (!AvindexStore.Images.waitAndUpdateDatabase(ViewMode.this.mHelper.getResolver(), ViewMode.this.getMediaId())) {
                    Log.i(ViewMode.TAG, ViewMode.MSG_WAIT_UPDATE_CANCELLED);
                } else {
                    Log.i(ViewMode.TAG, ViewMode.MSG_END_WAIT_UPDATE);
                    if (!this.mCancelled) {
                        aResult = ViewMode.this.queryGroup();
                        if (!this.mCancelled && aResult && ViewMode.this.getGroupCount() > 0) {
                            aResult = ViewMode.this.moveToEntryPosition();
                        }
                    }
                }
            }
            if (this.mCancelled) {
                Log.i(ViewMode.TAG, ViewMode.MSG_INITIAL_QUERY_CANCELLED);
                return;
            }
            ViewMode.this.mIsQueryDone = aResult;
            Runnable runnable = new Runnable() { // from class: com.sony.imaging.app.base.playback.contents.ViewMode.InitialQueryThread.1
                @Override // java.lang.Runnable
                public void run() {
                    Log.d(ViewMode.TAG, ViewMode.MSG_INITIAL_QUERY_CALLBACK);
                    ViewMode.this.mInitThread = null;
                    if (!InitialQueryThread.this.mCancelled) {
                        InitialQueryThread.this.mCallback.onCallback(ViewMode.this.mIsQueryDone);
                    } else {
                        Log.i(ViewMode.TAG, ViewMode.MSG_INITIAL_QUERY_CANCELLED);
                    }
                }
            };
            ViewMode.this.mHandler.post(runnable);
            Log.d(ViewMode.TAG, ViewMode.MSG_END_INITIAL_QUERY);
            LogHelper.onThreadEnd(this);
        }
    }

    public boolean isInitialQueryDone() {
        return this.mIsQueryDone;
    }

    public boolean startInitialQuery(final ContentsManager.QueryCallback callback, boolean forceQuery) {
        if (isInitialQueryDone() && !forceQuery) {
            this.mHandler.post(new Runnable() { // from class: com.sony.imaging.app.base.playback.contents.ViewMode.2
                @Override // java.lang.Runnable
                public void run() {
                    callback.onCallback(true);
                }
            });
        } else {
            if (this.mInitThread != null) {
                Log.i(TAG, MSG_INITIAL_QUERY_STARTED);
                return false;
            }
            this.mIsQueryDone = false;
            this.mInitThread = new InitialQueryThread(callback);
            this.mInitThread.start();
        }
        return true;
    }

    public void cancelInitialQuery() {
        if (this.mInitThread != null) {
            Log.i(TAG, MSG_CANCEL_INITIAL_QUERY);
            synchronized (this) {
                this.mInitThread.mCancelled = true;
                this.mInitThread.mWantsRetry = false;
            }
            AvindexStore.cancelWaitLoadMediaComplete(getMediaId());
            AvindexStore.Images.cancelWaitAndUpdateDatabase(this.mHelper.getResolver(), getMediaId());
            try {
                this.mInitThread.join();
                Log.i(TAG, MSG_JOIN_INITIAL_QUERY_THREAD);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.mInitThread = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String[] getGroupProjection() {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String[] getContentsProjection() {
        return this.mQueryProjection;
    }

    public void setContentsProjection(String[] columns) {
        if (isInitialQueryDone()) {
            Log.w(TAG, MSG_FAIL_SET_PROJECTION_ALREADY_QUERIED);
            return;
        }
        for (String column : columns) {
            if (column == null || column.contains(AV_SEPARATOR)) {
                Log.e(TAG, LogHelper.getScratchBuilder(MSG_FAIL_SET_PROJECTION_INVALID_ARG).append(column).toString());
                return;
            }
        }
        this.mQueryProjection = columns;
    }

    protected Cursor queryLastContent() {
        Cursor c = AvindexStore.Images.Media.getContentFocusPoint(this.mHelper.getResolver(), AvindexStore.Images.Media.getContentUri(getMediaId()));
        if (c != null && !c.moveToFirst()) {
            Log.i(TAG, LogHelper.getScratchBuilder(MSG_LAST_CONTENT_NOT_FOUND).append(c.getCount()).toString());
            c.close();
            return null;
        }
        return c;
    }

    public void setContentPos() {
        long id = getId();
        boolean result = AvindexStore.Images.Media.setContentFocusPoint(this.mHelper.getResolver(), AvindexStore.Images.Media.getContentUri(getMediaId()), id);
        Log.i(TAG, LogHelper.getScratchBuilder(MSG_SET_CONTENT_POS).append(id).append(" -> ").append(result).toString());
    }

    public synchronized boolean requeryData() {
        boolean result;
        result = false;
        if (this.mGroupCursor != null) {
            result = this.mGroupCursor.requery();
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
        this.mHelper.invalidateCachedInfo();
        return result;
    }

    public synchronized int getGroupCount() {
        return this.mGroupCursor == null ? 0 : this.mGroupCursor.getCount();
    }

    public synchronized int getGroupPosition() {
        return this.mGroupCursor == null ? -1 : this.mGroupCursor.getPosition();
    }

    public synchronized int getContentsCount() {
        return this.mContentsCursor == null ? 0 : this.mContentsCursor.getCount();
    }

    public synchronized int getContentsPosition() {
        return this.mContentsCursor == null ? -1 : this.mContentsCursor.getPosition();
    }

    public synchronized int getContentsTotalCount() {
        int i = 0;
        synchronized (this) {
            if (this.mGroupCursor != null) {
                if (-1 == this.mTotalCount) {
                    if (this.mGroupCursor.getCount() == 0) {
                        Log.d(TAG, MSG_GET_TOTAL_COUNT_GROUP_IS_NONE);
                    } else {
                        int current = this.mGroupCursor.getPosition();
                        this.mGroupCursor.moveToLast();
                        int countOfOneBefore = this.mGroupCursor.getInt(this.mGroupCursor.getColumnIndexOrThrow("count_of_one_before"));
                        int count = this.mGroupCursor.getInt(this.mGroupCursor.getColumnIndexOrThrow("_count"));
                        this.mGroupCursor.moveToPosition(current);
                        this.mTotalCount = countOfOneBefore + count;
                    }
                }
                i = this.mTotalCount;
            }
        }
        return i;
    }

    public synchronized int getContentsTotalPosition() {
        int i = -1;
        synchronized (this) {
            if (this.mGroupCursor != null && this.mContentsCursor != null) {
                if (this.mGroupCursor.getCount() == 0) {
                    Log.d(TAG, MSG_GET_TOTAL_POSITION_GROUP_IS_NONE);
                } else {
                    int countOfOneBefore = this.mGroupCursor.getInt(this.mGroupCursor.getColumnIndexOrThrow("count_of_one_before"));
                    i = this.mContentsCursor.getPosition() + countOfOneBefore;
                }
            }
        }
        return i;
    }

    public synchronized int getContentsCountOfGroup(int groupId) {
        int count;
        int current = this.mGroupCursor.getPosition();
        count = this.mGroupCursor.moveToPosition(groupId) ? this.mGroupCursor.getInt(this.mGroupCursor.getColumnIndexOrThrow("_count")) : -1;
        this.mGroupCursor.moveToPosition(current);
        return count;
    }

    public int findGroup(SearchInfo searchData) {
        return -1;
    }

    public int findContents(SearchInfo searchData) {
        return -1;
    }

    public synchronized boolean moveGroupTo(int position) {
        boolean z = false;
        synchronized (this) {
            if (this.mGroupCursor != null && this.mGroupCursor.moveToPosition(position)) {
                z = queryInGroup();
            }
        }
        return z;
    }

    public synchronized boolean moveGroupToNext(boolean doLoopBack) {
        boolean result;
        if (this.mGroupCursor == null) {
            result = false;
        } else {
            result = false;
            if (this.mGroupCursor.isLast()) {
                if (doLoopBack) {
                    result = this.mGroupCursor.moveToFirst();
                }
            } else {
                result = this.mGroupCursor.moveToNext();
            }
            if (result) {
                result = queryInGroup();
            }
        }
        return result;
    }

    public synchronized boolean moveGroupToPrevious(boolean doLoopBack) {
        boolean result;
        if (this.mGroupCursor == null) {
            result = false;
        } else {
            result = false;
            if (this.mGroupCursor.isFirst()) {
                if (doLoopBack) {
                    result = this.mGroupCursor.moveToLast();
                }
            } else {
                result = this.mGroupCursor.moveToPrevious();
            }
            if (result) {
                result = queryInGroup();
            }
        }
        return result;
    }

    public synchronized boolean moveTo(int position) {
        return this.mContentsCursor == null ? false : this.mContentsCursor.moveToPosition(position);
    }

    public synchronized boolean moveToNext() {
        boolean result;
        if (this.mGroupCursor == null || this.mContentsCursor == null) {
            result = false;
        } else if (this.mContentsCursor.isLast()) {
            result = this.mGroupCursor.moveToNext();
            if (result && (result = queryInGroup())) {
                result = this.mContentsCursor.moveToFirst();
            }
        } else {
            result = this.mContentsCursor.moveToNext();
        }
        return result;
    }

    public synchronized boolean moveToPrevious() {
        boolean result;
        if (this.mGroupCursor == null || this.mContentsCursor == null) {
            result = false;
        } else if (this.mContentsCursor.isFirst()) {
            result = this.mGroupCursor.moveToPrevious();
            if (result && (result = queryInGroup())) {
                result = this.mContentsCursor.moveToLast();
            }
        } else {
            result = this.mContentsCursor.moveToPrevious();
        }
        return result;
    }

    public synchronized boolean moveToFirst() {
        boolean result;
        if (this.mGroupCursor == null || this.mContentsCursor == null) {
            result = false;
        } else {
            result = this.mGroupCursor.moveToFirst();
            if (result && (result = queryInGroup())) {
                result = this.mContentsCursor.moveToFirst();
            }
        }
        return result;
    }

    public synchronized boolean moveToLast() {
        boolean result;
        if (this.mGroupCursor == null || this.mContentsCursor == null) {
            result = false;
        } else {
            result = this.mGroupCursor.moveToLast();
            if (result && (result = queryInGroup())) {
                result = this.mContentsCursor.moveToLast();
            }
        }
        return result;
    }

    public synchronized boolean isFirst() {
        boolean z = false;
        synchronized (this) {
            if (this.mGroupCursor != null && this.mContentsCursor != null && this.mGroupCursor.isFirst()) {
                if (this.mContentsCursor.isFirst()) {
                    z = true;
                }
            }
        }
        return z;
    }

    public synchronized boolean isLast() {
        boolean z = false;
        synchronized (this) {
            if (this.mGroupCursor != null && this.mContentsCursor != null && this.mGroupCursor.isLast()) {
                if (this.mContentsCursor.isLast()) {
                    z = true;
                }
            }
        }
        return z;
    }

    public synchronized long getId() {
        long j = -1;
        synchronized (this) {
            if (this.mGroupCursor != null && this.mContentsCursor != null && !this.mContentsCursor.isBeforeFirst() && !this.mContentsCursor.isAfterLast()) {
                j = this.mHelper.getLong(this.mContentsCursor, "_id");
            }
        }
        return j;
    }

    public synchronized String getData() {
        String str = null;
        synchronized (this) {
            if (this.mGroupCursor != null && this.mContentsCursor != null && !this.mContentsCursor.isBeforeFirst() && !this.mContentsCursor.isAfterLast()) {
                str = this.mHelper.getString(this.mContentsCursor, "_data");
            }
        }
        return str;
    }

    @Deprecated
    public synchronized int getFNumber() {
        int i = -1;
        synchronized (this) {
            if (this.mGroupCursor != null && this.mContentsCursor != null && !this.mContentsCursor.isBeforeFirst() && !this.mContentsCursor.isAfterLast()) {
                i = this.mHelper.getInt(this.mContentsCursor, "dcf_file_number");
            }
        }
        return i;
    }

    public synchronized int getInt(String tag) {
        int i = Integer.MIN_VALUE;
        synchronized (this) {
            if (this.mGroupCursor != null && this.mContentsCursor != null && !this.mContentsCursor.isBeforeFirst() && !this.mContentsCursor.isAfterLast()) {
                i = this.mHelper.getInt(this.mContentsCursor, tag);
            }
        }
        return i;
    }

    public synchronized String getString(String tag) {
        String str = null;
        synchronized (this) {
            if (this.mGroupCursor != null && this.mContentsCursor != null && !this.mContentsCursor.isBeforeFirst() && !this.mContentsCursor.isAfterLast()) {
                str = this.mHelper.getString(this.mContentsCursor, tag);
            }
        }
        return str;
    }

    public synchronized long getLong(String tag) {
        long j = Long.MIN_VALUE;
        synchronized (this) {
            if (this.mGroupCursor != null && this.mContentsCursor != null && !this.mContentsCursor.isBeforeFirst() && !this.mContentsCursor.isAfterLast()) {
                j = this.mHelper.getLong(this.mContentsCursor, tag);
            }
        }
        return j;
    }

    public synchronized int getIntAt(int position, String tag) {
        int i = Integer.MIN_VALUE;
        synchronized (this) {
            if (this.mWorkCursor != null) {
                if (!this.mWorkCursor.moveToPosition(position)) {
                    Log.w(TAG, LogHelper.getScratchBuilder(MSG_FAILED_TO_MOVE).append(position).toString());
                } else {
                    i = this.mHelper.getInt(this.mWorkCursor, tag);
                }
            }
        }
        return i;
    }

    public synchronized String getStringAt(int position, String tag) {
        String str = null;
        synchronized (this) {
            if (this.mWorkCursor != null) {
                if (!this.mWorkCursor.moveToPosition(position)) {
                    Log.w(TAG, LogHelper.getScratchBuilder(MSG_FAILED_TO_MOVE).append(position).toString());
                } else {
                    str = this.mHelper.getString(this.mWorkCursor, tag);
                }
            }
        }
        return str;
    }

    public synchronized long getLongAt(int position, String tag) {
        long j = Long.MIN_VALUE;
        synchronized (this) {
            if (this.mWorkCursor != null) {
                if (!this.mWorkCursor.moveToPosition(position)) {
                    Log.w(TAG, LogHelper.getScratchBuilder(MSG_FAILED_TO_MOVE).append(position).toString());
                } else {
                    j = this.mHelper.getLong(this.mWorkCursor, tag);
                }
            }
        }
        return j;
    }

    public synchronized int getIntOfGroupAt(int position, String tag) {
        int result;
        if (this.mGroupCursor == null) {
            result = Integer.MIN_VALUE;
        } else {
            int old = this.mGroupCursor.getPosition();
            if (!this.mGroupCursor.moveToPosition(position)) {
                Log.w(TAG, LogHelper.getScratchBuilder(MSG_FAILED_TO_MOVE).append(position).toString());
                result = Integer.MIN_VALUE;
            } else {
                result = this.mHelper.getInt(this.mGroupCursor, tag);
            }
            this.mGroupCursor.moveToPosition(old);
        }
        return result;
    }

    public synchronized String getStringOfGroupAt(int position, String tag) {
        String result;
        if (this.mGroupCursor == null) {
            result = null;
        } else {
            int old = this.mGroupCursor.getPosition();
            if (!this.mGroupCursor.moveToPosition(position)) {
                Log.w(TAG, LogHelper.getScratchBuilder(MSG_FAILED_TO_MOVE).append(position).toString());
                result = null;
            } else {
                result = this.mHelper.getString(this.mGroupCursor, tag);
            }
            this.mGroupCursor.moveToPosition(old);
        }
        return result;
    }

    public synchronized long getLongOfGroupAt(int position, String tag) {
        long result;
        if (this.mGroupCursor == null) {
            result = Long.MIN_VALUE;
        } else {
            int old = this.mGroupCursor.getPosition();
            if (!this.mGroupCursor.moveToPosition(position)) {
                Log.w(TAG, LogHelper.getScratchBuilder(MSG_FAILED_TO_MOVE).append(position).toString());
                result = Long.MIN_VALUE;
            } else {
                result = this.mHelper.getLong(this.mGroupCursor, tag);
            }
            this.mGroupCursor.moveToPosition(old);
        }
        return result;
    }

    public synchronized ContentsIdentifier getContentsIdentifier() {
        return new ContentsIdentifier(getId(), getData(), getMediaId());
    }

    public ContentsIdentifier getResumeId() {
        Cursor c = queryLastContent();
        if (c == null) {
            return null;
        }
        String uniqId = c.getString(c.getColumnIndex("_data"));
        Log.i(TAG, LogHelper.getScratchBuilder(MSG_GET_RESUME_ID).append(uniqId).toString());
        return new ContentsIdentifier(-1L, uniqId, getMediaId());
    }

    public synchronized ContentsIdentifier getContentsIdentifierAt(int position) {
        ContentsIdentifier contentsIdentifier;
        if (this.mWorkCursor == null) {
            contentsIdentifier = new ContentsIdentifier(-1L, null, null);
        } else if (!this.mWorkCursor.moveToPosition(position)) {
            Log.w(TAG, LogHelper.getScratchBuilder(MSG_FAILED_TO_MOVE).append(position).toString());
            contentsIdentifier = new ContentsIdentifier(-1L, null, null);
        } else {
            long workId = this.mHelper.getLong(this.mWorkCursor, "_id");
            String workData = this.mHelper.getString(this.mWorkCursor, "_data");
            String workMediaId = getMediaId();
            contentsIdentifier = new ContentsIdentifier(workId, workData, workMediaId);
        }
        return contentsIdentifier;
    }

    public synchronized SearchInfo getSearchInfo(SearchInfo info) {
        SearchInfo info2;
        try {
            info2 = SearchInfoFromCursor(this.mContentsCursor, info, false);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            info2 = null;
        }
        return info2;
    }

    public SearchInfo getSearchInfoFromResumeContents(SearchInfo info) {
        try {
            Cursor cursor = queryLastContent();
            if (cursor != null) {
                int folderNumber = cursor.getInt(cursor.getColumnIndexOrThrow("dcf_folder_number"));
                int fileNumber = cursor.getInt(cursor.getColumnIndexOrThrow("dcf_file_number"));
                if (info == null) {
                    info = new SearchInfo(folderNumber, fileNumber, null, null);
                } else {
                    info.fileNumber = fileNumber;
                    info.folderNumber = folderNumber;
                    info.localDate = null;
                    info.utcDate = null;
                }
            }
            return info;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    public synchronized SearchInfo getSearchInfoAt(int position, SearchInfo info) {
        SearchInfo info2;
        SearchInfo searchInfo = null;
        synchronized (this) {
            if (this.mWorkCursor != null) {
                try {
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    info2 = null;
                }
                if (!this.mWorkCursor.moveToPosition(position)) {
                    Log.w(TAG, LogHelper.getScratchBuilder(MSG_FAILED_TO_MOVE).append(position).toString());
                } else {
                    info2 = SearchInfoFromCursor(this.mWorkCursor, info, false);
                    searchInfo = info2;
                }
            }
        }
        return searchInfo;
    }

    public synchronized SearchInfo getSearchInfoWithDateTime(SearchInfo info) {
        SearchInfo info2;
        try {
            info2 = SearchInfoFromCursor(this.mContentsCursor, info, true);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            info2 = null;
        }
        return info2;
    }

    public synchronized SearchInfo getSearchInfoWithDateTimeAt(int position, SearchInfo info) {
        SearchInfo info2;
        SearchInfo searchInfo = null;
        synchronized (this) {
            if (this.mWorkCursor != null) {
                try {
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    info2 = null;
                }
                if (!this.mWorkCursor.moveToPosition(position)) {
                    Log.w(TAG, LogHelper.getScratchBuilder(MSG_FAILED_TO_MOVE).append(position).toString());
                } else {
                    info2 = SearchInfoFromCursor(this.mWorkCursor, info, true);
                    searchInfo = info2;
                }
            }
        }
        return searchInfo;
    }

    private static SearchInfo SearchInfoFromCursor(Cursor cursor, SearchInfo info, boolean useDateTime) {
        if (cursor == null) {
            return null;
        }
        int folderNumber = cursor.getInt(cursor.getColumnIndexOrThrow("dcf_folder_number"));
        int fileNumber = cursor.getInt(cursor.getColumnIndexOrThrow("dcf_file_number"));
        String localDate = cursor.getString(cursor.getColumnIndexOrThrow("content_created_local_date"));
        String utcDate = cursor.getString(cursor.getColumnIndexOrThrow("content_created_utc_date"));
        if (info == null) {
            info = new SearchInfo(folderNumber, fileNumber, localDate, utcDate);
        } else {
            info.fileNumber = fileNumber;
            info.folderNumber = folderNumber;
            info.localDate = localDate;
            info.utcDate = utcDate;
        }
        if (useDateTime) {
            info.localDateTime = cursor.getLong(cursor.getColumnIndexOrThrow("content_created_local_date_time"));
            info.utcDateTime = cursor.getLong(cursor.getColumnIndexOrThrow("content_created_utc_date_time"));
        }
        return info;
    }
}
