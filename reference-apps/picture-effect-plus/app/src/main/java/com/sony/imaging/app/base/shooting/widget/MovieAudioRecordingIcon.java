package com.sony.imaging.app.base.shooting.widget;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.widget.ActiveImage;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.media.AudioManager;

/* loaded from: classes.dex */
public class MovieAudioRecordingIcon extends ActiveImage {
    private AudioManager.Parameters mAudioParameters;
    protected final DisplayModeObserver mDisplayObserver;
    private ActiveImage.ActiveImageListener mListener;

    public MovieAudioRecordingIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mDisplayObserver = DisplayModeObserver.getInstance();
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    public void refresh() {
        int resId;
        int resId2;
        if (Environment.isMovieAPISupported()) {
            this.mAudioParameters = CameraSetting.getInstance().getAudioParameters();
            int visibility = 4;
            if (this.mAudioParameters != null && !this.mAudioParameters.getMicrophoneEnable()) {
                int device = this.mDisplayObserver.getActiveDevice();
                if (device != 1) {
                    resId2 = R.drawable.progress_bg_holo_light;
                } else {
                    resId2 = 17305775;
                }
                setImageResource(resId2);
                visibility = 0;
            } else if (this.mAudioParameters != null && this.mAudioParameters.getMicrophoneEnable() && "on".equals(this.mAudioParameters.getMicrophoneWindNoiseReduction()) && 2 == ExecutorCreator.getInstance().getRecordingMode()) {
                int device2 = this.mDisplayObserver.getActiveDevice();
                if (device2 != 1) {
                    resId = 17304465;
                } else {
                    resId = R.drawable.quickcontact_badge_overlay_normal_light_am;
                }
                setImageResource(resId);
                visibility = 0;
            }
            setVisibility(visibility);
        }
    }
}
