package com.sony.imaging.app.soundphoto.playback;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import com.sony.imaging.app.base.common.EventParcel;
import com.sony.imaging.app.base.playback.PlayRootContainer;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.snsdirect.util.MimeType;
import com.sony.imaging.app.soundphoto.database.DataBaseOperations;
import com.sony.imaging.app.soundphoto.database.SoundPhotoDataBaseUtil;
import com.sony.imaging.app.soundphoto.playback.upload.util.DirectUploadUtilManager;
import com.sony.imaging.app.soundphoto.playback.viewmode.SPPseudoViewMode;
import com.sony.imaging.app.soundphoto.playback.viewmode.SPStillFolderViewMode;
import com.sony.imaging.app.soundphoto.util.AppLog;
import com.sony.imaging.app.soundphoto.util.SPUtil;
import com.sony.imaging.app.util.DatabaseUtil;

/* loaded from: classes.dex */
public class SPPlayRootContainer extends PlayRootContainer {
    public static final String ID_DELETE_MULTIPLE = "ID_DELETE_MULTIPLE";
    public static final String ID_DELETE_SOUND_DATA = "ID_DELETE_SOUND_DATA";
    public static final String ID_MAIN_LIST_LAYOUT = "ID_MAIN_LIST_LAYOUT";
    public static final String ID_PLAYBACK_MENU = "ID_PLAYBACK_MENU";
    public static final String ID_TRANSIT_4K_DISPLAY = "ID_TRANSIT_4K_DISPLAY";
    public static final String ID_UPLOAD_MULTIPLE = "ID_UPLOAD_MULTIPLE";
    public static final String ID_VOLUME_SETTING = "ID_VOLUME_SETTING";
    private static final String MSG_INIT_PSEUDO_VIEW = "initializeViewMode(PSEUDO)";
    private static final String MSG_INIT_STILL_VIEW = "initializeViewMode(STILL_FOLDER SounPhoto)";

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer, com.sony.imaging.app.base.playback.base.PlayStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        DataBaseOperations.sTotalCount = -1;
        DirectUploadUtilManager.getInstance().initialize(getActivity().getApplicationContext(), MimeType.IMAGE_SPHOTO);
        Log.d(this.TAG, "Db update program test start");
        super.onResume();
        SPUtil.getInstance().updateSPVolumeSetting();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer
    public String getStartFunction() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        SPUtil.getInstance().setSoundDataDeleteCalled(false);
        if (isMemoryCardAvailable()) {
            DatabaseUtil.setIsDatabaseChecked(false);
            DataBaseOperations.getInstance().refreshDatabase();
        } else {
            SPUtil.getInstance().releaseAudioTrackController();
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return super.getStartFunction();
    }

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer
    public void changeApp(String appName, Bundle bundle) {
        if (PlayRootContainer.ID_CHANGING_OUTPUT_MODE.equals(appName)) {
            SPUtil.getInstance().releaseAudioTrackController();
        }
        super.changeApp(appName, bundle);
    }

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer
    public void changeApp(String appName) {
        if (PlayRootContainer.ID_CHANGING_OUTPUT_MODE.equals(appName)) {
            SPUtil.getInstance().releaseAudioTrackController();
        }
        super.changeApp(appName);
    }

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer
    protected void initializeViewMode() {
        ContentsManager mgr = ContentsManager.getInstance();
        if (!isMemoryCardAvailable()) {
            AppLog.info(this.TAG, MSG_INIT_PSEUDO_VIEW);
            mgr.setViewMode(SPPseudoViewMode.class);
        } else {
            AppLog.info(this.TAG, MSG_INIT_STILL_VIEW);
            mgr.setViewMode(SPStillFolderViewMode.class);
        }
    }

    @Override // com.sony.imaging.app.base.playback.base.PlayStateBase, com.sony.imaging.app.base.playback.layout.IPlaybackTriggerFunction
    public boolean transitionShooting(EventParcel event) {
        SoundPhotoDataBaseUtil.getInstance().cancelUpdateSPFSyncContentFromPlayBack();
        SoundPhotoDataBaseUtil.getInstance().cancelBackgroundTask();
        return super.transitionShooting(event);
    }

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        SPUtil.getInstance().updateSPVolumeBackupSetting();
        DataBaseOperations.sTotalCount = -1;
        DirectUploadUtilManager.getInstance().clear();
        SPUtil.getInstance().setSoundDataDeleteCalled(false);
        SPUtil.getInstance().setMenuBootrequired(false);
        SPUtil.getInstance().releaseAudioTrackController();
        super.onPause();
        AppLog.exit(this.TAG, "onPause() ");
    }

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        int code = event.getScanCode();
        if (!CustomizableFunction.Unchanged.equals(func)) {
            return 0;
        }
        switch (code) {
            case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
            case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
            case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
            case AppRoot.USER_KEYCODE.SLIDE_AEL_FOCUS_CHANGED /* 589 */:
            case AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_CHANGED /* 597 */:
            case AppRoot.USER_KEYCODE.UM_S1 /* 613 */:
            case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
            case AppRoot.USER_KEYCODE.SHOOTING_MODE /* 624 */:
            case AppRoot.USER_KEYCODE.MODE_P /* 628 */:
            case AppRoot.USER_KEYCODE.MODE_A /* 629 */:
            case AppRoot.USER_KEYCODE.MODE_S /* 630 */:
            case AppRoot.USER_KEYCODE.MODE_M /* 631 */:
                changeToShooting(code);
                return 1;
            case AppRoot.USER_KEYCODE.MOVIE_REC /* 515 */:
            case AppRoot.USER_KEYCODE.UM_MOVIE_REC /* 616 */:
            case AppRoot.USER_KEYCODE.MOVIE_REC_2ND /* 637 */:
            case AppRoot.USER_KEYCODE.IR_MOVIE_REC /* 643 */:
                changeToShooting(AppRoot.USER_KEYCODE.MOVIE_REC);
                return 1;
            case AppRoot.USER_KEYCODE.S2_ON /* 518 */:
                return -1;
            default:
                int result = super.onConvertedKeyDown(event, func);
                return result;
        }
    }
}
