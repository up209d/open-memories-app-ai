package com.sony.imaging.app.base.shooting.camera.executor;

import android.hardware.Camera;
import android.util.Log;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.KikilogUtil;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class IntervalRecExecutor extends ShootingExecutor {
    private static final String INH_FACTOR_STILL_WRITING = "INH_FACTOR_STILL_WRITING";
    public static final int INTVL_REC_STARTED = 202;
    public static final int INTVL_REC_STARTING = 201;
    private static final String LOG_INTERVAL_REC_COMPLETED = "intervalRecCompleted";
    private static final String LOG_LISTENER_NULL = ": listener is null";
    private static final String LOG_MYRELEASE = "myRelease";
    private static final String LOG_MY_CANCEL_SELFTIMER_SHUTTER = "myCancelSelfTimerShutter";
    private static final String LOG_MY_CANCEL_TAKEPICTURE = "myCancelTakePicture";
    private static final String LOG_MY_START_SELFTIMER_SHUTTER = "myStartSelfTimerShutter";
    private static final String LOG_MY_TAKEPICTURE = "myTakePicture";
    private static final String LOG_ON_SHUTTER = "onShutter status:";
    private static final String LOG_ON_START_FAILED = "onStart failed";
    private static final String LOG_ON_START_SUCCESS = "onStart successed";
    private static final String LOG_ON_STOP = "onStop";
    private static final String LOG_PREPARE = "prepare";
    private static final String LOG_RELEASE_INTVL_REC_LISTENER = "set null to intervalRecListener";
    private static final String LOG_SET_INTVL_REC_LISTENER = "setIntervalRecListener";
    private static final String LOG_SHOOTING_COUNT = "Shooting count is ";
    private static final String LOG_STOP_INTERVAL_REC = "stopIntervalRec";
    private static final String LOG_TRY_RELEASE = "tryRelease";
    protected static final int PF_VER_EXIST_SKELTON_CAUTION = 12;
    private static final String TAG = "IntervalRecExecutor";
    protected static int sShootingCount = 0;
    protected static CameraEx.IntervalRecListener sIntervalRecListener = null;
    protected static MyIntervalRecCb sMyIntervalRecCb = null;
    protected static MyIntervalRecShutterCb sIntervalRecShutterCb = null;
    protected static boolean sIsPreviewStatus = true;
    public static final int INTVL_REC_INITIALIZED = 200;
    private static int sIntervalRecStatus = INTVL_REC_INITIALIZED;
    private static boolean sIntervalRecExecuting = false;
    protected static MyIntervalRecShootingPreviewStartCb sMyIntervalRecPreviewStartCb = null;

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor, com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void prepare(Camera camera, CameraEx cameraEx) {
        if (ExecutorCreator.isIntervalRecEnable()) {
            sIntervalRecShutterCb = new MyIntervalRecShutterCb(cameraEx);
            sCameraEx.setIntervalRecListener(getIntervalRecCb());
        }
        super.prepare(camera, cameraEx);
        Log.i(TAG, LOG_PREPARE);
    }

    public static void setIntervalRecListener(CameraEx.IntervalRecListener callback) {
        Log.i(TAG, LOG_SET_INTVL_REC_LISTENER);
        if (callback == null) {
            Log.i(TAG, LOG_LISTENER_NULL);
        }
        sIntervalRecListener = callback;
    }

    protected CameraEx.IntervalRecListener getIntervalRecCb() {
        if (!ExecutorCreator.isIntervalRecEnable()) {
            return null;
        }
        if (sMyIntervalRecCb == null) {
            sMyIntervalRecCb = new MyIntervalRecCb();
        }
        return sMyIntervalRecCb;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public static class MyIntervalRecCb implements CameraEx.IntervalRecListener {
        protected MyIntervalRecCb() {
        }

        public void onStart(boolean isSuccess, CameraEx cameraEx) {
            IntervalRecExecutor.onIntervalRecStarted(isSuccess, cameraEx);
        }

        public void onStop(CameraEx cameraEx) {
            IntervalRecExecutor.onIntervalRecStopped(cameraEx);
        }
    }

    protected static void onIntervalRecStarted(boolean isSuccess, CameraEx cameraEx) {
        if (isSuccess) {
            sIntervalRecExecuting = true;
            sIntervalRecStatus = INTVL_REC_STARTED;
            sHandlerToMain.post(new MyIntervalRecStartedRunnable(cameraEx));
        } else {
            sIntervalRecStatus = INTVL_REC_INITIALIZED;
            sHandlerToMain.post(new MyIntervalRecNotStartRunnable(cameraEx));
        }
    }

    protected static void onIntervalRecStopped(CameraEx cameraEx) {
        sIntervalRecExecuting = false;
        intervalRecCompleted(cameraEx);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public static class MyIntervalRecStartedRunnable implements Runnable {
        protected CameraEx mCameraEx;

        public MyIntervalRecStartedRunnable(CameraEx cameraEx) {
            this.mCameraEx = cameraEx;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (IntervalRecExecutor.sIntervalRecListener != null) {
                IntervalRecExecutor.sIntervalRecListener.onStart(true, this.mCameraEx);
            }
            BaseShootingExecutor.sNotify.requestNotify(CameraNotificationManager.TAG_INTVAL_REC_STARTED);
            StringBuilder builder = BaseShootingExecutor.getStringBuilder();
            builder.replace(0, builder.length(), "UI :").append("onStart successed");
            Log.i(IntervalRecExecutor.TAG, builder.toString());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public static class MyIntervalRecNotStartRunnable implements Runnable {
        protected CameraEx mCameraEx;

        public MyIntervalRecNotStartRunnable(CameraEx cameraEx) {
            this.mCameraEx = cameraEx;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (IntervalRecExecutor.sIntervalRecListener != null) {
                IntervalRecExecutor.sIntervalRecListener.onStart(false, this.mCameraEx);
            }
            BaseShootingExecutor.sNotify.requestNotify(CameraNotificationManager.TAG_INTVAL_REC_FAILED);
            StringBuilder builder = BaseShootingExecutor.getStringBuilder();
            builder.replace(0, builder.length(), "UI :").append("onStart failed");
            Log.e(IntervalRecExecutor.TAG, builder.toString());
            if (12 > Environment.getVersionPfAPI() || !Environment.isNewBizDeviceActionCam()) {
                CautionUtilityClass.getInstance().requestTrigger(586);
            }
            CautionUtilityClass.getInstance().requestTrigger(32771);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public static class MyIntervalRecStoppedRunnable implements Runnable {
        protected CameraEx mCameraEx;

        public MyIntervalRecStoppedRunnable(CameraEx cameraEx) {
            this.mCameraEx = cameraEx;
        }

        @Override // java.lang.Runnable
        public void run() {
            StringBuilder builder = BaseShootingExecutor.getStringBuilder();
            builder.replace(0, builder.length(), "UI :").append("onStop");
            Log.i(IntervalRecExecutor.TAG, builder.toString());
            if (IntervalRecExecutor.sIntervalRecListener != null) {
                Log.i(IntervalRecExecutor.TAG, IntervalRecExecutor.LOG_INTERVAL_REC_COMPLETED);
                IntervalRecExecutor.sIntervalRecListener.onStop(this.mCameraEx);
            }
            BaseShootingExecutor.sNotify.requestNotify(CameraNotificationManager.TAG_INTVAL_REC_STOPPED);
        }
    }

    protected static void intervalRecCompleted(CameraEx cameraEx) {
        if (!sIntervalRecExecuting && sIsPreviewStatus) {
            Runnable runnable = new MyIntervalRecStoppedRunnable(cameraEx);
            sHandlerToMain.post(runnable);
        }
    }

    /* loaded from: classes.dex */
    protected static class MyIntervalRecShutterCb extends ShootingExecutor.MyShutterCb {
        CameraEx mCameraEx;

        public MyIntervalRecShutterCb(CameraEx cameraEx) {
            this.mCameraEx = null;
            this.mCameraEx = cameraEx;
        }

        @Override // com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor.MyShutterCb
        public void onShutter(int status, CameraEx cameraEx) {
            super.onShutter(status, cameraEx);
            ShootingExecutor.sQueue = null;
            if (status == 0) {
                IntervalRecExecutor.sShootingCount++;
                IntervalRecExecutor.sIsPreviewStatus = false;
                KikilogUtil.incrementShootingCount();
            }
            StringBuilder builder = BaseShootingExecutor.getStringBuilder();
            builder.replace(0, builder.length(), "CAMERA :").append(IntervalRecExecutor.LOG_SHOOTING_COUNT).append(IntervalRecExecutor.sShootingCount);
            Log.i(IntervalRecExecutor.TAG, builder.toString());
            Runnable runnable = new MyIntervalRecShutterRunnable(status, cameraEx);
            BaseShootingExecutor.sHandlerToMain.post(runnable);
        }
    }

    /* loaded from: classes.dex */
    protected static class MyIntervalRecShutterRunnable implements Runnable {
        protected CameraEx mCameraEx;
        protected int mStatus;

        public MyIntervalRecShutterRunnable(int status, CameraEx cameraEx) {
            this.mStatus = status;
            this.mCameraEx = cameraEx;
        }

        @Override // java.lang.Runnable
        public void run() {
            StringBuilder builder = BaseShootingExecutor.getStringBuilder();
            builder.replace(0, builder.length(), "UI :").append(IntervalRecExecutor.LOG_ON_SHUTTER).append(this.mStatus);
            Log.i(IntervalRecExecutor.TAG, builder.toString());
            if (this.mStatus == 0) {
                BaseShootingExecutor.sNotify.requestNotify(CameraNotificationManager.TAG_INTVAL_REC_SHOTS_COUNT_UPDATED);
            } else {
                BaseShootingExecutor.sNotify.requestNotify(CameraNotificationManager.TAG_INTVAL_REC_ON_SHUTTER_STATUS_NOT_OK);
            }
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public int getIntervalRecShootingCount() {
        return sShootingCount;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor
    protected void myTakePicture() {
        Log.i(TAG, LOG_MY_TAKEPICTURE);
        if (ExecutorCreator.isIntervalRecEnable()) {
            sShootingCount = 0;
            MediaNotificationManager mediaMgr = MediaNotificationManager.getInstance();
            if (mediaMgr.isNoCard() || mediaMgr.getRemaining() <= 0 || AvailableInfo.isFactor(INH_FACTOR_STILL_WRITING)) {
                sHandlerToMain.post(new MyIntervalRecNotStartRunnable(sCameraEx));
            } else {
                sIntervalRecStatus = INTVL_REC_STARTING;
                this.mAdapter.takePicture();
            }
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor
    protected void myCancelTakePicture() {
        StringBuilder builder = getStringBuilder();
        builder.replace(0, builder.length(), "CAMERA :").append(LOG_MY_CANCEL_TAKEPICTURE);
        Log.i(TAG, builder.toString());
        lockAutoFocus(false);
        if (ExecutorCreator.isIntervalRecEnable()) {
            sCameraEx.stopIntervalRec();
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void stopIntervalRec() {
        StringBuilder builder = getStringBuilder();
        builder.replace(0, builder.length(), "UI :").append(LOG_STOP_INTERVAL_REC);
        Log.e(TAG, builder.toString());
        sNotify.requestNotify(CameraNotificationManager.TAG_INTVAL_REC_STOP_BEGIN);
        Runnable runnable = new MyIntervalRecStopRunnable();
        sHandlerFromMain.post(runnable);
    }

    /* loaded from: classes.dex */
    protected class MyIntervalRecStopRunnable implements Runnable {
        protected MyIntervalRecStopRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            IntervalRecExecutor.this.myCancelTakePicture();
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor
    protected void myStartSelfTimerShutter() {
        Log.w(TAG, LOG_MY_START_SELFTIMER_SHUTTER);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor
    protected void myCancelSelfTimerShutter() {
        Log.w(TAG, LOG_MY_CANCEL_SELFTIMER_SHUTTER);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor
    protected CameraEx.ShutterListener getShutterListener() {
        return sIntervalRecShutterCb;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    protected void myRelease() {
        Log.i(TAG, LOG_MYRELEASE);
        if (202 == sIntervalRecStatus) {
            myCancelTakePicture();
        }
        tryRelease();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor, com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public boolean tryRelease() {
        StringBuilder builder = getStringBuilder();
        builder.replace(0, builder.length(), "CAMERA :").append(LOG_TRY_RELEASE);
        Log.i(TAG, builder.toString());
        if (ExecutorCreator.isIntervalRecEnable()) {
            Log.i(TAG, LOG_RELEASE_INTVL_REC_LISTENER);
            sCameraEx.setIntervalRecListener((CameraEx.IntervalRecListener) null);
        }
        return super.tryRelease();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void inquireKey(int key) {
        sendKey(key, true);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void stopPreview() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor, com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void terminate() {
        super.terminate();
    }

    public static int getIntervalRecStatus() {
        return sIntervalRecStatus;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor, com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    protected CameraEx.PreviewStartListener getPreviewStartListener() {
        if (sMyIntervalRecPreviewStartCb == null) {
            sMyIntervalRecPreviewStartCb = new MyIntervalRecShootingPreviewStartCb(this);
        }
        return sMyIntervalRecPreviewStartCb;
    }

    /* loaded from: classes.dex */
    protected static class MyIntervalRecShootingPreviewStartCb extends ShootingExecutor.MyShootingPreviewStartCb {
        protected ShootingExecutor mExecutor;

        public MyIntervalRecShootingPreviewStartCb(IntervalRecExecutor executor) {
            this.mExecutor = executor;
        }

        @Override // com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor.MyShootingPreviewStartCb, com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor.MyBasePreviewStartCb
        public void onStart(CameraEx cameraEx) {
            super.onStart(cameraEx);
            StringBuilder builder = BaseShootingExecutor.getStringBuilder();
            builder.replace(0, builder.length(), "CAMERA :").append("onPreviewStart");
            Log.i(IntervalRecExecutor.TAG, builder.toString());
            IntervalRecExecutor.sIsPreviewStatus = true;
            IntervalRecExecutor.intervalRecCompleted(cameraEx);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public boolean needToBeStable(int flg) {
        return (flg & (-3)) != 0;
    }
}
