package com.sony.imaging.app.soundphoto.shooting.state.layout;

import com.sony.imaging.app.soundphoto.menu.layout.controller.ApplicationSettingsMenuController;
import com.sony.imaging.app.soundphoto.util.AppLog;
import com.sony.imaging.app.soundphoto.util.SPUtil;

/* loaded from: classes.dex */
public class SPAudioBufferAnimationController {
    public static final int IMAGE_CLICKED = 2;
    public static final int NO_SUFFICIENT_AUDIO_BUFFER = 0;
    public static final int PRE_AUDIO_BUFFER_AVAILABLE = 1;
    private static final String TAG = "SPAudioBufferAnimationController";
    private static SPAudioBufferAnimationController sParameterSettingInstance;
    private int mAudiobufferStatus = 0;
    private int mBlinkedImage = 0;
    private long mStartTime = 0;
    private long mImageClickTime = 0;
    public boolean mEarlyClick = false;
    public int startRedDot = 0;
    private ApplicationSettingsMenuController mAppSettingMenuController = null;
    private boolean isPostTimeDone = false;

    public int getStartRedDot() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.startRedDot;
    }

    public void setStartRedDot(int startRedDot) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        this.startRedDot = startRedDot;
    }

    public static SPAudioBufferAnimationController getInstance() {
        if (sParameterSettingInstance == null) {
            sParameterSettingInstance = new SPAudioBufferAnimationController();
        }
        return sParameterSettingInstance;
    }

    public int getAudiobufferStatus() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.mAudiobufferStatus;
    }

    private ApplicationSettingsMenuController getApplicationSettingController() {
        if (this.mAppSettingMenuController == null) {
            this.mAppSettingMenuController = ApplicationSettingsMenuController.getInstance();
        }
        return this.mAppSettingMenuController;
    }

    public void setAudiobufferStatus(int audiobufferStatus) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        this.mAudiobufferStatus = audiobufferStatus;
    }

    public int getmBlinkedImage() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.mBlinkedImage;
    }

    public void setmBlinkedImage(int mBlinkedImage) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        this.mBlinkedImage = mBlinkedImage;
    }

    public long getmStartTime() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.mStartTime;
    }

    public void setmStartTime(long mStartTime) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        this.mStartTime = mStartTime;
    }

    public int getCurrentAudioBufferTimeSec() {
        AppLog.enter(TAG, AppLog.getMethodName());
        long endTime = SPUtil.getInstance().getCurrentTimeinSec();
        AppLog.exit(TAG, AppLog.getMethodName());
        return (int) (endTime - this.mStartTime);
    }

    public int getPostClickTimeSec() {
        AppLog.enter(TAG, AppLog.getMethodName());
        long endImageClickTime = SPUtil.getInstance().getCurrentTimeinSec();
        AppLog.exit(TAG, AppLog.getMethodName());
        return (int) (endImageClickTime - this.mImageClickTime);
    }

    public long getmImageClickTime() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.mImageClickTime;
    }

    public void setmImageClickTime(long mImageClickTime) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        this.mImageClickTime = mImageClickTime;
    }

    public void executeTask() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (getPostClickTimeSec() < getApplicationSettingController().getPostDuration() && getmImageClickTime() > 0) {
            setAudiobufferStatus(2);
            if (getCurrentAudioBufferTimeSec() < getApplicationSettingController().getPreAudioTime()) {
                this.mEarlyClick = true;
                setmBlinkedImage(getPostClickTimeSec() + getApplicationSettingController().getPreAudioTime() + 1);
            } else {
                setmBlinkedImage(getPostClickTimeSec() + getApplicationSettingController().getPreAudioTime() + 1);
            }
        } else if (getCurrentAudioBufferTimeSec() < getApplicationSettingController().getPreAudioTime()) {
            setAudiobufferStatus(0);
            setmBlinkedImage(getCurrentAudioBufferTimeSec());
        } else {
            setAudiobufferStatus(1);
            setmBlinkedImage(getApplicationSettingController().getPreAudioTime());
            setmImageClickTime(0L);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public boolean isPostTimeDone() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.isPostTimeDone;
    }

    public void setPostTimeDone(boolean isPostTimeDone) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        this.isPostTimeDone = isPostTimeDone;
    }
}
