package com.sony.imaging.app.base.playback.loader;

import android.os.Handler;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;
import com.sony.imaging.app.base.playback.LogHelper;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class Loader {
    private static final int DELAY_TIME_WAKE_IDLE_HANDLER = 1;
    private static final String MSG_END_GET_DATA = "getData end";
    private static final int MSG_ID_LOADER_IDLE = 4097;
    private static final String MSG_JOIN_THREAD = "join loader ";
    private static final String MSG_QUEUE_IDLE = "queueIdle";
    private static final String MSG_REMOVE_FROM_QUEUE = "remove from Queue pos=";
    private static final String MSG_START_GET_DATA = "getData start";
    private static final String MSG_WAIT_IDLE = "wait until idle";
    private Thread mDecodeThread;
    private boolean mDone;
    final String TAG = getClass().getSimpleName();
    private final ArrayList<WorkItem> mQueue = new ArrayList<>();
    private Handler mHandler = new Handler();
    private MessageQueue.IdleHandler mIdleHandler = new MessageQueue.IdleHandler() { // from class: com.sony.imaging.app.base.playback.loader.Loader.1
        @Override // android.os.MessageQueue.IdleHandler
        public synchronized boolean queueIdle() {
            Log.d(Loader.this.TAG, Loader.MSG_QUEUE_IDLE);
            notifyAll();
            return true;
        }
    };

    /* loaded from: classes.dex */
    public interface LoadedCallback {
        void run(Object obj, Task task);
    }

    public void apply(Task task, LoadedCallback loadedRunnable) {
        if (this.mDecodeThread == null) {
            start();
        }
        synchronized (this.mQueue) {
            WorkItem w = new WorkItem(task, loadedRunnable);
            this.mQueue.add(w);
            this.mQueue.notifyAll();
        }
    }

    public void terminate() {
        clearQueue();
        stop();
    }

    public boolean removeQueueItem(int item) {
        synchronized (this.mQueue) {
            int i = 0;
            int c = this.mQueue.size();
            while (true) {
                if (i >= c) {
                    break;
                }
                if (this.mQueue.get(i).mTask.getPos() != item) {
                    i++;
                } else {
                    Log.v(this.TAG, LogHelper.getScratchBuilder(MSG_REMOVE_FROM_QUEUE).append(item).toString());
                    this.mQueue.remove(i);
                    break;
                }
            }
        }
        return true;
    }

    public int[] clearQueue() {
        int[] tags;
        synchronized (this.mQueue) {
            int n = this.mQueue.size();
            tags = new int[n];
            for (int i = 0; i < n; i++) {
                tags[i] = this.mQueue.get(i).mTask.getPos();
            }
            this.mQueue.clear();
        }
        return tags;
    }

    public boolean isEmpty() {
        boolean result;
        synchronized (this.mQueue) {
            result = this.mQueue.isEmpty();
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class WorkItem {
        LoadedCallback mOnLoadedRunnable;
        Task mTask;

        WorkItem(Task task, LoadedCallback onLoadedRunnable) {
            this.mTask = task;
            this.mOnLoadedRunnable = onLoadedRunnable;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class WorkerThread implements Runnable {
        private WorkerThread() {
        }

        /* JADX WARN: Can't wrap try/catch for region: R(12:6|7|8|9|38|27|28|29|30|31|(3:33|34|35)(1:36)|21) */
        /* JADX WARN: Code restructure failed: missing block: B:38:0x00b6, code lost:            r2 = move-exception;     */
        /* JADX WARN: Code restructure failed: missing block: B:39:0x00b7, code lost:            r2.printStackTrace();     */
        @Override // java.lang.Runnable
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public void run() {
            /*
                r10 = this;
            L0:
                com.sony.imaging.app.base.playback.loader.Loader r5 = com.sony.imaging.app.base.playback.loader.Loader.this
                android.os.MessageQueue$IdleHandler r6 = com.sony.imaging.app.base.playback.loader.Loader.access$000(r5)
                monitor-enter(r6)
                com.sony.imaging.app.base.playback.loader.Loader r5 = com.sony.imaging.app.base.playback.loader.Loader.this     // Catch: java.lang.Throwable -> L4b
                boolean r5 = com.sony.imaging.app.base.playback.loader.Loader.access$100(r5)     // Catch: java.lang.Throwable -> L4b
                if (r5 == 0) goto L11
                monitor-exit(r6)     // Catch: java.lang.Throwable -> L4b
            L10:
                return
            L11:
                com.sony.imaging.app.base.playback.loader.Loader r5 = com.sony.imaging.app.base.playback.loader.Loader.this     // Catch: java.lang.InterruptedException -> L46 java.lang.Throwable -> L4b
                android.os.Handler r5 = com.sony.imaging.app.base.playback.loader.Loader.access$200(r5)     // Catch: java.lang.InterruptedException -> L46 java.lang.Throwable -> L4b
                r7 = 4097(0x1001, float:5.741E-42)
                r8 = 1
                r5.sendEmptyMessageDelayed(r7, r8)     // Catch: java.lang.InterruptedException -> L46 java.lang.Throwable -> L4b
                com.sony.imaging.app.base.playback.loader.Loader r5 = com.sony.imaging.app.base.playback.loader.Loader.this     // Catch: java.lang.InterruptedException -> L46 java.lang.Throwable -> L4b
                java.lang.String r5 = r5.TAG     // Catch: java.lang.InterruptedException -> L46 java.lang.Throwable -> L4b
                java.lang.String r7 = "wait until idle"
                android.util.Log.i(r5, r7)     // Catch: java.lang.InterruptedException -> L46 java.lang.Throwable -> L4b
                com.sony.imaging.app.base.playback.loader.Loader r5 = com.sony.imaging.app.base.playback.loader.Loader.this     // Catch: java.lang.InterruptedException -> L46 java.lang.Throwable -> L4b
                android.os.MessageQueue$IdleHandler r5 = com.sony.imaging.app.base.playback.loader.Loader.access$000(r5)     // Catch: java.lang.InterruptedException -> L46 java.lang.Throwable -> L4b
                r5.wait()     // Catch: java.lang.InterruptedException -> L46 java.lang.Throwable -> L4b
            L30:
                monitor-exit(r6)     // Catch: java.lang.Throwable -> L4b
                r4 = 0
                com.sony.imaging.app.base.playback.loader.Loader r5 = com.sony.imaging.app.base.playback.loader.Loader.this
                java.util.ArrayList r6 = com.sony.imaging.app.base.playback.loader.Loader.access$300(r5)
                monitor-enter(r6)
                com.sony.imaging.app.base.playback.loader.Loader r5 = com.sony.imaging.app.base.playback.loader.Loader.this     // Catch: java.lang.Throwable -> L43
                boolean r5 = com.sony.imaging.app.base.playback.loader.Loader.access$100(r5)     // Catch: java.lang.Throwable -> L43
                if (r5 == 0) goto L4e
                monitor-exit(r6)     // Catch: java.lang.Throwable -> L43
                goto L10
            L43:
                r5 = move-exception
                monitor-exit(r6)     // Catch: java.lang.Throwable -> L43
                throw r5
            L46:
                r3 = move-exception
                r3.printStackTrace()     // Catch: java.lang.Throwable -> L4b
                goto L30
            L4b:
                r5 = move-exception
                monitor-exit(r6)     // Catch: java.lang.Throwable -> L4b
                throw r5
            L4e:
                com.sony.imaging.app.base.playback.loader.Loader r5 = com.sony.imaging.app.base.playback.loader.Loader.this     // Catch: java.lang.Throwable -> L43
                java.util.ArrayList r5 = com.sony.imaging.app.base.playback.loader.Loader.access$300(r5)     // Catch: java.lang.Throwable -> L43
                boolean r5 = r5.isEmpty()     // Catch: java.lang.Throwable -> L43
                if (r5 != 0) goto L97
                com.sony.imaging.app.base.playback.loader.Loader r5 = com.sony.imaging.app.base.playback.loader.Loader.this     // Catch: java.lang.Throwable -> L43
                java.util.ArrayList r5 = com.sony.imaging.app.base.playback.loader.Loader.access$300(r5)     // Catch: java.lang.Throwable -> L43
                r7 = 0
                java.lang.Object r5 = r5.remove(r7)     // Catch: java.lang.Throwable -> L43
                r0 = r5
                com.sony.imaging.app.base.playback.loader.Loader$WorkItem r0 = (com.sony.imaging.app.base.playback.loader.Loader.WorkItem) r0     // Catch: java.lang.Throwable -> L43
                r4 = r0
                monitor-exit(r6)     // Catch: java.lang.Throwable -> L43
                java.lang.Thread.yield()
                r5 = 0
                java.lang.Thread.sleep(r5)     // Catch: java.lang.InterruptedException -> Lb6
            L72:
                com.sony.imaging.app.base.playback.loader.Loader r5 = com.sony.imaging.app.base.playback.loader.Loader.this
                java.lang.String r5 = r5.TAG
                java.lang.String r6 = "getData start"
                android.util.Log.d(r5, r6)
                com.sony.imaging.app.base.playback.loader.Task r5 = r4.mTask
                java.lang.Object r1 = r5.getData()
                com.sony.imaging.app.base.playback.loader.Loader r5 = com.sony.imaging.app.base.playback.loader.Loader.this
                java.lang.String r5 = r5.TAG
                java.lang.String r6 = "getData end"
                android.util.Log.d(r5, r6)
                com.sony.imaging.app.base.playback.loader.Loader$LoadedCallback r5 = r4.mOnLoadedRunnable
                if (r5 == 0) goto L0
                com.sony.imaging.app.base.playback.loader.Loader$LoadedCallback r5 = r4.mOnLoadedRunnable
                com.sony.imaging.app.base.playback.loader.Task r6 = r4.mTask
                r5.run(r1, r6)
                goto L0
            L97:
                com.sony.imaging.app.base.playback.loader.Loader r5 = com.sony.imaging.app.base.playback.loader.Loader.this     // Catch: java.lang.Throwable -> L43 java.lang.InterruptedException -> Lb1
                android.os.Handler r5 = com.sony.imaging.app.base.playback.loader.Loader.access$200(r5)     // Catch: java.lang.Throwable -> L43 java.lang.InterruptedException -> Lb1
                com.sony.imaging.app.base.playback.loader.Loader$WorkerThread$1 r7 = new com.sony.imaging.app.base.playback.loader.Loader$WorkerThread$1     // Catch: java.lang.Throwable -> L43 java.lang.InterruptedException -> Lb1
                r7.<init>()     // Catch: java.lang.Throwable -> L43 java.lang.InterruptedException -> Lb1
                r5.post(r7)     // Catch: java.lang.Throwable -> L43 java.lang.InterruptedException -> Lb1
                com.sony.imaging.app.base.playback.loader.Loader r5 = com.sony.imaging.app.base.playback.loader.Loader.this     // Catch: java.lang.Throwable -> L43 java.lang.InterruptedException -> Lb1
                java.util.ArrayList r5 = com.sony.imaging.app.base.playback.loader.Loader.access$300(r5)     // Catch: java.lang.Throwable -> L43 java.lang.InterruptedException -> Lb1
                r5.wait()     // Catch: java.lang.Throwable -> L43 java.lang.InterruptedException -> Lb1
            Lae:
                monitor-exit(r6)     // Catch: java.lang.Throwable -> L43
                goto L0
            Lb1:
                r3 = move-exception
                r3.printStackTrace()     // Catch: java.lang.Throwable -> L43
                goto Lae
            Lb6:
                r2 = move-exception
                r2.printStackTrace()
                goto L72
            */
            throw new UnsupportedOperationException("Method not decompiled: com.sony.imaging.app.base.playback.loader.Loader.WorkerThread.run():void");
        }
    }

    private void start() {
        if (this.mDecodeThread == null) {
            this.mDecodeThread = new Thread(new WorkerThread());
            this.mDecodeThread.setName(getClass().getSimpleName());
            this.mDone = false;
            this.mDecodeThread.start();
            Looper.myQueue().addIdleHandler(this.mIdleHandler);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stop() {
        synchronized (this.mQueue) {
            if (this.mQueue.isEmpty()) {
                this.mDone = true;
            }
            this.mQueue.notifyAll();
        }
        if (this.mDone) {
            synchronized (this.mIdleHandler) {
                Looper.myQueue().removeIdleHandler(this.mIdleHandler);
                this.mIdleHandler.notifyAll();
            }
            if (this.mDecodeThread != null) {
                try {
                    long start = System.currentTimeMillis();
                    this.mDecodeThread.join();
                    long end = System.currentTimeMillis();
                    Log.i(this.TAG, LogHelper.getScratchBuilder(MSG_JOIN_THREAD).append(end - start).toString());
                    this.mDecodeThread = null;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
