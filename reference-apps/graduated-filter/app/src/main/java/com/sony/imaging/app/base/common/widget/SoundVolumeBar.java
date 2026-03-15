package com.sony.imaging.app.base.common.widget;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.sony.imaging.app.base.common.AudioNotificationManager;
import com.sony.imaging.app.base.common.AudioVolumeController;
import com.sony.imaging.app.util.NotificationListener;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class SoundVolumeBar extends RelativeLayout {
    protected static final int FIRST_LEFT_MARGIN = 4;
    protected static final int HORIZONTAL_MARGIN = 20;
    public static final int SUPPORTED_RANGE = 16;
    protected static final String[] TAGS = {AudioNotificationManager.SOUND_VOLUME_CHANGED};
    protected static final int TOP_MARGIN = 4;
    protected ArrayList<ImageView> mLevelImages;
    protected NotificationListener mListener;

    public SoundVolumeBar(Context context) {
        super(context);
        this.mListener = getAudioListener();
        createSubViews(context);
    }

    public SoundVolumeBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mListener = getAudioListener();
        createSubViews(context);
    }

    public SoundVolumeBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mListener = getAudioListener();
        createSubViews(context);
    }

    protected void finalize() throws Throwable {
        this.mLevelImages = null;
        super.finalize();
    }

    protected void createSubViews(Context context) {
        ViewGroup.LayoutParams layoutParam = new RelativeLayout.LayoutParams(-2, -2);
        ImageView background = new ImageView(context);
        background.setImageResource(17306209);
        addView(background, layoutParam);
        this.mLevelImages = new ArrayList<>(15);
        int leftMargin = 4;
        for (int i = 0; i < 15; i++) {
            RelativeLayout.LayoutParams layoutParam2 = new RelativeLayout.LayoutParams(-2, -2);
            layoutParam2.leftMargin = leftMargin;
            layoutParam2.topMargin = 4;
            ImageView view = new ImageView(context);
            view.setImageResource(R.drawable.ft_avd_tooverflow_animation);
            addView(view, layoutParam2);
            this.mLevelImages.add(view);
            leftMargin += 20;
        }
    }

    protected NotificationListener getAudioListener() {
        return new AudioListener();
    }

    /* loaded from: classes.dex */
    public class AudioListener implements NotificationListener {
        public AudioListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return SoundVolumeBar.TAGS;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            SoundVolumeBar.this.refresh();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        AudioNotificationManager.getInstance().setNotificationListener(this.mListener);
        refresh();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        AudioNotificationManager.getInstance().removeNotificationListener(this.mListener);
        super.onDetachedFromWindow();
    }

    protected void refresh() {
        AudioVolumeController controller = AudioVolumeController.getInstance();
        int current = controller.getInt(AudioVolumeController.TAG_AUDIO_VOLUME);
        for (int i = 0; i < 15; i++) {
            this.mLevelImages.get(i).setVisibility(i + 1 <= current ? 0 : 4);
        }
    }
}
