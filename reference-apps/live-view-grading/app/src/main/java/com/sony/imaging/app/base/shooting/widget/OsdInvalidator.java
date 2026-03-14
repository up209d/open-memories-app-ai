package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class OsdInvalidator extends View implements NotificationListener {
    private static final int INVALIDATE_SIZE = 1;
    private static final String[] TAGS = {CameraNotificationManager.REC_MODE_CHANGING, CameraNotificationManager.REC_MODE_CHANGED};

    public OsdInvalidator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public OsdInvalidator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OsdInvalidator(Context context) {
        super(context);
    }

    @Override // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        CameraNotificationManager.getInstance().setNotificationListener(this);
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        super.onDetachedFromWindow();
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        postInvalidate(0, 0, 1, 1);
    }
}
