package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class ApertureView extends DigitView {
    private static final String FORMAT_BIG_DIGIT = "F%.0f";
    private static final String FORMAT_ONE_DIGIT = "F%.1f";
    private static final String INVALID_APERTURE_STRING = "F--";
    private static final float INVALID_APERTURE_VALUE = 0.0f;
    private static final float NUMBER_100 = 100.0f;
    private static final float THRESHODL_BIG_OR_ONE = 10.0f;
    private ApertureChangeListener mApertureListener;

    public ApertureView(Context context) {
        this(context, null);
    }

    public ApertureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.DigitView, com.sony.imaging.app.base.shooting.widget.ActiveText
    protected NotificationListener getNotificationListener() {
        if (this.mApertureListener == null) {
            this.mApertureListener = new ApertureChangeListener();
        }
        return this.mApertureListener;
    }

    @Override // com.sony.imaging.app.base.shooting.widget.DigitView, com.sony.imaging.app.base.shooting.widget.ActiveText
    protected void refresh() {
        float aperture = CameraSetting.getInstance().getAperture() / NUMBER_100;
        setValue(aperture);
        boolean iris = CameraSetting.getInstance().getIrisErr();
        if (iris && aperture == INVALID_APERTURE_VALUE) {
            iris = false;
        }
        blink(iris ? false : true);
    }

    private void setValue(float value) {
        String displayValue;
        if (value == INVALID_APERTURE_VALUE) {
            displayValue = INVALID_APERTURE_STRING;
        } else if (value < THRESHODL_BIG_OR_ONE) {
            displayValue = String.format(FORMAT_ONE_DIGIT, Float.valueOf(value));
        } else {
            displayValue = String.format(FORMAT_BIG_DIGIT, Float.valueOf(value));
        }
        setValue(displayValue);
    }

    /* loaded from: classes.dex */
    private class ApertureChangeListener implements NotificationListener {
        private String[] TAGS;

        private ApertureChangeListener() {
            this.TAGS = new String[]{"Aperture"};
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.TAGS;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            ApertureView.this.refresh();
        }
    }
}
