package com.sony.imaging.app.base.shooting.widget;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.common.AudioNotificationManager;
import com.sony.imaging.app.base.common.AudioSetting;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.media.AudioManager;

/* loaded from: classes.dex */
public class MovieAudioRecordingIcon extends ActiveImage {
    protected static final String[] AUDIO_TAGS = {"AudioWindNoiseReduction"};
    protected NotificationListener mAudioListener;
    protected final DisplayModeObserver mDisplayObserver;

    public MovieAudioRecordingIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mDisplayObserver = DisplayModeObserver.getInstance();
        this.mAudioListener = getAudioListener();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage, android.widget.ImageView, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mAudioListener != null) {
            AudioNotificationManager.getInstance().setNotificationListener(this.mAudioListener);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage, android.widget.ImageView, android.view.View
    public void onDetachedFromWindow() {
        if (this.mAudioListener != null) {
            AudioNotificationManager.getInstance().removeNotificationListener(this.mAudioListener);
        }
        super.onDetachedFromWindow();
    }

    protected NotificationListener getAudioListener() {
        return new AudioNotificationListener();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class AudioNotificationListener implements NotificationListener {
        protected AudioNotificationListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return MovieAudioRecordingIcon.AUDIO_TAGS;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if ("AudioWindNoiseReduction".equals(tag)) {
                boolean visible = MovieAudioRecordingIcon.this.isVisible() || MovieAudioRecordingIcon.this.mFnMode;
                if (visible) {
                    MovieAudioRecordingIcon.this.setVisibility(0);
                    MovieAudioRecordingIcon.this.refresh();
                } else {
                    MovieAudioRecordingIcon.this.setVisibility(4);
                }
            }
        }
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    public void refresh() {
        int resId;
        int resId2;
        if (Environment.isMovieAPISupported()) {
            AudioManager.Parameters parameters = AudioSetting.getInstance().getAudioParameters();
            if (parameters != null && !parameters.getMicrophoneEnable()) {
                int device = this.mDisplayObserver.getActiveDevice();
                if (device != 1) {
                    resId2 = R.drawable.progress_bg_holo_light;
                } else {
                    resId2 = 17305775;
                }
                setImageResource(resId2);
                return;
            }
            if (parameters != null && parameters.getMicrophoneEnable() && "on".equals(parameters.getMicrophoneWindNoiseReduction()) && 2 == ExecutorCreator.getInstance().getRecordingMode()) {
                int device2 = this.mDisplayObserver.getActiveDevice();
                if (device2 != 1) {
                    resId = 17304465;
                } else {
                    resId = R.drawable.quickcontact_badge_overlay_normal_light_am;
                }
                setImageResource(resId);
            }
        }
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected boolean isVisible() {
        if (!Environment.isMovieAPISupported() || 2 != ExecutorCreator.getInstance().getRecordingMode()) {
            return false;
        }
        AudioManager.Parameters parameters = AudioSetting.getInstance().getAudioParameters();
        if (parameters != null && !parameters.getMicrophoneEnable()) {
            return true;
        }
        if (parameters == null || !parameters.getMicrophoneEnable() || !"on".equals(parameters.getMicrophoneWindNoiseReduction()) || 2 != ExecutorCreator.getInstance().getRecordingMode()) {
            return false;
        }
        return true;
    }
}
