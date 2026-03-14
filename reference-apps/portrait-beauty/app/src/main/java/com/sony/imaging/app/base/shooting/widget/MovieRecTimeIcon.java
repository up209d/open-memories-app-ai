package com.sony.imaging.app.base.shooting.widget;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.movie.MovieShootingExecutor;
import com.sony.imaging.app.base.shooting.widget.ActiveText;

/* loaded from: classes.dex */
public class MovieRecTimeIcon extends ActiveText {
    private final String FORMAT_HMS;
    private final String FORMAT_MS;
    private final String FORMAT_OVER;
    private final int ONE_HOUR;
    private final int ONE_MINUTES;
    private final int OVER_100H;
    private String TAG;
    NotificationListener mNotificationListener;

    public MovieRecTimeIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.TAG = "MovieRecTime";
        this.mNotificationListener = null;
        this.ONE_MINUTES = 60;
        this.ONE_HOUR = 3600;
        this.OVER_100H = 360000;
        this.FORMAT_MS = getResources().getString(R.string.restr_pin_try_later);
        this.FORMAT_HMS = getResources().getString(R.string.app_category_video);
        this.FORMAT_OVER = getResources().getString(R.string.ext_media_move_failure_message);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveText
    protected void refresh() {
        String text;
        int recTime = 0;
        Integer i = (Integer) CameraNotificationManager.getInstance().getValue(CameraNotificationManager.MOVIE_REC_TIME_CHANGED);
        if (i != null) {
            recTime = i.intValue();
        }
        if (recTime < 3600) {
            text = String.format(this.FORMAT_MS, Integer.valueOf(recTime / 60), Integer.valueOf(recTime % 60));
        } else if (recTime < 360000) {
            text = String.format(this.FORMAT_HMS, Integer.valueOf(recTime / 3600), Integer.valueOf((recTime % 3600) / 60), Integer.valueOf(recTime % 60));
        } else {
            text = this.FORMAT_OVER;
        }
        setText(text);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveText
    protected boolean isVisible() {
        if ((2 != ExecutorCreator.getInstance().getRecordingMode() && 8 != ExecutorCreator.getInstance().getRecordingMode()) || !MovieShootingExecutor.isMovieRecording()) {
            return false;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.ActiveText
    public NotificationListener getNotificationListener() {
        if (this.mNotificationListener == null) {
            this.mNotificationListener = new NotificationListener();
        }
        return this.mNotificationListener;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class NotificationListener extends ActiveText.ActiveTextListener {
        private final String[] tags;

        private NotificationListener() {
            super();
            this.tags = new String[]{CameraNotificationManager.MOVIE_REC_START_SUCCEEDED, CameraNotificationManager.MOVIE_REC_START_FAILED, CameraNotificationManager.MOVIE_REC_STOP, CameraNotificationManager.MOVIE_REC_TIME_CHANGED};
        }

        @Override // com.sony.imaging.app.base.shooting.widget.ActiveText.ActiveTextListener, com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (tag.equals(CameraNotificationManager.MOVIE_REC_START_SUCCEEDED) || tag.equals(CameraNotificationManager.MOVIE_REC_START_FAILED) || tag.equals(CameraNotificationManager.MOVIE_REC_STOP)) {
                MovieRecTimeIcon.this.refresh();
                MovieRecTimeIcon.this.setVisibility(MovieRecTimeIcon.this.isVisible() ? 0 : 4);
            } else if (tag.equals(CameraNotificationManager.MOVIE_REC_TIME_CHANGED)) {
                MovieRecTimeIcon.this.refresh();
            } else {
                super.onNotify(tag);
            }
        }

        @Override // com.sony.imaging.app.base.shooting.widget.ActiveText.ActiveTextListener
        public String[] addTags() {
            return this.tags;
        }
    }
}
