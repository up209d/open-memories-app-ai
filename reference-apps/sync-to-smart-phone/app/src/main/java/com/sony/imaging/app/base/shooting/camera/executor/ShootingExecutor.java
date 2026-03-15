package com.sony.imaging.app.base.shooting.camera.executor;

import android.hardware.Camera;
import android.util.Log;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor;
import com.sony.imaging.app.util.PTag;
import com.sony.scalar.hardware.CameraEx;
import java.util.LinkedList;
import java.util.Queue;

/* loaded from: classes.dex */
public abstract class ShootingExecutor extends BaseShootingExecutor {
    private static final String LOG_CANCEL_TAKE_PICTURE = "cancelTakePicture";
    private static final String LOG_ON_GET_PICTURE_REVIEW_INFO = "onGetPictureReviewInfo";
    private static final String LOG_ON_MOTIONSHOT_COMPLETED = "onMotionShotCompleted";
    private static final String LOG_ON_PICTURE_REVIEW_START = "onPictureReviewStart";
    private static final String LOG_ON_PICTURE_TAKEN = "onPictureTaken";
    private static final String LOG_ON_SHUTTER = "onShutter";
    private static final String LOG_ON_START_SHUTTER = "onStartShutter";
    private static final String LOG_START_PICTURE_REVIEW = "startPictureReview";
    private static final String TAG = "ShootingExecutor";
    private MyPictureReviewInfoCb mMyPictureReviewInfoCb;
    private MyPictureReviewStartCb mMyPictureReviewStartCb;
    protected int mShutterType;
    private static MyJpegCb sMyJpegCb = new MyJpegCb();
    private static Queue<Runnable> sQueue = null;
    protected static CameraEx.AutoPictureReviewControl sAutoReviewControl = null;
    protected static MyShootingPreviewStartCb sMyPreviewStartCb = new MyShootingPreviewStartCb();
    private static CameraEx.PictureReviewStartListener sPictureReviewCb = null;
    private static CameraEx.ShutterListener sShutterCb = null;
    private static CameraEx.JpegListener sJpegCb = null;
    private static boolean sIsRequestedCancel = false;
    private static boolean isLockCancelTakePicture = false;
    protected static MyRecordingMediaChangeCallback sMyRecordingMediaChangeCallback = new MyRecordingMediaChangeCallback();
    private CancelTakePictureRunnable mCancelTakePictureRunnable = new CancelTakePictureRunnable(this);
    private CancelSelfTimerShutterRunnable mCancelSelfTimerShutterRunnable = new CancelSelfTimerShutterRunnable(this);

    protected abstract CameraEx.ShutterListener getShutterListener();

    protected abstract void myCancelSelfTimerShutter();

    protected abstract void myCancelTakePicture();

    protected abstract void myStartSelfTimerShutter();

    protected abstract void myTakePicture();

    /* JADX INFO: Access modifiers changed from: protected */
    public ShootingExecutor() {
        this.mMyPictureReviewStartCb = new MyPictureReviewStartCb();
        this.mMyPictureReviewInfoCb = new MyPictureReviewInfoCb();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void setAdapter(IAdapter adapter) {
        this.mAdapter = adapter;
        if (adapter != null) {
            adapter.setExecutor(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void prepare(Camera camera, CameraEx cameraEx) {
        super.prepare(camera, cameraEx);
        Log.i(TAG, "prepare");
        sAutoReviewControl = new CameraEx.AutoPictureReviewControl();
        sAutoReviewControl.setPictureReviewStartListener(this.mMyPictureReviewStartCb);
        sAutoReviewControl.setPictureReviewInfoListener(this.mMyPictureReviewInfoCb);
        sCameraEx.setShutterListener(getShutterListener());
        sCameraEx.setAutoPictureReviewControl(sAutoReviewControl);
        if (9 <= CameraSetting.getPfApiVersion()) {
            sCameraEx.setMotionShotResultListener(new MyMotionShotResultCb());
        }
        DisplayModeObserver displayoModeObserver = DisplayModeObserver.getInstance();
        int displayMode = displayoModeObserver.getActiveDispMode(1);
        boolean isHistgramLayout = displayMode == 5;
        setPictureReviewInfoHist(isHistgramLayout);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void terminate() {
        super.terminate();
        Log.i(TAG, "terminate");
        lockCancelTakePicture(false);
        sweepQueue(1, sCameraEx);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void enableTermination(boolean canTerminate) {
        this.mCanTerminate = canTerminate;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void release(BaseShootingExecutor.ReleasedListener listener) {
        super.release(listener);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public boolean tryRelease() {
        Log.i(TAG, "onReleased");
        return super.tryRelease();
    }

    /* loaded from: classes.dex */
    public static class MyShutterCb implements CameraEx.ShutterListener {
        private static final String PTAG_RELEASE_LAG_END = "end shutter lag";

        public void onShutter(int status, CameraEx cameraEx) {
            PTag.end(PTAG_RELEASE_LAG_END);
            StringBuilder builder = BaseShootingExecutor.getStringBuilder();
            builder.replace(0, builder.length(), "CAMERA :").append("onShutter").append(": status ").append(status);
            Log.i(ShootingExecutor.TAG, builder.toString());
            Queue unused = ShootingExecutor.sQueue = new LinkedList();
        }
    }

    /* loaded from: classes.dex */
    private static class MyPictureReviewStartCb implements CameraEx.PictureReviewStartListener {
        private MyPictureReviewStartCb() {
        }

        public void onStart(CameraEx cameraEx) {
            StringBuilder builder = BaseShootingExecutor.getStringBuilder();
            builder.replace(0, builder.length(), "CAMERA :").append(ShootingExecutor.LOG_ON_PICTURE_REVIEW_START);
            Log.i(ShootingExecutor.TAG, builder.toString());
            if (ShootingExecutor.sQueue != null) {
                StartPictureReviewRunnable runnable = new StartPictureReviewRunnable(cameraEx);
                ShootingExecutor.sQueue.add(runnable);
            } else {
                ShootingExecutor.onStartPictureReview(cameraEx);
            }
        }
    }

    /* loaded from: classes.dex */
    private static class MyPictureReviewInfoCb implements CameraEx.PictureReviewInfoListener {
        private MyPictureReviewInfoCb() {
        }

        public void onGetInfo(CameraEx.ReviewInfo info, CameraEx cameraEx) {
            StringBuilder builder = BaseShootingExecutor.getStringBuilder();
            builder.replace(0, builder.length(), "CAMERA :").append(ShootingExecutor.LOG_ON_GET_PICTURE_REVIEW_INFO);
            Log.i(ShootingExecutor.TAG, builder.toString());
            if (ShootingExecutor.sQueue != null) {
                AutoReviewInfoRunnable runnable = new AutoReviewInfoRunnable(info);
                ShootingExecutor.sQueue.add(runnable);
            } else {
                ShootingExecutor.onAutoReviewInfo(info);
            }
        }
    }

    /* loaded from: classes.dex */
    static class MyJpegCb implements CameraEx.JpegListener {
        MyJpegCb() {
        }

        public void onPictureTaken(byte[] jpeg, CameraEx cameraEx) {
            StringBuilder builder = BaseShootingExecutor.getStringBuilder();
            builder.replace(0, builder.length(), "CAMERA :").append(ShootingExecutor.LOG_ON_PICTURE_TAKEN);
            Log.i(ShootingExecutor.TAG, builder.toString());
            ShootingExecutor.onMyPictureTaken(jpeg, cameraEx);
        }
    }

    /* loaded from: classes.dex */
    protected static class MyShootingPreviewStartCb extends BaseShootingExecutor.MyBasePreviewStartCb {
        @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor.MyBasePreviewStartCb
        public void onStart(CameraEx cameraEx) {
            StringBuilder builder = BaseShootingExecutor.getStringBuilder();
            builder.replace(0, builder.length(), "CAMERA :").append("onPreviewStart");
            Log.i(ShootingExecutor.TAG, builder.toString());
            if (ShootingExecutor.sQueue != null) {
                BaseShootingExecutor.StartPreviewRunnable runnable = new BaseShootingExecutor.StartPreviewRunnable(cameraEx);
                ShootingExecutor.sQueue.add(runnable);
            } else {
                BaseShootingExecutor.onStartPreview(cameraEx);
            }
        }
    }

    /* loaded from: classes.dex */
    protected static class MyMotionShotResultCb implements CameraEx.MotionShotResultListener {
        protected MyMotionShotResultCb() {
        }

        public void onCompleted(boolean result, CameraEx cameraEx) {
            StringBuilder builder = BaseShootingExecutor.getStringBuilder();
            builder.replace(0, builder.length(), "CAMERA :").append(ShootingExecutor.LOG_ON_MOTIONSHOT_COMPLETED);
            Log.i(ShootingExecutor.TAG, builder.toString());
            ShootingExecutor.onMotionShotCompleted(result, cameraEx);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    protected CameraEx.PreviewStartListener getPreviewStartListener() {
        return sMyPreviewStartCb;
    }

    public static void setPictureReviewStartListener(CameraEx.PictureReviewStartListener listener) {
        sPictureReviewCb = listener;
    }

    public static void setShutterListener(CameraEx.ShutterListener listener) {
        sShutterCb = listener;
    }

    public static void setJpegListener(CameraEx.JpegListener listener) {
        sJpegCb = listener;
        if (listener != null) {
            sCameraEx.setJpegListener(sMyJpegCb);
        } else {
            sCameraEx.setJpegListener((CameraEx.JpegListener) null);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void onStartPictureReview(CameraEx cameraEx) {
        Runnable runnable = new StartPictureReviewRunnable(cameraEx);
        sHandlerToMain.post(runnable);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class StartPictureReviewRunnable implements Runnable {
        private CameraEx mCameraEx;

        public StartPictureReviewRunnable(CameraEx cameraEx) {
            this.mCameraEx = cameraEx;
        }

        @Override // java.lang.Runnable
        public void run() {
            StringBuilder builder = BaseShootingExecutor.getStringBuilder();
            builder.replace(0, builder.length(), "UI :").append(ShootingExecutor.LOG_START_PICTURE_REVIEW).append("Runnable");
            Log.d(ShootingExecutor.TAG, builder.toString());
            if (ShootingExecutor.sPictureReviewCb != null) {
                ShootingExecutor.sPictureReviewCb.onStart(this.mCameraEx);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onStartShutter(int status, CameraEx cameraEx) {
        sweepQueue(status, cameraEx);
        StringBuilder builder = getStringBuilder();
        builder.replace(0, builder.length(), "CAMERA :").append(LOG_ON_START_SHUTTER).append(": status ").append(status);
        Log.i(TAG, builder.toString());
        this.mCanTerminate = true;
        tryRelease();
    }

    protected void sweepQueue(int status, CameraEx cameraEx) {
        Runnable runnable = new StartShutterRunnable(status, cameraEx);
        sHandlerToMain.post(runnable);
        if (sQueue != null) {
            for (Runnable r : sQueue) {
                sHandlerToMain.post(r);
            }
            sQueue = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class StartShutterRunnable implements Runnable {
        private CameraEx mCameraEx;
        private int mStatus;

        public StartShutterRunnable(int status, CameraEx cameraEx) {
            this.mStatus = status;
            this.mCameraEx = cameraEx;
        }

        @Override // java.lang.Runnable
        public void run() {
            StringBuilder builder = BaseShootingExecutor.getStringBuilder();
            builder.replace(0, builder.length(), "UI :").append("onShutter").append("Runnable").append(": status ").append(this.mStatus);
            Log.d(ShootingExecutor.TAG, builder.toString());
            if (ShootingExecutor.sShutterCb != null) {
                ShootingExecutor.sShutterCb.onShutter(this.mStatus, this.mCameraEx);
            }
        }
    }

    public static void onMyPictureTaken(byte[] jpeg, CameraEx cameraEx) {
        Runnable runnable = new OnPictureTakenRunnable(jpeg, cameraEx);
        sHandlerToMain.post(runnable);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class OnPictureTakenRunnable implements Runnable {
        private CameraEx mCameraEx;
        private byte[] mJpeg;

        public OnPictureTakenRunnable(byte[] jpeg, CameraEx cameraEx) {
            this.mJpeg = jpeg;
            this.mCameraEx = cameraEx;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (ShootingExecutor.sJpegCb != null) {
                ShootingExecutor.sJpegCb.onPictureTaken(this.mJpeg, this.mCameraEx);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void onAutoReviewInfo(CameraEx.ReviewInfo info) {
        Runnable runnable = new AutoReviewInfoRunnable(info);
        sHandlerToMain.post(runnable);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class AutoReviewInfoRunnable implements Runnable {
        private CameraEx.ReviewInfo mInfo;

        public AutoReviewInfoRunnable(CameraEx.ReviewInfo info) {
            this.mInfo = info;
        }

        @Override // java.lang.Runnable
        public void run() {
            CameraNotificationManager notification = CameraNotificationManager.getInstance();
            notification.requestSyncNotify(CameraNotificationManager.PICTURE_REVIEW_INFO, this.mInfo);
        }
    }

    protected static void onMotionShotCompleted(boolean result, CameraEx cameraEx) {
        Runnable runnable = new MotionShotCompleteRunnable(result, cameraEx);
        sHandlerToMain.post(runnable);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class MotionShotCompleteRunnable implements Runnable {
        private CameraEx mCameraEx;
        private boolean mResult;

        public MotionShotCompleteRunnable(boolean result, CameraEx cameraEx) {
            this.mResult = result;
            this.mCameraEx = cameraEx;
        }

        @Override // java.lang.Runnable
        public void run() {
            StringBuilder builder = BaseShootingExecutor.getStringBuilder();
            builder.replace(0, builder.length(), "UI :").append(ShootingExecutor.LOG_ON_MOTIONSHOT_COMPLETED);
            Log.i(ShootingExecutor.TAG, builder.toString());
            CameraNotificationManager.getInstance().requestNotify(CameraNotificationManager.MOTION_SHOT_COMPLETED, Boolean.valueOf(this.mResult));
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void setPictureReviewInfoHist(boolean histogramLayout) {
        sAutoReviewControl.setPictureReviewInfoHist(histogramLayout);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public int getShutterType() {
        return this.mShutterType;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void takePicture() {
        takePicture(2);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void takePicture(int shutterType) {
        sHandlerFromMain.post(new TakePictureRunnable(this, shutterType));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class TakePictureRunnable implements Runnable {
        private ShootingExecutor mExecutor;
        private int mShutterType;

        public TakePictureRunnable(ShootingExecutor executor, int shutterType) {
            this.mExecutor = executor;
            this.mShutterType = shutterType;
        }

        @Override // java.lang.Runnable
        public void run() {
            ShootingExecutor.this.mShutterType = this.mShutterType;
            this.mExecutor.myTakePicture();
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void cancelTakePicture() {
        sHandlerFromMain.post(this.mCancelTakePictureRunnable);
    }

    /* loaded from: classes.dex */
    private static class CancelTakePictureRunnable implements Runnable {
        private ShootingExecutor mExecutor;

        public CancelTakePictureRunnable(ShootingExecutor executor) {
            this.mExecutor = executor;
        }

        @Override // java.lang.Runnable
        public void run() {
            boolean unused = ShootingExecutor.sIsRequestedCancel = true;
            if (!ShootingExecutor.isLockCancelTakePicture) {
                StringBuilder builder = BaseShootingExecutor.getStringBuilder();
                builder.replace(0, builder.length(), ShootingExecutor.LOG_CANCEL_TAKE_PICTURE);
                Log.d(ShootingExecutor.TAG, builder.toString());
                this.mExecutor.myCancelTakePicture();
                boolean unused2 = ShootingExecutor.sIsRequestedCancel = false;
            }
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void lockCancelTakePicture(boolean lock) {
        if (isLockCancelTakePicture != lock) {
            isLockCancelTakePicture = lock;
            if (!lock && sIsRequestedCancel) {
                StringBuilder builder = getStringBuilder();
                builder.replace(0, builder.length(), LOG_CANCEL_TAKE_PICTURE).append(": status ").append("lock");
                Log.d(TAG, builder.toString());
                myCancelTakePicture();
                sIsRequestedCancel = false;
            }
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void startSelfTimerShutter() {
        startSelfTimerShutter(2);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void startSelfTimerShutter(int shutterType) {
        sHandlerFromMain.post(new StartSelfTimerShutterRunnable(this, shutterType));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class StartSelfTimerShutterRunnable implements Runnable {
        private ShootingExecutor mExecutor;
        private int mShutterType;

        public StartSelfTimerShutterRunnable(ShootingExecutor executor, int shutterType) {
            this.mExecutor = executor;
            this.mShutterType = shutterType;
        }

        @Override // java.lang.Runnable
        public void run() {
            ShootingExecutor.this.mShutterType = this.mShutterType;
            this.mExecutor.myStartSelfTimerShutter();
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void cancelSelfTimerShutter() {
        sHandlerFromMain.post(this.mCancelSelfTimerShutterRunnable);
    }

    /* loaded from: classes.dex */
    private static class CancelSelfTimerShutterRunnable implements Runnable {
        private ShootingExecutor mExecutor;

        public CancelSelfTimerShutterRunnable(ShootingExecutor executor) {
            this.mExecutor = executor;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.mExecutor.myCancelSelfTimerShutter();
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void cancelAutoPictureReview() {
        sAutoReviewControl.cancelAutoPictureReview();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void setRecordingMedia(String media, CameraEx.RecordingMediaChangeCallback cb) {
        sRecordingMediaChangeCallback = cb;
        StringBuilder builder = getStringBuilder();
        builder.replace(0, builder.length(), "UI :").append("setRecordingMedia");
        Log.d(TAG, builder.toString());
        sCurrentMedia = media;
        SetRecordingMediaRunnable mSetRecordingMediaRunnable = new SetRecordingMediaRunnable(media);
        synchronized (mediaLockObject) {
            sHandlerFromMain.post(mSetRecordingMediaRunnable);
            try {
                mediaLockObject.wait();
            } catch (Exception e) {
            }
        }
    }

    /* loaded from: classes.dex */
    protected static class SetRecordingMediaRunnable implements Runnable {
        private String mMedia;

        public SetRecordingMediaRunnable(String media) {
            this.mMedia = media;
        }

        @Override // java.lang.Runnable
        public void run() {
            StringBuilder builder = BaseShootingExecutor.getStringBuilder();
            builder.replace(0, builder.length(), "CAMERA :").append("setRecordingMedia");
            Log.d(ShootingExecutor.TAG, builder.toString());
            BaseShootingExecutor.sCameraEx.setRecordingMedia(this.mMedia, ShootingExecutor.sMyRecordingMediaChangeCallback);
        }
    }

    /* loaded from: classes.dex */
    protected static class MyRecordingMediaChangeCallback implements CameraEx.RecordingMediaChangeCallback {
        protected MyRecordingMediaChangeCallback() {
        }

        public void onRecordingMediaChange(CameraEx cameraEx) {
            StringBuilder builder = BaseShootingExecutor.getStringBuilder();
            builder.replace(0, builder.length(), "CAMERA :").append("onRecordingMediaChanged");
            Log.i(ShootingExecutor.TAG, builder.toString());
            BaseShootingExecutor.onDoneRecordingMediaChange(cameraEx);
        }
    }

    protected int[] getPictureReviewSupportedTimes() {
        return sAutoReviewControl.getPictureReviewSupportedTimes();
    }

    protected void setPictureReviewTime(int t) {
        sAutoReviewControl.setPictureReviewTime(t);
    }

    public static int getReviewType() {
        if (3 <= CameraSetting.getPfApiVersion()) {
            return sAutoReviewControl.getReviewType();
        }
        return 0;
    }
}
