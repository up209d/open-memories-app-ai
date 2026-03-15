package com.sony.imaging.app.base.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.NotificationManager;

/* loaded from: classes.dex */
public abstract class RotationalSubLcdActiveTextView extends RotationalSubLcdTextView {
    protected NotificationListener mListener;
    protected final NotificationManager mNotifier;

    protected abstract NotificationListener getNotificationListener();

    protected abstract NotificationManager getNotificationManager();

    public RotationalSubLcdActiveTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mNotifier = getNotificationManager();
        this.mListener = getNotificationListener();
    }

    public RotationalSubLcdActiveTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mNotifier = getNotificationManager();
        this.mListener = getNotificationListener();
    }

    public RotationalSubLcdActiveTextView(Context context) {
        super(context);
        this.mNotifier = getNotificationManager();
        this.mListener = getNotificationListener();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView, com.sony.imaging.app.base.common.widget.SubLcdTextView, com.sony.imaging.app.base.common.widget.AbstractSubLCDView, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mListener != null && this.mNotifier != null) {
            this.mNotifier.setNotificationListener(this.mListener);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView, com.sony.imaging.app.base.common.widget.SubLcdTextView, com.sony.imaging.app.base.common.widget.AbstractSubLCDView, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mListener != null && this.mNotifier != null) {
            this.mNotifier.removeNotificationListener(this.mListener);
        }
    }
}
