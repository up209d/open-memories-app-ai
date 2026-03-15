package com.sony.imaging.app.base.playback.contents;

import android.content.ContentResolver;
import android.net.Uri;
import android.util.Log;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.util.PTag;
import com.sony.scalar.provider.AvindexStore;

/* loaded from: classes.dex */
public class DeleteService extends EditServiceImpl {
    public static final int ERR_FAILED_TO_DELETE = -5;
    public static final int ERR_FAILED_TO_SEARCH_CONTENTS = -4;
    public static final int ERR_PROTECTED = -3;
    private static final String MSG_EXECUTE_DELETION = "execute deletion";
    private static final String MSG_EXECUTING_DELETION = "deleting ";
    private static final String MSG_FAILED_TO_GET_CONTENT = "failed to get content";
    private static final String MSG_FAILED_TO_REQUERY = "failed to requeryData";
    private static final String MSG_POST_PROCESS = "postprocess";
    protected static final int PROGRESS_INTERVAL = 1;
    protected static final int SELECTABLE_COUNT = 100;
    private static final String TAG = "DeleteService";
    private static DeleteService mInstance = new DeleteService(100, 1);
    private ContentsManager.MoveHelper mHelper;

    public static DeleteService getInstance() {
        return mInstance;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public DeleteService(int selectableCount, int notifyProgressInterval) {
        super(selectableCount, notifyProgressInterval);
    }

    @Override // com.sony.imaging.app.base.playback.contents.EditServiceImpl, com.sony.imaging.app.base.playback.contents.EditService
    public int select(boolean selectIt, int file) {
        ContentInfo info = this.mMgr.getContentInfo(this.mMgr.getContentsIdAt(file));
        if (info == null) {
            Log.w(TAG, MSG_FAILED_TO_GET_CONTENT);
            return EditServiceImpl.ERR_UNKNOWN;
        }
        int protect = info.getInt("FILE_SYSTEMProtectInfo");
        if (protect != 0) {
            return -3;
        }
        return super.select(selectIt, file);
    }

    @Override // com.sony.imaging.app.base.playback.contents.EditServiceImpl, com.sony.imaging.app.base.playback.contents.EditService
    public boolean isSelectable(int file) {
        if (isSelected(file)) {
            return true;
        }
        ContentInfo info = this.mMgr.getContentInfo(this.mMgr.getContentsIdAt(file));
        if (info == null) {
            Log.w(TAG, MSG_FAILED_TO_GET_CONTENT);
            return false;
        }
        int protect = info.getInt("FILE_SYSTEMProtectInfo");
        if (protect == 0) {
            return super.isSelectable(file);
        }
        return false;
    }

    @Override // com.sony.imaging.app.base.playback.contents.EditServiceImpl, com.sony.imaging.app.base.playback.contents.EditService
    public void execute() {
        PTag.start(MSG_EXECUTE_DELETION);
        super.execute();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.contents.EditServiceImpl
    public int preprocess() {
        int org_group = this.mMgr.getGroupPosition();
        int org_contents = this.mMgr.getContentsPosition();
        boolean found = !isSelected(org_contents);
        while (!found && this.mMgr.moveToNext()) {
            int contents = this.mMgr.getContentsPosition();
            found = !isSelected(contents);
        }
        if (!found) {
            this.mMgr.moveGroupTo(org_group);
            this.mMgr.moveTo(org_contents);
            while (!found && this.mMgr.moveToPrevious()) {
                int contents2 = this.mMgr.getContentsPosition();
                found = !isSelected(contents2);
            }
        }
        if (found) {
            this.mHelper = this.mMgr.getMoveHelperToCurrentFile();
        } else {
            this.mHelper = null;
        }
        this.mMgr.moveGroupTo(org_group);
        this.mMgr.moveTo(org_contents);
        return 0;
    }

    @Override // com.sony.imaging.app.base.playback.contents.EditServiceImpl
    protected synchronized int processContents(ContentsIdentifier id) {
        boolean result;
        Log.d(TAG, LogHelper.getScratchBuilder(MSG_EXECUTING_DELETION).append(id.data).append(", ").append(id._id).toString());
        ContentResolver resolver = ContentsManager.getInstance().getContentResolver();
        Uri uri = id.getContentUri();
        result = AvindexStore.Images.Media.deleteImage(resolver, uri, id._id);
        if (result) {
            AvindexStore.Images.waitAndUpdateDatabase(resolver, id.mediaId);
        }
        return result ? 0 : -5;
    }

    @Override // com.sony.imaging.app.base.playback.contents.EditServiceImpl
    protected int postprocess(int result) {
        Log.d(TAG, MSG_POST_PROCESS);
        if (!this.mMgr.requeryData()) {
            Log.e(TAG, MSG_FAILED_TO_REQUERY);
            return result;
        }
        if (this.mMgr.getGroupCount() > 0) {
            boolean bFound = false;
            if (this.mHelper != null) {
                bFound = this.mMgr.moveByHelper(this.mHelper);
            }
            if (!bFound) {
                bFound = this.mMgr.moveToLast();
            }
            if (result == 0 && !bFound) {
                result = -4;
            }
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.contents.EditServiceImpl
    public boolean isError(int result) {
        return result != 0;
    }
}
