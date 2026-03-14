package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class ExposureAndMeteredManualView extends DigitView {
    private static final int ICON_HEIGHT = 34;
    private static final int ICON_V_OFFSET = 1;
    private static final int ICON_WIDTH = 36;
    private static final float METERED_MANUAL_MAX = 2.0f;
    private static final float METERED_MANUAL_MIN = -2.0f;
    private static final int MODE_EXPOSURE = 0;
    private static final int MODE_METEREDMANUAL = 1;
    private static final int MODE_OTHER = 2;
    private final String FORMAT_FOR_MINUS;
    private final String FORMAT_FOR_PLUS;
    private final String FORMAT_FOR_ZERO;
    private ExposureAndMeteredManualListener mExpMmListener;
    private int mMode;
    private TypedArray mTypedArray;

    public ExposureAndMeteredManualView(Context context) {
        this(context, null);
    }

    public ExposureAndMeteredManualView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.ExposureAndMeteredManualView);
        this.FORMAT_FOR_ZERO = getResources().getString(android.R.string.restr_pin_enter_new_pin);
        this.FORMAT_FOR_PLUS = getResources().getString(17041720);
        this.FORMAT_FOR_MINUS = getResources().getString(17041719);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.DigitView, com.sony.imaging.app.base.shooting.widget.ActiveText
    protected NotificationListener getNotificationListener() {
        if (this.mExpMmListener == null && ExposureCompensationController.getInstance().getSupportedValue(null) != null) {
            this.mExpMmListener = new ExposureAndMeteredManualListener();
        }
        return this.mExpMmListener;
    }

    void setValue(float value) {
        String displayValue;
        if (value < 0.0f) {
            float value2 = Math.abs(value);
            displayValue = String.format(this.FORMAT_FOR_MINUS, Integer.valueOf(((int) value2) / 1), Integer.valueOf(((int) (value2 * 10.0f)) % 10));
        } else {
            displayValue = value > 0.0f ? String.format(this.FORMAT_FOR_PLUS, Integer.valueOf(((int) value) / 1), Integer.valueOf(((int) (value * 10.0f)) % 10)) : "" + String.format(this.FORMAT_FOR_ZERO, Float.valueOf(value));
        }
        setValue(displayValue);
    }

    void updateMode() {
        if (ExposureCompensationController.getInstance().getSupportedValue(null) != null) {
            blink(false);
            this.mMode = getMode();
            Drawable icon = this.mTypedArray.getDrawable(getIconIndex());
            icon.setBounds(0, 1, 36, 35);
            setImage(icon);
            if (this.mMode == 0) {
                setVisibility(0);
                updateExposureCompensation();
            } else if (this.mMode == 1) {
                setVisibility(0);
                updateMeteredManual();
            } else {
                updateExposureCompensation();
            }
        }
    }

    private int getIconIndex() {
        switch (this.mMode) {
            case 0:
                return 0;
            case 1:
                return 1;
            default:
                return 0;
        }
    }

    @Override // com.sony.imaging.app.base.shooting.widget.DigitView, com.sony.imaging.app.base.shooting.widget.ActiveText
    protected void refresh() {
        updateMode();
    }

    private int getMode() {
        String expMode = ExposureModeController.getInstance().getValue(null);
        if (ExposureCompensationController.getInstance().isExposureCompensationAvailable()) {
            return 0;
        }
        if (!ExposureModeController.MANUAL_MODE.equals(expMode) && !ExposureModeController.MOVIE_MANUAL_MODE.equals(expMode)) {
            return 2;
        }
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateExposureCompensation() {
        float exposure = ExposureCompensationController.getInstance().getExposureCompensationValue();
        setValue(exposure);
        updataBlink();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateMeteredManual() {
        float value = CameraSetting.getInstance().getMeteredManual();
        setValue(Math.max(METERED_MANUAL_MIN, Math.min(METERED_MANUAL_MAX, value)));
        updataBlink();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updataBlink() {
        boolean isInRange = CameraSetting.getInstance().isMeteringInRange();
        if (this.mMode == 2) {
            setVisibility(isInRange ? 4 : 0);
        }
        blink(isInRange ? false : true);
    }

    /* loaded from: classes.dex */
    class ExposureAndMeteredManualListener implements NotificationListener {
        private String[] TAGS = {"ExposureCompensation", CameraNotificationManager.SCENE_MODE, CameraNotificationManager.METERED_MANUAL, CameraNotificationManager.METERING_RANGE, CameraNotificationManager.REC_MODE_CHANGED, CameraNotificationManager.ISO_SENSITIVITY};

        ExposureAndMeteredManualListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.TAGS;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (tag.equals("ExposureCompensation")) {
                if (ExposureAndMeteredManualView.this.mMode == 0) {
                    ExposureAndMeteredManualView.this.updateExposureCompensation();
                }
            } else if (tag.equals(CameraNotificationManager.METERED_MANUAL)) {
                if (ExposureAndMeteredManualView.this.mMode == 1) {
                    ExposureAndMeteredManualView.this.updateMeteredManual();
                }
            } else if (tag.equals(CameraNotificationManager.METERING_RANGE)) {
                ExposureAndMeteredManualView.this.updataBlink();
            } else if (tag.equals(CameraNotificationManager.SCENE_MODE) || tag.equals(CameraNotificationManager.REC_MODE_CHANGED) || tag.equals(CameraNotificationManager.ISO_SENSITIVITY)) {
                ExposureAndMeteredManualView.this.updateMode();
            }
        }
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveText
    protected boolean isVisible() {
        if (ExposureCompensationController.getInstance().getSupportedValue(null) != null) {
            return true;
        }
        return false;
    }
}
