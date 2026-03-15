package com.sony.imaging.app.soundphoto.util;

import android.os.Environment;
import android.text.format.Time;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.common.AudioVolumeController;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.soundphoto.common.caution.SPInfo;
import com.sony.imaging.app.soundphoto.menu.layout.controller.ApplicationSettingsMenuController;
import com.sony.imaging.app.soundphoto.playback.audiotrack.controller.AudioTrackControl;
import com.sony.imaging.app.soundphoto.shooting.state.layout.SPAudioBufferAnimationController;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.DatabaseUtil;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.media.MediaInfo;
import com.sony.scalar.provider.AvindexStore;
import com.sony.scalar.sysutil.didep.Kikilog;
import java.util.LinkedList;
import java.util.Queue;

/* loaded from: classes.dex */
public class SPUtil {
    private static final String TAG = "SPUtil";
    private static SPUtil sParameterSettingInstance = null;
    private AudioTrackControl mAudioTrackControl;
    private boolean isSoundDataDeleteCalled = false;
    private boolean isMovieRecordingCautionShown = true;
    private boolean isAppAlive = true;
    private boolean isDeleteScreenVisible = false;
    private Queue<CameraEx.StoreImageInfo> mRecordingInfoArraySequence = null;
    private boolean isMenuBootrequired = false;
    private boolean isSoundPlayingState = false;
    private boolean isPlayBackKeyPressed = false;
    private String appState = BaseApp.APP_SHOOTING;
    boolean isRecoveryDone = false;

    public boolean isDeleteScreenVisible() {
        return this.isDeleteScreenVisible;
    }

    public void setDeleteScreenVisible(boolean isDeleteScreenVisible) {
        this.isDeleteScreenVisible = isDeleteScreenVisible;
    }

    public boolean isMovieRecordingCautionShown() {
        return this.isMovieRecordingCautionShown;
    }

    public void setMovieRecordingCautionShown(boolean isSoundPhotoExist) {
        this.isMovieRecordingCautionShown = isSoundPhotoExist;
    }

    public static SPUtil getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (sParameterSettingInstance == null) {
            sParameterSettingInstance = new SPUtil();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return sParameterSettingInstance;
    }

    public int getCautionId() {
        AppLog.enter(TAG, AppLog.getMethodName());
        int cautionID = 0;
        if (DatabaseUtil.checkMediaStatus() == DatabaseUtil.MediaStatus.NO_CARD) {
            cautionID = SPInfo.CAUTION_ID_DLAPP_NO_MEMEORY_CARD_SHOOTING;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return cautionID;
    }

    public String getFilePathOnMedia() {
        AppLog.enter(TAG, AppLog.getMethodName());
        String mFilePathOnMedia = null;
        String[] mMediaIds = AvindexStore.getExternalMediaIds();
        AppLog.trace(TAG, "========== Files path on Media regarding this application   mMediaIds[0] ============== " + mMediaIds);
        if (mMediaIds[0] != null) {
            MediaInfo mInfo = AvindexStore.getMediaInfo(mMediaIds[0]);
            int mMediaId = mInfo.getMediaType();
            AppLog.trace(TAG, "========== Files path on Media regarding this application MediaInfo getMediaType ============== " + mMediaId);
            if (2 == mMediaId) {
                mFilePathOnMedia = Environment.getExternalStorageDirectory().getAbsolutePath() + SPConstants.MS_CARD_PATH;
                AppLog.trace(TAG, "========== Files path on Media regarding this application MediaInfo getMediaType  MS_CARD_PATH  ============== /MSSONY/CAM_APPS/APP_SDPH");
            } else if (1 == mMediaId) {
                mFilePathOnMedia = Environment.getExternalStorageDirectory().getAbsolutePath() + SPConstants.SD_CARD_PATH;
                AppLog.trace(TAG, "========== Files path on Media regarding this application MediaInfo getMediaType SDCARD PATH ============== /PRIVATE/SONY/APP_SDPH");
            } else if (mMediaId == 0) {
                AppLog.trace(TAG, "========== Files path on Media regarding this application MediaInfo getMediaType MEDIA_TYPE_UNKNOWN   ============== 0");
                switch (MediaNotificationManager.getInstance().getInsertedMediaType()) {
                    case 1:
                        mFilePathOnMedia = Environment.getExternalStorageDirectory().getAbsolutePath() + SPConstants.SD_CARD_PATH;
                        break;
                    case 2:
                        mFilePathOnMedia = Environment.getExternalStorageDirectory().getAbsolutePath() + SPConstants.MS_CARD_PATH;
                        break;
                    default:
                        AppLog.trace(TAG, "========== Files path on Media regarding this application MediaInfo getMediaType MS_CARD  PATH ============== /PRIVATE/SONY/APP_SDPH");
                        break;
                }
            }
            AppLog.trace(TAG, "========== Files path on Media regarding this application ============== " + mFilePathOnMedia);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return mFilePathOnMedia;
    }

    public void setSoundDataDeleteCalled(boolean isSoundDeleteFired) {
        this.isSoundDataDeleteCalled = isSoundDeleteFired;
    }

    public boolean isSoundDataDeletePerforming() {
        return this.isSoundDataDeleteCalled;
    }

    public long getCurrentTimeinSec() {
        Time t1 = new Time();
        t1.setToNow();
        return t1.toMillis(true) / 1000;
    }

    public boolean isMenuBootrequired() {
        return this.isMenuBootrequired;
    }

    public void setMenuBootrequired(boolean transitToMenu) {
        this.isMenuBootrequired = transitToMenu;
    }

    public AudioTrackControl getAudioTrackController() {
        if (this.mAudioTrackControl == null || this.mAudioTrackControl.getAudioTrack() == null) {
            this.mAudioTrackControl = new AudioTrackControl();
            this.mAudioTrackControl.intialize();
        }
        return this.mAudioTrackControl;
    }

    public AudioTrackControl getAudioTrackControllersStatus() {
        return this.mAudioTrackControl;
    }

    public void releaseAudioTrackController() {
        if (this.mAudioTrackControl != null) {
            this.mAudioTrackControl.release();
            this.mAudioTrackControl = null;
            getInstance().setSoundPlayingState(false);
        }
    }

    public void printKikiLog(Integer kikilogId) {
        Kikilog.Options options = new Kikilog.Options();
        options.getClass();
        options.recType = 32;
        Kikilog.setUserLog(kikilogId.intValue(), options);
    }

    public boolean isSoundPlayingState() {
        return this.isSoundPlayingState;
    }

    public void setSoundPlayingState(boolean playingState) {
        if (!playingState) {
            AppLog.info(TAG, "SoundPlayingState set to pause");
        }
        this.isSoundPlayingState = playingState;
    }

    public boolean isPlayBackKeyPressed() {
        return this.isPlayBackKeyPressed;
    }

    public void setPlayBackKeyPressed(boolean isPressed) {
        this.isPlayBackKeyPressed = isPressed;
    }

    public void notifyImageClicked() {
        SPAudioBufferAnimationController.getInstance().setAudiobufferStatus(2);
        SPAudioBufferAnimationController.getInstance().setPostTimeDone(true);
        SPAudioBufferAnimationController.getInstance().setmImageClickTime(getInstance().getCurrentTimeinSec());
        if (SPAudioBufferAnimationController.getInstance().getCurrentAudioBufferTimeSec() < ApplicationSettingsMenuController.getInstance().getPreAudioTime()) {
            SPAudioBufferAnimationController.getInstance().mEarlyClick = true;
        } else {
            SPAudioBufferAnimationController.getInstance().mEarlyClick = false;
        }
    }

    public void updateSPVolumeSetting() {
        AppLog.enter(TAG, AppLog.getMethodName());
        int volumeLevel = BackUpUtil.getInstance().getPreferenceInt(SPBackUpKey.VOLUME_LEVEL_KEY, -1);
        if (-1 != volumeLevel) {
            AudioVolumeController.getInstance().setInt(AudioVolumeController.TAG_AUDIO_VOLUME, volumeLevel);
        } else {
            updateSPVolumeBackupSetting();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void updateSPVolumeBackupSetting() {
        AppLog.enter(TAG, AppLog.getMethodName());
        int volumeLevel = AudioVolumeController.getInstance().getInt(AudioVolumeController.TAG_AUDIO_VOLUME);
        BackUpUtil.getInstance().setPreference(SPBackUpKey.VOLUME_LEVEL_KEY, Integer.valueOf(volumeLevel));
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public Queue<CameraEx.StoreImageInfo> getmRecordingInfoArraySequence() {
        AppLog.info(TAG, AppLog.getMethodName());
        return this.mRecordingInfoArraySequence;
    }

    private void initilizeStoreImageInfoList() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mRecordingInfoArraySequence == null) {
            this.mRecordingInfoArraySequence = new LinkedList();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void addToStoreImageInfoList(CameraEx.StoreImageInfo info) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mRecordingInfoArraySequence == null) {
            initilizeStoreImageInfoList();
        }
        this.mRecordingInfoArraySequence.add(info);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void clearStoreImageInfoList() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mRecordingInfoArraySequence != null) {
            this.mRecordingInfoArraySequence.clear();
            this.mRecordingInfoArraySequence = null;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public String getAppState() {
        return this.appState;
    }

    public void setAppState(String changedAppState) {
        this.appState = changedAppState;
    }

    public boolean getMediaCardStatus() {
        int mediaState = MediaNotificationManager.getInstance().getMediaState();
        if (mediaState != 2) {
            return false;
        }
        return true;
    }

    public void setRecoveryStatus(boolean capturingStatus) {
        this.isRecoveryDone = capturingStatus;
    }

    public boolean isRecoverDone() {
        AppLog.info(TAG, "TESTTAG isRecoveryDone == " + this.isRecoveryDone);
        return this.isRecoveryDone;
    }

    public boolean isAppAlive() {
        return this.isAppAlive;
    }

    public void setAppAlive(boolean isAppAlive) {
        this.isAppAlive = isAppAlive;
    }
}
