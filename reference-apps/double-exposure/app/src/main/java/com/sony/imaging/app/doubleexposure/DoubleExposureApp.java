package com.sony.imaging.app.doubleexposure;

import android.os.Handler;
import android.os.Looper;
import android.os.MessageQueue;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.menu.MenuTable;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.widget.AppIconView;
import com.sony.imaging.app.base.shooting.widget.AppNameView;
import com.sony.imaging.app.doubleexposure.common.AppLog;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureBackUpKey;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureConstant;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureUtil;
import com.sony.imaging.app.doubleexposure.menu.controller.ThemeSelectionController;
import com.sony.imaging.app.doubleexposure.playback.layout.ProcessingLayout;
import com.sony.imaging.app.doubleexposure.playback.state.PreparingPlayState;
import com.sony.imaging.app.doubleexposure.playback.state.PreviewPlayState;
import com.sony.imaging.app.doubleexposure.shooting.DoubleExposureExecutorCreator;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.BootFactor;
import com.sony.imaging.app.util.AppInfo;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.scalar.sysutil.KeyStatus;
import com.sony.scalar.sysutil.ScalarInput;

/* loaded from: classes.dex */
public class DoubleExposureApp extends BaseApp {
    private String TAG = AppLog.getClassName();
    private DoubleExposureUtil mDoubleExposureUtil = null;
    public static int sBootFactor = -1;
    private static String sBootApp = BaseApp.APP_SHOOTING;

    public DoubleExposureApp() {
        doAbortInCaseUncautghtException = true;
        AppLog.enter(this.TAG, AppLog.getMethodName());
        new Factory();
        new DoubleExposureExecutorCreator();
        setBootLogo(R.drawable.p_16_dd_parts_mle_launchericon, R.string.STRID_FUNC_MLE_NAME);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.BaseApp
    public String getStartApp() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return sBootApp;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.fw.AppRoot, android.app.Activity
    public void onResume() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onResume();
        AppInfo.notifyAppInfo(this, getPackageName(), getClass().getName(), AppInfo.LARGE_CATEGORY_REC, AppInfo.SMALL_CATEGORY_STILL, PULLING_BACK_KEYS_FOR_SHOOTING, RESUME_KEYS_FOR_SHOOTING);
        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() { // from class: com.sony.imaging.app.doubleexposure.DoubleExposureApp.1
            @Override // android.os.MessageQueue.IdleHandler
            public boolean queueIdle() {
                MenuTable.getInstance().createDefaultMenuTree(DoubleExposureApp.this.getApplicationContext());
                return false;
            }
        });
        this.mDoubleExposureUtil = DoubleExposureUtil.getInstance();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.BaseApp, com.sony.imaging.app.fw.AppRoot
    public void onBoot(BootFactor factor) {
        KeyStatus status;
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onBoot(factor);
        AppIconView.setIcon(R.drawable.p_16_dd_parts_mle_appicon, R.drawable.p_16_aa_parts_mle_appicon);
        AppNameView.show(true);
        this.mDoubleExposureUtil.startKikiLog(4204);
        this.mDoubleExposureUtil.loadCameraSetting();
        switch (factor.bootFactor) {
            case 0:
                sBootFactor = 0;
                this.mDoubleExposureUtil.setCurrentMenuSelectionScreen(DoubleExposureConstant.THEME_SELECTION);
                break;
        }
        String selectedTheme = BackUpUtil.getInstance().getPreferenceString(DoubleExposureBackUpKey.KEY_SELECTED_THEME, ThemeSelectionController.SKY);
        ThemeSelectionController.getInstance().setValue(ThemeSelectionController.THEMESELECTION, selectedTheme);
        this.mDoubleExposureUtil.setCurrentShootingScreen(DoubleExposureConstant.FIRST_SHOOTING);
        if (AppInfo.KEY_PLAY_APO.equals(factor.bootKey) || AppInfo.KEY_PLAY_PON.equals(factor.bootKey)) {
            sBootApp = BaseApp.APP_PLAY;
        } else {
            boolean isHousingAttached = false;
            if (!isHousingSupported() && (status = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.WATER_HOUSING)) != null && 1 == status.valid && 1 == status.status) {
                isHousingAttached = true;
            }
            if (!isHousingAttached) {
                sBootApp = BaseApp.APP_SHOOTING;
                Thread initThread = new ExecutorInitThread();
                initThread.start();
            }
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.BaseApp
    protected int getDefaultBackupResName() {
        return R.xml.default_value;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.BaseApp, com.sony.imaging.app.fw.AppRoot
    public void onShutdown() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        Handler handler = getHandler();
        if (handler != null) {
            handler.removeMessages(DoubleExposureConstant.SEND_MESSAGE_TO_EE_STATE);
            handler.removeMessages(100);
            handler.removeMessages(PreparingPlayState.TRANSIT_TO_SHOOTING);
            handler.removeMessages(PreviewPlayState.TRANSIT_TO_PREVIEWSTATE);
        }
        if (ProcessingLayout.sOptimizedImage != null) {
            ProcessingLayout.sOptimizedImage.release();
            ProcessingLayout.sOptimizedImage = null;
        }
        this.mDoubleExposureUtil.saveCameraSettings();
        this.mDoubleExposureUtil = null;
        super.onShutdown();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    /* loaded from: classes.dex */
    private class ExecutorInitThread extends Thread {
        private ExecutorInitThread() {
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            ExecutorCreator.getInstance().init();
        }
    }
}
