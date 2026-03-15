package com.sony.imaging.app.lightgraffiti;

import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.MenuTable;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.CreativeStyleController;
import com.sony.imaging.app.base.shooting.camera.DROAutoHDRController;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.FlashController;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.base.shooting.camera.PictureEffectController;
import com.sony.imaging.app.base.shooting.camera.PictureQualityController;
import com.sony.imaging.app.base.shooting.camera.PictureSizeController;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.widget.AppIconView;
import com.sony.imaging.app.base.shooting.widget.AppNameView;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.BootFactor;
import com.sony.imaging.app.lightgraffiti.caution.LGInfo;
import com.sony.imaging.app.lightgraffiti.shooting.LGExecutorCreator;
import com.sony.imaging.app.lightgraffiti.shooting.LGStateHolder;
import com.sony.imaging.app.lightgraffiti.shooting.controller.LGAppTopController;
import com.sony.imaging.app.lightgraffiti.shooting.controller.LGCreativeStyleController;
import com.sony.imaging.app.lightgraffiti.shooting.controller.LGDROAutoHDRController;
import com.sony.imaging.app.lightgraffiti.shooting.controller.LGExposureCompensationController;
import com.sony.imaging.app.lightgraffiti.shooting.controller.LGExposureModeController;
import com.sony.imaging.app.lightgraffiti.shooting.controller.LGFlashController;
import com.sony.imaging.app.lightgraffiti.shooting.controller.LGFocusModeController;
import com.sony.imaging.app.lightgraffiti.shooting.controller.LGISOSensitivityController;
import com.sony.imaging.app.lightgraffiti.shooting.controller.LGLongExposureNrController;
import com.sony.imaging.app.lightgraffiti.shooting.controller.LGPictureEffectController;
import com.sony.imaging.app.lightgraffiti.shooting.controller.LGPictureQualityController;
import com.sony.imaging.app.lightgraffiti.shooting.controller.LGPictureSizeController;
import com.sony.imaging.app.lightgraffiti.shooting.controller.LGRemoConController;
import com.sony.imaging.app.lightgraffiti.shooting.controller.LGWhiteBalanceController;
import com.sony.imaging.app.lightgraffiti.shooting.timer.LGTimerThread;
import com.sony.imaging.app.lightgraffiti.util.LGConstants;
import com.sony.imaging.app.lightgraffiti.util.LGUtility;
import com.sony.imaging.app.util.AppInfo;
import com.sony.scalar.sysutil.KeyStatus;
import com.sony.scalar.sysutil.ScalarInput;

/* loaded from: classes.dex */
public class LightGraffitiApp extends BaseApp {
    private static final String TAG2 = "DetailedVersion";
    private static final String VERSION = "0.0030";
    private static String bootApp = BaseApp.APP_SHOOTING;
    public static LightGraffitiApp thisApp = null;
    public static boolean openMenuOnEE = false;
    public static final String[] PULLING_BACK_KEYS_INVALID = new String[0];

    public LightGraffitiApp() {
        doAbortInCaseUncautghtException = true;
        thisApp = this;
        new Factory();
        new LGExecutorCreator();
        BaseMenuService.setEachCautionIdClass(LGInfo.class);
        setBootLogo(R.drawable.p_16_dd_parts_lg_launchericon, R.string.STRID_FUNC_LIGHTGRAFFITI_NAME);
        CameraSetting.registController(CreativeStyleController.getName(), LGCreativeStyleController.class);
        CameraSetting.registController(ExposureCompensationController.getName(), LGExposureCompensationController.class);
        CameraSetting.registController(DROAutoHDRController.getName(), LGDROAutoHDRController.class);
        CameraSetting.registController(ISOSensitivityController.getName(), LGISOSensitivityController.class);
        CameraSetting.registController(PictureEffectController.getName(), LGPictureEffectController.class);
        CameraSetting.registController(PictureQualityController.getName(), LGPictureQualityController.class);
        CameraSetting.registController(PictureSizeController.getName(), LGPictureSizeController.class);
        CameraSetting.registController(ExposureModeController.getName(), LGExposureModeController.class);
        CameraSetting.registController(FlashController.getName(), LGFlashController.class);
        CameraSetting.registController(WhiteBalanceController.getName(), LGWhiteBalanceController.class);
        CameraSetting.registController(LGRemoConController.getName(), LGRemoConController.class);
        CameraSetting.registController(LGFocusModeController.getName(), LGFocusModeController.class);
    }

    @Override // com.sony.imaging.app.base.BaseApp
    public int getSupportingRecMode() {
        return 1;
    }

    @Override // com.sony.imaging.app.base.BaseApp
    public String getStartApp() {
        return bootApp;
    }

    /* loaded from: classes.dex */
    class ExecutorInitThread extends Thread {
        ExecutorInitThread() {
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            ExecutorCreator.getInstance().init();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.fw.AppRoot, android.app.Activity
    public void onResume() {
        super.onResume();
        AppInfo.notifyAppInfo(this, getPackageName(), getClass().getName(), AppInfo.LARGE_CATEGORY_REC, AppInfo.SMALL_CATEGORY_STILL, PULLING_BACK_KEYS_FOR_SHOOTING, RESUME_KEYS_FOR_SHOOTING);
        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() { // from class: com.sony.imaging.app.lightgraffiti.LightGraffitiApp.1
            @Override // android.os.MessageQueue.IdleHandler
            public boolean queueIdle() {
                MenuTable.getInstance().createDefaultMenuTree(LightGraffitiApp.this.getApplicationContext());
                return false;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.BaseApp, com.sony.imaging.app.fw.AppRoot
    public void onBoot(BootFactor factor) {
        KeyStatus status;
        super.onBoot(factor);
        Log.i(TAG2, "DetailedVersion 0.0030");
        AppIconView.setIcon(R.drawable.p_16_dd_parts_lg_appicon);
        AppNameView.setText(getResources().getString(R.string.STRID_FUNC_LIGHTGRAFFITI_NAME));
        AppNameView.show(true);
        switch (factor.bootFactor) {
            case 0:
                openMenuOnEE = true;
                LGUtility.getInstance().setMenuBoot(true);
                LGUtility.getInstance().outputKikilog(LGConstants.KIKILOG_ID_APPLICATION_BOOTING);
                break;
            case 1:
                openMenuOnEE = false;
                LGUtility.getInstance().setMenuBoot(false);
                break;
            case 2:
                openMenuOnEE = false;
                LGUtility.getInstance().setMenuBoot(false);
                break;
        }
        if (AppInfo.KEY_PLAY_APO.equals(factor.bootKey) || AppInfo.KEY_PLAY_PON.equals(factor.bootKey)) {
            bootApp = BaseApp.APP_PLAY;
            return;
        }
        boolean isHousingAttached = false;
        if (!isHousingSupported() && (status = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.WATER_HOUSING)) != null && 1 == status.valid && 1 == status.status) {
            isHousingAttached = true;
        }
        if (!isHousingAttached) {
            bootApp = BaseApp.APP_SHOOTING;
            Thread initThread = new ExecutorInitThread();
            initThread.start();
        }
    }

    @Override // com.sony.imaging.app.base.BaseApp, com.sony.imaging.app.fw.AppRoot
    public void onShutdown() {
        super.onShutdown();
        LGStateHolder.getInstance().release();
        LGAppTopController.getInstance().setShootingScreenOpened(false);
        LGTimerThread.getInstance().timerStop();
        LGLongExposureNrController.getInstance().release();
        LGStateHolder.getInstance().endShootingStage();
        LGUtility.clearPlayDispMode();
        LGUtility.destroyInstance();
    }

    @Override // com.sony.imaging.app.base.BaseApp
    protected int getDefaultBackupResName() {
        return R.xml.default_value;
    }
}
