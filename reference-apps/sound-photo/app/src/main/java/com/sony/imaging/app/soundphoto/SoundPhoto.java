package com.sony.imaging.app.soundphoto;

import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.BaseBackUpKey;
import com.sony.imaging.app.base.menu.MenuTable;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.base.shooting.widget.AppIconView;
import com.sony.imaging.app.base.shooting.widget.AppNameView;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.BootFactor;
import com.sony.imaging.app.soundphoto.database.DataBaseAdapter;
import com.sony.imaging.app.soundphoto.database.DataBaseOperations;
import com.sony.imaging.app.soundphoto.database.SoundPhotoDataBaseUtil;
import com.sony.imaging.app.soundphoto.microphone.SPFMicrophoneCtrl;
import com.sony.imaging.app.soundphoto.shooting.SPExecutorCreator;
import com.sony.imaging.app.soundphoto.shooting.audiorecorder.AudioRecorder;
import com.sony.imaging.app.soundphoto.shooting.state.SPShootingState;
import com.sony.imaging.app.soundphoto.util.AppLog;
import com.sony.imaging.app.soundphoto.util.SPConstants;
import com.sony.imaging.app.soundphoto.util.SPUtil;
import com.sony.imaging.app.util.AppInfo;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.DatabaseUtil;
import com.sony.scalar.sysutil.KeyStatus;
import com.sony.scalar.sysutil.ScalarInput;
import java.io.IOException;

/* loaded from: classes.dex */
public class SoundPhoto extends BaseApp {
    private static final String TAG = "SoundPhoto";
    private static final String TAG2 = "DetailedVersion";
    private static final String VERSION = "1.1102";
    public static Object mWaitForRecovery = new Object();
    private static String bootApp = BaseApp.APP_SHOOTING;
    public static final byte[] SECRET_WORDS_SHA = {83, 111, 110, 121, 32, 68, 105, 103, 105, 116, 97, 108, 32, 73, 109, 97, 103, 105, 110, 103, 32, 80, 108, 97, 121, 32, 77, 101, 109, 111, 114, 105, 101, 115, 32, 67, 97, 109, 101, 114, 97, 32, 65, 112, 112, 115, 32, 50, 48, 49, 52, 47, 48, 57, 47, 48, 49, 32, 77, 111, 110, 100, 97, 121};
    public static final byte[] SEED_AES = {112, 109, 99, 97, 83, 85, 71, 73, 77, 79, 82, 73, 65, 73, 90, 65, 87, 65, 56, 84, 72, 65, 80, 80};

    public SoundPhoto() {
        new SPFactory();
        new SPExecutorCreator();
        setBootLogo(R.drawable.p_16_dd_parts_soundphoto_launchericon, R.string.STRID_FUNC_SOUNDPHOTO);
    }

    @Override // com.sony.imaging.app.base.BaseApp
    public boolean is4kPlaybackSupported() {
        return true;
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
            SoundPhoto.this.handleDBOpertation();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.fw.AppRoot, android.app.Activity
    public void onResume() {
        super.onResume();
        AppLog.enter(TAG, "onResume");
        SPUtil.getInstance().setAppAlive(true);
        AppInfo.notifyAppInfo(this, getPackageName(), getClass().getName(), AppInfo.LARGE_CATEGORY_REC, AppInfo.SMALL_CATEGORY_STILL, PULLING_BACK_KEYS_FOR_SHOOTING, RESUME_KEYS_FOR_SHOOTING);
        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() { // from class: com.sony.imaging.app.soundphoto.SoundPhoto.1
            @Override // android.os.MessageQueue.IdleHandler
            public boolean queueIdle() {
                MenuTable.getInstance().createDefaultMenuTree(SoundPhoto.this.getApplicationContext());
                return false;
            }
        });
        AppLog.exit(TAG, "onResume");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDBOpertation() {
        DataBaseOperations dBaseOperation = DataBaseOperations.getInstance();
        try {
            dBaseOperation.exportXMLtoMedia();
        } catch (IOException e) {
            Log.d(TAG, e.toString());
        }
        DatabaseUtil.setIsDatabaseChecked(false);
        DataBaseOperations.getInstance().refreshDatabase();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.BaseApp
    public void _needUpdatePreference() {
        super._needUpdatePreference();
        if (ModeDialDetector.hasModeDial()) {
            BackUpUtil.getInstance().setPreference(BaseBackUpKey.ID_SCENE_SELECTION_VALUE, "portrait");
            BackUpUtil.getInstance().setPreference(BaseBackUpKey.ID_MOVIE_MODE_VALUE, ExposureModeController.MOVIE_PROGRAM_AUTO_MODE);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.BaseApp, com.sony.imaging.app.fw.AppRoot
    public void onBoot(BootFactor factor) {
        KeyStatus status;
        super.onBoot(factor);
        AppLog.info(TAG2, "DetailedVersion 1.1102");
        AppLog.enter(TAG, "onBoot Start factor : " + factor);
        SPFMicrophoneCtrl.getInstance().initialMicSetting();
        SPUtil.getInstance().printKikiLog(SPConstants.SOUND_PHOTO_LAUNCH_KIKILOG_ID);
        AppIconView.setIcon(R.drawable.p_16_dd_parts_soundphoto_appicon, R.drawable.p_16_aa_parts_soundphoto_appicon);
        AppNameView.setText(getResources().getString(R.string.STRID_FUNC_SOUNDPHOTO));
        AppNameView.show(true);
        DatabaseUtil.initialize(DataBaseAdapter.getInstance(), SEED_AES, SECRET_WORDS_SHA);
        if (AppInfo.KEY_PLAY_APO.equals(factor.bootKey) || AppInfo.KEY_PLAY_PON.equals(factor.bootKey)) {
            SPUtil.getInstance().setPlayBackKeyPressed(true);
            bootApp = BaseApp.APP_PLAY;
            SPUtil.getInstance().setAppState(bootApp);
        } else {
            boolean isHousingAttached = false;
            if (!isHousingSupported() && (status = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.WATER_HOUSING)) != null && 1 == status.valid && 1 == status.status) {
                isHousingAttached = true;
            }
            if (!isHousingAttached) {
                AudioRecorder.getInstance().start();
                bootApp = BaseApp.APP_SHOOTING;
                Thread initThread = new ExecutorInitThread();
                initThread.setName("initThread");
                initThread.start();
            }
        }
        AppLog.exit(TAG, "onBoot end ");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.fw.AppRoot, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        doAbortInCaseUncautghtException = true;
        super.onCreate(savedInstanceState);
        BackUpUtil backup = BackUpUtil.getInstance();
        backup.Init(getApplicationContext());
        backup.setDefaultValues(getDefaultBackupResName());
    }

    @Override // com.sony.imaging.app.base.BaseApp
    protected int getDefaultBackupResName() {
        return R.xml.default_value;
    }

    @Override // com.sony.imaging.app.base.BaseApp
    public void changeApp(String app, Bundle bundle) {
        SPUtil.getInstance().setSoundPlayingState(false);
        if (BaseApp.APP_PLAY.equals(app)) {
            SPUtil.getInstance().setPlayBackKeyPressed(true);
            SPShootingState.isSyncRequest = true;
        }
        SPUtil.getInstance().setAppState(app);
        super.changeApp(app, bundle);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.BaseApp, com.sony.imaging.app.fw.AppRoot, android.app.Activity
    public void onPause() {
        SPUtil.getInstance().setAppAlive(false);
        SPUtil.getInstance().setMovieRecordingCautionShown(false);
        AppLog.enter(TAG, AppLog.getMethodName());
        SPShootingState.getInstance().waitCameraSequence();
        SPUtil.getInstance().releaseAudioTrackController();
        SPFMicrophoneCtrl.getInstance().finishMicSetting();
        AppLog.exit(TAG, AppLog.getMethodName());
        super.onPause();
        SPUtil.getInstance().setRecoveryStatus(false);
        if (SPUtil.getInstance().getMediaCardStatus()) {
            String fileClause = DataBaseOperations.getInstance().getTotalFileFromLocalDB();
            AppLog.info(TAG, "TESTTAG updateSPFSyncContent fileClause=" + fileClause);
            if (fileClause == null || fileClause.length() < 1) {
                SPUtil.getInstance().clearStoreImageInfoList();
                DataBaseOperations.getInstance().exportDatabase();
                DatabaseUtil.terminate();
                return;
            } else {
                SoundPhotoDataBaseUtil.getInstance().startSyncFromPlayBack(true);
                AppLog.info("SoundPhoto.java", "TESTTAG onPause SoundPhoto.java Recover ENDS");
                synchronized (mWaitForRecovery) {
                    try {
                        mWaitForRecovery.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        AppLog.info(TAG, "TESTTAG Wait completed");
        SPUtil.getInstance().clearStoreImageInfoList();
        DataBaseOperations.getInstance().exportDatabase();
        DatabaseUtil.terminate();
    }
}
