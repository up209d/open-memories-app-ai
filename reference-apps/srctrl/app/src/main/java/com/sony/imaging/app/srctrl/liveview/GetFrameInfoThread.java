package com.sony.imaging.app.srctrl.liveview;

import android.os.SystemClock;
import android.util.Log;
import com.sony.imaging.app.srctrl.liveview.FrameInfoLoader;
import com.sony.scalar.hardware.CameraSequence;

/* loaded from: classes.dex */
public class GetFrameInfoThread {
    private static final long GET_FRAMEINFO_INTERVAL = 10;
    private static final int INITIAL_INT_VALUE = -1;
    private FrameInfoLoader.FrameInfoData frameInfoDataCopy;
    private FrameInfoLoader.FrameInfoData frameInfoDataSnapShot;
    private static final String TAG = GetFrameInfoThread.class.getName();
    private static FrameInfoLoader frameInfoloader = null;
    private static CameraSequence cameraSequence = null;
    private Thread mThread = null;
    private Object copyLock = new Object();
    private Object resumeLock = new Object();
    private long lastGetFrameTime = 0;
    private volatile boolean isStoped = false;
    private volatile boolean isPaused = true;

    public GetFrameInfoThread() {
        this.frameInfoDataSnapShot = null;
        this.frameInfoDataCopy = null;
        this.frameInfoDataSnapShot = new FrameInfoLoader.FrameInfoData();
        this.frameInfoDataSnapShot.headerDataSize = -1;
        this.frameInfoDataSnapShot.headerData = new byte[LiveviewCommon.HEADER_DATA_SIZE_MAX];
        this.frameInfoDataSnapShot.frameDataSize = -1;
        this.frameInfoDataSnapShot.frameData = new byte[624];
        this.frameInfoDataCopy = new FrameInfoLoader.FrameInfoData();
        this.frameInfoDataCopy.headerDataSize = -1;
        this.frameInfoDataCopy.headerData = new byte[LiveviewCommon.HEADER_DATA_SIZE_MAX];
        this.frameInfoDataCopy.frameDataSize = -1;
        this.frameInfoDataCopy.frameData = new byte[624];
    }

    public FrameInfoLoader.FrameInfoData getFrameInfoDataCopy() {
        synchronized (this.copyLock) {
            if (-1 == this.frameInfoDataSnapShot.headerDataSize) {
                return null;
            }
            this.frameInfoDataCopy.headerDataSize = this.frameInfoDataSnapShot.headerDataSize;
            System.arraycopy(this.frameInfoDataSnapShot.headerData, 0, this.frameInfoDataCopy.headerData, 0, this.frameInfoDataSnapShot.headerDataSize);
            this.frameInfoDataCopy.frameDataSize = this.frameInfoDataSnapShot.frameDataSize;
            System.arraycopy(this.frameInfoDataSnapShot.frameData, 0, this.frameInfoDataCopy.frameData, 0, this.frameInfoDataSnapShot.frameDataSize);
            LiveviewCommon.setNetworkByte(this.frameInfoDataCopy.headerData, 2, LiveviewCommon.getCurrentSequenceNumber(), 2);
            int timeStamp = LiveviewCommon.getTimeStamp();
            LiveviewCommon.setNetworkByte(this.frameInfoDataCopy.headerData, 4, timeStamp, 4);
            return this.frameInfoDataCopy;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setFrameInfoDataSnapShot(FrameInfoLoader.FrameInfoData src) {
        if (src != null) {
            synchronized (this.copyLock) {
                this.frameInfoDataSnapShot.headerDataSize = src.headerDataSize;
                System.arraycopy(src.headerData, 0, this.frameInfoDataSnapShot.headerData, 0, src.headerDataSize);
                this.frameInfoDataSnapShot.frameDataSize = src.frameDataSize;
                System.arraycopy(src.frameData, 0, this.frameInfoDataSnapShot.frameData, 0, src.frameDataSize);
            }
        }
    }

    public void start(FrameInfoLoader loader, CameraSequence camSeq) {
        Log.v(TAG, "GetFrameInfoThread#start()");
        if (this.mThread == null) {
            this.isStoped = false;
            this.isPaused = true;
            frameInfoloader = loader;
            cameraSequence = camSeq;
            this.mThread = new Thread(new Runnable() { // from class: com.sony.imaging.app.srctrl.liveview.GetFrameInfoThread.1
                @Override // java.lang.Runnable
                public void run() {
                    Log.v(GetFrameInfoThread.TAG, "GetFrameInfoThread#run()");
                    while (!GetFrameInfoThread.this.isStoped) {
                        synchronized (GetFrameInfoThread.this.resumeLock) {
                            if (GetFrameInfoThread.this.isPaused) {
                                try {
                                    GetFrameInfoThread.this.resumeLock.wait();
                                } catch (InterruptedException e) {
                                    Log.v(GetFrameInfoThread.TAG, "InterruptedException in startGetFrameInfoThread");
                                    return;
                                }
                            }
                        }
                        try {
                            FrameInfoLoader.FrameInfoData frameInfoData = GetFrameInfoThread.frameInfoloader.getFrameInfoData(GetFrameInfoThread.cameraSequence);
                            GetFrameInfoThread.this.setFrameInfoDataSnapShot(frameInfoData);
                            try {
                                GetFrameInfoThread.this.sleepByState();
                            } catch (InterruptedException e2) {
                                Log.v(GetFrameInfoThread.TAG, "InterruptedException in startGetFrameInfoThread sleep");
                                return;
                            }
                        } catch (IllegalStateException e3) {
                            Log.v(GetFrameInfoThread.TAG, "IllegalStateException at getFrameInfoData()");
                            return;
                        }
                    }
                }
            }, "FrameInfo");
            this.mThread.start();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sleepByState() throws InterruptedException {
        long now = SystemClock.uptimeMillis();
        if (now - this.lastGetFrameTime < 30) {
            Thread.sleep(30 - (now - this.lastGetFrameTime));
        }
        this.lastGetFrameTime = SystemClock.uptimeMillis();
    }

    public void stop() {
        Log.v(TAG, "GetFrameInfoThread#stop()");
        this.isStoped = true;
        synchronized (this.copyLock) {
            this.frameInfoDataSnapShot.headerDataSize = -1;
            this.frameInfoDataSnapShot.frameDataSize = -1;
        }
        if (this.mThread != null) {
            try {
                if (this.isPaused) {
                    Log.v(TAG, "mThread.interrupt ");
                    this.mThread.interrupt();
                }
                this.mThread.join();
                Log.v(TAG, "Thread joined");
            } catch (InterruptedException e) {
                Log.v(TAG, "InterruptedException in startGetFrameInfoThread");
            }
        }
        this.mThread = null;
    }

    public void pause() {
        if (!this.isPaused) {
            this.isPaused = true;
            synchronized (this.copyLock) {
                this.frameInfoDataSnapShot.headerDataSize = -1;
                this.frameInfoDataSnapShot.frameDataSize = -1;
            }
        }
    }

    public void resume() {
        if (this.isPaused) {
            if (this.mThread != null && this.mThread.isAlive()) {
                synchronized (this.resumeLock) {
                    this.isPaused = false;
                    Log.v(TAG, "resumeLock.notifyAll ");
                    this.resumeLock.notifyAll();
                }
                return;
            }
            Log.v(TAG, "Thread not ready!");
        }
    }
}
