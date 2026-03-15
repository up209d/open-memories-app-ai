package com.sony.imaging.app.smoothreflection.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.widget.ShutterSpeedView;
import com.sony.imaging.app.smoothreflection.shooting.SmoothReflectionCompositProcess;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class SRShutterSpeedView extends ShutterSpeedView {
    private ShutterSpeedChangeListener mShutterSpeedListener;

    public SRShutterSpeedView(Context context) {
        this(context, null);
    }

    public SRShutterSpeedView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ShutterSpeedView, com.sony.imaging.app.base.shooting.widget.DigitView, com.sony.imaging.app.base.shooting.widget.ActiveText
    protected NotificationListener getNotificationListener() {
        if (this.mShutterSpeedListener == null) {
            this.mShutterSpeedListener = new ShutterSpeedChangeListener();
        }
        return this.mShutterSpeedListener;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.ShutterSpeedView, com.sony.imaging.app.base.shooting.widget.DigitView, com.sony.imaging.app.base.shooting.widget.ActiveText
    public void refresh() {
        if (!SmoothReflectionCompositProcess.sbISShootingFinish) {
            super.refresh();
        }
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
            SRShutterSpeedView.this.refresh();
        }
    }
}
