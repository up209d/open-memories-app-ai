package com.sony.imaging.app.photoretouch.common;

import android.graphics.Bitmap;
import android.os.Handler;
import com.sony.imaging.app.photoretouch.playback.control.LCEControl;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/* loaded from: classes.dex */
public class ControlImageLoadingTask {
    private static final int mCacheNum = 9;
    private Bitmap mDummyBitmap;
    private LinkedList<GalObject> mGalObjectList;
    private Handler mHandler;
    private LCEControl mLceControl;
    private final int mMaxValue;
    private ChangeParameter mSetter;
    private Runnable mUpdate;
    private Object sync = new Object();
    private List<GalObject> operationList = new ArrayList();
    private Thread mThread = new Thread(new worker(), "ControlImageLoadingTask");

    /* loaded from: classes.dex */
    public interface ChangeParameter {
        void setParameter(int i, LCEControl lCEControl, LinkedList<GalObject> linkedList);
    }

    /* loaded from: classes.dex */
    public static class GalObject {
        public Bitmap bmp;
        public boolean isDummy;
        public int position;
    }

    public ControlImageLoadingTask(LCEControl lce, LinkedList<GalObject> list, ChangeParameter setter, Runnable update, Bitmap dummy) {
        this.mThread.setPriority(1);
        this.mHandler = new Handler();
        this.mUpdate = update;
        this.mLceControl = lce;
        this.mDummyBitmap = dummy;
        this.mGalObjectList = list;
        this.mSetter = setter;
        this.mMaxValue = list.size();
    }

    public void start() {
        this.mThread.start();
    }

    public void stop() {
        this.mThread.interrupt();
        try {
            this.mThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.mHandler.removeCallbacks(this.mUpdate);
        this.operationList.clear();
    }

    /* loaded from: classes.dex */
    private class worker implements Runnable {
        private boolean running;

        private worker() {
        }

        @Override // java.lang.Runnable
        public void run() {
            int count;
            this.running = true;
            while (!Thread.interrupted() && this.running) {
                try {
                    synchronized (ControlImageLoadingTask.this.operationList) {
                        count = ControlImageLoadingTask.this.operationList.size();
                    }
                    if (count == 0) {
                        synchronized (ControlImageLoadingTask.this.sync) {
                            ControlImageLoadingTask.this.sync.wait();
                        }
                    }
                    synchronized (ControlImageLoadingTask.this.operationList) {
                        int count2 = ControlImageLoadingTask.this.operationList.size();
                        if (count2 != 0) {
                            GalObject gObj1 = (GalObject) ControlImageLoadingTask.this.operationList.remove(0);
                            synchronized (gObj1) {
                                if (gObj1.isDummy) {
                                    ControlImageLoadingTask.this.mSetter.setParameter(gObj1.position, ControlImageLoadingTask.this.mLceControl, ControlImageLoadingTask.this.mGalObjectList);
                                    gObj1.bmp = ControlImageLoadingTask.this.mLceControl.getRGBImage(ControlImageLoadingTask.this.mLceControl.getScaledImage());
                                    gObj1.isDummy = false;
                                }
                            }
                            ControlImageLoadingTask.this.mHandler.post(ControlImageLoadingTask.this.mUpdate);
                        }
                    }
                } catch (InterruptedException e) {
                    this.running = false;
                }
            }
        }
    }

    public void kick(int start, int end) {
        int additionalNumber = 9 - ((end - start) + 1);
        int addedStart = start;
        int addedEnd = end;
        while (additionalNumber > 0 && (addedStart != 0 || addedEnd != this.mMaxValue)) {
            if (additionalNumber > 0 && addedStart > 0) {
                addedStart--;
                additionalNumber--;
            }
            if (additionalNumber > 0 && addedEnd < this.mMaxValue) {
                addedEnd++;
                additionalNumber--;
            }
        }
        synchronized (this.operationList) {
            for (int i = 0; i < this.mMaxValue; i++) {
                GalObject gObj1 = this.mGalObjectList.get(i);
                if (start <= i && i <= end) {
                    if (!this.operationList.contains(gObj1)) {
                        this.operationList.add(0, gObj1);
                    }
                } else if (addedStart <= i && i <= addedEnd) {
                    if (!this.operationList.contains(gObj1)) {
                        this.operationList.add(gObj1);
                    }
                } else {
                    this.operationList.remove(gObj1);
                    synchronized (gObj1) {
                        if (!gObj1.isDummy) {
                            gObj1.bmp.recycle();
                            gObj1.bmp = this.mDummyBitmap;
                            gObj1.isDummy = true;
                        }
                    }
                }
            }
        }
        synchronized (this.sync) {
            this.sync.notify();
        }
    }
}
