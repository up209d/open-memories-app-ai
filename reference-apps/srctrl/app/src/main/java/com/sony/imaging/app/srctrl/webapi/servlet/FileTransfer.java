package com.sony.imaging.app.srctrl.webapi.servlet;

import android.util.Log;
import com.sony.mexi.orb.server.http.StatusCode;
import com.sony.mexi.orb.service.http.GenericHttpRequestHandler;
import com.sony.mexi.orb.service.http.HttpRequest;
import com.sony.mexi.orb.service.http.HttpResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
public abstract class FileTransfer extends GenericHttpRequestHandler {
    private static final long serialVersionUID = 1;
    private static final String TAG = FileTransfer.class.getSimpleName();
    private static int WAIT_TIME = 30;
    private static int DEFAULT_COUNT = 0;
    private static int INITIAL_COUNT = -1;
    private boolean mTransferCancel = false;
    private volatile int mTaskCount = INITIAL_COUNT;
    private Object mResLook = new Object();
    private HttpResponse _res = null;

    abstract boolean enableTransfer();

    abstract ExecutorService getExecute();

    abstract InputStream getInputStream(HttpRequest httpRequest, HttpResponse httpResponse);

    public void endCurrentResponce() {
        synchronized (this.mResLook) {
            Log.i(TAG, "endCurrentResponce start :" + this._res);
            if (this._res != null) {
                this._res.end();
                this._res = null;
            }
            Log.i(TAG, "endCurrentResponce");
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ExecutorService initial() {
        transferCancel(false);
        this.mTaskCount = DEFAULT_COUNT;
        return Executors.newSingleThreadExecutor();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void terminal(ExecutorService exe) {
        if (exe != null) {
            transferCancel(true);
            exe.shutdown();
            endCurrentResponce();
            int cnt = 0;
            try {
                Log.v(TAG, "awaitTermination start");
                while (true) {
                    if (cnt >= 12) {
                        break;
                    }
                    if (exe.awaitTermination(5L, TimeUnit.SECONDS)) {
                        Log.v(TAG, "awaitTermination success cnt = :" + cnt);
                        break;
                    }
                    cnt++;
                }
                Log.v(TAG, "awaitTermination end");
                if (cnt >= 12) {
                    Log.e(TAG, "awaitTermination timeout (1min)");
                    exe.shutdownNow();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            transferCancel(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void transferCancel(boolean isCancel) {
        Log.v(TAG, "transferCancel:" + isCancel);
        this.mTransferCancel = isCancel;
    }

    public boolean isTransfer() {
        return this.mTaskCount > 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void decrementTaskCount() {
        this.mTaskCount--;
        Log.v(TAG, "decrementTaskCount : TaskCount = " + this.mTaskCount);
    }

    private void incrementTaskCount() {
        this.mTaskCount++;
        Log.v(TAG, "incrementTaskCount : TaskCount = " + this.mTaskCount);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.mexi.orb.service.http.GenericHttpRequestHandler
    public void handleGet(HttpRequest req, HttpResponse res) throws IOException {
        Log.v(TAG, "handleGet : TaskCount = " + this.mTaskCount);
        try {
            if (!this.mTransferCancel) {
                ExecutorService exe = getExecute();
                FileTransferRunnable runnable = new FileTransferRunnable(req, res, this);
                exe.execute(runnable);
                incrementTaskCount();
            } else {
                Log.v(TAG, "TransferCancel");
                res.sendStatusAsResponse(StatusCode.INTERNAL_SERVER_ERROR);
                res.end();
            }
        } catch (Exception e) {
            Log.e(TAG, "FileTransferRunnable failed : " + e.getMessage());
            res.sendStatusAsResponse(StatusCode.INTERNAL_SERVER_ERROR);
            res.end();
        }
    }

    /* loaded from: classes.dex */
    private class FileTransferRunnable implements Runnable {
        private final String TAG = FileTransferRunnable.class.getSimpleName();
        private FileTransfer fileTranfer;
        private HttpRequest req;
        private HttpResponse res;

        public FileTransferRunnable(HttpRequest req, HttpResponse res, FileTransfer fileTranfer) {
            this.req = null;
            this.res = null;
            this.fileTranfer = null;
            this.req = req;
            this.res = res;
            this.fileTranfer = fileTranfer;
        }

        /* JADX WARN: Code restructure failed: missing block: B:37:0x0254, code lost:            r13.append("write end : total=").append(r16);        android.util.Log.v(r24.TAG, r13.toString());     */
        /* JADX WARN: Removed duplicated region for block: B:12:0x006b A[EXC_TOP_SPLITTER, SYNTHETIC] */
        @Override // java.lang.Runnable
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public void run() {
            /*
                Method dump skipped, instructions count: 752
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: com.sony.imaging.app.srctrl.webapi.servlet.FileTransfer.FileTransferRunnable.run():void");
        }
    }
}
