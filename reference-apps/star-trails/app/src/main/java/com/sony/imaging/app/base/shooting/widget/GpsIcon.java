package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.GpsController;
import com.sony.imaging.app.base.common.widget.GpsNotificationManager;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.sysutil.ScalarProperties;

/* loaded from: classes.dex */
public class GpsIcon extends ImageView {
    private static final String[] TAGS = {GpsNotificationManager.GPS_STATE_CHANGED};
    private String TAG;
    private final String TAG_GPS_POWER;
    private final String TAG_GPS_SIGNALLEVEL;
    private final String TAG_GPS_STATE;
    private GpsStateChangedListener mGpsStateChangedListener;
    private TypedArray mTypedArray;

    public GpsIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.TAG = GpsIcon.class.getSimpleName();
        this.TAG_GPS_STATE = GpsNotificationManager.GPS_STATE;
        this.TAG_GPS_POWER = GpsNotificationManager.GPS_POWER;
        this.TAG_GPS_SIGNALLEVEL = GpsNotificationManager.GPS_SIGNALLEVEL;
        this.mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.GpsIcon);
        setVisibility(4);
    }

    public void RefreshGpsIcon() {
        Drawable draw = null;
        if (ScalarProperties.getInt("device.gps.supported") == 1 && GpsNotificationManager.Status.PowerON == GpsNotificationManager.getInstance().getValue(GpsNotificationManager.GPS_POWER)) {
            if (GpsNotificationManager.Status.Ng == GpsNotificationManager.getInstance().getValue(GpsNotificationManager.GPS_STATE)) {
                if (GpsNotificationManager.SignalLevel.No == GpsNotificationManager.getInstance().getValue(GpsNotificationManager.GPS_SIGNALLEVEL)) {
                    draw = this.mTypedArray.getDrawable(2);
                }
            } else if (GpsNotificationManager.Status.Fix == GpsNotificationManager.getInstance().getValue(GpsNotificationManager.GPS_STATE)) {
                if (GpsNotificationManager.SignalLevel.Low == GpsNotificationManager.getInstance().getValue(GpsNotificationManager.GPS_SIGNALLEVEL)) {
                    draw = this.mTypedArray.getDrawable(3);
                } else if (GpsNotificationManager.SignalLevel.Mid == GpsNotificationManager.getInstance().getValue(GpsNotificationManager.GPS_SIGNALLEVEL)) {
                    draw = this.mTypedArray.getDrawable(4);
                } else if (GpsNotificationManager.SignalLevel.High == GpsNotificationManager.getInstance().getValue(GpsNotificationManager.GPS_SIGNALLEVEL)) {
                    draw = this.mTypedArray.getDrawable(5);
                }
            } else if (GpsNotificationManager.Status.Nofix == GpsNotificationManager.getInstance().getValue(GpsNotificationManager.GPS_STATE)) {
                if (GpsNotificationManager.SignalLevel.No == GpsNotificationManager.getInstance().getValue(GpsNotificationManager.GPS_SIGNALLEVEL)) {
                    draw = this.mTypedArray.getDrawable(6);
                } else if (GpsNotificationManager.SignalLevel.Low == GpsNotificationManager.getInstance().getValue(GpsNotificationManager.GPS_SIGNALLEVEL)) {
                    draw = this.mTypedArray.getDrawable(7);
                } else if (GpsNotificationManager.SignalLevel.Mid == GpsNotificationManager.getInstance().getValue(GpsNotificationManager.GPS_SIGNALLEVEL)) {
                    draw = this.mTypedArray.getDrawable(8);
                } else if (GpsNotificationManager.SignalLevel.High == GpsNotificationManager.getInstance().getValue(GpsNotificationManager.GPS_SIGNALLEVEL)) {
                    draw = this.mTypedArray.getDrawable(9);
                }
            }
        }
        if (ScalarProperties.getInt("device.gps.supported") == 2 && GpsNotificationManager.Status.PowerON == GpsNotificationManager.getInstance().getValue(GpsNotificationManager.GPS_POWER)) {
            if (GpsNotificationManager.Status.Fix == GpsNotificationManager.getInstance().getValue(GpsNotificationManager.GPS_STATE)) {
                draw = this.mTypedArray.getDrawable(0);
            } else if (GpsNotificationManager.Status.Nofix == GpsNotificationManager.getInstance().getValue(GpsNotificationManager.GPS_STATE)) {
                draw = this.mTypedArray.getDrawable(1);
            }
        }
        if (draw != null) {
            setVisibility(0);
            setImageDrawable(draw);
        } else {
            setVisibility(4);
        }
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (GpsController.getInstance().isDeviceSupportedGps()) {
            this.mGpsStateChangedListener = new GpsStateChangedListener();
            GpsNotificationManager.getInstance().setNotificationListener(this.mGpsStateChangedListener);
            RefreshGpsIcon();
        }
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (GpsController.getInstance().isDeviceSupportedGps()) {
            GpsNotificationManager.getInstance().removeNotificationListener(this.mGpsStateChangedListener);
            this.mGpsStateChangedListener = null;
        }
    }

    /* loaded from: classes.dex */
    class GpsStateChangedListener implements NotificationListener {
        GpsStateChangedListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return GpsIcon.TAGS;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (tag.equals(GpsNotificationManager.GPS_STATE_CHANGED)) {
                GpsIcon.this.RefreshGpsIcon();
            }
        }
    }
}
