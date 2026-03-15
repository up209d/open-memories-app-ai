package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import com.sony.imaging.app.base.common.AudioNotificationManager;
import com.sony.imaging.app.base.common.widget.SubLcdActiveIcon;
import com.sony.imaging.app.base.shooting.camera.AudioRecordingController;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.NotificationManager;

/* loaded from: classes.dex */
public class AudioRecordingSubLCDIcon extends SubLcdActiveIcon {
    private static final String TAG = "AudioRecordingSubLCDIcon";

    public AudioRecordingSubLCDIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.SubLcdActiveIcon, com.sony.imaging.app.base.common.widget.AbstractSubLCDView
    public void refresh() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.SubLcdActiveIcon, com.sony.imaging.app.base.common.widget.AbstractSubLCDView
    public boolean isVisible() {
        if (!Environment.isMovieAPISupported()) {
            return false;
        }
        AudioRecordingController controller = AudioRecordingController.getInstance();
        String audioRecordingValue = controller.getValue(AudioRecordingController.AUDIO_RECORDING);
        if ("ON".equals(audioRecordingValue)) {
            return false;
        }
        if ("OFF".equals(audioRecordingValue)) {
            return true;
        }
        Log.e(TAG, "Unknown value : " + audioRecordingValue);
        return false;
    }

    @Override // com.sony.imaging.app.base.common.widget.SubLcdActiveIcon
    protected NotificationListener getNotificationListener() {
        if (this.mListener == null) {
            this.mListener = new AudioRecordingStateChangedListener();
        }
        return this.mListener;
    }

    @Override // com.sony.imaging.app.base.common.widget.SubLcdActiveIcon
    protected NotificationManager getNotificationManager() {
        return AudioNotificationManager.getInstance();
    }

    /* loaded from: classes.dex */
    protected class AudioRecordingStateChangedListener implements NotificationListener {
        protected AudioRecordingStateChangedListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            boolean visible = AudioRecordingSubLCDIcon.this.isVisible();
            AudioRecordingSubLCDIcon.this.setOwnVisible(visible);
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public final String[] getTags() {
            return new String[]{AudioNotificationManager.AUDIO_RECORDING_CHANGED};
        }
    }
}
