package com.sony.imaging.app.base.playback.contents;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.sony.imaging.app.base.playback.LogHelper;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public class EditServiceImpl extends EditService {
    public static final int ERR_UNKNOWN = -9999;
    private static final int KIND_END = 8194;
    private static final int KIND_PROGRESS = 8193;
    private static final String MSG_CANCEL = "cancel";
    private static final String MSG_CANCELLED = "Already cancelled";
    private static final String MSG_END_CANCEL = "cancel end";
    private static final String MSG_EXECUTING = "executing ";
    private static final String MSG_FAILED_TO_GET_ID = "failed to get id";
    private static final String MSG_HANDLE_MSG_AFTER_TERMINATED = "handleProgressMsg already terminated";
    private static final String TAG = "EditService";
    private SelectionList mList;
    private int mNotifyProgressInterval;
    private int mSelectableCount;
    private boolean mIsCancelled = false;
    private boolean[] mIsGroupChecked = null;
    private Thread mProcessingThread = null;
    private Handler mHandler = new Handler() { // from class: com.sony.imaging.app.base.playback.contents.EditServiceImpl.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            if (msg.what == EditServiceImpl.KIND_PROGRESS) {
                EditServiceImpl.this.handleProgressMsg(msg.arg1, msg);
            } else if (msg.what == EditServiceImpl.KIND_END) {
                EditServiceImpl.this.handleEndMsg(msg.arg1, msg);
            }
        }
    };

    public EditServiceImpl(int selectableCount, int notifyProgressInterval) {
        this.mNotifyProgressInterval = 1;
        this.mSelectableCount = selectableCount;
        this.mNotifyProgressInterval = notifyProgressInterval;
    }

    public boolean isInitialized() {
        return this.mList != null;
    }

    @Override // com.sony.imaging.app.base.playback.contents.EditService
    public void initialize() {
        if (this.mList == null) {
            this.mList = new SelectionList(this.mSelectableCount);
        }
        this.mIsCancelled = false;
        int count = this.mMgr.getGroupCount();
        if (count <= 0) {
            Log.w(TAG, "Group Count less than 0 : " + count);
            count = 0;
        }
        this.mIsGroupChecked = new boolean[count];
    }

    @Override // com.sony.imaging.app.base.playback.contents.EditService
    public void terminate() {
        if (this.mList != null) {
            this.mList.close();
            this.mList = null;
        }
        this.mIsGroupChecked = null;
        this.mProcessingThread = null;
    }

    @Override // com.sony.imaging.app.base.playback.contents.EditService
    public int getSelectableCount() {
        return this.mSelectableCount;
    }

    @Override // com.sony.imaging.app.base.playback.contents.EditService
    public int countSelected() {
        return this.mList.count();
    }

    @Override // com.sony.imaging.app.base.playback.contents.EditService
    public int select(boolean selectIt, int file) {
        int group = this.mMgr.getGroupPosition();
        if (selectIt) {
            ContentsIdentifier id = this.mMgr.getContentsIdAt(file);
            if (id == null) {
                Log.w(TAG, MSG_FAILED_TO_GET_ID);
                return ERR_UNKNOWN;
            }
            if (!this.mList.add(group, file, id)) {
                return -2;
            }
        } else {
            this.mList.remove(group, file);
        }
        this.mIsGroupChecked[group] = false;
        return 0;
    }

    @Override // com.sony.imaging.app.base.playback.contents.EditService
    public boolean isSelectable(int file) {
        return true;
    }

    @Override // com.sony.imaging.app.base.playback.contents.EditService
    public boolean isSelected(int file) {
        int group = this.mMgr.getGroupPosition();
        return this.mList.isSelected(group, file);
    }

    @Override // com.sony.imaging.app.base.playback.contents.EditService
    public int selectGroup(boolean selectIt) {
        int c = this.mMgr.getContentsCount();
        if (c > this.mSelectableCount) {
            return -2;
        }
        ArrayList<Integer> list = new ArrayList<>(c);
        int selectingCount = 0;
        for (int i = 0; i < c; i++) {
            if (isSelectable(i) && isSelected(i) != selectIt) {
                list.add(Integer.valueOf(i));
                selectingCount++;
            }
        }
        Log.d(TAG, LogHelper.getScratchBuilder(Integer.toString(list.size())).append(LogHelper.MSG_COMMA).append(selectingCount).toString());
        if (selectIt && countSelected() + selectingCount > this.mSelectableCount) {
            return -2;
        }
        int group = this.mMgr.getGroupPosition();
        Iterator<Integer> iterator = list.iterator();
        while (iterator.hasNext()) {
            Integer file = iterator.next();
            if (selectIt) {
                this.mList.add(group, file.intValue(), this.mMgr.getContentsIdAt(file.intValue()));
            } else {
                this.mList.remove(group, file.intValue());
            }
        }
        list.clear();
        this.mIsGroupChecked[group] = selectIt;
        return 0;
    }

    @Override // com.sony.imaging.app.base.playback.contents.EditService
    public boolean isGroupSelectable() {
        return ContentsManager.getInstance().getContentsCount() <= this.mSelectableCount;
    }

    @Override // com.sony.imaging.app.base.playback.contents.EditService
    public boolean isGroupChecked() {
        int group = this.mMgr.getGroupPosition();
        return this.mIsGroupChecked[group];
    }

    @Override // com.sony.imaging.app.base.playback.contents.EditService
    public void execute() {
        this.mIsCancelled = false;
        int result = preprocess();
        if (isError(result)) {
            Message msg = Message.obtain(this.mHandler, KIND_END, Integer.valueOf(result));
            msg.sendToTarget();
        } else {
            this.mProcessingThread = new Thread(TAG) { // from class: com.sony.imaging.app.base.playback.contents.EditServiceImpl.2
                @Override // java.lang.Thread, java.lang.Runnable
                public void run() {
                    int current = 0;
                    Iterator<ContentsIdentifier> iterator = EditServiceImpl.this.mList.iterate();
                    int result2 = 0;
                    while (!EditServiceImpl.this.mIsCancelled && iterator.hasNext()) {
                        ContentsIdentifier id = iterator.next();
                        Log.d(EditServiceImpl.TAG, LogHelper.getScratchBuilder(EditServiceImpl.MSG_EXECUTING).append(id.data).toString());
                        EditServiceImpl editServiceImpl = EditServiceImpl.this;
                        result2 = EditServiceImpl.this.processContents(id);
                        if (editServiceImpl.isError(result2)) {
                            break;
                        }
                        current++;
                        if (current % EditServiceImpl.this.mNotifyProgressInterval == 0) {
                            Message msg2 = Message.obtain(EditServiceImpl.this.mHandler, EditServiceImpl.KIND_PROGRESS, current, 0);
                            msg2.sendToTarget();
                            Thread.yield();
                        }
                    }
                    if (!EditServiceImpl.this.mIsCancelled) {
                        Message msg3 = Message.obtain(EditServiceImpl.this.mHandler, EditServiceImpl.KIND_END, result2, 0);
                        msg3.sendToTarget();
                    }
                }
            };
            this.mProcessingThread.start();
        }
    }

    protected void handleProgressMsg(int progress, Message msg) {
        if (this.mList == null) {
            Log.w(TAG, MSG_HANDLE_MSG_AFTER_TERMINATED);
        } else {
            int max = this.mList.count();
            notifyProgress(progress, max);
        }
    }

    protected void handleEndMsg(int result, Message msg) {
        notifyEnd(postprocess(result));
        terminate();
    }

    @Override // com.sony.imaging.app.base.playback.contents.EditService
    public void cancel() {
        Log.i(TAG, MSG_CANCEL);
        if (this.mProcessingThread == null) {
            Log.i(TAG, MSG_CANCELLED);
        } else {
            this.mIsCancelled = true;
            try {
                this.mProcessingThread.interrupt();
                this.mProcessingThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.mHandler.removeMessages(KIND_PROGRESS);
            this.mHandler.removeMessages(KIND_END);
            handleEndMsg(-1, null);
        }
        Log.i(TAG, MSG_END_CANCEL);
    }

    protected int preprocess() {
        return 0;
    }

    protected int processContents(ContentsIdentifier id) {
        return 0;
    }

    protected int postprocess(int result) {
        return result;
    }

    protected boolean isError(int result) {
        return false;
    }
}
