package com.sony.imaging.app.srctrl.shooting.state;

import android.os.SystemClock;
import android.util.Log;
import com.sony.imaging.app.base.caution.CautionProcessingFunction;
import com.sony.imaging.app.base.caution.IkeyDispatchEach;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;
import com.sony.imaging.app.base.shooting.camera.PictureQualityController;
import com.sony.imaging.app.srctrl.liveview.LiveviewLoader;
import com.sony.imaging.app.srctrl.shooting.SRCtrlExecutorCreator;
import com.sony.imaging.app.srctrl.shooting.camera.SRCtrlPictureQualityController;
import com.sony.imaging.app.srctrl.util.SRCtrlEnvironment;
import com.sony.imaging.app.srctrl.util.StateController;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationContShootingMode;
import com.sony.imaging.app.srctrl.webapi.availability.ParamsGenerator;
import com.sony.imaging.app.srctrl.webapi.specific.ServerEventHandler;
import com.sony.imaging.app.srctrl.webapi.specific.ShutterListenerNotifier;
import com.sony.scalar.media.MediaInfo;
import com.sony.scalar.provider.AvindexStore;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
public abstract class CaptureStateUtil {
    public static boolean remote_shooting_mode;
    BulbCapturingTimeNotify captureTimeNotify = null;
    protected ShutterListenerNotifier notifier;
    private static final String TAG = CaptureStateUtil.class.getSimpleName();
    public static final String KEY_NUMOFBURSTPICTURES = CaptureStateUtil.class.getName() + "_NUMOFBURSTPICTURES";
    public static final String KEY_NUMOFFINISHEDPICTURES = CaptureStateUtil.class.getName() + "_FINISHEDPICTURES";
    public static final String KEY_USEFINALPICTURE = CaptureStateUtil.class.getName() + "_USEFINALPICTURE";
    private static CaptureStateUtil sUtil = null;
    public static boolean mIsNoCard = true;

    public abstract void init();

    public abstract void startCapture();

    public abstract void term();

    public static CaptureStateUtil getUtil() {
        if (sUtil == null) {
            if (SRCtrlEnvironment.getInstance().isEnableContinuousShooting()) {
                sUtil = new CaptureStateUtilContShootingSupported();
            } else {
                sUtil = new CaptureStateUtilSingleShootingOnly();
            }
        }
        return sUtil;
    }

    public static boolean isSingleShooting() {
        return !isContinuousShooting();
    }

    public static boolean isBlibShootingMode() {
        String shtterSpeed;
        return isSingleShooting() && ParamsGenerator.peekShutterSpeedSnapshot() != null && (shtterSpeed = ParamsGenerator.peekShutterSpeedSnapshot().currentShutterSpeed) != null && "BULB".equals(shtterSpeed);
    }

    public static boolean isContinuousShooting() {
        return CameraOperationContShootingMode.enableContShootingMode();
    }

    public static boolean isSelfTimer() {
        return DriveModeController.getInstance().isSelfTimer();
    }

    public static boolean isExternalMediaMounted() {
        String mediaId = SRCtrlExecutorCreator.getRecordingMedia();
        if (mediaId == null) {
            return false;
        }
        MediaInfo mMediaInfo = AvindexStore.getMediaInfo(mediaId);
        int mMediaType = mMediaInfo.getMediaType();
        return mMediaType == 2 || mMediaType == 1;
    }

    /* loaded from: classes.dex */
    private class BulbCapturingTimeNotify extends Thread {
        private boolean halt_;

        private BulbCapturingTimeNotify() {
            this.halt_ = false;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            this.halt_ = false;
            long startPoint = SystemClock.uptimeMillis();
            while (!this.halt_) {
                StateController.AppCondition appCondition = StateController.getInstance().getAppCondition();
                if (StateController.AppCondition.SHOOTING_LOCAL.equals(appCondition) || StateController.AppCondition.SHOOTING_REMOTE.equals(appCondition)) {
                    long captureTime = TimeUnit.SECONDS.convert(SystemClock.uptimeMillis() - startPoint, TimeUnit.MILLISECONDS);
                    Log.v(CaptureStateUtil.TAG, "captime = " + captureTime);
                    if (ParamsGenerator.updateBulbCapturingTimeParams((int) captureTime)) {
                        ServerEventHandler.getInstance().onServerStatusChanged();
                    }
                    try {
                        Thread.sleep(200L);
                    } catch (InterruptedException e) {
                        Log.v(CaptureStateUtil.TAG, "captime end!!");
                        e.printStackTrace();
                        return;
                    }
                } else {
                    return;
                }
            }
        }

        public void halt() {
            this.halt_ = true;
            interrupt();
        }
    }

    public void onBulbShuterStart() {
        Log.v(TAG, "onBulbShuterStart");
        if (this.captureTimeNotify != null) {
            Log.e(TAG, "captureTimeNotify is not null.");
            this.captureTimeNotify.halt();
            this.captureTimeNotify = null;
        }
        this.captureTimeNotify = new BulbCapturingTimeNotify();
        this.captureTimeNotify.start();
        Thread liveviewStopThread = new Thread(new Runnable() { // from class: com.sony.imaging.app.srctrl.shooting.state.CaptureStateUtil.1
            @Override // java.lang.Runnable
            public void run() {
                Log.v(CaptureStateUtil.TAG, "LiveviewStop:start!");
                LiveviewLoader.stopObtainingImages();
                Log.v(CaptureStateUtil.TAG, "LiveviewStop:end!");
            }
        }, "LiveviewStopThread");
        liveviewStopThread.start();
    }

    public void onBulbShuterEnd() {
        Log.v(TAG, "onBulbShuterEnd");
    }

    public void setNotifier(ShutterListenerNotifier notifier) {
        this.notifier = notifier;
        if (notifier != null) {
            remote_shooting_mode = true;
        } else {
            remote_shooting_mode = false;
        }
    }

    public ShutterListenerNotifier getNotifier() {
        return this.notifier;
    }

    public boolean isReadyToTakePicture() {
        if (isContinuousShooting() && !MediaNotificationManager.getInstance().isMounted()) {
            Log.v(TAG, "Force canceling taking a [Continuous Shooting] because media isn't mounted");
            return false;
        }
        if (CameraSetting.getPfApiVersion() < 2) {
            try {
                String quality = SRCtrlPictureQualityController.getInstance().getValue(null);
                if (!MediaNotificationManager.getInstance().isMounted() && PictureQualityController.PICTURE_QUALITY_RAWJPEG.equals(quality)) {
                    Log.v(TAG, "Force canceling taking a RAW-JPEG picture because media isn't mounted");
                    return false;
                }
            } catch (IController.NotSupportedException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /* loaded from: classes.dex */
    public static class SRCtrlKeyDispatchForDLT06 extends IkeyDispatchEach {
        public SRCtrlKeyDispatchForDLT06(CautionProcessingFunction p) {
            super(p, null);
        }

        @Override // com.sony.imaging.app.base.caution.IkeyDispatchEach, com.sony.imaging.app.fw.BaseKeyHandler
        protected String getKeyConvCategory() {
            return "Menu";
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedCenterKey() {
            CautionProcessingFunction.executeTerminate();
            return 1;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedS1Key() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedS2Key() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedPlayBackKey() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedMovieRecKey() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedModeDial() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int attachedLens() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int detachedLens() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedS1Key() {
            return 0;
        }
    }
}
