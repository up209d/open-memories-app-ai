package com.sony.imaging.app.srctrl;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.MovieFormatController;
import com.sony.imaging.app.base.shooting.widget.AppIconView;
import com.sony.imaging.app.base.shooting.widget.AppNameView;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.BootFactor;
import com.sony.imaging.app.srctrl.liveview.AFFrameConverter;
import com.sony.imaging.app.srctrl.liveview.LiveviewCommon;
import com.sony.imaging.app.srctrl.shooting.ExposureModeControllerEx;
import com.sony.imaging.app.srctrl.shooting.SRCtrlExecutorCreator;
import com.sony.imaging.app.srctrl.shooting.camera.EachDriveModeController;
import com.sony.imaging.app.srctrl.shooting.camera.SRCtrlFocusAreaController;
import com.sony.imaging.app.srctrl.shooting.camera.SRCtrlMovieFormatController;
import com.sony.imaging.app.srctrl.shooting.camera.SRCtrlPictureQualityController;
import com.sony.imaging.app.srctrl.shooting.camera.SRCtrlSilentShutterController;
import com.sony.imaging.app.srctrl.shooting.camera.SRCtrlTemperatureManager;
import com.sony.imaging.app.srctrl.util.ModelInfo;
import com.sony.imaging.app.srctrl.util.NfcCtrlManager;
import com.sony.imaging.app.srctrl.util.SRCtrlEnvironment;
import com.sony.imaging.app.srctrl.util.SRCtrlKikiLogUtil;
import com.sony.imaging.app.srctrl.util.ScalarAAirplaneModeActivity;
import com.sony.imaging.app.srctrl.util.UtilPFWorkaround;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationFNumber;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationLiveviewFrameInfo;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationShutterSpeed;
import com.sony.imaging.app.srctrl.webapi.availability.ParamsGenerator;
import com.sony.imaging.app.srctrl.webapi.definition.Name;
import com.sony.imaging.app.srctrl.webapi.util.AttachedLensInfo;
import com.sony.imaging.app.util.ApoWrapper;
import com.sony.imaging.app.util.AppInfo;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.scalar.sysutil.KeyStatus;
import com.sony.scalar.sysutil.ScalarInput;
import com.sony.scalar.sysutil.ScalarProperties;

/* loaded from: classes.dex */
public class SRCtrl extends BaseApp {
    private static final String ACTION_AIRPLANEMODE_ACTIVITY = "com.sony.scalar.ScalarAAirplaneModeActivity.AIRPLANEMODE_ACTIVITY";
    private static final String TAG = "DetailedVersion";
    private static final String VERSION = "03";
    private static final String tag = SRCtrl.class.getSimpleName();
    public static final String SRCTRL_ROOT = "APP_SRCTRL";
    private static String bootApp = SRCTRL_ROOT;
    private static final String[] FOCTORS_CACHE_FILTER = {"setOutputFormat", Name.SET_WHITEBALANCE, Name.SET_EXPOSURE_COMPENSATION, Name.SET_SELF_TIMER, "setDriveMode", "setISOSensitivity", Name.SET_FLASH_MODE, "setFlashType", "setFlashCompensation", "setFocusMode", "setAutoFocusMode", "setFocusAreaMode", "setSceneMode"};
    private static Integer CACHED_APP_STRING_ID = null;
    private static Boolean IS_SYSTEM_APP = null;
    public static String VERSION_NAME_STR = null;

    public SRCtrl() {
        new Factory();
        new SRCtrlExecutorCreator();
        CameraSetting.registController(ExposureModeControllerEx.getName(), ExposureModeControllerEx.class);
        CameraSetting.registController(EachDriveModeController.getName(), EachDriveModeController.class);
        CameraSetting.registController(SRCtrlPictureQualityController.getName(), SRCtrlPictureQualityController.class);
        CameraSetting.registController(SRCtrlFocusAreaController.getName(), SRCtrlFocusAreaController.class);
        CameraSetting.registController(MovieFormatController.getName(), SRCtrlMovieFormatController.class);
        CameraSetting.registController(SRCtrlSilentShutterController.getName(), SRCtrlSilentShutterController.class);
        AvailableInfo.setFactorsCacheFilter(FOCTORS_CACHE_FILTER);
    }

    @Override // com.sony.imaging.app.base.BaseApp
    public String getStartApp() {
        return bootApp;
    }

    @Override // com.sony.imaging.app.base.BaseApp
    public int getSupportingRecMode() {
        return getRecMode();
    }

    private static boolean isMovieSupportedSet() {
        return ModelInfo.getInstance().isMovieRecSupported();
    }

    public static int getRecMode() {
        return isMovieSupportedSet() ? 3 : 1;
    }

    @Override // com.sony.imaging.app.base.BaseApp, com.sony.imaging.app.fw.AppRoot
    protected void onBoot(BootFactor factor) {
        KeyStatus status;
        String modelName = ScalarProperties.getString(UtilPFWorkaround.PROP_MODEL_NAME);
        ModelInfo.getInstance().set(modelName);
        SRCtrlEnvironment.getInstance().initialize(getApplicationContext());
        super.onBoot(factor);
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), LiveviewCommon.PAYLOAD_HEADER_SIZE);
            Log.i(TAG, "DetailedVersion " + ("".equals(VERSION) ? packageInfo.versionName : packageInfo.versionName + VERSION));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        boolean isHousingAttached = false;
        if (!isHousingSupported() && (status = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.WATER_HOUSING)) != null && 1 == status.valid && 1 == status.status) {
            isHousingAttached = true;
        }
        if (!isHousingAttached) {
            SRCtrlKikiLogUtil.logAppLaunch();
            AppIconView.setIcon(R.drawable.p_16_dd_parts_sr_appicon, R.drawable.p_16_aa_parts_sr_appicon);
            AppNameView.setText(getResources().getString(getAppStringId(this)));
            AppNameView.show(true);
            ApoWrapper.fixApoType(ApoWrapper.APO_TYPE.NONE);
            NfcCtrlManager.getInstance().init();
            SRCtrlTemperatureManager.getInstance().init();
            initialize();
            AttachedLensInfo.load();
            bootApp = SRCTRL_ROOT;
        }
    }

    private void initialize() {
        ParamsGenerator.initialize();
        ParamsGenerator.initBootShootModeParams();
        CameraOperationFNumber.setFNumberSetViaWebApi(null);
        CameraOperationLiveviewFrameInfo.set(false);
        CameraOperationShutterSpeed.clearExposureNRSettingValueCache();
        AFFrameConverter.clear();
    }

    @Override // com.sony.imaging.app.base.BaseApp, com.sony.imaging.app.fw.AppRoot
    protected void onShutdown() {
        ApoWrapper.fixApoType(ApoWrapper.APO_TYPE.UNKNOWN);
        AttachedLensInfo.save();
        super.onShutdown();
        NfcCtrlManager.getInstance().release();
        SRCtrlTemperatureManager.getInstance().release();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.fw.AppRoot, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        doAbortInCaseUncautghtException = true;
        super.onCreate(savedInstanceState);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.fw.AppRoot, android.app.Activity
    public void onResume() {
        super.onResume();
        notifyAppInfo();
    }

    private void notifyAppInfo() {
        AppInfo.notifyAppInfo(this, getPackageName(), getClass().getName(), AppInfo.LARGE_CATEGORY_REC, AppInfo.SMALL_CATEGORY_STILL, PULLING_BACK_KEYS_FOR_PLAYBACK, RESUME_KEYS_FOR_SHOOTING);
    }

    @Override // com.sony.imaging.app.base.BaseApp, com.sony.imaging.app.fw.AppRoot
    public void onSystemReady() {
        if (isAirPlaneModeOn()) {
            notifyAppInfo();
            startAirPlaneModeActivity();
        } else {
            setBootLogo(R.drawable.p_16_dd_parts_sr_launchericon, getAppStringId(this));
            super.onSystemReady();
        }
    }

    private void startAirPlaneModeActivity() {
        Intent i;
        if (ModelInfo.getInstance().isNeedDisplayOwnAirPlaneMode()) {
            i = new Intent(this, (Class<?>) ScalarAAirplaneModeActivity.class);
            i.putExtra("CUSTOM_TITLE", getResources().getString(getAppStringId(this)));
        } else {
            i = new Intent(ACTION_AIRPLANEMODE_ACTIVITY);
        }
        startActivity(i);
        finish(AppRoot.FINISH_TYPE.ONLY_ACTIVITY);
    }

    private boolean isAirPlaneModeOn() {
        return Settings.System.getInt(getContentResolver(), "airplane_mode_on", 0) == 1;
    }

    public static int getAppStringId(Context context) {
        if (CACHED_APP_STRING_ID != null) {
            return CACHED_APP_STRING_ID.intValue();
        }
        if (context == null) {
            Log.e(tag, "ERROR: context is null.");
            return R.string.STRID_FUNC_EZREMOTE;
        }
        if (isSystemApp(context)) {
            CACHED_APP_STRING_ID = new Integer(R.string.STRID_FUNC_EZREMOTE_EMBEDDED);
        } else {
            CACHED_APP_STRING_ID = new Integer(R.string.STRID_FUNC_EZREMOTE);
        }
        return CACHED_APP_STRING_ID.intValue();
    }

    public static boolean isSystemApp(Context context) {
        if (IS_SYSTEM_APP != null) {
            return IS_SYSTEM_APP.booleanValue();
        }
        if (VERSION_NAME_STR == null) {
            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(SRCtrl.class.getPackage().getName(), LiveviewCommon.PAYLOAD_HEADER_SIZE);
                VERSION_NAME_STR = packageInfo.versionName;
                Log.v(tag, "APP-Version: " + VERSION_NAME_STR);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        ApplicationInfo appInfo = context.getApplicationInfo();
        if (1 == (appInfo.flags & 1) && 128 != (appInfo.flags & LiveviewCommon.PAYLOAD_HEADER_SIZE)) {
            IS_SYSTEM_APP = Boolean.TRUE;
            Log.v(tag, "APP-MODE: PreInstlled App");
        } else {
            IS_SYSTEM_APP = Boolean.FALSE;
            Log.v(tag, "APP-MODE: Downloaded App");
        }
        return IS_SYSTEM_APP.booleanValue();
    }

    public static boolean isSystemApp() {
        return isSystemApp(null);
    }

    @Override // com.sony.imaging.app.base.BaseApp
    protected int getDefaultBackupResName() {
        return R.xml.default_value;
    }
}
