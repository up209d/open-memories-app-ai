package com.sony.imaging.app.soundphoto.database;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import com.sony.imaging.app.base.playback.contents.aviadapter.IFolder;
import com.sony.imaging.app.base.playback.contents.aviadapter.ILocalDate;
import com.sony.imaging.app.base.playback.contents.aviadapter.IUtcDate;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.soundphoto.SoundPhoto;
import com.sony.imaging.app.soundphoto.util.AppContext;
import com.sony.imaging.app.soundphoto.util.AppLog;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.provider.AvindexStore;
import com.sony.scalar.provider.AvindexUpdateObserver;
import java.util.ArrayList;

@SuppressLint({"DefaultLocale"})
/* loaded from: classes.dex */
public class SoundPhotoDataBaseUtil {
    private static SoundPhotoDataBaseUtil sSoundPhotoDataBaseUtil = null;
    private static Object mWaitUpdate = new Object();
    private final String TAG = AppLog.getClassName();
    private UpdateMultiAttributeThread mUpdateMultiAttribute = null;
    private SoundPhotoDataBaseUtilListener listener = null;
    public final int UPDATE_CONTENTS_NUM = 10;
    private Cursor mCursor = null;
    private Uri mBaseUri = null;
    private int mCursorNum = 0;
    private int mUpdateNum = 0;
    private int mLastUpdateContents = 0;
    private int mUpdateIndex = 0;
    private long[] mIds = null;
    private int mCExistJpeg = 0;
    private int mCExistRAW = 0;
    private boolean updateStarted = false;
    protected AvindexUpdateObserver mObserver = getObserver();
    BackGroundTask mBackGroundTask = null;
    boolean isThreadNotify = false;
    ArrayList<String[]> fileNumberList = null;

    static /* synthetic */ int access$108(SoundPhotoDataBaseUtil x0) {
        int i = x0.mUpdateIndex;
        x0.mUpdateIndex = i + 1;
        return i;
    }

    public boolean isUpdateStarted() {
        return this.updateStarted;
    }

    public void setUpdateStarted(boolean sUpdateStarted) {
        this.updateStarted = sUpdateStarted;
    }

    private SoundPhotoDataBaseUtil() {
        AppLog.checkIf(this.TAG, "Instance of AutoSyncDataBaseUtil is created.");
    }

    public static SoundPhotoDataBaseUtil getInstance() {
        AppLog.enter(AppLog.getClassName(), AppLog.getMethodName());
        if (sSoundPhotoDataBaseUtil == null) {
            sSoundPhotoDataBaseUtil = new SoundPhotoDataBaseUtil();
        }
        AppLog.exit(AppLog.getClassName(), AppLog.getMethodName());
        return sSoundPhotoDataBaseUtil;
    }

    protected AvindexUpdateObserver getObserver() {
        return new Observer();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class Observer implements AvindexUpdateObserver {
        protected static final String MSG_ON_COMPLETE = "onComplete : ";
        protected static final String MSG_ON_PROGRESS = "onProgress : ";

        protected Observer() {
        }

        public void onComplete(int result) {
            AppLog.enter(SoundPhotoDataBaseUtil.this.TAG, AppLog.getMethodName());
            Log.i(SoundPhotoDataBaseUtil.this.TAG, "Update completed");
            if (SoundPhotoDataBaseUtil.this.mUpdateIndex != SoundPhotoDataBaseUtil.this.mUpdateNum) {
                Log.i(SoundPhotoDataBaseUtil.this.TAG, "Next Data Update");
                synchronized (SoundPhotoDataBaseUtil.mWaitUpdate) {
                    Log.e(SoundPhotoDataBaseUtil.this.TAG, "### mWaitUpdate.notify() by complete");
                    SoundPhotoDataBaseUtil.mWaitUpdate.notify();
                }
                return;
            }
            Log.i(SoundPhotoDataBaseUtil.this.TAG, "All Data Complete");
            if (true == SoundPhotoDataBaseUtil.this.updateStarted && SoundPhotoDataBaseUtil.this.mUpdateMultiAttribute != null) {
                Log.i(SoundPhotoDataBaseUtil.this.TAG, "mUpdateMultiAttribute release starts");
                SoundPhotoDataBaseUtil.this.mUpdateMultiAttribute.halt();
                SoundPhotoDataBaseUtil.this.mUpdateMultiAttribute = null;
            }
            Log.i(SoundPhotoDataBaseUtil.this.TAG, "mUpdateMultiAttribute release end");
            SoundPhotoDataBaseUtil.this.updateStarted = false;
            AppLog.info(SoundPhotoDataBaseUtil.this.TAG, "TESTTAG inside onComplete finishedUpdateStatus");
            SoundPhotoDataBaseUtil.this.closeProcessingLayout();
            SoundPhotoDataBaseUtil.this.notifyRecoverThread();
            Log.i(SoundPhotoDataBaseUtil.this.TAG, "All Data Complete and finish");
        }
    }

    public void updateMultiAttributeExcec() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.checkIf(this.TAG, " updateMultiAttribute call param1: " + AppContext.getAppContext().getContentResolver() + " param2 : " + this.mBaseUri + " param3 : " + this.mIds[0] + " param4 : " + this.mObserver);
        AvindexStore.Images.Media.updateMultiAttribute(AppContext.getAppContext().getContentResolver(), this.mBaseUri, this.mIds, this.mObserver);
        DataBaseOperations.getInstance().update(this.fileNumberList);
        this.fileNumberList.clear();
        this.fileNumberList = null;
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    public void startAutoSynchBackground(boolean waitMediaAccess) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.updateStarted = true;
        updateSPFSyncContent(waitMediaAccess);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    public void startSyncFromPlayBack(boolean waitMediaAccess) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.updateStarted = true;
        this.mBackGroundTask = new BackGroundTask();
        this.mBackGroundTask.execute(Boolean.valueOf(waitMediaAccess));
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    public void cancelBackgroundTask() {
        AppLog.enter(this.TAG, "TESTTAG " + AppLog.getMethodName());
        if (this.mBackGroundTask != null && !this.mBackGroundTask.isCancelled()) {
            AppLog.info(this.TAG, "TESTTAG BackgroundTask running.....");
            this.mBackGroundTask.cancel(true);
            this.mBackGroundTask = null;
        }
        AppLog.exit(this.TAG, "TESTTAG " + AppLog.getMethodName());
    }

    /* loaded from: classes.dex */
    class BackGroundTask extends AsyncTask<Boolean, Void, Void> {
        BackGroundTask() {
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            AppLog.enter(SoundPhotoDataBaseUtil.this.TAG, AppLog.getMethodName());
            super.onPreExecute();
            AppLog.exit(SoundPhotoDataBaseUtil.this.TAG, AppLog.getMethodName());
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Boolean... params) {
            AppLog.enter(SoundPhotoDataBaseUtil.this.TAG, AppLog.getMethodName());
            SoundPhotoDataBaseUtil.this.updateSPFSyncContent(params[0].booleanValue());
            AppLog.exit(SoundPhotoDataBaseUtil.this.TAG, AppLog.getMethodName());
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onProgressUpdate(Void... values) {
            AppLog.enter(SoundPhotoDataBaseUtil.this.TAG, AppLog.getMethodName());
            super.onProgressUpdate((Object[]) values);
            AppLog.exit(SoundPhotoDataBaseUtil.this.TAG, AppLog.getMethodName());
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void result) {
            AppLog.enter(SoundPhotoDataBaseUtil.this.TAG, AppLog.getMethodName());
            super.onPostExecute((BackGroundTask) result);
            AppLog.exit(SoundPhotoDataBaseUtil.this.TAG, AppLog.getMethodName());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyRecoverThread() {
        synchronized (SoundPhoto.mWaitForRecovery) {
            SoundPhoto.mWaitForRecovery.notify();
            AppLog.info(this.TAG, "TESTTAG Wait notified");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closeProcessingLayout() {
        if (this.listener != null) {
            this.listener.finishedUpdateStatus();
        }
    }

    public boolean updateSPFSyncContent(boolean waitMediaAccess) {
        AppLog.enter(this.TAG, AppLog.getMethodName() + " waitMediaAccess: " + waitMediaAccess);
        String mediaId = AvindexStore.getExternalMediaIds()[0];
        if (waitMediaAccess) {
            AvindexStore.loadMedia(mediaId, 1);
            if (!AvindexStore.waitLoadMediaComplete(mediaId)) {
                Log.i(this.TAG, "load cancelled");
                notifyRecoverThread();
                closeProcessingLayout();
                return false;
            }
            if (!AvindexStore.Images.waitAndUpdateDatabase(AppContext.getAppContext().getContentResolver(), mediaId)) {
                Log.i(this.TAG, "Update cancelled");
                notifyRecoverThread();
                closeProcessingLayout();
                return false;
            }
        }
        this.mBaseUri = AvindexStore.Images.Media.getContentUri(mediaId);
        String[] projection = {"_id", "_data", "dcf_file_number", IFolder.DCF_FOLDER_NUMBER, "content_created_utc_date_time", IUtcDate.CONTENT_CREATED_UTC_DATE, "content_created_utc_time", ILocalDate.CONTENT_CREATED_LOCAL_DATE, "content_created_local_time", "exist_jpeg", "exist_raw"};
        AppLog.info(this.TAG, "TIMESTAMP fileClause Query START");
        String fileClause = DataBaseOperations.getInstance().getTotalFileFromLocalDB();
        AppLog.info(this.TAG, "TESTTAG updateSPFSyncContent fileClause=" + fileClause);
        if (fileClause == null || fileClause.length() < 1) {
            notifyRecoverThread();
            closeProcessingLayout();
            return false;
        }
        AppLog.info(this.TAG, "TIMESTAMP fileClause Query STOP");
        String selection = "exist_jpeg=1 AND  ( " + fileClause + StringBuilderThreadLocal.ROUND_BRACKET_CLOSE;
        this.mCursor = AppContext.getAppContext().getContentResolver().query(AvindexStore.Images.Media.getContentUri(AvindexStore.getExternalMediaIds()[0]), projection, selection, null, null);
        AppLog.checkIf(this.TAG, " query call param1: " + AvindexStore.Images.Media.getContentUri(AvindexStore.getExternalMediaIds()[0]) + " param2 : " + projection + "param3" + selection);
        if (this.mCursor == null) {
            AppLog.checkIf(this.TAG, "Auto Sync Cursor is null");
            notifyRecoverThread();
            closeProcessingLayout();
            return false;
        }
        this.mCursorNum = this.mCursor.getCount();
        AppLog.checkIf(this.TAG, "mCursorNum = " + this.mCursorNum);
        if (this.mCursorNum == 0) {
            AppLog.checkIf(this.TAG, "mCursorNum is zero");
            notifyRecoverThread();
            closeProcessingLayout();
            return false;
        }
        this.mUpdateMultiAttribute = new UpdateMultiAttributeThread(this);
        this.mUpdateMultiAttribute.setName("mUpdateMultiAttribute");
        this.mUpdateMultiAttribute.start();
        this.mUpdateNum = this.mCursorNum / 10;
        this.mLastUpdateContents = this.mCursorNum % 10;
        if (this.mLastUpdateContents != 0) {
            this.mUpdateNum++;
        }
        AppLog.checkIf(this.TAG, "mCursorNum : " + this.mCursorNum + " UPDATE_CONTENTS_NUM : 10 mUpdateNum : " + this.mUpdateNum + "mLastUpdateContents : " + this.mLastUpdateContents);
        this.mCursor.moveToFirst();
        this.mUpdateIndex = 0;
        synchronized (mWaitUpdate) {
            Log.e(this.TAG, "### mWaitUpdate.notify() by start");
            this.isThreadNotify = true;
            mWaitUpdate.notify();
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return true;
    }

    public void cancelUpdateSPFSyncContent() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (true == this.updateStarted && this.mUpdateMultiAttribute != null) {
            Log.i(this.TAG, "mUpdateMultiAttribute release starts");
            this.mUpdateMultiAttribute.halt();
            this.mUpdateMultiAttribute = null;
            Log.i(this.TAG, "mUpdateMultiAttribute release end");
            this.updateStarted = false;
            closeProcessingLayout();
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    public void cancelUpdateSPFSyncContentFromPlayBack() {
        AppLog.enter(this.TAG, "TESTTAG " + AppLog.getMethodName());
        if (true == this.updateStarted && this.mUpdateMultiAttribute != null) {
            Log.i(this.TAG, "mUpdateMultiAttribute release starts");
            this.mUpdateMultiAttribute.halt();
            this.mUpdateMultiAttribute.interrupt();
            this.mUpdateMultiAttribute = null;
            Log.i(this.TAG, "mUpdateMultiAttribute release end");
            this.updateStarted = false;
        }
        AppLog.exit(this.TAG, "TESTTAG" + AppLog.getMethodName());
    }

    /* loaded from: classes.dex */
    public class UpdateMultiAttributeThread extends Thread {
        static final String TAG = "UpdateMultiAttributeThread";
        private boolean halt_;
        private SoundPhotoDataBaseUtil mDatabaseUtil;

        public UpdateMultiAttributeThread(SoundPhotoDataBaseUtil databaseUtil) {
            this.mDatabaseUtil = null;
            this.halt_ = false;
            this.halt_ = false;
            this.mDatabaseUtil = databaseUtil;
        }

        /* JADX WARN: Removed duplicated region for block: B:33:0x018d A[SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:37:0x0001 A[SYNTHETIC] */
        @Override // java.lang.Thread, java.lang.Runnable
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public void run() {
            /*
                Method dump skipped, instructions count: 469
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: com.sony.imaging.app.soundphoto.database.SoundPhotoDataBaseUtil.UpdateMultiAttributeThread.run():void");
        }

        public void halt() {
            Log.i(TAG, "halt");
            this.halt_ = true;
            synchronized (SoundPhotoDataBaseUtil.mWaitUpdate) {
                SoundPhotoDataBaseUtil.mWaitUpdate.notify();
            }
        }
    }

    public void setListener(SoundPhotoDataBaseUtilListener listener) {
        AppLog.enter(this.TAG, AppLog.getMethodName() + ExposureModeController.SOFT_SNAP + listener);
        this.listener = listener;
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    public void removeListener() {
        AppLog.enter(this.TAG, AppLog.getMethodName() + ExposureModeController.SOFT_SNAP + this.listener);
        this.listener = null;
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }
}
