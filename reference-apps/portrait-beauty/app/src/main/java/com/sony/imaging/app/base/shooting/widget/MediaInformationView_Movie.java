package com.sony.imaging.app.base.shooting.widget;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.common.MediaNotificationManager;

/* loaded from: classes.dex */
public class MediaInformationView_Movie extends MediaInformationView {
    private final String FORMAT_HM;
    private final String FORMAT_M;
    private final String FORMAT_OVER;
    private final int ONE_HOUR;
    private final int ONE_MINUTES;
    private final int OVER_100H;
    private final String[] TAGS_Movie;

    public MediaInformationView_Movie(Context context) {
        this(context, null);
    }

    public MediaInformationView_Movie(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.ONE_MINUTES = 60;
        this.ONE_HOUR = 3600;
        this.OVER_100H = 360000;
        this.TAGS_Movie = new String[]{MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE, MediaNotificationManager.TAG_MOVIE_REC_REMAIN_TIME_CHANGED};
        this.FORMAT_M = getResources().getString(R.string.anr_title);
        this.FORMAT_HM = getResources().getString(R.string.app_blocked_message);
        this.FORMAT_OVER = getResources().getString(R.string.anr_process);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.MediaInformationView
    protected void createRemainingView(Context context, AttributeSet attrs) {
        this.mRemainingView = new DigitView(context, attrs, false);
        this.mRemainingView.setTextAlign(19);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.MediaInformationView
    protected void showRemaining(int remaining) {
        String text;
        if (getVisibility() != 4) {
            int value = remaining / 60;
            if (remaining < 3600) {
                text = String.format(this.FORMAT_M, Integer.valueOf(remaining / 60));
            } else if (remaining < 360000) {
                text = String.format(this.FORMAT_HM, Integer.valueOf(remaining / 3600), Integer.valueOf((remaining % 3600) / 60));
            } else {
                text = this.FORMAT_OVER;
            }
            this.mRemainingView.setValue(text);
            if (value == 0) {
                this.mRemainingView.blink(true);
                this.mRemainingView.highlight(true);
            } else {
                this.mRemainingView.blink(false);
                this.mRemainingView.highlight(false);
            }
        }
    }

    @Override // com.sony.imaging.app.base.shooting.widget.MediaInformationView
    protected int getRemaining() {
        return this.mMediaNotifier.getRemainMovieRecTime();
    }

    @Override // com.sony.imaging.app.base.shooting.widget.MediaInformationView
    protected void changeVisibilty() {
        int visible = 4;
        int current = getVisibility();
        if (isMovieMode()) {
            visible = 0;
        }
        if (visible != current) {
            if (visible == 4) {
                this.mRemainingView.setValue("");
            }
            setVisibility(visible);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.widget.MediaInformationView
    protected String[] getMediaMountEvent() {
        return this.TAGS_Movie;
    }

    @Override // com.sony.imaging.app.base.shooting.widget.MediaInformationView
    protected void displayInfo(String tag) {
        if (tag.equals(MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE)) {
            refresh();
        } else if (tag.equals(MediaNotificationManager.TAG_MOVIE_REC_REMAIN_TIME_CHANGED) && this.mMediaNotifier.isMounted()) {
            showRemaining(getRemaining());
        }
    }
}
