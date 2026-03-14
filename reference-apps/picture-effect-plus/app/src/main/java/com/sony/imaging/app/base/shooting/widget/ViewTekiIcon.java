package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.ViewTekiController;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class ViewTekiIcon extends ActiveImage implements NotificationListener {
    private static final String[] TAGS = {CameraNotificationManager.VIEW_TEKI, CameraNotificationManager.REC_MODE_CHANGED};

    public ViewTekiIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected void refresh() {
        String value = ViewTekiController.getInstance().getValue();
        if (value.equals(ViewTekiController.VIEWTEKI_OFF)) {
            setVisibility(0);
        } else {
            setVisibility(4);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected NotificationListener getNotificationListener() {
        return this;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        refresh();
    }
}
