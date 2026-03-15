package com.sony.imaging.app.soundphoto.playback.viewmode;

import android.database.Cursor;
import android.util.Log;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.playback.contents.ContentsIdentifier;
import com.sony.imaging.app.base.playback.contents.ProviderHelper;
import com.sony.imaging.app.base.playback.contents.StillFolderViewMode;
import com.sony.imaging.app.base.playback.contents.aviadapter.IFolder;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.soundphoto.database.DataBaseOperations;
import com.sony.imaging.app.soundphoto.util.AppLog;
import com.sony.imaging.app.soundphoto.util.SPConstants;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.provider.AvindexStore;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class SPStillFolderViewMode extends StillFolderViewMode implements NotificationListener {
    private static final String MSG_GET_TOTAL_COUNT_GROUP_IS_NONE = "getContentsTotalCount : group count is zero";
    private static final String MSG_QUERY_CONTENTS_FAILED = "Contents query failed";
    private static final String MSG_QUERY_GP_FAILED = "Group query returns null";
    private static final String MSG_RESULT_QUERY_GROUP = "queryInGroup date ";
    private static final String TAG = "SPStillFolderViewMode";
    protected Cursor mLocalCountCursor;
    private int mTotalCount = -1;
    String mWhereClauseFile = null;
    String mWhereClauseForFolder = null;
    private final String[] TAGS = {SPConstants.DELETE_IMAGE_DATABASE_UPDATE};

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.contents.StillFolderViewMode, com.sony.imaging.app.base.playback.contents.ViewMode
    public synchronized boolean queryGroup() {
        boolean z;
        if (this.mGroupCursor != null) {
            this.mGroupCursor.close();
            this.mGroupCursor = null;
        }
        if (this.mContentsCursor != null) {
            this.mContentsCursor.close();
            this.mContentsCursor = null;
        }
        DataBaseOperations.getInstance().initializeSoundDataList();
        this.mWhereClauseForFolder = DataBaseOperations.getInstance().getAllFolderWhereClauseFromLocalDb();
        if (this.mWhereClauseForFolder != null) {
            this.mGroupCursor = this.mHelper.getResolver().query(AvindexStore.Images.Folder.getContentUri(getMediaId()), getContentsProjection(), this.mWhereClauseForFolder, null, null);
        }
        if (this.mGroupCursor == null) {
            Log.w(TAG, MSG_QUERY_GP_FAILED);
            z = false;
        } else {
            Log.i(TAG, LogHelper.getScratchBuilder(LogHelper.MSG_GROUP).append(this.mGroupCursor.getCount()).toString());
            AppLog.info(TAG, "Group Count = " + this.mGroupCursor.getCount());
            updateLocalCountCursor();
            z = true;
        }
        return z;
    }

    private void updateLocalCountCursor() {
        ArrayList<Integer> folderList = DataBaseOperations.getInstance().getSoundPhotoFolderList();
        this.mTotalCount = 0;
        int numberOfFolder = folderList.size();
        String mWhereClauseFile = LogHelper.MSG_OPEN_BRACKET;
        for (int index = 0; index < numberOfFolder; index++) {
            mWhereClauseFile = mWhereClauseFile + DataBaseOperations.getInstance().getQueryWhereClauseFromLocalDb(folderList.get(index).intValue()) + LogHelper.MSG_CLOSE_BRACKET;
            if (index < numberOfFolder - 1) {
                mWhereClauseFile = mWhereClauseFile + " OR ( ";
            }
        }
        this.mTotalCount = queryForTotalCount(mWhereClauseFile);
    }

    protected synchronized int queryForTotalCount(String whereClause) {
        if (this.mLocalCountCursor != null) {
            this.mLocalCountCursor.close();
            this.mLocalCountCursor = null;
        }
        this.mLocalCountCursor = this.mHelper.getResolver().query(AvindexStore.Images.Media.getContentUri(getMediaId()), new String[]{"dcf_file_number", IFolder.DCF_FOLDER_NUMBER}, whereClause, null, null);
        if (this.mLocalCountCursor != null) {
            Log.i(TAG, LogHelper.getScratchBuilder(LogHelper.MSG_CONTENTS).append(this.mLocalCountCursor.getCount()).toString());
        } else {
            Log.w(TAG, MSG_QUERY_CONTENTS_FAILED);
        }
        return this.mLocalCountCursor == null ? 0 : this.mLocalCountCursor.getCount();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.contents.StillFolderViewMode, com.sony.imaging.app.base.playback.contents.ViewMode
    public synchronized boolean queryInGroup() {
        boolean z = false;
        synchronized (this) {
            int dcfFolderNum = this.mGroupCursor.getInt(this.mGroupCursor.getColumnIndex(IFolder.DCF_FOLDER_NUMBER));
            Log.d(TAG, LogHelper.getScratchBuilder(MSG_RESULT_QUERY_GROUP).append(dcfFolderNum).toString());
            if (this.mContentsCursor != null) {
                this.mContentsCursor.close();
                this.mContentsCursor = null;
            }
            this.mWhereClauseFile = DataBaseOperations.getInstance().getQueryWhereClauseFromLocalDb(dcfFolderNum);
            if (this.mWhereClauseFile.isEmpty()) {
                if (this.mWorkCursor != null) {
                    this.mWorkCursor.close();
                    this.mWorkCursor = null;
                }
            } else {
                this.mContentsCursor = this.mHelper.getResolver().query(AvindexStore.Images.Media.getContentUri(getMediaId()), getContentsProjection(), this.mWhereClauseFile, null, null);
                if (this.mContentsCursor != null) {
                    Log.i(TAG, LogHelper.getScratchBuilder(LogHelper.MSG_CONTENTS).append(this.mContentsCursor.getCount()).toString());
                } else {
                    Log.w(TAG, MSG_QUERY_CONTENTS_FAILED);
                }
                if (this.mWorkCursor != null) {
                    this.mWorkCursor.close();
                }
                this.mWorkCursor = this.mHelper.getResolver().query(AvindexStore.Images.Media.getContentUri(getMediaId()), getContentsProjection(), this.mWhereClauseFile, null, null);
                z = (this.mContentsCursor == null || this.mWorkCursor == null) ? false : true;
            }
        }
        return z;
    }

    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    public synchronized int getContentsTotalPosition() {
        int i = -1;
        synchronized (this) {
            if (this.mGroupCursor != null && this.mContentsCursor != null && this.mGroupCursor.getCount() != 0) {
                int countOfOneBefore = this.mGroupCursor.getInt(this.mGroupCursor.getColumnIndexOrThrow(IFolder.DCF_FOLDER_NUMBER));
                i = this.mContentsCursor.getPosition() + DataBaseOperations.getInstance().getBeforeFileCount(countOfOneBefore);
            }
        }
        return i;
    }

    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    public void initialize(ProviderHelper helper) {
        super.initialize(helper);
        CameraNotificationManager.getInstance().setNotificationListener(this);
    }

    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    public void terminate() {
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        synchronized (this) {
            if (this.mLocalCountCursor != null) {
                this.mLocalCountCursor.close();
                this.mLocalCountCursor = null;
            }
            DataBaseOperations.getInstance().deInitializeSoundDataList();
        }
        super.terminate();
    }

    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    public synchronized int getContentsTotalCount() {
        int i = 0;
        synchronized (this) {
            if (this.mGroupCursor != null) {
                if (-1 == this.mTotalCount && this.mGroupCursor.getCount() == 0) {
                    Log.d(TAG, MSG_GET_TOTAL_COUNT_GROUP_IS_NONE);
                } else {
                    i = this.mTotalCount;
                }
            }
        }
        return i;
    }

    @Override // com.sony.imaging.app.base.playback.contents.ViewMode
    public ContentsIdentifier getResumeId() {
        if (DataBaseOperations.getInstance().getTotalFiles() <= 0 || this.mTotalCount <= 0) {
            return null;
        }
        ContentsIdentifier contentsIdentifier = super.getResumeId();
        return contentsIdentifier;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return this.TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        int localGroupCursor = -1;
        int localContentCursor = -1;
        if (this.mGroupCursor != null && this.mGroupCursor.getCount() > 0) {
            localGroupCursor = this.mGroupCursor.getPosition();
        }
        if (this.mContentsCursor != null && this.mContentsCursor.getCount() > 0) {
            localContentCursor = this.mContentsCursor.getPosition();
        }
        queryGroup();
        if (this.mGroupCursor != null && this.mGroupCursor.getCount() > 0 && localGroupCursor != -1) {
            this.mGroupCursor.moveToPosition(localGroupCursor);
            queryInGroup();
            if (localContentCursor != -1) {
                this.mContentsCursor.moveToPosition(localContentCursor);
            }
        }
    }
}
