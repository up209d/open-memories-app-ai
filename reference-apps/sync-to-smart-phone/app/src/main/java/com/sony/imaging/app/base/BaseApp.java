package com.sony.imaging.app.base;

import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.backup.BackupReader;
import com.sony.imaging.app.base.beep.BeepController;
import com.sony.imaging.app.base.beep.BeepUtilityIdTable;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.AudioSetting;
import com.sony.imaging.app.base.common.AudioVolumeController;
import com.sony.imaging.app.base.common.CommonSettings;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.KikilogUtil;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.common.layout.ForceExitScreenLayout;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.shooting.camera.AELController;
import com.sony.imaging.app.base.shooting.camera.AntiHandBlurController;
import com.sony.imaging.app.base.shooting.camera.ApscModeController;
import com.sony.imaging.app.base.shooting.camera.AutoFocusModeController;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.CinematoneController;
import com.sony.imaging.app.base.shooting.camera.CreativeStyleController;
import com.sony.imaging.app.base.shooting.camera.DROAutoHDRController;
import com.sony.imaging.app.base.shooting.camera.DigitalZoomController;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.FaceDetectionController;
import com.sony.imaging.app.base.shooting.camera.FlashController;
import com.sony.imaging.app.base.shooting.camera.FocusAreaController;
import com.sony.imaging.app.base.shooting.camera.FocusMagnificationController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.base.shooting.camera.MeteringController;
import com.sony.imaging.app.base.shooting.camera.MicRefLevelController;
import com.sony.imaging.app.base.shooting.camera.MovieFormatController;
import com.sony.imaging.app.base.shooting.camera.NDfilterController;
import com.sony.imaging.app.base.shooting.camera.PictureEffectController;
import com.sony.imaging.app.base.shooting.camera.PictureQualityController;
import com.sony.imaging.app.base.shooting.camera.PictureSizeController;
import com.sony.imaging.app.base.shooting.camera.ProColorController;
import com.sony.imaging.app.base.shooting.camera.ViewTekiController;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.base.shooting.trigger.custom.NormalFunctionTable;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.BootFactor;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.HoldKeyServer;
import com.sony.imaging.app.fw.IFunctionTable;
import com.sony.imaging.app.fw.RunStatus;
import com.sony.imaging.app.fw.StateHandle;
import com.sony.imaging.app.util.AppInfo;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.BatteryObserver;
import com.sony.imaging.app.util.BeepUtility;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.GyroscopeObserver;
import com.sony.scalar.sysutil.KeyStatus;
import com.sony.scalar.sysutil.ScalarInput;
import com.sony.scalar.sysutil.didep.Gpelibrary;
import java.util.Arrays;

/* loaded from: classes.dex */
public abstract class BaseApp extends AppRoot {
    public static final String APP_PLAY = "APP_PLAY";
    public static final String APP_SHOOTING = "APP_SHOOTING";
    public static final String CAUTION = "CAUTION";
    public static final String EXIT_SCREEN = "EXITSCREEN";
    public static final String FORCE_EXIT_SCREEN = "FORCE_EXITSCREEN";
    public static final int REC_MODE_BOTH = 3;
    public static final int REC_MODE_INTERVAL = 4;
    public static final int REC_MODE_MOVIE = 2;
    public static final int REC_MODE_NONE = 0;
    public static final int REC_MODE_STILL = 1;
    private static final String TAG = "BaseApp";
    private static final String VERSION = "1.131";
    public static StateHandle mCautionHandle = null;
    public static final String[] PULLING_BACK_KEYS_FOR_SHOOTING = {AppInfo.KEY_USB_CONNECT};
    public static final String[] RESUME_KEYS_FOR_SHOOTING = {AppInfo.KEY_POWER_SLIDE_PON, AppInfo.KEY_RELEASE_APO, AppInfo.KEY_PLAY_APO, AppInfo.KEY_MEDIA_INOUT_APO, AppInfo.KEY_LENS_APO, AppInfo.KEY_ACCESSORY_APO, AppInfo.KEY_DEDICATED_APO, AppInfo.KEY_POWER_APO, AppInfo.KEY_PLAY_PON};
    public static final String[] PULLING_BACK_KEYS_FOR_PLAYBACK = {AppInfo.KEY_S2, AppInfo.KEY_S1_1, AppInfo.KEY_S1_2, AppInfo.KEY_MOVREC, AppInfo.KEY_MODE_DIAL, AppInfo.KEY_USB_CONNECT};
    public static final String[] RESUME_KEYS_FOR_PLAYBACK = {AppInfo.KEY_PLAY_APO, AppInfo.KEY_PLAY_PON};
    public static final String[] PULLING_BACK_KEYS_FOR_EDITING = PULLING_BACK_KEYS_FOR_PLAYBACK;
    public static final String[] RESUME_KEYS_FOR_EDITING = new String[0];
    public static final String[] PULLING_BACK_KEYS_FOR_MOVIE = {AppInfo.KEY_USB_CONNECT};
    public static final String[] RESUME_KEYS_FOR_MOVIE = {AppInfo.KEY_POWER_SLIDE_PON, AppInfo.KEY_RELEASE_APO, AppInfo.KEY_PLAY_APO, AppInfo.KEY_MOVREC_APO, AppInfo.KEY_MEDIA_INOUT_APO, AppInfo.KEY_LENS_APO, AppInfo.KEY_ACCESSORY_APO, AppInfo.KEY_DEDICATED_APO, AppInfo.KEY_POWER_APO, AppInfo.KEY_PLAY_PON};
    protected boolean mIgnoreExclusiveCaution = true;
    protected boolean mIgnoreUnsupportedCaution = true;
    private CautionUtilityClass.CautionCallback cautionProcessing = new CautionUtilityClass.CautionCallback() { // from class: com.sony.imaging.app.base.BaseApp.1
        @Override // com.sony.imaging.app.base.caution.CautionUtilityClass.CautionCallback
        public void onCallback(int[] cautionId) {
            Log.i(BaseApp.TAG, "[START]onCallback cautionId:" + Arrays.toString(cautionId));
            if (BaseApp.this.mIgnoreExclusiveCaution) {
                cautionId[0] = 65535;
            }
            if (BaseApp.this.mIgnoreUnsupportedCaution) {
                cautionId[3] = 65535;
                cautionId[4] = 65535;
                cautionId[5] = 65535;
            }
            int[] currentCautionId = CautionUtilityClass.getInstance().CurrentCautionId();
            if (Arrays.equals(cautionId, currentCautionId)) {
                Log.i(BaseApp.TAG, "[END]onCallback same cautionId");
                return;
            }
            int cnt = 0;
            for (int i : cautionId) {
                if (i == 65535) {
                    cnt++;
                }
            }
            if (cnt < cautionId.length) {
                Bundle bundle = new Bundle();
                bundle.putIntArray("CAUTION_ID", cautionId);
                if (BaseApp.mCautionHandle == null) {
                    if (BaseApp.this.root != null) {
                        BaseApp.mCautionHandle = BaseApp.this.root.addChildState(BaseApp.CAUTION, bundle);
                        Log.i(BaseApp.TAG, "oncallback addChildState");
                    } else {
                        Log.i(BaseApp.TAG, "onCallback root is NULL");
                    }
                } else if (BaseApp.this.root != null) {
                    BaseApp.this.root.replaceChildState(BaseApp.mCautionHandle, BaseApp.CAUTION, bundle);
                    Log.i(BaseApp.TAG, "onCallback replaceChildState");
                } else {
                    Log.i(BaseApp.TAG, "onCallback root is NULL");
                }
            } else if (BaseApp.mCautionHandle != null) {
                if (BaseApp.this.root != null) {
                    BaseApp.this.root.removeChildState(BaseApp.mCautionHandle);
                    BaseApp.mCautionHandle = null;
                    CautionUtilityClass.getInstance().initCurrentCautionId();
                    Log.i(BaseApp.TAG, "onCallback removeChildState");
                } else {
                    Log.i(BaseApp.TAG, "onCallback root is NULL");
                }
            }
            Log.i(BaseApp.TAG, "[END]onCallback mCautionHandle:" + BaseApp.mCautionHandle);
        }
    };
    private StateHandle mExitHandle = null;
    private int mBootLogoIcon = -1;
    private int mBootLogoString = -1;
    private int mBootLogoBackground = -1;
    View mBootLogoView = null;
    MessageQueue.IdleHandler removeBootLogo = new MessageQueue.IdleHandler() { // from class: com.sony.imaging.app.base.BaseApp.2
        @Override // android.os.MessageQueue.IdleHandler
        public boolean queueIdle() {
            if (BaseApp.this.mBootLogoView != null) {
                ((ViewGroup) BaseApp.this.getView()).removeView(BaseApp.this.mBootLogoView);
                BaseApp.this.mBootLogoView = null;
                return false;
            }
            return false;
        }
    };
    MessageQueue.IdleHandler continueBoot = new MessageQueue.IdleHandler() { // from class: com.sony.imaging.app.base.BaseApp.3
        @Override // android.os.MessageQueue.IdleHandler
        public boolean queueIdle() {
            BaseApp.super.onSystemReady();
            return false;
        }
    };
    private BootIdleHandler mBootIdleHandler = new BootIdleHandler();

    public abstract String getStartApp();

    public int getSupportingRecMode() {
        return 1;
    }

    public boolean isMovieModeSupported() {
        return (getSupportingRecMode() & 2) != 0;
    }

    public boolean isStillModeSupported() {
        return (getSupportingRecMode() & 1) != 0;
    }

    public boolean isIntervalRecModeSupported() {
        return (getSupportingRecMode() & 4) != 0;
    }

    public boolean isShootingSupported() {
        return getSupportingRecMode() != 0;
    }

    public void changeApp(String app, Bundle bundle) {
        this.root.replaceChildState(this.handle, app, bundle);
    }

    public void changeApp(String app) {
        changeApp(app, null);
    }

    protected int getDefaultBackupResName() {
        return R.xml.base_default_value;
    }

    protected boolean isHousingSupported() {
        return false;
    }

    public boolean is4kPlaybackSupported() {
        return false;
    }

    public boolean canVolumeAdjustment() {
        return false;
    }

    @Override // com.sony.imaging.app.fw.AppRoot
    public final AppRoot.OpenStateData _getStartApp() {
        if (!isHousingSupported()) {
            KeyStatus status = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.WATER_HOUSING);
            Log.i(TAG, "Housing Status " + status.valid + ", " + status.status);
            if (status != null && 1 == status.valid && 1 == status.status) {
                Bundle bundle = new Bundle();
                bundle.putInt(ForceExitScreenLayout.DIALOG_MESSAGE, android.R.string.ime_action_done);
                return new AppRoot.OpenStateData(this, FORCE_EXIT_SCREEN, bundle);
            }
        }
        String name = getStartApp();
        Bundle data = getStartAppData();
        return new AppRoot.OpenStateData(this, name, data);
    }

    protected Bundle getStartAppData() {
        return null;
    }

    public BaseApp() {
        Log.i(TAG, "BaseApp version 1.131");
        CameraSetting.registController(AELController.getName(), AELController.class);
        CameraSetting.registController(AntiHandBlurController.getName(), AntiHandBlurController.class);
        CameraSetting.registController(AutoFocusModeController.getName(), AutoFocusModeController.class);
        CameraSetting.registController(FaceDetectionController.getName(), FaceDetectionController.class);
        CameraSetting.registController(FocusMagnificationController.getName(), FocusMagnificationController.class);
        CameraSetting.registController(ViewTekiController.getName(), ViewTekiController.class);
        CameraSetting.registController(WhiteBalanceController.getName(), WhiteBalanceController.class);
        CameraSetting.registController(DriveModeController.getName(), DriveModeController.class);
        CameraSetting.registController(CreativeStyleController.getName(), CreativeStyleController.class);
        CameraSetting.registController(ExposureCompensationController.getName(), ExposureCompensationController.class);
        CameraSetting.registController(DROAutoHDRController.getName(), DROAutoHDRController.class);
        CameraSetting.registController(ISOSensitivityController.getName(), ISOSensitivityController.class);
        CameraSetting.registController(MeteringController.getName(), MeteringController.class);
        CameraSetting.registController(MovieFormatController.getName(), MovieFormatController.class);
        CameraSetting.registController(PictureEffectController.getName(), PictureEffectController.class);
        CameraSetting.registController(PictureQualityController.getName(), PictureQualityController.class);
        CameraSetting.registController(PictureSizeController.getName(), PictureSizeController.class);
        CameraSetting.registController(ExposureModeController.getName(), ExposureModeController.class);
        CameraSetting.registController(FocusAreaController.getName(), FocusAreaController.class);
        CameraSetting.registController(ApscModeController.getName(), ApscModeController.class);
        CameraSetting.registController("flush1", null);
        CameraSetting.registController(DigitalZoomController.getName(), DigitalZoomController.class);
        CameraSetting.registController(FlashController.getName(), FlashController.class);
        CameraSetting.registController(FocusModeController.getName(), FocusModeController.class);
        CameraSetting.registController(MicRefLevelController.getName(), MicRefLevelController.class);
        CameraSetting.registController(CinematoneController.getName(), CinematoneController.class);
        CameraSetting.registController(NDfilterController.getName(), NDfilterController.class);
        CameraSetting.registController(BeepController.getName(), BeepController.class);
        CameraSetting.registController(ProColorController.getName(), ProColorController.class);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.fw.AppRoot
    public void onCautionModeChanged(int mode) {
        CautionUtilityClass.getInstance().setMode(mode);
    }

    @Override // com.sony.imaging.app.fw.AppRoot, android.app.Activity
    public void finish() {
        dismissExitScreen();
        this.mExitHandle = this.root.addChildState(EXIT_SCREEN, (Bundle) null);
    }

    public void dismissExitScreen() {
        if (this.mExitHandle != null && this.mExitHandle.isAlive()) {
            this.root.removeChildState(this.mExitHandle);
        }
    }

    public void finish(boolean special) {
        if (special) {
            finish();
        } else {
            super.finish();
        }
    }

    protected void setBootLogo(int iconId, int stringId, int color) {
        this.mBootLogoIcon = iconId;
        this.mBootLogoString = stringId;
        this.mBootLogoBackground = color;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setBootLogo(int iconId, int stringId) {
        setBootLogo(iconId, stringId, AudioVolumeController.INVALID_VALUE);
    }

    @Override // com.sony.imaging.app.fw.AppRoot
    public void onSystemReady() {
        if (-1 == this.mBootLogoIcon) {
            this.continueBoot.queueIdle();
            return;
        }
        this.mBootLogoView = getLayoutInflater().inflate(R.layout.boot_logo, (ViewGroup) null);
        this.mBootLogoView.setBackgroundColor(this.mBootLogoBackground);
        ImageView image = (ImageView) this.mBootLogoView.findViewById(R.id.bootlogo);
        image.setImageResource(this.mBootLogoIcon);
        TextView text = (TextView) this.mBootLogoView.findViewById(R.id.boottext);
        text.setText(this.mBootLogoString);
        ((ViewGroup) getView()).addView(this.mBootLogoView);
        Looper.myQueue().addIdleHandler(this.continueBoot);
        Looper.myQueue().addIdleHandler(this.removeBootLogo);
    }

    /* loaded from: classes.dex */
    private static class BootIdleHandler implements MessageQueue.IdleHandler {
        private BootIdleHandler() {
        }

        @Override // android.os.MessageQueue.IdleHandler
        public boolean queueIdle() {
            BeepUtility.getInstance().getIdTable().init();
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.fw.AppRoot
    public void onBoot(BootFactor factor) {
        super.onBoot(factor);
        KikilogUtil kikilogUtil = KikilogUtil.getInstance();
        kikilogUtil.setAppName(getPackageName());
        kikilogUtil.startApp();
        BatteryObserver.start(getApplicationContext());
        GyroscopeObserver.getInstance().setContext(getApplicationContext());
        Gpelibrary.changeFrameBufferPixel(Gpelibrary.GS_FRAMEBUFFER_TYPE.RGBA4444);
        CautionUtilityClass.getInstance().initialized(this);
        CautionUtilityClass.getInstance().registerCallback(this.cautionProcessing);
        CautionUtilityClass.getInstance().registerExCallback();
        CustomizableFunction.setTable(getFunctionTable());
        CommonSettings.getInstance().resume();
        DisplayModeObserver.getInstance().resume(is4kPlaybackSupported());
        MediaNotificationManager.getInstance().resume(this);
        BackUpUtil backup = BackUpUtil.getInstance();
        backup.Init(getApplicationContext());
        BeepUtility.getInstance().setIdTable(new BeepUtilityIdTable());
        Looper.myQueue().addIdleHandler(this.mBootIdleHandler);
        AvailableInfo.initialize(this);
        ExecutorCreator creator = ExecutorCreator.getInstance();
        creator.setSupportingRecMode(getSupportingRecMode());
        backup.setDefaultValues(getDefaultBackupResName());
        if (creator != null && creator.isAssistApp() && factor.bootFactor == 0) {
            backup.reset();
        }
        if (!isSameVersion()) {
            _needUpdatePreference();
        }
        selectShootingMode();
        BackupReader.reset();
        AudioSetting.getInstance().initialize();
        CameraSetting cameraSetting = CameraSetting.getInstance();
        cameraSetting.resetProgramLine();
        cameraSetting.resetMfAssistInfo();
        cameraSetting.clearFocusAreaInfos();
        DigitalZoomController.setWantResetZoom(true);
        ExposureCompensationController.setBootFlg(true);
        FlashController.setInitFlashModeFlg(true);
        FocusAreaController.initAFFlexibleSpotSetCenterFlag();
        ExposureModeController.onBoot();
        if (factor.bootFactor == 0) {
            backup.removePreference(BaseBackUpKey.ID_DIAL_AVTV_TOGGLE);
        }
        FocusModeController.keepToggleFocusModeFlag(false);
        if (isShootingSupported()) {
            HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.EV_DIAL_CHANGED, 4);
            HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_CHANGED, 4);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.fw.AppRoot, android.app.Activity
    public void onPause() {
        this.removeBootLogo.queueIdle();
        Looper.myQueue().removeIdleHandler(this.removeBootLogo);
        Looper.myQueue().removeIdleHandler(this.continueBoot);
        super.onPause();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.fw.AppRoot
    public void onShutdown() {
        super.onShutdown();
        AudioSetting.getInstance().terminate();
        MediaNotificationManager.getInstance().pause(this);
        CautionUtilityClass.getInstance().terminated();
        CautionUtilityClass.getInstance().unregisterExCallback();
        CautionUtilityClass.getInstance().unregisterCallback(this.cautionProcessing);
        mCautionHandle = null;
        DisplayModeObserver.getInstance().pause();
        CommonSettings.getInstance().pause();
        AvailableInfo.terminate();
        Looper.myQueue().removeIdleHandler(this.mBootIdleHandler);
        BackUpUtil.getInstance().finishSettings(false);
        Gpelibrary.changeFrameBufferPixel(Gpelibrary.GS_FRAMEBUFFER_TYPE.RGBA4444);
        BatteryObserver.stop(getApplicationContext());
        KikilogUtil kikilogUtil = KikilogUtil.getInstance();
        kikilogUtil.stopApp();
    }

    protected String getVersionName() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 1);
            String ret = packageInfo.versionName;
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    protected boolean isSameVersion() {
        String preferenceVersion = BackUpUtil.getInstance().getPreferenceString(BaseBackUpKey.ID_APPLICATION_VERSION, null);
        String applicationVersion = getVersionName();
        return applicationVersion.equals(preferenceVersion);
    }

    protected void needUpdatePreference() {
        BackUpUtil.getInstance().resetAll();
    }

    protected void _needUpdatePreference() {
        needUpdatePreference();
        BackUpUtil.getInstance().setPreference(BaseBackUpKey.ID_APPLICATION_VERSION, getVersionName());
    }

    public void selectShootingMode() {
        if (getSupportingRecMode() != 0) {
            ExecutorCreator creator = ExecutorCreator.getInstance();
            if (creator.isMovieModeSupported() && Environment.isMovieAPISupported() && ModeDialDetector.hasModeDial()) {
                int recMode = 1;
                if (544 == ModeDialDetector.getModeDialPosition()) {
                    recMode = 2;
                }
                creator.setRecordingMode(recMode, null);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.fw.AppRoot
    public void onLocaleChanged() {
        super.onLocaleChanged();
        BaseMenuService.clearTextCache();
    }

    protected IFunctionTable getFunctionTable() {
        return new NormalFunctionTable();
    }

    @Override // com.sony.imaging.app.fw.AppRoot, android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isHousingSupported() || 640 != event.getScanCode()) {
            return super.onKeyDown(keyCode, event);
        }
        if (1 != RunStatus.getStatus() && 4 != RunStatus.getStatus()) {
            return true;
        }
        Log.i("AppRoot", "onKeyDown 640 force exit");
        Bundle bundle = new Bundle();
        bundle.putInt(ForceExitScreenLayout.DIALOG_MESSAGE, android.R.string.ime_action_done);
        changeApp(FORCE_EXIT_SCREEN, bundle);
        return true;
    }
}
