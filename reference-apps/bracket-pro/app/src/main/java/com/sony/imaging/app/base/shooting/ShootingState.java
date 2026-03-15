package com.sony.imaging.app.base.shooting;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;
import android.view.SurfaceHolder;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.shooting.camera.AELController;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor;
import com.sony.imaging.app.base.shooting.layout.ShootingLayout;
import com.sony.imaging.app.bracketpro.menu.layout.BracketMasterSubMenu;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.HoldKeyServer;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.fw.StateHandle;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.HDMIInfoWrapper;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.provider.AvindexStore;

/* loaded from: classes.dex */
public class ShootingState extends StateBase implements ShootingLayout.HolderListener {
    private static final String FORCE_SETTING_STATE = "FORCESETTING";
    private static final String LOG_CALL_CANCEL_TAKE_PICTURE_ON_MEDIA_CHANGED = "cancelTakePicture on media changed";
    private static final String LOG_ON_RECORDING_MEDIA_CHANGED = "onRecordingMediaChanged :";
    protected static final int PF_VER_CANCEL_TAKEPICTURE_ON_MEDIA_CHANGED = 8;
    private static final String TAG = "ShootingState";
    private Callback mHolderCallback;
    private NotificationListener mRecModeChangedListener;
    private ShootingIdleHandler mShootingIdleHandler;
    private StateHandle mStateHandle;
    private NotificationListener mediaStatusChangedListener;
    private CameraEx.MotionShotResultListener motionshotResultListener;
    private static int mSetRecordingMediaCount = 0;
    private static boolean mIsMediaChecking = false;

    public ShootingState() {
        this.mShootingIdleHandler = new ShootingIdleHandler();
        this.mediaStatusChangedListener = new MyNotificationListener();
        this.mRecModeChangedListener = new RecModeChangedListener();
    }

    static /* synthetic */ int access$508() {
        int i = mSetRecordingMediaCount;
        mSetRecordingMediaCount = i + 1;
        return i;
    }

    static /* synthetic */ int access$510() {
        int i = mSetRecordingMediaCount;
        mSetRecordingMediaCount = i - 1;
        return i;
    }

    /* loaded from: classes.dex */
    class Callback implements SurfaceHolder.Callback {
        Callback() {
        }

        @Override // android.view.SurfaceHolder.Callback
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                BaseShootingExecutor executor = ExecutorCreator.getInstance().getSequence();
                executor.setPreviewDisplay(holder);
            } catch (Exception e) {
                e.printStackTrace();
                Activity appRoot = ShootingState.this.getActivity();
                appRoot.finish();
            }
        }

        @Override // android.view.SurfaceHolder.Callback
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        @Override // android.view.SurfaceHolder.Callback
        public void surfaceDestroyed(SurfaceHolder holder) {
        }
    }

    @Override // com.sony.imaging.app.fw.ContainerState, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsMediaChecking = false;
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        ExecutorCreator creator = ExecutorCreator.getInstance();
        BaseApp base = (BaseApp) getActivity();
        base.selectShootingMode();
        creator.init();
        BaseShootingExecutor executor = ExecutorCreator.getInstance().getSequence();
        creator.setMessageReceiver(this);
        MediaNotificationManager.getInstance().setNotificationListener(this.mediaStatusChangedListener);
        CameraNotificationManager.getInstance().setNotificationListener(this.mRecModeChangedListener);
        this.mRecModeChangedListener.onNotify(CameraNotificationManager.REC_MODE_CHANGED);
        if (2 == Environment.DEVICE_TYPE || 4 == Environment.DEVICE_TYPE) {
            CameraEx cameraEx = CameraSetting.getInstance().getCamera();
            Camera camera = cameraEx.getNormalCamera();
            Camera.Parameters params = camera.getParameters();
            CameraEx.ParametersModifier modifier = cameraEx.createParametersModifier(params);
            if (params.getSceneMode() == null) {
                params.setSceneMode(BracketMasterSubMenu.MANUAL_EXPOSURE);
            }
            if (modifier.getDROMode() == null) {
                modifier.setDROMode("auto");
            }
            if (modifier.getHDRMode() == null) {
                modifier.setHDRMode("auto");
            }
            camera.setParameters(params);
        }
        ShootingLayout layout = (ShootingLayout) Layout.getInstance(ShootingLayout.class);
        if (layout != null) {
            layout.setHolderType(executor.getHolderType());
            this.mHolderCallback = new Callback();
            layout.setHolderListener(this);
        }
        this.mStateHandle = addChildState(getNextState(), this.data);
        Looper.myQueue().addIdleHandler(this.mShootingIdleHandler);
        mSetRecordingMediaCount = 0;
        if (Environment.isNewBizDeviceActionCam() && Environment.hasSubLcd() && 9 <= CameraSetting.getPfApiVersion()) {
            this.motionshotResultListener = new MyMotionShotResultListener();
            ShootingExecutor.setMotionShotResultListener(this.motionshotResultListener);
        }
    }

    /* loaded from: classes.dex */
    private static class ShootingIdleHandler implements MessageQueue.IdleHandler {
        private ShootingIdleHandler() {
        }

        @Override // android.os.MessageQueue.IdleHandler
        public boolean queueIdle() {
            MediaNotificationManager.getInstance().updateRemainingAmount();
            MediaNotificationManager.getInstance().notifyRemainingAmountChange();
            CameraSetting.getInstance().initListeners();
            return false;
        }
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        SurfaceHolder holder;
        if (Environment.isNewBizDeviceActionCam() && Environment.hasSubLcd() && 9 <= CameraSetting.getPfApiVersion()) {
            ShootingExecutor.setMotionShotResultListener(null);
        }
        super.onPause();
        ShootingLayout layout = (ShootingLayout) Layout.getInstance(ShootingLayout.class);
        if (layout != null && (holder = layout.getHolder()) != null) {
            holder.removeCallback(this.mHolderCallback);
        }
        AELController.getInstance().cancelAELock();
        FocusModeController focusModeController = FocusModeController.getInstance();
        if (focusModeController.isFocusControl()) {
            FocusModeController.keepToggleFocusModeFlag(true);
        }
        focusModeController.cancelFocusControl();
        ExecutorCreator creator = ExecutorCreator.getInstance();
        BaseShootingExecutor executor = creator.getSequence();
        CameraNotificationManager.getInstance().removeNotificationListener(this.mRecModeChangedListener);
        MediaNotificationManager.getInstance().removeNotificationListener(this.mediaStatusChangedListener);
        if (executor instanceof ShootingExecutor) {
            ((ShootingExecutor) executor).cancelTakePicture();
        }
        executor.stopPreview();
        creator.halt();
        CameraNotificationManager.getInstance().clear();
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED, 3);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.SLIDE_AF_MF, 3);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.SLIDE_AEL_FOCUS_CHANGED, 3);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.IRIS_DIAL_CHANGED, 3);
        Looper.myQueue().removeIdleHandler(this.mShootingIdleHandler);
    }

    protected String getNextState() {
        return FORCE_SETTING_STATE;
    }

    @Override // com.sony.imaging.app.base.shooting.layout.ShootingLayout.HolderListener
    public void onStateChanged(int state, SurfaceHolder holder) {
        if (holder != null) {
            if (state == 1) {
                holder.addCallback(this.mHolderCallback);
            } else {
                holder.removeCallback(this.mHolderCallback);
            }
        }
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase
    protected int getMyBeepPattern() {
        return 6;
    }

    @Override // com.sony.imaging.app.fw.State
    public HDMIInfoWrapper.INFO_TYPE getHDMIInfoType() {
        return HDMIInfoWrapper.INFO_TYPE.INFO_OFF;
    }

    /* loaded from: classes.dex */
    private static class MyNotificationListener implements NotificationListener {
        private CameraEx.RecordingMediaChangeCallback recordingTargetMediaChangedCb;
        private String[] tags;

        private MyNotificationListener() {
            this.recordingTargetMediaChangedCb = new MyRecordingMediaChangeCallback();
            this.tags = new String[]{MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE};
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            int state = MediaNotificationManager.getInstance().getMediaState();
            if (2 == state || state == 0) {
                ExecutorCreator creator = ExecutorCreator.getInstance();
                String currentMedia = creator.getSequence().getRecordingMedia();
                String[] ids = ExecutorCreator.getMediaId();
                if (!currentMedia.equals(ids[0])) {
                    if (ShootingState.mIsMediaChecking) {
                        boolean unused = ShootingState.mIsMediaChecking = false;
                    } else {
                        if (8 <= Environment.getVersionPfAPI()) {
                            Log.i(ShootingState.TAG, ShootingState.LOG_CALL_CANCEL_TAKE_PICTURE_ON_MEDIA_CHANGED);
                            BaseShootingExecutor executor = ExecutorCreator.getInstance().getSequence();
                            if (ShootingExecutor.class.isInstance(executor)) {
                                executor.cancelTakePicture();
                            }
                        }
                        creator.stableSequence(2);
                    }
                    BaseShootingExecutor executor2 = ExecutorCreator.getInstance().getSequence();
                    ShootingState.access$508();
                    executor2.setRecordingMedia(ids[0], this.recordingTargetMediaChangedCb);
                } else if (ShootingState.mIsMediaChecking) {
                    ExecutorCreator.getInstance().updateSequence(2);
                    boolean unused2 = ShootingState.mIsMediaChecking = false;
                }
                BaseShootingExecutor executor3 = ExecutorCreator.getInstance().getSequence();
                String[] m = {executor3.getRecordingMedia()};
                if (4 == ExecutorCreator.getInstance().getRecordingMode()) {
                    m = AvindexStore.getExternalMediaIds();
                }
                CautionUtilityClass.getInstance().setMedia(m);
                return;
            }
            int type = MediaNotificationManager.getInstance().getInsertedMediaType();
            if (2 == type || 1 == type) {
                CautionUtilityClass.getInstance().setMedia(AvindexStore.getExternalMediaIds());
            } else {
                CautionUtilityClass.getInstance().setMedia(AvindexStore.getVirtualMediaIds());
            }
            if (1 == state) {
                boolean unused3 = ShootingState.mIsMediaChecking = true;
                ExecutorCreator creator2 = ExecutorCreator.getInstance();
                if (8 <= Environment.getVersionPfAPI()) {
                    BaseShootingExecutor executor4 = ExecutorCreator.getInstance().getSequence();
                    if (ShootingExecutor.class.isInstance(executor4)) {
                        executor4.cancelTakePicture();
                    }
                }
                creator2.stableSequence(2);
            }
        }
    }

    /* loaded from: classes.dex */
    private static class RecModeChangedListener implements NotificationListener {
        private String[] tags;

        private RecModeChangedListener() {
            this.tags = new String[]{CameraNotificationManager.REC_MODE_CHANGED};
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            int state = MediaNotificationManager.getInstance().getMediaState();
            int type = MediaNotificationManager.getInstance().getInsertedMediaType();
            String[] m = ExecutorCreator.getInstance().getCautionMedia(state, type);
            CautionUtilityClass.getInstance().setMedia(m);
        }
    }

    /* loaded from: classes.dex */
    private static class MyRecordingMediaChangeCallback implements CameraEx.RecordingMediaChangeCallback {
        private MyRecordingMediaChangeCallback() {
        }

        public void onRecordingMediaChange(CameraEx cam) {
            ShootingState.access$510();
            StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
            builder.replace(0, builder.length(), ShootingState.LOG_ON_RECORDING_MEDIA_CHANGED).append(ShootingState.mSetRecordingMediaCount);
            Log.d(ShootingState.TAG, builder.toString());
            StringBuilderThreadLocal.releaseScratchBuilder(builder);
            if (ShootingState.mSetRecordingMediaCount == 0) {
                ExecutorCreator.getInstance().updateSequence(2);
            }
        }
    }

    public void replaceChildState(String name, Bundle data) {
        replaceChildState(this.mStateHandle, name, data);
    }

    /* loaded from: classes.dex */
    private static class MyMotionShotResultListener implements CameraEx.MotionShotResultListener {
        private MyMotionShotResultListener() {
        }

        public void onCompleted(boolean arg0, CameraEx arg1) {
            Log.d(ShootingState.TAG, "MotionShot composite resule " + arg0);
            if (!arg0) {
                CautionUtilityClass.getInstance().requestTrigger(3595);
            }
        }
    }
}
