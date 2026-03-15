package com.sony.imaging.app.srctrl.webapi.servlet;

import android.content.res.AssetManager;
import android.os.SystemClock;
import android.util.Log;
import com.sony.imaging.app.srctrl.liveview.FrameInfoLoader;
import com.sony.imaging.app.srctrl.liveview.JpegLoader;
import com.sony.imaging.app.srctrl.liveview.LiveviewLoader;
import com.sony.imaging.app.srctrl.util.SRCtrlConstants;
import com.sony.imaging.app.srctrl.util.StateController;
import com.sony.mexi.orb.service.http.GenericHttpRequestHandler;
import com.sony.mexi.orb.service.http.HttpRequest;
import com.sony.mexi.orb.service.http.HttpResponse;
import java.io.IOException;
import java.io.OutputStream;

/* loaded from: classes.dex */
public class LiveviewChunkTransfer extends GenericHttpRequestHandler {
    private static final long CANCEL_TIMEOUT = 300;
    private static final boolean DBG = false;
    private static final long IDLE_SLEEP_TIMEOUT = 30;
    private static final long IDLE_SLEEP_TIMEOUT_FOR_LOOP = 35;
    private static final long IDLE_SLEEP_TIMEOUT_LONG = 90;
    private static final long IDLE_SLEEP_TIMEOUT_LONG_LONG = 150;
    private static final long SEND_BLOCKING_TIMEOUT = 10000;
    private static final long SLEEP_TIMEOUT_FOR_FAILED_GET_JPEGDATA = 15;
    private static final String TAG = "LiveviewChunkTransfer";
    private static final long serialVersionUID = 1;
    private LiveviewChunkTransfer sendingMutex;
    private static long lastSendTime = 0;
    private static Object _lock = new Object();
    private static OutputStream mStreamRef = null;
    private static Object _lockStreamRef = new Object();
    private volatile long latestSendingTime = -1;
    private volatile boolean isCanceled = false;

    public LiveviewChunkTransfer() {
        this.sendingMutex = null;
        this.sendingMutex = this;
    }

    public static void setAssertManager(AssetManager am) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.mexi.orb.service.http.GenericHttpRequestHandler
    public void handleGet(HttpRequest req, HttpResponse res) throws IOException {
        long threadId = Thread.currentThread().getId();
        Log.v(TAG, "Accepted HTTP GET for Liveview. Thread ID : " + threadId + " Frame on:" + FrameInfoLoader.isEnabled());
        OutputStream stream_ = res.getOutputStream();
        synchronized (_lockStreamRef) {
            if (mStreamRef != null) {
                Log.w(TAG, "mStreamRef in use.  force close it, since it should be freezing at transition. This will cause rise of exception of existing stream. Thread ID:" + threadId);
                mStreamRef.close();
            }
            mStreamRef = stream_;
        }
        this.isCanceled = true;
        synchronized (_lock) {
            try {
                this.isCanceled = false;
                LiveviewLoader.setLiveviewChunkTransferInstance(this.sendingMutex);
                LiveviewLoader.resumeGetFrameInfoThread();
                FrameInfoLoader.FrameInfoData frameInfoData = null;
                Log.v(TAG, "Write Loop Start. Thread ID : " + threadId);
                while (!this.isCanceled) {
                    try {
                        sleepByState();
                        if (FrameInfoLoader.isEnabled()) {
                            LiveviewLoader.resumeGetFrameInfoThread();
                            try {
                                StateController state_controller = StateController.getInstance();
                                StateController.AppCondition ac = state_controller.getAppCondition();
                                if (StateController.AppCondition.SHOOTING_REMOTE != ac && StateController.AppCondition.SHOOTING_LOCAL != ac) {
                                    frameInfoData = LiveviewLoader.getFrameInfoData();
                                }
                            } catch (IllegalStateException e) {
                                Log.e(TAG, "get FrameInfo Error. IllegalStateException. :" + e.getMessage());
                            }
                        } else {
                            frameInfoData = null;
                            LiveviewLoader.pauseFrameInfoThread();
                        }
                        try {
                            JpegLoader.LiveviewData liveviewData = LiveviewLoader.getLiveviewData();
                            while (liveviewData == null) {
                                try {
                                    Thread.sleep(SLEEP_TIMEOUT_FOR_FAILED_GET_JPEGDATA);
                                    if (this.isCanceled) {
                                        throw new IllegalStateException("Operation was canceled.");
                                    }
                                    liveviewData = LiveviewLoader.getLiveviewData();
                                } catch (InterruptedException e2) {
                                    Log.e(TAG, "sleep() InterruptedException.");
                                    throw new IllegalStateException("InterruptedException");
                                }
                            }
                            if (stream_ != null) {
                                try {
                                    if (FrameInfoLoader.isEnabled() && frameInfoData != null) {
                                        stream_.write(frameInfoData.headerData, 0, frameInfoData.headerDataSize);
                                        if (frameInfoData.frameDataSize != 0) {
                                            stream_.write(frameInfoData.frameData, 0, frameInfoData.frameDataSize);
                                        }
                                    }
                                    stream_.write(liveviewData.headerData, 0, liveviewData.headerDataSize);
                                    stream_.write(liveviewData.jpegData, 0, liveviewData.jpegDataAndPaddingSize);
                                } catch (IOException e3) {
                                    Log.w(TAG, "IOException from Stream Access. Thread ID:" + threadId + " : " + e3.getMessage());
                                    e3.printStackTrace();
                                    res.end();
                                    LiveviewLoader.setLiveviewChunkTransferInstance(null);
                                    LiveviewLoader.pauseFrameInfoThread();
                                    synchronized (_lockStreamRef) {
                                        if (mStreamRef == stream_) {
                                            Log.v(TAG, "mStreamInSyncLock=null. Thread ID: " + threadId);
                                            mStreamRef = null;
                                        }
                                        Log.v(TAG, "sender finish Thread ID : " + threadId);
                                        return;
                                    }
                                } catch (IllegalStateException e4) {
                                    Log.e(TAG, "IllegalStateException from Stream Access. Thread ID:" + threadId + " : " + e4.getMessage());
                                }
                            }
                        } catch (IllegalStateException e5) {
                            Log.e(TAG, "get Liveview Error. IllegalStateException. :" + e5.getMessage());
                        }
                    } catch (InterruptedException e6) {
                        Log.v(TAG, "InterruptedException in sleep");
                    }
                }
                res.end();
                LiveviewLoader.setLiveviewChunkTransferInstance(null);
                LiveviewLoader.pauseFrameInfoThread();
                synchronized (_lockStreamRef) {
                    if (mStreamRef == stream_) {
                        Log.v(TAG, "mStreamInSyncLock=null. Thread ID: " + threadId);
                        mStreamRef = null;
                    }
                }
                Log.v(TAG, "sender finish Thread ID : " + threadId);
            } catch (Throwable th) {
                res.end();
                LiveviewLoader.setLiveviewChunkTransferInstance(null);
                LiveviewLoader.pauseFrameInfoThread();
                synchronized (_lockStreamRef) {
                    if (mStreamRef == stream_) {
                        Log.v(TAG, "mStreamInSyncLock=null. Thread ID: " + threadId);
                        mStreamRef = null;
                    }
                    Log.v(TAG, "sender finish Thread ID : " + threadId);
                    throw th;
                }
            }
        }
    }

    public void notifyGetScalarInfraIsKilled() {
        Log.e(TAG, "notifyGetScalarInfraIsKilled!!!!");
        synchronized (_lockStreamRef) {
            if (mStreamRef != null) {
                Log.w(TAG, "notifyGetScalarInfraIsKilled  mStreamRef exist.  force close stream.");
                try {
                    mStreamRef.close();
                } catch (IOException e) {
                    Log.e(TAG, "notifyGetScalarInfraIsKilled  mStreamRef.close() failed");
                    e.printStackTrace();
                }
            }
        }
        this.isCanceled = true;
    }

    public String getRootPath() {
        return SRCtrlConstants.SERVLET_ROOT_PATH_LIVEVIEW;
    }

    private void sleepByState() throws InterruptedException {
        StateController state_controller = StateController.getInstance();
        StateController.AppCondition ac = state_controller.getAppCondition();
        if (state_controller.isDuringCameraSetupRoutine()) {
            Thread.sleep(IDLE_SLEEP_TIMEOUT_LONG_LONG);
        } else if (StateController.AppCondition.SHOOTING_EE != ac && StateController.AppCondition.SHOOTING_REMOTE_TOUCHAF != ac && StateController.AppCondition.SHOOTING_REMOTE_TOUCHAFASSIST != ac && StateController.AppCondition.SHOOTING_MOVIE_EE != ac && StateController.AppCondition.SHOOTING_MOVIE_REC != ac && StateController.AppCondition.SHOOTING_START_FOCUSING != ac && StateController.AppCondition.SHOOTING_START_FOCUSING_REMOTE != ac && StateController.AppCondition.SHOOTING_FOCUSING != ac && StateController.AppCondition.SHOOTING_FOCUSING_REMOTE != ac) {
            Thread.sleep(IDLE_SLEEP_TIMEOUT_LONG);
        } else {
            long now = SystemClock.uptimeMillis();
            long diff = now - lastSendTime;
            if (diff < IDLE_SLEEP_TIMEOUT_FOR_LOOP) {
                Thread.sleep(IDLE_SLEEP_TIMEOUT_FOR_LOOP - diff);
            }
        }
        lastSendTime = SystemClock.uptimeMillis();
    }
}
