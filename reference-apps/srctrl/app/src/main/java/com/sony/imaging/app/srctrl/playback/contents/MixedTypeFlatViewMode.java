package com.sony.imaging.app.srctrl.playback.contents;

import android.net.Uri;
import android.util.Log;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.base.playback.contents.MixedTypeViewMode;
import com.sony.imaging.app.base.playback.contents.MoveHelperToSearchInfo;
import com.sony.imaging.app.base.playback.contents.ProviderHelper;
import com.sony.imaging.app.base.playback.contents.SearchInfo;
import com.sony.imaging.app.base.playback.contents.ViewMode;
import com.sony.imaging.app.base.playback.contents.aviadapter.FilesLocalDate;
import com.sony.imaging.app.base.playback.contents.aviadapter.IFolder;
import com.sony.imaging.app.base.playback.contents.aviadapter.ILocalDate;
import com.sony.imaging.app.base.playback.contents.aviadapter.IUtcDate;
import com.sony.imaging.app.base.playback.contents.aviadapter.ImagesLocalDate;
import com.sony.imaging.app.util.Environment;

/* loaded from: classes.dex */
public class MixedTypeFlatViewMode extends MixedTypeViewMode {
    protected static final String[] CONTENTS_QUERY_PROJECTION;
    protected static final String CONTENT_ORDER_ASC;
    protected static final String CONTENT_ORDER_DESC;
    private static final String MSG_QUERY_CONTENTS_FAILED = "Contents query failed";
    private static final String MSG_QUERY_GP_FAILED = "Group query returns null";
    private static final String TAG = MixedTypeFlatViewMode.class.getName();
    private static boolean mIsSortTypeAsc;
    protected static final ILocalDate sLocalDateAdapter;
    protected String[] mContentTypeFilterForContent;

    static {
        sLocalDateAdapter = Environment.isAvindexFilesSupported() ? new FilesLocalDate() : new ImagesLocalDate();
        CONTENT_ORDER_ASC = sMediaAdapter.convColumn("content_created_local_date_time") + ViewMode.ASC;
        CONTENT_ORDER_DESC = sMediaAdapter.convColumn("content_created_local_date_time") + ViewMode.DESC;
        mIsSortTypeAsc = true;
        CONTENTS_QUERY_PROJECTION = new String[]{"_id", "_data", "dcf_file_number", IFolder.DCF_FOLDER_NUMBER, ILocalDate.CONTENT_CREATED_LOCAL_DATE, IUtcDate.CONTENT_CREATED_UTC_DATE, "content_created_local_date_time", "content_created_utc_date_time", "exist_jpeg", "exist_raw", "exist_mpo", "rec_order", "time_zone", "content_type"};
    }

    public MixedTypeFlatViewMode() {
        this.mQueryProjection = sMediaAdapter.convColumns(CONTENTS_QUERY_PROJECTION);
    }

    protected ILocalDate getLocalDate() {
        return sLocalDateAdapter;
    }

    @Override // com.sony.imaging.app.base.playback.contents.MixedTypeViewMode, com.sony.imaging.app.base.playback.contents.ViewMode
    public void initialize(ProviderHelper helper) {
        this.mContentTypeFilterForContent = null;
        mIsSortTypeAsc = true;
        super.initialize(helper);
    }

    public boolean filterByContentType(String[] types) {
        this.mContentTypeFilterForContent = types;
        if (isInitialQueryDone()) {
            SearchInfo info = getSearchInfo(null);
            ContentsManager.MoveHelper helper = new MoveHelperToSearchInfo(info);
            queryGroup();
            if (!helper.apply(this)) {
                moveToLast();
                return true;
            }
            return true;
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
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    public synchronized boolean queryInGroup() {
        boolean z;
        if (this.mContentsCursor != null) {
            this.mContentsCursor.close();
        }
        int numOfContentType = this.mContentTypeFilterForContent == null ? 0 : this.mContentTypeFilterForContent.length;
        String selection = "";
        if (this.mContentTypeFilterForContent != null) {
            selection = createClauseToSelectSomeValue(sMediaAdapter.convColumn("content_type"), numOfContentType);
        }
        this.mContentsCursor = this.mHelper.getResolver().query(sMediaAdapter.getContentUri(getMediaId()), getContentsProjection(), selection, this.mContentTypeFilterForContent, getContentOrder());
        if (this.mContentsCursor != null) {
            Log.i(TAG, LogHelper.getScratchBuilder(LogHelper.MSG_CONTENTS).append(this.mContentsCursor.getCount()).toString());
        } else {
            Log.w(TAG, MSG_QUERY_CONTENTS_FAILED);
        }
        if (this.mWorkCursor != null) {
            this.mWorkCursor.close();
        }
        this.mWorkCursor = this.mHelper.getResolver().query(sMediaAdapter.getContentUri(getMediaId()), getContentsProjection(), selection, this.mContentTypeFilterForContent, getContentOrder());
        if (this.mContentsCursor != null) {
            if (this.mWorkCursor != null) {
                z = true;
            }
        }
        z = false;
        return z;
    }

    protected String getContentOrder() {
        return mIsSortTypeAsc ? CONTENT_ORDER_ASC : CONTENT_ORDER_DESC;
    }

    public boolean setContentOrder(boolean isSortTypeAsc) {
        Log.i(TAG, "setContentOrder = " + isSortTypeAsc);
        if (isSortTypeAsc == mIsSortTypeAsc) {
            return false;
        }
        mIsSortTypeAsc = isSortTypeAsc;
        return true;
    }
}
