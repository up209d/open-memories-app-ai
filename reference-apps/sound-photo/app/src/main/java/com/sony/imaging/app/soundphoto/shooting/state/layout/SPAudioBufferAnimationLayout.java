package com.sony.imaging.app.soundphoto.shooting.state.layout;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.soundphoto.R;
import com.sony.imaging.app.soundphoto.common.caution.SPInfo;
import com.sony.imaging.app.soundphoto.menu.layout.controller.ApplicationSettingsMenuController;
import com.sony.imaging.app.soundphoto.shooting.audiorecorder.AudioRecorder;
import com.sony.imaging.app.soundphoto.util.AppLog;
import java.util.Timer;
import java.util.TimerTask;

/* loaded from: classes.dex */
public class SPAudioBufferAnimationLayout extends Layout {
    private static final int DEFAULT_LEFT_MARGIN = 100;
    private static final int HALF_DOT_WIDTH = 15;
    public static final String TAG = "SPAudioBufferAnimationLayout";
    RelativeLayout mAnimationStrip;
    private ImageView[] mImageViewArray;
    Timer mTimer;
    MyTimerTask mTimerTask;
    private ImageView mikeview;
    private View mCurrentView = null;
    int PRE_AUDIO_TIMING = 8;
    int POST_AUDIO_TIMING = 2;
    int totalTime = 0;
    private boolean camerashow = false;
    int mAudiobufferState = 0;
    int mDisplayMode = 0;
    boolean isAllWhiteDone = false;
    private int[] imageId = {R.id.anim0image, R.id.anim1image, R.id.anim2image, R.id.anim3image, R.id.anim4image, R.id.anim5image, R.id.anim6image, R.id.anim7image, R.id.anim8image, R.id.anim9image, R.id.anim10image};
    Handler _handler = new Handler() { // from class: com.sony.imaging.app.soundphoto.shooting.state.layout.SPAudioBufferAnimationLayout.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            SPAudioBufferAnimationLayout.this.updateProgress();
        }
    };

    private int getimageID(int position) {
        return this.imageId[position];
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.mCurrentView = obtainViewFromPool(R.layout.sp_audio_animation);
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(TAG, "onResume()");
        super.onResume();
        getAudioTime();
        initlizeView();
        setVisibilityofAnimation();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.mAnimationStrip.getLayoutParams();
        layoutParams.leftMargin = getMargin();
        this.mAnimationStrip.setLayoutParams(layoutParams);
        startTimerForAnimation();
    }

    private void startTimerForAnimation() {
        this.mTimer = new Timer();
        this.mTimerTask = new MyTimerTask();
        this.mTimer.scheduleAtFixedRate(this.mTimerTask, 0L, 1000L);
    }

    private void initlizeView() {
        SPAudioBufferAnimationController.getInstance().executeTask();
        if (this.mImageViewArray == null) {
            this.mImageViewArray = new ImageView[11];
        }
        for (int i = 0; i <= 10; i++) {
            this.mImageViewArray[i] = (ImageView) this.mCurrentView.findViewById(getimageID(i));
        }
        this.mikeview = (ImageView) this.mCurrentView.findViewById(R.id.audio_mike);
        this.mAnimationStrip = (RelativeLayout) this.mCurrentView.findViewById(R.id.animation_strip);
        this.mAudiobufferState = SPAudioBufferAnimationController.getInstance().getAudiobufferStatus();
        if (this.mAudiobufferState == 0) {
            int blinkedImage = SPAudioBufferAnimationController.getInstance().getmBlinkedImage();
            for (int i2 = 0; i2 <= this.totalTime; i2++) {
                if (i2 <= blinkedImage) {
                    setSmallWhiteImage(this.mImageViewArray[i2]);
                } else {
                    setSmallGrayImage(this.mImageViewArray[i2]);
                }
            }
            setWhiteMikeImage(this.mikeview);
            setNormalCameraPosition(this.mImageViewArray[this.PRE_AUDIO_TIMING]);
            return;
        }
        if (this.mAudiobufferState == 1) {
            int blinkedImage2 = SPAudioBufferAnimationController.getInstance().getmBlinkedImage();
            for (int i3 = 0; i3 <= this.totalTime; i3++) {
                if (i3 < blinkedImage2) {
                    setSmallWhiteImage(this.mImageViewArray[i3]);
                } else {
                    setSmallGrayImage(this.mImageViewArray[i3]);
                }
            }
            setWhiteMikeImage(this.mikeview);
            setNormalCameraPosition(this.mImageViewArray[this.PRE_AUDIO_TIMING]);
            return;
        }
        if (this.mAudiobufferState == 2) {
            setRedMikeImage(this.mikeview);
            int startRedPoint = 0;
            if (SPAudioBufferAnimationController.getInstance().mEarlyClick) {
                startRedPoint = SPAudioBufferAnimationController.getInstance().getStartRedDot();
            }
            for (int i4 = 0; i4 <= this.totalTime; i4++) {
                if (startRedPoint <= i4 && i4 <= ApplicationSettingsMenuController.getInstance().getPreAudioTime()) {
                    setSmallRedImage(this.mImageViewArray[i4]);
                } else {
                    setSmallGrayImage(this.mImageViewArray[i4]);
                }
                if (i4 == this.PRE_AUDIO_TIMING) {
                    setCapturedCameraPosition(this.mImageViewArray[i4]);
                }
            }
        }
    }

    private void getAudioTime() {
        this.POST_AUDIO_TIMING = ApplicationSettingsMenuController.getInstance().getPostDuration();
        this.totalTime = ApplicationSettingsMenuController.getInstance().getTotalDuration();
        this.PRE_AUDIO_TIMING = this.totalTime - this.POST_AUDIO_TIMING;
    }

    private void setVisibilityofAnimation() {
        for (int i = 0; i < 11; i++) {
            if (i <= this.totalTime) {
                this.mImageViewArray[i].setVisibility(0);
            } else {
                this.mImageViewArray[i].setVisibility(8);
            }
        }
    }

    protected void updateProgress() {
        SPAudioBufferAnimationController.getInstance().executeTask();
        int count = SPAudioBufferAnimationController.getInstance().getmBlinkedImage();
        this.mAudiobufferState = SPAudioBufferAnimationController.getInstance().getAudiobufferStatus();
        switch (this.mAudiobufferState) {
            case 0:
                for (int i = 0; i <= count; i++) {
                    setSmallWhiteImage(this.mImageViewArray[i]);
                }
                for (int i2 = count + 1; i2 <= this.totalTime; i2++) {
                    setSmallGrayImage(this.mImageViewArray[i2]);
                }
                setWhiteMikeImage(this.mikeview);
                setNormalCameraPosition(this.mImageViewArray[this.PRE_AUDIO_TIMING]);
                return;
            case 1:
                if (count == this.PRE_AUDIO_TIMING) {
                    if (SPAudioBufferAnimationController.getInstance().isPostTimeDone()) {
                        for (int i3 = this.PRE_AUDIO_TIMING + 1; i3 <= this.totalTime; i3++) {
                            setSmallGrayImage(this.mImageViewArray[i3]);
                        }
                        for (int i4 = 0; i4 < this.PRE_AUDIO_TIMING; i4++) {
                            setSmallWhiteImage(this.mImageViewArray[i4]);
                        }
                        setWhiteMikeImage(this.mikeview);
                        setNormalCameraPosition(this.mImageViewArray[this.PRE_AUDIO_TIMING]);
                        SPAudioBufferAnimationController.getInstance().setPostTimeDone(false);
                    }
                    if (!this.isAllWhiteDone) {
                        for (int i5 = 0; i5 < this.PRE_AUDIO_TIMING; i5++) {
                            setSmallWhiteImage(this.mImageViewArray[i5]);
                        }
                        this.isAllWhiteDone = true;
                    }
                    if (this.camerashow) {
                        setNormalCameraPosition(this.mImageViewArray[count]);
                        this.camerashow = false;
                        return;
                    } else {
                        setBlinkingCameraPosition(this.mImageViewArray[count]);
                        this.camerashow = true;
                        return;
                    }
                }
                return;
            case 2:
                for (int i6 = this.PRE_AUDIO_TIMING + 1; i6 <= count; i6++) {
                    if (i6 < this.mImageViewArray.length) {
                        setSmallRedImage(this.mImageViewArray[i6]);
                    }
                }
                return;
            default:
                return;
        }
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void onReopened() {
        if (this.mTimer == null && this.mTimerTask == null) {
            startTimerForAnimation();
        }
        super.onReopened();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        if (event.getScanCode() == 518) {
            if (this.mTimer != null) {
                this.mTimer.cancel();
                this.mTimer = null;
            }
            if (this.mTimerTask != null) {
                this.mTimerTask.cancel();
                this.mTimerTask = null;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setSmallGrayImage(ImageView imageview) {
        if (imageview != null) {
            releaseImageViewDrawable(imageview);
            imageview.setImageResource(R.drawable.p_16_dd_parts_soundphoto_buffer_disable);
        }
    }

    private void setSmallRedImage(ImageView imageview) {
        if (imageview != null) {
            releaseImageViewDrawable(imageview);
            imageview.setImageResource(R.drawable.p_16_dd_parts_soundphoto_buffer_capturing);
        }
    }

    private void setSmallWhiteImage(ImageView imageview) {
        if (imageview != null) {
            releaseImageViewDrawable(imageview);
            imageview.setImageResource(R.drawable.p_16_dd_parts_soundphoto_buffer_enable);
        }
    }

    private void setNormalCameraPosition(ImageView imageview) {
        if (imageview != null) {
            releaseImageViewDrawable(imageview);
            imageview.setImageResource(R.drawable.p_16_dd_parts_soundphoto_camera_pos_normal);
        }
    }

    private void setBlinkingCameraPosition(ImageView imageview) {
        if (imageview != null) {
            releaseImageViewDrawable(imageview);
            imageview.setImageResource(R.drawable.p_16_dd_parts_soundphoto_camera_pos_blinking);
        }
    }

    private void setCapturedCameraPosition(ImageView imageview) {
        if (imageview != null) {
            releaseImageViewDrawable(imageview);
            imageview.setImageResource(R.drawable.p_16_dd_parts_soundphoto_camera_pos_captureing);
        }
    }

    private void setRedMikeImage(ImageView imageview) {
        if (imageview != null) {
            releaseImageViewDrawable(imageview);
            imageview.setImageResource(R.drawable.p_16_dd_parts_soundphoto_microphone_capturing);
        }
    }

    private void setWhiteMikeImage(ImageView imageview) {
        if (imageview != null) {
            releaseImageViewDrawable(imageview);
            imageview.setImageResource(R.drawable.p_16_dd_parts_soundphoto_microphone_normal);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class MyTimerTask extends TimerTask {
        MyTimerTask() {
        }

        @Override // java.util.TimerTask, java.lang.Runnable
        public void run() {
            SPAudioBufferAnimationLayout.this._handler.sendEmptyMessage(0);
        }
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        releaseResources();
        super.onPause();
    }

    private void releaseResources() {
        if (this.mTimer != null) {
            this.mTimer.cancel();
            this.mTimer = null;
        }
        if (this.mTimerTask != null) {
            this.mTimerTask.cancel();
            this.mTimerTask = null;
        }
        if (this.mAnimationStrip != null) {
            this.mAnimationStrip = null;
        }
        if (this.mikeview != null) {
            this.mikeview = null;
        }
        releaseAllImageView();
    }

    private int getMargin() {
        int margin = ((10 - this.totalTime) * 15) + 100;
        return margin;
    }

    private void releaseAllImageView() {
        for (int i = 0; i < 11; i++) {
            releaseImageViewDrawable(this.mImageViewArray[i]);
        }
    }

    private void releaseImageViewDrawable(ImageView imageView) {
        if (imageView != null) {
            imageView.setImageResource(0);
            if (imageView.getDrawable() != null) {
                imageView.getDrawable().setCallback(null);
            }
            imageView.setImageDrawable(null);
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onMenuKeyPushed(KeyEvent event) {
        SPAudioBufferAnimationController.getInstance().executeTask();
        this.mAudiobufferState = SPAudioBufferAnimationController.getInstance().getAudiobufferStatus();
        if (this.mAudiobufferState == 2) {
            CautionUtilityClass.getInstance().requestTrigger(SPInfo.CAUTION_ID_SOUNDPHOTO_WAITING_SOUND_RECORDING);
            return -1;
        }
        if (AudioRecorder.getInstance().getCreateSoundStatus()) {
            CautionUtilityClass.getInstance().requestTrigger(SPInfo.CAUTION_ID_SOUNDPHOTO_WAITING_SOUND_RECORDING);
            return -1;
        }
        return super.onMenuKeyPushed(event);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onPlayBackKeyPushed(KeyEvent event) {
        SPAudioBufferAnimationController.getInstance().executeTask();
        this.mAudiobufferState = SPAudioBufferAnimationController.getInstance().getAudiobufferStatus();
        if (this.mAudiobufferState == 2) {
            CautionUtilityClass.getInstance().requestTrigger(SPInfo.CAUTION_ID_SOUNDPHOTO_WAITING_SOUND_RECORDING);
            return -1;
        }
        if (AudioRecorder.getInstance().getCreateSoundStatus()) {
            CautionUtilityClass.getInstance().requestTrigger(SPInfo.CAUTION_ID_SOUNDPHOTO_WAITING_SOUND_RECORDING);
            return -1;
        }
        return super.onPlayBackKeyPushed(event);
    }
}
