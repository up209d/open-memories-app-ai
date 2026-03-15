package com.sony.imaging.app.base.shooting.widget;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.common.widget.RotationalSubLcdActiveTextView;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.NotificationManager;
import com.sony.imaging.app.util.PTag;

/* loaded from: classes.dex */
public class RotationalSubLcdTextRecTime extends RotationalSubLcdActiveTextView {
    private final int ONE_HOUR;
    private final int ONE_MINUTES;
    private final int OVER_100H;
    NotificationListener mNotificationListener;
    private Object mObject;
    private String mTimeText;
    private boolean writeTimeTagOnce;

    public RotationalSubLcdTextRecTime(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.ONE_MINUTES = 60;
        this.ONE_HOUR = 3600;
        this.OVER_100H = 360000;
        this.mNotificationListener = null;
        this.writeTimeTagOnce = false;
        this.mTimeText = "00:00";
        this.mObject = new Object();
    }

    public RotationalSubLcdTextRecTime(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.ONE_MINUTES = 60;
        this.ONE_HOUR = 3600;
        this.OVER_100H = 360000;
        this.mNotificationListener = null;
        this.writeTimeTagOnce = false;
        this.mTimeText = "00:00";
        this.mObject = new Object();
    }

    public RotationalSubLcdTextRecTime(Context context) {
        super(context);
        this.ONE_MINUTES = 60;
        this.ONE_HOUR = 3600;
        this.OVER_100H = 360000;
        this.mNotificationListener = null;
        this.writeTimeTagOnce = false;
        this.mTimeText = "00:00";
        this.mObject = new Object();
    }

    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdActiveTextView
    protected NotificationManager getNotificationManager() {
        return CameraNotificationManager.getInstance();
    }

    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdActiveTextView
    protected NotificationListener getNotificationListener() {
        if (this.mNotificationListener == null) {
            this.mNotificationListener = new NotificationListener() { // from class: com.sony.imaging.app.base.shooting.widget.RotationalSubLcdTextRecTime.1
                @Override // com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    Log.i("RotationalSubLCDRecTime", "onNotify: " + tag);
                    if (tag.equals(CameraNotificationManager.MOVIE_REC_TIME_CHANGED)) {
                        RotationalSubLcdTextRecTime.this.refresh();
                    }
                    if (RotationalSubLcdTextRecTime.this.isVisible()) {
                        RotationalSubLcdTextRecTime.this.setVisibility(0);
                    } else {
                        RotationalSubLcdTextRecTime.this.setVisibility(4);
                    }
                    if (tag.equals("stopMovieRec") || tag.equals(CameraNotificationManager.MOVIE_REC_START_FAILED)) {
                        RotationalSubLcdTextRecTime.this.init();
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

    @Override // com.sony.imaging.app.base.common.widget.AbstractSubLCDView
    protected void refresh() {
        int recTime = 0;
        Integer i = (Integer) CameraNotificationManager.getInstance().getValue(CameraNotificationManager.MOVIE_REC_TIME_CHANGED);
        if (i != null) {
            recTime = i.intValue();
        }
        if (recTime >= 0) {
            if (recTime < 3600) {
                this.mTimeText = String.format("%02d:%02d", Integer.valueOf(recTime / 60), Integer.valueOf(recTime % 60));
            } else if (recTime < 360000) {
                if (recTime % 5 == 0) {
                    String hour_ID = getResources().getString(R.string.serviceClassDataSync);
                    this.mTimeText = String.format("%3d%s ", Integer.valueOf(recTime / 3600), hour_ID);
                } else {
                    this.mTimeText = String.format("%02d:%02d", Integer.valueOf((recTime / 60) % 60), Integer.valueOf(recTime % 60));
                }
            }
            setText(this.mTimeText);
            if (recTime % 5 == 1) {
                stop();
            }
        }
    }

    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView, com.sony.imaging.app.base.common.widget.Rotational
    public void start() {
        this.started = true;
        synchronized (this.mObject) {
            if (isVisible()) {
                if (!this.writeTimeTagOnce) {
                    PTag.setTimeTag("RSubLcdRecTimeDisp: " + this.mText, 0);
                    this.writeTimeTagOnce = true;
                }
                setVisibility(0);
            } else {
                setVisibility(4);
            }
        }
    }

    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView, com.sony.imaging.app.base.common.widget.Rotational
    public void stop() {
        this.started = false;
        callRotationFinishNotify();
    }

    @Override // com.sony.imaging.app.base.common.widget.AbstractSubLCDView
    protected boolean isVisible() {
        if (!this.started) {
            return false;
        }
        if (2 != ExecutorCreator.getInstance().getRecordingMode() && 8 != ExecutorCreator.getInstance().getRecordingMode()) {
            return false;
        }
        return true;
    }

    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView, com.sony.imaging.app.base.common.widget.Rotational
    public void init() {
        super.init();
        if (MediaNotificationManager.getInstance().getMediaState() != 2 && MediaNotificationManager.getInstance().getRemainMovieRecTime() < 0) {
            this.mTimeText = getResources().getString(R.string.lock_to_app_unlock_password);
        } else {
            this.mTimeText = "00:00";
        }
        this.mText = this.mTimeText;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdActiveTextView, com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView, com.sony.imaging.app.base.common.widget.SubLcdTextView, com.sony.imaging.app.base.common.widget.AbstractSubLCDView, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        init();
    }
}
