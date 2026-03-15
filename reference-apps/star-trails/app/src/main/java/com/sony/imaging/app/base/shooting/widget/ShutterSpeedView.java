package com.sony.imaging.app.base.shooting.widget;

import android.R;
import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Pair;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.startrails.util.STConstants;
import com.sony.imaging.app.util.NotificationListener;
import java.math.BigDecimal;

/* loaded from: classes.dex */
public class ShutterSpeedView extends DigitView {
    private static final String FORMAT_BIG_DIGIT = "%.0f\"";
    private static final String FORMAT_ONE_DIGIT = "%.1f\"";
    private static final float THRESHODL_BIG_OR_ONE = 10.0f;
    private static final float THRESHODL_ONE_OR_SMALL = 0.4f;
    private final String FORMAT_SMALL_DIGIT;
    private final String STRING_BULB;
    private ShutterSpeedChangeListener mShutterSpeedListener;

    public ShutterSpeedView(Context context) {
        this(context, null);
    }

    public ShutterSpeedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.FORMAT_SMALL_DIGIT = getResources().getString(R.string.restr_pin_create_pin);
        String bulb = "";
        try {
            bulb = getResources().getString(17042030);
        } catch (Resources.NotFoundException e) {
        }
        this.STRING_BULB = bulb;
    }

    @Override // com.sony.imaging.app.base.shooting.widget.DigitView, com.sony.imaging.app.base.shooting.widget.ActiveText
    protected NotificationListener getNotificationListener() {
        if (this.mShutterSpeedListener == null) {
            this.mShutterSpeedListener = new ShutterSpeedChangeListener();
        }
        return this.mShutterSpeedListener;
    }

    @Override // com.sony.imaging.app.base.shooting.widget.DigitView, com.sony.imaging.app.base.shooting.widget.ActiveText
    protected void refresh() {
        Pair<Integer, Integer> ss = CameraSetting.getInstance().getShutterSpeed();
        if (ss != null) {
            setValue(ss);
            boolean ae_shut = CameraSetting.getInstance().getAEShutErr();
            blink(!ae_shut);
        }
    }

    private void setValue(Pair<Integer, Integer> ss) {
        String displayValue;
        if (CameraSetting.isShutterSpeedBulb(ss)) {
            displayValue = this.STRING_BULB;
        } else {
            float value = STConstants.INVALID_APERTURE_VALUE;
            if (((Integer) ss.second).intValue() != 0) {
                float value2 = ((Integer) ss.first).intValue() / ((Integer) ss.second).intValue();
                BigDecimal bi = new BigDecimal(String.valueOf(value2));
                value = bi.setScale(1, 4).floatValue();
            }
            if (value < THRESHODL_ONE_OR_SMALL) {
                displayValue = String.format(this.FORMAT_SMALL_DIGIT, ss.first, ss.second);
            } else {
                displayValue = (value >= 10.0f || ((float) ((Integer) ss.second).intValue()) == 1.0f) ? "" + String.format(FORMAT_BIG_DIGIT, Float.valueOf(value)) : String.format(FORMAT_ONE_DIGIT, Float.valueOf(value));
            }
        }
        setValue(displayValue);
    }

    /* loaded from: classes.dex */
    class ShutterSpeedChangeListener implements NotificationListener {
        private String[] TAGS = {CameraNotificationManager.SHUTTER_SPEED};

        ShutterSpeedChangeListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.TAGS;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            ShutterSpeedView.this.refresh();
        }
    }
}
