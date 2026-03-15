package com.sony.imaging.app.srctrl.shooting.state;

import android.app.Activity;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.menu.MenuTable;
import com.sony.imaging.app.base.shooting.ShootingState;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.DigitalZoomController;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.FlashController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.srctrl.liveview.LiveviewLoader;
import com.sony.imaging.app.srctrl.shooting.SRCtrlExecutorCreator;
import com.sony.imaging.app.srctrl.util.MediaObserver;
import com.sony.imaging.app.srctrl.util.MediaObserverAggregator;
import com.sony.imaging.app.srctrl.util.SRCtrlConstants;
import com.sony.imaging.app.srctrl.util.SRCtrlEnvironment;
import com.sony.imaging.app.srctrl.util.StateController;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationHalfPressShutter;
import com.sony.imaging.app.srctrl.webapi.availability.ParamsGenerator;
import com.sony.imaging.app.srctrl.webapi.definition.ResponseParams;
import com.sony.imaging.app.srctrl.webapi.servlet.ContinuousResourceLoader;
import com.sony.imaging.app.srctrl.webapi.servlet.SingleResourceLoader;
import com.sony.imaging.app.srctrl.webapi.specific.ServerEventHandler;
import com.sony.imaging.app.srctrl.webapi.specific.ShootingHandler;
import com.sony.imaging.app.util.AppInfo;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class ShootingStateEx extends ShootingState {
    private static final String tag = ShootingStateEx.class.getName();
    private MessageQueue.IdleHandler mCameraParameterNotifier;
    private MediaObserverAggregator mMediaObservers;
    private MessageQueue.IdleHandler mMenuTreeCreator = new MessageQueue.IdleHandler() { // from class: com.sony.imaging.app.srctrl.shooting.state.ShootingStateEx.1
        @Override // android.os.MessageQueue.IdleHandler
        public boolean queueIdle() {
            MenuTable.getInstance().createDefaultMenuTree(ShootingStateEx.this.getActivity());
            return false;
        }
    };
    private NotificationListener mRecModeChangeNotificationListener = new NotificationListener() { // from class: com.sony.imaging.app.srctrl.shooting.state.ShootingStateEx.3
        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return new String[]{CameraNotificationManager.REC_MODE_CHANGED};
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tags) {
            if (ExecutorCreator.getInstance().getRecordingMode() == 2) {
                MediaObserver.setCanCount(false);
            } else {
                MediaObserver.setCanCount(true);
            }
        }
    };

    /* loaded from: classes.dex */
    class ExecutorInitThread extends Thread {
        ExecutorInitThread() {
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            SRCtrlExecutorCreator.getInstance().init();
        }
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        ParamsGenerator.startCameraSettingListener();
        LiveviewLoader.clean();
        StateController stateController = StateController.getInstance();
        stateController.setState(this);
        ParamsGenerator.updateCameraFunctionParams(SRCtrlConstants.CAMERA_FUNCTION_REMOTE_SHOOTING, SRCtrlConstants.CAMERA_FUNCTION_CANDIDATES);
        int recMode = ExecutorCreator.getInstance().getRecordingMode();
        if (recMode == 2) {
            stateController.setAppCondition(StateController.AppCondition.SHOOTING_MOVIE_INHIBIT);
        } else {
            stateController.setAppCondition(StateController.AppCondition.SHOOTING_INHIBIT);
        }
        stateController.setHasModeDial(ModeDialDetector.hasModeDial());
        super.onResume();
        if (SRCtrlEnvironment.getInstance().isIrisRingInvalid()) {
            SRCtrlExecutorCreator.getInstance().getSequence().getCameraEx().disableIrisRing();
        }
        CameraNotificationManager.getInstance().setNotificationListener(this.mRecModeChangeNotificationListener);
        if (ExecutorCreator.getInstance().getRecordingMode() == 2) {
            MediaObserver.setCanCount(false);
        } else {
            MediaObserver.setCanCount(true);
        }
        CaptureStateUtil.getUtil().init();
        StateController sc = StateController.getInstance();
        sc.setDuringCameraSetupRoutine(true);
        Activity activity = getActivity();
        AppInfo.notifyAppInfo(activity.getApplicationContext(), activity.getPackageName(), activity.getClass().getName(), AppInfo.LARGE_CATEGORY_REC, AppInfo.SMALL_CATEGORY_STILL, BaseApp.PULLING_BACK_KEYS_FOR_SHOOTING, BaseApp.RESUME_KEYS_FOR_SHOOTING);
        this.mCameraParameterNotifier = new MessageQueue.IdleHandler() { // from class: com.sony.imaging.app.srctrl.shooting.state.ShootingStateEx.2
            private boolean mExposureModeNotified = false;
            private boolean mFocusModeNotified = false;
            private boolean mSelfTimerNotified = false;
            private boolean mExposureCompensationNotified = false;
            private boolean mFlashModeNotified = false;
            private boolean mZoomInformationNotified = false;
            private boolean mZoomSettingNotified = false;

            @Override // android.os.MessageQueue.IdleHandler
            public boolean queueIdle() {
                CameraNotificationManager manager = CameraNotificationManager.getInstance();
                if (!this.mExposureModeNotified) {
                    ExposureModeController emc = ExposureModeController.getInstance();
                    String modeInBase = emc.getValue(ExposureModeController.EXPOSURE_MODE);
                    if (modeInBase == null) {
                        Log.v(ShootingStateEx.tag, "Exposure Mode was not initilaized...  Try again.");
                    } else {
                        manager.requestNotify(CameraNotificationManager.SCENE_MODE);
                        this.mExposureModeNotified = true;
                    }
                }
                if (!this.mFocusModeNotified) {
                    FocusModeController fmc = FocusModeController.getInstance();
                    String focusModeInBase = fmc.getValue();
                    if (focusModeInBase == null) {
                        Log.v(ShootingStateEx.tag, "Focus Mode was not initialized yet... Try again.");
                    } else {
                        manager.requestNotify(CameraNotificationManager.FOCUS_CHANGE);
                        this.mFocusModeNotified = true;
                    }
                }
                if (!this.mSelfTimerNotified) {
                    int recMode2 = ExecutorCreator.getInstance().getRecordingMode();
                    if (recMode2 == 2) {
                        this.mSelfTimerNotified = true;
                    } else {
                        DriveModeController dmc = DriveModeController.getInstance();
                        String value = dmc.getValue(DriveModeController.SELF_TIMER);
                        if (value == null) {
                            Log.v(ShootingStateEx.tag, "Selftimer was not initialized yet... Try again.");
                        } else {
                            manager.requestNotify(CameraNotificationManager.SELFTIMER_COUNTDOWN_STATUS);
                            this.mSelfTimerNotified = true;
                        }
                    }
                }
                if (!this.mExposureCompensationNotified) {
                    ExposureCompensationController ecc = ExposureCompensationController.getInstance();
                    String value2 = null;
                    try {
                        value2 = ecc.getValue(null);
                    } catch (Exception e) {
                    }
                    if (value2 == null) {
                        Log.v(ShootingStateEx.tag, "ExposureCompensation was not initialized yet... Try again.");
                    } else {
                        manager.requestNotify("ExposureCompensation");
                        this.mExposureCompensationNotified = true;
                    }
                }
                if (!this.mFlashModeNotified) {
                    int recMode3 = ExecutorCreator.getInstance().getRecordingMode();
                    if (recMode3 == 2) {
                        this.mFlashModeNotified = true;
                    } else {
                        FlashController fmc2 = FlashController.getInstance();
                        String value3 = fmc2.getValue(FlashController.FLASHMODE);
                        if (value3 == null) {
                            Log.v(ShootingStateEx.tag, "Flash Mode was not initialized yet... Try again.");
                        } else {
                            manager.requestNotify(CameraNotificationManager.FLASH_CHANGE);
                            this.mFlashModeNotified = true;
                        }
                    }
                }
                if (!this.mZoomInformationNotified) {
                    DigitalZoomController dzc = DigitalZoomController.getInstance();
                    int optPositon = dzc.getOpticalZoomPosition();
                    int digPositon = dzc.getDigitalZoomPosition();
                    if (optPositon == -1 && digPositon == -1) {
                        Log.v(ShootingStateEx.tag, "Zoom Information was not initialized yet... Try again.");
                    } else {
                        manager.requestNotify(CameraNotificationManager.ZOOM_INFO_CHANGED);
                        this.mZoomInformationNotified = true;
                    }
                }
                if (!this.mZoomSettingNotified) {
                    String value4 = DigitalZoomController.getInstance().getValue(DigitalZoomController.TAG_DIGITAL_ZOOM_TYPE);
                    if (value4 == null) {
                        Log.v(ShootingStateEx.tag, "Zoom setting was not initialized yet... Try again.");
                    } else {
                        manager.requestNotify(CameraNotificationManager.DIGITAL_ZOOM_MODE_CHANGED);
                        this.mZoomSettingNotified = true;
                        Log.v(ShootingStateEx.tag, "Zoom setting initialized end.");
                    }
                }
                return (this.mExposureModeNotified && this.mFocusModeNotified && this.mSelfTimerNotified && this.mExposureCompensationNotified && this.mFlashModeNotified && this.mZoomInformationNotified && this.mZoomSettingNotified) ? false : true;
            }
        };
        Looper.myQueue().addIdleHandler(this.mCameraParameterNotifier);
        Looper.myQueue().addIdleHandler(this.mMenuTreeCreator);
        this.mMediaObservers = new MediaObserverAggregator();
        this.mMediaObservers.start(activity, getHandler());
        ShootingHandler.getInstance().setMediaObserverAggregator(this.mMediaObservers);
        MediaNotificationManager.getInstance().updateRemainingAmount();
        if (SRCtrlEnvironment.getInstance().isEnableContinuousShooting()) {
            SingleResourceLoader.getInstance().initialize();
            ContinuousResourceLoader.getInstance().initialize();
        }
        SRCtrlEnvironment.getInstance().setS1AfDisable(AvailableInfo.isFactor("INH_FACTOR_CAM_SET_S1_AF_OFF_TYPE_P"));
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        Looper.myQueue().removeIdleHandler(this.mCameraParameterNotifier);
        this.mCameraParameterNotifier = null;
        Looper.myQueue().removeIdleHandler(this.mMenuTreeCreator);
        ParamsGenerator.stopCameraSettingListener();
        ShootingHandler.getInstance().setMediaObserverAggregator(null);
        ShootingExecutor.setJpegListener(null);
        this.mMediaObservers.stop();
        this.mMediaObservers = null;
        CautionUtilityClass.getInstance().executeTerminate();
        StateController.getInstance().setAppCondition(StateController.AppCondition.PREPARATION);
        ParamsGenerator.updateAvailableApiList();
        LiveviewLoader.clean();
        if (SRCtrlEnvironment.getInstance().isIrisRingInvalid()) {
            SRCtrlExecutorCreator.getInstance().getSequence().getCameraEx().enableIrisRing();
        }
        if (SRCtrlEnvironment.getInstance().isEnableContinuousShooting()) {
            SingleResourceLoader.getInstance().terminate();
            ContinuousResourceLoader.getInstance().terminate();
        }
        CaptureStateUtil.getUtil().term();
        super.onPause();
        boolean toBeNotified = ParamsGenerator.updateFocusStatusParams(CameraOperationHalfPressShutter.FOCUS_STATUS_NOT_FORCUSING);
        if (toBeNotified) {
            ServerEventHandler.getInstance().onServerStatusChanged();
        }
        if (!Environment.isCancelExposureSupported) {
            ParamsGenerator.removeContinuousErrorParams(ResponseParams.ERROR_STRING_LONG_EXPOSURE_NOISE_REDUCTION_NOT_ACTIVATED);
        }
    }
}
