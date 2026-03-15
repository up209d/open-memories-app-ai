package com.sony.imaging.app.base.shooting.camera.executor;

import android.hardware.Camera;
import android.util.Log;
import com.sony.scalar.hardware.CameraEx;
import java.util.Random;

/* loaded from: classes.dex */
public class StubExecutor extends ShootingExecutor {
    private static final int AUTO_REVIEW_TIME = 5000;
    private static final String LOG_STOP_PREVIEW = "stopPreview";
    private static final String TAG = "StubExecutor";
    private boolean isSelfTimer = false;

    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void myRelease() {
        terminate();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor
    public void myTakePicture() {
        sCamera.takePicture(null, null, new MyTakePictureCallback());
    }

    /* loaded from: classes.dex */
    private final class MyTakePictureCallback implements Camera.PictureCallback {
        private MyTakePictureCallback() {
        }

        @Override // android.hardware.Camera.PictureCallback
        public void onPictureTaken(byte[] data, Camera camera) {
            StubExecutor.this.onStartShutter(0, BaseShootingExecutor.sCameraEx);
            BaseShootingExecutor.sHandlerToMain.post(new Runnable() { // from class: com.sony.imaging.app.base.shooting.camera.executor.StubExecutor.MyTakePictureCallback.1
                @Override // java.lang.Runnable
                public void run() {
                    StubExecutor.this.startAutoReview();
                }
            });
        }
    }

    protected void startAutoReview() {
        if (AUTO_REVIEW_TIME == 0) {
            onStartPreview(sCameraEx);
            return;
        }
        onStartPictureReview(sCameraEx);
        CameraEx.ReviewInfo info = new CameraEx.ReviewInfo();
        onAutoReviewInfo(info);
        sHandlerToMain.postDelayed(new Runnable() { // from class: com.sony.imaging.app.base.shooting.camera.executor.StubExecutor.1
            @Override // java.lang.Runnable
            public void run() {
                BaseShootingExecutor.onStartPreview(BaseShootingExecutor.sCameraEx);
            }
        }, AUTO_REVIEW_TIME);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor
    public void myStartSelfTimerShutter() {
        this.isSelfTimer = true;
        sHandlerToMain.postDelayed(new Runnable() { // from class: com.sony.imaging.app.base.shooting.camera.executor.StubExecutor.2
            @Override // java.lang.Runnable
            public void run() {
                if (StubExecutor.this.isSelfTimer) {
                    BaseShootingExecutor.sCamera.takePicture(null, null, new MyTakePictureCallback());
                }
            }
        }, 10000L);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor
    public void myCancelSelfTimerShutter() {
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void autoFocus(Camera.AutoFocusCallback cb) {
        super.autoFocus(cb);
        onStartAutoFocus();
        sHandlerToMain.postDelayed(new Runnable() { // from class: com.sony.imaging.app.base.shooting.camera.executor.StubExecutor.3
            @Override // java.lang.Runnable
            public void run() {
                Random random = new Random();
                int num = random.nextInt(5) + 1;
                int[] areas = new int[num];
                for (int i = 0; i < num; i++) {
                    areas[i] = random.nextInt(10);
                }
                BaseShootingExecutor.onDoneAutoFocus(1, areas);
            }
        }, 1000L);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void cancelAutoFocus() {
        super.cancelAutoFocus();
        sHandlerToMain.post(new Runnable() { // from class: com.sony.imaging.app.base.shooting.camera.executor.StubExecutor.4
            @Override // java.lang.Runnable
            public void run() {
                BaseShootingExecutor.onDoneAutoFocus(0, null);
            }
        });
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void inquireKey(int key) {
        sendKey(key, true);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor
    public void myCancelTakePicture() {
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void stopPreview() {
        Log.d(TAG, LOG_STOP_PREVIEW);
        sCamera.stopPreview();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor
    protected CameraEx.ShutterListener getShutterListener() {
        return null;
    }
}
