package com.sony.imaging.app.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/* loaded from: classes.dex */
public abstract class NotificationManager {
    private static final int ACTION_NOTIFY_LISTENER = 1;
    private static final int ACTION_NOTIFY_TAG_CHANGED = 2;
    private static final int CALLER_STACK = 3;
    private static final int DEFAULT_LIST_SIZE = 32;
    private static final int DEFAULT_MAP_SIZE = 0;
    public static final int HOLD_TYPE_OFF = 0;
    public static final int HOLD_TYPE_ON = 1;
    public static final int HOLD_TYPE_ON_ELIMINATE_DUPLICATE = 2;
    private static final String MSG_ELIMINATED = "duplicate eliminated ";
    private static final String MSG_HELD = "held ";
    private static final String MSG_HOLD = "hold ";
    private static final String MSG_HOLD_OFF = "hold off ";
    private static final String MSG_LISTENR = "listener : ";
    private static final String MSG_NOTIFY = "notify ";
    private static final String MSG_NOTIFY_SYNC = "notifySync ";
    private static final int PF_VER_SUPPORT_DEQUE_MESSAGE = 12;
    private static final String TAG = "NotificationManager";
    private HashMap<NotificationListener, String> mCallers;
    private NotifyHandler mHandler;
    protected ArrayList<String> mHeldMessage;
    protected int mHoldType;
    private HashMap<String, ArrayList<NotificationListener>> mListeners;
    private boolean mLogEachListener;
    protected boolean mNotPostIfAlreadyQueued;
    protected boolean mNotifyOnAdded;
    protected Collection<String> mTagsThinnedOut;
    protected ArrayList<String> mThroughMessage;

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
        this.mLogEachListener = 2 == PTag.getSystemLogLevel();
        this.mHeldMessage = new ArrayList<>(32);
        this.mNotifyOnAdded = false;
        this.mNotPostIfAlreadyQueued = false;
        this.mHoldType = 0;
        this.mListeners = new HashMap<>(getDefaultListenerCapacity());
        this.mCallers = new HashMap<>();
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
        if (this.mLogEachListener && !this.mCallers.containsKey(listener)) {
            StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            if (stack.length > 3) {
                String caller = stack[3].getClassName();
                StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
                builder.replace(0, builder.length(), caller.substring(caller.lastIndexOf(46) + 1)).append(StringBuilderThreadLocal.PERIOD).append(stack[3].getMethodName()).append(" : ").append(stack[3].getLineNumber());
                this.mCallers.put(listener, builder.toString());
                StringBuilderThreadLocal.releaseScratchBuilder(builder);
            }
        }
        setNotificationListener(listener, false);
    }

    public void setNotificationListener(NotificationListener listener, boolean addEvenIfRegistered) {
        ArrayList<NotificationListener> list;
        if (this.mLogEachListener && !this.mCallers.containsKey(listener)) {
            StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            if (stack.length > 3) {
                String caller = stack[3].getClassName();
                StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
                builder.replace(0, builder.length(), caller.substring(caller.lastIndexOf(46) + 1)).append(StringBuilderThreadLocal.PERIOD).append(stack[3].getMethodName()).append(" : ").append(stack[3].getLineNumber());
                this.mCallers.put(listener, builder.toString());
                StringBuilderThreadLocal.releaseScratchBuilder(builder);
            }
        }
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
                Log.w(TAG, "Already contained in list " + tag + " : " + listener);
                if (addEvenIfRegistered) {
                    list.add(listener);
                    Log.d(TAG, "added in list " + tag + " : " + listener + "->" + list.size());
                }
            } else {
                list.add(listener);
                Log.d(TAG, "added in list " + tag + " : " + listener + "->" + list.size());
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
            Log.d(TAG, "removing from list " + tag + " : " + listener);
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
        if (this.mLogEachListener) {
            this.mCallers.remove(listener);
        }
    }

    public void notify(String tag) {
        notify(tag, false);
    }

    public void notify(String tag, boolean ignoreIfAlreadyQueued) {
        boolean checkIfQueued = false;
        if (ignoreIfAlreadyQueued || this.mNotPostIfAlreadyQueued || (this.mTagsThinnedOut != null && this.mTagsThinnedOut.contains(tag))) {
            checkIfQueued = true;
        }
        if (checkIfQueued && this.mHandler.hasMessages(2, tag)) {
            Log.d(TAG, "notification NOT posted because it already queued.");
            return;
        }
        synchronized (this.mHeldMessage) {
            if (this.mHoldType != 0 && (this.mThroughMessage == null || !this.mThroughMessage.contains(tag))) {
                StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
                if (1 == this.mHoldType || !this.mHeldMessage.contains(tag)) {
                    builder.replace(0, builder.length(), MSG_HELD).append(MSG_NOTIFY).append(tag);
                    Log.i(TAG, builder.toString());
                    this.mHeldMessage.add(tag);
                } else {
                    builder.replace(0, builder.length(), MSG_ELIMINATED).append(MSG_NOTIFY).append(tag);
                    Log.i(TAG, builder.toString());
                }
                StringBuilderThreadLocal.releaseScratchBuilder(builder);
                return;
            }
            Message msg = this.mHandler.obtainMessage(2, tag);
            this.mHandler.sendMessage(msg);
        }
    }

    public void remove(String tag) {
        this.mHandler.removeMessages(2, tag);
    }

    public void notifySync(String tag) {
        synchronized (this.mHeldMessage) {
            if (this.mHoldType != 0 && (this.mThroughMessage == null || !this.mThroughMessage.contains(tag))) {
                StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
                if (1 == this.mHoldType || !this.mHeldMessage.contains(tag)) {
                    builder.replace(0, builder.length(), MSG_HELD).append(MSG_NOTIFY_SYNC).append(tag);
                    Log.i(TAG, builder.toString());
                    this.mHeldMessage.add(tag);
                } else {
                    builder.replace(0, builder.length(), MSG_ELIMINATED).append(MSG_NOTIFY_SYNC).append(tag);
                    Log.i(TAG, builder.toString());
                }
                StringBuilderThreadLocal.releaseScratchBuilder(builder);
                return;
            }
            if (this.mListeners.containsKey(tag)) {
                ArrayList<NotificationListener> list = this.mListeners.get(tag);
                ArrayList<NotificationListener> cloned = (ArrayList) list.clone();
                String notify = "";
                String msg_listener = "";
                if (1 <= PTag.getSystemLogLevel()) {
                    StringBuilder builder2 = StringBuilderThreadLocal.getScratchBuilder();
                    notify = builder2.replace(0, builder2.length(), MSG_NOTIFY).append(tag).toString();
                    StringBuilderThreadLocal.releaseScratchBuilder(builder2);
                    PTag.start(notify, 1);
                    PTag.startTimeTag(notify, 1);
                }
                Iterator i$ = cloned.iterator();
                while (i$.hasNext()) {
                    NotificationListener listener = i$.next();
                    if (list.contains(listener)) {
                        if (this.mLogEachListener) {
                            StringBuilder builder3 = StringBuilderThreadLocal.getScratchBuilder();
                            msg_listener = builder3.replace(0, builder3.length(), MSG_LISTENR).append(this.mCallers.get(listener)).toString();
                            StringBuilderThreadLocal.releaseScratchBuilder(builder3);
                            PTag.start(msg_listener, 2);
                            PTag.startTimeTag(msg_listener, 2);
                        }
                        listener.onNotify(tag);
                        if (this.mLogEachListener) {
                            PTag.endTimeTag(msg_listener, 2);
                            PTag.end(msg_listener, 2);
                        }
                    }
                }
                PTag.endTimeTag(notify, 1);
                PTag.end(notify, 1);
                return;
            }
            Log.w(TAG, "listener NOT found : " + tag);
        }
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

    public void hold(int type) {
        hold(type, null);
    }

    public void hold(int type, ArrayList<String> throughTags) {
        if (Environment.getVersionPfAPI() >= 12) {
            StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
            builder.replace(0, builder.length(), MSG_HOLD).append(type).append(" : ").append(throughTags);
            Log.i(TAG, builder.toString());
            StringBuilderThreadLocal.releaseScratchBuilder(builder);
            synchronized (this.mHeldMessage) {
                this.mThroughMessage = throughTags;
                this.mHoldType = type;
                if (type == 0) {
                    ArrayList<String> list = this.mHeldMessage;
                    long start = SystemClock.uptimeMillis();
                    StringBuilder builder2 = StringBuilderThreadLocal.getScratchBuilder();
                    builder2.replace(0, builder2.length(), MSG_HOLD_OFF).append(list.size());
                    PTag.start(builder2.toString());
                    StringBuilderThreadLocal.releaseScratchBuilder(builder2);
                    Iterator i$ = list.iterator();
                    while (i$.hasNext()) {
                        String tag = i$.next();
                        notify(tag);
                    }
                    this.mHeldMessage.clear();
                    long end = SystemClock.uptimeMillis();
                    StringBuilder builder3 = StringBuilderThreadLocal.getScratchBuilder();
                    builder3.replace(0, builder3.length(), MSG_HOLD_OFF).append(end - start);
                    PTag.end(builder3.toString());
                    StringBuilderThreadLocal.releaseScratchBuilder(builder3);
                } else {
                    List<Message> sent = this.mHandler.dequeueMessages(2);
                    if (sent != null) {
                        StringBuilder builder4 = StringBuilderThreadLocal.getScratchBuilder();
                        for (Message message : sent) {
                            if (this.mThroughMessage != null && this.mThroughMessage.contains(message.obj)) {
                                this.mHandler.sendMessageAtTime(message, message.getWhen());
                            } else {
                                if (1 == type || !this.mHeldMessage.contains(message.obj)) {
                                    builder4.replace(0, builder4.length(), MSG_HELD).append(message.obj);
                                    Log.i(TAG, builder4.toString());
                                    this.mHeldMessage.add((String) message.obj);
                                } else {
                                    builder4.replace(0, builder4.length(), MSG_ELIMINATED).append(message.obj);
                                    Log.i(TAG, builder4.toString());
                                }
                                message.recycle();
                            }
                        }
                        StringBuilderThreadLocal.releaseScratchBuilder(builder4);
                    }
                }
            }
        }
    }
}
