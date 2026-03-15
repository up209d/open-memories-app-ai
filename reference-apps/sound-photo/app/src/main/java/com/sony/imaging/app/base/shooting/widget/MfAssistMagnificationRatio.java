package com.sony.imaging.app.base.shooting.widget;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.FocusMagnificationController;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class MfAssistMagnificationRatio extends ActiveText {
    private static final String[] NOTIFICATION_TAGS = {CameraNotificationManager.FOCUS_MAGNIFICATION_CHANGED};

    public MfAssistMagnificationRatio(Context context) {
        super(context);
    }

    public MfAssistMagnificationRatio(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveText
    protected void refresh() {
        String value = FocusMagnificationController.getInstance().getValue(FocusMagnificationController.TAG_ACTUAL_MAGNIFICATION_RATIO);
        if (value != null) {
            float fValue = Integer.parseInt(value) / 100.0f;
            String text = String.format(getResources().getString(R.string.region_picker_section_all), String.format("%.1f", Float.valueOf(fValue)));
            setText(text);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveText
    protected NotificationListener getNotificationListener() {
        return new MagnificationListener();
    }

    /* loaded from: classes.dex */
    private class MagnificationListener implements NotificationListener {
        private MagnificationListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (CameraNotificationManager.FOCUS_MAGNIFICATION_CHANGED.equals(tag)) {
                MfAssistMagnificationRatio.this.refresh();
                MfAssistMagnificationRatio.this.setVisibility(MfAssistMagnificationRatio.this.isVisible() ? 0 : 4);
            }
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return MfAssistMagnificationRatio.NOTIFICATION_TAGS;
        }
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveText
    protected boolean isVisible() {
        return FocusMagnificationController.getInstance().isMagnifying();
    }
}
