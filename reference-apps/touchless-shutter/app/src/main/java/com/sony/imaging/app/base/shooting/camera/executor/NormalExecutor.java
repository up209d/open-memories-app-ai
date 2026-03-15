package com.sony.imaging.app.base.shooting.camera.executor;

import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor;
import com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class NormalExecutor extends ShootingExecutor {
    private static final String LOG_CANCEL_SELF_TIMER = "cancelSelfTimer";
    private static final String LOG_CANCEL_TAKE_PICTURE = "cancelTakePicture";
    private static final String LOG_ON_SHUTTER = "onShutter";
    private static final String LOG_STATUS = ": status ";
    private static final StringBuilder STRBUILD = new StringBuilder();
    private static final String TAG = "NormalExecutor";
    private NormalShutterCb mShutterCb;
    protected boolean mTempRemote;
    protected int mTempRemoteStatus = 0;

    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void myRelease() {
        tryRelease();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor, com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void prepare(Camera camera, CameraEx cameraEx) {
        this.mShutterCb = new NormalShutterCb(cameraEx, this.mAdapter);
        super.prepare(camera, cameraEx);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor, com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void terminate() {
        super.terminate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class NormalShutterCb extends ShootingExecutor.MyShutterCb {
        IAdapter mAdapter;
        CameraEx mCameraEx;

        public NormalShutterCb(CameraEx cameraEx, IAdapter adapter) {
            this.mAdapter = null;
            this.mCameraEx = null;
            this.mCameraEx = cameraEx;
            this.mAdapter = adapter;
        }

        @Override // com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor.MyShutterCb
        public void onShutter(int status, CameraEx cameraEx) {
            super.onShutter(status, cameraEx);
            Log.d(NormalExecutor.TAG, NormalExecutor.LOG_ON_SHUTTER);
            int type = NormalExecutor.this.getShutterType();
            if (status != 0) {
                BaseShootingExecutor.isBulbOpened = false;
                NormalExecutor.this.updateFocusLock();
            }
            if (1 == type && !BaseShootingExecutor.isBulbOpened) {
                this.mCameraEx.cancelTakePicture();
            }
            this.mAdapter.onShutter(status, cameraEx);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor
    public void myTakePicture() {
        int type = getShutterType();
        if (2 == type) {
            setTempRemoteControl(1);
        }
        enableTermination(false);
        this.mAdapter.takePicture();
        if (CameraSetting.getInstance().isShutterSpeedBulb()) {
            isBulbOpened = true;
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor
    public void myCancelTakePicture() {
        Log.d(TAG, LOG_CANCEL_TAKE_PICTURE);
        sCameraEx.cancelTakePicture();
        if (isBulbOpened) {
            isBulbOpened = false;
            updateFocusLock();
        }
        int type = getShutterType();
        if (2 == type) {
            setTempRemoteControl(0);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor
    public void myStartSelfTimerShutter() {
        int type = getShutterType();
        if (2 == type) {
            setTempRemoteControl(1);
        }
        enableTermination(false);
        ((ICaptureAdapater) this.mAdapter).startSelfTimerShutter();
        if (CameraSetting.getInstance().isShutterSpeedBulb()) {
            isBulbOpened = true;
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor
    public void myCancelSelfTimerShutter() {
        Log.d(TAG, LOG_CANCEL_TAKE_PICTURE);
        sCameraEx.cancelTakePicture();
        boolean status = sCameraEx.cancelSelfTimerShutter();
        STRBUILD.replace(0, STRBUILD.length(), LOG_CANCEL_SELF_TIMER).append(LOG_STATUS).append(status);
        Log.i(TAG, STRBUILD.toString());
        if (status) {
            CameraEx.ShutterListener listener = getShutterListener();
            listener.onShutter(1, sCameraEx);
        }
        if (isBulbOpened) {
            isBulbOpened = false;
            updateFocusLock();
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void inquireKey(int key) {
        sendKey(key, true);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void stopPreview() {
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor
    protected CameraEx.ShutterListener getShutterListener() {
        return this.mShutterCb;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public int getTempRemoteControl() {
        return this.mTempRemoteStatus;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public boolean getOriginalRemoteControl() {
        return this.mTempRemote;
    }

    protected void setTempRemoteControl(int status) {
        CameraSetting camSet = CameraSetting.getInstance();
        this.mTempRemoteStatus = status;
        if (1 == status) {
            CameraEx.ParametersModifier currentParams = (CameraEx.ParametersModifier) camSet.getParameters().second;
            this.mTempRemote = currentParams.getRemoteControlMode();
            if (true == this.mTempRemote) {
                Pair<Camera.Parameters, CameraEx.ParametersModifier> setParams = camSet.getEmptyParameters();
                ((CameraEx.ParametersModifier) setParams.second).setRemoteControlMode(false);
                sCamera.setParameters((Camera.Parameters) setParams.first);
                return;
            }
            return;
        }
        if (true == this.mTempRemote) {
            Pair<Camera.Parameters, CameraEx.ParametersModifier> setParams2 = camSet.getEmptyParameters();
            ((CameraEx.ParametersModifier) setParams2.second).setRemoteControlMode(true);
            sCamera.setParameters((Camera.Parameters) setParams2.first);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    protected BaseShootingExecutor.AutoFocusRunnable getAutoFocusRunnable() {
        return new NormalAutoFocusRunnable();
    }

    /* loaded from: classes.dex */
    protected static class NormalAutoFocusRunnable extends BaseShootingExecutor.AutoFocusRunnable {
        protected NormalAutoFocusRunnable() {
        }

        @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor.AutoFocusRunnable, java.lang.Runnable
        public void run() {
            BaseShootingExecutor.sIsAutoFocusRequested = true;
            if (!BaseShootingExecutor.isFocusLocked) {
                Log.d(NormalExecutor.TAG, "autoFocus : " + BaseShootingExecutor.sIsAutoFocusRequested);
                synchronized (BaseShootingExecutor.sAFBehaviorLock) {
                    if (BaseShootingExecutor.sRunningAutoFocusBehavior != null && BaseShootingExecutor.sRunningAutoFocusBehavior != BaseShootingExecutor.sAutoFocusBehavior) {
                        if (BaseShootingExecutor.sRunningAutoFocusBehavior == null || "".equals(BaseShootingExecutor.sRunningAutoFocusBehavior)) {
                            BaseShootingExecutor.sCamera.cancelAutoFocus();
                        } else {
                            BaseShootingExecutor.sCameraEx.resetQuickAutoFocus(BaseShootingExecutor.sRunningAutoFocusBehavior);
                        }
                    }
                    BaseShootingExecutor.sRunningAutoFocusBehavior = BaseShootingExecutor.sAutoFocusBehavior;
                    if (BaseShootingExecutor.sAutoFocusBehavior == null || "".equals(BaseShootingExecutor.sAutoFocusBehavior)) {
                        BaseShootingExecutor.sCamera.autoFocus(BaseShootingExecutor.sMyAutoFocusCb);
                    } else {
                        BaseShootingExecutor.sCameraEx.setQuickAutoFocus(BaseShootingExecutor.sAutoFocusBehavior);
                    }
                }
            }
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    protected BaseShootingExecutor.CancelAutoFocusRunnable getCancelAutoFocusRunnable() {
        return new NormalCancelAutoFocusRunnable();
    }

    /* loaded from: classes.dex */
    protected static class NormalCancelAutoFocusRunnable extends BaseShootingExecutor.CancelAutoFocusRunnable {
        protected NormalCancelAutoFocusRunnable() {
        }

        @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor.CancelAutoFocusRunnable, java.lang.Runnable
        public void run() {
            BaseShootingExecutor.sIsAutoFocusRequested = false;
            if (!BaseShootingExecutor.isFocusLocked && !BaseShootingExecutor.isBulbOpened) {
                Log.d(NormalExecutor.TAG, "cancelAutoFocus : " + BaseShootingExecutor.sIsAutoFocusRequested);
                if (BaseShootingExecutor.sRunningAutoFocusBehavior == null || "".equals(BaseShootingExecutor.sRunningAutoFocusBehavior)) {
                    BaseShootingExecutor.sCamera.cancelAutoFocus();
                } else {
                    BaseShootingExecutor.sCameraEx.resetQuickAutoFocus(BaseShootingExecutor.sRunningAutoFocusBehavior);
                }
                BaseShootingExecutor.sRunningAutoFocusBehavior = null;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void lockAutoFocus(boolean lock) {
        if (isFocusLocked != lock) {
            isFocusLocked = lock;
            if (!lock && !isBulbOpened) {
                StringBuilder builder = getStringBuilder();
                builder.replace(0, builder.length(), "cancelAutoFocus").append(LOG_STATUS).append(sIsAutoFocusRequested);
                Log.d(TAG, builder.toString());
                if (!sIsAutoFocusRequested) {
                    builder.replace(0, builder.length(), "cancelAutoFocus").append(LOG_STATUS).append("lock");
                    Log.d(TAG, builder.toString());
                    if (sRunningAutoFocusBehavior == null || "".equals(sRunningAutoFocusBehavior)) {
                        sCamera.cancelAutoFocus();
                    } else {
                        sCameraEx.resetQuickAutoFocus(sRunningAutoFocusBehavior);
                    }
                    sRunningAutoFocusBehavior = null;
                }
            }
        }
    }

    protected void updateFocusLock() {
        if (!isFocusLocked && !sIsAutoFocusRequested) {
            StringBuilder builder = getStringBuilder();
            builder.replace(0, builder.length(), "cancelAutoFocus").append(LOG_STATUS).append("lock");
            Log.d(TAG, builder.toString());
            if (sRunningAutoFocusBehavior == null || "".equals(sRunningAutoFocusBehavior)) {
                sCamera.cancelAutoFocus();
            } else {
                sCameraEx.resetQuickAutoFocus(sRunningAutoFocusBehavior);
            }
            sRunningAutoFocusBehavior = null;
        }
    }
}
