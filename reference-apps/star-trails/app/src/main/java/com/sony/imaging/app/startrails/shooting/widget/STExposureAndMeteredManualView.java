package com.sony.imaging.app.startrails.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.widget.ExposureAndMeteredManualView;
import com.sony.imaging.app.startrails.base.menu.controller.STExposureModeController;
import com.sony.imaging.app.startrails.util.STUtility;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class STExposureAndMeteredManualView extends ExposureAndMeteredManualView {
    private static final int MODE_EXPOSURE = 0;
    private static final int MODE_METEREDMANUAL = 1;
    private static final int MODE_OTHER = 2;
    private STExposureAndMeteredManualListener mExpMmListener;

    public STExposureAndMeteredManualView(Context context) {
        this(context, null);
        if (STUtility.getInstance().getCurrentTrail() == 1) {
            setVisibility(4);
        }
    }

    public STExposureAndMeteredManualView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.i("kamata", "hare");
        if (STUtility.getInstance().getCurrentTrail() == 1) {
            setVisibility(4);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.ActiveText, android.widget.TextView, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override // android.view.View
    public void setVisibility(int visibility) {
        int visibility2 = 0;
        if (ExposureCompensationController.getInstance().getSupportedValue(null) == null) {
            visibility2 = 4;
        }
        if (STUtility.getInstance().getCurrentTrail() == 1 && 1 == getMode()) {
            visibility2 = 4;
        }
        super.setVisibility(visibility2);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.DigitView, com.sony.imaging.app.base.shooting.widget.ActiveText, android.view.View
    public void onDetachedFromWindow() {
        if (STUtility.getInstance().getCurrentTrail() == 1) {
            setVisibility(4);
        }
        super.onDetachedFromWindow();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.ExposureAndMeteredManualView, com.sony.imaging.app.base.shooting.widget.DigitView, com.sony.imaging.app.base.shooting.widget.ActiveText
    public NotificationListener getNotificationListener() {
        if (STUtility.getInstance().getCurrentTrail() != 1) {
            NotificationListener listner = super.getNotificationListener();
            return listner;
        }
        if (this.mExpMmListener == null) {
            this.mExpMmListener = new STExposureAndMeteredManualListener();
        }
        NotificationListener listner2 = this.mExpMmListener;
        return listner2;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.ExposureAndMeteredManualView, com.sony.imaging.app.base.shooting.widget.DigitView, com.sony.imaging.app.base.shooting.widget.ActiveText
    public void refresh() {
        super.refresh();
        if (STUtility.getInstance().getCurrentTrail() == 1) {
            setVisibility(4);
        }
    }

    /* loaded from: classes.dex */
    class STExposureAndMeteredManualListener implements NotificationListener {
        private String[] TAGS = {"ExposureCompensation", CameraNotificationManager.SCENE_MODE, CameraNotificationManager.METERED_MANUAL, CameraNotificationManager.METERING_RANGE, CameraNotificationManager.REC_MODE_CHANGED};

        STExposureAndMeteredManualListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.TAGS;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            STExposureAndMeteredManualView.this.refresh();
        }
    }

    private int getMode() {
        String expMode = STExposureModeController.getInstance().getValue(null);
        if (ExposureCompensationController.getInstance().isExposureCompensationAvailable()) {
            return 0;
        }
        if (!ExposureModeController.MANUAL_MODE.equals(expMode) && !ExposureModeController.MOVIE_MANUAL_MODE.equals(expMode)) {
            return 2;
        }
        return 1;
    }
}
