package com.sony.imaging.app.startrails.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.widget.DigitView;
import com.sony.imaging.app.startrails.menu.controller.STSelfTimerMenuController;
import com.sony.imaging.app.startrails.util.AppLog;
import com.sony.imaging.app.startrails.util.STConstants;
import com.sony.imaging.app.startrails.util.STUtility;
import com.sony.imaging.app.util.NotificationListener;
import java.math.BigDecimal;

/* loaded from: classes.dex */
public class CapturingShootTimeWidget extends DigitView {
    private static final String TAG = "CapturingShootTimeWidget";
    private static final float THRESHODL_ONE_OR_SMALL = 0.4f;
    private ShutterSpeedChangeListener mShutterSpeedListener;

    public CapturingShootTimeWidget(Context context) {
        this(context, null);
        if ((STConstants.sCaptureImageCounter < 1 && STSelfTimerMenuController.getInstance().isSelfTimer()) || STUtility.getInstance().isPreTakePictureTestShot()) {
            setVisibility(4);
        }
    }

    public CapturingShootTimeWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.DigitView, com.sony.imaging.app.base.shooting.widget.ActiveText
    protected NotificationListener getNotificationListener() {
        if (this.mShutterSpeedListener == null) {
            this.mShutterSpeedListener = new ShutterSpeedChangeListener();
        }
        return this.mShutterSpeedListener;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.ActiveText, android.widget.TextView, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if ((STConstants.sCaptureImageCounter < 1 && STSelfTimerMenuController.getInstance().isSelfTimer()) || STUtility.getInstance().isPreTakePictureTestShot()) {
            setVisibility(4);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.DigitView, com.sony.imaging.app.base.shooting.widget.ActiveText, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setVisibility(4);
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
        float value = STConstants.INVALID_APERTURE_VALUE;
        if (((Integer) ss.second).intValue() != 0) {
            float value2 = ((Integer) ss.first).intValue() / ((Integer) ss.second).intValue();
            BigDecimal bi = new BigDecimal(String.valueOf(value2));
            value = bi.setScale(1, 4).floatValue();
        }
        AppLog.info(TAG, "" + value);
        if (value < THRESHODL_ONE_OR_SMALL || value < 1.0f) {
            value = 1.0f;
        }
        int ssValue = (int) value;
        STUtility.getInstance().setShutterSpeed(ssValue);
        AppLog.info(TAG, "");
        String displayValue = String.format(STUtility.getInstance().getTotleRecordingTime(), new Object[0]);
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
            if (STConstants.sCaptureImageCounter < 1 || STUtility.getInstance().isCapturingStarted()) {
                CapturingShootTimeWidget.this.refresh();
            }
        }
    }

    @Override // com.sony.imaging.app.base.shooting.widget.DigitView, com.sony.imaging.app.util.Oscillator.OnPeriodListener
    public void onPeriod(int Hz, boolean highlow) {
        Log.d(TAG, "Do nothing");
    }
}
