package com.sony.imaging.app.soundphoto.playback.player.layout;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.playback.contents.ContentInfo;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.base.playback.layout.BrowserSingleLayout;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.soundphoto.R;
import com.sony.imaging.app.soundphoto.database.DataBaseOperations;
import com.sony.imaging.app.soundphoto.database.SoundPhotoBO;
import com.sony.imaging.app.soundphoto.database.SoundPhotoDataBaseUtil;
import com.sony.imaging.app.soundphoto.menu.layout.controller.AutoPlayBackStatusController;
import com.sony.imaging.app.soundphoto.playback.SPPlayRootContainer;
import com.sony.imaging.app.soundphoto.playback.audiotrack.controller.AudioTrackControl;
import com.sony.imaging.app.soundphoto.playback.audiotrack.controller.AudioTrackControlListener;
import com.sony.imaging.app.soundphoto.playback.menu.layout.ITransitToPlaybackMenu;
import com.sony.imaging.app.soundphoto.shooting.state.SPShootingState;
import com.sony.imaging.app.soundphoto.util.AppLog;
import com.sony.imaging.app.soundphoto.util.SPConstants;
import com.sony.imaging.app.soundphoto.util.SPUtil;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/* loaded from: classes.dex */
public class SPBrowserPlayingLayout extends BrowserSingleLayout implements NotificationListener, AudioTrackControlListener {
    protected static final long POST_DELAYED_TIME = 500;
    protected boolean isPlaying;
    protected View mCurrentView;
    protected DelayInAudioPlay mDelayInAudioPlay;
    protected NotificationListener mDisplayChangedListener;
    ITransitToPlaybackMenu mEachPbTrigger;
    ArrayList<SoundPhotoBO> spBOList;
    protected boolean spf_info;
    public static String TAG = "SPBrowserPlayingLayout";
    protected static final String[] TAGS_OBSERVE_DISPLAY = {DisplayModeObserver.TAG_DEVICE_CHANGE, DisplayModeObserver.TAG_4K_PLAYBACK_STATUS_CHANGE};
    protected static String[] TAGS = {SPConstants.DELETE_IMAGE_DATABASE_UPDATE, SPPlayRootContainer.ID_DELETE_SOUND_DATA, SPConstants.PLAY_TIMER_UPDATE, SPConstants.PLAY_TIMER_STOP};
    protected final int FRAME_PER_SECOND = 33;
    protected final int MINIMUM_MILLI = 1000;
    protected int maxAudioLength = 10000;
    protected final int DELAY_AUDIO = 1500;
    protected SeekBar mprogressBar = null;
    protected ImageView mPlayPauseStatusImageView = null;
    protected TextView pb_duration_text = null;
    protected int mCurrentReadPosition = 0;
    protected SetStateToStopRunnable mSetStateToStop = new SetStateToStopRunnable();
    protected int mdisplayMode = 1;
    protected boolean contentIDChanged = false;
    protected AudioTrackControl mAudioTrackControl = null;
    protected int mMaxProgressLimit = 0;

    public SPBrowserPlayingLayout() {
        this.isPlaying = false;
        this.spf_info = false;
        this.spf_info = false;
        this.isPlaying = false;
    }

    public void setEachPbTrigger(ITransitToPlaybackMenu trigger) {
        this.mEachPbTrigger = trigger;
        if (!SPUtil.getInstance().isSoundPlayingState() && SPUtil.getInstance().getAudioTrackControllersStatus() == null) {
            this.contentIDChanged = true;
            this.spf_info = false;
        }
    }

    @Override // com.sony.imaging.app.base.playback.layout.BrowserSingleLayout, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        SPUtil.getInstance().setSoundPlayingState(true);
        SoundPhotoDataBaseUtil.getInstance().cancelUpdateSPFSyncContentFromPlayBack();
        SoundPhotoDataBaseUtil.getInstance().cancelBackgroundTask();
        return this.mEachPbTrigger.transitionToMenuState() ? 1 : -1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        CautionUtilityClass.getInstance().executeTerminate();
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mCurrentView = (ViewGroup) obtainViewFromPool(getLayoutResource());
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.mCurrentView;
    }

    protected void initializeView() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mdisplayMode = DisplayModeObserver.getInstance().getActiveDispMode(1);
        if (this.mCurrentView != null && this.mdisplayMode == 1) {
            this.mprogressBar = (SeekBar) this.mCurrentView.findViewById(R.id.pb_progressBar);
            if (this.mprogressBar != null) {
                this.mprogressBar.setOnTouchListener(new View.OnTouchListener() { // from class: com.sony.imaging.app.soundphoto.playback.player.layout.SPBrowserPlayingLayout.1
                    @Override // android.view.View.OnTouchListener
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });
                this.mprogressBar.setFocusableInTouchMode(false);
                this.mprogressBar.setFocusable(false);
            }
            if (this.mPlayPauseStatusImageView == null) {
                this.mPlayPauseStatusImageView = (ImageView) this.mCurrentView.findViewById(R.id.play_pause);
            }
            if (this.pb_duration_text == null) {
                this.pb_duration_text = (TextView) this.mCurrentView.findViewById(R.id.pb_duration_text);
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    protected void intializeAudioTrack() {
        this.mAudioTrackControl = SPUtil.getInstance().getAudioTrackController();
    }

    @Override // com.sony.imaging.app.base.playback.layout.BrowserSingleLayout, com.sony.imaging.app.base.playback.layout.SingleLayoutBase, com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (SPUtil.getInstance().getMediaCardStatus() && SPShootingState.isSyncRequest) {
            SoundPhotoDataBaseUtil.getInstance().startSyncFromPlayBack(true);
            SPShootingState.isSyncRequest = false;
        }
        super.onResume();
        if (SPUtil.getInstance().getAudioTrackController() != null && SPUtil.getInstance().getAudioTrackController().getAudioTrack() != null) {
            if (SPUtil.getInstance().getAudioTrackController().getAudioTrack().getPlayState() != 3) {
                this.isPlaying = false;
            } else {
                this.isPlaying = true;
            }
            AppLog.trace(TAG, "isPlaying  " + SPUtil.getInstance().getAudioTrackController().getAudioTrack().getPlayState());
        }
        Log.d(TAG, "setNotificationListener start");
        CameraNotificationManager.getInstance().setNotificationListener(this);
        Log.d(TAG, "initializeView start");
        initializeView();
        if (this.contentIDChanged) {
            this.contentIDChanged = false;
            Log.d(TAG, "setStatusToResetOnChangeContent start");
            setStatusToResetOnChangeContent();
        } else {
            Log.d(TAG, "setinitialValues start");
            setinitialValues();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.playback.layout.BrowserSingleLayout, com.sony.imaging.app.base.playback.layout.SingleLayoutBase, com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        SoundPhotoDataBaseUtil.getInstance().cancelUpdateSPFSyncContent();
        SoundPhotoDataBaseUtil.getInstance().cancelBackgroundTask();
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        deinitializeResources();
        super.onPause();
    }

    public void releaseAudioTrackController() {
        SPUtil.getInstance().releaseAudioTrackController();
        if (getHandler() != null && this.mSetStateToStop != null) {
            getHandler().removeCallbacks(this.mSetStateToStop);
        }
    }

    protected void setinitialValues() {
        if (this.mPlayPauseStatusImageView != null) {
            if (!this.isPlaying) {
                releaseImageViewDrawable(this.mPlayPauseStatusImageView);
                this.mPlayPauseStatusImageView.setImageResource(R.drawable.playstatus_002);
            } else {
                releaseImageViewDrawable(this.mPlayPauseStatusImageView);
                this.mPlayPauseStatusImageView.setImageResource(R.drawable.playstatus_001);
            }
        }
        updateSoundDurration();
        handleGuideDataState();
    }

    protected void updateSoundDurration() {
        if (this.pb_duration_text != null && this.mprogressBar != null) {
            this.pb_duration_text.setText("" + displaySecondFormat(this.mCurrentReadPosition));
            this.mprogressBar.setProgress(this.mCurrentReadPosition);
        }
    }

    protected void handleGuideDataState() {
        if (DisplayModeObserver.getInstance().is4kDeviceAvailable()) {
            int status = DisplayModeObserver.getInstance().get4kOutputStatus();
            AppLog.info(TAG, "handleGuideDataState status " + status);
            if (2 == status || 6 == status || 4 == status) {
                if (this.isPlaying) {
                    setFooterGuideData(new FooterGuideDataResId(getActivity(), R.string.STRID_FUNC_SOUNDPHTO_FOOTER_GUIDE_4K_PLAYING_PAUSE, R.string.STRID_FUNC_SOUNDPHTO_FOOTER_GUIDE_4K_PLAYING_PAUSE));
                    return;
                } else {
                    setFooterGuideData(new FooterGuideDataResId(getActivity(), R.string.STRID_FUNC_SOUNDPHTO_FOOTER_GUIDE_4K_PLAYING_PLAY, R.string.STRID_FUNC_SOUNDPHTO_FOOTER_GUIDE_4K_PLAYING_PLAY));
                    return;
                }
            }
            if (this.isPlaying) {
                setFooterGuideData(new FooterGuideDataResId(getActivity(), R.string.STRID_FUNC_SOUNDPHTO_FOOTER_GUIDE_4K_OUTPUT_PAUSE, R.string.STRID_FUNC_SOUNDPHTO_FOOTER_GUIDE_4K_OUTPUT_PAUSE));
                return;
            } else {
                setFooterGuideData(new FooterGuideDataResId(getActivity(), R.string.STRID_FUNC_SOUNDPHTO_FOOTER_GUIDE_4K_OUTPUT_PLAY, R.string.STRID_FUNC_SOUNDPHTO_FOOTER_GUIDE_4K_OUTPUT_PLAY));
                return;
            }
        }
        if (this.isPlaying) {
            setFooterGuideData(new FooterGuideDataResId(getActivity(), R.string.STRID_FUNC_SOUNDPHOTO_SINGLE_PAUSE_FOOTER, R.string.STRID_FUNC_SOUNDPHOTO_SINGLE_PAUSE_FOOTER));
        } else {
            setFooterGuideData(new FooterGuideDataResId(getActivity(), R.string.STRID_FUNC_SOUNDPHOTO_SINGLE_PLAY_FOOTER, R.string.STRID_FUNC_SOUNDPHOTO_SINGLE_PLAY_FOOTER));
        }
    }

    @SuppressLint({"SimpleDateFormat"})
    protected String displaySecondFormat(int milliSecond) {
        AppLog.enter(TAG, AppLog.getMethodName());
        Calendar cal = Calendar.getInstance();
        cal.clear(12);
        cal.clear(13);
        cal.set(12, 0);
        cal.set(13, milliSecond / 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        String str = sdf.format(cal.getTime());
        AppLog.trace(TAG, "getRecordingTime: " + str);
        AppLog.exit(TAG, AppLog.getMethodName());
        return str;
    }

    @Override // com.sony.imaging.app.base.playback.layout.BrowserSingleLayout, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        AppLog.enter(TAG, "pushedCenterKey()");
        cancelDelayTimerTask();
        playPauseStatus();
        AppLog.exit(TAG, "pushedCenterKey()");
        return 1;
    }

    protected void playPauseStatus() {
        AppLog.enter(TAG, "playPauseStatus()");
        if (this.mAudioTrackControl == null || this.mAudioTrackControl.getAudioTrack() == null) {
            intializeAudioTrack();
            this.spf_info = false;
        }
        if (!this.spf_info) {
            this.spf_info = loadAudioTrack();
        }
        AppLog.enter(TAG, "playPauseStatus()  spf_info" + this.spf_info);
        if (true == this.spf_info) {
            handlePlayingState();
        }
        AppLog.exit(TAG, "playPauseStatus()");
    }

    protected boolean loadAudioTrack() {
        AppLog.enter(TAG, "loadAudioTrack()");
        ContentsManager mgr = ContentsManager.getInstance();
        ContentInfo info = mgr.getContentInfo(mgr.getContentsId());
        String dirName = info.getString("DCF_TBLDirName");
        String fileName = info.getString("DCF_TBLFileName");
        AppLog.trace(TAG, "loadAudioTrack dirName: " + dirName + ", fileName = " + fileName);
        AppLog.exit(TAG, "loadAudioTrack()");
        String filePath = SPConstants.ROOT_FOLDER_PATH + dirName + SPConstants.FILE_SEPARATER + fileName;
        Log.d(TAG, "loadAudio filePath: " + filePath);
        if (this.mAudioTrackControl == null || this.mAudioTrackControl.getAudioTrack() == null) {
            intializeAudioTrack();
        }
        if (2 == this.mAudioTrackControl.getAudioTrack().getBufferState()) {
            this.spf_info = this.mAudioTrackControl.loadAudio(filePath, this);
        }
        AppLog.exit(TAG, "loadAudioTrack()");
        return this.spf_info;
    }

    protected void handlePlayingState() {
        if (!this.isPlaying) {
            setStatusToPlay();
        } else {
            setStatusToPause();
        }
    }

    protected void setStatusToPause() {
        if (this.mPlayPauseStatusImageView != null && this.mdisplayMode == 1) {
            releaseImageViewDrawable(this.mPlayPauseStatusImageView);
            this.mPlayPauseStatusImageView.setImageResource(R.drawable.playstatus_002);
        }
        if (this.isPlaying && this.mAudioTrackControl != null && this.mAudioTrackControl.getAudioTrack() != null) {
            this.mAudioTrackControl.pauseAudio();
        }
        this.isPlaying = false;
        handleGuideDataState();
    }

    protected void setStatusToPlay() {
        this.mMaxProgressLimit = (((int) this.mAudioTrackControl.getAudioTrack().getWriteMarkerPosition()) / 33) * 1000;
        AppLog.info(TAG, " Value check of Audio Avai mMaxProgressLimit = " + this.mMaxProgressLimit);
        if (this.mPlayPauseStatusImageView != null && this.mdisplayMode == 1) {
            releaseImageViewDrawable(this.mPlayPauseStatusImageView);
            this.mPlayPauseStatusImageView.setImageResource(R.drawable.playstatus_001);
        }
        if (this.mprogressBar != null) {
            this.maxAudioLength = this.mMaxProgressLimit;
            this.mprogressBar.setMax(this.mMaxProgressLimit);
        }
        this.mAudioTrackControl.playAudio();
        this.isPlaying = true;
        handleGuideDataState();
    }

    @Override // com.sony.imaging.app.base.playback.layout.BrowserSingleLayout, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDeleteFuncKey() {
        setStatusToPause();
        return super.pushedDeleteFuncKey();
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    public boolean transitionDeleteThis() {
        if (this.mPbTrigger != null) {
            return this.mPbTrigger.transitionDeleteThis();
        }
        return false;
    }

    @Override // com.sony.imaging.app.base.playback.layout.BrowserSingleLayout, com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase
    public boolean previousContents() {
        this.spf_info = false;
        AppLog.info(TAG, " notifiedPlayTime previousContents = " + this.mCurrentReadPosition);
        if (this.mCurrentReadPosition <= 1000) {
            boolean returnStatus = super.previousContents();
            setStatusToResetOnChangeContent();
            return returnStatus;
        }
        setStatusToResetOnChangeContent();
        return false;
    }

    protected void cancelDelayTimerTask() {
        if (this.mDelayInAudioPlay != null && getHandler() != null) {
            getHandler().removeCallbacks(this.mDelayInAudioPlay);
            this.mDelayInAudioPlay = null;
        }
    }

    protected void startDelayTimerTask() {
        if (this.mDelayInAudioPlay == null) {
            this.mDelayInAudioPlay = new DelayInAudioPlay();
        }
        getHandler().postDelayed(this.mDelayInAudioPlay, 1500L);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class DelayInAudioPlay implements Runnable {
        DelayInAudioPlay() {
        }

        @Override // java.lang.Runnable
        public void run() {
            if (SPUtil.getInstance().getMediaCardStatus()) {
                SPBrowserPlayingLayout.this.playAudioForAutoPlay();
            }
        }
    }

    public void playAudioForAutoPlay() {
        if (SPUtil.getInstance().isAppAlive()) {
            if (AutoPlayBackStatusController.getInstance().isAutoPlayBackOn() && BaseApp.APP_PLAY.equals(SPUtil.getInstance().getAppState())) {
                AppLog.info(TAG, "DelayInAudioPlay :: restartAudio()");
                restartAudio();
            } else {
                AppLog.info(TAG, "DelayInAudioPlay :: restartAudio() not able to play due to transit to shooting");
            }
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        int result = super.onConvertedKeyDown(event, func);
        switch (event.getScanCode()) {
            case AppRoot.USER_KEYCODE.SK2 /* 513 */:
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                SPUtil.getInstance().setSoundDataDeleteCalled(false);
            default:
                return result;
        }
    }

    protected void setStatusToResetOnChangeContent() {
        stopProgress();
        SPUtil.getInstance().releaseAudioTrackController();
        if (SPUtil.getInstance().isPlayBackKeyPressed()) {
            SPUtil.getInstance().setPlayBackKeyPressed(false);
            playAudioForAutoPlay();
        } else if (AutoPlayBackStatusController.getInstance().isAutoPlayBackOn()) {
            cancelDelayTimerTask();
            startDelayTimerTask();
            this.isPlaying = true;
        }
        this.mCurrentReadPosition = 0;
        updateUIonReset();
        handleGuideDataState();
    }

    protected void updateUIonReset() {
        if (this.mPlayPauseStatusImageView != null && this.mprogressBar != null && this.mdisplayMode == 1) {
            releaseImageViewDrawable(this.mPlayPauseStatusImageView);
            if (this.isPlaying) {
                this.mPlayPauseStatusImageView.setImageResource(R.drawable.playstatus_001);
            } else {
                this.mPlayPauseStatusImageView.setImageResource(R.drawable.playstatus_002);
            }
        }
    }

    protected void restartAudio() {
        this.isPlaying = false;
        playPauseStatus();
    }

    @Override // com.sony.imaging.app.base.playback.layout.BrowserSingleLayout, com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase
    public boolean nextContents() {
        boolean isChanged = super.nextContents();
        this.spf_info = false;
        setStatusToResetOnChangeContent();
        return isChanged;
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        releaseAudioTrackController();
        deinitializeResources();
        if (getHandler() != null && this.mSetStateToStop != null) {
            getHandler().removeCallbacks(this.mSetStateToStop);
        }
        super.closeLayout();
    }

    @Override // com.sony.imaging.app.base.playback.layout.SingleLayoutBase, com.sony.imaging.app.base.common.IModableLayout
    public void onLayoutModeChanged(int device, int displayMode) {
        this.mdisplayMode = displayMode;
        if (!SPUtil.getInstance().isDeleteScreenVisible()) {
            super.onLayoutModeChanged(device, displayMode);
        }
    }

    public void deinitializeResources() {
        AppLog.enter(TAG, "deinitializeResources()");
        if (this.mCurrentView != null) {
            this.mprogressBar = null;
            releaseImageViewDrawable(this.mPlayPauseStatusImageView);
            this.mPlayPauseStatusImageView = null;
            this.mCurrentView.destroyDrawingCache();
            this.mCurrentView = null;
            this.pb_duration_text = null;
        }
        AppLog.enter(TAG, "deinitializeResources()");
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.Layout
    public Drawable onGetBackground() {
        return BACKGROUND_TRANSPARENT;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase
    public void updateDisplay() {
        updatePosition();
        super.updateDisplay();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(TAG, "onDestroy()");
        deinitializeResources();
        this.isPlaying = false;
        AppLog.exit(TAG, "onDestroy()");
        super.onDestroy();
    }

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        AppLog.enter(TAG, "onDestroyView()");
        deinitializeResources();
        AppLog.exit(TAG, "onDestroyView()");
        super.onDestroyView();
    }

    protected void releaseImageViewDrawable(ImageView imageView) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (imageView != null) {
            imageView.setBackgroundResource(0);
            imageView.setImageBitmap(null);
            if (imageView.getDrawable() != null) {
                imageView.getDrawable().setCallback(null);
            }
            imageView.setImageDrawable(null);
            if (imageView.getDrawable() != null) {
                imageView.getDrawable().setCallback(null);
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    protected void updatePosition() {
        ContentsManager mgr = ContentsManager.getInstance();
        if (mgr.getContentsId()._id == -1) {
            mgr.moveToFirst();
            if (SPConstants.CURRENT_SELECTED_ID_POSITION == mgr.getContentsTotalCount()) {
                SPConstants.CURRENT_SELECTED_ID_POSITION--;
                mgr.moveToLast();
            } else if (SPConstants.CURRENT_SELECTED_ID_POSITION >= 0) {
                mgr.moveTo(SPConstants.CURRENT_SELECTED_ID_POSITION);
            }
        }
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        AppLog.enter(TAG, "onNotify tag=  " + tag);
        if (tag.equals(SPConstants.DELETE_IMAGE_DATABASE_UPDATE) && DataBaseOperations.getInstance().getTotalFiles() > 0) {
            this.contentIDChanged = true;
            this.spf_info = false;
            updateView();
        } else if (tag.equals(SPPlayRootContainer.ID_DELETE_SOUND_DATA)) {
            SPUtil.getInstance().setSoundDataDeleteCalled(true);
            SPUtil.getInstance().setSoundPlayingState(false);
            pushedDeleteFuncKey();
        }
        AppLog.exit(TAG, "onNotify tag=  " + tag);
    }

    protected void stopProgress() {
        if (this.mprogressBar != null && this.pb_duration_text != null) {
            this.mprogressBar.setProgress(0);
            this.pb_duration_text.setText(displaySecondFormat(0));
        }
        if (this.isPlaying) {
            if (this.mAudioTrackControl != null && this.mAudioTrackControl.getAudioTrack() != null && this.mAudioTrackControl.getAudioTrack().getPlayState() != 1) {
                Log.d(TAG, "audioTrack.stop()");
                this.mAudioTrackControl.stopAudio();
            }
            this.isPlaying = false;
        }
    }

    @Override // com.sony.imaging.app.base.playback.layout.BrowserSingleLayout
    protected FooterGuideDataResId getFooterGuideDataResId() {
        if (Environment.getVersionOfHW() == 1) {
            FooterGuideDataResId resId = new FooterGuideDataResId(getActivity(), 0, android.R.string.heavy_weight_notification_detail);
            return resId;
        }
        if (!DisplayModeObserver.getInstance().is4kDeviceAvailable()) {
            return null;
        }
        int status = DisplayModeObserver.getInstance().get4kOutputStatus();
        if (2 == status) {
            FooterGuideDataResId resId2 = new FooterGuideDataResId(getActivity(), android.R.string.kg_too_many_failed_password_attempts_dialog_message);
            return resId2;
        }
        if (1 != status) {
            return null;
        }
        FooterGuideDataResId resId3 = new FooterGuideDataResId(getActivity(), android.R.string.kg_too_many_failed_password_attempts_dialog_message);
        return resId3;
    }

    /* loaded from: classes.dex */
    protected class SPDisplayChangedListener implements NotificationListener {
        protected SPDisplayChangedListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return SPBrowserPlayingLayout.TAGS_OBSERVE_DISPLAY;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (!SPUtil.getInstance().isDeleteScreenVisible()) {
                SPBrowserPlayingLayout.this.handleGuideDataState();
            }
        }
    }

    @Override // com.sony.imaging.app.base.playback.layout.BrowserSingleLayout
    protected void registerDisplayChangedListener() {
        if (((BaseApp) getActivity()).is4kPlaybackSupported()) {
            this.mDisplayChangedListener = new SPDisplayChangedListener();
            DisplayModeObserver.getInstance().setNotificationListener(this.mDisplayChangedListener);
        }
    }

    @Override // com.sony.imaging.app.base.playback.layout.BrowserSingleLayout
    protected void unregisterDisplayChangedListener() {
        if (this.mDisplayChangedListener != null) {
            DisplayModeObserver.getInstance().removeNotificationListener(this.mDisplayChangedListener);
            this.mDisplayChangedListener = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.layout.BrowserSingleLayout, com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    public int getFooterGuideResource() {
        int displayMode = DisplayModeObserver.getInstance().getActiveDispMode(1);
        if (displayMode == 1) {
            return R.id.cmn_footer_guide;
        }
        int id = super.getFooterGuideResource();
        return id;
    }

    @Override // com.sony.imaging.app.soundphoto.playback.audiotrack.controller.AudioTrackControlListener
    public void notifiedPlayTime(int currentPlayTime) {
        this.mCurrentReadPosition = currentPlayTime;
        AppLog.info(TAG, " notifiedPlayTime = " + this.mCurrentReadPosition);
        if (this.mCurrentReadPosition >= this.maxAudioLength) {
            AppLog.info(TAG, " maxAudioLength  = " + this.mCurrentReadPosition);
            this.mCurrentReadPosition = this.maxAudioLength;
        }
        updateSoundDurration();
    }

    @Override // com.sony.imaging.app.soundphoto.playback.audiotrack.controller.AudioTrackControlListener
    public void finishedUpdateStatus() {
        Log.d("TAG-finishedUpdateStatus", "finishedUpdateStatus");
        if (this.mSetStateToStop == null) {
            this.mSetStateToStop = new SetStateToStopRunnable();
        }
        this.mCurrentReadPosition = this.mMaxProgressLimit;
        if (((AppRoot) getActivity()) != null) {
            getHandler().postDelayed(this.mSetStateToStop, POST_DELAYED_TIME);
            return;
        }
        if (this.mCurrentReadPosition > 0) {
            this.mCurrentReadPosition = 0;
            Log.d("TAG-finishedUpdateStatus", "mCurrentReadPosition > 0");
            stopProgress();
            this.isPlaying = false;
            setinitialValues();
            AppLog.info(TAG, "finishedUpdateStatus");
            CameraNotificationManager.getInstance().requestNotify(SPConstants.PLAY_TIMER_STOP);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class SetStateToStopRunnable implements Runnable {
        SetStateToStopRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            Log.d("TAG-finishedUpdateStatus", "SetStateToStopRunnable");
            if (SPBrowserPlayingLayout.this.mCurrentReadPosition > 0) {
                SPBrowserPlayingLayout.this.mCurrentReadPosition = 0;
                Log.d("TAG-finishedUpdateStatus", "mCurrentReadPosition > 0");
                SPBrowserPlayingLayout.this.stopProgress();
                SPBrowserPlayingLayout.this.isPlaying = false;
                SPBrowserPlayingLayout.this.setinitialValues();
                AppLog.info(SPBrowserPlayingLayout.TAG, "SetStateToStopRunnable");
                CameraNotificationManager.getInstance().requestNotify(SPConstants.PLAY_TIMER_STOP);
            }
        }
    }

    @Override // com.sony.imaging.app.base.playback.layout.SingleLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPbZoomFuncMinus() {
        cancelDelayTimerTask();
        return super.pushedPbZoomFuncMinus();
    }
}
