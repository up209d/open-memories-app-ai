package com.sony.imaging.app.srctrl.playback.delete.multiple;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.playback.base.PlaySubApp;
import com.sony.imaging.app.base.playback.contents.ContentsIdentifier;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.fw.State;
import com.sony.imaging.app.srctrl.util.SRCtrlConstants;
import com.sony.imaging.app.srctrl.util.SRCtrlPlaybackUtil;
import com.sony.imaging.app.srctrl.util.StateController;
import com.sony.imaging.app.srctrl.webapi.servlet.ContentsTransfer;
import com.sony.imaging.app.srctrl.webapi.specific.DeletingHandler;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.provider.AvindexDeleteObserver;
import com.sony.scalar.provider.AvindexStore;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class DeleteMultiple extends State {
    public static final int ERR_FAILED_TO_DELETE = -2;
    private static final String INH_ID_PB_MODIFY = "INH_FEATURE_COMMON_PB_MODIFY";
    private static final String MSG_ON_COMPLETE = "onComplete : ";
    private static final String MSG_ON_PROGRESS = "onProgress : ";
    public static final int OPERATION_CANCELLED = -1;
    private static final int PF_VER_SG = 7;
    public static final int SUCCEEDED = 0;
    private static final int TRANS_ID_INVALID = -1;
    private StateController.AppCondition mPrevCondition;
    private static final String TAG = DeleteMultiple.class.getSimpleName();
    private static Object sync = new Object();
    private static Object token = new Object();
    private int mTransId = -1;
    private int mResult = 0;
    private boolean mHasErrorUri = false;
    private int mBeforeNumOfContent = 0;
    private int mAfterNumOfContent = 0;
    private int mNumOfDeleteContent = 0;
    private AvindexDeleteObserver mProxyObserver = getProxyObserver();
    private AvindexDeleteObserver mOrgObserver = null;

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        ContentsTransfer.getInstance().terminate();
        this.mResult = -2;
        this.mHasErrorUri = false;
        StateController stateController = StateController.getInstance();
        stateController.setState(this);
        ContentsManager mgr = ContentsManager.getInstance();
        this.mBeforeNumOfContent = mgr.getContentsTotalCount();
        this.mAfterNumOfContent = 0;
        this.mPrevCondition = stateController.getAppCondition();
        stateController.setAppCondition(StateController.AppCondition.PLAYBACK_DELETING);
        Bundle uriData = this.data;
        String[] uri = uriData.getStringArray(SRCtrlConstants.DELETE_CONTENT_LIST);
        this.mNumOfDeleteContent = uri.length;
        Log.i(TAG, "onResume mBeforeNumOfContent = " + this.mBeforeNumOfContent + " / mNumOfDeleteContent = " + this.mNumOfDeleteContent);
        this.mOrgObserver = getOrgObserver();
        if (isMediaErr()) {
            Log.w(TAG, "Media is not modifiable. ");
            this.mTransId = -1;
            setNextState(PlaySubApp.ID_INDEX_PB, null);
            this.mAfterNumOfContent = this.mBeforeNumOfContent;
            return;
        }
        if (Environment.isAvindexFilesSupported()) {
            deleteFiles(uri);
        } else if (Environment.getVersionPfAPI() == 7) {
            deleteSingleImage(uri);
        } else {
            deleteImages(uri);
        }
    }

    protected boolean isMediaErr() {
        if (!MediaNotificationManager.getInstance().isMounted()) {
            return false;
        }
        String[] media = AvindexStore.getExternalMediaIds();
        AvailableInfo.update();
        return AvailableInfo.isInhibition(INH_ID_PB_MODIFY, media[0]);
    }

    private void deleteFiles(String[] uri) {
        String[] deleteList = getDeleteFileList(uri);
        this.mTransId = -1;
        if (deleteList != null) {
            this.mTransId = AvindexStore.Files.Media.deleteMultiple(deleteList, this.mProxyObserver);
        }
        if (this.mTransId < 0) {
            this.mTransId = -1;
            setNextState(PlaySubApp.ID_INDEX_PB, null);
        }
    }

    private void deleteImages(String[] uri) {
        long[] deleteList = getDeleteImageList(uri);
        this.mTransId = -1;
        if (deleteList != null) {
            ContentsManager mgr = ContentsManager.getInstance();
            ContentResolver resolver = mgr.getContentResolver();
            Uri baseUri = AvindexStore.Images.Media.getContentUri(AvindexStore.getExternalMediaIds()[0]);
            this.mTransId = AvindexStore.Images.Media.deleteMultiImage(resolver, baseUri, deleteList, this.mProxyObserver);
        }
        if (this.mTransId < 0) {
            this.mTransId = -1;
            setNextState(PlaySubApp.ID_INDEX_PB, null);
        }
    }

    private void deleteSingleImage(String[] uriList) {
        long[] deleteList = getDeleteSingleImageList(uriList);
        this.mTransId = -1;
        if (deleteList != null) {
            int deleteComplete = 0;
            int uriCount = uriList.length;
            StringBuilder stb = new StringBuilder();
            this.mTransId = -1;
            ContentsManager mgr = ContentsManager.getInstance();
            ContentResolver resolver = mgr.getContentResolver();
            for (String _uri : uriList) {
                ContentsIdentifier id = SRCtrlPlaybackUtil.getContentsIdentifier(_uri);
                Uri uri = id.getContentUri();
                boolean result = AvindexStore.Images.Media.deleteImage(resolver, uri, id._id);
                if (result) {
                    AvindexStore.Images.waitAndUpdateDatabase(resolver, id.mediaId);
                    deleteComplete++;
                    stb.replace(0, stb.length(), "deleteCount  ").append(deleteComplete).append(StringBuilderThreadLocal.SLASH).append(uriCount);
                    Log.v(TAG, stb.toString());
                }
            }
            this.mOrgObserver.onComplete(0);
        }
        if (this.mTransId < 0) {
            this.mTransId = -1;
            setNextState(PlaySubApp.ID_INDEX_PB, null);
        }
    }

    private void cancelDelete(int transId) {
        boolean bret;
        if (transId != -1) {
            synchronized (sync) {
                if (Environment.isAvindexFilesSupported()) {
                    bret = AvindexStore.Files.Media.cancelDeleteMultiImage(transId);
                } else {
                    bret = AvindexStore.Images.Media.cancelDeleteMultiImage(transId);
                }
                if (bret) {
                    try {
                        sync.wait();
                        this.mResult = -1;
                    } catch (InterruptedException e) {
                        Log.e(TAG, "InterruptedException while waiting.");
                        this.mResult = -2;
                    }
                }
            }
        }
    }

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        getHandler().removeCallbacksAndMessages(token);
        this.mOrgObserver = null;
        cancelDelete(this.mTransId);
        super.onPause();
        if (this.mAfterNumOfContent != this.mBeforeNumOfContent - this.mNumOfDeleteContent) {
            this.mResult = -2;
            Log.e(TAG, "onPause ERR_FAILED_TO_DELETE.");
        }
        DeletingHandler.getInstance().onFinishDeleting(this.mResult);
        StateController.getInstance().setAppCondition(this.mPrevCondition);
        ContentsTransfer.getInstance().initialize();
    }

    private long[] getDeleteImageList(String[] uri) {
        String decUrl;
        long[] deleteList = null;
        int listSize = uri.length;
        if (listSize > 0) {
            List<Long> tmpDeleteList = new ArrayList<>();
            for (int cnt = 0; cnt < listSize; cnt++) {
                if (uri[cnt] != null && -1 != uri[cnt].indexOf("image:content?contentId=") && (decUrl = SRCtrlPlaybackUtil.urlDecode(uri[cnt])) != null) {
                    long recOrderId = SRCtrlPlaybackUtil.getRecOrderId(decUrl);
                    if (-1 != recOrderId) {
                        tmpDeleteList.add(Long.valueOf(recOrderId));
                    }
                }
            }
            int deleteListSize = tmpDeleteList.size();
            this.mHasErrorUri = deleteListSize < listSize;
            if (this.mHasErrorUri) {
                Log.i(TAG, " This delete content list included invalid ID !");
            }
            if (deleteListSize > 0) {
                deleteList = new long[deleteListSize];
                for (int cnt2 = 0; cnt2 < deleteListSize; cnt2++) {
                    deleteList[cnt2] = tmpDeleteList.get(cnt2).longValue();
                }
            }
        }
        return deleteList;
    }

    private long[] getDeleteSingleImageList(String[] uri) {
        String decUrl;
        long[] deleteList = null;
        int listSize = uri.length;
        if (listSize > 0) {
            List<Long> tmpDeleteList = new ArrayList<>();
            for (int cnt = 0; cnt < listSize; cnt++) {
                if (uri[cnt] != null && -1 != uri[cnt].indexOf("image:content?contentId=") && (decUrl = SRCtrlPlaybackUtil.urlDecode(uri[cnt])) != null) {
                    long recOrderId = SRCtrlPlaybackUtil.getRecOrderId(decUrl);
                    if (-1 != recOrderId) {
                        tmpDeleteList.add(Long.valueOf(recOrderId));
                    }
                }
            }
            int deleteListSize = tmpDeleteList.size();
            this.mHasErrorUri = deleteListSize < listSize;
            if (this.mHasErrorUri) {
                Log.i(TAG, " This delete content list included invalid ID !");
            }
            if (deleteListSize > 0) {
                deleteList = new long[deleteListSize];
                for (int cnt2 = 0; cnt2 < deleteListSize; cnt2++) {
                    deleteList[cnt2] = tmpDeleteList.get(cnt2).longValue();
                }
            }
        }
        return deleteList;
    }

    private String[] getDeleteFileList(String[] uri) {
        String decUrl;
        String uniqId;
        int listSize = uri.length;
        if (listSize <= 0) {
            return null;
        }
        List<String> tmpDeleteList = new ArrayList<>();
        for (int cnt = 0; cnt < listSize; cnt++) {
            if (uri[cnt] != null && ((-1 != uri[cnt].indexOf("image:content?contentId=") || -1 != uri[cnt].indexOf("video:content?contentId=")) && (decUrl = SRCtrlPlaybackUtil.urlDecode(uri[cnt])) != null && (uniqId = SRCtrlPlaybackUtil.getUniqId(decUrl)) != null)) {
                tmpDeleteList.add(uniqId);
            }
        }
        int deleteListSize = tmpDeleteList.size();
        this.mHasErrorUri = deleteListSize < listSize;
        if (this.mHasErrorUri) {
            Log.i(TAG, " This delete content list included invalid ID !");
        }
        if (deleteListSize > 0) {
            return (String[]) tmpDeleteList.toArray(new String[0]);
        }
        return null;
    }

    private AvindexDeleteObserver getProxyObserver() {
        return new ProxyObserver();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ProxyObserver implements AvindexDeleteObserver {
        private static final String TAG1 = "ProxyObserver";

        private ProxyObserver() {
        }

        public void onComplete(int arg0) {
            Log.i(TAG1, DeleteMultiple.MSG_ON_COMPLETE + arg0);
            DeleteMultiple.this.mTransId = -1;
            if (DeleteMultiple.this.mOrgObserver != null) {
                DeleteMultiple.this.getHandler().postAtTime(new OnCompleteRunnable(arg0), DeleteMultiple.token, SystemClock.uptimeMillis());
            }
            synchronized (DeleteMultiple.sync) {
                DeleteMultiple.sync.notify();
            }
        }

        public void onProgress(int arg0, int arg1) {
            Log.i(TAG1, DeleteMultiple.MSG_ON_PROGRESS + arg0 + "/" + arg1);
            if (DeleteMultiple.this.mOrgObserver != null) {
                DeleteMultiple.this.getHandler().postAtTime(new OnProgressRunnable(arg0, arg1), DeleteMultiple.token, SystemClock.uptimeMillis());
            }
        }
    }

    /* loaded from: classes.dex */
    protected class OnCompleteRunnable implements Runnable {
        private int mArg0;

        public OnCompleteRunnable(int arg0) {
            this.mArg0 = 0;
            this.mArg0 = arg0;
        }

        @Override // java.lang.Runnable
        public void run() {
            Log.i(DeleteMultiple.TAG, "onCompleteRunnable " + this.mArg0);
            DeleteMultiple.this.mOrgObserver.onComplete(this.mArg0);
        }
    }

    /* loaded from: classes.dex */
    protected class OnProgressRunnable implements Runnable {
        private int mArg0;
        private int mArg1;

        public OnProgressRunnable(int arg0, int arg1) {
            this.mArg0 = 0;
            this.mArg1 = 0;
            this.mArg0 = arg0;
            this.mArg1 = arg1;
        }

        @Override // java.lang.Runnable
        public void run() {
            Log.i(DeleteMultiple.TAG, "OnProgressRunnable " + this.mArg0 + "/" + this.mArg1);
            DeleteMultiple.this.mOrgObserver.onProgress(this.mArg0, this.mArg1);
        }
    }

    private AvindexDeleteObserver getOrgObserver() {
        return new OrgObserver();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class OrgObserver implements AvindexDeleteObserver {
        private static final String TAG2 = "OrgObserver";

        private OrgObserver() {
        }

        public void onComplete(int result) {
            Log.i(TAG2, DeleteMultiple.MSG_ON_COMPLETE + result);
            switch (result) {
                case 0:
                    DeleteMultiple.this.mResult = DeleteMultiple.this.mHasErrorUri ? -2 : 0;
                    break;
                case 1:
                    DeleteMultiple.this.mResult = -1;
                    break;
                default:
                    DeleteMultiple.this.mResult = -2;
                    break;
            }
            if (result == 0 || 1 == result) {
                ContentsManager mgr = ContentsManager.getInstance();
                ContentResolver resolver = mgr.getContentResolver();
                if (Environment.isAvindexFilesSupported()) {
                    AvindexStore.Files.waitAndUpdateDatabase(resolver, AvindexStore.getExternalMediaIds()[0]);
                } else {
                    AvindexStore.Images.waitAndUpdateDatabase(resolver, AvindexStore.getExternalMediaIds()[0]);
                }
                boolean bret = mgr.requeryData(true);
                if (bret) {
                    DeleteMultiple.this.mAfterNumOfContent = mgr.getContentsTotalCount();
                    mgr.moveToEntryPosition();
                    Log.i(DeleteMultiple.TAG, "onPause mAfterNumOfContent = " + DeleteMultiple.this.mAfterNumOfContent + StringBuilderThreadLocal.PERIOD);
                }
            }
            DeleteMultiple.this.setNextState(PlaySubApp.ID_INDEX_PB, null);
        }

        public void onProgress(int index, int all) {
            Log.i(TAG2, DeleteMultiple.MSG_ON_PROGRESS + index + "/" + all);
        }
    }
}
