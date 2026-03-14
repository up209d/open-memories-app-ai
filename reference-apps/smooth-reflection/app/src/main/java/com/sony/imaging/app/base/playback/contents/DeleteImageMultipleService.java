package com.sony.imaging.app.base.playback.contents;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Message;
import android.util.Log;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.scalar.provider.AvindexDeleteObserver;
import com.sony.scalar.provider.AvindexStore;
import java.util.Iterator;

@Deprecated
/* loaded from: classes.dex */
public class DeleteImageMultipleService extends DeleteService {
    private static final String MSG_CANCEL = "cancel delete : ";
    private static final String MSG_DELETE = "execute delete : ";
    private static final String TAG = "DeleteMultipleService";
    protected static final int TRANS_ID_INVALID = -1;
    private static DeleteService mInstance = new DeleteImageMultipleService(100);
    protected long[] mIds;
    protected Object mLockObject;
    protected AvindexDeleteObserver mObserver;
    protected int mTransId;

    public static DeleteService getInstance() {
        return mInstance;
    }

    protected DeleteImageMultipleService(int selectableCount) {
        super(selectableCount, 1);
        this.mLockObject = new Object();
        this.mTransId = -1;
        this.mObserver = getObserver();
    }

    @Override // com.sony.imaging.app.base.playback.contents.EditServiceImpl, com.sony.imaging.app.base.playback.contents.EditService
    public void initialize() {
        super.initialize();
        this.mIds = null;
        this.mTransId = -1;
    }

    protected AvindexDeleteObserver getObserver() {
        return new Observer();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class Observer implements AvindexDeleteObserver {
        protected static final String MSG_ON_COMPLETE = "onComplete : ";
        protected static final String MSG_ON_PROGRESS = "onProgress : ";

        protected Observer() {
        }

        public void onComplete(int result) {
            int result2;
            synchronized (DeleteImageMultipleService.this.mLockObject) {
                Log.i(DeleteImageMultipleService.TAG, LogHelper.getScratchBuilder(MSG_ON_COMPLETE).append(result).toString());
                switch (result) {
                    case 0:
                        result2 = 0;
                        break;
                    case 1:
                        result2 = -1;
                        break;
                    default:
                        result2 = -5;
                        break;
                }
                DeleteImageMultipleService.this.mTransId = -1;
                Message msg = Message.obtain(DeleteImageMultipleService.this.mHandler, 8194, Integer.valueOf(result2));
                msg.sendToTarget();
                DeleteImageMultipleService.this.mLockObject.notifyAll();
            }
        }

        public void onProgress(int index, int all) {
            Log.i(DeleteImageMultipleService.TAG, LogHelper.getScratchBuilder(MSG_ON_PROGRESS).append(index).append(LogHelper.MSG_COLON).append(all).toString());
            Message msg = Message.obtain(DeleteImageMultipleService.this.mHandler, 8193, index, all);
            msg.sendToTarget();
        }
    }

    protected String getMediaId() {
        String[] externalMedias = AvindexStore.getExternalMediaIds();
        return externalMedias[0];
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.contents.DeleteService, com.sony.imaging.app.base.playback.contents.EditServiceImpl
    public int preprocess() {
        if (this.mIds != null) {
            return 0;
        }
        return super.preprocess();
    }

    @Override // com.sony.imaging.app.base.playback.contents.DeleteService, com.sony.imaging.app.base.playback.contents.EditServiceImpl, com.sony.imaging.app.base.playback.contents.EditService
    public void execute() {
        int result = preprocess();
        if (isError(result)) {
            Message msg = Message.obtain(this.mHandler, 8194, Integer.valueOf(result));
            msg.sendToTarget();
            return;
        }
        ContentResolver resolver = ContentsManager.getInstance().getContentResolver();
        Uri baseUri = AvindexStore.Images.Media.getContentUri(getMediaId());
        this.mTransId = AvindexStore.Images.Media.deleteMultiImage(resolver, baseUri, getIds(), this.mObserver);
        Log.i(TAG, LogHelper.getScratchBuilder(MSG_DELETE).append(this.mTransId).toString());
        if (this.mTransId < 0) {
            this.mTransId = -1;
            Message msg2 = Message.obtain(this.mHandler, 8194, Integer.valueOf(EditServiceImpl.ERR_UNKNOWN));
            msg2.sendToTarget();
        }
    }

    @Override // com.sony.imaging.app.base.playback.contents.EditServiceImpl, com.sony.imaging.app.base.playback.contents.EditService
    public void cancel() {
        synchronized (this.mLockObject) {
            if (this.mTransId > 0) {
                AvindexStore.Images.Media.cancelDeleteMultiImage(this.mTransId);
                Log.i(TAG, LogHelper.getScratchBuilder(MSG_CANCEL).append(this.mTransId).toString());
                while (this.mTransId > 0) {
                    try {
                        this.mLockObject.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void setIds(long[] Ids) {
        this.mIds = Ids;
    }

    public long[] getIds() {
        if (this.mIds != null) {
            return this.mIds;
        }
        if (this.mList.count() > 0) {
            long[] ids = new long[this.mList.count()];
            int index = 0;
            Iterator<ContentsIdentifier> it = this.mList.iterate();
            while (it.hasNext()) {
                ids[index] = it.next()._id;
                index++;
            }
            return ids;
        }
        return null;
    }

    @Override // com.sony.imaging.app.base.playback.contents.EditServiceImpl, com.sony.imaging.app.base.playback.contents.EditService
    public int countSelected() {
        return this.mIds != null ? this.mIds.length : super.countSelected();
    }
}
