package com.sony.imaging.app.base.shooting.widget;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import com.sony.imaging.app.base.common.widget.SubLcdActiveText;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.movie.MovieShootingExecutor;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.NotificationManager;

/* loaded from: classes.dex */
public class SegmentMovieTopText extends SubLcdActiveText {
    private static final String TAG = SegmentMovieTopText.class.getSimpleName();
    private NotificationListener mListener;

    public SegmentMovieTopText(Context context) {
        super(context);
        Log.i(TAG, "constructor");
    }

    public SegmentMovieTopText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // com.sony.imaging.app.base.common.widget.SubLcdActiveText
    protected NotificationManager getNotificationManager() {
        return CameraNotificationManager.getInstance();
    }

    @Override // com.sony.imaging.app.base.common.widget.SubLcdActiveText
    protected NotificationListener getNotificationListener() {
        if (this.mListener == null) {
            this.mListener = new NotificationListener() { // from class: com.sony.imaging.app.base.shooting.widget.SegmentMovieTopText.1
                @Override // com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    SegmentMovieTopText.this.refresh();
                }

                @Override // com.sony.imaging.app.util.NotificationListener
                public String[] getTags() {
                    return new String[]{CameraNotificationManager.STREAM_WRITE_START, CameraNotificationManager.STREAM_WRITE_STOP, CameraNotificationManager.MOVIE_REC_START_SUCCEEDED, "stopMovieRec"};
                }
            };
        }
        return this.mListener;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.SubLcdActiveText, com.sony.imaging.app.base.common.widget.SubLcdTextView, com.sony.imaging.app.base.common.widget.AbstractSubLCDView, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.i(TAG, "onAttachedToWindow");
        boolean visible = isVisible();
        setOwnVisible(visible);
        if (visible) {
            refresh();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.SubLcdActiveText, com.sony.imaging.app.base.common.widget.SubLcdTextView, com.sony.imaging.app.base.common.widget.AbstractSubLCDView, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.i(TAG, "onDetachedFromWindow");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.SubLcdActiveText, com.sony.imaging.app.base.common.widget.AbstractSubLCDView
    public void refresh() {
        Log.i(TAG, "refresh");
        setVisibility(isVisible() ? 0 : 4);
        if (!MovieShootingExecutor.isMovieRecording()) {
            setText(getResources().getString(R.string.lock_pattern_view_aspect));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.SubLcdActiveText, com.sony.imaging.app.base.common.widget.AbstractSubLCDView
    public boolean isVisible() {
        if ((2 != ExecutorCreator.getInstance().getRecordingMode() && 8 != ExecutorCreator.getInstance().getRecordingMode()) || MovieShootingExecutor.isMovieRecording()) {
            return false;
        }
        return true;
    }
}
