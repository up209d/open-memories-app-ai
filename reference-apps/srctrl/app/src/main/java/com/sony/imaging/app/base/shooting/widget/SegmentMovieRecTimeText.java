package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.common.widget.SubLcdActiveText;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.movie.MovieShootingExecutor;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.NotificationManager;

/* loaded from: classes.dex */
public class SegmentMovieRecTimeText extends SubLcdActiveText {
    private static final String TAG = SegmentMovieRecTimeText.class.getSimpleName();
    private final int ONE_HOUR;
    private final int ONE_MINUTES;
    private final int OVER_100H;
    NotificationListener mNotificationListener;

    public SegmentMovieRecTimeText(Context context) {
        super(context);
        this.mNotificationListener = null;
        this.ONE_MINUTES = 60;
        this.ONE_HOUR = 3600;
        this.OVER_100H = 360000;
    }

    public SegmentMovieRecTimeText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mNotificationListener = null;
        this.ONE_MINUTES = 60;
        this.ONE_HOUR = 3600;
        this.OVER_100H = 360000;
    }

    @Override // com.sony.imaging.app.base.common.widget.SubLcdActiveText
    protected NotificationManager getNotificationManager() {
        return CameraNotificationManager.getInstance();
    }

    @Override // com.sony.imaging.app.base.common.widget.SubLcdActiveText
    protected NotificationListener getNotificationListener() {
        if (this.mNotificationListener == null) {
            this.mNotificationListener = new NotificationListener() { // from class: com.sony.imaging.app.base.shooting.widget.SegmentMovieRecTimeText.1
                @Override // com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    SegmentMovieRecTimeText.this.refresh();
                    if (SegmentMovieRecTimeText.this.isVisible()) {
                        SegmentMovieRecTimeText.this.setVisibility(0);
                    } else {
                        SegmentMovieRecTimeText.this.setVisibility(4);
                    }
                }

                @Override // com.sony.imaging.app.util.NotificationListener
                public String[] getTags() {
                    return new String[]{CameraNotificationManager.MOVIE_REC_START_SUCCEEDED, CameraNotificationManager.MOVIE_REC_START_FAILED, "stopMovieRec", CameraNotificationManager.MOVIE_REC_TIME_CHANGED};
                }
            };
        }
        return this.mNotificationListener;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.SubLcdActiveText, com.sony.imaging.app.base.common.widget.AbstractSubLCDView
    public void refresh() {
        String text = "";
        int recTime = 0;
        Integer i = (Integer) CameraNotificationManager.getInstance().getValue(CameraNotificationManager.MOVIE_REC_TIME_CHANGED);
        if (i != null) {
            recTime = i.intValue();
        }
        if (recTime < 3600) {
            text = String.format("%02d:%02d", Integer.valueOf(recTime / 60), Integer.valueOf(recTime % 60));
        } else if (recTime < 360000) {
            if (recTime % 5 == 0) {
                text = String.format("%3dh ", Integer.valueOf(recTime / 3600));
            } else {
                text = String.format("%02d:%02d", Integer.valueOf((recTime / 60) % 60), Integer.valueOf(recTime % 60));
            }
        }
        setText(text);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.SubLcdActiveText, com.sony.imaging.app.base.common.widget.AbstractSubLCDView
    public boolean isVisible() {
        if ((2 != ExecutorCreator.getInstance().getRecordingMode() && 8 != ExecutorCreator.getInstance().getRecordingMode()) || !MovieShootingExecutor.isMovieRecording()) {
            return false;
        }
        return true;
    }
}
