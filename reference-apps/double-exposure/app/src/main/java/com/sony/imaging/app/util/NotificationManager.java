package com.sony.imaging.app.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.sony.imaging.app.base.playback.LogHelper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/* loaded from: classes.dex */
public abstract class NotificationManager {
    private static final int ACTION_NOTIFY_LISTENER = 1;
    private static final int ACTION_NOTIFY_TAG_CHANGED = 2;
    private static final int DEFAULT_MAP_SIZE = 0;
    private static final String TAG = "NotificationManager";
    private NotifyHandler mHandler;
    private HashMap<String, ArrayList<NotificationListener>> mListeners;
    protected boolean mNotPostIfAlreadyQueued;
    protected boolean mNotifyOnAdded;
    protected Collection<String> mTagsThinnedOut;

    public abstract Object getValue(String str);

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class NotifyHandler extends Handler {
        private NotifyHandler() {
            super(Looper.getMainLooper());
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            int what = msg.what;
            if (what == 1) {
                NotificationListener listener = (NotificationListener) msg.obj;
                String[] tags = listener.getTags();
                for (String tag : tags) {
                    listener.onNotify(tag);
                }
                return;
            }
            if (what == 2) {
                NotificationManager.this.notifySync((String) msg.obj);
            }
        }
    }

    public NotificationManager() {
        this.mNotifyOnAdded = false;
        this.mNotPostIfAlreadyQueued = false;
        this.mListeners = new HashMap<>(getDefaultListenerCapacity());
        this.mHandler = new NotifyHandler();
    }

    public NotificationManager(boolean notifyOnAdded) {
        this();
        this.mNotifyOnAdded = notifyOnAdded;
    }

    public NotificationManager(boolean notifyOnAdded, Collection<String> tagsThinnedOut) {
        this(notifyOnAdded);
        this.mTagsThinnedOut = tagsThinnedOut;
    }

    public NotificationManager(boolean notifyOnAdded, boolean notPostIfAlreadyQueued) {
        this(notifyOnAdded);
        this.mNotPostIfAlreadyQueued = notPostIfAlreadyQueued;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onFirstListenerSet(String tag) {
    }

    protected void onAllListenerRemoved(String tag) {
    }

    protected int getDefaultListenerCapacity() {
        return 0;
    }

    public void setNotificationListener(NotificationListener listener) {
        setNotificationListener(listener, false);
    }

    public void setNotificationListener(NotificationListener listener, boolean addEvenIfRegistered) {
        ArrayList<NotificationListener> list;
        String[] tags = listener.getTags();
        for (String tag : tags) {
            if (this.mListeners.containsKey(tag)) {
                list = this.mListeners.get(tag);
            } else {
                list = new ArrayList<>();
                this.mListeners.put(tag, list);
                onFirstListenerSet(tag);
            }
            if (list.contains(listener)) {
                Log.w(TAG, "Already contained in list " + tag + LogHelper.MSG_COLON + listener);
                if (addEvenIfRegistered) {
                    list.add(listener);
                    Log.d(TAG, "added in list " + tag + LogHelper.MSG_COLON + listener + "->" + list.size());
                }
            } else {
                list.add(listener);
                Log.d(TAG, "added in list " + tag + LogHelper.MSG_COLON + listener + "->" + list.size());
            }
            if (this.mNotifyOnAdded) {
                Message msg = this.mHandler.obtainMessage(1, listener);
                this.mHandler.sendMessage(msg);
            }
        }
    }

    public void removeNotificationListener(NotificationListener listener) {
        String[] tags = listener.getTags();
        if (this.mNotifyOnAdded) {
            this.mHandler.removeMessages(1, listener);
        }
        for (String tag : tags) {
            Log.d(TAG, "removing from list " + tag + LogHelper.MSG_COLON + listener);
            if (this.mListeners.containsKey(tag)) {
                ArrayList<NotificationListener> list = this.mListeners.get(tag);
                list.remove(listener);
                Log.d(TAG, "removed " + list.size());
                if (list.isEmpty()) {
                    this.mListeners.remove(tag);
                    onAllListenerRemoved(tag);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void notify(String tag) {
        notify(tag, false);
    }

    protected void notify(String tag, boolean ignoreIfAlreadyQueued) {
        boolean checkIfQueued = ignoreIfAlreadyQueued || this.mNotPostIfAlreadyQueued || (this.mTagsThinnedOut != null && this.mTagsThinnedOut.contains(tag));
        if (checkIfQueued && this.mHandler.hasMessages(2, tag)) {
            Log.d(TAG, "notification NOT posted because it already queued.");
        } else {
            Message msg = this.mHandler.obtainMessage(2, tag);
            this.mHandler.sendMessage(msg);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void notifySync(String tag) {
        if (this.mListeners.containsKey(tag)) {
            ArrayList<NotificationListener> list = this.mListeners.get(tag);
            ArrayList<NotificationListener> cloned = (ArrayList) list.clone();
            Iterator i$ = cloned.iterator();
            while (i$.hasNext()) {
                NotificationListener listener = i$.next();
                if (list.contains(listener)) {
                    listener.onNotify(tag);
                }
            }
            return;
        }
        Log.w(TAG, "listener NOT found : " + tag);
    }

    public void dumpListener() {
        Log.i(TAG, "-------------- dumpListener --------------");
        Set<String> keySet = this.mListeners.keySet();
        for (String key : keySet) {
            Log.i(TAG, "[TAG] " + key);
            ArrayList<NotificationListener> list = this.mListeners.get(key);
            Iterator i$ = list.iterator();
            while (i$.hasNext()) {
                NotificationListener listener = i$.next();
                Log.i(TAG, listener.toString());
            }
        }
    }
}
