package com.sony.imaging.app.util;

import android.os.AsyncTask;
import android.util.Log;

/* loaded from: classes.dex */
public abstract class CancelableAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    public static boolean DEBUG = true;
    private static final String MSG_ALREADY_CANCELLED = "already cancelled";
    private static final String MSG_ALREADY_RAN = "already ran";
    private static final String MSG_NOTIFYING = "Processing done to notify ";
    private static final String MSG_ON_POST_EXECUTED_AFTER_CANCEL = "onPostExecuted called after cancel, issue onCancel instead";
    private static final String MSG_WAITING = "start waiting ";
    private static final String MSG_WAIT_CANCEL_INTERRUPTED = "wait is interrupted";
    private static final String TAG = "CancelableAsyncTask";
    private Object lock;
    private volatile boolean mBackgroundProcessingDone;
    private boolean mCallOnCancelIfCancelledAfterRan;
    private boolean mIssueCancelled;
    private Thread mWorker;

    protected abstract Result doInBackgroundAlternate(Params... paramsArr);

    public CancelableAsyncTask() {
        this.lock = new Object();
        this.mCallOnCancelIfCancelledAfterRan = true;
        this.mBackgroundProcessingDone = false;
        this.mIssueCancelled = false;
    }

    public CancelableAsyncTask(boolean callOnCancelIfCancelledAfterRan) {
        this.lock = new Object();
        this.mCallOnCancelIfCancelledAfterRan = true;
        this.mBackgroundProcessingDone = false;
        this.mIssueCancelled = false;
        this.mCallOnCancelIfCancelledAfterRan = callOnCancelIfCancelledAfterRan;
    }

    public boolean isBackgroundProcessingDone() {
        return this.mBackgroundProcessingDone;
    }

    public boolean cancelSync(boolean mayInterruptIfRunning, int timeout) {
        if (this.mWorker == Thread.currentThread()) {
            throw new IllegalThreadStateException();
        }
        this.mIssueCancelled = true;
        boolean cancelAccepted = cancel(mayInterruptIfRunning);
        if (!cancelAccepted) {
            if (!isCancelled()) {
                Log.i(TAG, MSG_ALREADY_RAN);
            } else {
                Log.i(TAG, MSG_ALREADY_CANCELLED);
            }
        }
        synchronized (this.lock) {
            if (!this.mBackgroundProcessingDone) {
                try {
                    if (DEBUG) {
                        Log.d(TAG, MSG_WAITING);
                    }
                    this.lock.wait(timeout);
                } catch (InterruptedException e) {
                    Log.w(TAG, MSG_WAIT_CANCEL_INTERRUPTED);
                    e.printStackTrace();
                }
            }
        }
        return cancelAccepted;
    }

    public boolean cancelAsync(boolean mayInterruptIfRunning) {
        this.mIssueCancelled = true;
        return cancel(mayInterruptIfRunning);
    }

    @Override // android.os.AsyncTask
    @Deprecated
    protected final Result doInBackground(Params... params) {
        this.mWorker = Thread.currentThread();
        Result result = doInBackgroundAlternate(params);
        synchronized (this.lock) {
            if (DEBUG) {
                Log.d(TAG, MSG_NOTIFYING);
            }
            this.mBackgroundProcessingDone = true;
            this.lock.notifyAll();
        }
        this.mWorker = null;
        return result;
    }

    protected void onPostExecuteAlternate(Result result) {
    }

    @Override // android.os.AsyncTask
    protected final void onPostExecute(Result result) {
        if (this.mCallOnCancelIfCancelledAfterRan && this.mIssueCancelled) {
            Log.i(TAG, MSG_ON_POST_EXECUTED_AFTER_CANCEL);
            onCancelled();
        } else {
            onPostExecuteAlternate(result);
        }
    }
}
