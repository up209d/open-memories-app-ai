package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.playback.widget.GraphView;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class ShootingGraphView extends GraphView implements NotificationListener {
    public ShootingGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return new String[]{CameraNotificationManager.HISTOGRAM_UPDATE};
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        CameraEx.AnalizedData data = (CameraEx.AnalizedData) CameraNotificationManager.getInstance().getValue(tag);
        setHistogram(data.hist.Y);
    }

    @Override // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        CameraNotificationManager.getInstance().setNotificationListener(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.widget.GraphView, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        setHistogram(null);
    }
}
