package com.sony.imaging.app.manuallenscompensation.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCUtil;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class OCImageView extends ImageView {
    private NotificationListener mListener;
    protected CameraNotificationManager mNotifier;

    public OCImageView(Context context) {
        super(context);
        this.mNotifier = null;
    }

    public OCImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mNotifier = null;
        this.mNotifier = CameraNotificationManager.getInstance();
        this.mListener = getNotificationListener();
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mListener != null) {
            this.mNotifier.setNotificationListener(this.mListener);
        }
        refresh();
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mListener != null) {
            this.mNotifier.removeNotificationListener(this.mListener);
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override // android.view.View
    public boolean onTrackballEvent(MotionEvent event) {
        return false;
    }

    protected NotificationListener getNotificationListener() {
        if (this.mListener == null) {
            this.mListener = new ApertureChangeListener();
        }
        return this.mListener;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ApertureChangeListener implements NotificationListener {
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
            OCImageView.this.refresh();
        }
    }

    public void refresh() {
        int visibility = OCUtil.getInstance().isOldLensAttached() ? 0 : 4;
        setVisibility(visibility);
    }
}
