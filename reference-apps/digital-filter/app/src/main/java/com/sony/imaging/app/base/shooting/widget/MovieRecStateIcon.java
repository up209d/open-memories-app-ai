package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.movie.MovieShootingExecutor;
import com.sony.imaging.app.base.shooting.widget.ActiveLayout;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class MovieRecStateIcon extends ActiveLayout {
    private String TAG;
    protected Context mContext;
    protected final DisplayModeObserver mDisplayObserver;
    private ActiveLayout.ActiveLayoutListener mListener;
    private int mRecColorId;
    protected TextView mRecStateView;
    private int mRecTextId;
    private int mStbyColorId;
    private int mStbyTextId;
    private int mStyleId;

    public MovieRecStateIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.TAG = "MovieRecState";
        this.mRecStateView = null;
        this.mDisplayObserver = DisplayModeObserver.getInstance();
        this.mContext = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MovieRecStateIcon);
        this.mRecTextId = a.getResourceId(0, 0);
        this.mStbyTextId = a.getResourceId(1, 0);
        a.recycle();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.ActiveLayout, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.ActiveLayout, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        removeAllViews();
        super.onDetachedFromWindow();
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveLayout
    protected void refresh() {
        int color;
        if (Environment.isMovieAPISupported()) {
            String text = "";
            removeView(this.mRecStateView);
            int device = this.mDisplayObserver.getActiveDevice();
            if (device != 1) {
                this.mStbyColorId = this.mContext.getResources().getColor(R.color.RESID_FONTSTYLE_STD_MOVIE_CAPTURE);
                this.mRecColorId = this.mContext.getResources().getColor(R.color.RESID_FONTSTYLE_STD_MOVIE_REC);
                this.mStyleId = R.attr.RESID_FONTSIZE_SP_REC_LV_MOVIE_TIME;
            } else {
                this.mStbyColorId = this.mContext.getResources().getColor(R.color.RESID_FONTSTYLE_STD_MOVIE_CAPTURE);
                this.mRecColorId = this.mContext.getResources().getColor(R.color.RESID_FONTSTYLE_EVF_MOVIE_REC);
                this.mStyleId = R.attr.RESID_FONTSIZE_SP_REC_LV_MOVIE_TIME_EVF;
            }
            if (!MovieShootingExecutor.isMovieRecording()) {
                if (this.mStbyTextId != 0) {
                    text = this.mContext.getResources().getString(this.mStbyTextId);
                }
                color = this.mStbyColorId;
            } else {
                if (this.mRecTextId != 0) {
                    text = this.mContext.getResources().getString(this.mRecTextId);
                }
                color = this.mRecColorId;
            }
            this.mRecStateView = new TextView(this.mContext, null, this.mStyleId);
            this.mRecStateView.setTextColor(color);
            this.mRecStateView.setText(text);
            addView(this.mRecStateView);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.ActiveLayout
    public boolean isVisible() {
        boolean retRecmode = 2 == ExecutorCreator.getInstance().getRecordingMode() || 8 == ExecutorCreator.getInstance().getRecordingMode();
        return retRecmode && MovieShootingExecutor.isMovieRecording();
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveLayout
    protected NotificationListener getNotificationListener() {
        if (this.mListener == null) {
            this.mListener = new ActiveLayout.ActiveLayoutListener() { // from class: com.sony.imaging.app.base.shooting.widget.MovieRecStateIcon.1
                @Override // com.sony.imaging.app.base.shooting.widget.ActiveLayout.ActiveLayoutListener, com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    if (CameraNotificationManager.MOVIE_REC_START_SUCCEEDED.equals(tag) || CameraNotificationManager.MOVIE_REC_START_FAILED.equals(tag) || CameraNotificationManager.MOVIE_REC_STOP.equals(tag)) {
                        MovieRecStateIcon.this.refresh();
                        MovieRecStateIcon.this.setVisibility(MovieRecStateIcon.this.isVisible() ? 0 : 4);
                    } else {
                        super.onNotify(tag);
                    }
                }

                @Override // com.sony.imaging.app.base.shooting.widget.ActiveLayout.ActiveLayoutListener
                public String[] addTags() {
                    return new String[]{CameraNotificationManager.MOVIE_REC_START_SUCCEEDED, CameraNotificationManager.MOVIE_REC_START_FAILED, CameraNotificationManager.MOVIE_REC_STOP};
                }
            };
        }
        return this.mListener;
    }
}
