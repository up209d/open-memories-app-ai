package com.sony.imaging.app.lightgraffiti.util;

import android.util.Log;
import com.sony.imaging.app.lightgraffiti.shooting.CompositProcess;
import com.sony.imaging.app.lightgraffiti.shooting.LGImagingAdapterImpl;
import com.sony.imaging.app.lightgraffiti.shooting.LGStateHolder;
import com.sony.scalar.hardware.CameraSequence;
import com.sony.scalar.hardware.DSP;

/* loaded from: classes.dex */
public class LGPreviewEffect {
    private static final String TAG = LGPreviewEffect.class.getSimpleName();
    private static CameraSequence sCameraSeq = null;
    private static LGPreviewEffect instance = null;
    private static Boolean isExec = false;
    private Object mCameraSeqLock = new Object();
    private LGSAMixFilter mSaw = null;
    private LGStateHolder mLGStateHolder = null;

    private LGPreviewEffect() {
        Log.d(TAG, "LGPreviewEffect()");
    }

    public static LGPreviewEffect getInstance() {
        Log.d(TAG, "getInstance()");
        if (instance == null) {
            instance = new LGPreviewEffect();
        }
        return instance;
    }

    public void startPreviewEffect() {
        synchronized (this.mCameraSeqLock) {
            Log.d(TAG, "startPreviewEffect()");
            if (!isExec.booleanValue()) {
                if (sCameraSeq == null) {
                    if (CompositProcess.getAdapter() != null) {
                        sCameraSeq = ((LGImagingAdapterImpl) CompositProcess.getAdapter()).getSequence();
                    } else {
                        Log.w(TAG, "warning: Adapter of CompositProcess is not created yet. It will be created again.");
                        return;
                    }
                }
                if (this.mSaw == null) {
                    this.mSaw = new LGSAMixFilter();
                }
                if (this.mLGStateHolder == null) {
                    this.mLGStateHolder = LGStateHolder.getInstance();
                }
                this.mSaw.setParamWithDeviceBuffer(this.mLGStateHolder.getDb_mini(), null, LGUtility.getInstance().getScaledOptimazeImageSizeX(), LGUtility.getInstance().getScaledOptimazeImageSizeY(), 4);
                sCameraSeq.setPreviewPlugin(this.mSaw.getDSP());
                CameraSequence.Options opts = new CameraSequence.Options();
                opts.setOption("PREVIEW_FRAME_RATE", 30000);
                opts.setOption("PREVIEW_FRAME_WIDTH", LGUtility.getInstance().getScaledOptimazeImageSizeX());
                opts.setOption("PREVIEW_FRAME_HEIGHT", 0);
                opts.setOption("PREVIEW_PLUGIN_NOTIFY_MASK", 0);
                opts.setOption("PREVIEW_DEBUG_NOTIFY_ENABLED", false);
                opts.setOption("PREVIEW_PLUGIN_OUTPUT_ENABLED", true);
                Log.d(TAG, "startPreviewSequence");
                sCameraSeq.startPreviewSequence(opts);
                isExec = true;
            } else {
                Log.d(TAG, "startPreviewEffect() didn't exec.");
            }
        }
    }

    public void stopPreviewEffect() {
        Log.d(TAG, "stopPreviewEffect()");
        release();
        isExec = false;
    }

    protected void release() {
        synchronized (this.mCameraSeqLock) {
            Log.d(TAG, "release()");
            if (sCameraSeq != null) {
                sCameraSeq.stopPreviewSequence();
                sCameraSeq.setPreviewPlugin((DSP) null);
                sCameraSeq = null;
            }
            if (this.mSaw != null) {
                this.mSaw.releaseResources();
                this.mSaw = null;
            }
            if (this.mLGStateHolder != null) {
                this.mLGStateHolder = null;
            }
        }
    }
}
