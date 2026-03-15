package com.sony.imaging.app.base.shooting.widget;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.common.widget.RotationalSubLcdActiveTextView;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.NotificationManager;

/* loaded from: classes.dex */
public class RotationalSubLcdTextMovieRemainTime extends RotationalSubLcdActiveTextView {
    private int mMovieRecRemainTime;
    NotificationListener mNotificationListener;

    public RotationalSubLcdTextMovieRemainTime(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mMovieRecRemainTime = -1;
        this.mNotificationListener = null;
    }

    public RotationalSubLcdTextMovieRemainTime(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mMovieRecRemainTime = -1;
        this.mNotificationListener = null;
    }

    public RotationalSubLcdTextMovieRemainTime(Context context) {
        super(context);
        this.mMovieRecRemainTime = -1;
        this.mNotificationListener = null;
    }

    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView, com.sony.imaging.app.base.common.widget.Rotational
    public void start() {
        if (MediaNotificationManager.getInstance().getMediaState() == 2) {
            this.mMovieRecRemainTime = MediaNotificationManager.getInstance().getRemainMovieRecTime();
        }
        super.start();
    }

    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdActiveTextView
    protected NotificationManager getNotificationManager() {
        return MediaNotificationManager.getInstance();
    }

    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdActiveTextView
    protected NotificationListener getNotificationListener() {
        if (this.mNotificationListener == null) {
            this.mNotificationListener = new NotificationListener() { // from class: com.sony.imaging.app.base.shooting.widget.RotationalSubLcdTextMovieRemainTime.1
                @Override // com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    if (tag.equals(MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE) && ((MediaNotificationManager) RotationalSubLcdTextMovieRemainTime.this.mNotifier).getMediaState() != 2) {
                        RotationalSubLcdTextMovieRemainTime.this.mMovieRecRemainTime = -1;
                    }
                    if (tag.equals(MediaNotificationManager.TAG_MOVIE_REC_REMAIN_TIME_CHANGED) && ((MediaNotificationManager) RotationalSubLcdTextMovieRemainTime.this.mNotifier).isMounted()) {
                        RotationalSubLcdTextMovieRemainTime.this.mMovieRecRemainTime = ((MediaNotificationManager) RotationalSubLcdTextMovieRemainTime.this.mNotifier).getRemainMovieRecTime();
                    }
                }

                @Override // com.sony.imaging.app.util.NotificationListener
                public String[] getTags() {
                    return new String[]{MediaNotificationManager.TAG_MOVIE_REC_REMAIN_TIME_CHANGED, MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE};
                }
            };
        }
        return this.mNotificationListener;
    }

    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView
    protected String makeText() {
        String ret = getResources().getString(R.string.lock_to_app_unlock_password);
        MediaNotificationManager mediaNotifyMng = MediaNotificationManager.getInstance();
        if (!mediaNotifyMng.isNoCard()) {
            if (this.mMovieRecRemainTime >= 0) {
                String minute_ID = getResources().getString(R.string.serviceClassDataAsync);
                String hour_ID = getResources().getString(R.string.serviceClassDataSync);
                int minute = this.mMovieRecRemainTime / 60;
                int hour = minute / 60;
                int minute2 = minute % 60;
                if (hour >= 10) {
                    String ret2 = String.format("%4d%s", Integer.valueOf(hour), hour_ID);
                    return ret2;
                }
                String ret3 = String.format("%d%s%02d%s", Integer.valueOf(hour), hour_ID, Integer.valueOf(minute2), minute_ID);
                return ret3;
            }
            String ret4 = getResources().getString(R.string.lock_to_app_unlock_password);
            return ret4;
        }
        return ret;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdActiveTextView, com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView, com.sony.imaging.app.base.common.widget.SubLcdTextView, com.sony.imaging.app.base.common.widget.AbstractSubLCDView, android.view.View
    public void onAttachedToWindow() {
        if (MediaNotificationManager.getInstance().getMediaState() == 2) {
            this.mMovieRecRemainTime = MediaNotificationManager.getInstance().getRemainMovieRecTime();
        }
        super.onAttachedToWindow();
    }
}
