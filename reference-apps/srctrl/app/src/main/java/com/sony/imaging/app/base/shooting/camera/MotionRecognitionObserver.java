package com.sony.imaging.app.base.shooting.camera;

import android.util.Log;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class MotionRecognitionObserver {
    private static final boolean DBG = true;
    private static final int MOTIONRECOGNITION_SUPPORTING_PFAPI_VERSION = 8;
    public static final String MOTION_RECOGNITION_BRIGHT = "Bright";
    public static final String MOTION_RECOGNITION_DARK = "Dark";
    private static final String MOTION_RECOGNITION_DEFAULT = "None";
    public static final String MOTION_RECOGNITION_INVALID = "None";
    public static final String MOTION_RECOGNITION_NONE = "None";
    public static final String MOTION_RECOGNITION_NORMAL = "Normal";
    private static final String TAG = "MotionRecognitionObserver";
    public static final String TAG_MOTION_RECONGNITION = "MotionRecognition";
    private static MotionRecognitionObserver mObserver = null;
    private Object mLock = new Object();
    private String mMotionRecognition = "None";
    private OnMotionRecognitionListener mMotionRecognitionListener;

    public static MotionRecognitionObserver getInstance() {
        if (mObserver == null) {
            mObserver = new MotionRecognitionObserver();
        }
        return mObserver;
    }

    protected MotionRecognitionObserver() {
        this.mMotionRecognitionListener = null;
        if (isSupportedByPF()) {
            this.mMotionRecognitionListener = new OnMotionRecognitionListener();
        }
    }

    public Object getValue(String tag) {
        String ret;
        if (this.mMotionRecognition == null) {
            return "None";
        }
        synchronized (this.mLock) {
            ret = this.mMotionRecognition;
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
            cameraEx.setMotionRecognitionListener(this.mMotionRecognitionListener);
            requestNotify();
        }
    }

    public void stopListener(CameraEx cameraEx) {
        if (isSupportedByPF()) {
            Log.v(TAG, "stopListener()");
            cameraEx.setMotionRecognitionListener((CameraEx.MotionRecognitionListener) null);
        }
    }

    public boolean isSupportedByPF() {
        if (8 > CameraSetting.getPfApiVersion()) {
            return false;
        }
        return DBG;
    }

    public void requestNotify() {
        Log.v(TAG, "requestNotify()");
        CameraNotificationManager.getInstance().requestNotify(CameraNotificationManager.MOTION_RECOGNITION_CHANGED);
    }

    protected String convertPF2App(String pfValue) {
        if (pfValue == null || !isSupportedByPF() || "none".equals(pfValue)) {
            return "None";
        }
        if ("normal".equals(pfValue)) {
            return MOTION_RECOGNITION_NORMAL;
        }
        if ("bright".equals(pfValue)) {
            return MOTION_RECOGNITION_BRIGHT;
        }
        if ("dark".equals(pfValue)) {
            return MOTION_RECOGNITION_DARK;
        }
        if (CinematoneController.CINEMA_TONE_INVALID.equals(pfValue)) {
            return "None";
        }
        return "None";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class OnMotionRecognitionListener implements CameraEx.MotionRecognitionListener {
        protected OnMotionRecognitionListener() {
        }

        public void onChanged(String mode, CameraEx cameraEx) {
            Log.v(MotionRecognitionObserver.TAG, "onChanged(mode=" + (mode != null ? mode : "null") + LogHelper.MSG_CLOSE_BRACKET);
            synchronized (MotionRecognitionObserver.this.mLock) {
                MotionRecognitionObserver.this.mMotionRecognition = MotionRecognitionObserver.this.convertPF2App(mode);
            }
            MotionRecognitionObserver.this.requestNotify();
        }
    }
}
