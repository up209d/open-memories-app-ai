package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.sony.imaging.app.base.common.TouchManager;
import com.sony.imaging.app.base.common.TouchNotificationManager;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class TouchOffIcon extends ImageView {
    private static final String[] TAGS = {TouchNotificationManager.TAG_TOUCH_PANEL_ENABLED, TouchNotificationManager.TAG_TOUCHABLE_SCREEN};
    private NotificationListener mListener;

    public TouchOffIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mListener = new NotificationListener() { // from class: com.sony.imaging.app.base.shooting.widget.TouchOffIcon.1
            @Override // com.sony.imaging.app.util.NotificationListener
            public void onNotify(String tag) {
                TouchOffIcon.this.refresh();
            }

            @Override // com.sony.imaging.app.util.NotificationListener
            public String[] getTags() {
                return TouchOffIcon.TAGS;
            }
        };
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        TouchNotificationManager.getInstance().setNotificationListener(this.mListener);
        refresh();
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        TouchNotificationManager.getInstance().removeNotificationListener(this.mListener);
    }

    public void refresh() {
        int visibility = 4;
        if (TouchManager.getInstance().isTouchPanelEnabled() && TouchManager.getInstance().isTouchableScreen()) {
            visibility = 0;
        }
        setVisibility(visibility);
    }
}
