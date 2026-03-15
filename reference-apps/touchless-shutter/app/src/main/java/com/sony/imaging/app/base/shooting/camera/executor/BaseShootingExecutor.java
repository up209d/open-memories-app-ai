package com.sony.imaging.app.base.shooting.camera.executor;

import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.fw.State;
import com.sony.scalar.hardware.CameraEx;
import java.io.IOException;

/* loaded from: classes.dex */
public abstract class BaseShootingExecutor {
    protected static final String LOG_AUTO_FOCUS = "autoFocus";
    protected static final String LOG_CAMERA = "CAMERA :";
    protected static final String LOG_CANCEL_AUTO_FOCUS = "cancelAutoFocus";
    protected static final String LOG_CAPTURE_CUSTOM_WHITE_BALANCE = "captureCustomWhiteBalance";
    protected static final String LOG_EXECUTOR_NAME = " Executor: ";
    protected static final String LOG_LOCK = "lock";
    protected static final String LOG_ON_AUTO_FOCUS = "onAutoFocus";
    protected static final String LOG_ON_AUTO_FOCUS_DONE = "onAutoFocusDone";
    protected static final String LOG_ON_AUTO_FOCUS_START = "onAutoFocusStart";
    protected static final String LOG_ON_CUSTOM_WHITE_BALANCE_CAPTURE = "onCustomWhiteBalanceCapture";
    protected static final String LOG_ON_PREVIEW_START = "onPreviewStart";
    protected static final String LOG_ON_RECORDING_MEDIA_CHANGED = "onRecordingMediaChanged";
    protected static final String LOG_ON_RELEASED = "onReleased";
    protected static final String LOG_PREPARE = "prepare";
    protected static final String LOG_RUNNABLE = "Runnable";
    protected static final String LOG_SEND_KEY = "send key ";
    protected static final String LOG_SET_RECORDING_MEDIA = "setRecordingMedia";
    protected static final String LOG_STACK_TRACE = "StackTrace Log";
    protected static final String LOG_START_PREVIEW = "startPreview";
    protected static final String LOG_STATUS = ": status ";
    protected static final String LOG_SWEEP_QUEUE = "sweepQueue";
    protected static final String LOG_TERMINATE = "terminate";
    protected static final String LOG_UI = "UI :";
    protected static final String LOG_UNOVERRIDDEN_METHOD_LOG = "un-overridden method log";
    public static final int REAL_SHUTTER = 2;
    public static final int REMOTECONTROL_NORMAL = 0;
    public static final int REMOTECONTROL_TEMPORARY = 1;
    public static final int REMOTE_SHUTTER = 1;
    private static final String TAG = "BaseShootingExecutor";
    public static final int UNKNOWN_SHUTTER = -1;
    protected static CameraSetting sCameraSetting;
    protected static CameraNotificationManager sNotify;
    protected static String sCurrentMedia = null;
    protected static Camera sCamera = null;
    protected static CameraEx sCameraEx = null;
    protected static final StringBuilder STRBUILD = new StringBuilder();
    protected static StringBuilderThreadLocal sStrBuilderPool = new StringBuilderThreadLocal();
    protected static MyBaseAutoFocusCb sMyAutoFocusCb = new MyBaseAutoFocusCb();
    protected static MyBaseAutoFocusStartCb sMyAutoFocusStartCb = new MyBaseAutoFocusStartCb();
    protected static MyBaseAutoFocusDoneCb sMyAutoFocusDoneCb = new MyBaseAutoFocusDoneCb();
    protected static MyBaseCustomWhiteBalanceCallback sMyCustomWhiteBalanceCallback = new MyBaseCustomWhiteBalanceCallback();
    protected static MyBasePreviewStartCb sMyPreviewStartCb = new MyBasePreviewStartCb();
    protected static Handler sHandlerToMain = null;
    protected static Handler sHandlerFromMain = null;
    protected static Camera.AutoFocusCallback sAutoFocusCb = null;
    protected static CameraEx.RecordingMediaChangeCallback sRecordingMediaChangeCallback = null;
    protected static CameraEx.CustomWhiteBalanceCallback sCustomWhiteBalanceCallback = null;
    protected static CameraEx.PreviewStartListener sPreviewCb = null;
    protected static boolean isFocusLocked = false;
    protected static boolean isBulbOpened = false;
    protected static boolean sIsAutoFocusRequested = false;
    protected static String sAutoFocusBehavior = null;
    protected static String sRunningAutoFocusBehavior = null;
    protected static Object sAFBehaviorLock = new Object();
    protected static Object mediaLockObject = new Object();
    protected State state = null;
    protected ReleasedListener mReleasedListener = null;
    protected boolean mTerminating = false;
    protected boolean mCanTerminate = true;
    protected IAdapter mAdapter = null;
    protected AutoFocusRunnable mAutoFocusRunnable = getAutoFocusRunnable();
    protected CancelAutoFocusRunnable mCancelAutoFocusRunnable = getCancelAutoFocusRunnable();
    protected CaptureCustomWhiteBalanceRunnable mCaptureCustomWhiteBalanceRunnable = new CaptureCustomWhiteBalanceRunnable();

    /* loaded from: classes.dex */
    public interface ReleasedListener {
        void onReleased();
    }

    public abstract void inquireKey(int i);

    protected abstract void myRelease();

    public abstract void setAdapter(IAdapter iAdapter);

    public abstract void setRecordingMedia(String str, CameraEx.RecordingMediaChangeCallback recordingMediaChangeCallback);

    public abstract void stopPreview();

    /* loaded from: classes.dex */
    public static class StringBuilderThreadLocal extends ThreadLocal<StringBuilder> {
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.lang.ThreadLocal
        public StringBuilder initialValue() {
            return new StringBuilder();
        }

        @Override // java.lang.ThreadLocal
        public StringBuilder get() {
            return (StringBuilder) super.get();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static StringBuilder getStringBuilder() {
        return sStrBuilderPool.get();
    }

    public Camera getCamera() {
        return sCamera;
    }

    public CameraEx getCameraEx() {
        return sCameraEx;
    }

    public void release(ReleasedListener listener) {
        this.mReleasedListener = listener;
        this.mTerminating = true;
        myRelease();
    }

    protected void enableTermination(boolean canTerminate) {
        this.mCanTerminate = canTerminate;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean tryRelease() {
        boolean ret = false;
        if (this.mTerminating && this.mCanTerminate) {
            Log.i(TAG, LOG_ON_RELEASED);
            ret = true;
            terminate();
            if (this.mReleasedListener != null) {
                this.mReleasedListener.onReleased();
                this.mReleasedListener = null;
            }
            this.mTerminating = false;
        }
        return ret;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void setHandler(Handler fromMain, Handler toMain) {
        sHandlerFromMain = fromMain;
        sHandlerToMain = toMain;
    }

    public BaseShootingExecutor() {
        sCameraSetting = CameraSetting.getInstance();
        sNotify = CameraNotificationManager.getInstance();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void prepare(Camera camera, CameraEx cameraEx) {
        sCamera = camera;
        sCameraEx = cameraEx;
        sCameraEx.setAutoFocusStartListener(sMyAutoFocusStartCb);
        sCameraEx.setAutoFocusDoneListener(sMyAutoFocusDoneCb);
        sCameraEx.setPreviewStartListener(getPreviewStartListener());
        if (this.mAdapter != null) {
            this.mAdapter.prepare();
        }
    }

    protected CameraEx.PreviewStartListener getPreviewStartListener() {
        return sMyPreviewStartCb;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void terminate() {
        Log.i(TAG, LOG_TERMINATE);
        lockAutoFocus(false);
        if (this.mAdapter != null) {
            this.mAdapter.terminate();
        }
    }

    @Deprecated
    /* loaded from: classes.dex */
    protected static class MyBaseAutoFocusCb implements Camera.AutoFocusCallback {
        protected MyBaseAutoFocusCb() {
        }

        @Override // android.hardware.Camera.AutoFocusCallback
        public void onAutoFocus(boolean success, Camera camera) {
            StringBuilder builder = BaseShootingExecutor.getStringBuilder();
            builder.replace(0, builder.length(), BaseShootingExecutor.LOG_CAMERA).append(BaseShootingExecutor.LOG_ON_AUTO_FOCUS).append(BaseShootingExecutor.LOG_STATUS).append(success);
            Log.i(BaseShootingExecutor.TAG, builder.toString());
            BaseShootingExecutor.onMyAutoFocus(success, camera);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public static class MyBaseAutoFocusStartCb implements CameraEx.AutoFocusStartListener {
        protected MyBaseAutoFocusStartCb() {
        }

        public void onStart(CameraEx camex) {
            StringBuilder builder = BaseShootingExecutor.getStringBuilder();
            builder.replace(0, builder.length(), BaseShootingExecutor.LOG_CAMERA).append(BaseShootingExecutor.LOG_ON_AUTO_FOCUS_START);
            Log.i(BaseShootingExecutor.TAG, builder.toString());
            BaseShootingExecutor.onStartAutoFocus();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public static class MyBaseAutoFocusDoneCb implements CameraEx.AutoFocusDoneListener {
        protected MyBaseAutoFocusDoneCb() {
        }

        public void onDone(int isSuccess, int[] focusarea, CameraEx camex) {
            StringBuilder builder = BaseShootingExecutor.getStringBuilder();
            builder.replace(0, builder.length(), BaseShootingExecutor.LOG_CAMERA).append(BaseShootingExecutor.LOG_ON_AUTO_FOCUS_DONE).append(BaseShootingExecutor.LOG_STATUS).append(isSuccess);
            Log.i(BaseShootingExecutor.TAG, builder.toString());
            BaseShootingExecutor.onDoneAutoFocus(isSuccess, focusarea);
        }
    }

    /* loaded from: classes.dex */
    protected static class MyBaseCustomWhiteBalanceCallback implements CameraEx.CustomWhiteBalanceCallback {
        protected MyBaseCustomWhiteBalanceCallback() {
        }

        public void onCapture(CameraEx.CustomWhiteBalanceInfo info, CameraEx cameraEx) {
            StringBuilder builder = BaseShootingExecutor.getStringBuilder();
            builder.replace(0, builder.length(), BaseShootingExecutor.LOG_CAMERA).append(BaseShootingExecutor.LOG_ON_CUSTOM_WHITE_BALANCE_CAPTURE);
            Log.i(BaseShootingExecutor.TAG, builder.toString());
            BaseShootingExecutor.onCaptureCustomWhiteBalance(info, cameraEx);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public static class MyBasePreviewStartCb implements CameraEx.PreviewStartListener {
        public void onStart(CameraEx cameraEx) {
            StringBuilder builder = BaseShootingExecutor.getStringBuilder();
            builder.replace(0, builder.length(), BaseShootingExecutor.LOG_CAMERA).append(BaseShootingExecutor.LOG_ON_PREVIEW_START);
            Log.i(BaseShootingExecutor.TAG, builder.toString());
            BaseShootingExecutor.onStartPreview(cameraEx);
        }
    }

    public static void setPreviewStartListener(CameraEx.PreviewStartListener listener) {
        sPreviewCb = listener;
    }

    protected static void onMyAutoFocus(boolean success, Camera camera) {
        Runnable runnable = new OnAutoFocusRunnable(success, camera);
        sHandlerToMain.post(runnable);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Deprecated
    /* loaded from: classes.dex */
    public static class OnAutoFocusRunnable implements Runnable {
        private Camera mCamera;
        private boolean mSuccess;

        public OnAutoFocusRunnable(boolean success, Camera camera) {
            this.mSuccess = success;
            this.mCamera = camera;
        }

        @Override // java.lang.Runnable
        public void run() {
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void onStartAutoFocus() {
        CameraNotificationManager notification = CameraNotificationManager.getInstance();
        notification.requestNotify(CameraNotificationManager.START_AUTO_FOCUS);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void onDoneAutoFocus(int status, int[] area) {
        boolean isSucceeded = true;
        CameraNotificationManager.OnFocusInfo info = new CameraNotificationManager.OnFocusInfo(status, area);
        if (sAutoFocusCb != null) {
            if (status != 1 && status != 4) {
                isSucceeded = false;
            }
            sAutoFocusCb.onAutoFocus(isSucceeded, sCamera);
        }
        CameraNotificationManager notification = CameraNotificationManager.getInstance();
        notification.requestNotify(CameraNotificationManager.DONE_AUTO_FOCUS, info);
    }

    public static void onProgress(double progress) {
        CameraNotificationManager notification = CameraNotificationManager.getInstance();
        notification.requestNotify(CameraNotificationManager.PROCESSING_PROGRESS, Double.valueOf(progress));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void onDoneRecordingMediaChange(CameraEx cameraEx) {
        synchronized (mediaLockObject) {
            try {
                sHandlerToMain.post(new RecordingMediaChangeRunnable(sRecordingMediaChangeCallback));
                sRecordingMediaChangeCallback = null;
                mediaLockObject.notifyAll();
            } catch (Exception e) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public static class RecordingMediaChangeRunnable implements Runnable {
        private CameraEx.RecordingMediaChangeCallback mCallback;

        public RecordingMediaChangeRunnable(CameraEx.RecordingMediaChangeCallback callback) {
            this.mCallback = callback;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (this.mCallback != null) {
                this.mCallback.onRecordingMediaChange(BaseShootingExecutor.sCameraEx);
            }
            this.mCallback = null;
        }
    }

    protected static void onCaptureCustomWhiteBalance(CameraEx.CustomWhiteBalanceInfo info, CameraEx cameraEx) {
        sHandlerToMain.post(new OnCaptureCustomWhiteBalanceRunnable(info, cameraEx));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public static class OnCaptureCustomWhiteBalanceRunnable implements Runnable {
        private CameraEx mCameraEx;
        private CameraEx.CustomWhiteBalanceInfo mInfo;

        public OnCaptureCustomWhiteBalanceRunnable(CameraEx.CustomWhiteBalanceInfo info, CameraEx cameraEx) {
            this.mInfo = info;
            this.mCameraEx = cameraEx;
        }

        @Override // java.lang.Runnable
        public void run() {
            BaseShootingExecutor.sCustomWhiteBalanceCallback.onCapture(this.mInfo, this.mCameraEx);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void onStartPreview(CameraEx cameraEx) {
        Runnable runnable = new StartPreviewRunnable(cameraEx);
        sHandlerToMain.post(runnable);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public static class StartPreviewRunnable implements Runnable {
        private CameraEx mCameraEx;

        public StartPreviewRunnable(CameraEx cameraEx) {
            this.mCameraEx = cameraEx;
        }

        @Override // java.lang.Runnable
        public void run() {
            StringBuilder builder = BaseShootingExecutor.getStringBuilder();
            builder.replace(0, builder.length(), BaseShootingExecutor.LOG_UI).append(BaseShootingExecutor.LOG_START_PREVIEW).append(BaseShootingExecutor.LOG_RUNNABLE);
            Log.d(BaseShootingExecutor.TAG, builder.toString());
            if (BaseShootingExecutor.sPreviewCb != null) {
                BaseShootingExecutor.sPreviewCb.onStart(this.mCameraEx);
            }
        }
    }

    public int getHolderType() {
        return 3;
    }

    public void autoFocus(Camera.AutoFocusCallback listener, String behavior) {
        sAutoFocusCb = listener;
        synchronized (sAFBehaviorLock) {
            if ("afc_woaf".equals(behavior)) {
                behavior = "af_woaf";
            }
            sAutoFocusBehavior = behavior;
        }
        sHandlerFromMain.post(this.mAutoFocusRunnable);
    }

    public void autoFocus(Camera.AutoFocusCallback listener) {
        autoFocus(listener, null);
    }

    protected AutoFocusRunnable getAutoFocusRunnable() {
        return new AutoFocusRunnable();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public static class AutoFocusRunnable implements Runnable {
        @Override // java.lang.Runnable
        public void run() {
            BaseShootingExecutor.sIsAutoFocusRequested = true;
            if (!BaseShootingExecutor.isFocusLocked) {
                Log.d(BaseShootingExecutor.TAG, "autoFocus : " + BaseShootingExecutor.sIsAutoFocusRequested);
                synchronized (BaseShootingExecutor.sAFBehaviorLock) {
                    BaseShootingExecutor.sRunningAutoFocusBehavior = BaseShootingExecutor.sAutoFocusBehavior;
                    BaseShootingExecutor.sCamera.autoFocus(BaseShootingExecutor.sMyAutoFocusCb);
                }
            }
        }
    }

    public void cancelAutoFocus() {
        sAutoFocusCb = null;
        sHandlerFromMain.post(this.mCancelAutoFocusRunnable);
    }

    protected CancelAutoFocusRunnable getCancelAutoFocusRunnable() {
        return new CancelAutoFocusRunnable();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public static class CancelAutoFocusRunnable implements Runnable {
        @Override // java.lang.Runnable
        public void run() {
            BaseShootingExecutor.sIsAutoFocusRequested = false;
            if (!BaseShootingExecutor.isFocusLocked) {
                Log.d(BaseShootingExecutor.TAG, "cancelAutoFocus : " + BaseShootingExecutor.sIsAutoFocusRequested);
                BaseShootingExecutor.sCamera.cancelAutoFocus();
                BaseShootingExecutor.sRunningAutoFocusBehavior = null;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void lockAutoFocus(boolean lock) {
        if (isFocusLocked != lock) {
            isFocusLocked = lock;
            if (!lock) {
                StringBuilder builder = getStringBuilder();
                builder.replace(0, builder.length(), LOG_CANCEL_AUTO_FOCUS).append(LOG_STATUS).append(sIsAutoFocusRequested);
                Log.d(TAG, builder.toString());
                if (!sIsAutoFocusRequested) {
                    builder.replace(0, builder.length(), LOG_CANCEL_AUTO_FOCUS).append(LOG_STATUS).append(LOG_LOCK);
                    Log.d(TAG, builder.toString());
                    sCamera.cancelAutoFocus();
                    sRunningAutoFocusBehavior = null;
                }
            }
        }
    }

    public void captureCustomWhiteBalance(CameraEx.CustomWhiteBalanceCallback listener) {
        sCustomWhiteBalanceCallback = listener;
        StringBuilder builder = getStringBuilder();
        builder.replace(0, builder.length(), LOG_UI).append(LOG_CAPTURE_CUSTOM_WHITE_BALANCE);
        Log.d(TAG, builder.toString());
        sHandlerFromMain.post(this.mCaptureCustomWhiteBalanceRunnable);
    }

    /* loaded from: classes.dex */
    protected static class CaptureCustomWhiteBalanceRunnable implements Runnable {
        protected CaptureCustomWhiteBalanceRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            StringBuilder builder = BaseShootingExecutor.getStringBuilder();
            builder.replace(0, builder.length(), BaseShootingExecutor.LOG_CAMERA).append(BaseShootingExecutor.LOG_CAPTURE_CUSTOM_WHITE_BALANCE);
            Log.d(BaseShootingExecutor.TAG, builder.toString());
            BaseShootingExecutor.sCameraEx.captureCustomWhiteBalance(BaseShootingExecutor.sMyCustomWhiteBalanceCallback);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void sendKey(int key, boolean isSync) {
        boolean ret;
        StringBuilder builder = getStringBuilder();
        builder.replace(0, builder.length(), LOG_SEND_KEY).append(key);
        Log.i(TAG, builder.toString());
        if (this.state != null) {
            Handler handler = this.state.getHandler();
            Message msg = handler.obtainMessage(key);
            if (isSync) {
                ret = handler.sendMessageAtFrontOfQueue(msg);
            } else {
                ret = handler.sendMessage(msg);
            }
            if (!ret) {
                Log.w(TAG, builder.toString() + " is failed");
            }
        }
    }

    public String getRecordingMedia() {
        return sCurrentMedia;
    }

    public void setPreviewDisplay(SurfaceHolder holder) throws IOException {
        sCamera.setPreviewDisplay(holder);
    }

    public void startPreview() {
        Log.d(TAG, LOG_START_PREVIEW);
        sCamera.startPreview();
    }

    public int getTempRemoteControl() {
        return 0;
    }

    public boolean getOriginalRemoteControl() {
        return false;
    }

    public boolean needToBeStable(int flg) {
        return true;
    }

    public void setPictureReviewInfoHist(boolean histogramLayout) {
        outputUnOverriddenMethodLog();
    }

    public void takePicture() {
        outputUnOverriddenMethodLog();
    }

    public void takePicture(int shutterType) {
        outputUnOverriddenMethodLog();
    }

    public void cancelTakePicture() {
        outputUnOverriddenMethodLog();
    }

    public void lockCancelTakePicture(boolean lock) {
        outputUnOverriddenMethodLog();
    }

    public boolean isBulbOpened() {
        return isBulbOpened;
    }

    public int getShutterType() {
        return -1;
    }

    public void startSelfTimerShutter() {
        outputUnOverriddenMethodLog();
    }

    public void startSelfTimerShutter(int shutterType) {
        outputUnOverriddenMethodLog();
    }

    public void cancelSelfTimerShutter() {
        outputUnOverriddenMethodLog();
    }

    public void cancelAutoPictureReview() {
        outputUnOverriddenMethodLog();
    }

    public void startMovieRec() {
        outputUnOverriddenMethodLog();
    }

    public void stopMovieRec() {
        outputUnOverriddenMethodLog();
    }

    protected void outputUnOverriddenMethodLog() {
        String exe_class_name = getClass().getSimpleName();
        String stack_trace = LOG_STACK_TRACE;
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            stack_trace = stack_trace + " :" + stackTraceElement.getMethodName();
        }
        STRBUILD.replace(0, STRBUILD.length(), LOG_UNOVERRIDDEN_METHOD_LOG).append(LOG_EXECUTOR_NAME).append(exe_class_name).append(stack_trace);
        RuntimeException e = new RuntimeException(STRBUILD.toString());
        throw e;
    }
}
