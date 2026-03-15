package com.sony.imaging.app.base.common.widget;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.sony.imaging.app.util.AvailableInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class InhWatch {
    private static final int INTERVAL_INDEX_MAX = 60;
    private static final int REPEAT_INTERVAL = 1000;
    public static final int REPEAT_INTERVAL_1SEC = 1;
    public static final int REPEAT_INTERVAL_2SEC = 2;
    public static final int REPEAT_INTERVAL_3SEC = 3;
    public static final int REPEAT_INTERVAL_4SEC = 4;
    public static final int REPEAT_INTERVAL_5SEC = 5;
    private static final String TAG = "InhWatch";
    private static final int exeRemove = 2;
    private static final int exeStart = 0;
    private static final int exeStop = 1;
    private static InhWatch mInstance = null;
    private static int mId = 0;
    private static int mAddNum = 0;
    private static int mStartNum = 0;
    private static int mIntervalIndex = 0;
    private static int TIMEOUT_MESSAGE = 1;
    private static List<callbackInfo> callbackList_1sec = new ArrayList();
    private static List<callbackInfo> callbackList_2sec = new ArrayList();
    private static List<callbackInfo> callbackList_3sec = new ArrayList();
    private static List<callbackInfo> callbackList_4sec = new ArrayList();
    private static List<callbackInfo> callbackList_5sec = new ArrayList();
    private static final HashMap<String, List<callbackInfo>> CALLBACKLIST = new HashMap<>();
    private Object mlock = new Object();
    private Handler handler = new Handler() { // from class: com.sony.imaging.app.base.common.widget.InhWatch.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            synchronized (InhWatch.this.mlock) {
                if (msg.what == InhWatch.TIMEOUT_MESSAGE) {
                    InhWatch.access$208();
                    if (InhWatch.mStartNum > 0) {
                        InhWatch.this.onCallback();
                        sendEmptyMessageDelayed(InhWatch.TIMEOUT_MESSAGE, 1000L);
                    }
                    if (InhWatch.mIntervalIndex >= 60) {
                        int unused = InhWatch.mIntervalIndex = 0;
                    }
                }
            }
        }
    };

    /* loaded from: classes.dex */
    public interface InhWatchInterface {
        void onCallback();
    }

    static /* synthetic */ int access$208() {
        int i = mIntervalIndex;
        mIntervalIndex = i + 1;
        return i;
    }

    static {
        CALLBACKLIST.put(Integer.toString(1), callbackList_1sec);
        CALLBACKLIST.put(Integer.toString(2), callbackList_2sec);
        CALLBACKLIST.put(Integer.toString(3), callbackList_3sec);
        CALLBACKLIST.put(Integer.toString(4), callbackList_4sec);
        CALLBACKLIST.put(Integer.toString(5), callbackList_5sec);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class callbackInfo {
        public InhWatchInterface mFunc;
        public int mId;
        public boolean mStatus;
        public String mUniqueId;

        callbackInfo(int id, boolean status, InhWatchInterface callback, String uniqueID) {
            this.mId = id;
            this.mStatus = status;
            this.mFunc = callback;
            this.mUniqueId = uniqueID;
        }
    }

    public InhWatch() {
        mInstance = this;
    }

    public static InhWatch getInstance() {
        if (mInstance == null) {
            new InhWatch();
        }
        return mInstance;
    }

    public int addCallback(InhWatchInterface callback, int interval, String uniqueID) {
        int id = -1;
        synchronized (this.mlock) {
            List<callbackInfo> callbacklist = CALLBACKLIST.get(Integer.toString(interval));
            if (callbacklist != null) {
                callbackInfo info = new callbackInfo(mId, false, callback, uniqueID);
                if (getIndex(callbacklist, info) == -1) {
                    callbacklist.add(info);
                    id = mId;
                    mId++;
                    mAddNum++;
                }
            }
        }
        Log.d(TAG, "add id:" + id + " mAddNum:" + mAddNum + " mStartNum:" + mStartNum + " uniqueID:" + uniqueID);
        return id;
    }

    public void removeCallback(int id) {
        boolean bExe;
        synchronized (this.mlock) {
            bExe = exeCallbackList(id, 2);
            if (mStartNum == 0) {
                mIntervalIndex = 0;
            }
            if (mAddNum == 0) {
                mId = 0;
            }
        }
        Log.d(TAG, "remove id:" + id + " bExe:" + bExe + " mAddNum:" + mAddNum + " mStartNum:" + mStartNum);
    }

    public void startCallback(int id) {
        boolean bExe;
        synchronized (this.mlock) {
            bExe = exeCallbackList(id, 0);
            if (bExe && mStartNum == 1) {
                this.handler.sendEmptyMessageDelayed(TIMEOUT_MESSAGE, 1000L);
            }
        }
        Log.d(TAG, "start id:" + id + " bExe:" + bExe + " mAddNum:" + mAddNum + " mStartNum:" + mStartNum);
    }

    public void stopCallback(int id) {
        boolean bExe;
        synchronized (this.mlock) {
            bExe = exeCallbackList(id, 1);
            if (mStartNum == 0) {
                mIntervalIndex = 0;
            }
        }
        Log.d(TAG, "stop id:" + id + " bExe:" + bExe + " mAddNum:" + mAddNum + " mStartNum:" + mStartNum);
    }

    private boolean exeCallbackList(int id, int exe) {
        int position;
        boolean bExe = false;
        if (id < 0) {
            return false;
        }
        for (int index = 0; index < CALLBACKLIST.size(); index++) {
            int interval = index + 1;
            List<callbackInfo> callbacklist = CALLBACKLIST.get(Integer.toString(interval));
            if (callbacklist != null) {
                Iterator i$ = callbacklist.iterator();
                while (true) {
                    if (!i$.hasNext()) {
                        break;
                    }
                    callbackInfo info = i$.next();
                    if (info.mId == id && (position = getIndex(callbacklist, info)) != -1) {
                        if (exe == 0) {
                            if (!info.mStatus) {
                                info.mStatus = true;
                                mStartNum++;
                            }
                        } else if (exe == 1) {
                            if (info.mStatus) {
                                info.mStatus = false;
                                mStartNum--;
                            }
                        } else if (exe == 2) {
                            if (info.mStatus) {
                                info.mStatus = false;
                                mStartNum--;
                            }
                            callbacklist.remove(position);
                            mAddNum--;
                        }
                        bExe = true;
                    }
                }
                if (bExe) {
                    break;
                }
            }
        }
        return bExe;
    }

    private int getIndex(List<callbackInfo> list, callbackInfo info) {
        int listIndex = 0;
        boolean isExist = false;
        Iterator i$ = list.iterator();
        while (true) {
            if (!i$.hasNext()) {
                break;
            }
            callbackInfo v = i$.next();
            if (v.mUniqueId.equals(info.mUniqueId)) {
                isExist = true;
                break;
            }
            listIndex++;
        }
        if (!isExist) {
            return -1;
        }
        return listIndex;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onCallback() {
        List<callbackInfo> callbacklist;
        boolean inhUpdate = false;
        for (int index = 0; index < CALLBACKLIST.size(); index++) {
            int interval = index + 1;
            if (mIntervalIndex % interval == 0 && (callbacklist = CALLBACKLIST.get(Integer.toString(interval))) != null && callbacklist.size() != 0) {
                for (callbackInfo info : callbacklist) {
                    if (info.mStatus) {
                        if (!inhUpdate) {
                            AvailableInfo.update();
                            inhUpdate = true;
                        }
                        info.mFunc.onCallback();
                    }
                }
            }
        }
    }
}
