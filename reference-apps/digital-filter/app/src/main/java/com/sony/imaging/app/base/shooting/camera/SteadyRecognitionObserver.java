package com.sony.imaging.app.base.shooting.camera;

import android.util.Log;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class SteadyRecognitionObserver {
    private static final boolean DBG = true;
    private static final int STEADYRECOGNITION_SUPPORT_PFAPI_VERSION = 8;
    private static final String STEADY_RECOGNITION_DEFAULT = "None";
    public static final String STEADY_RECOGNITION_INVALID = "None";
    public static final String STEADY_RECOGNITION_NONE = "None";
    public static final String STEADY_RECOGNITION_TRIPOD = "Tripod";
    public static final String STEADY_RECOGNITION_WALKING = "Walking";
    private static final String TAG = "SteadyRecognitionObserver";
    public static final String TAG_STEADY_RECONGNITION = "SteadyRecognition";
    private static SteadyRecognitionObserver mObserver = null;
    private Object mLock = new Object();
    private String mSteadyRecognition = "None";
    private OnSteadyRecognitionListener mSteadyRecognitionListener;

    public static SteadyRecognitionObserver getInstance() {
        if (mObserver == null) {
            mObserver = new SteadyRecognitionObserver();
        }
        return mObserver;
    }

    protected SteadyRecognitionObserver() {
        this.mSteadyRecognitionListener = null;
        if (isSupportedByPF()) {
            this.mSteadyRecognitionListener = new OnSteadyRecognitionListener();
        }
    }

    public Object getValue(String tag) {
        String ret;
        if (this.mSteadyRecognition == null) {
            return "None";
        }
        synchronized (this.mLock) {
            ret = this.mSteadyRecognition;
        }
        return ret;
    }

    protected void onFirstListenerSet(String tag) {
    }

    protected void onAllListenerRemoved(String tag) {
    }

    public void startListener(CameraEx cameraEx) {
        if (isSupportedByPF()) {
            Log.v(TAG, "startListener()");
            cameraEx.setSteadyRecognitionListener(this.mSteadyRecognitionListener);
            requestNotify();
        }
    }

    public void stopListener(CameraEx cameraEx) {
        if (isSupportedByPF()) {
            Log.v(TAG, "stopListener()");
            cameraEx.setSteadyRecognitionListener((CameraEx.SteadyRecognitionListener) null);
        }
    }

    public boolean isSupportedByPF() {
        return 8 <= CameraSetting.getPfApiVersion();
    }

    public void requestNotify() {
        Log.v(TAG, "requestNotify()");
        CameraNotificationManager.getInstance().requestNotify(CameraNotificationManager.STEADY_RECOGNITION_CHANGED);
    }

    protected String convertPF2App(String pfValue) {
        if (pfValue == null || !isSupportedByPF() || "none".equals(pfValue)) {
            return "None";
        }
        if ("walking".equals(pfValue)) {
            return STEADY_RECOGNITION_WALKING;
        }
        if ("tripod".equals(pfValue)) {
            return STEADY_RECOGNITION_TRIPOD;
        }
        if (CinematoneController.CINEMA_TONE_INVALID.equals(pfValue)) {
            return "None";
        }
        return "None";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class OnSteadyRecognitionListener implements CameraEx.SteadyRecognitionListener {
        protected OnSteadyRecognitionListener() {
        }

        public void onChanged(String mode, CameraEx cameraEx) {
            Log.v(SteadyRecognitionObserver.TAG, "onChanged(mode=" + (mode != null ? mode : "null") + LogHelper.MSG_CLOSE_BRACKET);
            synchronized (SteadyRecognitionObserver.this.mLock) {
                SteadyRecognitionObserver.this.mSteadyRecognition = SteadyRecognitionObserver.this.convertPF2App(mode);
            }
            SteadyRecognitionObserver.this.requestNotify();
        }
    }
}
