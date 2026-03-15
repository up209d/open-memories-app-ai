package com.sony.imaging.app.srctrl.playback.contents;

import android.util.Log;
import com.sony.imaging.app.base.playback.contents.MixedTypeDateViewMode;
import com.sony.imaging.app.base.playback.contents.ProviderHelper;
import com.sony.imaging.app.base.playback.contents.ViewMode;
import com.sony.imaging.app.base.playback.contents.aviadapter.FilesLocalDate;
import com.sony.imaging.app.base.playback.contents.aviadapter.IFolder;
import com.sony.imaging.app.base.playback.contents.aviadapter.ILocalDate;
import com.sony.imaging.app.base.playback.contents.aviadapter.IUtcDate;
import com.sony.imaging.app.base.playback.contents.aviadapter.ImagesLocalDate;
import com.sony.imaging.app.util.Environment;

/* loaded from: classes.dex */
public class MixedTypeDateViewModeEx extends MixedTypeDateViewMode {
    protected static final String[] CONTENTS_QUERY_PROJECTION;
    protected static final String CONTENT_ORDER_ASC;
    protected static final String CONTENT_ORDER_DESC;
    protected static final String GROUP_ORDER_ASC;
    protected static final String GROUP_ORDER_DESC;
    private static final int PF_VER_SG = 7;
    private static final String TAG = MixedTypeDateViewModeEx.class.getName();
    private static boolean mIsSortTypeAsc;
    protected static final ILocalDate sLocalDateAdapter;

    static {
        sLocalDateAdapter = Environment.isAvindexFilesSupported() ? new FilesLocalDate() : new ImagesLocalDate();
        GROUP_ORDER_ASC = sLocalDateAdapter.convColumn(ILocalDate.CONTENT_CREATED_LOCAL_DATE) + ViewMode.ASC;
        GROUP_ORDER_DESC = sLocalDateAdapter.convColumn(ILocalDate.CONTENT_CREATED_LOCAL_DATE) + ViewMode.DESC;
        CONTENT_ORDER_ASC = sMediaAdapter.convColumn("content_created_local_date_time") + ViewMode.ASC;
        CONTENT_ORDER_DESC = sMediaAdapter.convColumn("content_created_local_date_time") + ViewMode.DESC;
        CONTENTS_QUERY_PROJECTION = new String[]{"_id", "_data", "dcf_file_number", IFolder.DCF_FOLDER_NUMBER, ILocalDate.CONTENT_CREATED_LOCAL_DATE, IUtcDate.CONTENT_CREATED_UTC_DATE, "content_created_local_date_time", "content_created_utc_date_time", "exist_jpeg", "exist_raw", "exist_mpo", "rec_order", "time_zone", "content_type"};
        mIsSortTypeAsc = true;
    }

    public MixedTypeDateViewModeEx() {
        this.mQueryProjection = sMediaAdapter.convColumns(CONTENTS_QUERY_PROJECTION);
    }

    @Override // com.sony.imaging.app.base.playback.contents.MixedTypeDateViewMode, com.sony.imaging.app.base.playback.contents.MixedTypeViewMode, com.sony.imaging.app.base.playback.contents.ViewMode
    public void initialize(ProviderHelper helper) {
        this.mContentTypeFilterForContent = null;
        mIsSortTypeAsc = true;
        super.initialize(helper);
    }

    @Override // com.sony.imaging.app.base.playback.contents.MixedTypeDateViewMode
    protected String getGroupOrder() {
        return mIsSortTypeAsc ? GROUP_ORDER_ASC : GROUP_ORDER_DESC;
    }

    @Override // com.sony.imaging.app.base.playback.contents.MixedTypeDateViewMode
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

    @Override // com.sony.imaging.app.base.playback.contents.MixedTypeDateViewMode
    public boolean filterByContentType(String[] types) {
        if (this.mInitThread != null) {
            return false;
        }
        this.mContentTypeFilterForContent = types;
        if (isInitialQueryDone()) {
            queryGroup();
        }
        return true;
    }

    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    public synchronized int getContentsTotalCount() {
        int i = 0;
        synchronized (this) {
            if (7 == Environment.getVersionPfAPI()) {
                if (this.mGroupCursor != null) {
                    if (-1 == this.mTotalCount) {
                        if (this.mGroupCursor.getCount() == 0) {
                            Log.d(TAG, "getContentsTotalCount : group count is zero");
                        } else {
                            int current = this.mGroupCursor.getPosition();
                            if (GROUP_ORDER_ASC.equals(getGroupOrder())) {
                                this.mGroupCursor.moveToLast();
                            } else {
                                this.mGroupCursor.moveToFirst();
                            }
                            int countOfOneBefore = this.mGroupCursor.getInt(this.mGroupCursor.getColumnIndexOrThrow(getColumnGroupCountOfOneBefore()));
                            int count = this.mGroupCursor.getInt(this.mGroupCursor.getColumnIndexOrThrow(getColumnGroupCount()));
                            this.mGroupCursor.moveToPosition(current);
                            this.mTotalCount = countOfOneBefore + count;
                        }
                    }
                    i = this.mTotalCount;
                }
            } else {
                i = super.getContentsTotalCount();
            }
        }
        return i;
    }
}
